package karaoke.shared.edit;

import java.util.List;

import karaoke.shared.Location;
import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class WordTextEditService {

    public boolean canCutPaintedWord(wordLine current, boolean paintModeActive) {
        return current != null && !current.isThreeSplite() && !paintModeActive && current.isPaint();
    }

    public boolean splitWord(List<wordLine> words, int selectedWordIndex, int caretPosition, wordLine insertedWord) {
        wordLine current = words.get(selectedWordIndex);
        String currentText = current.getWord();
        if(caretPosition <= 0 || caretPosition >= currentText.length()) {
            return false;
        }

        String leftText = currentText.substring(0, caretPosition);
        String rightText = currentText.substring(caretPosition, currentText.length());
        current.setWord_(leftText);

        if(current.isPaint()) {
            int splitSec = current.getSec() + current.getDuration() / 2;
            insertedWord.setInfo(MyType.normal, splitSec, current.getLow_sec(), current.getLocation());
            words.add(selectedWordIndex + 1, insertedWord);
            current.setLow_sec(splitSec);
        } else {
            words.add(selectedWordIndex + 1, insertedWord);
        }

        insertedWord.setWord_(rightText);

        if(current.getLocation() == Location.end) {
            current.setLocation(Location.middle);
        }

        return true;
    }

    public boolean mergeWordWithPrevious(List<wordLine> words, int selectedWordIndex) {
        if(selectedWordIndex <= 0 || selectedWordIndex >= words.size()) {
            return false;
        }

        wordLine current = words.get(selectedWordIndex);
        wordLine before = words.get(selectedWordIndex - 1);
        String text = current.getWord();

        if(current.isPaint()) {
            before.addWord(text, current.getLow_sec(), current.getLocation());
        } else {
            before.setWord_splite(text);
        }
        before.setText(true);
        words.remove(selectedWordIndex);
        return true;
    }

    public boolean cutPaintedWord(List<wordLine> words, int selectedWordIndex, int caretPosition, wordLine insertedWord) {
        if(selectedWordIndex < 0 || selectedWordIndex >= words.size()) {
            return false;
        }

        wordLine current = words.get(selectedWordIndex);
        String currentText = current.getWord();
        if(caretPosition <= 0 || caretPosition >= currentText.length()) {
            return false;
        }

        String leftText = currentText.substring(0, caretPosition);
        String rightText = currentText.substring(caretPosition, currentText.length());
        current.setWord(leftText);
        insertedWord.setWord_(rightText);

        int splitSec = current.getSec() + current.getDuration() / 2;
        int insertedIndex = selectedWordIndex + 1;
        if(current.getType() == MyType.normal) {
            insertedWord.setInfo(MyType.splite_sub_end, splitSec, current.getLow_sec(), current.getLocation());
            current.setType(MyType.splite_main);
            insertedWord.setText(true);
        } else if(current.getType() == MyType.splite_main) {
            insertedWord.setInfo(MyType.splite_sub, splitSec, current.getLow_sec(), current.getLocation());
            current.setThreeSplite(true);
            insertedWord.setThreeSplite(true);
            insertedWord.setText(false);
            words.get(insertedIndex).setThreeSplite(true);
        } else if(current.getType() == MyType.splite_sub_end) {
            words.get(selectedWordIndex - 1).setThreeSplite(true);
            current.setThreeSplite(true);
            current.setType(MyType.splite_sub);
            insertedWord.setInfo(MyType.splite_sub_end, splitSec, current.getLow_sec(), current.getLocation());
            insertedWord.setThreeSplite(true);
        }

        words.add(insertedIndex, insertedWord);
        current.setLow_sec(splitSec);
        if(current.getLocation() == Location.end && selectedWordIndex > 0) {
            wordLine before = words.get(selectedWordIndex - 1);
            if(before.getLocation() == Location.end) {
                current.setLocation(Location.start);
            } else {
                current.setLocation(Location.middle);
            }
        }

        return true;
    }

    public boolean undoCutPaintedWord(List<wordLine> words, int selectedWordIndex) {
        if(selectedWordIndex <= 0 || selectedWordIndex >= words.size()) {
            return false;
        }

        wordLine current = words.get(selectedWordIndex);
        if(!current.isSplit() || current.getType() == MyType.splite_main) {
            return false;
        }

        wordLine before = words.get(selectedWordIndex - 1);
        before.addWord(current.getWord(), current.getLow_sec(), before.getLocation());
        if(current.getType() == MyType.splite_sub) {
            before.setText(false);
            before.setThreeSplite(false);
            words.get(selectedWordIndex + 1).setThreeSplite(false);
        } else if(current.getType() == MyType.splite_sub_end) {
            if(before.isThreeSplite()) {
                words.get(selectedWordIndex - 2).setThreeSplite(false);
                before.setThreeSplite(false);
                before.setText(true);
                before.setType(MyType.splite_sub_end);
                before.setLocation(current.getLocation());
            } else {
                before.setType(MyType.normal);
                before.setText(true);
                before.setSplit(false);
                if(current.getLocation() == Location.end) {
                    before.setLocation(Location.end);
                } else {
                    before.setLocation(before.getLocation());
                }
            }
        }
        words.remove(selectedWordIndex);
        return true;
    }
}
