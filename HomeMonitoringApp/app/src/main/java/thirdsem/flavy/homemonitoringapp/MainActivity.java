package thirdsem.flavy.homemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

public static final String TAG = "artuMSG";
private Button butCon, butStatus,butLights,butFlame,butAbout;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");




        butCon = (Button)findViewById(R.id.buttonCon);
        butStatus = (Button)findViewById(R.id.buttonStatus);
        butLights = (Button)findViewById(R.id.buttonLights);
        butFlame = (Button)findViewById(R.id.buttonFlame);
        butAbout = (Button)findViewById(R.id.buttonAbout);


        butCon.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(myIntent);


                Toast.makeText(MainActivity.this, "Hello from  Home Screen", Toast.LENGTH_LONG).show();


            }


        });

        butStatus.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(myIntent);


                Toast.makeText(MainActivity.this, "Hello from  Home Screen", Toast.LENGTH_LONG).show();


            }


        });

//        butLights.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(MainActivity.this, LightsActivity.class);
//                startActivity(myIntent);
//
//
//                Toast.makeText(MainActivity.this, "Hello from  Home Screen", Toast.LENGTH_LONG).show();
//
//                finish();
//            }
//
//
//        });

        butFlame.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, FlameActivity.class);
                startActivity(myIntent);


                Toast.makeText(MainActivity.this, "Hello from  Home Screen", Toast.LENGTH_LONG).show();


            }


        });

        butAbout.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(myIntent);


                Toast.makeText(MainActivity.this, "Hello from  Home Screen", Toast.LENGTH_LONG).show();


            }


        });


    }
}
