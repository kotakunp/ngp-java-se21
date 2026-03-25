package karaoke.app.main.service;

import java.awt.Color;
import java.util.ArrayList;

import karaoke.shared.Location;
import karaoke.shared.myTextfield;
import karaoke.shared.wordLine;

public class SelectionNavigationServiceVerifier {

    public static void main(String[] args) {
        SelectionNavigationService service = new SelectionNavigationService();
        ProjectSession session = new ProjectSession();
        ArrayList<wordLine> words = new ArrayList<wordLine>();

        wordLine first = new wordLine("first", new myTextfield("first", 0, 0, Color.white), 0);
        first.setLine_idx(0);
        wordLine second = new wordLine("second", new myTextfield("second", 1, 0, Color.white), 1);
        second.setLine_idx(0);
        wordLine third = new wordLine("third", new myTextfield("third", 2, 1, Color.white), 2);
        third.setLine_idx(1);
        first.setLocation(Location.start);
        second.setLocation(Location.end);
        third.setLocation(Location.start);
        words.add(first);
        words.add(second);
        words.add(third);

        session.setSelectedWordIndex(1);
        session.setTempSelectedWordIndex(0);
        session.setSelectedLineIndex(0);
        assertTrue(service.moveSelectionRight(session, words.size()), "Moving right should work inside bounds.");
        assertEquals(2, session.getSelectedWordIndex(), "Selection should move right.");
        assertTrue(!service.moveSelectionRight(session, words.size()), "Moving right should stop at the last word.");

        assertTrue(service.moveSelectionLeft(session), "Moving left should work inside bounds.");
        assertEquals(1, session.getSelectedWordIndex(), "Selection should move left.");

        SelectionNavigationService.SelectionResult basic = service.selectWord(session, words, false, false);
        assertTrue(basic.isValid(), "Basic selection should be valid.");
        assertEquals(0, session.getSelectedLineIndex(), "Selected line index should follow the word.");

        session.setSelectedWordIndex(2);
        session.setTempSelectedWordIndex(1);
        session.setSelectedLineIndex(1);
        SelectionNavigationService.SelectionResult endLine = service.selectWord(session, words, true, false);
        assertTrue(endLine.isValid(), "Cross-line selection should still be valid.");
        assertTrue(endLine.isEndLine(), "Crossing from an end word should enable end-line mode.");
        assertEquals(1, session.getSelectedWordIndex(), "Selection should snap back to the end-of-line word.");

        System.out.println("Selection navigation verification passed.");
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
}
