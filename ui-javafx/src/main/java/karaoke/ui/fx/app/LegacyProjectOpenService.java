package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LegacyProjectOpenService extends Service<LegacyProjectFacade.OpenedProject> {

    private final LegacyProjectFacade legacyProjectFacade;
    private File projectFile;

    public LegacyProjectOpenService(LegacyProjectFacade legacyProjectFacade) {
        this.legacyProjectFacade = legacyProjectFacade;
    }

    public void load(File projectFile) {
        if (isRunning()) {
            cancel();
        }
        this.projectFile = projectFile;
        reset();
        start();
    }

    @Override
    protected Task<LegacyProjectFacade.OpenedProject> createTask() {
        final File selectedFile = projectFile;
        return new Task<LegacyProjectFacade.OpenedProject>() {
            @Override
            protected LegacyProjectFacade.OpenedProject call() throws Exception {
                if (selectedFile == null) {
                    throw new IllegalStateException("No project file selected.");
                }
                updateMessage("Opening project...");
                return legacyProjectFacade.openProject(selectedFile);
            }
        };
    }
}
