package se.gu.group1.watch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Omar on 4/25/2016.
 */
public class BobResponse {



    private String userName ;

    public BobResponse(String userName) {
        this.userName = userName;
    }

    @NonNull
    public JSONObject createBobResponse(JSONObject cred, LocationAproximity loc, int xB, int yB, int radius) throws JSONException {
     // JSONObject jsonObj=message.getJSONObject("Requests");
       // JSONObject cred=jsonObj.getJSONObject("Cred");
        JSONArray bobResult;
        bobResult=new JSONArray();
       // Log.d("Mesg", cred.get("A0.C0").toString());
        CipherText a0=new CipherText(new BigInteger(cred.getString("A0.C0").toString()),new BigInteger(cred.getString("A0.C1").toString()));
        CipherText a1=new CipherText(new BigInteger(cred.getString("A1.C0").toString()),new BigInteger(cred.getString("A1.C1").toString()));
        CipherText a2=new CipherText(new BigInteger(cred.getString("A2.C0").toString()),new BigInteger(cred.getString("A2.C1").toString()));
        PublicKey Pk=new PublicKey(new BigInteger(cred.getString("P")),new BigInteger(cred.getString("G")),new BigInteger(cred.getString("Y")));
        CipherText D=loc.bobComputes(Pk, a0, a1, a2, yB, xB);
        int startTime = getTime();
        ArrayList<CipherText> result=loc.LessThan(D, radius, Pk);
        int endTime = getTime();
        int totalTime = endTime-startTime;
        System.out.println("totaltime: "+totalTime);
        Log.d("totaltime", ""+totalTime);
       MainActivity.requests.add(cred.get("Sender_ID").toString());
         MainActivity.requests.add(""+totalTime);
        for(int i=0;i<result.size();i++){
            bobResult.put(result.get(i).C0.toString());
            bobResult.put(result.get(i).C1.toString());
        }
        JSONObject jsonReq=new JSONObject();
        JSONObject json=new JSONObject();

        jsonReq.put("Sender_ID", userName);// bobs key
        jsonReq.put("Recepient_name", cred.get("Sender_ID"));// alice key which was sent in the request
        jsonReq.put("Answer", bobResult);
        jsonReq.put("Time",totalTime);// results computed by bob
        json.put("Answer_Location", jsonReq);// the tag of the message
        return json;
    }

    public int getTime(){
        Calendar c = Calendar.getInstance();
        int startMili = c.get(Calendar.MILLISECOND);
        int startSecond = c.get(Calendar.SECOND);
        int startMinute = c.get(Calendar.MINUTE);
        int startHour = c.get(Calendar.HOUR);
        int startTime = (startHour*3600000)+(startMinute*60000)+(startSecond*1000)+startMili;
        return startTime;
    }
}
