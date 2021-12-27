package com.example.consultion;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.InetAddress;


public class Call extends Activity {

    AudioGroup m_AudioGroup;
    AudioStream m_AudioStream;
    TextView srcIP , srcPort;
    StringBuilder localIp = new StringBuilder();
    ImageButton connect , disconnect ;
    boolean permissionGranted = false;
    final int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        showDialog();
        permissionGranted = ActivityCompat.checkSelfPermission(this , Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (!permissionGranted)
        {
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE);
        }
        if (!permissionGranted)
            return;
        srcIP = findViewById(R.id.dynamicIpText);
        srcPort = findViewById(R.id.dynamicPortText);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            AudioManager audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setMode(AudioManager.MODE_IN_COMMUNICATION);
            m_AudioGroup = new AudioGroup();
            m_AudioGroup.setMode(AudioGroup.MODE_NORMAL);
            m_AudioStream = new AudioStream(InetAddress.getByAddress(getLocalIPAddress()));
            int localPort = m_AudioStream.getLocalPort();
            srcPort.setText(localPort+"");
            srcIP.setText(localIp.deleteCharAt(localIp.length()-1).toString());
            m_AudioStream.setCodec(AudioCodec.PCMU);
            m_AudioStream.setMode(RtpStream.MODE_NORMAL);
            connect = findViewById(R.id.connectBtn);
            disconnect = findViewById(R.id.disconnectBtn);
            disconnect.setEnabled(false);
            connect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String remoteAddress = ((EditText)findViewById(R.id.destIpText)).getText().toString();
                    String remotePort = ((EditText)findViewById(R.id.destPortText)).getText().toString();
                    try {
                        try {
                            m_AudioStream.associate(InetAddress.getByName(remoteAddress), Integer.parseInt(remotePort));
                            connect.setEnabled(false);
                            disconnect.setEnabled(true);
                            m_AudioStream.join(m_AudioGroup);
                        } catch (Exception e) {
                            Toast.makeText(Call.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(Call.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            disconnect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    m_AudioStream.release();
                }
            });

        } catch (Exception e) {
            Log.e("----------------------", e.toString());
            e.printStackTrace();
        }
    }

    private byte[] getLocalIPAddress() {
        byte[] bytes = null;

        try {
            // get the string ip
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            // convert to bytes
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(ip);
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            bytes = new byte[0];
            if (inetAddress != null) {
                bytes = inetAddress.getAddress();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        for (byte b : bytes)
            localIp.append((b & 0xFF) + ".");
        return bytes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            permissionGranted = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
        }
    }

    public void showDialog()
    {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Call.this);
        alertDialogBuilder.setTitle("Help");
        alertDialogBuilder.setIcon(R.drawable.help);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.rounded_edit_text,null));
        }
        alertDialogBuilder.setMessage("1- Both sides should make sure that they gave the application the permission is asked for.\n" +
                "2- Both sides should be in the same network. \n" +
                "3- You should swap your local ip and ports appearing on the top of your screen.\n" +
                "4- Both of you should click the green button to establish the connection.\n" +
                "5- When any one needs to terminate they should simply click on the red button.");
        alertDialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}