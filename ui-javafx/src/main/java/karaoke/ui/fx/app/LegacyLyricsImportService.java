package karaoke.ui.fx.app;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LegacyLyricsImportService extends Service<LegacyProjectFacade.EditorProjectSnapshot> {

    private final LegacyProjectFacade legacyProjectFacade;
    private File textFile;

    public LegacyLyricsImportService(LegacyProjectFacade legacyProjectFacade) {
        this.legacyProjectFacade = legacyProjectFacade;
    }

    public void load(File textFile) {
        if (isRunning()) {
            cancel();
        }
        this.textFile = textFile;
        reset();
        start();
    }

    @Override
    protected Task<LegacyProjectFacade.EditorProjectSnapshot> createTask() {
        final File selectedFile = textFile;
        return new Task<LegacyProjectFacade.EditorProjectSnapshot>() {
            @Override
            protected LegacyProjectFacade.EditorProjectSnapshot call() throws Exception {
                if (selectedFile == null) {
                    throw new IllegalStateException("No text file selected.");
                }
                updateMessage("Importing text...");
                return legacyProjectFacade.importText(selectedFile);
            }
        };
    }
}
