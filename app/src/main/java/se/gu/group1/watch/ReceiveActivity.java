package se.gu.group1.watch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Omar on 4/11/2016.
 */
public class ReceiveActivity extends Activity { // results page

    JSONObject json=new JSONObject();
    JSONObject jsonReq=new JSONObject();

    JSONObject answer;
    LocationAproximity loc;
    BobResponse bob;
    ArrayList<CipherText> encResults;
    //int xB=(int)1364890360.8888892,yB=(int)774968259.0822252; // should be replaced by Alice(x-coordinate) and Bob(x,y coordinate)

    int xB=0,yB=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        loc=new LocationAproximity();
        String message = intent.getExtras().getString("msg");// get the msg in the extras
        bob=new BobResponse();
        Log.d("message in ReceiveAct", message);
        if (message.contains("Answer")) {


            Toast.makeText(this,"Hello Result",Toast.LENGTH_LONG).show();

         // if the message is an answer then we need to decrypt the data and display the result
           try {

                answer =new JSONObject(message);
                JSONArray result;
               result=(JSONArray)answer.get("Answer");
                encResults=new ArrayList<>();
                for(int i=0;i<result.length();i+=2){
                    encResults.add(new CipherText(new BigInteger(result.getString(i)),new BigInteger(result.getString(i+1))));
                }

               SharedPreferences prefs = getSharedPreferences("UserCred",
                       Context.MODE_PRIVATE);
               SecretKey secret=new SecretKey(new BigInteger(prefs.getString("Secret Key","")));

                Log.d("answer of dec",""+loc.InProx(encResults, MainActivity.Pk,secret));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Message in Answer",message);

        //put answer in
        }else if(message.contains("Radius")){ // if the message is a request then bob needs to make the computation and send the data back to alice
            try {
                JSONArray bobResult; // contains the result computed by bob
                JSONObject cred;
                cred=new JSONObject(message);
                bobResult = bob.createBobResponse(cred,loc,xB,yB);

                jsonReq.put("Sender_ID", "Cyril");// bobs key
                jsonReq.put("Recepient_name", cred.get("Sender_ID"));// alice key which was sent in the request
                jsonReq.put("Answer", bobResult);// results computed by bob
                json.put("Answer_Location", jsonReq);// the tag of the message
                Log.d("Length ", ""+bobResult.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SendData data=new SendData();
            data.execute(json.toString());// send the JsonObject to the server

        }

    }


}

