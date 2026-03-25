package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import karaoke.ui.fx.app.FxShellViewModel;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FxShellRoot extends BorderPane {

    public FxShellRoot(FxShellViewModel viewModel) {
        FxShellState state = viewModel.getState();

        setTop(new ToolbarPane(viewModel));
        setCenter(buildWorkspace(state));
        setBottom(new StatusBarPane(state));
        setStyle("-fx-background-color: " + FxTheme.APP_BACKGROUND + ";");
    }

    private SplitPane buildWorkspace(FxShellState state) {
        SplitPane horizontal = new SplitPane();
        horizontal.setDividerPositions(0.7);
        horizontal.getItems().addAll(buildEditorPane(state), buildSidebar(state));
        horizontal.setStyle("-fx-background-color: " + FxTheme.APP_BACKGROUND + ";");
        return horizontal;
    }

    private VBox buildEditorPane(FxShellState state) {
        VBox editorPane = new VBox(14);
        editorPane.setPadding(new Insets(16));

        Label title = sectionTitle("Timeline Editor");
        TimelineCanvasPane timelinePane = new TimelineCanvasPane(state);
        timelinePane.setMinHeight(420);

        Label subtitle = sectionTitle("Lyrics Editor");
        LyricsEditorPane lyricsEditor = new LyricsEditorPane();
        VBox.setVgrow(timelinePane, Priority.ALWAYS);
        VBox.setVgrow(lyricsEditor, Priority.ALWAYS);

        editorPane.getChildren().addAll(title, timelinePane, subtitle, lyricsEditor);
        return editorPane;
    }

    private VBox buildSidebar(FxShellState state) {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(16, 16, 16, 0));
        sidebar.getChildren().addAll(
            sectionTitle("Preview"),
            new PreviewPane(state),
            sectionTitle("Project State"),
            new InspectorPane(state)
        );
        return sidebar;
    }

    private Label sectionTitle(String text) {
        Label title = new Label(text);
        title.setTextFill(Color.web(FxTheme.TEXT_PRIMARY));
        title.setFont(Font.font("Arial", 18));
        return title;
    }
}
