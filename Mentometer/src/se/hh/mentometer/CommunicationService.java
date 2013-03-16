
package se.hh.mentometer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class CommunicationService extends Service {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String YOUR_RESULTS_TEXT = "Your final results...";

    public static final int NOTIFICATION_ID = 123123;
    public static final int FLAG_CONNECT = 0;
    public static final int FLAG_SEND_ANSWER = FLAG_CONNECT + 1;
    public static final int FLAG_DISCONNECT = FLAG_SEND_ANSWER + 1;

    // ===========================================================
    // Fields
    // ===========================================================
    private ArrayList<Result> mResults;
    private static CommunicationHandler mHandler;
    private Looper mLooper;

    // ===========================================================
    // Overridden events
    // ===========================================================
    @Override
    public IBinder onBind(Intent intent) { // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mResults = new ArrayList<Result>();
        HandlerThread handlerThread = new HandlerThread(
                "Ololo, ololo, I'm a driver of UFO",
                Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.setDaemon(true);
        handlerThread.start();
        mLooper = handlerThread.getLooper();
        mHandler = new CommunicationHandler(mLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mHandler.obtainMessage(intent.getFlags());
        msg.setData(intent.getBundleExtra("data"));
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    // ===========================================================
    // Inner handler class
    // ===========================================================
    @SuppressLint("HandlerLeak")
    private final class CommunicationHandler extends Handler {
        // ===========================================================
        // Fields
        // ===========================================================
        private Scanner mIn;
        private PrintWriter mOut;
        private Socket mSocket;
        private NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        private Thread mPortListenerThread;
        private Result mBuffResult;

        // ===========================================================
        // Constructor
        // ===========================================================
        public CommunicationHandler(Looper looper) {
            super(looper);
        }

        // ===========================================================
        // Methods
        // ===========================================================
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FLAG_CONNECT:
                    _connectToServer(msg.getData());
                    break;
                case FLAG_SEND_ANSWER:
                    _sendAnswer(msg.getData());
                    break;
                default:
            }
            return;
        }

        private synchronized void _recreatePortListenerThread() {
            mPortListenerThread = null;
            mPortListenerThread = new Thread(new Runnable() {

                public void run() {
                    String xmlString = mIn.next();
                    Thread.yield();
                    // Checking if "conclude" statement came
                    if (xmlString.equals("\r\n")) {
                        String parsed = XMLHelper.parseConclude("<conclude/>");
                        if (parsed != null) {
                            _sendResultsNotification();
                            stopSelf();
                        }
                    }
                    // Checking if "choice" statement came
                    Choice choice = XMLHelper.parseChoice(xmlString
                            + "</choice>");
                    if (choice != null) {
                        mBuffResult = new Result();
                        mBuffResult.setQuestion(choice.getQuestion());
                        _sendQuestionNotification(choice);
                    }
                    // Checking if "reply" statement came
                    Reply reply = XMLHelper.parseReply(xmlString + "</reply>");
                    if (reply != null) {
                        if (mBuffResult != null) {
                            mBuffResult.setCorrectAnswer(reply
                                    .getCorrectAnswer());
                            mBuffResult.setAnswer(reply.getReceivedAnswer());
                            mResults.add(mBuffResult);
                        }
                        _recreatePortListenerThread();
                    }
                }

            });
            if (mPortListenerThread != null) {
                mPortListenerThread.setDaemon(true);
                mPortListenerThread.run();
            }
        }

        private void _connectToServer(Bundle bundle) {
            String host = bundle.getString("ip");
            int port = bundle.getInt("port");
            try {
                // Establish a connection with the server
                mSocket = new Socket(host, port);
                mIn = new Scanner(new BufferedInputStream(mSocket
                        .getInputStream()));
                mIn.useDelimiter("</choice>|<conclude/>|</reply>");
                mOut = new PrintWriter(mSocket.getOutputStream(), true);
                // Start listening to port
                _notifyUserConnected();
                _recreatePortListenerThread();
            } catch (java.net.UnknownHostException e) {
                Toast.makeText(CommunicationService.this, "Unknown host",
                        Toast.LENGTH_SHORT).show();
                stopSelf();
            } catch (java.io.IOException e) {
                Toast.makeText(CommunicationService.this, "IOException",
                        Toast.LENGTH_SHORT).show();
                stopSelf();
            }

        }

        private void _sendAnswer(Bundle bundle) {
            int answer = bundle.getInt(QuestionActivity.ANSWER);
            String response = XMLHelper.convertAnswerToXML(answer);
            mOut.println(response);
            _recreatePortListenerThread();
        }

        private void _notifyUserConnected() {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(CommunicationService.this, "Connection established",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void _sendQuestionNotification(Choice choice) {
            Notification notification = new Notification(
                    R.drawable.ic_launcher, choice.getQuestion(), System
                            .currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Intent intent = new Intent(getApplicationContext(),
                    QuestionActivity.class);

            intent.putExtra(Choice.CLASS_NAME, choice);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                            | Notification.FLAG_AUTO_CANCEL);
            notification.setLatestEventInfo(getApplicationContext(),
                    "Question", choice.getQuestion(), pendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

        private void _sendResultsNotification() {
            Notification notification = new Notification(
                    R.drawable.ic_launcher, YOUR_RESULTS_TEXT, System
                            .currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Intent intent = new Intent(getApplicationContext(),
                    ResultsActivity.class);

            intent.putExtra(Result.RESULT, mResults);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                            | Notification.FLAG_AUTO_CANCEL);
            notification.setLatestEventInfo(getApplicationContext(), "Results",
                    YOUR_RESULTS_TEXT, pendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
