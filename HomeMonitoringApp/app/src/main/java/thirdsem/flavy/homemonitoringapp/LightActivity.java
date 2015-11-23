package thirdsem.flavy.homemonitoringapp;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LightActivity extends AppCompatActivity {

    public final String TAG = "Light";

    private Switch mySwitch;
    private ImageView bulb;
    private ColorPicker colorPicker;
    private Button setColorButton;
    private Button connectButtonL;

    private Bluetooth bt;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        connectButtonL = (Button) findViewById(R.id.connectButtonL);
        colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        setColorButton = (Button) findViewById(R.id.setColorButton);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        bulb = (ImageView) findViewById(R.id.bulb);
        mySwitch.setChecked(false);

        bt = new Bluetooth(this, mHandler);

        /*if(!bt.getConnection())
        {
            mySwitch.setVisibility(View.GONE);
            colorPicker.setVisibility(View.GONE);
        }*/
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bulb.setImageResource(R.drawable.bulbon);
                    bt.sendMessage("1");
                } else {
                    bulb.setImageResource(R.drawable.bulboff);
                    bt.sendMessage("0");
                }
            }
        });

        setColorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {

                int color = colorPicker.getColor();
                String rColor = "" + Color.red(color);
                String gColor = "" + Color.green(color);
                String bColor = "" + Color.blue(color);
                String toSend = "#" + rColor + "+" + gColor + "+" + bColor + "X";
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("color", String.valueOf(color)).commit();
                bt.sendMessage(toSend);
                Toast.makeText(LightActivity.this, toSend, Toast.LENGTH_SHORT).show();
            }
        });

        connectButtonL.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                connectService();
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume();
        String color = PreferenceManager.getDefaultSharedPreferences(this).getString("color", "-1");
        colorPicker.setColor(Integer.parseInt(color));
    }

    @Override
    protected void onStop () {
        super.onStop();
        bt.stop();
    }

    /* Connecting the phone to the arduino board */
    public void connectService(){
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.connectDevice("HC-06");
                if(!bt.getConnection())
                    Toast.makeText(LightActivity.this, "Not connected, try again", Toast.LENGTH_SHORT).show();
                else {
                    mySwitch.setVisibility(View.VISIBLE);
                    colorPicker.setVisibility(View.VISIBLE);
                    Toast.makeText(LightActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Btservice started - listening");
            } else {
                Toast.makeText(LightActivity.this, "Enable Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ", e);
        }
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
}
