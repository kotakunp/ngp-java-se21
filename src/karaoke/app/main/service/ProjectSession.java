package karaoke.app.main.service;

import java.util.ArrayList;
import java.util.Vector;

import karaoke.shared.wordLine;

public class ProjectSession {

    private final ArrayList<wordLine> words = new ArrayList<wordLine>();
    private final Vector<Integer> lines = new Vector<Integer>();

    private boolean save;
    private String audioDir = "";
    private String textDir = "";
    private String parentDir = "";
    private String songInfo = "";
    private String nameFile = "";
    private String projectPath = "";
    private String exportPath = "";
    private String lyricPath = "";
    private String message = "";
    private int selectedWordIndex = 0;
    private int selectedLineIndex = 0;
    private int tempSelectedWordIndex = 0;
    private int paintIndex = 0;

    public ArrayList<wordLine> getWords() {
        return words;
    }

    public Vector<Integer> getLines() {
        return lines;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public String getAudioDir() {
        return audioDir;
    }

    public void setAudioDir(String audioDir) {
        this.audioDir = audioDir;
    }

    public String getTextDir() {
        return textDir;
    }

    public void setTextDir(String textDir) {
        this.textDir = textDir;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public String getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(String songInfo) {
        this.songInfo = songInfo;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSelectedWordIndex() {
        return selectedWordIndex;
    }

    public void setSelectedWordIndex(int selectedWordIndex) {
        this.selectedWordIndex = selectedWordIndex;
    }

    public int getSelectedLineIndex() {
        return selectedLineIndex;
    }

    public void setSelectedLineIndex(int selectedLineIndex) {
        this.selectedLineIndex = selectedLineIndex;
    }

    public int getTempSelectedWordIndex() {
        return tempSelectedWordIndex;
    }

    public void setTempSelectedWordIndex(int tempSelectedWordIndex) {
        this.tempSelectedWordIndex = tempSelectedWordIndex;
    }

    public int getPaintIndex() {
        return paintIndex;
    }

    public void setPaintIndex(int paintIndex) {
        this.paintIndex = paintIndex;
    }
}
