package karaoke.shared.timing;

public final class TimelineMath {

    public static final int AUDIO_FRAMES_PER_TIMELINE_UNIT = 1000;
    public static final double EXPORT_UNITS_PER_TIMELINE_UNIT = 45.3514D;

    private TimelineMath() {
    }

    public static int timelineUnitToClipFrame(int timelineUnit) {
        return timelineUnit * AUDIO_FRAMES_PER_TIMELINE_UNIT;
    }

    public static int clipFrameToTimelineUnit(int clipFramePosition) {
        return clipFramePosition / AUDIO_FRAMES_PER_TIMELINE_UNIT;
    }

    public static int timelineUnitToExportPosition(int timelineUnit) {
        return (int)(timelineUnit * EXPORT_UNITS_PER_TIMELINE_UNIT);
    }

    public static long timelineUnitToExportPositionLong(int timelineUnit) {
        return (long)(timelineUnit * EXPORT_UNITS_PER_TIMELINE_UNIT);
    }

    public static int timelineDurationToExportDuration(int timelineDuration) {
        return (int)(timelineDuration * EXPORT_UNITS_PER_TIMELINE_UNIT);
    }

    public static int moveTimelineUnitByExportOffset(int timelineUnit, int exportOffset) {
        return (int)(timelineUnit - (exportOffset / EXPORT_UNITS_PER_TIMELINE_UNIT));
    }

    public static String formatMinutesSecondsFromMicros(long microseconds) {
        long totalSeconds = microseconds / 1000000L;
        return new StringBuilder(String.valueOf(totalSeconds / 60L))
            .append(" : ")
            .append(totalSeconds % 60L)
            .toString();
    }

    public static String formatMinutesSecondsFromMillis(long milliseconds) {
        long totalSeconds = milliseconds / 1000L;
        return new StringBuilder(String.valueOf(totalSeconds / 60L))
            .append(" : ")
            .append(totalSeconds % 60L)
            .toString();
    }
}
