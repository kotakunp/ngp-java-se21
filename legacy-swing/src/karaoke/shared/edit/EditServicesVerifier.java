package karaoke.shared.edit;

import java.awt.Color;
import java.util.ArrayList;

import karaoke.shared.Location;
import karaoke.shared.MyType;
import karaoke.shared.wordLine;
import karaoke.shared.myTextfield;

public class EditServicesVerifier {

    public static void main(String[] args) {
        verifyRowEdits();
        verifyTextSplitAndMerge();
        verifyPaintedCutAndUndo();
        System.out.println("Edit service verification passed.");
    }

    private static void verifyRowEdits() {
        ArrayList<wordLine> words = new ArrayList<wordLine>();
        words.add(createWord("one", 0, 0));
        words.add(createWord("two", 1, 0));
        words.add(createWord("three", 2, 1));

        RowEditService rows = new RowEditService();
        rows.splitRowFromWordIndex(words, 1);
        assertEquals(0, words.get(0).getLine_idx(), "first row unchanged");
        assertEquals(1, words.get(1).getLine_idx(), "second row moved");
        assertEquals(2, words.get(2).getLine_idx(), "third row moved");

        rows.mergeRowFromWordIndex(words, 1);
        assertEquals(0, words.get(0).getLine_idx(), "first row restored");
        assertEquals(0, words.get(1).getLine_idx(), "second row restored");
        assertEquals(1, words.get(2).getLine_idx(), "third row restored");
    }

    private static void verifyTextSplitAndMerge() {
        ArrayList<wordLine> words = new ArrayList<wordLine>();
        words.add(createWord("hello", 0, 0));
        words.add(createWord("world", 1, 0));

        WordTextEditService textEdits = new WordTextEditService();
        wordLine inserted = createWord("llo", 1, 0);
        boolean split = textEdits.splitWord(words, 0, 2, inserted);
        assertTrue(split, "split should succeed");
        assertEquals(3, words.size(), "word inserted");
        assertEquals("he", words.get(0).getWord(), "left side");
        assertEquals("llo", words.get(1).getWord(), "right side");

        boolean merged = textEdits.mergeWordWithPrevious(words, 1);
        assertTrue(merged, "merge should succeed");
        assertEquals(2, words.size(), "word removed after merge");
        assertEquals("hello", words.get(0).getWord(), "merged word");
    }

    private static void verifyPaintedCutAndUndo() {
        ArrayList<wordLine> words = new ArrayList<wordLine>();
        wordLine painted = createPaintedWord("hello", 0, 0, MyType.normal, Location.end, 10, 20);
        words.add(painted);
        words.add(createPaintedWord("next", 1, 0, MyType.normal, Location.middle, 25, 35));

        WordTextEditService textEdits = new WordTextEditService();
        assertTrue(textEdits.canCutPaintedWord(painted, false), "painted cut allowed");

        wordLine inserted = createWord("", 1, 0);
        boolean cut = textEdits.cutPaintedWord(words, 0, 2, inserted);
        assertTrue(cut, "painted cut should succeed");
        assertEquals(3, words.size(), "painted cut inserts word");
        assertEquals("he", words.get(0).getWord(), "painted cut left side");
        assertEquals("llo", words.get(1).getWord(), "painted cut right side");
        assertEquals(MyType.splite_main, words.get(0).getType(), "painted cut main type");
        assertEquals(MyType.splite_sub_end, words.get(1).getType(), "painted cut sub end type");
        assertEquals(Location.end, words.get(0).getLocation(), "painted cut location preserved at first word");

        boolean undo = textEdits.undoCutPaintedWord(words, 1);
        assertTrue(undo, "painted undo should succeed");
        assertEquals(2, words.size(), "painted undo removes word");
        assertEquals("hello", words.get(0).getWord(), "painted undo restores word");
        assertEquals(MyType.normal, words.get(0).getType(), "painted undo restores type");
        assertEquals(Location.end, words.get(0).getLocation(), "painted undo restores location");
    }

    private static wordLine createWord(String text, int idx, int lineIdx) {
        wordLine word = new wordLine(text, new myTextfield(text, idx, lineIdx, Color.lightGray), idx);
        word.setLine_idx(lineIdx);
        return word;
    }

    private static wordLine createPaintedWord(String text, int idx, int lineIdx, MyType type, Location location, int sec, int lowSec) {
        wordLine word = createWord(text, idx, lineIdx);
        word.setInfo(type, sec, lowSec, location);
        if(type == MyType.splite_main) {
            word.setText(false);
        } else {
            word.setText(true);
        }
        return word;
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if(expected == null ? actual != null : !expected.equals(actual)) {
            throw new IllegalStateException(label + " mismatch. expected=[" + expected + "] actual=[" + actual + "]");
        }
    }

    private static void assertTrue(boolean value, String label) {
        if(!value) {
            throw new IllegalStateException(label);
        }
    }
}
