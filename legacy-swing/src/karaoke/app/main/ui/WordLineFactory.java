package karaoke.app.main.ui;

import java.awt.Color;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;

import javax.swing.event.DocumentListener;

import karaoke.shared.myTextfield;
import karaoke.shared.wordLine;

public class WordLineFactory {

    private final DocumentListener documentListener;
    private final FocusListener focusListener;
    private final PopupHandler popupHandler;

    public interface PopupHandler {
        MouseAdapter create(wordLine word);
    }

    public WordLineFactory(DocumentListener documentListener, FocusListener focusListener, PopupHandler popupHandler) {
        this.documentListener = documentListener;
        this.focusListener = focusListener;
        this.popupHandler = popupHandler;
    }

    public wordLine createNew(String text, int wordIndex, int lineIndex) {
        wordLine word = new wordLine(text, new myTextfield(text, wordIndex, lineIndex, Color.lightGray), wordIndex);
        word.setLine_idx(lineIndex);
        bind(word);
        return word;
    }

    public void bindLoaded(wordLine word) {
        bind(word);
    }

    private void bind(wordLine word) {
        word.getField().getDocument().addDocumentListener(documentListener);
        word.getField().addFocusListener(focusListener);
        word.getField().addMouseListener(popupHandler.create(word));
    }
}
