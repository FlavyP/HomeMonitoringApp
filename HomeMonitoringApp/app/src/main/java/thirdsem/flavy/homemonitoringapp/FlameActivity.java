package thirdsem.flavy.homemonitoringapp;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FlameActivity extends Activity {

    private Button sendSms,sendEmail;
    private String numb, msg;
    private String mail;
    private TextView sendto;
    private TextView txtmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame);

        numb = PreferenceManager.getDefaultSharedPreferences(this).getString("cPhoneNumber", "");
        msg = PreferenceManager.getDefaultSharedPreferences(this).getString("message", "");
        mail = PreferenceManager.getDefaultSharedPreferences(this).getString("cEmail", "");



        sendto = (TextView) findViewById(R.id.txtSendTo);
        txtmsg = (TextView) findViewById(R.id.txtmsg);

        sendto.setText(numb);
        txtmsg.setText(msg);



        sendSms = (Button) findViewById(R.id.btnS);
        sendSms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sendSms(numb, msg);
                Toast.makeText(FlameActivity.this, "Send to " + numb, Toast.LENGTH_SHORT).show();
            }
        });

        sendEmail = (Button) findViewById(R.id.btnEmail);
        sendEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendEmail(mail, msg);
                Toast.makeText(FlameActivity.this, "mail: " + mail, Toast.LENGTH_LONG).show();
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
