package se.gu.group1.watch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Omar on 4/11/2016.
 */
public class ReceiveActivity extends Activity { // results page

    JSONObject json=new JSONObject();
    JSONObject jsonReq=new JSONObject();
    JSONObject cred;
    LocationAproximity loc;
    ArrayList<CipherText> encResults;
    JSONArray bobResult; // contains the result computed by bob
    int xB=0,yA=0,yB=3; // should be replaced by Alice(x-coordinate) and Bob(x,y coordinate)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        loc=new LocationAproximity();
        String message = intent.getExtras().getString("msg");// get the msg in the extras

        Log.d("message in ReceiveAct", message);
        if (message.contains("Answer")) { // if the message is an answer then we need to decrypt the data and display the result
           /* try {

                answer =new JSONObject(message);
                JSONArray result=new JSONArray(answer.get("Answer"));
                encResults=new ArrayList<>();
                for(int i=0;i<result.length();i+=2){
                    encResults.add(new CipherText(new BigInteger(result.getString(i)),new BigInteger(result.getString(i+1))));
                }
                Log.d("answer of dec","True");

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            Log.d("Message in Answer",message);
        //put answer in
        }else if(message.contains("Radius")){ // if the message is a request then bob needs to make the computation and send the data back to alice
            try {
                 cred=new JSONObject(message);
                bobResult=new JSONArray();
                CipherText a0=new CipherText(new BigInteger(String.valueOf(cred.get("A0.C0"))),new BigInteger(String.valueOf(cred.get("A0.C1"))));
                CipherText a1=new CipherText(new BigInteger(String.valueOf(cred.get("A1.C0"))),new BigInteger(String.valueOf(cred.get("A1.C1"))));
                CipherText a2=new CipherText(new BigInteger(String.valueOf(cred.get("A2.C0"))),new BigInteger(String.valueOf(cred.get("A2.C1"))));
                PublicKey Pk=new PublicKey(new BigInteger(String.valueOf(cred.get("P"))),new BigInteger(String.valueOf(cred.get("G"))),new BigInteger(String.valueOf(cred.get("Y"))));
                CipherText D=loc.bobComputes(Pk, a0, a1, a2, yA, yB, xB);
                ArrayList<CipherText> result=loc.LessThan(D, 2,Pk);

                for(int i=0;i<result.size()/2;i++){
                    bobResult.put(result.get(i).C0.toString());
                    bobResult.put(result.get(i).C1.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonReq.put("Sender_ID", 456);// bobs key
                jsonReq.put("Recepient_name", cred.get("Sender_ID"));// alice key which was sent in the request
                jsonReq.put("Answer", bobResult);// results computed by bob
                json.put("Answer_Location", jsonReq);// the tag of the message
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SendData data=new SendData();
            data.execute(json.toString());// send the JsonObject to the server
        }

    }
}

