package karaoke.app.main.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LyricImportService {

    public List<List<String>> readTextFile(File textFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8));
        try {
            return readLines(reader);
        } finally {
            reader.close();
        }
    }

    public List<List<String>> readText(Reader reader) throws IOException {
        BufferedReader buffered = new BufferedReader(reader);
        return readLines(buffered);
    }

    private List<List<String>> readLines(BufferedReader reader) throws IOException {
        List<List<String>> lines = new ArrayList<List<String>>();
        String line;
        while((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            List<String> words = new ArrayList<String>();
            for (int i = 0; i < tokens.length; i++) {
                if(tokens[i].trim().length() > 0) {
                    words.add(tokens[i]);
                }
            }
            lines.add(words);
        }
        return lines;
    }
}
