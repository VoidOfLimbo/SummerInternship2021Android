package com.voidoflimbo.chatapplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WAVConverter {

    // will store the data that will be used to make the raw data into wav format
    private byte[] header, rawData;

    //Sampling frequency
    //44100 is the current standard, but some devices still support 22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 8000;  // 44.1KHz,Commonly used frequency

    // will return the byte array of header
    public byte[] getHeader(){ return this.header;}

    public byte[] getRawData(){ return this.rawData; }

    WAVConverter(){}

    // Here to get a playable audio file
    public void convertPCMToWaveFile(String inFilename, String outFilename, int bufferSizeInBytes) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0, totalDataLen = 0;
        long longSampleRate = AUDIO_SAMPLE_RATE;
        int channels = 2, bitsPerSample = 16;       // 1 = mono || 2 = stereo
        long byteRate = (bitsPerSample * longSampleRate * channels) / 8;
        rawData = new byte[bufferSizeInBytes];

        // read from raw data and write it to wav file
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 44;

            // add header to the wav file before audio data
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            // read data from one file and write it on top of another file.
            while (in.read(rawData) != -1) {
                out.write(rawData);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here is a header information. Insert this information to get a file that can be played.
     * The reason for creating 44 byte header was referenced from
     * Reference https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent
     * http://www.topherlee.com/software/pcm-tut-wavformat.html
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        header = new byte[44];
        // RIFF / WAVE header
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' '; // 'fmt ' chunk
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0; header[18] = 0; header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
        out.flush();
    }
}
