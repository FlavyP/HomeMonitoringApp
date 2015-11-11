package thirdsem.flavy.homemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

public static final String TAG = "artuMSG";
private Button butCon, butStatus,butLights,butFlame,butAbout;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        initializeComponents();
    }

    private void initializeComponents()
    {
        butCon = (Button)findViewById(R.id.buttonCon);
        butCon.setOnClickListener(this);
        butStatus = (Button)findViewById(R.id.buttonStatus);
        butStatus.setOnClickListener(this);
        butLights = (Button)findViewById(R.id.buttonLights);
        butLights.setOnClickListener(this);
        butFlame = (Button)findViewById(R.id.buttonFlame);
        butFlame.setOnClickListener(this);
        butAbout = (Button)findViewById(R.id.buttonAbout);
        butAbout.setOnClickListener(this);
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
                //Intent lightIntent = new Intent(MainActivity.this, LightActivity.class);
                //startActivity(lightIntent);
                break;
            case R.id.buttonStatus:
                Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(statusIntent);
                break;
        }
    }
}
