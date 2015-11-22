package thirdsem.flavy.homemonitoringapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FlameActivity extends Activity {

    public final String TAG = "Main";
    private Button sendSms,sendEmail, dismiss, emrgcall;
    private Bluetooth bt;
    private String numb, msg;
    private String mail;
    private TextView sendto, flame , messageTxv;
    private TextView txtmsg;
    Handler bluetoothIn;
    MediaPlayer mySound;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address = "98:D3:31:FC:15:A5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame);

        numb = PreferenceManager.getDefaultSharedPreferences(this).getString("cPhoneNumber", "");
        msg = PreferenceManager.getDefaultSharedPreferences(this).getString("message", "");
        mail = PreferenceManager.getDefaultSharedPreferences(this).getString("cEmail", "");
        mySound = MediaPlayer.create(this, R.raw.fire);



        sendto = (TextView) findViewById(R.id.txtSendTo);
        txtmsg = (TextView) findViewById(R.id.txtmsg);
        flame  = (TextView) findViewById(R.id.FlameSensorTextView);
        messageTxv = (TextView) findViewById(R.id.textView5);
        sendto.setText(numb);
        txtmsg.setText(msg);

        //simple fire animation triggered by "red" input from arduino
        ImageView myView = (ImageView)findViewById(R.id.imageView);
        myView.setBackgroundResource(R.drawable.fire4);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(myView, "alpha",  1f, .3f);
        fadeOut.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(myView, "alpha", .3f, 1f);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });


        sendSms = (Button) findViewById(R.id.btnS);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(numb, msg);
                Toast.makeText(FlameActivity.this, "Send to " + numb, Toast.LENGTH_SHORT).show();
            }
        });

        sendEmail = (Button) findViewById(R.id.btnEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(mail, msg);
                Toast.makeText(FlameActivity.this, "mail: " + mail, Toast.LENGTH_LONG).show();
            }
        });


        dismiss = (Button) findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("9");    // Send "9" via Bluetooth to stop the alarm
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        emrgcall = (Button) findViewById(R.id.btnEmrgcall);
        emrgcall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent myIntent = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + "1234567890";
                myIntent.setData(Uri.parse(phNum));
                startActivity( myIntent ) ;

                Toast.makeText(getBaseContext(), "911 help", Toast.LENGTH_SHORT).show();
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    //Toast.makeText(getBaseContext(), "Message " + recDataString, Toast.LENGTH_SHORT).show();
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    txtmsg.setText("Data Received = " + recDataString);
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string
                        txtmsg.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtmsg.setText("String Length = " + String.valueOf(dataLength));
                        recDataString.delete(0, recDataString.length());

                        flame.setText(dataInPrint);



                    }
                }
            }
        };


        //this if statement might need to be inside the handler, wasnt sure where to place it xd
        if ( flame.getText().equals("red")) {
            messageTxv.setText("alarm, alarm, alarm");
            mAnimationSet.start();
            mySound.start();
        } else {
            messageTxv.setText("alarm is off , ALL OK");
            mAnimationSet.end();
            // myView.setVisibility(View.INVISIBLE);
            mySound.release();
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();




        //  bt = new Bluetooth(this, mHandler);  //works with the handler bellow



    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
            Toast.makeText(FlameActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mySound.release();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "BT is already Enabled", Toast.LENGTH_LONG).show();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    private void sendSms(String phNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phNumber,null, message, null, null);
        Toast.makeText(this, "Message sendSms", Toast.LENGTH_LONG).show();
    }

    private void sendEmail(String mailTo, String message)
    {
        Intent implicitIntent = new Intent(Intent.ACTION_SEND);
        implicitIntent.setType("message/rfc822");
        implicitIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mailTo});
        implicitIntent.putExtra(Intent.EXTRA_SUBJECT, "HELP!");
        implicitIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(implicitIntent);
    }




    //stop the upper handler to test this one and unlock bt. in onCreate

    //    private final Handler mHandler = new Handler() {
    //        @Override
    //        public void handleMessage(Message msg) {
    //            switch (msg.what) {
    //                case Bluetooth.MESSAGE_STATE_CHANGE:
    //                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
    //                    break;
    //                case Bluetooth.MESSAGE_WRITE:
    //                    Log.d(TAG, "MESSAGE_WRITE ");
    //                    break;
    //                case Bluetooth.MESSAGE_READ:
    //                    Log.d(TAG, "MESSAGE_READ ");
    //                    if (msg.what == handlerState) {                                     //if message is what we want
    //                        String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
    //                        recDataString.append(readMessage);                                      //keep appending to string until ~
    //                        //Toast.makeText(getBaseContext(), "Message " + recDataString, Toast.LENGTH_SHORT).show();
    //                        int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
    //                        txtmsg.setText("Data Received = " + recDataString);
    //                        if (endOfLineIndex > 0) {                                           // make sure there data before ~
    //                            String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string
    //                            txtmsg.setText("Data Received = " + dataInPrint);
    //                            int dataLength = dataInPrint.length();                          //get length of data received
    //                            txtmsg.setText("String Length = " + String.valueOf(dataLength));
    //                            recDataString.delete(0, recDataString.length());
    //
    //                            flame.setText(dataInPrint);
    //
    //
    //                    break;
    //                case Bluetooth.MESSAGE_DEVICE_NAME:
    //                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
    //                    break;
    //                case Bluetooth.MESSAGE_TOAST:
    //                    Log.d(TAG, "MESSAGE_TOAST " + msg);
    //                    break;
    //            }
    //        }
    //    };




    @Override
    protected void onDestroy(){
        super.onDestroy();

        mySound.release();
    }



    public void playSound(View view){

        mySound.start();

    }








    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                //finish();

            }
        }
    }
}
