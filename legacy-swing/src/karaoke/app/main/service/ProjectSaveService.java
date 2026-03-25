package karaoke.app.main.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import karaoke.shared.io.Ng1ExportResult;
import karaoke.shared.io.Ng1ExportWriter;
import karaoke.shared.io.NgpProjectWriter;
import karaoke.shared.io.NgpSerializedProject;
import karaoke.shared.wordLine;

public class ProjectSaveService {

    private final NgpProjectWriter projectWriter;
    private final Ng1ExportWriter exportWriter;

    public ProjectSaveService(NgpProjectWriter projectWriter, Ng1ExportWriter exportWriter) {
        this.projectWriter = projectWriter;
        this.exportWriter = exportWriter;
    }

    public void saveProject(File projectFile, File lyricFile, List<wordLine> words, int duration, String songInfo) throws IOException {
        NgpSerializedProject project = projectWriter.serialize(words, duration, songInfo);
        write(projectFile, project.getProjectText());
        write(lyricFile, project.getLyricText());
    }

    public Ng1ExportResult buildNg1Export(List<wordLine> words) {
        return exportWriter.serialize(words);
    }

    public void saveNg1(File exportFile, List<wordLine> words) throws IOException {
        Ng1ExportResult export = exportWriter.serialize(words);
        if(!export.isSuccessful()) {
            throw new IllegalStateException("Invalid word timing at line " + export.getInvalidLineIndex() + " word " + export.getInvalidWord());
        }
        write(exportFile, export.getExportText());
    }

    public void write(File file, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }
}
