package karaoke.shared;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;

import karaoke.shared.model.WordModel;
import karaoke.shared.timing.TimelineMath;
import karaoke.shared.view.WordVisualState;

public class wordLine {
    private final WordModel model;
    private final WordVisualState visual;
    private myTextfield field;
    private int umnu = 10;
    private int daraa = 0;

    public wordLine(String word) {
        this.model = new WordModel();
        this.visual = new WordVisualState();
        model.setWord(word);
        model.setSec(100);
        visual.setLine(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX2(), model.getSec()));
        visual.setLineHighToLow(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX1(), model.getLowSec()));
        visual.setWidth(30 * word.length());
    }

    public wordLine(String word, myTextfield field, int idx) {
        this.model = new WordModel();
        this.visual = new WordVisualState();
        model.setWord(word);
        model.setIdx(idx);
        model.setSec(-10);
        this.field = field;
        visual.setLine(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX2(), model.getSec()));
        visual.setLineHighToLow(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX1(), model.getSec()));
        visual.setWidth(30 * word.length());
    }

    public wordLine(String[] value) {
        this.model = new WordModel();
        this.visual = new WordVisualState();

        model.setIdx(Integer.parseInt(value[0]));
        model.setLineIndex(Integer.parseInt(value[1]));
        model.setHighSec(Integer.parseInt(value[2]));
        model.setSec(Integer.parseInt(value[3]));
        model.setLowSec(Integer.parseInt(value[4]));
        model.setWord(value[5]);
        setSplit(Integer.parseInt(value[6]));
        setPaint(Integer.parseInt(value[7]));
        setThreeSplite(Integer.parseInt(value[10]));

        int loc = Integer.parseInt(value[9]);
        switch (loc) {
        case 0:
            model.setLocation(Location.start);
            break;
        case 1:
            model.setLocation(Location.middle);
            break;
        case 2:
            model.setLocation(Location.end);
            visual.setLineEnd(new Line2D.Double(visual.getX1(), model.getLowSec(), visual.getX2(), model.getLowSec()));
            break;
        default:
            break;
        }

        visual.setLine(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX2(), model.getSec()));
        visual.setLineHighToLow(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX1(), model.getLowSec()));
        visual.setColor(model.isPaint() ? WordVisualState.PAINT_COLOR : WordVisualState.DEFAULT_COLOR);
        visual.setCharDuration((model.getLowSec() - model.getSec()) / model.getWord().length());
        visual.setWidth(30 * model.getWord().length());

        int ty = Integer.parseInt(value[8]);
        switch (ty) {
        case 0:
            model.setType(MyType.normal);
            field = new myTextfield(model.getWord(), model.getIdx(), model.getLineIndex(), visual.getColor());
            break;
        case 1:
            model.setType(MyType.splite_main);
            field = new myTextfield(model.getWord() + "-", model.getIdx(), model.getLineIndex(), visual.getColor());
            break;
        case 2:
            model.setType(MyType.splite_sub);
            if(model.isThreeSplit()) {
                field = new myTextfield(model.getWord() + "-", model.getIdx(), model.getLineIndex(), visual.getColor());
            } else {
                field = new myTextfield(model.getWord(), model.getIdx(), model.getLineIndex(), visual.getColor());
            }
            break;
        case 3:
            model.setType(MyType.splite_sub_end);
            field = new myTextfield(model.getWord(), model.getIdx(), model.getLineIndex(), visual.getColor());
            break;
        default:
            field = new myTextfield(model.getWord(), model.getIdx(), model.getLineIndex(), visual.getColor());
            break;
        }
    }

    private void refreshTimelineGeometry() {
        visual.setLine(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX2(), model.getSec()));
        visual.setLineHighToLow(new Line2D.Double(visual.getX1(), model.getSec(), visual.getX1(), model.getLowSec()));
        if(model.getLocation() == Location.end || visual.getLineEnd() != null) {
            visual.setLineEnd(new Line2D.Double(visual.getX1(), model.getLowSec(), visual.getX2(), model.getLowSec()));
        }
        if(model.getWord().length() > 0) {
            visual.setCharDuration((model.getLowSec() - model.getSec()) / model.getWord().length());
        } else {
            visual.setCharDuration(0);
        }
    }

    private void setThreeSplite(int value) {
        model.setThreeSplit(value != 0);
    }

    public int getThreeSplite() {
        return model.isThreeSplit() ? 1 : 0;
    }

    public int getIdx() {
        return model.getIdx();
    }

    public void setIdx(int idx) {
        model.setIdx(idx);
        if(field != null) {
            field.setIdx(idx);
        }
    }

    public int getX2() {
        return visual.getX2();
    }

    public int getX1() {
        return visual.getX1();
    }

    public void setLine(Point startPoint, Point endPoint) {
    }

    public Line2D getShape() {
        return visual.getLine();
    }

    public int getMin() {
        return model.getSec() - 10;
    }

    public int getMax() {
        return model.getSec() + 1;
    }

    public int getMinLow() {
        return model.getLowSec() - 1;
    }

    public int getMaxLow() {
        return model.getLowSec() + 10;
    }

    public double getUmnu() {
        return umnu;
    }

    public void moveLine(int sec) {
        model.setSec(sec);
        refreshTimelineGeometry();
    }

    public void moveLineEnd(int low_sec) {
        model.setLowSec(low_sec);
        refreshTimelineGeometry();
    }

    public int getSec() {
        return model.getSec();
    }

    public int getDuration() {
        return model.getDuration();
    }

    public int getCurrent_sec_paint() {
        return TimelineMath.timelineUnitToExportPosition(model.getSec());
    }

    public int getDurationSec() {
        return TimelineMath.timelineDurationToExportDuration(model.getLowSec() - model.getSec());
    }

    public long getPlaysec() {
        return TimelineMath.timelineUnitToExportPositionLong(model.getSec());
    }

    public int getLow_sec_paint() {
        return TimelineMath.timelineUnitToExportPosition(model.getLowSec());
    }

    public void moveSeek(int seek) {
        model.setSec(TimelineMath.moveTimelineUnitByExportOffset(model.getSec(), seek));
        model.setLowSec(TimelineMath.moveTimelineUnitByExportOffset(model.getLowSec(), seek));
        refreshTimelineGeometry();
    }

    public String getText() {
        return field.getText();
    }

    public int getLow_sec() {
        return model.getLowSec();
    }

    public void setLow_sec(int low_sec) {
        model.setLowSec(low_sec);
        refreshTimelineGeometry();
    }

    public int getHigh_sec() {
        return model.getHighSec();
    }

    public void setHigh_sec(int high_sec) {
        model.setHighSec(high_sec);
    }

    public int getCurrent_sec() {
        return model.getSec();
    }

    public Color getColor() {
        return visual.getColor();
    }

    public void setSelectColor() {
        visual.setCharIndex(0);
        visual.setColor(WordVisualState.SELECT_COLOR);
        field.setForeground(WordVisualState.SELECT_COLOR);
    }

    public Line2D getLine_h() {
        return visual.getLineHighToLow();
    }

    public void setLine_h(Line2D line_h) {
        visual.setLineHighToLow(line_h);
    }

    public myTextfield getField() {
        return field;
    }

    public int getLine_idx() {
        return model.getLineIndex();
    }

    public void setLine_idx(int line_idx) {
        model.setLineIndex(line_idx);
        if(field != null) {
            field.setLine_idx(line_idx);
        }
    }

    public void setLine_idxAdd() {
        setLine_idx(model.getLineIndex() + 1);
    }

    public void setLine_idxOdd() {
        setLine_idx(model.getLineIndex() - 1);
    }

    public boolean isSplit() {
        return model.isSplit();
    }

    public void setSplit(boolean split) {
        model.setSplit(split);
    }

    private void setSplit(int value) {
        model.setSplit(value != 0);
    }

    public void setFocusColor() {
        field.setForeground(WordVisualState.FOCUS_COLOR);
    }

    public boolean isPaint() {
        return model.isPaint();
    }

    public void setPaint(boolean paint) {
        model.setPaint(paint);
    }

    public int getSplit() {
        return model.isSplit() ? 1 : 0;
    }

    public int getPaint() {
        return model.isPaint() ? 1 : 0;
    }

    private void setPaint(int value) {
        model.setPaint(value != 0);
    }

    public boolean isThreeSplite() {
        return model.isThreeSplit();
    }

    public void setThreeSplite(boolean threeSplite) {
        model.setThreeSplit(threeSplite);
    }

    public MyType getType() {
        return model.getType();
    }

    public int getTypeInt() {
        if(model.getType() == MyType.splite_main) {
            return 1;
        } else if(model.getType() == MyType.splite_sub) {
            return 2;
        } else if(model.getType() == MyType.splite_sub_end) {
            return 3;
        }
        return 0;
    }

    public void setFocusable() {
        field.setFocusable(true);
    }

    public void setType(Enum<MyType> value) {
        model.setType((MyType) value);
    }

    public Location getLocation() {
        return model.getLocation();
    }

    public int getLocationInt() {
        if(model.getLocation() == Location.start) {
            return 0;
        } else if(model.getLocation() == Location.middle) {
            return 1;
        } else if(model.getLocation() == Location.end) {
            return 2;
        }
        return -1;
    }

    public void setLocation(Location location) {
        model.setLocation(location);
        if(location == Location.end) {
            addEndLine(model.getLowSec());
            System.out.println("after");
        }
        refreshTimelineGeometry();
    }

    public void addEndLine(int low_sec) {
        System.out.println("work");
        model.setLowSec(low_sec);
        model.setLocation(Location.end);
        visual.setLineEnd(new Line2D.Double(visual.getX1(), low_sec, visual.getX2(), low_sec));
        refreshTimelineGeometry();
    }

    public void setEndLine() {
        model.setLocation(Location.end);
        visual.setLineEnd(new Line2D.Double(visual.getX1(), model.getLowSec(), visual.getX2(), model.getLowSec()));
        refreshTimelineGeometry();
    }

    public void removeEndline() {
        visual.setLineEnd(null);
        model.setLocation(Location.middle);
    }

    public Line2D getLine_end() {
        return visual.getLineEnd();
    }

    public void setLine_end(Line2D line_end) {
        visual.setLineEnd(line_end);
    }

    public void setWord() {
        String word = field.getText();
        if(word.trim().length() > 0) {
            model.setWord(word);
        }
    }

    public String getWord() {
        return model.getWord();
    }

    public void setWord(String word) {
        model.setWord(word);
        field.setText(word + "-");
        model.setSplit(true);
        if(model.getLocation() == Location.end) {
            visual.setLineEnd(null);
        }
        visual.setWidth(30 * word.length());
    }

    public void setWord_(String word) {
        model.setWord(word);
        field.setText(word);
        if(model.getLocation() == Location.end) {
            visual.setLineEnd(null);
        }
        visual.setWidth(30 * word.length());
    }

    public void setWord_splite(String word) {
        model.setWord(model.getWord() + word);
        field.setText(word);
        visual.setWidth(30 * word.length());
    }

    public void addWord(String txt, int low_sec, Location location) {
        model.setWord(model.getWord() + txt);
        model.setLowSec(low_sec);
        model.setLocation(location);
        if(location == Location.end) {
            addEndLine(low_sec);
        }
        refreshTimelineGeometry();
        field.setText(model.getWord());
    }

    public void setInfo(MyType type, int sec, int low_sec, Location location) {
        model.setType(type);
        model.setSec(sec);
        model.setLowSec(low_sec);
        model.setPaint(true);
        model.setSplit(true);
        visual.setColor(WordVisualState.PAINT_COLOR);
        field.setForeground(WordVisualState.PAINT_COLOR);
        if(location == Location.end) {
            visual.setLineEnd(new Line2D.Double(visual.getX1(), low_sec, visual.getX2(), low_sec));
            model.setLocation(location);
        } else if(location == Location.start) {
            model.setLocation(Location.middle);
        } else {
            model.setLocation(location);
        }
        refreshTimelineGeometry();
    }

    public void setText(boolean b) {
        if(b) {
            field.setText(model.getWord());
        } else {
            field.setText(model.getWord() + "-");
        }
    }

    public void validate() {
        field.revalidate();
    }

    public void setSecond(int sec) {
        model.setSec(sec);
    }

    public int getSecond() {
        return model.getSec();
    }

    public void setPaintColor() {
        visual.setColor(WordVisualState.PAINT_COLOR);
        if(model.isPaint()) {
            field.setForeground(WordVisualState.PAINT_COLOR);
        } else {
            field.setForeground(WordVisualState.DEFAULT_COLOR);
        }
    }

    public void setReadColor() {
        visual.setColor(WordVisualState.READ_COLOR);
        field.setForeground(WordVisualState.READ_COLOR);
    }

    public void setRePaintColor() {
        visual.setColor(WordVisualState.REPAINT_COLOR);
        field.setForeground(WordVisualState.REPAINT_COLOR);
    }

    public void setTextSelectColor() {
        field.setForeground(WordVisualState.SELECT_COLOR);
    }

    public void setTextUnSelectColor() {
        if(model.isPaint()) {
            field.setForeground(WordVisualState.PAINT_COLOR);
        } else {
            field.setForeground(WordVisualState.DEFAULT_COLOR);
        }
    }

    public int getMicro_sec_low() {
        return model.getMicroSecLow();
    }

    public void setMicro_sec_low(int micro_sec_low) {
        model.setMicroSecLow(micro_sec_low);
    }

    public int getMicro_sec() {
        return model.getMicroSec();
    }

    public void setMicro_sec(int micro_sec) {
        model.setMicroSec(micro_sec);
    }

    public int getCharDuration() {
        return visual.getCharDuration();
    }

    public int getEndIndex() {
        return visual.getEndIndex();
    }

    public void setEndIndex(int endIndex) {
        visual.setEndIndex(endIndex);
    }

    public int getCharCurDuration() {
        int sub = visual.getCharIndex() * visual.getCharDuration();
        return model.getSec() + sub;
    }

    public int getChar_idx() {
        return visual.getCharIndex();
    }

    public void setChar_idx(int char_idx) {
        visual.setCharIndex(char_idx);
    }

    public void addChar_idx() {
        visual.incrementCharIndex();
    }

    public int setChar(int point_y) {
        int dur = point_y - model.getSec();
        int charDuration = visual.getCharDuration();
        int charIndex = charDuration == 0 ? 0 : dur / charDuration;
        visual.setCharIndex(charIndex);
        return charIndex;
    }

    public void setEdit(boolean edit) {
        field.setEditable(edit);
    }

    public void removePaint() {
        model.setLowSec(0);
        model.setSec(-10);
        model.setType(MyType.normal);
        model.setLocation(Location.middle);
        visual.setLine(null);
        visual.setLineHighToLow(null);
        visual.setLineEnd(null);
        visual.setColor(WordVisualState.DEFAULT_COLOR);
        visual.setCharDuration(0);
        visual.setEndIndex(0);
        visual.setCharIndex(0);
        field.setForeground(WordVisualState.DEFAULT_COLOR);
        umnu = 10;
        daraa = 0;
        model.setHighSec(0);
        model.setLowSec(0);
        model.setSplit(false);
        model.setPaint(false);
        model.setThreeSplit(false);
        model.setMicroSec(0);
        model.setMicroSecLow(0);
    }

    public boolean isPlay() {
        return visual.isPlay();
    }

    public void setPlay(boolean play) {
        visual.setPlay(play);
    }

    public int getPosition() {
        return visual.getPosition();
    }

    public void setPosition(int position) {
        visual.setPosition(position);
    }

    public float getRectPositionX() {
        return visual.getRectPositionX();
    }

    public void setRectPositionX(float sec) {
        float z = sec - model.getSec();
        visual.setRectPositionX((float) ((z * visual.getWidth()) / getDuration()));
    }

    public void setRectPositionX() {
        visual.setRectPositionX(0);
        visual.setPlay(false);
    }

    public void setWidth(int width) {
        visual.setWidth(width);
    }

    public float getWidth() {
        return visual.getWidth();
    }

    public void setPosition0() {
        visual.setPosition(1770 - visual.getWidth());
    }

    public void setPositionBack(int width, int position) {
        if(model.isSplit()) {
            visual.setPosition(position - width + visual.getSpace());
        } else {
            visual.setPosition(position - width - visual.getSpace());
        }
    }

    public int getPosWidth() {
        if(model.isSplit() && model.getType() != MyType.splite_sub_end) {
            return visual.getPosition() + visual.getWidth();
        }
        return visual.getPosition() + visual.getWidth() + visual.getSpace();
    }

    public boolean isSplite() {
        return model.isSplit();
    }

    public void setSplite(boolean splite) {
        model.setSplit(splite);
    }

    public void setAddLine() {
        model.setLineIndex(model.getLineIndex() + 1);
    }

    public WordModel getModel() {
        return model;
    }
}
