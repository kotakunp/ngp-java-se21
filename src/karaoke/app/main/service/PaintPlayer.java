package karaoke.app.main.service;

public interface PaintPlayer {

    void stop();

    void addEndLine(Boolean isPaint, int index);

    boolean isAudioLoading();

    boolean isAudioReady();

    void setPaint(int index);

    boolean play();

    boolean addLane(int index);
}
