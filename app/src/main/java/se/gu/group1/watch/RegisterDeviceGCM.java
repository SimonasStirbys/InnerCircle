package se.gu.group1.watch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDeviceGCM extends Activity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);

        new RegisterBackground().execute();

    }


    @Override
    protected void onResume(){
        super.onResume();

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
                sendRegistrationIdToBackend(regid); // send device id to server (need to do once if not registered)

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
           Log.d("Msg ", msg);

        }
    private void sendRegistrationIdToBackend(String regid) {
        JSONObject jsonReq=new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonReq.put("Sender_ID", 456); // should be replaced by the currect user ID
            jsonReq.put("Reg_ID", regid);
            jsonObj.put("Registration",jsonReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket("54.191.125.60", 5050);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(jsonObj.toString());
            out.close();
            Log.d("Message from reg",input.readLine()); // receive conformation messeage from server
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }}
}