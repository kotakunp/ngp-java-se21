package karaoke.app.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Timer;

import karaoke.shared.MyButton;
import karaoke.shared.MyComfirmDialog;
import karaoke.shared.MyLabel;
import karaoke.shared.MyMeniItem;
import karaoke.app.main.source.VideoPanel;
import karaoke.app.main.source.WavPlayer;

public class KaraokeCreator extends JFrame {
	private static final long serialVersionUID = 1L;

	protected ResourceBundle rb; 
	protected Dimension size;
	private static String myMark = "D8-FC-93-63-9F-E3"; // my
//	private static String myMark = "28-CD-C4-75-AB-79"; // Boogii 
//	private static String myMark = "0A-E0-AF-C4-3D-FD"; // Boogii ajil
//	private static String myMark = "2C-4D-54-D3-47-FD"; // Ganju
//	private static String myMark = "24-0A-64-B7-86-32"; // taivnaa_wifi
//	private static String myMark = "F0-1F-AF-1F-D9-E6"; // taivnaa_LAN
//	private static String myMark = "6C-71-D9-36-64-F8"; // taivnaa_LAN2
//	private static String myMark = "60-45-CB-5F-3C-BC"; // bambar
//	private static String myMark = "40-B0-76-A0-15-9A"; // uugii _PC
//	private static String myMark = "00-FF-17-4E-38-74"; // my 2
//	private static String myMark = "‎0C-9D-92-C1-61-B2"; // Dulguun
//	private static String myMark = "5C-F9-DD-76-6B-F1"; // boloroo egch
//	private static String myMark = "50-E5-49-46-AE-14"; // Idree
//	private static String myMark = "6C-F0-49-E6-44-8A"; // miigaa 2
//	private static String myMark = "2E-E5-CE-C7-30-7E"; // Altka
//	private static String myMark = "26-CA-86-10-B2-6F"; // Altka
	
	/*****************MENU BAR********************/
	private JMenuBar menuBar;
	private JMenu menuFile, menuEdit;
	protected MyMeniItem mItemNew, mItemOpen, mItemClose, mItemSave, mItemSavePro, mItemExport;
	protected MyMeniItem mItemAudio, mItemText, mItemPaint, mItemOPaintF, mItemPaintR;
	
	
	/*****************TOOLBAR********************/
	private JToolBar toolbar;
	protected MyButton importAudio, importText, paintStart, paintFinish, audioPause,audioPlay, syncPlay, savePro, openPro, textAdd, secEdit, closePro, oneWord, orderLine;
	protected MyButton playVideo, playlist;
	
	/*****************TEXT VIEW********************/
	protected JPopupMenu popupMenu;
	protected VideoPanel videoPanel;
	private JSplitPane centerSplit;
	protected JPanel songText;
	protected MyMeniItem editTextItem, editTextEnd, popPaintR, cutText, cutTextBack, mergeText, spliteText, mItemRows, mItemRow ;
	
	/*****************SEEK VIEW********************/
	protected MyLabel duration;
	private MyLabel seek;
	protected MyLabel file_name;
	protected MyLabel direction;
	protected MyLabel state;
	protected WavPlayer player;
	
	/*****************INSERT TEXT********************/
	protected JDialog insertTextdialog;
	protected JTextArea insertText;
	protected boolean editText = false;
	
	public KaraokeCreator() {
		super("Karaoke creator");
		String language = new String("mn");
		String country = new String("MN");
		Locale locale = new Locale(language, country);
		rb = ResourceBundle.getBundle("karaoke/shared/admin", locale);
//		if(checkStart()) {
			Toolkit toolkit = getToolkit();
			size = toolkit.getScreenSize();
			createBar();
			createToolbar();
			createText();
			createSeek();
			ImageIcon img = new ImageIcon("image/iconJ.png");
			setIconImage(img.getImage());
			setSize(size.width, (size.height-40));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
//			setLocation(1920, 0); //-size.width+  -size.width+400
			setVisible(true);
//		}else {
//			new MyComfirmDialog(null, rb.getString("message.header"), rb.getString("project.error"), 1);
//			System.exit(0); 
//		}
		
	}

	private boolean checkStart() {
		boolean back = false;
		InetAddress ip;
		try {	
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());
			
			NetworkInterface network;
			network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			if(mac != null) {
				System.out.print("Current MAC address : ");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				String origStr = sb.toString();
				System.out.print(origStr);
				if(origStr.equals(myMark))
					back = true;
			}else {
				System.out.println("work");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
		return back;
	}
	
	private void createBar() {
		menuBar = new JMenuBar();
		menuFile = new JMenu("Файл");
		menuEdit = new JMenu("Заавар");
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		
		mItemNew = new MyMeniItem(rb.getString("mItemNew"),new ImageIcon("image/menuOpen.png"), KeyEvent.VK_T, "mItemNew");
		mItemOpen = new MyMeniItem(rb.getString("openPro"), KeyEvent.VK_T, "mItemOpen");
		mItemClose = new MyMeniItem(rb.getString("mItemClose"),KeyEvent.VK_T, "mItemClose");
		mItemSavePro = new MyMeniItem(rb.getString("savePro"),new ImageIcon("image/menuOpenPro.png"),KeyEvent.VK_T, "mItemSavePro");
		mItemExport = new MyMeniItem(rb.getString("syncPlay"),new ImageIcon("image/menuSync.png"),KeyEvent.VK_T, "mItemExport");
		
		mItemAudio = new MyMeniItem(rb.getString("importAudio"),KeyEvent.VK_T, "mItemAudio");
		mItemText = new MyMeniItem(rb.getString("importText"),new ImageIcon("image/menuText.png"), KeyEvent.VK_T, "mItemText");
		mItemPaint = new MyMeniItem(rb.getString("paintStart"),KeyEvent.VK_T, "mItemPaint");
		mItemOPaintF = new MyMeniItem(rb.getString("paintFinish"),KeyEvent.VK_T, "mItemOPaintF");
		mItemPaintR = new MyMeniItem(rb.getString("paintCut"),KeyEvent.VK_T, "mItemPaintR");
		
		menuFile.add(mItemNew);
		menuFile.add(mItemOpen);
		menuFile.addSeparator();
		menuFile.add(mItemSavePro);
		menuFile.add(mItemExport);
		menuFile.addSeparator();
		menuFile.add(mItemClose);
		
		menuEdit.add(mItemAudio);
		menuEdit.add(mItemText);
		menuEdit.addSeparator();
		menuEdit.add(mItemPaint);
		menuEdit.add(mItemOPaintF);
		menuEdit.add(mItemPaintR);
		setJMenuBar(menuBar);

	}
	
	private void createToolbar() {
		toolbar = new JToolBar(JToolBar.CENTER);
		toolbar.setRollover(true);
		toolbar.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		openPro = new MyButton("openPro", "image/open pro.png", rb.getString("openPro"));
		savePro = new MyButton("savePro", "image/save pro.png", rb.getString("savePro"));
		
		importAudio = new MyButton("importAudio", "image/song1.png", rb.getString("importAudio"));
		importText = new MyButton("importText", "image/text.png", rb.getString("importText"));
		textAdd = new MyButton("textAdd", "image/textAdd.png", rb.getString("textAdd"));
		
		paintStart = new MyButton("paintStart", "image/paint.png", rb.getString("paintStart"));
		paintFinish = new MyButton("paintFinish", "image/paintF.png", rb.getString("paintFinish"));
//		paintRefresh = new MyButton("paintCut", "image/paintR.png", rb.getString("paintCut"));
		
		audioPlay = new MyButton("audioPlay", "image/audioPlay.png", rb.getString("audioPlay"));
		audioPause = new MyButton("audioPause", "image/audioPause.png", rb.getString("audioPause"));
		
		syncPlay = new MyButton("syncPlay", "image/export.png", rb.getString("syncPlay"));
		
		playVideo = new MyButton("playVideo", "image/playVideo2.png", rb.getString("playVideo"));
		playlist = new MyButton("playlist", "image/playlist2.png", rb.getString("playlist"));
		
		secEdit = new MyButton("secEdit", "image/secEdit.png", rb.getString("secEdit"));
		closePro = new MyButton("closePro", "image/close.png", rb.getString("closePro"));
		
		
		oneWord = new MyButton("oneWord", "image/open pro.png", rb.getString("openPro"));
		orderLine = new MyButton("orderLine", "image/paintR.png", rb.getString("openPro"));
		
		
		toolbar.addSeparator();
		
		toolbar.add(openPro);
		toolbar.add(savePro);
		toolbar.addSeparator(new Dimension(size.width/20,50));
		
		toolbar.add(importAudio);
		toolbar.add(importText);
//		toolbar.add(textAdd);
		toolbar.addSeparator(new Dimension(size.width/20,50));
		
		toolbar.add(paintStart);
		toolbar.add(paintFinish);
//		toolbar.add(paintRefresh);
		toolbar.addSeparator(new Dimension(size.width/20,50));  
		
		toolbar.add(syncPlay);
		toolbar.addSeparator(new Dimension(size.width/20,50));  
		
		toolbar.add(audioPlay);
		toolbar.add(audioPause);
//		toolbar.addSeparator(new Dimension(size.width/20,50));
//		toolbar.add(playVideo);
		toolbar.addSeparator(new Dimension(size.width/20,50));
//		toolbar.add(playlist);
		toolbar.add(secEdit);
		toolbar.addSeparator(new Dimension(size.width/20,50));
		toolbar.add(closePro);
		toolbar.addSeparator(new Dimension(size.width/20,50));
		toolbar.add(oneWord);
		toolbar.addSeparator(new Dimension(size.width/20,50));
		toolbar.add(orderLine);

		add(toolbar, BorderLayout.NORTH); 
	}
	
	private void createEncrypt() {
		
	}
	
	private void createText() {
		videoPanel = new VideoPanel();
		JPanel textContainer = new JPanel(new BorderLayout());
		centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, videoPanel, textContainer);
		centerSplit.setOneTouchExpandable(true);
		centerSplit.setDividerLocation(size.height/4);
		
		songText = new JPanel();
		songText.setLayout(new BoxLayout(songText, BoxLayout.Y_AXIS)); 
		songText.setBackground(Color.black);
		songText.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		JScrollPane pane =new JScrollPane(songText);
//		songText.setFont(new Font("Arial Mon", Font.BOLD, 25));
		textContainer.add(pane, BorderLayout.CENTER);
		
		popupMenu = new JPopupMenu("Edit");   
		editTextItem = new MyMeniItem(rb.getString("editText"),new ImageIcon("image/editText.png"), KeyEvent.VK_E, "editText"); 
        editTextEnd = new MyMeniItem(rb.getString("editTextEnd"),new ImageIcon("image/editTextEnd.png"), KeyEvent.VK_F, "editTextEnd"); 
		popPaintR = new MyMeniItem(rb.getString("paintCut"),new ImageIcon("image/paintRpop.png"), KeyEvent.VK_T, "popPaintR");
        cutText = new MyMeniItem(rb.getString("cutText"),new ImageIcon("image/cut.png"), KeyEvent.VK_T, "cutText");  
        cutTextBack = new MyMeniItem(rb.getString("cutTextBack"),new ImageIcon("image/cut back.png"), KeyEvent.VK_T, "cutTextBack");
        mergeText = new MyMeniItem(rb.getString("mergeText"),new ImageIcon("image/mergeText.png"), KeyEvent.VK_T, "mergeText");  
        spliteText = new MyMeniItem(rb.getString("spliteText"),new ImageIcon("image/spliteText.png"), KeyEvent.VK_T, "spliteText");  
        
        mItemRows = new MyMeniItem(rb.getString("mItemRows"),new ImageIcon("image/mItemRows.png"), KeyEvent.VK_T, "mItemRows");  
        mItemRow = new MyMeniItem(rb.getString("mItemRow"),new ImageIcon("image/mItemRow.png"), KeyEvent.VK_T, "mItemRow");  
        
        popupMenu.add(editTextItem);
        popupMenu.add(editTextEnd); 
        popupMenu.addSeparator();
        popupMenu.add(cutText); 
        popupMenu.add(cutTextBack); 
        popupMenu.addSeparator();
        popupMenu.add(popPaintR); 
        popupMenu.addSeparator();
        popupMenu.add(mergeText);
        popupMenu.add(spliteText);
        popupMenu.addSeparator();
        popupMenu.add(mItemRows);
        popupMenu.add(mItemRow);
	}
	
	private void createSeek() {
		JPanel paintContainer = new JPanel(new BorderLayout());
		
		JPanel tools = new JPanel(new BorderLayout());
		JPanel info = new JPanel(new GridLayout(5, 2)); 
		info.setBackground(Color.black);
		duration = new MyLabel();
		seek = new MyLabel();
		file_name = new MyLabel();
		direction = new MyLabel();
		state = new MyLabel(1);

		info.add(new MyLabel(rb.getString("direction")+": ", JLabel.RIGHT));
		info.add(direction);
		info.add(new MyLabel(rb.getString("file")+": ", JLabel.RIGHT));
		info.add(file_name);
		info.add(new MyLabel(rb.getString("length")+": ", JLabel.RIGHT));
		info.add(duration);
		info.add(new MyLabel(rb.getString("seek")+": ", JLabel.RIGHT));
		info.add(seek);
		info.add(new MyLabel(rb.getString("state")+": ", JLabel.RIGHT));
		info.add(state);
		tools.add(info, BorderLayout.CENTER);
		paintContainer.add(info, BorderLayout.NORTH);
		
		player = new WavPlayer(seek);
		JScrollPane scrollPane = new JScrollPane(player);
		player.setScroll(scrollPane);
		player.setVideoPanel(videoPanel); 
		paintContainer.add(scrollPane, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				paintContainer, centerSplit);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(400);
		Dimension minimumSize = new Dimension(100, 50);
		paintContainer.setMinimumSize(minimumSize);
		add(splitPane, BorderLayout.CENTER);
		createSecEditDialog();
	}
	JFrame seekdialog = new JFrame();
	protected JTextField seekValue;
	protected JButton moveSeek;
	protected ButtonGroup seekGroup;
	private void createSecEditDialog() {
		seekdialog.setLayout(new BorderLayout());
		seekdialog.getContentPane().setBackground(Color.black);
		seekdialog.getContentPane().setLayout(new BorderLayout());
		seekdialog.setTitle("Seconds settings");
		seekdialog.setUndecorated(false);
		seekdialog.setResizable(false);
		seekdialog.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
//		        if ("Optional condition") {
		    	seekdialog.setVisible(false);
//		        }
		    }
		});

	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		seekdialog.setSize(dim.width / 4, dim.height / 5);
	    seekdialog.setLocation(dim.width / 3 - this.getWidth() / 2, dim.height / 3 - this.getHeight() / 2);
		 
	     JPanel center = new JPanel(new GridLayout(3, 2));
	     center.setOpaque(false);
	     seekdialog.add(new MyLabel(rb.getString("seekGarchig"), JLabel.CENTER, Color.white, 25), BorderLayout.NORTH);
	     
	     JRadioButton urgshSeek = new JRadioButton(rb.getString("urgshSeek"));
//	     urgshSeek.setMnemonic(KeyEvent.VK_C);
	     urgshSeek.setActionCommand("urgshSeek");
	     urgshSeek.setSelected(true);
//
	     JRadioButton hoishSeek = new JRadioButton(rb.getString("hoishSeek"));
//	     hoishSeek.setMnemonic(KeyEvent.VK_D);
	     hoishSeek.setActionCommand("hoishSeek");
	     seekGroup= new ButtonGroup();
	     seekGroup.add(urgshSeek);
	     seekGroup.add(hoishSeek);
	     
	     center.add(urgshSeek);
	     center.add(hoishSeek);
	     
	     seekValue = new JTextField(3);
	     center.add(new JLabel(rb.getString("seek")));
	     center.add(seekValue);
	     seekValue.setText("200");
	     
	     moveSeek = new MyButton(rb.getString("seekbtn"), "moveSeek");
	     center.add(new JLabel());
	     center.add(moveSeek);
	     
	     
	     
//	     int x = JOptionPane.showOptionDialog(dialog, "Lauren's mom had four kids: Maria, Martha, Margaret...","The missing kid", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, options[0]);
//	     if (x == 3) {
//	    	 dialog.getContentPane().add(center);
//	    	 dialog.repaint();
//	    	 dialog.revalidate();
//	     } else {
//	    	 dialog.dispose();
//	    	 JOptionPane.showMessageDialog(null, "Nooope!");
//	     }
	     JPanel temp = new JPanel(new FlowLayout());
	     temp.add(center);
	     seekdialog.getContentPane().add(temp, BorderLayout.CENTER);

	     seekdialog.getContentPane().add(moveSeek, BorderLayout.SOUTH);
	}
	
	protected void textAdd(){
        insertTextdialog = new JDialog(this, rb.getString("textAdd"), false);
        insertTextdialog.setLayout(new BorderLayout());
        JPanel tool = new JPanel(new FlowLayout());
        JButton b = new JButton(rb.getString("importText"));
        b.setActionCommand("importText2");
//        b.addActionListener(this);
        tool.add(b);
        insertTextdialog.add(tool, "North");
        insertText = new JTextArea();
        insertText.setFont(new Font("", 0, 16));
        insertText.setBorder(BorderFactory.createBevelBorder(0));
        insertText.requestFocus(true);
        insertTextdialog.add(insertText, "Center");
        insertTextdialog.setSize(500, 500);
        insertTextdialog.setVisible(true);
        editText = true;
    }

	protected void clearWindow(){
//        index = 0;
//        save = false;
//        words.removeAll(words);
        songText.removeAll();
        songText.repaint();
        player.removeAlls();
        videoPanel.removeAlls();
        duration.setText("");
        file_name.setText("");
        seek.setText("");
        direction.setText("");
        
    }
	
	public void setFocus() {
//		importAudio, importText, paintStart, audioPlay, syncPlay, syncMon, savePro, openPro, textAdd, secEdit, closePro, oneWord, orderLine
		importAudio.setFocusable(false);
		importText.setFocusable(false);
		paintStart.setFocusable(false);
		audioPlay.setFocusable(false);
		syncPlay.setFocusable(false);
//		syncMon.setFocusable(false);
		
		toolbar.setFocusable(false);
		savePro.setFocusable(false);
		openPro.setFocusable(false);
		textAdd.setFocusable(false);
		closePro.setFocusable(false);
		secEdit.setFocusable(false);
		
	}

}
