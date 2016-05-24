package se.gu.group1.watch;

import android.support.annotation.NonNull;
import android.util.Log;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Omar on 4/25/2016.
 */
public class AliceRequestTest extends TestCase {

////    public void testMakeJsonObject() throws Exception {
////    ElgamalCrypto crypto=new ElgamalCrypto();
////        CipherText[] cred=new CipherText[3];
////        AliceRequest alice=new AliceRequest();
////        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
////        alice.generateEncryptedLocation(crypto,Pk,cred,2,3);
////        JSONObject msg=new JSONObject(alice.makeJsonObject(crypto,cred));
////        BigInteger P=new BigInteger(msg.getString("P"));
////        BigInteger G=new BigInteger(msg.getString("G"));
////        BigInteger Y=new BigInteger(msg.getString("Y"));
////        CipherText a0=new CipherText(new BigInteger(msg.getString("A0.C0")),new BigInteger(msg.getString("A0.C1")));
////        assertEquals(crypto.getP(),P);
////        assertEquals(crypto.getG(),G);
////        assertEquals(crypto.getY(),Y);
////        assertEquals(13,crypto.decryptText(Pk,new SecretKey(crypto.getSecretKey()),a0));
////    }
    public void testResult() throws JSONException {

        BobResponse bob=new BobResponse("Bob");

        int  xA = 0;
        int yA = 0;

        int  xB = 0;
        int yB= 3;
       Log.d("Test ", "" + xB + " " + yB);
        Log.d("Test 1", "" + xA + " " + yA);
        LocationAproximity loc=new LocationAproximity();
        AliceRequest alice=new AliceRequest();
        CipherText[] cred=new CipherText[3];
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        crypto.initializeSumOfSquares(Pk);
       // int xB=200000,yB=500000;
        alice.generateEncryptedLocation(crypto, Pk, cred, xA, yA);
        ArrayList<String> names=new ArrayList<>();
        names.add("Alice");
        JSONObject bobResponse = bob.createBobResponse(emulateGCM(alice, cred, crypto, names), new LocationAproximity(),xB, yB, 50);
        assertTrue(alice.parseBobResponse(new SecretKey(crypto.getSecretKey()),Pk,bobResponse.toString(),loc));


    }

    @NonNull
    private JSONObject emulateGCM(AliceRequest alice, CipherText[] cred, ElgamalCrypto crypto, ArrayList<String> names) throws JSONException {
        return new JSONObject(alice.makeJsonObject(crypto, cred,500,names,"Alice")).getJSONObject("Requests").getJSONObject("Cred");
    }
}