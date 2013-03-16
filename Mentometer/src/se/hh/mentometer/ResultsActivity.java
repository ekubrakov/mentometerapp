
package se.hh.mentometer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;

public class ResultsActivity extends Activity {
    // ===========================================================
    // Fields
    // ===========================================================

    private ListView mResultsList;
    private ArrayList<Result> mResultsDataList;
    private ResultsAdapter mResultsAdapter;

    // ===========================================================
    // Override application events
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_results);
        _setExtras();
        _setView();
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private void _setView() {
        mResultsList = (ListView) findViewById(R.id.results_list_results_ac);
        mResultsAdapter = new ResultsAdapter(this, R.id.question_text_item);
        mResultsList.setAdapter(mResultsAdapter);
        mResultsAdapter.notifyDataSetChanged();
    }

    private void _setExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mResultsDataList = bundle.getParcelableArrayList(Result.RESULT);
        } else {
            Toast.makeText(getBaseContext(), "No result data!!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ===========================================================
    // Adapter helper class
    // ===========================================================
    private class ResultsAdapter extends ArrayAdapter<String> {

        private LayoutInflater mInflater;

        public ResultsAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // ===========================================================
        // View holder class
        // ===========================================================
        private class ViewHolder {
            public TextView questionText;
            public TextView answerText;
            public TextView correctAnswerText;
        }

        // ===========================================================
        // Override methods
        // ===========================================================
        @Override
        public int getCount() {
            return mResultsDataList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            convertView = mInflater.inflate(R.layout.item_result, null);

            if (convertView.getTag() == null) {
                holder = new ViewHolder();
                holder.questionText = (TextView) convertView
                        .findViewById(R.id.question_descr_item);
                holder.answerText = (TextView) convertView
                        .findViewById(R.id.answer_descr_item);
                holder.correctAnswerText = (TextView) convertView
                        .findViewById(R.id.correct_descr_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Binding data
            Result result = mResultsDataList.get(position);

            if (result != null) {
                if (result.getQuestion() != null) {
                    holder.questionText.setText(result.getQuestion());
                }
                if (result.getAnswer() != null) {
                    String answer = result.getAnswer();
                    if (answer.equals(Result.RIGHT)) {
                        holder.answerText.setTextColor(Color.GREEN);
                    } else {
                        holder.answerText.setTextColor(Color.RED);
                    }
                    holder.answerText.setText(result.getAnswer());
                }
                if (result.getCorrectAnswer() != null) {
                    holder.correctAnswerText.setText(result.getCorrectAnswer());
                }
            }

            return convertView;
        }
    }
}
