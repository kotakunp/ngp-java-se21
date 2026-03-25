package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LyricsEditorPane extends VBox {

    public LyricsEditorPane(FxShellState state) {
        ListView<String> lyricsList = new ListView<String>();
        lyricsList.setItems(state.getLyricLines());
        getChildren().add(lyricsList);
        VBox.setVgrow(lyricsList, Priority.ALWAYS);
    }
}
