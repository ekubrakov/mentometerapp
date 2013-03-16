
package se.hh.mentometer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import java.io.IOException;
import java.io.StringReader;

public class XMLHelper {

    public static final String TAG_QUESTION = "question";
    public static final String TAG_ANSWER = "answer";
    public static final String TAG_CORRECT = "correct";
    public static final String TAG_CONCLUDE = "conclude";
    public static final String TAG_RECEIVED = "received";

    public static final int ZERO_ATTRIBUTE = 0; // as we have information only
                                                // in the first attribute

    public static String convertAnswerToXML(int answerNmb) {
        return "<answer text = \"" + answerNmb + "\"/>";
    }

    public static String parseConclude(String xmlString) {
        Log.e("Conclude string", xmlString);
        // Initializing XML parser
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setInput(new StringReader(xmlString));
            // Main parsing cycle
            while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xmlParser.getEventType()) {
                // When TAG is detected
                    case XmlPullParser.START_TAG:
                        // Tag question
                        if (xmlParser.getName().equals(TAG_CONCLUDE)) {
                            return "conclude";
                        }
                        break;
                    default:
                        break;
                }
                // Next element
                xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e("XML EXCEPTION", "Error parsing: " + xmlString);
        } catch (IOException e) {
            Log.e("IO Exception", "Error parsing: " + xmlString);
        }
        return null;
    }

    public static Choice parseChoice(String xmlString) {
        // Initializing XML parser
        XmlPullParser xmlParser = Xml.newPullParser();
        // Initializing choice object
        Choice choice = new Choice();
        choice.setAllAnswers(new String[Choice.POSSIBLE_ANSWERS_COUNT]);
        int answerCnt = 0; // counter for increasing answer position in the
                           // choice answers field
        try {
            xmlParser.setInput(new StringReader(xmlString));
            // Main parsing cycle
            while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xmlParser.getEventType()) {
                // When TAG is detected
                    case XmlPullParser.START_TAG:
                        // Tag question
                        if (xmlParser.getName().equals(TAG_QUESTION)) {
                            String question = xmlParser.getAttributeValue(ZERO_ATTRIBUTE);
                            choice.setQuestion(question);
                        }
                        // Tag answer
                        if (xmlParser.getName().equals(TAG_ANSWER)) {
                            String answer = xmlParser.getAttributeValue(ZERO_ATTRIBUTE);
                            choice.setAnswer(answer, answerCnt);
                            answerCnt++;
                        }
                        break;
                    default:
                        break;
                }
                // Next element
                xmlParser.next();
            }
            return choice;
        } catch (XmlPullParserException e) {
            Log.e("XML EXCEPTION", "Error parsing: " + xmlString);
        } catch (IOException e) {
            Log.e("IO Exception", "Error parsing: " + xmlString);
        }
        return null;
    }

    public static Reply parseReply(String xmlString) {
        // Initializing XML parser
        XmlPullParser xmlParser = Xml.newPullParser();
        // Initializing choice object
        Reply reply = new Reply();
        try {
            xmlParser.setInput(new StringReader(xmlString));
            // Main parsing cycle
            while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xmlParser.getEventType()) {
                // When TAG is detected
                    case XmlPullParser.START_TAG:
                        // Tag correct
                        if (xmlParser.getName().equals(TAG_CORRECT)) {
                            String correctAnswer = xmlParser.getAttributeValue(ZERO_ATTRIBUTE);
                            reply.setCorrectAnswer(correctAnswer);
                        }
                        // Tag received
                        if (xmlParser.getName().equals(TAG_RECEIVED)) {
                            String recievedAnswer = xmlParser.getAttributeValue(ZERO_ATTRIBUTE);
                            reply.setReceivedAnswer(recievedAnswer);
                        }
                        break;
                    default:
                        break;
                }
                // Next element
                xmlParser.next();
            }
            return reply;
        } catch (XmlPullParserException e) {
            Log.e("XML EXCEPTION", "Error parsing: " + xmlString);
        } catch (IOException e) {
            Log.e("IO Exception", "Error parsing: " + xmlString);
        }
        return null;
    }
}
