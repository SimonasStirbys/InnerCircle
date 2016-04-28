package se.gu.group1.watch;

import android.content.SharedPreferences;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
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
        LocationAproximity loc=new LocationAproximity();
        AliceRequest alice=new AliceRequest();
        CipherText[] cred=new CipherText[3];
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=1364879928,yA=774947461;
        alice.generateEncryptedLocation(crypto, Pk, cred, xA, yA);
    ArrayList<String> names=new ArrayList<>();
        names.add("Alice");
        JSONObject bobResponse = bob.createBobResponse(new JSONObject(alice.makeJsonObject(crypto, cred,500,names,"Alice")), new LocationAproximity(), (int)136.48902250666666, (int)77.49694290567865);
            assertTrue(alice.parseBobResponse(new SecretKey(crypto.getSecretKey()),Pk,bobResponse.toString(),loc));


    }
}