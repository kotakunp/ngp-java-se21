package karaoke.ui.fx.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

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

public class ClipAudioPlaybackService implements AudioPlaybackService {

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

    private final LoadClipService loadService = new LoadClipService();

    private Clip clip;
    private String loadedFileName = "";

    public ClipAudioPlaybackService() {
        positionPoller.setCycleCount(Timeline.INDEFINITE);

        loading.bind(loadService.runningProperty());
        loadService.messageProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().length() > 0) {
                status.set(newValue);
            }
        });

        loadService.setOnSucceeded(event -> {
            replaceClip(loadService.getValue());
            loaded.set(true);
            playing.set(false);
            positionMicros.set(0L);
            durationMicros.set(clip.getMicrosecondLength());
            durationTimelineUnits.set(clip.getFrameLength() / 1000L);
            status.set("Loaded audio: " + loadedFileName);
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
        if (clip == null || !loaded.get()) {
            status.set("Load audio before seeking");
            return;
        }
        long clampedTimelineUnit = Math.max(0L, Math.min(durationTimelineUnits.get(), timelineUnit));
        clip.setFramePosition((int) (clampedTimelineUnit * 1000L));
        long micros = clip.getMicrosecondPosition();
        positionMicros.set(micros);
        status.set("Seeked to " + clampedTimelineUnit);
    }

    @Override
    public void togglePlayback() {
        if (clip == null || !loaded.get()) {
            status.set("Load audio before playing");
            return;
        }
        if (clip.isRunning()) {
            clip.stop();
            playing.set(false);
            status.set("Paused");
        } else {
            clip.start();
            positionPoller.play();
            playing.set(true);
            status.set("Playing");
        }
    }

    @Override
    public void stop() {
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.setMicrosecondPosition(0L);
        }
        positionPoller.stop();
        positionMicros.set(0L);
        playing.set(false);
        if (loaded.get()) {
            status.set("Stopped");
        }
    }

    @Override
    public void dispose() {
        stop();
        if (clip != null && clip.isOpen()) {
            clip.close();
        }
        clip = null;
        loaded.set(false);
        durationMicros.set(0L);
        durationTimelineUnits.set(0L);
        status.set("Disposed");
    }

    private void replaceClip(Clip nextClip) {
        stop();
        if (clip != null && clip.isOpen()) {
            clip.close();
        }
        clip = nextClip;
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                Platform.runLater(() -> {
                    refreshPosition();
                    if (clip != null && !clip.isRunning()) {
                        playing.set(false);
                        positionPoller.stop();
                        if (positionMicros.get() >= durationMicros.get() && durationMicros.get() > 0) {
                            status.set("Playback finished");
                        }
                    }
                });
            } else if (event.getType() == LineEvent.Type.START) {
                Platform.runLater(() -> {
                    playing.set(true);
                    status.set("Playing");
                });
            }
        });
    }

    private void refreshPosition() {
        if (clip == null || !clip.isOpen()) {
            return;
        }
        positionMicros.set(clip.getMicrosecondPosition());
    }
    private static final class LoadClipService extends Service<Clip> {
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
        protected Task<Clip> createTask() {
            final File audioFile = selectedFile;
            return new Task<Clip>() {
                @Override
                protected Clip call() throws Exception {
                    if (audioFile == null) {
                        throw new IllegalStateException("No audio file selected.");
                    }
                    updateMessage("Loading audio...");
                    Clip clip = AudioSystem.getClip();
                    try (AudioInputStream input = AudioSystem.getAudioInputStream(audioFile)) {
                        clip.open(input);
                    }
                    return clip;
                }
            };
        }
    }
}
