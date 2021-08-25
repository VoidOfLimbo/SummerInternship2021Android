package com.voidoflimbo.chatapplication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WAVAudioRecorder {

    // Buffer size in bytes
    private int bufferSizeInBytes = 0;

    private byte[] audioData;

    private boolean isRecord = false;
    private AudioRecord recorder;

    WAVAudioRecorder(){}

    WAVConverter converter = new WAVConverter();

    // Raw audio data file from microphone & Playable audio file
    private String RawFileAddress = "", WaveFileAddress = "";

    public String getWAVFileAddress() { return this.WaveFileAddress; }

    public void startRecording(Context context) {
        if (AudioFileManager.isSdcardExit()) {
            if (!isRecord) {
                if (recorder == null) {
                    installRecorder(context);
                }

                recorder.startRecording();
                // Let the recording status be true
                isRecord = true;
                //Start the audio file writing thread
                new Thread(new AudioRecordThread()).start();

            }
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            System.out.println("stopped Recording");
            isRecord = false;//Stop file writing
            recorder.stop();
            recorder.release();//Release resources
            recorder = null;
        } else {
            System.out.println("nothing is being recorded");
        }
    }

    private void installRecorder(Context context) {
        // create both raw and WAV files
        prepareFiles(context);

        // Get the size of the buffer in bytes
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                WAVConverter.AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT );

        System.out.println("buffer size: " + bufferSizeInBytes);

        // Create AudioRecord object
        recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                WAVConverter.AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes );
    }

    private void prepareFiles(Context context) {
        RawFileAddress = AudioFileManager.getRawFilePath(context);
        WaveFileAddress = AudioFileManager.getWavFilePath(context);
    }

    private class AudioRecordThread implements Runnable{
        @Override
        public void run() {
            writeDataTOFile(); // Write raw data to the file
            converter.convertPCMToWaveFile(RawFileAddress, WaveFileAddress,bufferSizeInBytes); // Add header files to raw data
        }
    }

    /**
     * The data is written into the file here, but it cannot be played, because the audio obtained by AudioRecord is the original raw audio.
     */
    private void writeDataTOFile() {
        // new A byte array is used to store some byte data, the size is the buffer size
        audioData = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readSize;
        try {
            File file = new File(RawFileAddress);
            if (file.exists()) { file.delete(); }
            fos = new FileOutputStream(file);// Create a file with accessible bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecord) {
            readSize = recorder.read(audioData, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos!=null) {
                try {
                    fos.write(audioData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if(fos != null)
                fos.close();// Close write stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

