
package se.hh.mentometer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class ConnectActivity extends Activity {

    // ===========================================================
    // Fields
    // ===========================================================
    private EditText mTextIP;
    private EditText mTextPort;
    private Button mConnectBtn;

    // ===========================================================
    // Override events
    // ===========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect);
        _setView();
    }

    // ===========================================================
    // Methods
    // ===========================================================
    
    private void _setView() {
        mTextIP = (EditText) findViewById(R.id.text_ip_connect_ac);
        mTextPort = (EditText) findViewById(R.id.text_port_connect_ac);
        mConnectBtn = (Button) findViewById(R.id.btn_connect_ac);
        mConnectBtn.setOnClickListener(mOnConnectClickListener);

        // For testing
        mTextIP.setText("192.168.137.1");
        mTextPort.setText("4444");
    }

    // ===========================================================
    // Listeners
    // ===========================================================
    private OnClickListener mOnConnectClickListener = new OnClickListener() {

        public synchronized void onClick(View v) {
            String ip = mTextIP.getText().toString();
            String string_port = mTextPort.getText().toString();
            int port = Integer.parseInt(string_port);

            // create and prepare intent
            Intent intent = new Intent(getBaseContext(), CommunicationService.class);
            intent.setFlags(CommunicationService.FLAG_CONNECT);
            Bundle bundle = new Bundle();
            bundle.putInt("port", port);
            bundle.putString("ip", ip);
            intent.putExtra("data", bundle);

            // call the service
            startService(intent);
            finish();
        }
    };
}
