package karaoke.app.main.service;

import java.io.File;

public class ProjectSessionServiceVerifier {

    public static void main(String[] args) {
        ProjectSession session = new ProjectSession();
        ProjectSessionService service = new ProjectSessionService();

        String fallback = service.resolveBrowseDirectory(session, "D:\\budalt");
        assertEquals("D:\\budalt", fallback, "Fallback browse directory should be used when parent dir is empty.");

        File textFile = new File("/work/song.txt");
        service.configureImportedText(session, textFile);
        assertEquals("/work/song.txt", session.getTextDir(), "Imported text path mismatch.");
        assertEquals("/work", session.getParentDir(), "Imported text parent dir mismatch.");
        assertEquals("song", session.getNameFile(), "Imported text base name mismatch.");

        File audioFile = new File("/work/song.wav");
        service.configureImportedAudio(session, audioFile);
        assertEquals("/work/song.wav", session.getAudioDir(), "Imported audio path mismatch.");
        assertEquals("/work", session.getParentDir(), "Imported audio parent dir mismatch.");

        File saveTarget = new File("/export/demo.ngp");
        service.configureSaveTarget(session, saveTarget);
        assertEquals("demo", session.getNameFile(), "Save target base name mismatch.");
        assertEquals("/export\\demo.ngp", session.getProjectPath(), "Save target project path mismatch.");
        assertEquals("/export\\demo-.txt", session.getLyricPath(), "Save target lyric path mismatch.");

        File projectFile = new File("/projects/track.ngp");
        service.configureOpenedProject(session, projectFile);
        assertEquals("track", session.getNameFile(), "Opened project base name mismatch.");
        assertEquals("/projects", session.getParentDir(), "Opened project parent dir mismatch.");
        assertEquals("/projects/track.ngp", session.getProjectPath(), "Opened project path mismatch.");
        assertEquals("/projects//track-.txt", session.getLyricPath(), "Opened project lyric path mismatch.");
        assertTrue(session.isSave(), "Opened project should mark the session as saved.");

        String vocAudio = service.buildVocAudioPath(projectFile);
        assertEquals("/projects//trackvoc.wav", vocAudio, "Project voc audio path mismatch.");

        service.appendMessage(session, "hello");
        service.appendMessage(session, "\nworld");
        assertEquals("hello\nworld", session.getMessage(), "Session message append mismatch.");

        File batchEntry = new File("/batch/alpha");
        service.configureBatchProject(session, batchEntry);
        assertEquals("/batch/alpha\\alpha.ngp", session.getProjectPath(), "Batch project path mismatch.");
        assertEquals("/batch/alpha\\alpha.ng1", session.getExportPath(), "Batch export path mismatch.");

        System.out.println("Project session service verification passed.");
    }

    private static void assertEquals(String expected, String actual, String message) {
        if(!expected.equals(actual)) {
            throw new IllegalStateException(message + " Expected: " + expected + " Actual: " + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if(!value) {
            throw new IllegalStateException(message);
        }
    }
}
