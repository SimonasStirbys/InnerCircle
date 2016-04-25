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
    public JSONArray createBobResponse(JSONObject cred, LocationAproximity loc, int xB,int yB ) throws JSONException {
        JSONArray bobResult;
        bobResult=new JSONArray();
        Log.d("Mesg", cred.get("A0.C0").toString());
        CipherText a0=new CipherText(new BigInteger(cred.getString("A0.C0").toString()),new BigInteger(cred.getString("A0.C1").toString()));
        CipherText a1=new CipherText(new BigInteger(cred.getString("A1.C0").toString()),new BigInteger(cred.getString("A1.C1").toString()));
        CipherText a2=new CipherText(new BigInteger(cred.getString("A2.C0").toString()),new BigInteger(cred.getString("A2.C1").toString()));
        PublicKey Pk=new PublicKey(new BigInteger(cred.getString("P")),new BigInteger(cred.getString("G")),new BigInteger(cred.getString("Y")));
        CipherText D=loc.bobComputes(Pk, a0, a1, a2,yB, xB);
        ArrayList<CipherText> result=loc.LessThan(D,9 ,Pk);

        for(int i=0;i<result.size();i++){
            bobResult.put(result.get(i).C0.toString());
            bobResult.put(result.get(i).C1.toString());
        }
        return bobResult;
    }
}
