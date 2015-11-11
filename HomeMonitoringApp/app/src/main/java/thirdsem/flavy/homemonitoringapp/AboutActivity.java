package thirdsem.flavy.homemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Button saveButton;

    private EditText yourName;
    private EditText yourAddress;
    private EditText yourEmail;
    private EditText prefContactName;
    private EditText prefContactEmail;
    private EditText prefContactPhoneNumber;
    private EditText emergencyMessage;

    //private boolean canBeSaved = false;
    private boolean[] canBeSaved = new boolean[7];

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeComponents();
    }

    private void initializeComponents () {
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        yourName = (EditText) findViewById(R.id.yourName);
        yourName.addTextChangedListener(this);
        yourName.setError("Required");

        yourAddress = (EditText) findViewById(R.id.yourAddress);
        yourAddress.addTextChangedListener(this);
        yourAddress.setError("Required");

        yourEmail = (EditText) findViewById(R.id.yourEmail);
        yourEmail.addTextChangedListener(this);
        yourEmail.setError("Required");

        prefContactName = (EditText) findViewById(R.id.prefContactName);
        prefContactName.addTextChangedListener(this);
        prefContactName.setError("Required");

        prefContactEmail = (EditText) findViewById(R.id.prefContactEmail);
        prefContactEmail.addTextChangedListener(this);
        prefContactEmail.setError("Required");

        prefContactPhoneNumber = (EditText) findViewById(R.id.prefContactPhoneNumber);
        prefContactPhoneNumber.addTextChangedListener(this);
        prefContactPhoneNumber.setError("Required");

        emergencyMessage = (EditText) findViewById(R.id.emergencyMessage);
        emergencyMessage.addTextChangedListener(this);
        emergencyMessage.setError("Required");
    }

    private void saveDetails () {
        String name = yourName.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("name", name).commit();

        String address = yourAddress.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("address", address).commit();

        String yEmail = yourEmail.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("yEmail", yEmail).commit();

        String cName = prefContactName.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("cName", cName).commit();

        String cEmail = prefContactEmail.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("cEmail", cEmail).commit();

        String message = emergencyMessage.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("message", message).commit();
    }

    private void setAbout (String name, String address, String yEmail, String cName, String cEmail, String message) {
        yourName.setText(name);
        yourAddress.setText(address);
        yourEmail.setText(yEmail);
        prefContactName.setText(cName);
        prefContactEmail.setText(cEmail);
        emergencyMessage.setText(message);
    }

    private void checkText()
    {
        if (!yourName.getText().toString().isEmpty()) {
            yourName.setError(null);
            canBeSaved[0] = true;
        }
        else {
            yourName.setError("Required");
            canBeSaved[0] = false;
        }

        if (!yourAddress.getText().toString().isEmpty()){
            yourAddress.setError(null);
            canBeSaved[1] = true;
        }
        else{
            yourAddress.setError("Required");
            canBeSaved[1] = false;
        }

        if (!yourEmail.getText().toString().isEmpty()){
            yourEmail.setError(null);
            canBeSaved[2] = true;
            if(isEmailValid(yourEmail.getText().toString()) == false)
            {
                yourEmail.setError("Email not valid");
                canBeSaved[2] = false;
            }
            else if(isEmailValid(yourEmail.getText().toString())){
                canBeSaved[2] = true;
            }
        }
        else{
            yourEmail.setError("Required");
            canBeSaved[2] = false;
        }

        if (!prefContactName.getText().toString().isEmpty()){
            prefContactName.setError(null);
            canBeSaved[3] = true;
        }
        else{
            prefContactName.setError("Required");
            canBeSaved[3] = false;
        }

        if (!prefContactEmail.getText().toString().isEmpty()){
            prefContactEmail.setError(null);
            canBeSaved[4] = true;
            if(isEmailValid(prefContactEmail.getText().toString()) == false)
            {
                prefContactEmail.setError("Email not valid");
                canBeSaved[4] = false;
            }
            else if(isEmailValid(prefContactEmail.getText().toString())){
                canBeSaved[4] = true;
            }
        }
        else{
            prefContactEmail.setError("Required");
            canBeSaved[4] = false;
        }

        if (!prefContactPhoneNumber.getText().toString().isEmpty())
        {
            prefContactPhoneNumber.setError(null);
            canBeSaved[5] = true;
            if(isPhoneNumberValid(prefContactPhoneNumber.getText().toString()) == false)
            {
                prefContactPhoneNumber.setError("Enter valid phone number");
                canBeSaved[5] = false;
            }
        }
        else
        {
            prefContactPhoneNumber.setError("Required");
            canBeSaved[5] = false;
        }

        if (!emergencyMessage.getText().toString().isEmpty()){
            emergencyMessage.setError(null);
            canBeSaved[6] = true;
        }
        else{
            emergencyMessage.setError("Required");
            canBeSaved[6] = false;
        }
    }

    private boolean canSave()
    {
        for(int i = 0; i < canBeSaved.length; i++)
        {
            if(canBeSaved[i] == false)
                return false;
        }
        return true;
    }

    private boolean isEmailValid(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber)
    {
        return !TextUtils.isEmpty(phoneNumber) && android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    @Override
    protected void onResume () {
        super.onResume();

        String name = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "");
        String address = PreferenceManager.getDefaultSharedPreferences(this).getString("address", "");
        String yEmail = PreferenceManager.getDefaultSharedPreferences(this).getString("yEmail", "");
        String cName = PreferenceManager.getDefaultSharedPreferences(this).getString("cName", "");
        String cEmail = PreferenceManager.getDefaultSharedPreferences(this).getString("cEmail", "");
        String message = PreferenceManager.getDefaultSharedPreferences(this).getString("message", "");

        setAbout(name, address, yEmail, cName, cEmail, message);
        checkText();

    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                if(canSave()) {
                    saveDetails();
                    Toast.makeText(AboutActivity.this, "Details saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AboutActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged (CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged (Editable editable) {
        checkText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent( AboutActivity.this, MainActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

