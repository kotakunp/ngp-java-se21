package karaoke.shared.io;

public class Ng1ExportResult {

    private final boolean successful;
    private final String exportText;
    private final int invalidLineIndex;
    private final String invalidWord;

    public Ng1ExportResult(boolean successful, String exportText, int invalidLineIndex, String invalidWord) {
        this.successful = successful;
        this.exportText = exportText;
        this.invalidLineIndex = invalidLineIndex;
        this.invalidWord = invalidWord;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getExportText() {
        return exportText;
    }

    public int getInvalidLineIndex() {
        return invalidLineIndex;
    }

    public String getInvalidWord() {
        return invalidWord;
    }
}
