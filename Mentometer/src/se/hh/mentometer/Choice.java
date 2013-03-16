package se.hh.mentometer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Choice implements Parcelable {

	public static final String CLASS_NAME = "Choice";
	public static final int POSSIBLE_ANSWERS_COUNT = 3;

	private String mQuestion;
	private String[] mAnswers;

	public static final Parcelable.Creator<Choice> CREATOR = new Parcelable.Creator<Choice>() {
		public Choice createFromParcel(Parcel in) {
			return new Choice(in);
		}

		public Choice[] newArray(int size) {
			return new Choice[size];
		}
	};

	public Choice() {
		super();
	}

	private Choice(Parcel in) {
		super();
		mQuestion = in.readString();
		mAnswers = new String[POSSIBLE_ANSWERS_COUNT];
		in.readStringArray(mAnswers);
	}

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		this.mQuestion = question;
	}

	public String getAnswer(int pos) {
		try {
			return mAnswers[pos];
		} catch (Exception e) {
			Log.e("Choice error", "getting answer error");
		}
		return null;
	}

	public void setAnswer(String answer, int pos) {
		try {
			mAnswers[pos] = answer;
		} catch (Exception e) {
			Log.e("Choice error", "setting answer error");
		}
	}

	public String[] getAllAnswers() {
		return mAnswers;
	}

	public void setAllAnswers(String[] answers) {
		this.mAnswers = answers;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mQuestion);
		out.writeStringArray(mAnswers);
	}

}
