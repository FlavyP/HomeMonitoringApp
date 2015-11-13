package thirdsem.flavy.homemonitoringapp;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LightActivity extends AppCompatActivity {

    private TextView switchStatus;
    private Switch mySwitch;
    private ColorPicker colorPicker;
    private Button button;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        initializeComponents();
    }

    private void initializeComponents () {
        switchStatus = (TextView) findViewById(R.id.switchStatus);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        mySwitch.setChecked(false);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switchStatus.setText("Switch is currently ON");
                } else {
                    switchStatus.setText("Switch is currently OFF");
                }

            }
        });

        colorPicker = (ColorPicker) findViewById(R.id.colorPicker);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {

                int color = colorPicker.getColor();
                String rgbString = "R: " + Color.red(color) + " G: " + Color.green(color) + " B: " + Color.blue(color);
                String rColor = "" + Color.red(color);
                String gColor = "" + Color.green(color);
                String bColor = "" + Color.blue(color);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("color", String.valueOf(color)).commit();
                //Toast.makeText(LightActivity.this, rgbString, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume();
        String color = PreferenceManager.getDefaultSharedPreferences(this).getString("color", "-1");
        Toast.makeText(LightActivity.this, "Color: " + Integer.parseInt(color), Toast.LENGTH_SHORT).show();
        colorPicker.setColor(Integer.parseInt(color));
    }
}
