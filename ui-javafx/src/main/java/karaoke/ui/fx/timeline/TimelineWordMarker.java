package karaoke.ui.fx.timeline;

public class TimelineWordMarker {

    private final String text;
    private final int lineIndex;
    private final int wordIndex;
    private final int start;
    private final int end;
    private final boolean painted;

    public TimelineWordMarker(String text, int lineIndex, int wordIndex, int start, int end, boolean painted) {
        this.text = text;
        this.lineIndex = lineIndex;
        this.wordIndex = wordIndex;
        this.start = start;
        this.end = end;
        this.painted = painted;
    }

    public String getText() {
        return text;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isPainted() {
        return painted;
    }
}
