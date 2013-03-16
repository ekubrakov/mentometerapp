package se.hh.mentometer;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {
	// ===========================================================
    // Constants
    // ===========================================================
	
	public static final String RESULT = "result";
	public static final String DONT_KNOW = "Don't know!";
	public static final String RIGHT = "Right!";
	public static final String WRONG = "Wrong!";
	
	// ===========================================================
    // Fields
    // ===========================================================
	
	private String mQuestion;
	private String mAnswer;
	private String mCorrectAnswer;

	public Result() {
		super();
	}

	private Result(Parcel in) {
		super();
		mQuestion = in.readString();
		mAnswer = in.readString();
		mCorrectAnswer = in.readString();
	}

	public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
		public Result createFromParcel(Parcel in) {
			return new Result(in);
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		this.mQuestion = question;
	}

	public String getAnswer() {
	    if (mAnswer.equals("U")) {
	        return DONT_KNOW;
	    }
	    if (mAnswer.equals("W")) {
	        return WRONG;
	    }
	    if (mAnswer.equals("R")) {
	        return RIGHT;
	    }
		return mAnswer;
	}

	public void setAnswer(String answer) {
		this.mAnswer = answer;
	}

	public String getCorrectAnswer() {
		return mCorrectAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.mCorrectAnswer = correctAnswer;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mQuestion);
		dest.writeString(mAnswer);
		dest.writeString(mCorrectAnswer);
	}
}
