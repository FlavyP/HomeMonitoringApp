package thirdsem.flavy.homemonitoringapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener{

    private Button connectButton;
    private Button disableButton;
    private Button enableButton;

    private TextView bluetoothStatus;
    private TextView connectStatus;

    private ImageView bluetoothPic;
    private ImageView arduinoPic;

    private BluetoothAdapter BA;

    private Bluetooth bt;

    Set<BluetoothDevice> pairedDevices;

    public final String TAG = "Main";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initializeComponents();
        checkForBluetooth();
    }

    private void initializeComponents() {
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(this);
        disableButton = (Button) findViewById(R.id.disableButton);
        disableButton.setOnClickListener(this);
        enableButton = (Button) findViewById(R.id.enableButton);
        enableButton.setOnClickListener(this);


        bluetoothPic = (ImageView) findViewById(R.id.picBluetooth);
        arduinoPic = (ImageView) findViewById(R.id.picArduino);

        bluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        connectStatus = (TextView) findViewById(R.id.connectStatus);

        BA = BluetoothAdapter.getDefaultAdapter();

        bt = new Bluetooth(this, mHandler);
        pairedDevices = BA.getBondedDevices();
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
        }
    };

    /* Checking if the phone has or not bluetooth */
    private void checkForBluetooth()
    {
        ImageView bluetoothPic = (ImageView) findViewById(R.id.picBluetooth);
        if(BA != null) {
            Toast.makeText(BluetoothActivity.this, "Phone supports BT", Toast.LENGTH_SHORT).show();
            if (!BA.isEnabled()) {
                bluetoothStatus.setText("Bluetooth status: bluetooth not enabled");
                bluetoothPic.setImageResource(R.drawable.nobluetooth1v2);
            } else {
                bluetoothStatus.setText("Bluetooth status: bluetooth already enabled.");
                bluetoothPic.setImageResource(R.drawable.bluetooth1);
            }
        }
        else
            Toast.makeText(BluetoothActivity.this, "Phone does not support BT", Toast.LENGTH_SHORT).show();
    }

    /* Enabling bluetooth */
    private void enableBluetooth()
    {
        if (!BA.isEnabled())
        {
            bluetoothPic.setImageResource(R.drawable.nobluetooth1v2);
            bluetoothStatus.setText("Bluetooth status: bluetooth not enabled");
            Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBluetoothOn, 0);
            bluetoothStatus.setText("Bluetooth status: bluetooth activated!");
            bluetoothPic.setImageResource(R.drawable.bluetooth1);
        }
        else
        {
            bluetoothStatus.setText("Bluetooth status: bluetooth already enabled.");
            bluetoothPic.setImageResource(R.drawable.bluetooth1);
        }
    }

    /* Connecting the phone to the arduino board */
    private void connectDevice () {
        try {
            if (BA.isEnabled()) {
                bt.connectDevice("HC-06");
                if(bt.getConnection())
                {
                    Log.d(TAG, "Btservice started - listening");
                    connectStatus.setText("Connect status: connected to HC-06!");
                    arduinoPic.setImageResource(R.drawable.arduino);
                }
                else
                {
                    arduinoPic.setImageResource(R.drawable.noarduinov2);
                    connectStatus.setText("Connect status: try again");
                }
            } else {
                bluetoothPic.setImageResource(R.drawable.bluetooth1);
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                Toast.makeText(BluetoothActivity.this, "Enable BT in order to connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            bluetoothPic.setImageResource(R.drawable.bluetooth1);
            Log.e(TAG, "Unable to start bt ", e);
            Toast.makeText(BluetoothActivity.this, "Unable to connect " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.enableButton:
                enableBluetooth();
                break;
            case R.id.connectButton:
                connectDevice();
                break;
            case R.id.disableButton:
                BA.disable();
                bt.stop();
                bluetoothPic.setImageResource(R.drawable.nobluetooth1v2);
                arduinoPic.setImageResource(R.drawable.noarduinov2);
                bluetoothStatus.setText("Bluetooth status: not enabled.");
                connectStatus.setText("Connect status: disconnected.");
                break;
        }
    }
}
