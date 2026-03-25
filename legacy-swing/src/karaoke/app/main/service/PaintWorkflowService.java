package karaoke.app.main.service;

import java.util.List;

import karaoke.shared.Location;
import karaoke.shared.wordLine;

public class PaintWorkflowService {

    public enum PaintStartResult {
        STARTED,
        AUDIO_LOADING,
        AUDIO_NOT_READY,
        NO_WORDS,
        PLAYBACK_FAILED
    }

    public void stopPainting(ProjectSession session, List<wordLine> words, PaintPlayer player) {
        player.stop();
        if(session.getPaintIndex() > 0) {
            wordLine line = words.get(session.getPaintIndex() - 1);
            if(line.getLocation() != Location.end) {
                player.addEndLine(true, session.getPaintIndex() - 1);
            }
        }
    }

    public PaintStartResult startPainting(ProjectSession session, List<wordLine> words, PaintPlayer player) {
        if(player.isAudioLoading()) {
            return PaintStartResult.AUDIO_LOADING;
        }
        if(words.isEmpty()) {
            return PaintStartResult.NO_WORDS;
        }
        if(!player.isAudioReady()) {
            return PaintStartResult.AUDIO_NOT_READY;
        }

        player.setPaint(session.getPaintIndex());
        if(!player.play()) {
            return PaintStartResult.PLAYBACK_FAILED;
        }
        return PaintStartResult.STARTED;
    }

    public boolean markCurrentWord(ProjectSession session, PaintPlayer player) {
        if(player.addLane(session.getPaintIndex())) {
            session.setPaintIndex(session.getPaintIndex() + 1);
            return true;
        }
        return false;
    }

    public boolean undoLastPaint(ProjectSession session, List<wordLine> words, PaintPlayer player) {
        if(session.getPaintIndex() <= 0 || words.isEmpty()) {
            return false;
        }
        session.setPaintIndex(session.getPaintIndex() - 1);
        words.get(session.getPaintIndex()).removePaint();
        player.setPaint(session.getPaintIndex());
        return true;
    }

    public void resetPaintCursor(ProjectSession session, PaintPlayer player) {
        player.setPaint(session.getPaintIndex());
    }

    public void repaintFromSelection(ProjectSession session, List<wordLine> words) {
        int tempIndex = session.getPaintIndex();
        session.setPaintIndex(session.getSelectedWordIndex());
        if(session.getSelectedWordIndex() > 0) {
            words.get(session.getSelectedWordIndex() - 1).setEndLine();
        }
        for(int i = session.getSelectedWordIndex(); i <= tempIndex && i < words.size(); i++) {
            words.get(i).removePaint();
        }
    }
}
