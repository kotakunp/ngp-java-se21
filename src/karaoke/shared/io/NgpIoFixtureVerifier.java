package karaoke.shared.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class NgpIoFixtureVerifier {

    public static void main(String[] args) throws Exception {
        File fixtureDir = new File("test-fixtures/io");
        NgpProjectReader reader = new NgpProjectReader();
        NgpProjectWriter projectWriter = new NgpProjectWriter();
        Ng1ExportWriter exportWriter = new Ng1ExportWriter();

        NgpProjectData project = reader.read(new File(fixtureDir, "basic.ngp"));
        assertEquals("frame,title,singer,lyrics,composer", project.getSongInfo(), "song info");
        assertEquals(4, project.getWords().size(), "word count");
        assertEquals(4, project.getPaintIndex(), "paint index");

        NgpSerializedProject serializedProject = projectWriter.serialize(project.getWords(), 200, project.getSongInfo());
        assertEquals(readFile(new File(fixtureDir, "basic-expected.ngp")), serializedProject.getProjectText(), "project serialization");
        assertEquals(readFile(new File(fixtureDir, "basic-expected-lyrics.txt")), serializedProject.getLyricText(), "lyric serialization");

        Ng1ExportResult export = exportWriter.serialize(project.getWords());
        assertTrue(export.isSuccessful(), "export should be successful");
        assertEquals(readFile(new File(fixtureDir, "basic-expected.ng1")), export.getExportText(), "export serialization");

        System.out.println("Fixture verification passed.");
    }

    private static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        char[] buffer = new char[1024];
        Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        try {
            int read;
            while((read = reader.read(buffer)) != -1) {
                content.append(buffer, 0, read);
            }
        } finally {
            reader.close();
        }
        return content.toString();
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        Object normalizedExpected = normalize(expected);
        Object normalizedActual = normalize(actual);
        if(normalizedExpected == null ? normalizedActual != null : !normalizedExpected.equals(normalizedActual)) {
            throw new IllegalStateException(label + " mismatch. expected=[" + normalizedExpected + "] actual=[" + normalizedActual + "]");
        }
    }

    private static void assertTrue(boolean value, String label) {
        if(!value) {
            throw new IllegalStateException(label);
        }
    }

    private static Object normalize(Object value) {
        if(!(value instanceof String)) {
            return value;
        }

        String normalized = ((String) value).replace("\r\n", "\n").replace('\r', '\n');
        while(normalized.endsWith("\n")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
