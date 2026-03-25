package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PreviewPane extends StackPane {

    public PreviewPane(FxShellState state) {
        setMinSize(460, 260);
        setPrefSize(460, 260);
        setStyle("-fx-background-color: linear-gradient(to bottom right, " + FxTheme.PREVIEW_START + ", " + FxTheme.PREVIEW_END + ");"
            + "-fx-border-color: " + FxTheme.BORDER + ";"
            + "-fx-border-width: 1;"
            + "-fx-background-radius: 18;"
            + "-fx-border-radius: 18;");

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Karaoke Preview");
        title.setTextFill(Color.web(FxTheme.TEXT_PRIMARY));
        title.setFont(Font.font("Arial", 26));

        Label currentLine = new Label();
        currentLine.textProperty().bind(state.previewCurrentLineProperty());
        currentLine.setWrapText(true);
        currentLine.setTextFill(Color.web(FxTheme.TEXT_PRIMARY));
        currentLine.setFont(Font.font("Arial", 24));
        currentLine.setAlignment(Pos.CENTER);

        Label nextLine = new Label();
        nextLine.textProperty().bind(state.previewNextLineProperty());
        nextLine.setWrapText(true);
        nextLine.setTextFill(Color.web(FxTheme.TEXT_MUTED));
        nextLine.setFont(Font.font("Arial", 18));
        nextLine.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, currentLine, nextLine);
        getChildren().add(content);
    }
}
