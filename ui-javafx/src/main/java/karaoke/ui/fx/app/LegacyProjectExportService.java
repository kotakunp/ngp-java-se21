package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LegacyProjectExportService extends Service<File> {

    private final LegacyProjectFacade legacyProjectFacade;
    private File exportFile;
    private LegacyProjectFacade.EditorProjectSnapshot snapshot;

    public LegacyProjectExportService(LegacyProjectFacade legacyProjectFacade) {
        this.legacyProjectFacade = legacyProjectFacade;
    }

    public void export(File exportFile, LegacyProjectFacade.EditorProjectSnapshot snapshot) {
        if (isRunning()) {
            cancel();
        }
        this.exportFile = exportFile;
        this.snapshot = snapshot;
        reset();
        start();
    }

    @Override
    protected Task<File> createTask() {
        final File selectedExportFile = exportFile;
        final LegacyProjectFacade.EditorProjectSnapshot selectedSnapshot = snapshot;
        return new Task<File>() {
            @Override
            protected File call() throws Exception {
                if (selectedExportFile == null) {
                    throw new IllegalStateException("No export file selected.");
                }
                if (selectedSnapshot == null) {
                    throw new IllegalStateException("Nothing to export.");
                }
                updateMessage("Exporting timing file...");
                legacyProjectFacade.exportProject(selectedExportFile, selectedSnapshot);
                return selectedExportFile;
            }
        };
    }
}
