package karaoke.ui.fx.app;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import karaoke.app.main.service.LyricImportService;
import karaoke.app.main.service.ProjectSaveService;
import karaoke.shared.Location;
import karaoke.shared.myTextfield;
import karaoke.shared.io.Ng1ExportWriter;
import karaoke.shared.io.NgpProjectData;
import karaoke.shared.io.NgpProjectReader;
import karaoke.shared.io.NgpProjectWriter;
import karaoke.shared.wordLine;
import karaoke.ui.fx.timeline.TimelineWordMarker;

public class LegacyProjectFacade {

    private final LyricImportService lyricImportService = new LyricImportService();
    private final NgpProjectReader projectReader = new NgpProjectReader();
    private final ProjectSaveService projectSaveService = new ProjectSaveService(new NgpProjectWriter(), new Ng1ExportWriter());

    public EditorProjectSnapshot importText(File textFile) throws IOException {
        List<List<String>> lines = lyricImportService.readTextFile(textFile);
        return createSnapshot(createImportedWords(lines), 0, "", null, textFile);
    }

    public EditorProjectSnapshot openProject(File projectFile) throws IOException {
        NgpProjectData projectData = projectReader.read(projectFile);
        return createSnapshot(projectData.getWords(), projectData.getPaintIndex(), projectData.getSongInfo(), projectFile, null);
    }

    public void saveProject(File projectFile, EditorProjectSnapshot snapshot, int durationTimelineUnits) throws IOException {
        projectSaveService.saveProject(projectFile, lyricFileFor(projectFile), snapshot.getWords(), durationTimelineUnits, snapshot.getSongInfo());
    }

    public void exportProject(File exportFile, EditorProjectSnapshot snapshot) throws IOException {
        projectSaveService.saveNg1(exportFile, snapshot.getWords());
    }

    public EditorProjectSnapshot withProjectFile(EditorProjectSnapshot snapshot, File projectFile) {
        return new EditorProjectSnapshot(
            snapshot.getLines(),
            snapshot.getMarkers(),
            snapshot.getWords(),
            snapshot.getWordCount(),
            snapshot.getLineCount(),
            snapshot.getPaintIndex(),
            snapshot.getSongInfo(),
            projectFile,
            snapshot.getImportedTextFile()
        );
    }

    public EditorProjectSnapshot refreshSnapshot(EditorProjectSnapshot snapshot, int paintIndex) {
        return createSnapshot(snapshot.getWords(), paintIndex, snapshot.getSongInfo(), snapshot.getProjectFile(), snapshot.getImportedTextFile());
    }

    private File lyricFileFor(File projectFile) {
        String fileName = projectFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        return new File(projectFile.getParentFile(), baseName + "-.txt");
    }

    private EditorProjectSnapshot createSnapshot(List<wordLine> words, int paintIndex, String songInfo, File projectFile, File importedTextFile) {
        List<String> renderedLines = new ArrayList<String>();
        List<TimelineWordMarker> markers = new ArrayList<TimelineWordMarker>();
        int currentLine = -1;
        int timelineCursor = 0;
        StringBuilder lineBuilder = new StringBuilder();

        for (wordLine word : words) {
            if (currentLine != word.getLine_idx()) {
                if (lineBuilder.length() > 0) {
                    renderedLines.add(lineBuilder.toString().trim());
                    lineBuilder.setLength(0);
                }
                currentLine = word.getLine_idx();
            }

            lineBuilder.append(word.getWord()).append(' ');
            int start = word.isPaint() ? word.getSec() : timelineCursor;
            int end = word.isPaint() ? Math.max(word.getLow_sec(), word.getSec() + 1) : start + Math.max(18, word.getWord().length() * 10);
            markers.add(new TimelineWordMarker(word.getWord(), word.getLine_idx(), word.getIdx(), start, end, word.isPaint()));
            timelineCursor = end + 8;
        }

        if (lineBuilder.length() > 0) {
            renderedLines.add(lineBuilder.toString().trim());
        }

        return new EditorProjectSnapshot(
            renderedLines,
            markers,
            words,
            words.size(),
            renderedLines.size(),
            paintIndex,
            songInfo == null ? "" : songInfo,
            projectFile,
            importedTextFile
        );
    }

    private List<wordLine> createImportedWords(List<List<String>> lines) {
        List<wordLine> words = new ArrayList<wordLine>();
        int wordIndex = 0;
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            List<String> line = lines.get(lineIndex);
            for (int tokenIndex = 0; tokenIndex < line.size(); tokenIndex++) {
                String text = line.get(tokenIndex);
                wordLine word = new wordLine(text, new myTextfield(text, wordIndex, lineIndex, Color.lightGray), wordIndex);
                word.setLine_idx(lineIndex);
                word.setLocation(locationFor(line, tokenIndex));
                words.add(word);
                wordIndex++;
            }
        }
        return words;
    }

    private Location locationFor(List<String> line, int tokenIndex) {
        if (line.size() == 1 || tokenIndex == line.size() - 1) {
            return Location.end;
        }
        if (tokenIndex == 0) {
            return Location.start;
        }
        return Location.middle;
    }

    public static final class EditorProjectSnapshot {
        private final List<String> lines;
        private final List<TimelineWordMarker> markers;
        private final List<wordLine> words;
        private final int wordCount;
        private final int lineCount;
        private final int paintIndex;
        private final String songInfo;
        private final File projectFile;
        private final File importedTextFile;

        public EditorProjectSnapshot(
            List<String> lines,
            List<TimelineWordMarker> markers,
            List<wordLine> words,
            int wordCount,
            int lineCount,
            int paintIndex,
            String songInfo,
            File projectFile,
            File importedTextFile)
        {
            this.lines = lines;
            this.markers = markers;
            this.words = words;
            this.wordCount = wordCount;
            this.lineCount = lineCount;
            this.paintIndex = paintIndex;
            this.songInfo = songInfo;
            this.projectFile = projectFile;
            this.importedTextFile = importedTextFile;
        }

        public List<String> getLines() {
            return lines;
        }

        public List<TimelineWordMarker> getMarkers() {
            return markers;
        }

        public List<wordLine> getWords() {
            return words;
        }

        public int getWordCount() {
            return wordCount;
        }

        public int getLineCount() {
            return lineCount;
        }

        public int getPaintIndex() {
            return paintIndex;
        }

        public String getSongInfo() {
            return songInfo;
        }

        public File getProjectFile() {
            return projectFile;
        }

        public File getImportedTextFile() {
            return importedTextFile;
        }
    }
}
