package karaoke.shared.repair;

import java.awt.Color;
import java.util.ArrayList;

import karaoke.shared.Location;
import karaoke.shared.MyType;
import karaoke.shared.myTextfield;
import karaoke.shared.wordLine;

public class ProjectRepairVerifier {

    public static void main(String[] args) {
        verifyCompactLineIndexes();
        verifySplitSingleWordLines();
        System.out.println("Project repair verification passed.");
    }

    private static void verifyCompactLineIndexes() {
        ArrayList<wordLine> words = new ArrayList<wordLine>();
        words.add(createWord("a", 0, 0));
        words.add(createWord("b", 1, 2));
        words.add(createWord("c", 2, 2));

        ProjectRepairService repairs = new ProjectRepairService();
        boolean changed = repairs.compactLineIndexes(words);
        assertTrue(changed, "line compaction should detect gap");
        assertEquals(0, words.get(0).getLine_idx(), "first line unchanged");
        assertEquals(1, words.get(1).getLine_idx(), "gap compacted");
        assertEquals(1, words.get(2).getLine_idx(), "second word compacted");
    }

    private static void verifySplitSingleWordLines() {
        ArrayList<wordLine> words = new ArrayList<wordLine>();
        words.add(createPaintedWord("hello", 0, 0, Location.end, 10, 20));
        words.add(createPaintedWord("two", 1, 1, Location.start, 25, 30));
        words.add(createPaintedWord("words", 2, 1, Location.end, 30, 40));

        ProjectRepairService repairs = new ProjectRepairService();
        int repaired = repairs.splitSingleWordLines(words, new ProjectRepairService.WordFactory() {
            public wordLine create(String text, int wordIndex, int lineIndex) {
                return createWord(text, wordIndex, lineIndex);
            }
        });

        assertEquals(1, repaired, "one single-word line repaired");
        assertEquals(4, words.size(), "word inserted");
        assertEquals("he", words.get(0).getWord(), "left half");
        assertEquals("llo", words.get(1).getWord(), "right half");
        assertEquals(MyType.splite_main, words.get(0).getType(), "left type");
        assertEquals(MyType.splite_sub_end, words.get(1).getType(), "right type");
    }

    private static wordLine createWord(String text, int idx, int lineIdx) {
        wordLine word = new wordLine(text, new myTextfield(text, idx, lineIdx, Color.lightGray), idx);
        word.setLine_idx(lineIdx);
        return word;
    }

    private static wordLine createPaintedWord(String text, int idx, int lineIdx, Location location, int sec, int lowSec) {
        wordLine word = createWord(text, idx, lineIdx);
        word.setInfo(MyType.normal, sec, lowSec, location);
        word.setText(true);
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
