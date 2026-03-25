package karaoke.app.main.source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import karaoke.shared.MyType;
import karaoke.shared.wordLine;

public class PaintCharPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Timer timer;
	private ArrayList<wordLine> words;
	private Clip clip;
	private int[] seconds;
	private boolean addLine = false;
	private int  endIndex = 0, wordLength = 0, i = 0, line_idx = 0, word_idx = 0, word_idx_last = 0;
	private JButton play;
	StringBuffer textMessage1;
	StringBuffer textMessage2 ;
	public PaintCharPanel(){
		textMessage1 = new StringBuffer();
		textMessage2 = new StringBuffer();
		seconds = new  int[]{27, 43, 63, 47, 68};
		timer =new Timer(23, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		play = new JButton("play");
		play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.start();
			}
		});
		
//		JFrame frame = new JFrame("Hello String");
//		frame.setLayout(new BorderLayout()); 
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Toolkit toolkit = frame.getToolkit();
//		Dimension size = toolkit.getScreenSize();
//		setSize(size.width-400, 400);
		setBackground(Color.black);
//		frame.add(play, BorderLayout.NORTH);
//		frame.add(this, BorderLayout.CENTER);
//        frame.setSize(size.width-400, 400);
//        frame.setLocation(-size.width, 400);
//        frame.setVisible(true);
        
	}
	
	public void addChar() {
		endIndex++;
		wordLength++;
		repaint();
	}
	
	public void addWord(int word_idx) {
//		clip.setFramePosition(words.get(word_idx+1).getCurrent_sec()*1000);
		
		wordLength = 0;
		this.word_idx = word_idx;
		endIndex = words.get(word_idx).getEndIndex();
		line_idx = words.get(word_idx).getLine_idx();
		setTextMessage();
		repaint();
	}
	
	public void addWord(int word_idx, int char_idx) {
		wordLength = 0;
		this.word_idx = word_idx;
		endIndex = words.get(word_idx).getEndIndex() + char_idx;
		line_idx = words.get(word_idx).getLine_idx();
		setTextMessage();
		repaint();
	}
	
	public void addLine(int line_idx) {
		this.line_idx = line_idx;
		endIndex = 0;
		wordLength =0;
		setTextMessage();
		addLine= false;
		repaint();
	}
	
	public int getWordLength() {
		return wordLength;
	}
	
	public int getWord_idx_last() {
		return word_idx_last;
	}
	
	public boolean isAddLine() {
		return addLine;
	}

	private void setTextMessage() {
		textMessage1.delete(0, textMessage1.length());
    	textMessage2.delete(0, textMessage2.length());
		for (int i = 0; i < words.size(); i++) {
        	wordLine word = words.get(i);
			if(word.getLine_idx() == line_idx) {
				word.setEndIndex(textMessage1.length()); 
				if(word.getType() == MyType.splite_main)
					textMessage1.append(word.getWord());
				else if(word.getType() == MyType.splite_sub && word.isThreeSplite())
					textMessage1.append(word.getWord());
				else
					textMessage1.append(word.getWord()+" ");
				word_idx_last = word.getIdx()+1;
				
//				if(word_idx == -1)
//					word_idx = line.getIdx();
			}
			if(word.getLine_idx() == line_idx+1){
				if(word.getType() == MyType.splite_main)
					textMessage2.append(word.getWord());
				else if(word.getType() == MyType.splite_sub && word.isThreeSplite())
					textMessage2.append(word.getWord());
				else
					textMessage2.append(word.getWord()+" ");
			}
		}
		repaint();
	}
	Font font = new Font("AGCooper Mon", Font.BOLD, 30);
	Color color = new Color(255, 128, 0);
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics fm = g.getFontMetrics(font);

        g.setFont(font);
        try {
        	String before = textMessage1.substring(0, endIndex);
            int widthH = fm.stringWidth(before);
            String after = textMessage1.substring(endIndex, textMessage1.length());
            g.setColor(Color.yellow);
            g.drawString(before, 10, 100);

            g.setColor(color); 
            g.drawString(after, 10 + widthH, 100);
            g.setColor(color);
            g.drawString(textMessage2.toString(), 10, 200);
		} catch (StringIndexOutOfBoundsException e) {
//			e.printStackTrace();
//			addLine = true;
		} 
    }
    
    private void paintString(Graphics2D g2d, String value, Font font, int w, int h, Color color) {
    	FontRenderContext fontRendContext = g2d.getFontRenderContext();
//      String st = "Hello World.";

      // Create an layout of the text
      TextLayout text = new TextLayout(value, font, fontRendContext);
      // Generate a shape of the layout
      Shape shape = text.getOutline(null);
      // Align the shape to the center
      Rectangle rect = shape.getBounds();
      AffineTransform affineTransform = new AffineTransform();
      affineTransform = g2d.getTransform();
      affineTransform.translate(w / 2 - (rect.width / 2), h / 2
              + (rect.height / 2));
      g2d.transform(affineTransform);

      // Fill in blue
      g2d.setColor(color);
      g2d.fill(shape);
      // Outline in red
      g2d.setColor(Color.white);
      g2d.draw(shape);
      g2d.dispose();
    }

    @Override 
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);
    }

	public void removeAlls() {
		textMessage1.delete(0, textMessage1.length());
    	textMessage2.delete(0, textMessage2.length());
		repaint();
		timer.stop();
	}

	public void setWords(ArrayList<wordLine> words, Clip clip) {
		this.words = words;
		this.clip = clip;
//		line_idx = 0;
		setTextMessage(); 
		repaint();
	}

	public boolean isShow() {
		if(textMessage1.length() > 0)
			return true;
		else
			return false;
	}

	public void play() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void setIndexs(int line_idx, int word_idx) {
		endIndex = 0;
		this.line_idx = line_idx;
		this.word_idx = word_idx;
		StringBuffer text = new StringBuffer();
		for (int i = 0; i < words.size(); i++) {
        	wordLine line = words.get(i);
			if(line.getLine_idx() == line_idx ) {
				if(line.getIdx() < word_idx) {
					if(line.getType() == MyType.splite_main)
						text.append(line.getWord());
					else if(line.getType() == MyType.splite_sub && line.isThreeSplite())
						text.append(line.getWord());
					else
						text.append(line.getWord()+" ");
				}
				word_idx_last = line.getIdx()+1;
			}
		}
		
		endIndex = text.length();
		if(timer.isRunning())
			timer.restart();
		else		
			timer.start();
		System.out.println(line_idx+"             "+word_idx+"        "+endIndex+"      "+ timer.isRunning());

		setTextMessage(); 
//		repaint();
		
	}

	

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable(){
//            public void run() {
//                new PaintCharPanel();
//            }
//        });
//    }
}






