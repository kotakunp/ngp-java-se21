package karaoke.ui.fx.view;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LyricsEditorPane extends VBox {

    public LyricsEditorPane() {
        ListView<String> lyricsList = new ListView<String>();
        lyricsList.setItems(FXCollections.observableArrayList(
            "Line 1: [JavaFX shell placeholder]",
            "Line 2: [Replace this with a virtualized line/word editor]",
            "Line 3: [Bind to migrated session and edit services]"
        ));

        getChildren().add(lyricsList);
        VBox.setVgrow(lyricsList, Priority.ALWAYS);
    }
}
