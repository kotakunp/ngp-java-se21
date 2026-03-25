package karaoke.app.main.source;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import karaoke.shared.wordLine;

public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private List<wordLine> words;
    AlphaComposite ac;
    private int sec;
    private int line_idx;
    private int word_idx;
    private List<wordLine> lineOne;
    private List<wordLine> lineTwo;
    private int width;
    private int height;
    BufferedImage buffImg;
    Graphics2D gbi;
    AffineTransform transform;
    BasicStroke stroke;
    BasicStroke stroke_paint;
    Font f;
    Font f_paint;
    TextLayout textTl;
    private Color paintColor;
    private boolean play= false;
	private int fix = 25;

    public VideoPanel()
    {
        sec = 0;
        line_idx = 0;
        word_idx = 0;
        width = 1500;
        height = 400;
        paintColor = new Color(234, 103, 17);
        play = false;
        setBackground(Color.black);
        ac = AlphaComposite.getInstance(5, 1.0F);
        stroke_paint = new BasicStroke(1.0F);
        stroke = new BasicStroke(2.0F);
        f_paint = new Font("Monospaced", 1, 50); //  Mongolian Baiti
        f = new Font("Monospaced", 1, 47);
        lineOne = new ArrayList<wordLine>();
        lineTwo = new ArrayList<wordLine>();
    }

    private void setLinesOne()
    {
        buffImg = new BufferedImage(width, height, 2);
        lineOne.removeAll(lineOne);
        int too = 0;
        for(int i = 0; i < words.size(); i++)
        {
            wordLine word = words.get(i);
            if(word.getLine_idx() == line_idx)
            {
                if(too > 0)
                {
                    word.setPosition(words.get(i - 1).getPosWidth());
                } else
                {
                    word.setRectPositionX();
                }
                lineOne.add(word);
                too++;
            }
        }

    }

    private void setLinesTwo()
    {
        buffImg = new BufferedImage(width, height, 2);
        lineTwo.removeAll(lineTwo);
        int too = 0;
        for(int i = 0; i < words.size(); i++)
        {
            wordLine word = words.get(i);
            if(word.getLine_idx() == line_idx)
            {
                if(too > 0)
                {
                    word.setPosition(words.get(i - 1).getPosWidth());
                } else
                {
                    word.setRectPositionX();
                }
                lineTwo.add(word);
                too++;
            }
        }

    }

    private void refresh()
    {
        for(int i = 0; i < words.size(); i++)
        {
            words.get(i).setRectPositionX();
        }

    }

    public void setSec(int current_sec, int line_idx, int word_idx)
    {
        refresh();
        this.line_idx = line_idx;
        this.word_idx = word_idx;
        sec = current_sec;
        if(line_idx % 2 == 0)
        {
            setLinesOne();
        } else
        {
            setLinesTwo();
        }
        this.line_idx++;
        if(this.line_idx % 2 == 0)
        {
            setLinesOne();
        } else
        {
            setLinesTwo();
        }
        play = true;
        repaint();
    }

    public void setSec(int current_sec)
    {
        sec = current_sec;
        play = true;
        repaint();
    }

    public void setLine(int sec, int select_line_idx)
    {
        line_idx = select_line_idx;
        this.sec = sec;
        play = true;
        repaint();
    }

    public void setWords(List<wordLine> words)
    {
        this.words = words;
    }

    protected void paintComponent(Graphics g)
    {
        try {
            BufferedImage image = ImageIO.read(new File("image/img- (1).jpg"));
            g.drawImage(image, 0, 0, this);
        }
        catch(IOException ioexception) { }
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(Color.blue);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        
        
        
		int x = fix * 28;
		//line one
		g2.drawString(""+fix, x, 30); 
		g2.drawLine(x, 30, x, 240); 
		//line two
		x = x * 2;
		g2.drawString(""+fix, x, 30); 
		g2.drawLine(x, 30, x, 240);
        if(play){
            for(int i = 0; i < lineOne.size(); i++){
                paintWord(g2, lineOne.get(i), 100);
            }

            for(int i = 0; i < lineTwo.size(); i++){
                paintWord(g2, lineTwo.get(i), 200);
            }

            for(int i = word_idx; i < words.size(); i++){
                wordLine word = words.get(i);
                if(line_idx == 0){
                    setLinesOne();
                    line_idx++;
                    setLinesTwo();
                }
                if(word.getSec() > sec || word.isPlay() || !word.isPaint()){
                    continue;
                }
                word.setPlay(true);
                if(line_idx == word.getLine_idx()){
                    line_idx++;
                    if(line_idx % 2 == 0){
                        setLinesOne();
                    } else{
                        setLinesTwo();
                    }
                }
                break;
            }

            repaint();
        }
    }

    private void paintWord(Graphics2D g2, wordLine word, int h){
        gbi = buffImg.createGraphics();
        transform = new AffineTransform();
        transform.translate(word.getPosition() + 10, h);
        gbi.transform(transform);
        FontRenderContext frc3 = g2.getFontRenderContext();
        Font f = null;
        Color strokeColor;
        if(word.isPaint() || word.isPlay())
        {
            f = f_paint;
            gbi.setStroke(stroke_paint);
            strokeColor = Color.blue;
        } else
        {
            strokeColor = Color.DARK_GRAY;
            f = f_paint;
            gbi.setStroke(stroke);
        }
        textTl = new TextLayout(word.getWord(), f, frc3);
        Shape shape = textTl.getOutline(null);
        Shape outstroke = textTl.getOutline(null);
        gbi.setColor(strokeColor);
        gbi.draw(outstroke);
        gbi.setColor(Color.white);
        gbi.fill(shape);
        Rectangle2D rec = shape.getBounds();
        word.setWidth((int)rec.getWidth());
        if(word.isPlay())
        {
            gbi.setColor(paintColor);
            gbi.setComposite(ac);
            float rectPositionX = word.getRectPositionX();
            f = f_paint;
            gbi.fill(new java.awt.geom.Rectangle2D.Double(0.0D, -80D, rectPositionX, 110D));
            gbi.setColor(strokeColor);
            gbi.draw(outstroke);
            if(word.getWidth() > rectPositionX)
            {
                word.setRectPositionX(sec);
                repaint();
            } else
            {
                word.setPaint(true);
            }
        }
        g2.drawImage(buffImg, null, 0, 0);
        gbi.dispose();
    }

    public void play()
    {
        sec = 0;
        line_idx = 0;
        play = true;
        repaint();
    }

    public void removeAlls()
    {
    	sec = 0;
        lineOne.clear();
        lineTwo.clear();
        play = false;
        repaint();
    }

    public void stop()
    {
        play = false;
        repaint();
    }

    public boolean isShow(){
        return words.size() > 0;
    }
}
