package karaoke.app.main.service;

import java.awt.Color;
import java.util.ArrayList;

import karaoke.shared.myTextfield;
import karaoke.shared.Location;
import karaoke.shared.wordLine;

public class PaintWorkflowServiceVerifier {

    public static void main(String[] args) {
        PaintWorkflowService service = new PaintWorkflowService();
        ProjectSession session = new ProjectSession();
        ArrayList<wordLine> words = new ArrayList<wordLine>();

        wordLine first = new wordLine("first", new myTextfield("first", 0, 0, Color.white), 0);
        first.setLine_idx(0);
        wordLine second = new wordLine("second", new myTextfield("second", 1, 0, Color.white), 1);
        second.setLine_idx(0);
        words.add(first);
        words.add(second);

        session.setSelectedWordIndex(1);
        session.setPaintIndex(2);
        service.repaintFromSelection(session, words);
        assertEquals(1, session.getPaintIndex(), "Paint index should move back to the selected word.");
        assertTrue(first.getLocation() == Location.end, "Previous word should be marked as line end during repaint.");

        FakeWavPlayer player = new FakeWavPlayer();
        session.setPaintIndex(0);
        assertTrue(service.markCurrentWord(session, player), "Marking the current word should succeed when player accepts it.");
        assertEquals(1, session.getPaintIndex(), "Paint index should advance after marking.");

        words.get(0).setPaint(true);
        assertTrue(service.undoLastPaint(session, words, player), "Undo should succeed when there is a painted word.");
        assertEquals(0, session.getPaintIndex(), "Paint index should rewind after undo.");
        assertTrue(!words.get(0).isPaint(), "Undo should clear painted state.");

        player.audioLoading = true;
        assertTrue(service.startPainting(session, words, player) == PaintWorkflowService.PaintStartResult.AUDIO_LOADING,
            "Audio loading should block paint start.");
        player.audioLoading = false;
        player.audioReady = false;
        assertTrue(service.startPainting(session, words, player) == PaintWorkflowService.PaintStartResult.AUDIO_NOT_READY,
            "Audio readiness should be required.");
        player.audioReady = true;
        player.playResult = false;
        assertTrue(service.startPainting(session, words, player) == PaintWorkflowService.PaintStartResult.PLAYBACK_FAILED,
            "Playback failures should be reported.");
        player.playResult = true;
        assertTrue(service.startPainting(session, words, player) == PaintWorkflowService.PaintStartResult.STARTED,
            "Paint should start when audio is ready.");

        System.out.println("Paint workflow verification passed.");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if(expected != actual) {
            throw new IllegalStateException(message + " Expected: " + expected + " Actual: " + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if(!value) {
            throw new IllegalStateException(message);
        }
    }

    private static final class FakeWavPlayer implements PaintPlayer {
        boolean audioLoading;
        boolean audioReady = true;
        boolean playResult = true;

        @Override
        public boolean addLane(int index) {
            return true;
        }

        @Override
        public boolean isAudioLoading() {
            return audioLoading;
        }

        @Override
        public boolean isAudioReady() {
            return audioReady;
        }

        @Override
        public boolean play() {
            return playResult;
        }

        @Override
        public void setPaint(int index) {
        }

        @Override
        public void stop() {
        }

        @Override
        public void addEndLine(Boolean isPaint, int index) {
        }
    }
}
