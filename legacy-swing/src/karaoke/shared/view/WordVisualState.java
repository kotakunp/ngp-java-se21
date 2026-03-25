package karaoke.shared.view;

import java.awt.Color;
import java.awt.geom.Line2D;

public class WordVisualState {

    public static final Color PAINT_COLOR = new Color(255, 200, 153);
    public static final Color DEFAULT_COLOR = Color.lightGray;
    public static final Color SELECT_COLOR = new Color(255, 255, 0);
    public static final Color REPAINT_COLOR = Color.red;
    public static final Color FOCUS_COLOR = Color.white;
    public static final Color READ_COLOR = new Color(204, 102, 0);

    private int x1 = 20;
    private int x2 = 250;
    private Line2D line;
    private Line2D lineHighToLow;
    private Line2D lineEnd;
    private Color color = Color.red;
    private int charDuration = 0;
    private int endIndex = 0;
    private int charIndex = 0;
    private boolean play = false;
    private int position = 10;
    private float rectPositionX = 0;
    private int width = 0;
    private int space = 10;

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public Line2D getLine() {
        return line;
    }

    public void setLine(Line2D line) {
        this.line = line;
    }

    public Line2D getLineHighToLow() {
        return lineHighToLow;
    }

    public void setLineHighToLow(Line2D lineHighToLow) {
        this.lineHighToLow = lineHighToLow;
    }

    public Line2D getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(Line2D lineEnd) {
        this.lineEnd = lineEnd;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getCharDuration() {
        return charDuration;
    }

    public void setCharDuration(int charDuration) {
        this.charDuration = charDuration;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public void incrementCharIndex() {
        this.charIndex++;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getRectPositionX() {
        return rectPositionX;
    }

    public void setRectPositionX(float rectPositionX) {
        this.rectPositionX = rectPositionX;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getSpace() {
        return space;
    }
}
