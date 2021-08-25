package com.voidoflimbo.chatapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.File;

public class AudioFileManager {

    //Recording output file
    private final static String AUDIO_RAW_FILENAME = "test.raw";
    private final static String AUDIO_WAV_FILENAME = "finalAudio.wav";
    /**
     * Determine whether there is an external storage device sdcard
     * returns true | false
     */
    public static boolean isSdcardExit(){
        return (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable());
    }

    /**
     * Get the original audio stream file path of the microphone input
     */
    public static String getRawFilePath(Context context){
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, AUDIO_RAW_FILENAME);
        return file.getPath();
    }

    /**
     * Get the encoded WAV format audio file path
     */
    public static String getWavFilePath(Context context){
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, AUDIO_WAV_FILENAME);
        return file.getPath();
    }

}
