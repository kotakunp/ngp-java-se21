package karaoke.ui.fx.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import karaoke.ui.fx.timeline.TimelineWordMarker;

public class FxShellState {

    private final StringProperty audioStatus = new SimpleStringProperty("Not loaded");
    private final StringProperty projectStatus = new SimpleStringProperty("No project open");
    private final StringProperty selectionStatus = new SimpleStringProperty("No word selected");
    private final StringProperty playbackStatus = new SimpleStringProperty("Stopped");
    private final StringProperty shellStatus = new SimpleStringProperty("JavaFX migration module");
    private final StringProperty nextStepStatus = new SimpleStringProperty("Bind this shell to core services and audio engine");
    private final StringProperty timelineStatus = new SimpleStringProperty("Timeline shell: waveform canvas, cursor, markers, zoom, and paint overlays will live here.");
    private final StringProperty busyStatus = new SimpleStringProperty("Idle");
    private final IntegerProperty wordCount = new SimpleIntegerProperty(0);
    private final IntegerProperty lineCount = new SimpleIntegerProperty(0);
    private final IntegerProperty paintIndex = new SimpleIntegerProperty(0);
    private final BooleanProperty paintMode = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObservableList<String> lyricLines = FXCollections.observableArrayList();
    private final ObservableList<TimelineWordMarker> timelineMarkers = FXCollections.observableArrayList();

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

    public StringProperty busyStatusProperty() {
        return busyStatus;
    }

    public IntegerProperty wordCountProperty() {
        return wordCount;
    }

    public IntegerProperty lineCountProperty() {
        return lineCount;
    }

    public IntegerProperty paintIndexProperty() {
        return paintIndex;
    }

    public BooleanProperty paintModeProperty() {
        return paintMode;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public ObservableList<String> getLyricLines() {
        return lyricLines;
    }

    public ObservableList<TimelineWordMarker> getTimelineMarkers() {
        return timelineMarkers;
    }
}
