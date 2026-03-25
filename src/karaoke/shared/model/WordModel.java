package karaoke.shared.model;

import karaoke.shared.Location;
import karaoke.shared.MyType;

public class WordModel {

    private MyType type = MyType.normal;
    private Location location = Location.middle;
    private int idx = -1;
    private int lineIndex = -1;
    private String word = "";
    private int sec = 0;
    private int highSec = 0;
    private int lowSec = 0;
    private boolean split = false;
    private boolean paint = false;
    private boolean threeSplit = false;
    private int microSec = 0;
    private int microSecLow = 0;

    public MyType getType() {
        return type;
    }

    public void setType(MyType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getHighSec() {
        return highSec;
    }

    public void setHighSec(int highSec) {
        this.highSec = highSec;
    }

    public int getLowSec() {
        return lowSec;
    }

    public void setLowSec(int lowSec) {
        this.lowSec = lowSec;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public boolean isPaint() {
        return paint;
    }

    public void setPaint(boolean paint) {
        this.paint = paint;
    }

    public boolean isThreeSplit() {
        return threeSplit;
    }

    public void setThreeSplit(boolean threeSplit) {
        this.threeSplit = threeSplit;
    }

    public int getMicroSec() {
        return microSec;
    }

    public void setMicroSec(int microSec) {
        this.microSec = microSec;
    }

    public int getMicroSecLow() {
        return microSecLow;
    }

    public void setMicroSecLow(int microSecLow) {
        this.microSecLow = microSecLow;
    }

    public int getDuration() {
        return lowSec - sec;
    }
}
