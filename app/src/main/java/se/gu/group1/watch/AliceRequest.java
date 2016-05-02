package se.gu.group1.watch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Omar on 4/25/2016.
 */
public class AliceRequest {

    public String makeJsonObject(ElgamalCrypto crypto, CipherText[] cred,int radius,ArrayList<String> recpName, String sender) {
        String[] names=new String[recpName.size()];
        names=recpName.toArray(names);
        JSONObject jsonReq=new JSONObject();
        JSONObject jsonF=new JSONObject();
        JSONObject jsonObj=new JSONObject();
        try {
            jsonReq.put("A0.C0", cred[0].C0.toString());
            jsonReq.put("A0.C1", cred[0].C1.toString());
            jsonReq.put("A1.C0", cred[1].C0.toString());
            jsonReq.put("A1.C1", cred[1].C1.toString());
            jsonReq.put("A2.C0", cred[2].C0.toString());
            jsonReq.put("A2.C1", cred[2].C1.toString());
            jsonReq.put("P", crypto.getP().toString());
            jsonReq.put("G", crypto.getG().toString());
            jsonReq.put("Y", crypto.getY().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //TODO: make Bob id not hardcoded.
            jsonReq.put("Sender_ID", sender);// Alice ID
            jsonF.put("Recepient_ID", new JSONArray(names)); // Arrays of recp ID
            jsonReq.put("Radius", radius);//radius

            jsonF.put("Cred", jsonReq);// add all the keys in the message
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObj.put("Requests", jsonF);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }
    public  CipherText[] generateEncryptedLocation(ElgamalCrypto crypto,PublicKey Pk, CipherText[] cred,int xA, int yA) {// publickey ,Alice  x-coordinate, Alice y-coordinate
        BigInteger xA2=new BigInteger(String.valueOf(xA)).pow(2);
        BigInteger yA2=new BigInteger(String.valueOf(yA)).pow(2);

        CipherText a0 = crypto.encryption(Pk,xA2.add(yA2));
        CipherText a1 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * xA)));
        CipherText a2 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * yA)));
        cred[0] = a0;
        cred[1] = a1;
        cred[2] = a2;

        return cred;
    }
    public boolean parseBobResponse(SecretKey secret, PublicKey pk, String message,LocationAproximity loc) throws JSONException {
        JSONObject answer = new JSONObject(message);
        JSONObject fAnswer =answer.getJSONObject("Answer_Location");
        JSONArray result;

        result=(JSONArray)fAnswer.get("Answer");

        ArrayList<CipherText> encResults=new ArrayList<>();
        for(int i=0;i<result.length();i+=2){
            encResults.add(new CipherText(new BigInteger(result.getString(i)),new BigInteger(result.getString(i+1))));
        }
        return loc.InProx(encResults, pk, secret);
    }
}
