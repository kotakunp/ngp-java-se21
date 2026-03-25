package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class InspectorPane extends VBox {

    public InspectorPane(FxShellState state) {
        super(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: " + FxTheme.SURFACE + ";"
            + "-fx-border-color: " + FxTheme.BORDER + ";"
            + "-fx-border-width: 1;"
            + "-fx-background-radius: 12;"
            + "-fx-border-radius: 12;");

        getChildren().addAll(
            infoRow("Audio", state.audioStatusProperty()),
            infoRow("Project", state.projectStatusProperty()),
            infoRow("Selection", state.selectionStatusProperty()),
            infoRow("Playback", state.playbackStatusProperty()),
            infoRow("Busy", state.busyStatusProperty()),
            infoRow("Position", state.positionStatusProperty()),
            infoRow("Duration", state.durationStatusProperty()),
            infoRow("Selected", state.selectedWordIndexProperty().asString()),
            infoRow("Paint", Bindings.when(state.paintModeProperty()).then("Armed").otherwise("Idle")),
            infoRow("Paint Index", state.paintIndexProperty().asString()),
            infoRow("Words", state.wordCountProperty().asString()),
            infoRow("Lines", state.lineCountProperty().asString())
        );
    }

    private Label infoRow(String label, javafx.beans.value.ObservableValue<String> value) {
        Label row = new Label();
        row.textProperty().bind(Bindings.concat(label, ": ", value));
        row.setTextFill(Color.web(FxTheme.TEXT_SECONDARY));
        row.setFont(Font.font("Arial", 13));
        return row;
    }
}
