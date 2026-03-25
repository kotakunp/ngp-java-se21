package karaoke.shared.edit;

import java.util.List;

import karaoke.shared.wordLine;

public class RowEditService {

    public void splitRowFromWordIndex(List<wordLine> words, int wordIndex) {
        for (int i = wordIndex; i < words.size(); i++) {
            words.get(i).setLine_idxAdd();
        }
    }

    public void mergeRowFromWordIndex(List<wordLine> words, int wordIndex) {
        for (int i = wordIndex; i < words.size(); i++) {
            words.get(i).setLine_idxOdd();
        }
    }
}
