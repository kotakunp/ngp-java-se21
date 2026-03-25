package karaoke.ui.fx.audio;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public interface AudioPlaybackService {

    BooleanProperty loadedProperty();

    BooleanProperty loadingProperty();

    BooleanProperty playingProperty();

    LongProperty durationMicrosProperty();

    LongProperty durationTimelineUnitsProperty();

    LongProperty positionMicrosProperty();

    StringProperty statusProperty();

    void load(File audioFile);

    void togglePlayback();

    void stop();

    void dispose();
}
