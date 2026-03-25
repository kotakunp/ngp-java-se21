package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LegacyProjectSaveService extends Service<File> {

    private final LegacyProjectFacade legacyProjectFacade;
    private File projectFile;
    private LegacyProjectFacade.EditorProjectSnapshot snapshot;
    private int durationTimelineUnits;

    public LegacyProjectSaveService(LegacyProjectFacade legacyProjectFacade) {
        this.legacyProjectFacade = legacyProjectFacade;
    }

    public void save(File projectFile, LegacyProjectFacade.EditorProjectSnapshot snapshot, int durationTimelineUnits) {
        if (isRunning()) {
            cancel();
        }
        this.projectFile = projectFile;
        this.snapshot = snapshot;
        this.durationTimelineUnits = durationTimelineUnits;
        reset();
        start();
    }

    @Override
    protected Task<File> createTask() {
        final File selectedProjectFile = projectFile;
        final LegacyProjectFacade.EditorProjectSnapshot selectedSnapshot = snapshot;
        final int selectedDurationTimelineUnits = durationTimelineUnits;
        return new Task<File>() {
            @Override
            protected File call() throws Exception {
                if (selectedProjectFile == null) {
                    throw new IllegalStateException("No project file selected.");
                }
                if (selectedSnapshot == null) {
                    throw new IllegalStateException("Nothing to save.");
                }
                updateMessage("Saving project...");
                legacyProjectFacade.saveProject(selectedProjectFile, selectedSnapshot, selectedDurationTimelineUnits);
                return selectedProjectFile;
            }
        };
    }
}
