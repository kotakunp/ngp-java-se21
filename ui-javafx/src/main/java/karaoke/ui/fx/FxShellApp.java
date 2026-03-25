package karaoke.ui.fx;

import karaoke.ui.fx.app.FxShellViewModel;
import karaoke.ui.fx.view.FxShellRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class FxShellApp extends Application {

    @Override
    public void start(Stage stage) {
        FxShellViewModel viewModel = new FxShellViewModel();
        viewModel.setOwnerWindow(stage);
        FxShellRoot root = new FxShellRoot(viewModel);

        Scene scene = new Scene(root, 1480, 920);
        installKeyboardShortcuts(scene, viewModel);
        stage.setTitle("NGP Karaoke FX Shell");
        stage.setScene(scene);
        stage.show();
    }

    private void installKeyboardShortcuts(Scene scene, FxShellViewModel viewModel) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isAltDown() || event.isControlDown() || event.isMetaDown()) {
                return;
            }
            switch (event.getCode()) {
            case SPACE:
                viewModel.togglePlayback();
                event.consume();
                break;
            case F:
                viewModel.togglePaintMode();
                event.consume();
                break;
            case R:
                viewModel.markCurrentWord();
                event.consume();
                break;
            case E:
                viewModel.markLineEnd();
                event.consume();
                break;
            case T:
            case BACK_SPACE:
                viewModel.undoLastPaint();
                event.consume();
                break;
            case LEFT:
                viewModel.selectPreviousWord();
                event.consume();
                break;
            case RIGHT:
                viewModel.selectNextWord();
                event.consume();
                break;
            case UP:
                viewModel.nudgeSelectedWord(true);
                event.consume();
                break;
            case DOWN:
                viewModel.nudgeSelectedWord(false);
                event.consume();
                break;
            case SHIFT:
                viewModel.setEndLineAdjustMode(true);
                event.consume();
                break;
            default:
                break;
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                viewModel.setEndLineAdjustMode(false);
                event.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
