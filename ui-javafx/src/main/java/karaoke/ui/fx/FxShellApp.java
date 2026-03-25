package karaoke.ui.fx;

import karaoke.ui.fx.app.FxShellViewModel;
import karaoke.ui.fx.view.FxShellRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxShellApp extends Application {

    @Override
    public void start(Stage stage) {
        FxShellViewModel viewModel = new FxShellViewModel();
        FxShellRoot root = new FxShellRoot(viewModel);

        Scene scene = new Scene(root, 1480, 920);
        stage.setTitle("NGP Karaoke FX Shell");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
