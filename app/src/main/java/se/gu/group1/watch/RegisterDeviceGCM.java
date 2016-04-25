package se.gu.group1.watch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterDeviceGCM extends Service {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private final GcmBroadcastReceiver receiver = new GcmBroadcastReceiver();
    String SENDER_ID = "447368822063";

    static final String TAG = "GCMDemo";
    GoogleCloudMessaging gcm;

    //  TextView mDisplay;
    Context context;
    String regid;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name=intent.getStringExtra("Name");
        new RegisterBackground().execute(name);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class RegisterBackground extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Dvice registered, registration ID=" + regid;
                Log.d("Registration ", msg);
                // store regid in sharedPref
                sendRegistrationIdToBackend(regid,arg0[0]); // send device id to server (need to do once if not registered)

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.d("Msg ", msg);

        }
        private void sendRegistrationIdToBackend(String regid,String name) {
            JSONObject jsonReq=new JSONObject();
            JSONObject jsonObj = new JSONObject();
            try {
                jsonReq.put("Sender_ID", name); // should be replaced by the currect user ID
                jsonReq.put("Reg_ID", regid);
                jsonObj.put("Registration",jsonReq);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Socket socket = new Socket("54.191.125.60", 5050);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(jsonObj.toString());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
