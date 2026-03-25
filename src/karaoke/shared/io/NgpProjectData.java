package karaoke.shared.io;

import java.util.List;

import karaoke.shared.wordLine;

public class NgpProjectData {
    private final String songInfo;
    private final List<wordLine> words;
    private final int paintIndex;

    public NgpProjectData(String songInfo, List<wordLine> words, int paintIndex) {
        this.songInfo = songInfo;
        this.words = words;
        this.paintIndex = paintIndex;
    }

    public String getSongInfo() {
        return songInfo;
    }

    public List<wordLine> getWords() {
        return words;
    }

    public int getPaintIndex() {
        return paintIndex;
    }
}
