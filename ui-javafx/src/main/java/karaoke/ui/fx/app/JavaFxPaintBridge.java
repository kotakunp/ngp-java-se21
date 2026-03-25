package karaoke.ui.fx.app;

import java.util.List;

import karaoke.app.main.service.PaintPlayer;
import karaoke.shared.Location;
import karaoke.shared.wordLine;
import karaoke.ui.fx.audio.AudioPlaybackService;

final class JavaFxPaintBridge implements PaintPlayer {

    private final List<wordLine> words;
    private final AudioPlaybackService audioPlaybackService;

    JavaFxPaintBridge(List<wordLine> words, AudioPlaybackService audioPlaybackService) {
        this.words = words;
        this.audioPlaybackService = audioPlaybackService;
    }

    @Override
    public void stop() {
        audioPlaybackService.stop();
    }

    @Override
    public void addEndLine(Boolean isPaint, int index) {
        if (index < 0 || index >= words.size()) {
            return;
        }

        wordLine currentWord = words.get(index);
        int sec = currentTimelineUnit();
        if (Boolean.TRUE.equals(isPaint)) {
            currentWord.addEndLine(sec);
            return;
        }

        if (currentWord.getLocation() != Location.end) {
            currentWord.addEndLine(Math.max(currentWord.getSec(), currentWord.getLow_sec() - 15));
            if (index + 1 < words.size()) {
                wordLine afterWord = words.get(index + 1);
                if (afterWord.getLocation() == Location.middle) {
                    afterWord.setLocation(Location.start);
                }
            }
        } else if (index + 1 < words.size()) {
            wordLine afterWord = words.get(index + 1);
            currentWord.removeEndline();
            if (afterWord.getLocation() != Location.end) {
                afterWord.setLocation(Location.middle);
            }
            currentWord.setLow_sec(afterWord.getCurrent_sec());
        }
    }

    @Override
    public boolean isAudioLoading() {
        return audioPlaybackService.loadingProperty().get();
    }

    @Override
    public boolean isAudioReady() {
        return audioPlaybackService.loadedProperty().get();
    }

    @Override
    public void setPaint(int index) {
    }

    @Override
    public boolean play() {
        audioPlaybackService.togglePlayback();
        return audioPlaybackService.playingProperty().get();
    }

    @Override
    public boolean addLane(int index) {
        if (index < 0 || index >= words.size()) {
            return false;
        }

        int sec = currentTimelineUnit();
        wordLine currentWord = words.get(index);
        if (currentWord.isPaint()) {
            currentWord.setLocation(Location.middle);
            currentWord.setLine_end(null);
        }

        if (index > 0) {
            wordLine beforeWord = words.get(index - 1);
            if (beforeWord.getLocation() == Location.end) {
                currentWord.setLocation(Location.start);
                currentWord.setLow_sec(sec);
            } else {
                beforeWord.setLow_sec(sec);
                currentWord.setHigh_sec(beforeWord.getCurrent_sec());
            }
        } else {
            currentWord.setLocation(Location.start);
        }

        currentWord.setPaint(true);
        currentWord.setPaintColor();
        currentWord.moveLine(sec);
        currentWord.setLow_sec(sec);
        return true;
    }

    private int currentTimelineUnit() {
        long durationMicros = audioPlaybackService.durationMicrosProperty().get();
        long durationTimelineUnits = audioPlaybackService.durationTimelineUnitsProperty().get();
        if (durationMicros <= 0L || durationTimelineUnits <= 0L) {
            return 0;
        }
        long positionMicros = audioPlaybackService.positionMicrosProperty().get();
        long timelineUnit = (positionMicros * durationTimelineUnits) / durationMicros;
        return (int) Math.max(0L, Math.min(durationTimelineUnits, timelineUnit));
    }
}
