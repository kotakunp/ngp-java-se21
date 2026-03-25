package karaoke.shared.timing;

public class TimelineMathVerifier {

    public static void main(String[] args) {
        assertEquals(10000, TimelineMath.timelineUnitToClipFrame(10), "timeline->clip");
        assertEquals(10, TimelineMath.clipFrameToTimelineUnit(10000), "clip->timeline");
        assertEquals(453, TimelineMath.timelineUnitToExportPosition(10), "timeline->export position");
        assertEquals(680, TimelineMath.timelineDurationToExportDuration(15), "timeline duration->export duration");
        assertEquals(5, TimelineMath.moveTimelineUnitByExportOffset(10, 226), "seek conversion");
        assertEquals("2 : 5", TimelineMath.formatMinutesSecondsFromMicros(125000000L), "micros formatting");
        assertEquals("2 : 5", TimelineMath.formatMinutesSecondsFromMillis(125000L), "millis formatting");
        System.out.println("Timeline math verification passed.");
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if(expected == null ? actual != null : !expected.equals(actual)) {
            throw new IllegalStateException(label + " mismatch. expected=[" + expected + "] actual=[" + actual + "]");
        }
    }
}
