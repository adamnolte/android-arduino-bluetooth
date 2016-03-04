package com.example.adam.android_arduino_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private int onOff;
    private int highLow;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module
    private static String macAddress = "00:15:FF:F2:19:5F";
    private final static int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if device bluetooth enabled if not exit
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            finish();
        }

        //If adapter off.. Attempt to turn on
        if (!btAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }


        onOff = 0;
        highLow = 1;

        //Set On/Off Listener
        Switch onOffSwitch = (Switch) findViewById(R.id.onoffswitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //If checked turn on LED else Turn Off
                if(isChecked){
                    setOn();
                }
                else{
                    setOff();
                }
            }
        });

        //Set High/Low Listener
        RadioGroup highLow = (RadioGroup) findViewById(R.id.highLowRadio);
        highLow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.highButton){
                    setHigh();
                }
                else{
                    setLow();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resume");

        //Connect To Bluetooth Device in serial mode
        BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(macAddress);
        try {
            btSocket = remoteDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
        }
        catch(IOException e){
            System.out.println("Error Creating Socket");
        }

        //Establish connection and data stream
        try {
            btSocket.connect();
            outStream = btSocket.getOutputStream();
        }
        catch(IOException e){
            System.out.println("Error Creating Output Stream");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("paused");

        //Flush the output stream
        if(outStream != null){
            try {
                outStream.flush();
            }
            catch(IOException e){
                System.out.println("Error flushing output stream");
            }
        }

        //Close the socket connection to device
        try{
            btSocket.close();
        }
        catch(IOException e){
            System.out.println("Error Closing bluetooth socket");
        }
    }

    public void setOn(){
        System.out.println("Sending On Signal");
        onOff = 1;
        sendSignal();
    }

    public void setOff(){
        System.out.println("Sending Off Signal");
        onOff = 0;
        sendSignal();
    }

    public void setHigh(){
        System.out.println("Setting High");
        highLow = 1;
        sendSignal();
    }

    public void setLow(){
        System.out.println("Setting Low");
        highLow = 0;
        sendSignal();
    }

    public void sendSignal(){

        byte[] sendBuffer = "0".getBytes();

        if(onOff == 0){
            System.out.println("sending 0");
            sendBuffer = "0".getBytes();
        }

        else if(highLow == 0){
            System.out.println("sending 1");
            sendBuffer = "1".getBytes();
        }

        else if(highLow == 1){
            System.out.println("sending 2");
            sendBuffer = "2".getBytes();
        }

        try{
            outStream.write(sendBuffer);
        }
        catch(IOException e){
            System.out.println("Error Sending");
        }
    }
}
