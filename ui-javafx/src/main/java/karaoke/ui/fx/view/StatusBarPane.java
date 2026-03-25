package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StatusBarPane extends BorderPane {

    public StatusBarPane(FxShellState state) {
        setPadding(new Insets(10, 16, 12, 16));
        setStyle("-fx-background-color: #0b0f14; -fx-border-color: " + FxTheme.MUTED_BORDER + " transparent transparent transparent;");
        setLeft(statusLabel(Bindings.concat("Shell: ", state.shellStatusProperty())));
        setRight(statusLabel(Bindings.concat("Next: ", state.nextStepStatusProperty())));
    }

    private Label statusLabel(javafx.beans.value.ObservableValue<String> text) {
        Label label = new Label();
        label.textProperty().bind(text);
        label.setTextFill(Color.web(FxTheme.TEXT_SECONDARY));
        label.setFont(Font.font("Arial", 13));
        return label;
    }
}
