package se.gu.group1.watch;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Omar on 4/11/2016.
 */
public class GcmIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    Context context;
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String msg=" ";
    public static final String TAG = "GCM Demo";

    public GcmIntentService() {
        super("GcmIntentService");
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        msg = intent.getStringExtra("Request_Details"); // get the message details if it is a request

        if(msg==null){
            msg = intent.getStringExtra("Answer_Location"); // get the answer received if it wasn't a request
        }
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                sendNotification(msg);// pass the message

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.d("Message is",msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myintent = new Intent(this, ReceiveActivity.class);
        myintent.putExtra("msg", msg);// put the message in the intent extras
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(myintent);// start the activity
    }

}
