package com.example.amitgupta.audiorecord;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Button startRec,stopRec,playBack;
    Boolean isRecording = false;    // To check whether AUDIO is being RECORED or NOT.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);

        // is set to like INVISIBLE
        stopRec.setEnabled(false);
        playBack.setEnabled(false);

        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creating a Thread to Record the Audio . ???? WHY use Thread ????
                Thread recordThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        isRecording = true;
                        startRecording();

                    }
                });
                recordThread.start();
                startRec.setEnabled(false);
                stopRec.setEnabled(true);

                Toast.makeText(getApplicationContext(),"RECORDING STARTED",Toast.LENGTH_LONG).show();
            }
        });
    }

    // Inside the recordThread thread
    public  void startRecording(){

        File file = new File(Environment.getExternalStorageDirectory(),"Sound.txt");    //Does not mean that the file is text file,
        // .txt mean something here
        File file1 = new File(Environment.getExternalStorageDirectory(),"Store.txt");

        try {

            file.createNewFile();
            BufferedWriter bW = new BufferedWriter(new FileWriter(file1));
            OutputStream outputStream = new FileOutputStream(file); // MAY BE it is using CONCEPT of WRAPPER class.
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize  = (short) AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT); //Returns the minimum
            // buffer size required for the successful creation of an AudioRecord object, in byte units.

            short[] audioData = new short[minBufferSize];

            String[] audioString = new String[audioData.length];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);
            audioRecord.startRecording();

            while(isRecording){

                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numberOfShort; i++){
                    //audioString[i] = String.valueOf(audioData[i]);
                    //dataOutputStream.writeBytes(audioString[i]);
                    int temp = audioData[i];
                    bW.write(temp + "\n");
                    dataOutputStream.writeInt(temp);
                    System.out.println(audioData[i]);
                }

            }
            audioRecord.stop();
            dataOutputStream.close();
            bW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
