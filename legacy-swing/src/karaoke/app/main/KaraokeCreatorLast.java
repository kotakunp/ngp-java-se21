package karaoke.app.main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import karaoke.app.main.service.LyricImportService;
import karaoke.app.main.service.PaintWorkflowService;
import karaoke.app.main.service.ProjectSession;
import karaoke.app.main.service.ProjectSessionService;
import karaoke.app.main.service.ProjectSaveService;
import karaoke.app.main.service.SelectionNavigationService;
import karaoke.app.main.ui.WordLineFactory;
import karaoke.shared.Location;
import karaoke.shared.MyComfirmDialog;
import karaoke.shared.MyType;
import karaoke.shared.myTextfield;
import karaoke.shared.wordLine;
import karaoke.shared.edit.RowEditService;
import karaoke.shared.edit.WordTextEditService;
import karaoke.shared.io.NgpProjectData;
import karaoke.shared.io.Ng1ExportResult;
import karaoke.shared.io.Ng1ExportWriter;
import karaoke.shared.io.NgpProjectReader;
import karaoke.shared.io.NgpProjectWriter;
import karaoke.shared.io.NgpSerializedProject;
import karaoke.shared.repair.ProjectRepairService;
import karaoke.app.main.source.WavPlayer;

public class KaraokeCreatorLast extends KaraokeCreator implements ActionListener, KeyEventDispatcher, DocumentListener, FocusListener {
	/*
	 *  text audio oruulj ireh
	 */	
	private final ProjectSession session = new ProjectSession();
	private ArrayList<wordLine> words;
	private Vector<Integer> lines;
	
	private boolean isPaint = false,  end_line = false; //rePaint = false, ene shuu 
	private myTextfield field;
    private final NgpProjectReader projectReader = new NgpProjectReader();
    private final NgpProjectWriter projectWriter = new NgpProjectWriter();
    private final Ng1ExportWriter exportWriter = new Ng1ExportWriter();
    private final RowEditService rowEditService = new RowEditService();
    private final WordTextEditService wordTextEditService = new WordTextEditService();
    private final ProjectRepairService projectRepairService = new ProjectRepairService();
    private final LyricImportService lyricImportService = new LyricImportService();
    private final PaintWorkflowService paintWorkflowService = new PaintWorkflowService();
    private final ProjectSessionService projectSessionService = new ProjectSessionService();
    private final ProjectSaveService projectSaveService = new ProjectSaveService(projectWriter, exportWriter);
    private final SelectionNavigationService selectionNavigationService = new SelectionNavigationService();
    private final WordLineFactory wordLineFactory = new WordLineFactory(this, this, new WordLineFactory.PopupHandler() {
        public MouseAdapter create(final wordLine word) {
            return new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON3) {
                        popupMenu.show(word.getField(), e.getX(), e.getY());
                    }
                }
            };
        }
    });
	
	public KaraokeCreatorLast() {
		importAudio.addActionListener(this); 
		importText.addActionListener(this);
		textAdd.addActionListener(this);
		paintStart.addActionListener(this);
		audioPlay.addActionListener(this);
		syncPlay.addActionListener(this);
		savePro.addActionListener(this);
		openPro.addActionListener(this);
		secEdit.addActionListener(this);
		closePro.addActionListener(this);
		
		playVideo.addActionListener(this);
		playlist.addActionListener(this);
		
		editTextItem.addActionListener(this); 
		editTextEnd.addActionListener(this);
		popPaintR.addActionListener(this); 
		cutText.addActionListener(this); 
		cutTextBack.addActionListener(this); 
		mergeText.addActionListener(this); 
		spliteText.addActionListener(this); 
		mItemRows.addActionListener(this); 
		mItemRow.addActionListener(this); 
		
		
		mItemNew.addActionListener(this); 
		mItemOpen.addActionListener(this); 
		mItemClose.addActionListener(this); 
		mItemSavePro.addActionListener(this); 
		mItemExport.addActionListener(this); 
		
		mItemAudio.addActionListener(this); 
		mItemText.addActionListener(this); 
		mItemPaint.addActionListener(this); 
		mItemOPaintF.addActionListener(this); 
		mItemPaintR.addActionListener(this); 
		
		//SEEK SETTINGS
		moveSeek.addActionListener(this);
		oneWord.addActionListener(this);
		orderLine.addActionListener(this);

		words = session.getWords();
		lines = session.getLines();
//		saveTextOut = new StringBuffer();
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
//		System.out.println(action);
		switch (action) {
		/* TOOLBAR */
		case "importAudio":
			mItemAudio();
			break;
		case "importText":
			importText(); 
			break;
		case "textAdd":
			textAdd();
			break;
		case "importText2":
			importText2(); 
			break;
		case "paintStart":
			paintStart();
			break;
		case "syncPlay":
			syncPlay();
			break;
		case "audioPlay":
			Boolean play =  player.play();
			if(play) {
				audioPlay.setIcon(new ImageIcon("image/audioPause.png"));
			}else {
				audioPlay.setIcon(new ImageIcon("image/audioPlay.png"));
			}
			break;
		case "cutText":
			cutText();
			break;
		case "cutTextBack":
			cutTextBack();
			break;
		case "editText": // Zasvar ehleh
			editText();
			break;
		case "editTextEnd": //Zasvar duusgah
			editTextEnd();
			break;
		case "mergeText":
			mergeText();
			break;
		case "spliteText":
			spliteText();
			break;
			
		case "savePro": // Project hadgalah
			if(session.isSave())
				saveAsPro();
			else
				savePro();
			break;
		case "openPro":
			openPro();
			break;
		
		case "closePro":
			closePro();
			break;
			/* MENUBAR */
		case "mItemNew":
			mItemNew();
			break;
		case "mItemOpen":
			openPro();
			break;
		case "mItemClose":
			mItemClose();
			break;
		case "mItemSave":
			mItemSave();
			break;
		case "mItemSavePro":
			mItemSavePro();
			break;
		case "mItemExport":
			mItemExport();
			break;
		case "mItemAudio":
			mItemAudio();
			break;
		case "mItemText":
			importText();
			break;
		case "mItemPaint":
			paintStart();
			break;
		case "mItemPaintR":
			rePaint();
			break;
		case "popPaintR":
			rePaint();
			break;
		case "mItemRow":
			mItemRow();
			break;
		case "mItemRows":
			mItemRows();
			break;
		case "moveSeek":
			moveSeek();
			break;
		case "secEdit":
			secEdit();
			break;
		case "oneWord":
			oneWord();
			break;
		case "orderLine":
			orderLine();
			break;
		default:
			System.out.println("select us idx - "+action);
			break;
		}
	}

	private void orderLine() {
//		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt");
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File[] files = fileChooser.getSelectedFiles();
            for(File file: files) {
            	System.out.println("---------------------------------------------------");
            	projectSessionService.appendMessage(session, "------------------------------------------\n");
            	System.out.println(file.getAbsolutePath()+"   "+ file.getName());
            	projectSessionService.appendMessage(session, file.getAbsolutePath()+"   "+ file.getName()+"\n");
            	projectSessionService.configureBatchProject(session, file);
            	words.clear();
            	readData(session.getProjectPath());
            }
            JOptionPane.showMessageDialog(this, rb.getString("save.success"));
        } else {
            System.out.println("Open command cancelled by user.");
        }
	}


	private void readData(String proDir) {
        try{
            NgpProjectData projectData = projectReader.read(new File(proDir));
            words.clear();
            words.addAll(projectData.getWords());
            session.setSongInfo(projectData.getSongInfo());
			if(projectRepairService.compactLineIndexes(words)) {
				try{
		            NgpSerializedProject project = projectWriter.serialize(words, player.getDurition(), session.getSongInfo());
		            File pro = new File(proDir);
		            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pro), StandardCharsets.UTF_8));
		            writer.write(project.getProjectText());
		            writer.close();
		            System.out.println("success");

		        	projectSessionService.appendMessage(session, "Прожект хадгаллаа.\n");
		        }catch(IOException e){
		            e.printStackTrace();
		            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.error"), 1);
		        	projectSessionService.appendMessage(session, " Прожект хадгалахад алдаа гарсан\n");
		        }

				File file = new File(session.getExportPath());
	            try{
	            	if (writeNg1Export(file)) {
	                    projectSessionService.appendMessage(session, "NG1 хадгаллаа.\n");
					} else {
			        	projectSessionService.appendMessage(session, " NG1 хадгалахад алдаа гарсан\n");
					}
	            }catch(IOException e){
	                e.printStackTrace();
		        	projectSessionService.appendMessage(session, " NG1 хадгалахад алдаа гарсан\n");
	            }
			}
			try{
 	            File pro = new File("D:\\LOG mur daraalal.txt");
 	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pro), StandardCharsets.UTF_8));
 	            writer.write(session.getMessage());
 	            writer.close();
 	            System.out.println("success");
 	        }catch(IOException e){
 	            e.printStackTrace();
 	            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.error"), 1);
 	        }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void oneWord() {
		String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt");
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Folder", new String[] {
//            ""
//        }));
//        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = fileChooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File[] files = fileChooser.getSelectedFiles();
            for(File file: files) {
            	System.out.println("---------------------------------------------------");
            	projectSessionService.appendMessage(session, "------------------------------------------\n");
            	System.out.println(file.getAbsolutePath()+"   "+ file.getName());
            	projectSessionService.appendMessage(session, file.getAbsolutePath()+"   "+ file.getName()+"\n");
            	projectSessionService.configureBatchProject(session, file);
            	words.clear();
            	readPro(session.getProjectPath());
            }
            JOptionPane.showMessageDialog(this, rb.getString("save.success"));
        } else {
            System.out.println("Open command cancelled by user.");
        }
        
//       
	}
	
	private void readPro(String textDir) {
        try{
            NgpProjectData projectData = projectReader.read(new File(textDir));
            words.clear();
            words.addAll(projectData.getWords());
            session.setSongInfo(projectData.getSongInfo());

			int lastLine = 0;
			for (int i = 0; i < words.size(); i++) {
				lastLine = Math.max(lastLine, words.get(i).getLine_idx());
			}
			System.out.println(lastLine+",  size :"+ words.size());
        	projectSessionService.appendMessage(session, "  Нийт мөрийн тоо :"+lastLine+",  Нийт үгийн тоо :"+ words.size()+"\n");

			int repairedCount = projectRepairService.splitSingleWordLines(words, new ProjectRepairService.WordFactory() {
				public wordLine create(String text, int wordIndex, int lineIndex) {
					return addWord(text, wordIndex, lineIndex);
				}
			});
			if(repairedCount > 0) {
				try{
		            NgpSerializedProject project = projectWriter.serialize(words, player.getDurition(), session.getSongInfo());
		            File pro = new File(session.getProjectPath());
		            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pro), StandardCharsets.UTF_8));
		            writer.write(project.getProjectText());
		            writer.close();
		            System.out.println("success");

		        	projectSessionService.appendMessage(session, "Прожект хадгаллаа.\n");
		        }catch(IOException e){
		            e.printStackTrace();
		            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.error"), 1);
		        	projectSessionService.appendMessage(session, " Прожект хадгалахад алдаа гарсан\n");
		        }

				File file = new File(session.getExportPath());
	            try{
	            	if (writeNg1Export(file)) {
	                    projectSessionService.appendMessage(session, "NG1 хадгаллаа.\n");
					} else {
			        	projectSessionService.appendMessage(session, " NG1 хадгалахад алдаа гарсан\n");
					}
	            }catch(IOException e){
	                e.printStackTrace();
		        	projectSessionService.appendMessage(session, " NG1 хадгалахад алдаа гарсан\n");
	            }
			}
			try{
 	            File pro = new File("D:\\LOG.txt");
 	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pro), StandardCharsets.UTF_8));
 	            writer.write(session.getMessage());
 	            writer.close();
 	            System.out.println("success");
 	        }catch(IOException e){
 	            e.printStackTrace();
 	            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.error"), 1);
 	        }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void moveSeek() {
		int seek = 0;
		String seekVal = seekValue.getText();
		String value = seekGroup.getSelection().getActionCommand();
		try {
			seek = Integer.parseInt(seekVal);
		} catch (NumberFormatException e) {
		}
		
		if(seek > 0) {
			if(value.equals("hoishSeek")) {
				seek = -seek;
			}
//	        player.removeWords();
			for (int i = 0; i < words.size(); i++) {
	    		wordLine word = words.get(i);
	    		word.moveSeek(seek);
			}
			
//			JOptionPane.showMessageDialog(seekdialog, rb.getString("seekError")); 
			player.setWords(words);
			seekdialog.setVisible(false);
		}else {
			JOptionPane.showMessageDialog(seekdialog, rb.getString("seekError")); 
		}
		
	}


	private void secEdit() {
		if(words.size() > 0)
			seekdialog.setVisible(true);
	}


	private void closePro() {
        if(session.isSave()){
            MyComfirmDialog dialog = new MyComfirmDialog(this, rb.getString("project.open"), rb.getString("project.open"), 0);
            int input = dialog.getInput();
            if(input == 0){
                saveAsPro();
                clearWindow();
                session.setSave(false);
            } else{
            	clearWindow();
                session.setSave(false);
            }
        	session.setParentDir("");
        }else {
        	if(session.getParentDir().length() > 0) {
        		clearWindow();
            	session.setParentDir("");
        	}
        	
        }
	}


	private void mItemRows() {
		lines.add(lines.size());
		rowEditService.splitRowFromWordIndex(words, session.getSelectedWordIndex());
		reload();
	}
	
	private void mItemRow() {
		lines.remove(lines.size()-1);
		rowEditService.mergeRowFromWordIndex(words, session.getSelectedWordIndex());
		reload();
	}


    private void importText2()
    {
        songText.removeAll();
        int ug_idx = 0;
        try{
            List<List<String>> importedLines = lyricImportService.readText(new StringReader(insertText.getText()));
            for (int line_idx = 0; line_idx < importedLines.size(); line_idx++) {
                List<String> importedWords = importedLines.get(line_idx);
                JPanel panel = new JPanel(new FlowLayout(3, 5, 0));
                panel.setBorder(BorderFactory.createEmptyBorder());
                panel.setBackground(Color.black);
                for (int i = 0; i < importedWords.size(); i++) {
                    String ug = importedWords.get(i);
                    wordLine word = addWord(ug, ug_idx, line_idx);
                    panel.add(word.getField());
                    words.add(word);
                    ug_idx++;
                }

                songText.add(panel);
                lines.add(Integer.valueOf(line_idx));
            }
            editText = false;
            insertTextdialog.setVisible(false);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        songText.revalidate();
        songText.repaint();
        player.setWords(words);
    }
    
    private void spliteText(){
        wordLine undsen = words.get(session.getSelectedWordIndex());
        player.stop();
        int caretPosition = undsen.getField().getCaretPosition();
        if(caretPosition > 0 && caretPosition < undsen.getWord().length()){
            int shine_idx = session.getSelectedWordIndex() + 1;
            wordLine shine = addWord("", shine_idx, undsen.getLine_idx());
            if(wordTextEditService.splitWord(words, session.getSelectedWordIndex(), caretPosition, shine)) {
            undsen.setEdit(false);
            session.setPaintIndex(session.getPaintIndex() + 1);
            reload();
            player.repaint();
            }
        }
    }

    private void mergeText(){
        if(wordTextEditService.mergeWordWithPrevious(words, session.getSelectedWordIndex())) {
            reload();
            session.setPaintIndex(session.getPaintIndex() - 1);
            player.repaint();
        }
    }

    private void editTextEnd(){
        if(words.size() > 0 && editText){
            editText = false;
            wordLine undsen = words.get(session.getSelectedWordIndex());
            undsen.setEdit(false);
            undsen.setWord();
            reload();
        }
    }

    private void editText(){
        if(words.size() > 0)
        {
            editText = true;
            wordLine undsen = words.get(session.getSelectedWordIndex());
            undsen.setEdit(true);
            player.stop();
        }
    }

    private void cutTextBack()
    {
        if(words.size() > 0)
        {
            if(wordTextEditService.undoCutPaintedWord(words, session.getSelectedWordIndex())) {
                reload();
                session.setPaintIndex(session.getPaintIndex() - 1);
                player.repaint();
            }
        }
    }

    private void cutText(){
        wordLine undsen = words.get(session.getSelectedWordIndex());
        if(wordTextEditService.canCutPaintedWord(undsen, isPaint)){
            player.stop();
            int caretPosition = undsen.getField().getCaretPosition();
            int shine_idx = session.getSelectedWordIndex() + 1;
            wordLine shine = addWord("", shine_idx, undsen.getLine_idx());
            if(wordTextEditService.cutPaintedWord(words, session.getSelectedWordIndex(), caretPosition, shine)) {
                undsen.setEdit(false);
                session.setPaintIndex(session.getPaintIndex() + 1);
                editText = false;
                reload();
                player.repaint();
            }
        } else
        {
            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("main.barilt.info"), 1);
        }
    }

    private void mItemAudio()
    {
        if(session.isSave()){
            MyComfirmDialog dialog = new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("importAudio.open"), 0);
            int input = dialog.getInput();
            if(input == 0){
                importAudio();
            }
        } else{
            importAudio();
        }
    }

    private void mItemExport(){
    }

    private void mItemSavePro(){
    	
    }

    private void mItemSave(){
    }

    private void mItemClose(){
        boolean close = false;
        MyComfirmDialog dialog = new MyComfirmDialog(this, rb.getString("project.open"), rb.getString("project.open"), 2);
        int input = dialog.getInput();
        if(input == 0){
            if(session.isSave()){
                saveAsPro();
                close = true;
                session.setSave(false);
            } else{
                savePro();
                session.setSave(false);
            }
        } else if(input == 1){
            close = true;
        }
        if(close){
            clearWindow();
        }
    }

    private void importAudio(){	
    	String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt");
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(0);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Audio", new String[] {
            "wav"
        }));
        fileChooser.setAcceptAllFileFilterUsed(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if(returnVal == 0){
            File file = fileChooser.getSelectedFile();
            projectSessionService.configureImportedAudio(session, file);
            file_name.setText(file.getName());
            direction.setText(session.getAudioDir());
            openAudio(session.getAudioDir());
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void openAudio(String audioDir){
        session.setAudioDir(audioDir);
        state.setText("Loading audio...");
        duration.setText("");
        player.setAudioLoading(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<WavPlayer.AudioLoadResult, Void> worker = new SwingWorker<WavPlayer.AudioLoadResult, Void>() {
            @Override
            protected WavPlayer.AudioLoadResult doInBackground() throws Exception {
                return player.loadAudio(audioDir, size);
            }

            @Override
            protected void done() {
                try {
                    WavPlayer.AudioLoadResult result = get();
                    player.applyAudioLoad(result);
                    duration.setText(result.getDurationText());
                    state.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    state.setText("");
                    new MyComfirmDialog(KaraokeCreatorLast.this, rb.getString("message.header"), "Audio file could not be loaded.", 1);
                } finally {
                    player.setAudioLoading(false);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void importText(){
    	String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt");
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(0);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text", new String[] {
            "txt"
        }));
        fileChooser.setAcceptAllFileFilterUsed(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if(returnVal == 0){
            words.clear();
            songText.removeAll();
            File file = fileChooser.getSelectedFile();
            projectSessionService.configureImportedText(session, file);
            openText(session.getTextDir());
        } else
        {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void openText(String textDir){
        session.setTextDir(textDir);
        try{
            int ug_idx = 0;
            List<List<String>> importedLines = lyricImportService.readTextFile(new File(textDir));
            for (int line_idx = 0; line_idx < importedLines.size(); line_idx++) {
                List<String> importedWords = importedLines.get(line_idx);
                JPanel panel = new JPanel(new FlowLayout(3, 5, 0));
                panel.setBorder(BorderFactory.createEmptyBorder());
                panel.setBackground(Color.black);
                for (int i = 0; i < importedWords.size(); i++) {
                    String ug = importedWords.get(i);
                    wordLine word = addWord(ug, ug_idx, line_idx);
                    panel.add(word.getField());
                    words.add(word);
                    ug_idx++;
                }

                songText.add(panel);
                lines.add(Integer.valueOf(line_idx));
            }
            songText.revalidate();
            songText.repaint();
            player.setWords(words);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void openProject(String textDir){
        try{
            NgpProjectData projectData = projectReader.read(new File(textDir));
            words.clear();
            lines.clear();
            session.setSongInfo(projectData.getSongInfo());
            session.setPaintIndex(projectData.getPaintIndex());
            words.addAll(projectData.getWords());

            int line_idx = 0;
			Vector<Integer> line = new Vector<>();
			JPanel panel = null;
			System.out.println("words.size()  "+words.size());
			for (int i = 0; i < words.size(); i++) {
				wordLine word = words.get(i);
                prepareLoadedWord(word);
				line_idx = word.getLine_idx();
				System.out.println(i+"  "+word.getWord()+"  "+line.contains(line_idx));
				if(!line.contains(line_idx)) {
					this.lines.add(line_idx);
					line.add(line_idx);
					if(i > 0)
						songText.add(panel);
					panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		            panel.setBorder(BorderFactory.createEmptyBorder());
		            panel.setBackground(Color.black);
		            panel.add(word.getField()); 
				}else {
					panel.add(word.getField()); 
				}
			}
			panel.add(new myTextfield(true)); 
			panel.setBackground(Color.black);
			songText.add(panel);
			songText.revalidate();
			songText.repaint();
			player.setWords(words); 
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void prepareLoadedWord(final wordLine word) {
        wordLineFactory.bindLoaded(word);
    }

    private wordLine addWord(String ug, int ug_idx, int line_idx){
        return wordLineFactory.createNew(ug, ug_idx, line_idx);
    }

    private void paintStart(){
    	if(isPaint){
	        paintWorkflowService.stopPainting(session, words, player);
	        state.setText("");
            isPaint = false;
        }else {
        	PaintWorkflowService.PaintStartResult result = paintWorkflowService.startPainting(session, words, player);
        	if(result == PaintWorkflowService.PaintStartResult.STARTED) {
                isPaint = true;
                state.setText(rb.getString("record"));
            } else if(result == PaintWorkflowService.PaintStartResult.AUDIO_LOADING) {
                new MyComfirmDialog(this, rb.getString("message.header"), "Audio is still loading. Please wait.", 1);
                return;
            } else if(result == PaintWorkflowService.PaintStartResult.PLAYBACK_FAILED) {
                state.setText("");
                new MyComfirmDialog(this, rb.getString("message.header"), "Audio playback could not be started.", 1);
                return;
            } else {
                new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("paintStart.error1"), 1);
            }
        }
        
        if(isPaint) {
        	paintStart.setIcon(new ImageIcon("image/paintF.png"));
		}else {
			paintStart.setIcon(new ImageIcon("image/paint.png"));
		}
    }

    private void rePaint(){
        paintWorkflowService.repaintFromSelection(session, words);
        reload();
    }

    
    private void syncPlay(){
//      JFileChooser fileChooser = new JFileChooser(audioDir);
  	Boolean save = true;
  	String msg = "";
      if(session.getParentDir().length() > 0){
          File file = new File(session.getParentDir()+"/"+session.getNameFile()+"cr.txt"); 
          String str = "song";
          int temp = 0, sum = 0;
          for(int i = 0; i < words.size(); i++){
        	  wordLine word = words.get(i);
        	  int id = word.getLine_idx();
        	  if (temp == id) {
                  sum += word.getWord().length();
                  if(sum > 23) {
                	  int last = 0;
                	  if(i+1 < words.size()) {
                		  last = words.get(i+1).getLine_idx();
                	  }
                	  if(temp == last) {
                		  refresh(i);
                	  }else {
                		  refresh(i-1);
                	  }
    			  }
              }
        	  if( id > temp) {
        		  sum = word.getWord().length();
        		  temp = word.getLine_idx();
        	  }
          }
          
          try{
        	  if (writeNg1Export(file)) {
                  JOptionPane.showMessageDialog(this, rb.getString("save.success"));
              }
          }catch(IOException e){
              e.printStackTrace();
          }
      }
  }
    

    private void refresh(int j) {
    	for(int i = j; i < words.size(); i++){
    		words.get(i).setAddLine();
        }
		
	}


    private void syncPlay1(){
    	Boolean save = true;
    	String msg = "";
        if(session.getParentDir().length() > 0){
            File file = new File(session.getParentDir()+"/"+session.getNameFile()+".nc1"); 
            try{
            	if (writeNg1Export(file)) {
                    JOptionPane.showMessageDialog(this, rb.getString("save.success"));
				}
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void syncPlay2()
    {
        JFileChooser fileChooser = new JFileChooser("D:\\budalt");
        if(fileChooser.showSaveDialog(this) == 0)
        {
            File file = fileChooser.getSelectedFile();
            try
            {
                writeNg1Export(new File((new StringBuilder()).append(file.getAbsoluteFile()).append(".ng1").toString()));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean writeNg1Export(File file) throws IOException {
        Ng1ExportResult export = projectSaveService.buildNg1Export(words);
        if(!export.isSuccessful()) {
            String msg = (export.getInvalidLineIndex() + 1) + " " + rb.getString("murnii") + " \"" + export.getInvalidWord() + "\" ";
            JOptionPane.showMessageDialog(this, msg + rb.getString("save.unsuccess"));
            return false;
        }

        projectSaveService.write(file, export.getExportText());
        return true;
    }

    private void savePro(){
        if(words.size() > 0 && videoPanel.isShow()){
//        	myTextfield frame = new myTextfield();
//			frame.setText("25");
//			myTextfield songname = new myTextfield();
//			myTextfield singer = new myTextfield();
//			myTextfield lyrics = new myTextfield();
//			myTextfield comp = new myTextfield();
//			myTextfield pro = new myTextfield();
//			Object[] message = {
//				rb.getString("frame"), frame,
//			    rb.getString("songname"), songname,
//			    rb.getString("singer"), singer,
//			    rb.getString("lyrics"), lyrics,
//			    rb.getString("comp"), comp,
//			    rb.getString("pro"), pro
//			};
//
//			int option = JOptionPane.showConfirmDialog(null, message, rb.getString("song_info"), JOptionPane.OK_CANCEL_OPTION);
//			if (option == JOptionPane.OK_OPTION) {
//			    if (songname.getText().trim().length() > 0 && singer.getText().trim().length() > 0 && frame.getText().trim().length() > 0) {
			    	String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt");
			    	JFileChooser fileChooser = new JFileChooser(dir);
		            fileChooser.setDialogTitle(rb.getString("project.save"));
		            fileChooser.setSelectedFile(new File(session.getNameFile()+".ngp"));
		            int userSelection = fileChooser.showSaveDialog(this);
		            if(userSelection == JFileChooser.APPROVE_OPTION){
//						song_info = songname.getTexts()+","+singer.getTexts()+","+lyrics.getTexts()+","+comp.getTexts()+","+pro.getTexts()+","+frame.getTexts();
		            	session.setSongInfo("0,0,0,0,0,25");
		                File file = fileChooser.getSelectedFile();
		                projectSessionService.configureSaveTarget(session, file);
		                saveAsPro();
		                session.setSave(true);
		                direction.setText(session.getProjectPath());
		            }
//			    }
//			}
        }
    }

    private void saveAsPro(){
        try{
            projectSaveService.saveProject(new File(session.getProjectPath()), new File(session.getLyricPath()), words, player.getDurition(), session.getSongInfo());
            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.success"), 1);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            new MyComfirmDialog(this, rb.getString("message.header"), rb.getString("project.save.error"), 1);
        }
    }
    
    private void mItemNew() {
		closePro();
	}

    private void openPro(){
        boolean open = false;
        if(session.isSave()){
            MyComfirmDialog dialog = new MyComfirmDialog(this, rb.getString("project.open"), rb.getString("project.open"), 0);
            int input = dialog.getInput();
            if(input == 0){
                saveAsPro();
                open = true;
                clearWindow();
            }else {
            	open = true;
//                clearWindow();
            }
        } else{
            open = true;
        }
        if(open){
        	String dir = projectSessionService.resolveBrowseDirectory(session, "D:\\budalt"); //Users/ganchimegkhurelbaatar/Good_soft/Nexg Galaxy/Test
        	JFileChooser fileChooser = new JFileChooser(dir);
            fileChooser.setDialogTitle(rb.getString("openPro"));
            fileChooser.setFileSelectionMode(0);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Karaoke project", new String[] {
                    "ngp"
                }));
            fileChooser.setAcceptAllFileFilterUsed(true);
            int returnVal = fileChooser.showOpenDialog(this);
            if(returnVal == 0) {
                File file = fileChooser.getSelectedFile();
                String saveAudioDir = projectSessionService.buildVocAudioPath(file);
                projectSessionService.configureOpenedProject(session, file);
                openAudio(saveAudioDir);
                openProject(file.getAbsolutePath());
                direction.setText(file.getAbsolutePath());
                file_name.setText(file.getName());
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
    }

    
    @Override
    
    public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			int key = e.getKeyCode();
			System.out.println(key); 
			if(editText) {
				System.out.println(words.get(session.getSelectedWordIndex()).getField().isEditable()); 
			}else {
				switch (key) {
				case KeyEvent.VK_R:  // budah 
					if(isPaint){
                        paintWorkflowService.markCurrentWord(session, player);
                        break;
                    }
					break;
				case KeyEvent.VK_F: // budalt ehleh, zogsookh 70
					paintStart();
					break;
				case KeyEvent.VK_E: // ctrl ---------------- taslah
					player.addEndLine(isPaint, (session.getPaintIndex()-1)); 
					break;
				case KeyEvent.VK_T: // budalt arilgah - neg butsah
					if(isPaint){
                        paintWorkflowService.undoLastPaint(session, words, player);
                    }
					break;
				case KeyEvent.VK_SPACE:
					setFocus();
					Boolean play =  player.play();
					if(play) {
						audioPlay.setIcon(new ImageIcon("image/audioPause.png"));
					}else {
						audioPlay.setIcon(new ImageIcon("image/audioPlay.png"));
					}
					break;
				case 87: //w == 87
					if(isPaint){
                        paintWorkflowService.resetPaintCursor(session, player);
                    }
					break;
				case KeyEvent.VK_LEFT: // zuun---------ug deeshee songoh
					if(selectionNavigationService.moveSelectionLeft(session)){
                        selectWord(false);
                    }
					break;
				case KeyEvent.VK_RIGHT: // baruun---------ug dooshoo songoh
					if(selectionNavigationService.moveSelectionRight(session, words.size())){
                        selectWord(true);
                    }
					break;
				case KeyEvent.VK_UP: // deeshee 61
					player.moveWord(session.getSelectedWordIndex(), true, end_line);
					break;
				case KeyEvent.VK_DOWN: //dooshoo 93
					try {
						player.moveWord(session.getSelectedWordIndex(), false, end_line);
					} catch (IndexOutOfBoundsException e2) {
					}
					break;
					
				case KeyEvent.VK_SHIFT: // 
					end_line = true;
					break;
				case KeyEvent.VK_V: // mur niiluuleh
					mItemRow();
					break;
					
					
					
				default:
					
					break;
				}
			}
			
		}	
		return false;
	}

    private void reload(){
        songText.removeAll();
        for(int j = 0; j < lines.size(); j++){
            JPanel panel = new JPanel(new FlowLayout(3, 5, 0));
            panel.setBorder(BorderFactory.createEmptyBorder());
            panel.setBackground(Color.black);
            for(int i = 0; i < words.size(); i++){
                wordLine word = (wordLine)words.get(i);
                if(word.getLine_idx() == j){
                    panel.add(word.getField());
                    word.setIdx(i);
                }
            }

            songText.add(panel);
        }

        songText.revalidate();
        songText.repaint();
    }

    public void changedUpdate(DocumentEvent documentevent)
    {
    }

    public void insertUpdate(DocumentEvent e)
    {
        songText.repaint();
        songText.revalidate();
    }

    public void removeUpdate(DocumentEvent e)
    {
        songText.repaint();
        songText.revalidate();
    }

    public void focusGained(FocusEvent e)
    {
        field = (myTextfield)e.getSource();
        session.setSelectedWordIndex(field.getIdx());
        selectWord(false);
    }

    public void focusLost(FocusEvent e)
    {
        try
        {
            if(session.getSelectedWordIndex() > -1)
            {
                words.get(session.getSelectedWordIndex()).setPaintColor();
            }
        }
        catch(IndexOutOfBoundsException e2)
        {
            System.out.println("error");
        }
        player.repaint();
        if(editText)
        {
            editTextEnd();
        }
    }

    private void selectWord(boolean down)
    {
    	try {
    		SelectionNavigationService.SelectionResult selection = selectionNavigationService.selectWord(session, words, down, end_line);
            if(!selection.isValid()) {
                return;
            }
            selection.getPreviousWord().setPaintColor();
            end_line = selection.isEndLine();
            wordLine word = selection.getSelectedWord();
            System.out.println(word.getWord()+"  --   "+(word.getLocation() == Location.end));
            word.setSelectColor();
            player.play(word.getSec(), session.getSelectedWordIndex(), session.getSelectedLineIndex());
			
		} catch (IndexOutOfBoundsException e) {
		}
        
    }
	
	public static void main(String[] args) {
		UIManager.put("ToolBar.background", Color.orange);
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new KaraokeCreatorLast();
			}
		});
		
	}


	

}
