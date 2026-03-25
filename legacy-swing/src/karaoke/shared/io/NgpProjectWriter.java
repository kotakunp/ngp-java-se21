package karaoke.shared.io;

import java.util.List;

import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class NgpProjectWriter {

    public NgpSerializedProject serialize(List<wordLine> words, int duration, String songInfo) {
        StringBuilder project = new StringBuilder();
        StringBuilder lyric = new StringBuilder();

        project.append("0,").append(duration).append(",").append(songInfo).append(";");

        int lineIdx = 0;
        boolean startOfLine = true;
        for (int i = 0; i < words.size(); i++) {
            wordLine word = words.get(i);
            if(i > 0) {
                project.append(";");
            }
            project.append(serializeWord(word));

            String fieldText = word.getText();
            if(i == 0) {
                if(word.getType() == MyType.splite_main) {
                    lyric.append(fieldText.replaceFirst("-", ""));
                } else {
                    lyric.append(fieldText);
                }
                lineIdx = word.getLine_idx();
                startOfLine = false;
                continue;
            }

            if(lineIdx < word.getLine_idx()) {
                lineIdx = word.getLine_idx();
                lyric.append("\n");
                startOfLine = true;
            }

            if(word.isSplit()) {
                switch (word.getType()) {
                case splite_main:
                    appendLyricToken(lyric, fieldText.replaceFirst("-", ""), startOfLine);
                    startOfLine = false;
                    break;
                case splite_sub:
                    lyric.append(fieldText.replaceFirst("-", ""));
                    break;
                case splite_sub_end:
                    lyric.append(word.getText());
                    break;
                default:
                    break;
                }
            } else {
                appendLyricToken(lyric, word.getText(), startOfLine);
                startOfLine = false;
            }
        }

        return new NgpSerializedProject(project.toString(), lyric.toString());
    }

    private String serializeWord(wordLine word) {
        return new StringBuilder(String.valueOf(word.getIdx()))
            .append(",").append(word.getLine_idx())
            .append(",").append(word.getHigh_sec())
            .append(",").append(word.getCurrent_sec())
            .append(",").append(word.getLow_sec())
            .append(",").append(word.getWord())
            .append(",").append(word.getSplit())
            .append(",").append(word.getPaint())
            .append(",").append(word.getTypeInt())
            .append(",").append(word.getLocationInt())
            .append(",").append(word.getThreeSplite())
            .toString();
    }

    private void appendLyricToken(StringBuilder lyric, String token, boolean startOfLine) {
        if(!startOfLine) {
            lyric.append(" ");
        }
        lyric.append(token);
    }
}
