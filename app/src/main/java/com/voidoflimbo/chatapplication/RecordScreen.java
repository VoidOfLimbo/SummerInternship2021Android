package com.voidoflimbo.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class RecordScreen extends AppCompatActivity {

    private boolean audioRecorded = false;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    ImageButton recordButton, playButton;
    Button recordNew, sendAudio, openDownloader;
    TextView recordingStatus, gpSimulationStatus, orSeparator;
    String finalFilename;

    MediaPlayer mediaPlayer;

    WAVAudioRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_screen);

        recordButton = findViewById(R.id.buttonRecordAudio);
        playButton = findViewById(R.id.buttonPlayAudio);
        recordNew = findViewById(R.id.buttonRecordNew);
        sendAudio = findViewById(R.id.buttonSendAudio);
        openDownloader = findViewById(R.id.buttonOpenDownloader);
        recordingStatus = findViewById(R.id.recordingStatus);
        gpSimulationStatus = findViewById(R.id.gpSimulationLabel);
        orSeparator = findViewById(R.id.orSeparator);

        recorder = new WAVAudioRecorder();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        recordButton.setImageResource(R.drawable.ic_baseline_record_voice_over_24);

        playButton.setVisibility(View.INVISIBLE);
        sendAudio.setVisibility(View.INVISIBLE);
        recordNew.setVisibility(View.INVISIBLE);

    }

    public void micPressed(View view) {
        if(!audioRecorded){
            // record audio
            recordingStatus.setText(R.string.recording_started);
            recorder.startRecording(getApplicationContext());
            audioRecorded = true;
            openDownloader.setVisibility(View.INVISIBLE);
            gpSimulationStatus.setVisibility(View.INVISIBLE);
            recordButton.setImageResource(R.drawable.ic_baseline_stop_24);
        } else {
            // stop recording audio
            recordingStatus.setText(R.string.recording_stopped);
            playButton.setVisibility(View.VISIBLE);
            recordButton.setVisibility(View.INVISIBLE);
            recorder.stopRecording();
            prepareAudio();
            sendAudio.setVisibility(View.VISIBLE);
            recordNew.setVisibility(View.VISIBLE);
        }
        orSeparator.setVisibility(View.INVISIBLE);
        System.out.println("audioRecorded: " + audioRecorded);
    }

    public void playPressed(View view) {
        recordingStatus.setText(R.string.testingAudio);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            System.out.println("Audio Paused");
        }else{
            playButton.setImageResource(R.drawable.ic_baseline_pause_24);
            System.out.println("Audio Playing");
            mediaPlayer.start();
        }
        mediaPlayer.setOnCompletionListener(mp -> playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24));
    }

    public void recordAgain(View view) {
        playButton.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.VISIBLE);
        audioRecorded = false;
        recordingStatus.setText(R.string.recording_notification);
        sendAudio.setVisibility(View.INVISIBLE);
        recordNew.setVisibility(View.INVISIBLE);
        recordButton.setImageResource(R.drawable.ic_baseline_record_voice_over_24);

    }

    private void prepareAudio(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        try {
            mediaPlayer.setDataSource(recorder.getWAVFileAddress());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(mp -> {
            Toast.makeText(getApplicationContext(),"Audio Saved",Toast.LENGTH_SHORT).show();
            recordingStatus.setText(R.string.recording_stopped);
        });
    }

    public void uploadAudioToFirebase(View view) {

        progressDialog.setMessage("Uploading Audio");

        progressDialog.show();

        String filename = "User_Audio_";
        filename += Calendar.getInstance().get(Calendar.YEAR) + "_" +
                    Calendar.getInstance().get(Calendar.MONTH) + "_" +
                    Calendar.getInstance().get(Calendar.DATE) + "_" +
                    Calendar.getInstance().get(Calendar.HOUR) + "_" +
                    Calendar.getInstance().get(Calendar.MINUTE) + "_" +
                    Calendar.getInstance().get(Calendar.SECOND) + ".wav";
        System.out.println(filename);

        finalFilename = "voila.wav";
//        String finalFilename = filename;  // it will create a separate files in server.

        StorageReference filepath = storageReference.child("Audio").child(finalFilename);
        Uri uri = Uri.fromFile(new File(recorder.getWAVFileAddress()));


        filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            Intent listScreenActivity = new Intent(getApplicationContext(), ListScreen.class);
            startActivity(listScreenActivity);
        });
    }

    public void openListScreen(View view) {
        orSeparator.setVisibility(View.INVISIBLE);
        Intent listScreenActivity = new Intent(getApplicationContext(), ListScreen.class);
        startActivity(listScreenActivity);
    }
}