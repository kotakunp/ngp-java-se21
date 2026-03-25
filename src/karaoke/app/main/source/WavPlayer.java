package karaoke.app.main.source;

import java.awt.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;

import karaoke.app.main.service.PaintPlayer;
import karaoke.shared.wordLine;
import karaoke.shared.Location;
import karaoke.shared.MyLabel;
import karaoke.shared.timing.TimelineMath;


public class WavPlayer extends JPanel implements PaintPlayer {		
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
    private Clip clip;
    private Timer timer;
    private int sec;
    private static final int WAVEFORM_TIMER_DELAY_MS = 16;
    private wordLine cursor;
    Point prevPoint;
    Point newPoint;
    private wordLine currentWord;
    private wordLine beforeWord;
    private wordLine afterWord;
    private ArrayList<wordLine> words;
    private VideoPanel videoPanel;
    int select_ug_idx;
    int select_line_idx;
    int temp_ug_idx;
    private boolean isPaint;
    private MyLabel seek;
    private float sounwidth[];
    private float small_w[];
    private long soundlength;
    private int screen_h;
    private int screen_anh;
    private int tempScroll;
    private boolean audioLoading;
    private final AudioWaveformLoader audioWaveformLoader = new AudioWaveformLoader();
    private final WaveformImageCache waveformImageCache = new WaveformImageCache();

    public WavPlayer(MyLabel seek)
    {
        sec = 0;
        prevPoint = null;
        newPoint = null;
        currentWord = null;
        beforeWord = null;
        afterWord = null;
        select_ug_idx = 0;
        select_line_idx = 0;
        temp_ug_idx = -1;
        soundlength = 0L;
        screen_h = 0;
        screen_anh = 0;
        tempScroll = 0;
        this.seek = seek;
        setBackground(Color.black);
        try
        {
            clip = AudioSystem.getClip();
        }
        catch(LineUnavailableException e1)
        {
            e1.printStackTrace();
        }
        cursor = new wordLine("Cursor");
        prevPoint = new Point();
        newPoint = new Point();
        MouseAdapter mouseAdapter = new MouseAdapter() {

            public void mouseMoved(MouseEvent mouseevent)
            {
            }

            public void mousePressed(MouseEvent e)
            {
                if(audioLoading || !isAudioReady())
                {
                    return;
                }
                prevPoint = e.getPoint();
                int point_y = (int)prevPoint.getY();
                sec = point_y;
                cursor.moveLine(sec);
                clip.setFramePosition(TimelineMath.timelineUnitToClipFrame(sec));
                clip.start();
                timer.start();
                setScroll(sec);
                videoPanel.setSec(sec);
                if(words != null)
                {
                    for(int i = 0; i < words.size(); i++)
                    {
                        wordLine line = words.get(i);
                        if(point_y < line.getCurrent_sec() || point_y >= line.getLow_sec())
                        {
                            continue;
                        }
                        select_ug_idx = line.getIdx();
                        int char_idx = line.setChar(point_y);
                        currentWord = line;
                        select_line_idx = line.getLine_idx();
                        line.setPlay(true);
                        videoPanel.setSec(sec, select_line_idx, select_ug_idx);
                        repaint();
                        break;
                    }

                    if(words.get(0).getCurrent_sec() >= point_y)
                    {
                        select_ug_idx = words.get(0).getIdx();
                        select_line_idx = words.get(0).getLine_idx();
                        videoPanel.setSec(sec, select_line_idx, select_ug_idx);
                        repaint();
                    }
                }
                repaint();
            }

            public void mouseDragged(MouseEvent mouseevent){
//                this$0 = WavPlayer.this;
//                super();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void moveWord(int select_ug_idx, boolean up, boolean endLine)
    {
        currentWord = words.get(select_ug_idx);
        int sec = 0;
        if(endLine)
        {
            sec = currentWord.getLow_sec();
        } else
        {
            sec = currentWord.getSec();
        }
        if(up)
        {
            sec--;
        } else
        {
            sec++;
        }
        if(select_ug_idx > 0)
        {
            beforeWord = words.get(select_ug_idx - 1);
        }
        if(select_ug_idx < words.size() - 1)
        {
            afterWord = words.get(select_ug_idx + 1);
        }
        if(endLine){
            currentWord.moveLineEnd(sec);
            afterWord.setHigh_sec(sec);
        } else {
            currentWord.moveLine(sec);
            if(currentWord.getLocation() == Location.start) {
                afterWord.setHigh_sec(sec);
            } else if(currentWord.getLocation() == Location.middle){
                beforeWord.setLow_sec(sec);
                afterWord.setHigh_sec(sec);
            } else if(currentWord.getLocation() == Location.end && beforeWord.getLocation() != Location.end){
                beforeWord.setLow_sec(sec);
            }
        }
        clip.setFramePosition(TimelineMath.timelineUnitToClipFrame(sec));
        clip.start();
        repaint();
    }

    public String openAudio(String fileDir, Dimension size)
    {
        throw new UnsupportedOperationException("Use loadAudio/applyAudioLoad for background-safe loading");
    }

    public AudioLoadResult loadAudio(String fileDir, Dimension size)
        throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        File audioFile = new File(fileDir);
        return audioWaveformLoader.load(audioFile, size);
    }

    public void applyAudioLoad(AudioLoadResult result)
    {
        stop();
        if(clip != null && clip.isOpen())
        {
            clip.close();
        }
        clip = result.clip;
        small_w = result.waveform;
        soundlength = result.soundlength;
        screen_anh = result.screenAnh;
        screen_h = result.screenH;
        sec = 0;
        tempScroll = 0;
        waveformImageCache.invalidate();
        timer = createPlaybackTimer();
        setPreferredSize(new Dimension(300, result.preferredHeight));
        revalidate();
        repaint();
    }

    public void setAudioLoading(boolean audioLoading)
    {
        this.audioLoading = audioLoading;
    }

    public boolean isAudioLoading()
    {
        return audioLoading;
    }

    public boolean isAudioReady()
    {
        return clip != null && clip.isOpen() && timer != null;
    }

    private Timer createPlaybackTimer()
    {
        return new Timer(WAVEFORM_TIMER_DELAY_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(clip != null && clip.isRunning())
                {
                    sec = TimelineMath.clipFrameToTimelineUnit(clip.getFramePosition());
                    repaint();
                } else if(timer != null)
                {
                    timer.stop();
                }
            }
        });
    }

    public boolean play()
    {
    	if(audioLoading)
    	{
    		return false;
    	}
    	
    	if (clip.isRunning()) {
    		clip.stop();
    	    isPaint = false;
    	    videoPanel.stop();
    	}else {
    		if(isAudioReady()){
                clip.start();
                timer.start();
                videoPanel.play();
                repaint();
            } else{
                JOptionPane.showMessageDialog(this, "\u0414\u0443\u0443\u0433\u0430\u0430 \u043E\u0440\u0443\u0443\u043B\u043D\u0430 " +"\u0443\u0443");
            }
    	}
    	return clip.isRunning();
    	
    }

    public void play(int sec, int select_ug_idx, int select_line_idx)
    {
        this.sec = sec;
        setScroll(sec);
        clip.setFramePosition(TimelineMath.timelineUnitToClipFrame(sec));
        this.select_ug_idx = select_ug_idx;
        this.select_line_idx = select_line_idx;
        currentWord = words.get(select_ug_idx);
        videoPanel.setSec(sec, select_line_idx, select_ug_idx);
        if(select_ug_idx > 0)
        {
            beforeWord = words.get(select_ug_idx - 1);
        }
        int s = words.size();
        if(select_ug_idx < s)
        {
            try
            {
                afterWord = words.get(select_ug_idx + 1);
            }
            catch(IndexOutOfBoundsException indexoutofboundsexception) { }
        }
        repaint();
    }

    public void setScroll(JScrollPane scrollPane)
    {
        this.scrollPane = scrollPane;
    }

    public void setWords(ArrayList<wordLine> words){
        this.words = words;
        videoPanel.setWords(words);
        repaint();
    }

    public boolean addLane(int index)
    {
        int sec = TimelineMath.clipFrameToTimelineUnit(clip.getFramePosition());
        try
        {
            currentWord = words.get(index);
            if(currentWord.isPaint())
            {
                currentWord.setLocation(Location.middle);
                currentWord.setLine_end(null);
            }
            if(index > 0){
                beforeWord = words.get(index - 1);
                if(beforeWord.getLocation() == Location.end){
                    currentWord.setLocation(Location.start);
                    currentWord.setLow_sec(sec);
                } else{
                    beforeWord.setLow_sec(sec);
                    int high = beforeWord.getCurrent_sec();
                    currentWord.setHigh_sec(high);
                }
            } else if(index == 0) {
                currentWord.setLocation(Location.start);
            }
            currentWord.setPaint(true);
            currentWord.setPaintColor();
            currentWord.moveLine(sec);
            currentWord.setLow_sec(sec);
            words.get(index + 1).setFocusColor();
            return true;
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception) { }
        return false;
    }

    public void setWord(int index)
    {
        int sec = TimelineMath.clipFrameToTimelineUnit(clip.getFramePosition());
        currentWord = words.get(index);
        currentWord.moveLine(sec);
        currentWord.setLow_sec(sec);
        if(currentWord.getLocation() != Location.middle)
        {
            currentWord.removeEndline();
        }
        if(index > 0)
        {
            beforeWord = words.get(index - 1);
            beforeWord.setLow_sec(sec);
        }
    }

    public void addEndLine(Boolean isPaint, int index) {
        int sec = 0;
        if(currentWord != null) {
	        if(!isPaint) {
	            if(currentWord.getLocation() != Location.end){
	                System.out.println("---------------");
	            	System.out.println(currentWord.getLocation()+"              "+ afterWord.getLocation());
	            	System.out.println(currentWord.getIdx()+", "+ afterWord.getIdx());
	                sec = currentWord.getLow_sec() - 15;
	                currentWord.addEndLine(sec);
	                if(afterWord.getLocation() == Location.middle){
	                    afterWord.setLocation(Location.start);
	                }
	            	System.out.println(currentWord.getLocation()+"              "+ afterWord.getLocation());
	            } else{
	            	if(currentWord.getIdx() < index) {
	            		currentWord.removeEndline();
		                if(afterWord.getLocation() != Location.end){
		                    afterWord.setLocation(Location.middle);
		                }
		                currentWord.setLow_sec(afterWord.getCurrent_sec());
	            	}
	            }
	        } else{
	            currentWord = words.get(index);
	            sec = TimelineMath.clipFrameToTimelineUnit(clip.getFramePosition());
	            currentWord.addEndLine(sec);
	        }
	        repaint();
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        int width = getWidth();
        if(cursor != null){
            g2d.setColor(Color.blue);
            cursor.moveLine(sec);
            g2d.draw(cursor.getShape());
            if(sec == screen_h && tempScroll < sec) {
                tempScroll = sec;
                scrollPane.getVerticalScrollBar().setValue(sec - 30);
                screen_h = (screen_h + screen_anh) - 100;
            }
        }
        if(soundlength > 0){
            java.awt.image.BufferedImage waveformImage = waveformImageCache.getOrCreate(width, small_w);
            if(waveformImage != null)
            {
                g.drawImage(waveformImage, 0, 0, null);
            }
        }
        String timeSum = clip != null && clip.isOpen()
            ? (new StringBuilder(String.valueOf(clip.getMicrosecondLength() / 1000L)))
                .append(" : ").append(clip.getFrameLength() / 1000).toString()
            : "0 : 0";
        g.setColor(Color.WHITE);
        g.drawString(timeSum.toString(), 10, 20);
        String time = clip != null && clip.isOpen()
            ? TimelineMath.formatMinutesSecondsFromMicros(clip.getMicrosecondPosition())
            : "0 : 0";
        g.setColor(Color.RED);
        g.drawString(time.toString(), 10, 40);
        long elapsedMillis = clip != null && clip.isOpen()
            ? clip.getMicrosecondPosition() / 1000L
            : 0L;
        String time2 = TimelineMath.formatMinutesSecondsFromMillis(elapsedMillis);
        g.setColor(Color.BLUE);
        g.drawString(time2.toString(), 10, 60);
        int frame = clip != null && clip.isOpen() ? TimelineMath.clipFrameToTimelineUnit(clip.getFramePosition()) : 0;
        String frameToo = (new StringBuilder()).append(frame).toString();
        g.setColor(Color.yellow);
        g.drawString(frameToo, 10, 80);
        seek.setText(time2.toString());
        if(words != null)
        {
            for(int i = 0; i < words.size(); i++)
            {
                wordLine line = words.get(i);
                if(line.isPaint() && !isPaint)
                {
                    if(line.getCurrent_sec() <= sec && select_ug_idx == line.getIdx())
                    {
                        videoPanel.setSec(sec);
                    }
                    if(line.getCurrent_sec() <= sec && select_ug_idx < line.getIdx())
                    {
                        if(temp_ug_idx > -1)
                        {
                            words.get(temp_ug_idx).setPaintColor();
                        }
                        select_ug_idx = line.getIdx();
                        temp_ug_idx = select_ug_idx;
                        line.setChar_idx(0);
                        line.setReadColor();
                    }
                    if(line.getCharCurDuration() <= sec && select_ug_idx == line.getIdx())
                    {
                        line.addChar_idx();
                    }
                }
                g2d.setColor(line.getColor());
                if(line.getLine_h() != null)
                {
                    g2d.draw(line.getLine_h());
                }
                if(line.getLine_end() != null)
                {
                    g2d.draw(line.getLine_end());
                }
                if(line.getShape() != null)
                {
                    g2d.draw(line.getShape());
                    g2d.setFont(new Font("", 1, 16));
                    g2d.drawString(line.getWord(), line.getX2(), line.getCurrent_sec());
                }
            }

        }
    }

    public Clip getClip()
    {
        return clip;
    }

    private void setScroll(int sec)
    {
        scrollPane.getVerticalScrollBar().setValue(sec - 100);
        tempScroll = sec;
        screen_h = (sec + screen_anh) - 200;
    }

    public void removeAlls(){
        if(timer != null)
        {
            timer.stop();
        }
        if(clip != null && clip.isOpen())
        {
            clip.stop();
            clip.close();
        }
        sec = 0;
        soundlength = 0L;
        small_w = null;
        waveformImageCache.invalidate();
        if(words != null)
        {
            words.removeAll(words);
        }
        repaint();
    }
    
    public void removeWords(){
        if(timer != null)
        {
            timer.stop();
        }
        if(clip != null && clip.isOpen())
        {
            clip.stop();
        }
        sec = 0;
        soundlength = 0L;
        small_w = null;
        waveformImageCache.invalidate();
        if(words != null)
        {
            words.removeAll(words);
        }
        repaint();
    }

    public void setVideoPanel(VideoPanel videoPanel)
    {
        this.videoPanel = videoPanel;
    }

    public void stop()
    {
        if(timer != null)
        {
            timer.stop();
        }
        if(clip != null && clip.isOpen())
        {
            clip.stop();
        }
        isPaint = false;
        videoPanel.stop();
    }

    public void setPaint(int index)
    {
        isPaint = true;
        int sec = 0;
        if(index >= 4)
        {
            sec = words.get(index - 2).getCurrent_sec();
        }
        setScroll(sec);
        clip.setFramePosition(TimelineMath.timelineUnitToClipFrame(sec));
    }

	public int getDurition() {
		return clip != null && clip.isOpen() ? TimelineMath.clipFrameToTimelineUnit(clip.getFrameLength()) : 0;
	}

	public void repaintSeek() {
		
	}

    public static final class AudioLoadResult
    {
        private final Clip clip;
        private final float[] waveform;
        private final long soundlength;
        private final int preferredHeight;
        private final int screenAnh;
        private final int screenH;
        private final String durationText;

        private AudioLoadResult(
            Clip clip,
            float[] waveform,
            long soundlength,
            int preferredHeight,
            int screenAnh,
            int screenH,
            String durationText)
        {
            this.clip = clip;
            this.waveform = waveform;
            this.soundlength = soundlength;
            this.preferredHeight = preferredHeight;
            this.screenAnh = screenAnh;
            this.screenH = screenH;
            this.durationText = durationText;
        }

        static AudioLoadResult create(
            Clip clip,
            float[] waveform,
            long soundlength,
            int preferredHeight,
            int screenAnh,
            int screenH,
            String durationText)
        {
            return new AudioLoadResult(clip, waveform, soundlength, preferredHeight, screenAnh, screenH, durationText);
        }

        public String getDurationText()
        {
            return durationText;
        }
    }

}
