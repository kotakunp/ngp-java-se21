package karaoke.ui.fx.view;

import karaoke.ui.fx.app.FxShellState;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TimelineCanvasPane extends StackPane {

    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 420;

    private final Canvas canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private final FxShellState state;

    public TimelineCanvasPane(FxShellState state) {
        this.state = state;
        getChildren().add(canvas);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: " + FxTheme.CANVAS_BACKGROUND + ";"
            + "-fx-border-color: " + FxTheme.BORDER + ";"
            + "-fx-border-width: 1;"
            + "-fx-background-radius: 12;"
            + "-fx-border-radius: 12;");

        canvas.widthProperty().bind(widthProperty().subtract(16));
        canvas.heightProperty().bind(heightProperty().subtract(16));

        ChangeListener<Object> redraw = (obs, oldVal, newVal) -> draw();
        canvas.widthProperty().addListener(redraw);
        canvas.heightProperty().addListener(redraw);
        state.timelineStatusProperty().addListener(redraw);
        state.paintModeProperty().addListener(redraw);
        state.playingProperty().addListener(redraw);
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = Math.max(1, canvas.getWidth());
        double height = Math.max(1, canvas.getHeight());

        gc.setFill(Color.web(FxTheme.CANVAS_BACKGROUND));
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.web(FxTheme.GRID));
        for (int row = 0; row < 24; row++) {
            double y = 18 + (row * ((height - 30) / 24));
            gc.strokeLine(0, y, width, y);
        }

        gc.setStroke(Color.web(FxTheme.GRID_STRONG));
        gc.setLineWidth(1.5);
        for (int i = 0; i < 9; i++) {
            double x = 80 + (i * ((width - 160) / 8));
            gc.strokeLine(x, 0, x, height);
        }

        gc.setStroke(Color.web(FxTheme.WAVEFORM));
        gc.setLineWidth(2);
        double baseX = width / 2;
        for (int i = 0; i < 150; i++) {
            double y = 12 + (i * ((height - 24) / 150));
            double waveWidth = 20 + (Math.sin(i * 0.22) * 18) + (Math.cos(i * 0.07) * 12);
            gc.strokeLine(baseX - waveWidth, y, baseX + waveWidth, y);
        }

        gc.setStroke(Color.web(FxTheme.CURSOR));
        gc.setLineWidth(2.5);
        double cursorX = state.playingProperty().get() ? width * 0.35 : width * 0.2;
        gc.strokeLine(cursorX, 0, cursorX, height);

        if (state.paintModeProperty().get()) {
            gc.setFill(Color.web("#2f6f54", 0.25));
            gc.fillRect(0, 0, width, height);
        }

        gc.setFill(Color.web(FxTheme.TEXT_PRIMARY));
        gc.setFont(Font.font("Arial", 13));
        gc.fillText(state.timelineStatusProperty().get(), 18, 28);
    }
}
