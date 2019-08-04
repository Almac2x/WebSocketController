package com.example.tutorialspoint7.androidsocketwiththreading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button Start,Stop,SendMessage;
    TextView SensorView,ComputerMessage;
    EditText MessageText,IpAddress,Port;
    Sensor Accelerometer;
    SensorManager sensorManager;
    private static final String TAG = "MainActivity";

    Socket socket;
    DataInputStream input;
    DataOutputStream out;

    Socket socket2;
    DataInputStream input2;
    DataOutputStream out2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Start = (Button) findViewById(R.id.start_button);
        Stop = (Button) findViewById(R.id.stop_button);
        SendMessage = (Button)findViewById(R.id.SendMessage);
        SensorView = (TextView) findViewById(R.id.SensorView);
        ComputerMessage = (TextView) findViewById(R.id.computerMessage);
        MessageText = (EditText)findViewById(R.id.MessageText);
        IpAddress = (EditText)findViewById(R.id.IpAddress);
        Port = (EditText)findViewById(R.id.Port);

        IpAddress.setText("192.168.43.151");
        Port.setText("2000");
        SensorView.setText("Sensor Value");


        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void StartSensor(View view){

        sensorManager.registerListener(sensorEventListener,Accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "StartSensor: "+"Started");
    }

    public void StopSensor(View view){

        sensorManager.unregisterListener(sensorEventListener,Accelerometer);
        Log.d(TAG, "StartSensor: "+"Stop");
        SensorView.setText("Sensor Value");
    }

    public void Connect(View view){
        RunSocket runSocket = new RunSocket(IpAddress.getText().toString(),Port.getText().toString());
        new Thread(runSocket).start();//Y
        RunSocket2 runSocket2 = new RunSocket2("192.168.43.151","5000");
        new Thread(runSocket2).start();//X

    }
    public void Disconnect (View view) throws IOException {
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {

                  out.writeUTF("Over");
                  Log.d(TAG, "Connection Status: " + "Disconnected");
                  socket.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }).start();

    }

    public  void SendMessage(View view){
      
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    out.writeUTF(MessageText.getText().toString());
                    Log.d(TAG, "Message: "+MessageText.getText().toString());
                } catch (IOException e) {
                    Log.d(TAG, "Error: "+"Message Send Failed");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {

            SensorView.setText("X: "+Float.toString(sensorEvent.values[0])+"\n"+"Y: "+Float.toString(sensorEvent.values[1])+"\n"+"Z: "+Float.toString(sensorEvent.values[2]));

            if(socket.isConnected()){

                Thread thread = new Thread(new Runi(sensorEvent));
                thread.setPriority(Thread.MAX_PRIORITY);

                thread.start();

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {



        }
    };

    //Sockets and Handlers

     public class RunSocket implements Runnable{
         String IpAddress,Port;

         RunSocket(String IpAddress,String Port){

              this.IpAddress = IpAddress;
              this.Port = Port;
         }


         @Override
         public void run() {



             try
             {
                 socket = new Socket(IpAddress, Integer.parseInt(Port));
                 if(socket.isConnected()){
                     Log.d(TAG, "Status: "+ "Connected");
                 }

                 // takes input from terminal
                 input  = new DataInputStream( new BufferedInputStream(socket.getInputStream()));



                 // sends output to the socket
                 out    = new DataOutputStream(socket.getOutputStream());


             }
             catch(UnknownHostException u)
             {
                 Log.d(TAG, "Host: "+u);
             }
             catch(IOException i)
             {
                 Log.d(TAG, "Input: "+i);
             }

         }
     }

    public class RunSocket2 implements Runnable{
        String IpAddress,Port;

        RunSocket2(String IpAddress,String Port){

            this.IpAddress = IpAddress;
            this.Port = Port;
        }


        @Override
        public void run() {



            try
            {
                socket = new Socket(IpAddress, Integer.parseInt(Port));
                if(socket.isConnected()){
                    Log.d(TAG, "Status: "+ "Connected");
                }

                // takes input from terminal
                input2  = new DataInputStream( new BufferedInputStream(socket.getInputStream()));



                // sends output to the socket
                out2  = new DataOutputStream(socket.getOutputStream());


            }
            catch(UnknownHostException u)
            {
                Log.d(TAG, "Host: "+u);
            }
            catch(IOException i)
            {
                Log.d(TAG, "Input: "+i);
            }

        }
    }

    public class Runi implements Runnable{
        SensorEvent sensorEvent;

        Runi(SensorEvent event){

            sensorEvent = event;

        }


        @Override
        public void run() {

                try {

                    //Y=1
                    //x==0
                    out.writeInt((int)sensorEvent.values[ 1]);// Y Value
                    out2.writeInt((int)sensorEvent.values[0]);// X Value

                    //Log.d(TAG, "Connected: "+ Float.toString(sensorEvent.values[0]));
                    Log.d(TAG, "Connected: "+  sensorEvent.values[1]);
                } catch (IOException e) {
                    Log.d(TAG, "Error: "+"Sensor Send Failed");
                    e.printStackTrace();
                }
            }


    }




}
