package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import karaoke.app.main.service.PaintWorkflowService;
import karaoke.app.main.service.ProjectSession;
import karaoke.app.main.service.SelectionNavigationService;
import karaoke.ui.fx.audio.AudioPlaybackService;
import karaoke.ui.fx.audio.StreamAudioPlaybackService;

public class FxShellViewModel {

    private final FxShellState state = new FxShellState();
    private final LegacyProjectFacade legacyProjectFacade = new LegacyProjectFacade();
    private final LegacyLyricsImportService lyricsImportService = new LegacyLyricsImportService(legacyProjectFacade);
    private final LegacyProjectOpenService projectOpenService = new LegacyProjectOpenService(legacyProjectFacade);
    private final LegacyProjectSaveService projectSaveService = new LegacyProjectSaveService(legacyProjectFacade);
    private final LegacyProjectExportService projectExportService = new LegacyProjectExportService(legacyProjectFacade);
    private final AudioPlaybackService audioPlaybackService = new StreamAudioPlaybackService();
    private final ProjectSession projectSession = new ProjectSession();
    private final PaintWorkflowService paintWorkflowService = new PaintWorkflowService();
    private final SelectionNavigationService selectionNavigationService = new SelectionNavigationService();
    private LegacyProjectFacade.EditorProjectSnapshot currentProject;
    private Window ownerWindow;

    public FxShellViewModel() {
        state.loadingProperty().bind(
            lyricsImportService.runningProperty()
                .or(projectOpenService.runningProperty())
                .or(projectSaveService.runningProperty())
                .or(projectExportService.runningProperty())
                .or(audioPlaybackService.loadingProperty())
        );
        bindWorkerState(lyricsImportService);
        bindWorkerState(projectOpenService);
        bindWorkerState(projectSaveService);
        bindWorkerState(projectExportService);
        bindAudioState();

        lyricsImportService.setOnSucceeded(event -> {
            applyProject(lyricsImportService.getValue(), "Imported text");
            state.projectStatusProperty().set("Imported text");
            state.timelineStatusProperty().set("Imported text into a JavaFX editor session backed by the legacy project model.");
            state.nextStepStatusProperty().set("Select a word on the timeline, then bind paint commands to audio playback time.");
            state.busyStatusProperty().set("Text import complete");
        });

        lyricsImportService.setOnFailed(event -> {
            state.projectStatusProperty().set("Text import failed");
            state.busyStatusProperty().set(messageForFailure(lyricsImportService));
            state.nextStepStatusProperty().set(messageForFailure(lyricsImportService));
        });

        projectOpenService.setOnSucceeded(event -> {
            applyProject(projectOpenService.getValue(), "Opened project");
            state.projectStatusProperty().set("Opened project");
            state.timelineStatusProperty().set("Opened project into the JavaFX editor session. Save/export now runs through the same underlying words.");
            state.nextStepStatusProperty().set("Bind mark-word and end-line commands to playback time and update markers from edited timing.");
            state.busyStatusProperty().set("Project open complete");
        });

        projectOpenService.setOnFailed(event -> {
            state.projectStatusProperty().set("Project open failed");
            state.busyStatusProperty().set(messageForFailure(projectOpenService));
            state.nextStepStatusProperty().set(messageForFailure(projectOpenService));
        });

        projectSaveService.setOnSucceeded(event -> {
            File savedFile = projectSaveService.getValue();
            currentProject = legacyProjectFacade.withProjectFile(currentProject, savedFile);
            state.projectStatusProperty().set("Saved project: " + savedFile.getName());
            state.busyStatusProperty().set("Project saved");
            state.nextStepStatusProperty().set("Export the timing file or continue wiring paint commands.");
        });

        projectSaveService.setOnFailed(event -> {
            state.projectStatusProperty().set("Project save failed");
            state.busyStatusProperty().set(messageForFailure(projectSaveService));
            state.nextStepStatusProperty().set(messageForFailure(projectSaveService));
        });

        projectExportService.setOnSucceeded(event -> {
            File exportFile = projectExportService.getValue();
            state.projectStatusProperty().set("Exported timing: " + exportFile.getName());
            state.busyStatusProperty().set("Export complete");
            state.nextStepStatusProperty().set("Continue wiring paint mode so timing edits come from JavaFX actions.");
        });

        projectExportService.setOnFailed(event -> {
            state.projectStatusProperty().set("Export failed");
            state.busyStatusProperty().set(messageForFailure(projectExportService));
            state.nextStepStatusProperty().set(messageForFailure(projectExportService));
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
        audioPlaybackService.load(file);
        state.nextStepStatusProperty().set("Audio is loading through the streaming playback service.");
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
        if (currentProject == null) {
            state.projectStatusProperty().set("Nothing to save");
            state.nextStepStatusProperty().set("Import text or open a project first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Project");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Karaoke Project", "*.ngp"));
        File initialProjectFile = currentProject.getProjectFile();
        if (initialProjectFile != null && initialProjectFile.getParentFile() != null) {
            chooser.setInitialDirectory(initialProjectFile.getParentFile());
            chooser.setInitialFileName(initialProjectFile.getName());
        } else {
            chooser.setInitialFileName("project.ngp");
        }

        File selectedFile = chooser.showSaveDialog(ownerWindow);
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.getName().toLowerCase().endsWith(".ngp")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".ngp");
        }

        state.projectStatusProperty().set("Saving project: " + selectedFile.getName());
        projectSaveService.save(selectedFile, currentProject, (int) audioPlaybackService.durationTimelineUnitsProperty().get());
    }

    public void exportProject() {
        if (currentProject == null) {
            state.projectStatusProperty().set("Nothing to export");
            state.nextStepStatusProperty().set("Open a painted project before exporting.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Timing File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Timing Export", "*.ng1"));
        chooser.setInitialFileName("project.ng1");

        File selectedFile = chooser.showSaveDialog(ownerWindow);
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.getName().toLowerCase().endsWith(".ng1")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".ng1");
        }

        state.projectStatusProperty().set("Exporting timing: " + selectedFile.getName());
        projectExportService.export(selectedFile, currentProject);
    }

    public void togglePlayback() {
        audioPlaybackService.togglePlayback();
        state.nextStepStatusProperty().set("Playback is now driven by the streaming audio service boundary.");
    }

    public void togglePaintMode() {
        boolean nextPaintMode = !state.paintModeProperty().get();
        state.paintModeProperty().set(nextPaintMode);
        if (nextPaintMode && state.selectedWordIndexProperty().get() >= 0) {
            projectSession.setPaintIndex(state.selectedWordIndexProperty().get());
            state.paintIndexProperty().set(projectSession.getPaintIndex());
        }
        state.selectionStatusProperty().set(nextPaintMode
            ? "Paint mode armed at word " + state.paintIndexProperty().get()
            : "Paint mode idle");
        state.nextStepStatusProperty().set("Use the selected word as the paint anchor, then bind mark/end-line commands to playback time.");
    }

    public void markCurrentWord() {
        if (currentProject == null) {
            state.nextStepStatusProperty().set("Import text or open a project first.");
            return;
        }
        if (!state.paintModeProperty().get()) {
            state.nextStepStatusProperty().set("Arm paint mode first.");
            return;
        }
        JavaFxPaintBridge paintBridge = new JavaFxPaintBridge(currentProject.getWords(), audioPlaybackService);
        if (paintWorkflowService.markCurrentWord(projectSession, paintBridge)) {
            refreshCurrentProject();
            state.selectionStatusProperty().set("Marked word " + (projectSession.getPaintIndex() - 1));
            state.nextStepStatusProperty().set("Continue marking words or mark the line end.");
        } else {
            state.nextStepStatusProperty().set("Could not mark the current word.");
        }
    }

    public void markLineEnd() {
        if (currentProject == null || projectSession.getPaintIndex() <= 0) {
            state.nextStepStatusProperty().set("Mark at least one word before ending a line.");
            return;
        }
        JavaFxPaintBridge paintBridge = new JavaFxPaintBridge(currentProject.getWords(), audioPlaybackService);
        paintBridge.addEndLine(Boolean.TRUE, projectSession.getPaintIndex() - 1);
        refreshCurrentProject();
        state.selectionStatusProperty().set("Marked end-of-line at word " + (projectSession.getPaintIndex() - 1));
        state.nextStepStatusProperty().set("Line end applied. Continue painting or export once timings are complete.");
    }

    public void undoLastPaint() {
        if (currentProject == null) {
            state.nextStepStatusProperty().set("Nothing to undo.");
            return;
        }
        JavaFxPaintBridge paintBridge = new JavaFxPaintBridge(currentProject.getWords(), audioPlaybackService);
        if (paintWorkflowService.undoLastPaint(projectSession, currentProject.getWords(), paintBridge)) {
            refreshCurrentProject();
            state.selectionStatusProperty().set("Removed last painted timing.");
            state.nextStepStatusProperty().set("Continue painting from the restored cursor.");
        } else {
            state.nextStepStatusProperty().set("No painted word to undo.");
        }
    }

    public void zoomIn() {
        state.timelineStatusProperty().set("Zoom in requested. Add timeline zoom state and multi-resolution waveform rendering.");
    }

    public void zoomOut() {
        state.timelineStatusProperty().set("Zoom out requested. Add timeline zoom state and multi-resolution waveform rendering.");
    }

    public void selectWord(int wordIndex) {
        if (currentProject == null || wordIndex < 0 || wordIndex >= currentProject.getWords().size()) {
            projectSession.setSelectedWordIndex(-1);
            state.selectedWordIndexProperty().set(-1);
            state.selectionStatusProperty().set("No word selected");
            return;
        }
        projectSession.setSelectedWordIndex(wordIndex);
        projectSession.setTempSelectedWordIndex(wordIndex);
        projectSession.setSelectedLineIndex(currentProject.getWords().get(wordIndex).getLine_idx());
        state.selectedWordIndexProperty().set(wordIndex);
        state.selectionStatusProperty().set("Selected word " + wordIndex + ": " + currentProject.getWords().get(wordIndex).getWord());
    }

    public void selectPreviousWord() {
        if (currentProject == null) {
            return;
        }
        if (selectionNavigationService.moveSelectionLeft(projectSession)) {
            selectWord(projectSession.getSelectedWordIndex());
        }
    }

    public void selectNextWord() {
        if (currentProject == null) {
            return;
        }
        if (selectionNavigationService.moveSelectionRight(projectSession, currentProject.getWords().size())) {
            selectWord(projectSession.getSelectedWordIndex());
        }
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

    private void bindAudioState() {
        audioPlaybackService.statusProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().length() > 0) {
                state.busyStatusProperty().set(newValue);
                state.playbackStatusProperty().set(newValue);
            }
        });
        audioPlaybackService.playingProperty().addListener((obs, oldValue, newValue) -> {
            state.playingProperty().set(Boolean.TRUE.equals(newValue));
        });
        audioPlaybackService.durationMicrosProperty().addListener((obs, oldValue, newValue) -> {
            state.durationMicrosProperty().set(newValue.longValue());
            state.durationStatusProperty().set(formatMicros(newValue.longValue()));
        });
        audioPlaybackService.positionMicrosProperty().addListener((obs, oldValue, newValue) -> {
            state.positionMicrosProperty().set(newValue.longValue());
            state.positionStatusProperty().set(formatMicros(newValue.longValue()));
        });
    }

    private void applyProject(LegacyProjectFacade.EditorProjectSnapshot project, String shellStatus) {
        currentProject = project;
        state.shellStatusProperty().set(shellStatus);
        state.getLyricLines().setAll(project.getLines());
        state.getTimelineMarkers().setAll(project.getMarkers());
        state.wordCountProperty().set(project.getWordCount());
        state.lineCountProperty().set(project.getLineCount());
        projectSession.setPaintIndex(project.getPaintIndex());
        state.paintIndexProperty().set(projectSession.getPaintIndex());
        selectWord(project.getPaintIndex());
    }

    private void refreshCurrentProject() {
        if (currentProject == null) {
            return;
        }
        currentProject = legacyProjectFacade.refreshSnapshot(currentProject, projectSession.getPaintIndex());
        state.getLyricLines().setAll(currentProject.getLines());
        state.getTimelineMarkers().setAll(currentProject.getMarkers());
        state.paintIndexProperty().set(projectSession.getPaintIndex());
        state.wordCountProperty().set(currentProject.getWordCount());
        state.lineCountProperty().set(currentProject.getLineCount());
    }

    private String formatMicros(long micros) {
        long totalSeconds = Math.max(0L, micros / 1_000_000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
