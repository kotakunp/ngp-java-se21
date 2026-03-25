package karaoke.app.main.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioWaveformLoader {

    private static final int FRAMES_PER_WAVE_BIN = 1000;

    public WavPlayer.AudioLoadResult load(File audioFile, java.awt.Dimension size)
        throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        float[] waveform = buildWaveform(audioFile);
        Clip loadedClip = AudioSystem.getClip();
        try(AudioInputStream input = AudioSystem.getAudioInputStream(audioFile)) {
            loadedClip.open(input);
        }

        int preferredHeight = Math.max(10, waveform.length + 10);
        int nextScreenAnh = (int)(size.getHeight() - 200D);
        String durationText = (new StringBuilder(String.valueOf(
            loadedClip.getMicrosecondLength() / 1000L / 1000L / 60L)))
            .append(" : ")
            .append((loadedClip.getMicrosecondLength() / 1000L / 1000L) % 60L)
            .toString();

        return WavPlayer.AudioLoadResult.create(
            loadedClip,
            waveform,
            loadedClip.getFrameLength(),
            preferredHeight,
            nextScreenAnh,
            nextScreenAnh - 100,
            durationText
        );
    }

    private float[] buildWaveform(File audioFile)
        throws IOException, UnsupportedAudioFileException
    {
        try(AudioInputStream sourceStream = AudioSystem.getAudioInputStream(audioFile)) {
            AudioInputStream pcmStream = createPcmStream(sourceStream);
            try(AudioInputStream input = pcmStream) {
                AudioFormat format = input.getFormat();
                int frameSize = format.getFrameSize();
                int channels = Math.max(1, format.getChannels());
                int sampleBytes = Math.max(1, format.getSampleSizeInBits() / 8);
                boolean bigEndian = format.isBigEndian();
                boolean signed = AudioFormat.Encoding.PCM_SIGNED.equals(format.getEncoding());
                byte[] buffer = new byte[frameSize * 4096];
                ArrayList<Float> peaks = new ArrayList<Float>();
                int bytesRead;
                int framesInBin = 0;
                float peak = 0F;
                while((bytesRead = input.read(buffer)) != -1) {
                    int completeBytes = bytesRead - (bytesRead % frameSize);
                    for(int offset = 0; offset < completeBytes; offset += frameSize) {
                        float framePeak = 0F;
                        for(int channel = 0; channel < channels; channel++) {
                            int sampleOffset = offset + (channel * sampleBytes);
                            framePeak = Math.max(
                                framePeak,
                                Math.abs(decodeSample(buffer, sampleOffset, sampleBytes, bigEndian, signed))
                            );
                        }
                        peak = Math.max(peak, framePeak);
                        framesInBin++;
                        if(framesInBin >= FRAMES_PER_WAVE_BIN) {
                            peaks.add(Float.valueOf(peak));
                            framesInBin = 0;
                            peak = 0F;
                        }
                    }
                }
                if(framesInBin > 0) {
                    peaks.add(Float.valueOf(peak));
                }
                if(peaks.isEmpty()) {
                    peaks.add(Float.valueOf(0F));
                }
                float[] waveform = new float[peaks.size()];
                for(int i = 0; i < peaks.size(); i++) {
                    waveform[i] = peaks.get(i).floatValue();
                }
                return waveform;
            }
        }
    }

    private AudioInputStream createPcmStream(AudioInputStream sourceStream) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if(AudioFormat.Encoding.PCM_SIGNED.equals(sourceFormat.getEncoding())
            && sourceFormat.getSampleSizeInBits() == 16) {
            return sourceStream;
        }

        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            sourceFormat.getSampleRate(),
            16,
            sourceFormat.getChannels(),
            sourceFormat.getChannels() * 2,
            sourceFormat.getFrameRate(),
            false
        );
        return AudioSystem.getAudioInputStream(decodedFormat, sourceStream);
    }

    private float decodeSample(byte[] buffer, int offset, int sampleBytes, boolean bigEndian, boolean signed) {
        if(sampleBytes <= 0) {
            return 0F;
        }

        int sample;
        if(sampleBytes == 1) {
            sample = buffer[offset];
            if(!signed) {
                sample = (buffer[offset] & 0xFF) - 128;
            }
            return sample / 128F;
        }

        int low;
        int high;
        if(bigEndian) {
            high = buffer[offset];
            low = buffer[offset + 1] & 0xFF;
        } else {
            low = buffer[offset] & 0xFF;
            high = buffer[offset + 1];
        }
        sample = (high << 8) | low;
        return sample / 32768F;
    }
}
