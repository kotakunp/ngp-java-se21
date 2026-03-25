package karaoke.ui.fx.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import karaoke.shared.timing.TimelineMath;

public class StreamAudioPlaybackService implements AudioPlaybackService {

    private final BooleanProperty loaded = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final LongProperty durationMicros = new SimpleLongProperty(0L);
    private final LongProperty durationTimelineUnits = new SimpleLongProperty(0L);
    private final LongProperty positionMicros = new SimpleLongProperty(0L);
    private final StringProperty status = new SimpleStringProperty("No audio loaded");

    private final Timeline positionPoller = new Timeline(
        new KeyFrame(Duration.millis(75), event -> refreshPosition())
    );

    private final LoadStreamMetadataService loadService = new LoadStreamMetadataService();

    private File audioFile;
    private AudioFormat playbackFormat;
    private long totalFrames;
    private volatile SourceDataLine dataLine;
    private volatile Thread playbackThread;
    private volatile long resumeFrames;
    private volatile long playbackBaseMicros;
    private volatile boolean stopRequested;
    private volatile boolean resetRequested;
    private String loadedFileName = "";

    public StreamAudioPlaybackService() {
        positionPoller.setCycleCount(Timeline.INDEFINITE);

        loading.bind(loadService.runningProperty());
        loadService.messageProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().length() > 0) {
                status.set(newValue);
            }
        });

        loadService.setOnSucceeded(event -> {
            StreamMetadata metadata = loadService.getValue();
            stopInternal(true);
            audioFile = metadata.audioFile;
            playbackFormat = metadata.playbackFormat;
            totalFrames = metadata.totalFrames;
            resumeFrames = 0L;
            playbackBaseMicros = 0L;
            positionMicros.set(0L);
            durationMicros.set(metadata.durationMicros);
            durationTimelineUnits.set(metadata.durationTimelineUnits);
            loaded.set(true);
            playing.set(false);
            status.set("Loaded audio: " + loadedFileName + " (stream)");
        });

        loadService.setOnFailed(event -> {
            loaded.set(false);
            playing.set(false);
            durationMicros.set(0L);
            durationTimelineUnits.set(0L);
            positionMicros.set(0L);
            Throwable exception = loadService.getException();
            status.set(exception == null ? "Audio load failed" : exception.getMessage());
        });
    }

    @Override
    public BooleanProperty loadedProperty() {
        return loaded;
    }

    @Override
    public BooleanProperty loadingProperty() {
        return loading;
    }

    @Override
    public BooleanProperty playingProperty() {
        return playing;
    }

    @Override
    public LongProperty durationMicrosProperty() {
        return durationMicros;
    }

    @Override
    public LongProperty durationTimelineUnitsProperty() {
        return durationTimelineUnits;
    }

    @Override
    public LongProperty positionMicrosProperty() {
        return positionMicros;
    }

    @Override
    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public void load(File audioFile) {
        loadedFileName = audioFile.getName();
        status.set("Loading audio...");
        loadService.load(audioFile);
    }

    @Override
    public void seekToTimelineUnit(long timelineUnit) {
        if (!loaded.get() || playbackFormat == null) {
            status.set("Load audio before seeking");
            return;
        }

        long clampedTimelineUnit = Math.max(0L, Math.min(durationTimelineUnits.get(), timelineUnit));
        long targetFrames = clampedTimelineUnit * TimelineMath.AUDIO_FRAMES_PER_TIMELINE_UNIT;
        boolean resumePlayback = playing.get();
        stopInternal(false);
        resumeFrames = Math.max(0L, Math.min(totalFrames, targetFrames));
        playbackBaseMicros = framesToMicros(resumeFrames, playbackFormat);
        Platform.runLater(() -> {
            positionMicros.set(playbackBaseMicros);
            status.set("Seeked to " + clampedTimelineUnit);
        });
        if (resumePlayback) {
            startPlaybackThread();
        }
    }

    @Override
    public void togglePlayback() {
        if (!loaded.get() || audioFile == null || playbackFormat == null) {
            status.set("Load audio before playing");
            return;
        }
        if (playing.get()) {
            stopInternal(false);
        } else {
            startPlaybackThread();
        }
    }

    @Override
    public void stop() {
        stopInternal(true);
    }

    @Override
    public void dispose() {
        stopInternal(true);
        loaded.set(false);
        durationMicros.set(0L);
        durationTimelineUnits.set(0L);
        status.set("Disposed");
        audioFile = null;
        playbackFormat = null;
        totalFrames = 0L;
    }

    private void startPlaybackThread() {
        if (playbackThread != null && playbackThread.isAlive()) {
            return;
        }

        final long startFrames = resumeFrames;
        stopRequested = false;
        resetRequested = false;

        playbackThread = new Thread(() -> streamPlayback(startFrames), "ngp-audio-stream");
        playbackThread.setDaemon(true);
        playbackThread.start();
    }

    private void streamPlayback(long startFrames) {
        boolean naturalEnd = false;
        playbackBaseMicros = framesToMicros(startFrames, playbackFormat);
        Platform.runLater(() -> {
            status.set("Streaming audio...");
            playing.set(true);
            positionPoller.play();
        });

        try (AudioInputStream sourceStream = AudioSystem.getAudioInputStream(audioFile);
             AudioInputStream pcmStream = createPlaybackStream(sourceStream)) {

            skipFrames(pcmStream, startFrames, playbackFormat);

            SourceDataLine line = AudioSystem.getSourceDataLine(playbackFormat);
            dataLine = line;
            line.open(playbackFormat);
            line.start();

            byte[] buffer = new byte[Math.max(playbackFormat.getFrameSize(), 1) * 4096];
            int bytesRead;
            while (!stopRequested && (bytesRead = pcmStream.read(buffer, 0, buffer.length)) != -1) {
                int written = 0;
                while (!stopRequested && written < bytesRead) {
                    written += line.write(buffer, written, bytesRead - written);
                }
            }

            if (!stopRequested) {
                line.drain();
                naturalEnd = true;
            }
        } catch (UnsupportedAudioFileException e) {
            reportFailure(e);
        } catch (LineUnavailableException e) {
            reportFailure(e);
        } catch (IOException e) {
            reportFailure(e);
        } finally {
            finishPlayback(startFrames, naturalEnd);
        }
    }

    private AudioInputStream createPlaybackStream(AudioInputStream sourceStream) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if (AudioFormat.Encoding.PCM_SIGNED.equals(sourceFormat.getEncoding())
            && sourceFormat.getSampleSizeInBits() == 16
            && !sourceFormat.isBigEndian()) {
            return sourceStream;
        }

        AudioFormat targetFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            sourceFormat.getSampleRate(),
            16,
            sourceFormat.getChannels(),
            sourceFormat.getChannels() * 2,
            sourceFormat.getSampleRate(),
            false
        );
        return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
    }

    private void skipFrames(AudioInputStream stream, long framesToSkip, AudioFormat format) throws IOException {
        long bytesToSkip = framesToSkip * format.getFrameSize();
        byte[] discard = new byte[4096];
        while (bytesToSkip > 0) {
            long skipped = stream.skip(bytesToSkip);
            if (skipped > 0) {
                bytesToSkip -= skipped;
                continue;
            }
            int read = stream.read(discard, 0, (int) Math.min(discard.length, bytesToSkip));
            if (read == -1) {
                break;
            }
            bytesToSkip -= read;
        }
    }

    private void finishPlayback(long startFrames, boolean naturalEnd) {
        SourceDataLine line = dataLine;
        long processedFrames = 0L;
        if (line != null) {
            processedFrames = line.getLongFramePosition();
            line.stop();
            line.flush();
            line.close();
        }
        dataLine = null;
        playbackThread = null;
        positionPoller.stop();

        if (naturalEnd) {
            resumeFrames = 0L;
            playbackBaseMicros = 0L;
            Platform.runLater(() -> positionMicros.set(durationMicros.get()));
        } else if (resetRequested) {
            resumeFrames = 0L;
            playbackBaseMicros = 0L;
            Platform.runLater(() -> positionMicros.set(0L));
        } else {
            resumeFrames = startFrames + processedFrames;
            playbackBaseMicros = framesToMicros(resumeFrames, playbackFormat);
            Platform.runLater(() -> positionMicros.set(playbackBaseMicros));
        }

        Platform.runLater(() -> {
            playing.set(false);
            if (naturalEnd) {
                status.set("Playback finished");
            } else if (resetRequested) {
                status.set("Stopped");
            } else {
                status.set("Paused");
            }
        });
    }

    private void stopInternal(boolean reset) {
        stopRequested = true;
        resetRequested = reset;
        SourceDataLine line = dataLine;
        if (line != null) {
            line.stop();
            line.flush();
        }
        Thread thread = playbackThread;
        if (thread != null) {
            thread.interrupt();
        }
        if (reset) {
            resumeFrames = 0L;
            playbackBaseMicros = 0L;
            Platform.runLater(() -> {
                positionMicros.set(0L);
                if (loaded.get()) {
                    status.set("Stopped");
                }
            });
        }
        Platform.runLater(() -> playing.set(false));
    }

    private void refreshPosition() {
        SourceDataLine line = dataLine;
        if (line == null) {
            return;
        }
        positionMicros.set(playbackBaseMicros + line.getMicrosecondPosition());
    }

    private void reportFailure(Exception exception) {
        Platform.runLater(() -> {
            playing.set(false);
            status.set(exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
        });
    }

    private long framesToMicros(long frames, AudioFormat format) {
        if (format == null || format.getFrameRate() <= 0F) {
            return 0L;
        }
        return (long) ((frames * 1_000_000D) / format.getFrameRate());
    }

    private static final class StreamMetadata {
        private final File audioFile;
        private final AudioFormat playbackFormat;
        private final long totalFrames;
        private final long durationMicros;
        private final long durationTimelineUnits;

        private StreamMetadata(File audioFile, AudioFormat playbackFormat, long totalFrames, long durationMicros, long durationTimelineUnits) {
            this.audioFile = audioFile;
            this.playbackFormat = playbackFormat;
            this.totalFrames = totalFrames;
            this.durationMicros = durationMicros;
            this.durationTimelineUnits = durationTimelineUnits;
        }
    }

    private static final class LoadStreamMetadataService extends Service<StreamMetadata> {
        private File selectedFile;

        void load(File audioFile) {
            selectedFile = audioFile;
            if (isRunning()) {
                cancel();
            }
            reset();
            start();
        }

        @Override
        protected Task<StreamMetadata> createTask() {
            final File audioFile = selectedFile;
            return new Task<StreamMetadata>() {
                @Override
                protected StreamMetadata call() throws Exception {
                    if (audioFile == null) {
                        throw new IllegalStateException("No audio file selected.");
                    }
                    updateMessage("Loading audio...");
                    try (AudioInputStream sourceStream = AudioSystem.getAudioInputStream(audioFile)) {
                        AudioFormat sourceFormat = sourceStream.getFormat();
                        AudioFormat playbackFormat = createPlaybackFormat(sourceFormat);
                        long totalFrames = sourceStream.getFrameLength();
                        long durationMicros = calculateDurationMicros(totalFrames, playbackFormat);
                        long durationTimelineUnits = totalFrames <= 0 ? 0L : totalFrames / TimelineMath.AUDIO_FRAMES_PER_TIMELINE_UNIT;
                        return new StreamMetadata(audioFile, playbackFormat, totalFrames, durationMicros, durationTimelineUnits);
                    }
                }

                private AudioFormat createPlaybackFormat(AudioFormat sourceFormat) {
                    if (AudioFormat.Encoding.PCM_SIGNED.equals(sourceFormat.getEncoding())
                        && sourceFormat.getSampleSizeInBits() == 16
                        && !sourceFormat.isBigEndian()) {
                        return sourceFormat;
                    }
                    return new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sourceFormat.getSampleRate(),
                        16,
                        sourceFormat.getChannels(),
                        sourceFormat.getChannels() * 2,
                        sourceFormat.getSampleRate(),
                        false
                    );
                }

                private long calculateDurationMicros(long totalFrames, AudioFormat playbackFormat) {
                    if (totalFrames <= 0 || playbackFormat.getFrameRate() <= 0F) {
                        return 0L;
                    }
                    return (long) ((totalFrames * 1_000_000D) / playbackFormat.getFrameRate());
                }
            };
        }
    }
}
