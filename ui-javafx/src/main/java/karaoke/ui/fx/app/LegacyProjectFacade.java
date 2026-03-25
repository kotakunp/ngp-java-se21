package karaoke.ui.fx.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import karaoke.app.main.service.LyricImportService;
import karaoke.shared.io.NgpProjectData;
import karaoke.shared.io.NgpProjectReader;
import karaoke.shared.wordLine;
import karaoke.ui.fx.timeline.TimelineWordMarker;

public class LegacyProjectFacade {

    private final LyricImportService lyricImportService = new LyricImportService();
    private final NgpProjectReader projectReader = new NgpProjectReader();

    public ImportedLyrics importText(File textFile) throws IOException {
        List<List<String>> lines = lyricImportService.readTextFile(textFile);
        List<String> renderedLines = new ArrayList<String>();
        List<TimelineWordMarker> markers = new ArrayList<TimelineWordMarker>();
        int wordCount = 0;
        int timelineCursor = 0;
        for (List<String> line : lines) {
            renderedLines.add(String.join(" ", line));
            for (int i = 0; i < line.size(); i++) {
                String word = line.get(i);
                int duration = Math.max(18, word.length() * 10);
                markers.add(new TimelineWordMarker(word, renderedLines.size() - 1, wordCount + i, timelineCursor, timelineCursor + duration, false));
                timelineCursor += duration + 8;
            }
            wordCount += line.size();
            timelineCursor += 24;
        }
        return new ImportedLyrics(renderedLines, markers, wordCount, lines.size());
    }

    public OpenedProject openProject(File projectFile) throws IOException {
        NgpProjectData projectData = projectReader.read(projectFile);
        List<String> renderedLines = new ArrayList<String>();
        List<TimelineWordMarker> markers = new ArrayList<TimelineWordMarker>();
        List<wordLine> words = projectData.getWords();
        int currentLine = -1;
        StringBuilder builder = new StringBuilder();
        for (wordLine word : words) {
            if (currentLine != word.getLine_idx()) {
                if (builder.length() > 0) {
                    renderedLines.add(builder.toString().trim());
                    builder.setLength(0);
                }
                currentLine = word.getLine_idx();
            }
            builder.append(word.getWord()).append(' ');
            markers.add(new TimelineWordMarker(
                word.getWord(),
                word.getLine_idx(),
                word.getIdx(),
                word.getSec(),
                Math.max(word.getLow_sec(), word.getSec() + 1),
                word.isPaint()
            ));
        }
        if (builder.length() > 0) {
            renderedLines.add(builder.toString().trim());
        }
        return new OpenedProject(renderedLines, markers, words.size(), renderedLines.size(), projectData.getPaintIndex(), projectData.getSongInfo());
    }

    public static final class ImportedLyrics {
        private final List<String> lines;
        private final List<TimelineWordMarker> markers;
        private final int wordCount;
        private final int lineCount;

        public ImportedLyrics(List<String> lines, List<TimelineWordMarker> markers, int wordCount, int lineCount) {
            this.lines = lines;
            this.markers = markers;
            this.wordCount = wordCount;
            this.lineCount = lineCount;
        }

        public List<String> getLines() {
            return lines;
        }

        public int getWordCount() {
            return wordCount;
        }

        public int getLineCount() {
            return lineCount;
        }

        public List<TimelineWordMarker> getMarkers() {
            return markers;
        }
    }

    public static final class OpenedProject {
        private final List<String> lines;
        private final List<TimelineWordMarker> markers;
        private final int wordCount;
        private final int lineCount;
        private final int paintIndex;
        private final String songInfo;

        public OpenedProject(List<String> lines, List<TimelineWordMarker> markers, int wordCount, int lineCount, int paintIndex, String songInfo) {
            this.lines = lines;
            this.markers = markers;
            this.wordCount = wordCount;
            this.lineCount = lineCount;
            this.paintIndex = paintIndex;
            this.songInfo = songInfo;
        }

        public List<String> getLines() {
            return lines;
        }

        public int getWordCount() {
            return wordCount;
        }

        public int getLineCount() {
            return lineCount;
        }

        public List<TimelineWordMarker> getMarkers() {
            return markers;
        }

        public int getPaintIndex() {
            return paintIndex;
        }

        public String getSongInfo() {
            return songInfo;
        }
    }
}
