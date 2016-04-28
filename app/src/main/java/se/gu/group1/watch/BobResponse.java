package se.gu.group1.watch;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Omar on 4/25/2016.
 */
public class BobResponse {

    @NonNull
    public JSONObject createBobResponse(JSONObject cred, LocationAproximity loc, int xB,int yB ) throws JSONException {
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
        ArrayList<CipherText> result=loc.LessThan(D,4,Pk); // to be changed to radius later

        for(int i=0;i<result.size();i++){
            bobResult.put(result.get(i).C0.toString());
            bobResult.put(result.get(i).C1.toString());
        }
        JSONObject jsonReq=new JSONObject();
        JSONObject json=new JSONObject();
        jsonReq.put("Sender_ID", "Bob");// bobs key
        jsonReq.put("Recepient_name", cred.get("Sender_ID"));// alice key which was sent in the request
        jsonReq.put("Answer", bobResult);// results computed by bob
        json.put("Answer_Location", jsonReq);// the tag of the message
        return json;
    }
}
