package karaoke.shared.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class NgpProjectReader {

    public NgpProjectData read(File projectFile) throws IOException {
        List<wordLine> words = new ArrayList<wordLine>();
        String songInfo = "";
        int paintIndex = 0;

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(projectFile), StandardCharsets.UTF_8)
        );
        try {
            String text;
            while((text = reader.readLine()) != null) {
                String[] records = text.split(";");
                for (String record : records) {
                    String[] value = record.split(",");
                    if(value.length < 10) {
                        if(value.length >= 7) {
                            songInfo = value[2] + "," + value[3] + "," + value[4] + "," + value[5] + "," + value[6];
                        }
                        continue;
                    }

                    wordLine word = new wordLine(value);
                    words.add(word);
                    if(Integer.parseInt(value[7]) == 1) {
                        paintIndex = Integer.parseInt(value[0]) + 1;
                    }
                }
            }
        } finally {
            reader.close();
        }

        normalizeSplitWords(words);
        return new NgpProjectData(songInfo, words, paintIndex);
    }

    private void normalizeSplitWords(List<wordLine> words) {
        for (int i = 0; i < words.size(); i++) {
            wordLine current = words.get(i);
            if(!current.isSplit()) {
                continue;
            }

            if(current.isThreeSplite()) {
                wordLine after = words.get(i + 1);
                wordLine next = words.get(i + 2);
                current.addWord(after.getWord(), next.getLow_sec(), after.getLocation());
                current.addWord(next.getWord(), next.getLow_sec(), next.getLocation());
                current.setType(MyType.normal);
                current.setSplit(false);
                current.getField().setText(current.getWord());
                words.remove(i + 1);
                words.remove(i + 1);
            } else {
                wordLine after = words.get(i + 1);
                current.addWord(after.getWord(), after.getLow_sec(), after.getLocation());
                current.setType(MyType.normal);
                current.setSplit(false);
                current.getField().setText(current.getWord());
                words.remove(i + 1);
            }
        }
    }
}
