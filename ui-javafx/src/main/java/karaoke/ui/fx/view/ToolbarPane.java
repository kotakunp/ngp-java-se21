package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class ToolbarPane extends ToolBar {

    public ToolbarPane(FxShellViewModel viewModel) {
        getItems().addAll(
            actionButton("Import Audio", viewModel::importAudio),
            actionButton("Import Text", viewModel::importText),
            new Separator(Orientation.VERTICAL),
            actionButton("Open Project", viewModel::openProject),
            actionButton("Save Project", viewModel::saveProject),
            actionButton("Export", viewModel::exportProject),
            new Separator(Orientation.VERTICAL),
            actionButton("Play", viewModel::togglePlayback),
            actionButton("Paint", viewModel::togglePaintMode),
            actionButton("Mark Word", viewModel::markCurrentWord),
            actionButton("End Line", viewModel::markLineEnd),
            actionButton("Undo Paint", viewModel::undoLastPaint),
            new Separator(Orientation.VERTICAL),
            actionButton("Prev Word", viewModel::selectPreviousWord),
            actionButton("Next Word", viewModel::selectNextWord),
            new Separator(Orientation.VERTICAL),
            actionButton("Zoom In", viewModel::zoomIn),
            actionButton("Zoom Out", viewModel::zoomOut)
        );
        setStyle("-fx-background-color: linear-gradient(to right, " + FxTheme.TOOLBAR_START + ", " + FxTheme.TOOLBAR_END + ");");
        setPadding(new Insets(10, 12, 10, 12));
    }

    private Button actionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setStyle("-fx-background-color: " + FxTheme.BUTTON_BACKGROUND + ";"
            + "-fx-text-fill: " + FxTheme.TEXT_PRIMARY + ";"
            + "-fx-background-radius: 999;"
            + "-fx-padding: 7 14 7 14;");
        return button;
    }
}
