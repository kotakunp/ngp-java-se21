package karaoke.app.main.source;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class WaveformImageCache {

    private BufferedImage waveformImage;
    private int waveformImageWidth = -1;

    public BufferedImage getOrCreate(int width, float[] waveform) {
        if(width <= 0 || waveform == null) {
            return null;
        }
        if(waveformImage != null && waveformImageWidth == width && waveformImage.getHeight() == waveform.length) {
            return waveformImage;
        }

        waveformImage = new BufferedImage(width, Math.max(1, waveform.length), BufferedImage.TYPE_INT_ARGB);
        waveformImageWidth = width;
        java.awt.Graphics2D imageGraphics = waveformImage.createGraphics();
        imageGraphics.setColor(Color.white);
        for(int i = 0; i < waveform.length; i++) {
            int w = (int)(waveform[i] * 300F);
            int wi = (width - w) / 3;
            int zuruu = (width - w) % 3;
            int wi2 = (wi + w) - zuruu;
            if(w <= 90) {
                imageGraphics.drawLine(wi, i, wi2, i);
            } else {
                imageGraphics.drawLine(wi, i, 90, i);
            }
        }
        imageGraphics.dispose();
        return waveformImage;
    }

    public void invalidate() {
        waveformImage = null;
        waveformImageWidth = -1;
    }
}
