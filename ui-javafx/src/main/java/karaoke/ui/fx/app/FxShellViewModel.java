package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FxShellViewModel {

    private final FxShellState state = new FxShellState();
    private final LegacyProjectFacade legacyProjectFacade = new LegacyProjectFacade();
    private final LegacyLyricsImportService lyricsImportService = new LegacyLyricsImportService(legacyProjectFacade);
    private final LegacyProjectOpenService projectOpenService = new LegacyProjectOpenService(legacyProjectFacade);
    private Window ownerWindow;

    public FxShellViewModel() {
        state.loadingProperty().bind(lyricsImportService.runningProperty().or(projectOpenService.runningProperty()));
        bindWorkerState(lyricsImportService);
        bindWorkerState(projectOpenService);

        lyricsImportService.setOnSucceeded(event -> {
            LegacyProjectFacade.ImportedLyrics lyrics = lyricsImportService.getValue();
            state.getLyricLines().setAll(lyrics.getLines());
            state.getTimelineMarkers().setAll(lyrics.getMarkers());
            state.wordCountProperty().set(lyrics.getWordCount());
            state.lineCountProperty().set(lyrics.getLineCount());
            state.paintIndexProperty().set(0);
            state.projectStatusProperty().set("Imported text");
            state.selectionStatusProperty().set("No word selected");
            state.timelineStatusProperty().set("Imported " + lyrics.getWordCount() + " words across " + lyrics.getLineCount() + " lines. Timeline binding is next.");
            state.nextStepStatusProperty().set("Map imported lyric lines into the JavaFX editor and timeline model");
            state.busyStatusProperty().set("Text import complete");
        });

        lyricsImportService.setOnFailed(event -> {
            state.projectStatusProperty().set("Text import failed");
            state.busyStatusProperty().set(messageForFailure(lyricsImportService));
            state.nextStepStatusProperty().set(messageForFailure(lyricsImportService));
        });

        projectOpenService.setOnSucceeded(event -> {
            LegacyProjectFacade.OpenedProject project = projectOpenService.getValue();
            state.getLyricLines().setAll(project.getLines());
            state.getTimelineMarkers().setAll(project.getMarkers());
            state.wordCountProperty().set(project.getWordCount());
            state.lineCountProperty().set(project.getLineCount());
            state.paintIndexProperty().set(project.getPaintIndex());
            state.projectStatusProperty().set("Opened project");
            state.selectionStatusProperty().set("Paint index: " + project.getPaintIndex());
            state.timelineStatusProperty().set("Loaded project with " + project.getWordCount() + " words. Bind paint index and selection into the timeline.");
            state.nextStepStatusProperty().set("Connect opened project data into JavaFX session state and timeline rendering");
            state.busyStatusProperty().set("Project open complete");
        });

        projectOpenService.setOnFailed(event -> {
            state.projectStatusProperty().set("Project open failed");
            state.busyStatusProperty().set(messageForFailure(projectOpenService));
            state.nextStepStatusProperty().set(messageForFailure(projectOpenService));
        });
    }

    public FxShellState getState() {
        return state;
    }

    public void setOwnerWindow(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    public void importAudio() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Audio");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.wav"));
        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) {
            return;
        }
        state.audioStatusProperty().set(file.getName());
        state.nextStepStatusProperty().set("Connect this audio file to the new streaming playback engine");
    }

    public void importText() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Text");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) {
            return;
        }
        state.projectStatusProperty().set("Importing text: " + file.getName());
        lyricsImportService.load(file);
    }

    public void openProject() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Project");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Karaoke Project", "*.ngp"));
        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) {
            return;
        }
        state.projectStatusProperty().set("Opening project: " + file.getName());
        projectOpenService.load(file);
    }

    public void saveProject() {
        state.projectStatusProperty().set("Project save workflow not wired yet");
        state.nextStepStatusProperty().set("Connect Save Project to ProjectSaveService");
    }

    public void exportProject() {
        state.projectStatusProperty().set("Export workflow not wired yet");
        state.nextStepStatusProperty().set("Connect Export to Ng1ExportWriter via the application layer");
    }

    public void togglePlayback() {
        boolean nextPlaying = !state.playingProperty().get();
        state.playingProperty().set(nextPlaying);
        state.playbackStatusProperty().set(nextPlaying ? "Playing (shell state only)" : "Stopped");
        state.audioStatusProperty().set(nextPlaying ? "Playback requested" : state.audioStatusProperty().get());
        state.nextStepStatusProperty().set("Connect playback controls to the new audio engine");
    }

    public void togglePaintMode() {
        boolean nextPaintMode = !state.paintModeProperty().get();
        state.paintModeProperty().set(nextPaintMode);
        state.selectionStatusProperty().set(nextPaintMode ? "Paint mode armed" : "Paint mode idle");
        state.nextStepStatusProperty().set("Connect paint mode to migrated timeline and selection services");
    }

    public void zoomIn() {
        state.timelineStatusProperty().set("Zoom in requested. Add timeline zoom model and multi-resolution waveform cache.");
    }

    public void zoomOut() {
        state.timelineStatusProperty().set("Zoom out requested. Add timeline zoom model and multi-resolution waveform cache.");
    }

    private void bindWorkerState(Service<?> service) {
        service.messageProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().length() > 0) {
                state.busyStatusProperty().set(newValue);
            }
        });
    }

    private String messageForFailure(Service<?> service) {
        Throwable exception = service.getException();
        if (exception == null) {
            return "Operation failed";
        }
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }
}
