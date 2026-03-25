package karaoke.shared.io;

public class NgpSerializedProject {
    private final String projectText;
    private final String lyricText;

    public NgpSerializedProject(String projectText, String lyricText) {
        this.projectText = projectText;
        this.lyricText = lyricText;
    }

    public String getProjectText() {
        return projectText;
    }

    public String getLyricText() {
        return lyricText;
    }
}
