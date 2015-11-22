package thirdsem.flavy.homemonitoringapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "artuMSG";
    private ImageButton butCon, butLights,butFlame,butAbout;

    private BluetoothAdapter BA;
    private Bluetooth bt;

    private View mainLayout;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        initializeComponents();
        bluetoothSnackBar();
    }

    private void initializeComponents()
    {
        butCon = (ImageButton)findViewById(R.id.buttonCon);
        butCon.setOnClickListener(this);
        butLights = (ImageButton)findViewById(R.id.buttonLights);
        butLights.setOnClickListener(this);
        butFlame = (ImageButton)findViewById(R.id.buttonFlame);
        butFlame.setOnClickListener(this);
        butAbout = (ImageButton)findViewById(R.id.buttonAbout);
        butAbout.setOnClickListener(this);

        mainLayout = findViewById(R.id.mainLayout);
        BA = BluetoothAdapter.getDefaultAdapter();
        bt = new Bluetooth(this, mHandler);
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

    private void bluetoothSnackBar()
    {
        if(!BA.isEnabled())
        {
            Snackbar.make(mainLayout, "Bluetooth not activated!", Snackbar.LENGTH_INDEFINITE).setAction("Fix", new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(bluetoothIntent);
                }
            }).setActionTextColor(Color.GREEN).show();
        }

        if(BA.isEnabled() && !bt.getConnection())
        {
            Snackbar.make(mainLayout, "Arduino not connected!", Snackbar.LENGTH_INDEFINITE).setAction("Fix", new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(bluetoothIntent);
                }
            }).setActionTextColor(Color.GREEN).show();
        }

        if(!BA.isEnabled() && !bt.getConnection())
        {
            Snackbar.make(mainLayout, "Bluetooth & Arduino not connected!", Snackbar.LENGTH_INDEFINITE).setAction("Fix", new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(bluetoothIntent);
                }
            }).setActionTextColor(Color.GREEN).show();
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        bluetoothSnackBar();
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()){
            case R.id.buttonCon:
                Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(bluetoothIntent);
                break;
            case R.id.buttonAbout:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.buttonFlame:
                Intent flameIntent = new Intent(MainActivity.this, FlameActivity.class);
                startActivity(flameIntent);
                break;
            case R.id.buttonLights:
                Intent lightIntent = new Intent(MainActivity.this, LightActivity.class);
                startActivity(lightIntent);
                break;
        }
    }
}
