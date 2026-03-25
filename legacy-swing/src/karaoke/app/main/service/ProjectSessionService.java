package karaoke.app.main.service;

import java.io.File;

public class ProjectSessionService {

    public String resolveBrowseDirectory(ProjectSession session, String fallbackDirectory) {
        if(session.getParentDir().length() > 0) {
            return session.getParentDir();
        }
        return fallbackDirectory;
    }

    public void configureBatchProject(ProjectSession session, File file) {
        session.setProjectPath(file.getAbsolutePath() + "\\" + file.getName() + ".ngp");
        session.setExportPath(file.getAbsolutePath() + "\\" + file.getName() + ".ng1");
    }

    public void configureImportedAudio(ProjectSession session, File file) {
        session.setAudioDir(file.getAbsolutePath());
        session.setParentDir(file.getParent());
    }

    public void configureImportedText(ProjectSession session, File file) {
        session.setTextDir(file.getAbsolutePath());
        session.setParentDir(file.getParent());
        session.setNameFile(baseName(file));
    }

    public void configureSaveTarget(ProjectSession session, File file) {
        String baseDir = file.getAbsoluteFile().getParent();
        String baseName = baseName(file);
        session.setNameFile(baseName);
        session.setProjectPath(baseDir + "\\" + baseName + ".ngp");
        session.setLyricPath(baseDir + "\\" + baseName + "-.txt");
    }

    public void configureOpenedProject(ProjectSession session, File file) {
        String baseName = baseName(file);
        String parentDir = file.getParent();
        session.setNameFile(baseName);
        session.setParentDir(parentDir);
        session.setProjectPath(file.getAbsolutePath());
        session.setLyricPath(parentDir + "//" + baseName + "-.txt");
        session.setSave(true);
    }

    public String buildVocAudioPath(File projectFile) {
        return projectFile.getParent() + "//" + baseName(projectFile) + "voc.wav";
    }

    public void appendMessage(ProjectSession session, String value) {
        session.setMessage(session.getMessage() + value);
    }

    private String baseName(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
