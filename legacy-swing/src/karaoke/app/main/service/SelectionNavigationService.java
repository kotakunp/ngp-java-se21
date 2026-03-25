package karaoke.app.main.service;

import java.util.List;

import karaoke.shared.Location;
import karaoke.shared.wordLine;

public class SelectionNavigationService {

    public static final class SelectionResult {
        private final boolean valid;
        private final boolean endLine;
        private final wordLine previousWord;
        private final wordLine selectedWord;

        private SelectionResult(boolean valid, boolean endLine, wordLine previousWord, wordLine selectedWord) {
            this.valid = valid;
            this.endLine = endLine;
            this.previousWord = previousWord;
            this.selectedWord = selectedWord;
        }

        public static SelectionResult invalid(boolean endLine) {
            return new SelectionResult(false, endLine, null, null);
        }

        public static SelectionResult valid(boolean endLine, wordLine previousWord, wordLine selectedWord) {
            return new SelectionResult(true, endLine, previousWord, selectedWord);
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isEndLine() {
            return endLine;
        }

        public wordLine getPreviousWord() {
            return previousWord;
        }

        public wordLine getSelectedWord() {
            return selectedWord;
        }
    }

    public boolean moveSelectionLeft(ProjectSession session) {
        if(session.getSelectedWordIndex() > 0) {
            session.setSelectedWordIndex(session.getSelectedWordIndex() - 1);
            return true;
        }
        return false;
    }

    public boolean moveSelectionRight(ProjectSession session, int wordCount) {
        if(wordCount > session.getSelectedWordIndex() + 1) {
            session.setSelectedWordIndex(session.getSelectedWordIndex() + 1);
            return true;
        }
        return false;
    }

    public SelectionResult selectWord(ProjectSession session, List<wordLine> words, boolean down, boolean endLine) {
        if(words.isEmpty()) {
            return SelectionResult.invalid(endLine);
        }
        int tempIndex = session.getTempSelectedWordIndex();
        if(tempIndex < 0 || tempIndex >= words.size()) {
            tempIndex = Math.max(0, Math.min(session.getSelectedWordIndex(), words.size() - 1));
            session.setTempSelectedWordIndex(tempIndex);
        }

        wordLine previousWord = words.get(tempIndex);
        boolean nextEndLine = endLine;
        if(previousWord.getLocation() == Location.end && !endLine && down) {
            nextEndLine = true;
            if(session.getSelectedLineIndex() > 0 && session.getSelectedWordIndex() > 0) {
                session.setTempSelectedWordIndex(session.getSelectedWordIndex());
                session.setSelectedWordIndex(session.getSelectedWordIndex() - 1);
            }
        } else {
            nextEndLine = false;
        }

        if(session.getSelectedWordIndex() < 0 || session.getSelectedWordIndex() >= words.size()) {
            return SelectionResult.invalid(nextEndLine);
        }

        session.setTempSelectedWordIndex(session.getSelectedWordIndex());
        wordLine selectedWord = words.get(session.getSelectedWordIndex());
        session.setSelectedLineIndex(selectedWord.getLine_idx());
        return SelectionResult.valid(nextEndLine, previousWord, selectedWord);
    }
}
