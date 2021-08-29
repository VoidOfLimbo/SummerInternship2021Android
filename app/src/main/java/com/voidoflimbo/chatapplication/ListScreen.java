package com.voidoflimbo.chatapplication;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ListScreen extends AppCompatActivity {

    StorageReference storageReference, downloadFileName;
    String filename,filepath;

    Button playAudio,downloadAudio;
    MediaPlayer mediaPlayer;
    NetworkInfo activeNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        downloadAudio = findViewById(R.id.button_download_audio);
        playAudio = findViewById(R.id.button_play_downloaded_audio);
        playAudio.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference("Audio");
        storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    // we have only one audio for all the time so only one for now
                    for (StorageReference item : listResult.getItems()) {
                        filename = item.getName();
                    }
                });
    }

    public void downloadFromFirebase(View view) {
        if (!isNetworkAvailable()) {
            AlertDialog.Builder customAlert = new AlertDialog.Builder( this);
            customAlert.setMessage("Please enable internet in order to download audio")
                    .setCancelable(true)
                    .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                    .show();
            System.out.println("i got called");
        } else {
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                downloadFileName = storageReference.child(filename);

                System.out.println(filename);
                System.out.println(downloadFileName);

                removePreviousFile();

                downloadFileName.getDownloadUrl().addOnSuccessListener(uri -> {
                    System.out.println(uri);
                    DownloadManager downloadManager = (DownloadManager) getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_MUSIC, filename);

                    downloadManager.enqueue(request);
                }).addOnCompleteListener(task -> playAudio.setVisibility(View.VISIBLE));
            } else {
                Toast.makeText(this, "Internet not available please try again",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removePreviousFile() {
        ContextWrapper contextWrapper = new ContextWrapper(this);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, filename);
        filepath = file.getPath();
        if(file.exists()) file.delete();
    }

    public void playAudio(View view) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}