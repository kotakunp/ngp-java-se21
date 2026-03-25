package karaoke.ui.fx.app;

public class FxShellViewModel {

    private final FxShellState state = new FxShellState();

    public FxShellState getState() {
        return state;
    }

    public void importAudio() {
        state.audioStatusProperty().set("Audio import workflow not wired yet");
        state.nextStepStatusProperty().set("Connect Import Audio to the migrated audio service");
    }

    public void importText() {
        state.projectStatusProperty().set("Text import workflow not wired yet");
        state.nextStepStatusProperty().set("Connect Import Text to LyricImportService");
    }

    public void openProject() {
        state.projectStatusProperty().set("Project open workflow not wired yet");
        state.nextStepStatusProperty().set("Connect Open Project to NgpProjectReader and session state");
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
}
