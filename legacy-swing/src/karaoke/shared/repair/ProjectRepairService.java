package karaoke.shared.repair;

import java.util.List;

import karaoke.shared.Location;
import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class ProjectRepairService {

    public interface WordFactory {
        wordLine create(String text, int wordIndex, int lineIndex);
    }

    public boolean compactLineIndexes(List<wordLine> words) {
        int maxLineIndex = getMaxLineIndex(words);
        boolean lineError = false;
        for (int lineIndex = 0; lineIndex <= maxLineIndex; lineIndex++) {
            if(countWordsOnLine(words, lineIndex) == 0) {
                lineError = true;
                break;
            }
        }
        if(!lineError) {
            return false;
        }

        reindexWordIds(words, maxLineIndex + 1);
        int previousLine = 0;
        int compactedLine = 0;
        for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
            wordLine word = words.get(wordIndex);
            int lineId = word.getLine_idx();
            if(previousLine == lineId) {
                word.setLine_idx(compactedLine);
            } else {
                previousLine = lineId;
                compactedLine++;
                word.setLine_idx(compactedLine);
            }
        }
        return true;
    }

    public int splitSingleWordLines(List<wordLine> words, WordFactory wordFactory) {
        int maxLineIndex = getMaxLineIndex(words);
        int repairedCount = 0;
        for (int lineIndex = 0; lineIndex < maxLineIndex; lineIndex++) {
            wordLine loneWord = findOnlyWordOnLine(words, lineIndex);
            if(loneWord == null) {
                continue;
            }

            String currentText = loneWord.getWord();
            int half = currentText.length() / 2;
            String left = currentText.substring(0, half);
            String right = currentText.substring(half, currentText.length());
            int selectedWordIndex = loneWord.getIdx();
            int selectedLineIndex = loneWord.getLine_idx();

            loneWord.setWord_(left);
            int splitSec = loneWord.getSec() + loneWord.getDuration() / 2;
            int insertedIndex = selectedWordIndex + 1;
            wordLine insertedWord = wordFactory.create(right, insertedIndex, selectedLineIndex);
            insertedWord.setInfo(MyType.splite_sub_end, splitSec, loneWord.getLow_sec(), loneWord.getLocation());
            loneWord.setLow_sec(splitSec);
            loneWord.setType(MyType.splite_main);
            loneWord.setSplit(true);
            words.add(insertedIndex, insertedWord);

            if(loneWord.getLocation() == Location.end && selectedWordIndex > 0) {
                wordLine before = words.get(selectedWordIndex - 1);
                if(before.getLocation() == Location.end) {
                    loneWord.setLocation(Location.start);
                } else {
                    loneWord.setLocation(Location.middle);
                }
            }

            reindexWordIds(words, maxLineIndex);
            repairedCount++;
        }
        return repairedCount;
    }

    private void reindexWordIds(List<wordLine> words, int lineCountHint) {
        for (int lineIndex = 0; lineIndex < lineCountHint; lineIndex++) {
            for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
                wordLine word = words.get(wordIndex);
                if(word.getLine_idx() == lineIndex) {
                    word.setIdx(wordIndex);
                }
            }
        }
    }

    private int countWordsOnLine(List<wordLine> words, int lineIndex) {
        int count = 0;
        for (int i = 0; i < words.size(); i++) {
            if(words.get(i).getLine_idx() == lineIndex) {
                count++;
            }
        }
        return count;
    }

    private wordLine findOnlyWordOnLine(List<wordLine> words, int lineIndex) {
        int count = 0;
        wordLine loneWord = null;
        for (int i = 0; i < words.size(); i++) {
            wordLine word = words.get(i);
            if(word.getLine_idx() == lineIndex) {
                count++;
                loneWord = word;
            }
        }
        return count == 1 ? loneWord : null;
    }

    private int getMaxLineIndex(List<wordLine> words) {
        int maxLineIndex = 0;
        for (int i = 0; i < words.size(); i++) {
            maxLineIndex = Math.max(maxLineIndex, words.get(i).getLine_idx());
        }
        return maxLineIndex;
    }
}
