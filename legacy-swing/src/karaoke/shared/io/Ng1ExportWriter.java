package karaoke.shared.io;

import java.util.List;

import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class Ng1ExportWriter {

    public Ng1ExportResult serialize(List<wordLine> words) {
        StringBuilder export = new StringBuilder("song");

        for (int i = 0; i < words.size(); i++) {
            wordLine line = words.get(i);
            double current = line.getCurrent_sec_paint();
            double duration = line.getDurationSec();
            if(current <= 0 || duration <= 0) {
                return new Ng1ExportResult(false, export.toString(), line.getLine_idx(), line.getWord());
            }

            int adjustedDuration = line.getDurationSec();
            if(i < words.size() - 1) {
                wordLine afterLine = words.get(i + 1);
                int diff = afterLine.getCurrent_sec_paint() - line.getLow_sec_paint();
                if(diff < 0) {
                    adjustedDuration = adjustedDuration + diff;
                }
            }

            export.append(",").append(line.getLine_idx())
                .append(",").append(line.getCurrent_sec_paint())
                .append(",").append(adjustedDuration)
                .append(",").append(getExportWord(line));
        }

        return new Ng1ExportResult(true, export.toString(), -1, null);
    }

    private String getExportWord(wordLine line) {
        if(line.isSplit() && line.getType() != MyType.splite_sub_end) {
            return line.getWord() + "-";
        }
        return line.getWord();
    }
}
