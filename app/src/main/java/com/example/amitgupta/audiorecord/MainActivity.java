package com.example.amitgupta.audiorecord;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Button startRec,stopRec,playBack;
    Boolean isRecording = false;    // To check whether AUDIO is being RECORED or NOT.

   /* short[] audioData;  // Array of short that stores the Audio Data
    int minBufferSize;  // MAX size of Buffer needed to store the AUDIO*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);

        // is set to like INVISIBLE , not really INVISIBLE
        stopRec.setEnabled(false);
        playBack.setEnabled(false);

        // For RECORDING the Audio
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

        // For STOPPING the RECORDING
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                startRec.setEnabled(true);
                stopRec.setEnabled(false);
                playBack.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();

            }
        });

        // For Playing the RECORDED SOUND
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

            }
        });
    }// End of onCreate()

    // Inside the recordThread thread
    public  void startRecording(){

        File file = new File(Environment.getExternalStorageDirectory(),"Sound.txt");    //Does not mean that the file is text file,
        // .txt mean something here
        File file1 = new File(Environment.getExternalStorageDirectory(),"Store.txt");

        try {

           // file.createNewFile();
            BufferedWriter bW = new BufferedWriter(new FileWriter(file1));
           /* OutputStream outputStream = new FileOutputStream(file); // MAY BE it is using CONCEPT of WRAPPER class.
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);*/

            int minBufferSize  =AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT); //Returns the minimum
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
                    //dataOutputStream.writeInt(temp);
                    System.out.println(audioData[i] + " " + temp);
                }




            }
            audioRecord.stop();
            //dataOutputStream.close();
            bW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//End of startRecording()

    public void playRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "sound.txt");
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            System.out.println(bufferSizeInBytes);
            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//End of playRecord()
}// End of MainActivity
