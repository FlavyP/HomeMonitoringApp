package thirdsem.flavy.homemonitoringapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class FlameActivity extends Activity {

    public final String TAG = "Main";
    private static final String TAGNFC = "NFC";

    private Button sendSms,dismiss, emrgcall;
    private String numb, msg;
    private String message;
    private TextView sendto, flame , messageTxv, statusView;
    private TextView txtmsg;
    Handler bluetoothIn;
    MediaPlayer mySound;
    private NfcAdapter mNfcAdapter;
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
        mySound = MediaPlayer.create(this, R.raw.fire);

        message = "";



        sendto = (TextView) findViewById(R.id.txtSendTo);
        txtmsg = (TextView) findViewById(R.id.txtmsg);
        flame  = (TextView) findViewById(R.id.FlameSensorTextView);
        statusView = (TextView) findViewById(R.id.statusView);
        messageTxv = (TextView) findViewById(R.id.textView5);
        sendto.setText(numb);
        txtmsg.setText(msg);

        //simple fire animation triggered by "red" input from arduino
        ImageView myView = (ImageView)findViewById(R.id.imageView);
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

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            //finish();
            return;

        }

        if (!mNfcAdapter.isEnabled())
        {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
        }

        //connectService();


        sendSms = (Button) findViewById(R.id.btnS);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(numb, msg);
                Toast.makeText(FlameActivity.this, "Send to " + numb, Toast.LENGTH_SHORT).show();
            }
        });


        dismiss = (Button) findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("9");    // Send "9" via Bluetooth to stop the alarm
                Toast.makeText(getBaseContext(), "Alarmed turned off", Toast.LENGTH_SHORT).show();
            }
        });

        emrgcall = (Button) findViewById(R.id.btnEmrgcall);
        emrgcall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent myIntent = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + numb;
                myIntent.setData(Uri.parse(phNum));
                startActivity(myIntent) ;

                //Toast.makeText(getBaseContext(), "911 help", Toast.LENGTH_SHORT).show();
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    //Toast.makeText(getBaseContext(), "Message " + recDataString, Toast.LENGTH_SHORT).show();
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    //Toast.makeText(FlameActivity.this, "End of line: " + endOfLineIndex, Toast.LENGTH_SHORT).show();
                    txtmsg.setText("Data Received = " + recDataString);
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string
                        message = dataInPrint;
                        txtmsg.setText("Data Received = /" + dataInPrint + "/");
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtmsg.append("\nString Length = " + String.valueOf(dataLength));
                        recDataString.delete(0, recDataString.length());
                        flame.setText("Flame sensor status: " + dataInPrint);
                        //this if statement might need to be inside the handler, wasnt sure where to place it xd
                        if ( dataInPrint.equals("red")) {
                            statusView.setText("Status:\nAlarm triggered!");
                            mAnimationSet.start();
                            startPlaying();
                        }
                        else {
                            statusView.setText("Status\nAlarm stopped!");
                            mAnimationSet.end();
                            // myView.setVisibility(View.INVISIBLE);
                            stopPlaying();
                        }

                    }
                }
            }
        };



        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    private void startPlaying() {
        stopPlaying();
        mySound = MediaPlayer.create(this, R.raw.fire);
        mySound.start();

    }

    private void stopPlaying() {
        if (mySound != null) {
            mySound.stop();
            mySound.release();
            mySound = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mNfcAdapter.isEnabled())
        {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
            //Toast.makeText(FlameActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
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
        //mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mNfcAdapter.disableForegroundDispatch(this);

        stopPlaying();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        //Something to close connection
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
                //Toast.makeText(getBaseContext(), "BT is already Enabled", Toast.LENGTH_LONG).show();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    private void sendSms(String phNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phNumber, null, message, null, null);
        //Toast.makeText(this, "Message sendSms", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        stopPlaying();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        //If tag received
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            //Toast.makeText(this, "NFC intent received", Toast.LENGTH_SHORT).show();
        }
        //Get the tag
        Parcelable[] parceables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parceables != null && parceables.length > 0)
        {
            readTextFromMessage((NdefMessage) parceables[0]);
        }
        else
        {
            //Toast.makeText(this, "No message", Toast.LENGTH_SHORT).show();
        }
    }

    private void readTextFromMessage(NdefMessage ndefMsg)
    {
        NdefRecord[] ndefRecs = ndefMsg.getRecords();
        if(ndefRecs != null && ndefRecs.length > 0)
        {
            NdefRecord ndefRec = ndefRecs[0];
            String tagContent = getTextFromNdefRecord(ndefRec);

            if(tagContent.equals("FIRE!"))
            {
                //Toast.makeText(this, tagContent, Toast.LENGTH_SHORT).show();
                Toast.makeText(FlameActivity.this, "Alarm dismissed", Toast.LENGTH_SHORT).show();
                mConnectedThread.write("9");
            }
        }
        else
        {
            //Toast.makeText(this, "No ndef Records found", Toast.LENGTH_SHORT).show();

        }
    }

    private String getTextFromNdefRecord(NdefRecord ndefRec)
    {
        String tagContent = null;
        try{
            byte[] payload = ndefRec.getPayload();
            String textEncoding = "UTF-8";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize +1, payload.length - languageSize-1, textEncoding);
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        //Toast.makeText(FlameActivity.this, tagContent, Toast.LENGTH_SHORT).show();
        return tagContent;
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
                //Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                //finish();

            }
        }
    }
}
