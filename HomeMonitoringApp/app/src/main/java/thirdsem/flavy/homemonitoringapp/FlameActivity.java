package thirdsem.flavy.homemonitoringapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FlameActivity extends Activity {

    public final String TAG = "Main";
    private Button sendSms,sendEmail, dismiss;
    private Bluetooth bt;
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

        dismiss = (Button) findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bt.sendMessage("9");
                Toast.makeText(FlameActivity.this, "Dismiss msg send", Toast.LENGTH_LONG).show();
            }
        });

        bt = new Bluetooth(this, mHandler);
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
