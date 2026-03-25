package karaoke.ui.fx.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FxShellState {

    private final StringProperty audioStatus = new SimpleStringProperty("Not loaded");
    private final StringProperty projectStatus = new SimpleStringProperty("No project open");
    private final StringProperty selectionStatus = new SimpleStringProperty("No word selected");
    private final StringProperty playbackStatus = new SimpleStringProperty("Stopped");
    private final StringProperty shellStatus = new SimpleStringProperty("JavaFX migration module");
    private final StringProperty nextStepStatus = new SimpleStringProperty("Bind this shell to core services and audio engine");
    private final StringProperty timelineStatus = new SimpleStringProperty("Timeline shell: waveform canvas, cursor, markers, zoom, and paint overlays will live here.");
    private final BooleanProperty paintMode = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);

    public StringProperty audioStatusProperty() {
        return audioStatus;
    }

    public StringProperty projectStatusProperty() {
        return projectStatus;
    }

    public StringProperty selectionStatusProperty() {
        return selectionStatus;
    }

    public StringProperty playbackStatusProperty() {
        return playbackStatus;
    }

    public StringProperty shellStatusProperty() {
        return shellStatus;
    }

    public StringProperty nextStepStatusProperty() {
        return nextStepStatus;
    }

    public StringProperty timelineStatusProperty() {
        return timelineStatus;
    }

    public BooleanProperty paintModeProperty() {
        return paintMode;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }
}
