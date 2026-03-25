package karaoke.ui.fx.view;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import karaoke.ui.fx.app.FxShellState;
import karaoke.ui.fx.app.FxShellViewModel;
import karaoke.ui.fx.timeline.TimelineWordMarker;

public class TimelineCanvasPane extends StackPane {

    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 420;

    private final Canvas canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private final FxShellState state;
    private final FxShellViewModel viewModel;

    public TimelineCanvasPane(FxShellState state, FxShellViewModel viewModel) {
        this.state = state;
        this.viewModel = viewModel;
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
        state.paintIndexProperty().addListener(redraw);
        state.selectedWordIndexProperty().addListener(redraw);
        state.durationMicrosProperty().addListener(redraw);
        state.positionMicrosProperty().addListener(redraw);
        state.getTimelineMarkers().addListener((javafx.collections.ListChangeListener<TimelineWordMarker>) change -> draw());
        canvas.setOnMouseClicked(event -> handleSelection(event.getX(), event.getY()));
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
        double cursorX = width * 0.2;
        long durationMicros = state.durationMicrosProperty().get();
        if (durationMicros > 0L) {
            double ratio = Math.min(1D, Math.max(0D, (double) state.positionMicrosProperty().get() / durationMicros));
            cursorX = 24 + ((width - 48) * ratio);
        } else if (state.playingProperty().get()) {
            cursorX = width * 0.35;
        }
        gc.strokeLine(cursorX, 0, cursorX, height);

        drawTimelineMarkers(gc, width, height);

        if (state.paintModeProperty().get()) {
            gc.setFill(Color.web("#2f6f54", 0.25));
            gc.fillRect(0, 0, width, height);
        }

        gc.setFill(Color.web(FxTheme.TEXT_PRIMARY));
        gc.setFont(Font.font("Arial", 13));
        gc.fillText(state.timelineStatusProperty().get(), 18, 28);
    }

    private void drawTimelineMarkers(GraphicsContext gc, double width, double height) {
        if (state.getTimelineMarkers().isEmpty()) {
            return;
        }

        int maxEnd = 1;
        int maxLine = 0;
        for (TimelineWordMarker marker : state.getTimelineMarkers()) {
            maxEnd = Math.max(maxEnd, marker.getEnd());
            maxLine = Math.max(maxLine, marker.getLineIndex());
        }

        double leftPadding = 24;
        double topPadding = 48;
        double timelineWidth = width - (leftPadding * 2);
        double laneHeight = Math.max(28, (height - topPadding - 18) / Math.max(1, maxLine + 1));

        for (TimelineWordMarker marker : state.getTimelineMarkers()) {
            double x = leftPadding + ((double) marker.getStart() / maxEnd) * timelineWidth;
            double endX = leftPadding + ((double) marker.getEnd() / maxEnd) * timelineWidth;
            double markerWidth = Math.max(18, endX - x);
            double y = topPadding + (marker.getLineIndex() * laneHeight);

            Color fill = marker.isPainted() ? Color.web("#3da46a", 0.8) : Color.web("#2e6ea8", 0.7);
            if (marker.getWordIndex() == state.paintIndexProperty().get()) {
                fill = Color.web("#ffb347", 0.9);
            } else if (marker.getWordIndex() == state.selectedWordIndexProperty().get()) {
                fill = Color.web("#5bc0eb", 0.9);
            }

            gc.setFill(fill);
            gc.fillRoundRect(x, y, markerWidth, Math.max(18, laneHeight - 10), 8, 8);

            gc.setFill(Color.web(FxTheme.TEXT_PRIMARY));
            gc.setFont(Font.font("Arial", 11));
            gc.fillText(marker.getText(), x + 6, y + Math.min(16, laneHeight / 2 + 6));
        }
    }

    private void handleSelection(double mouseX, double mouseY) {
        if (state.getTimelineMarkers().isEmpty()) {
            return;
        }

        int maxEnd = 1;
        int maxLine = 0;
        for (TimelineWordMarker marker : state.getTimelineMarkers()) {
            maxEnd = Math.max(maxEnd, marker.getEnd());
            maxLine = Math.max(maxLine, marker.getLineIndex());
        }

        double width = Math.max(1, canvas.getWidth());
        double height = Math.max(1, canvas.getHeight());
        double leftPadding = 24;
        double topPadding = 48;
        double timelineWidth = width - (leftPadding * 2);
        double laneHeight = Math.max(28, (height - topPadding - 18) / Math.max(1, maxLine + 1));

        for (TimelineWordMarker marker : state.getTimelineMarkers()) {
            double x = leftPadding + ((double) marker.getStart() / maxEnd) * timelineWidth;
            double endX = leftPadding + ((double) marker.getEnd() / maxEnd) * timelineWidth;
            double markerWidth = Math.max(18, endX - x);
            double y = topPadding + (marker.getLineIndex() * laneHeight);
            double markerHeight = Math.max(18, laneHeight - 10);
            if (mouseX >= x && mouseX <= x + markerWidth && mouseY >= y && mouseY <= y + markerHeight) {
                viewModel.selectWord(marker.getWordIndex());
                return;
            }
        }
    }
}
