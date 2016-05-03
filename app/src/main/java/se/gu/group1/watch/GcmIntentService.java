package se.gu.group1.watch;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    SharedPreferences prefs;

    JSONObject answer;
    LocationAproximity loc;
    BobResponse bob;
    ArrayList<CipherText> encResults;
    int xB=MainActivity.resultReceiver.makePrecsion()[0],yB=MainActivity.resultReceiver.makePrecsion()[1];
    public static final String TAG = "GCM Demo";

    public GcmIntentService() {

        super("GcmIntentService");

        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs =  getSharedPreferences("UserCred",
                Context.MODE_PRIVATE);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast toast = Toast.makeText(getApplicationContext(), "Received Request", Toast.LENGTH_SHORT);
        toast.show();
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
        SendData data=new SendData(prefs,null,getApplicationContext());
        loc=new LocationAproximity();
    String userName= prefs.getString("Username", "");
        bob=new BobResponse(userName);
        Log.d("message in ReceiveAct", msg);
        Toast toast = Toast.makeText(getApplicationContext(), "Received Answer", Toast.LENGTH_SHORT);
        toast.show();

        if (msg.contains("Message")) {

            data.execute("Answer");

            Toast.makeText(this, "Hello Result", Toast.LENGTH_LONG).show();

            // if the message is an answer then we need to decrypt the data and display the result

            Log.d("Message in Answer",msg);

            //put answer in
        }else if(msg.contains("Radius")){ // if the message is a request then bob needs to make the computation and send the data back to alice
            try {
                JSONObject bobResult; // contains the result computed by bob
                JSONObject cred;
                cred=new JSONObject(msg);
                bobResult = bob.createBobResponse(cred,loc,xB,yB, cred.getInt("Radius"));

//                jsonReq.put("Sender_ID", "Cyril");// bobs key
//                jsonReq.put("Recepient_name", cred.get("Sender_ID"));// alice key which was sent in the request
//                jsonReq.put("Answer", bobResult);// results computed by bob
//                json.put("Answer_Location", jsonReq);// the tag of the message

                data.execute(bobResult.toString());
                Log.d("Length ", ""+bobResult.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // send the JsonObject to the server

        }
    }

}
