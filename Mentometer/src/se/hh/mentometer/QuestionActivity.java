
package se.hh.mentometer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class QuestionActivity extends Activity {

    // ===========================================================
    // Fields
    // ===========================================================
    private TextView mQuestion;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private Button mSubmitBtn;
    public static final String ANSWER = "Answer";

    // ===========================================================
    // Override events
    // ===========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);

        _setView();
        _setExtras();
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private void _setView() {
        mQuestion = (TextView) findViewById(R.id.text_question_ac);

        mRadioButton1 = (RadioButton) findViewById(R.id.radio0_question_ac);
        mRadioButton2 = (RadioButton) findViewById(R.id.radio1_question_ac);
        mRadioButton3 = (RadioButton) findViewById(R.id.radio2_question_ac);

        mSubmitBtn = (Button) findViewById(R.id.btn_submit_question_ac);
        mSubmitBtn.setOnClickListener(mOnSubmitClickListener);
    }

    private void _setExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Choice choice = extras.getParcelable(Choice.CLASS_NAME);
            try {
                mQuestion.setText(choice.getQuestion());
                mRadioButton1.setText(choice.getAnswer(0));
                mRadioButton2.setText(choice.getAnswer(1));
                mRadioButton3.setText(choice.getAnswer(2));
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Choice is empty", Toast.LENGTH_LONG).show();
            }

        }
    }

    // ===========================================================
    // Listeners
    // ===========================================================
    private OnClickListener mOnSubmitClickListener = new OnClickListener() {

        public void onClick(View v) {
            int answer = 1;
            if (mRadioButton2.isChecked()) {
                answer = 2;
            }
            if (mRadioButton3.isChecked()) {
                answer = 3;
            }

            Intent intent = new Intent(getBaseContext(), CommunicationService.class);
            intent.setFlags(CommunicationService.FLAG_SEND_ANSWER);
            Bundle bundle = new Bundle();
            bundle.putInt(ANSWER, answer);
            intent.putExtra("data", bundle);

            startService(intent);
            finish();
        }
    };
}
