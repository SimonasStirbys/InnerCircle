package se.gu.group1.watch;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Omar on 4/25/2016.
 */
public class BobResponseTest extends TestCase {

    public void testCreateBobResponse() throws Exception {
    BobResponse bob=new BobResponse("Bob");
        AliceRequest alice=new AliceRequest();
        CipherText[] cred=new CipherText[3];
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=0,yA=1;
       alice.generateEncryptedLocation(crypto,Pk,cred,xA,yA);
        ArrayList<String> names=new ArrayList<>();
        names.add("Alice");
        JSONObject bobResponse = bob.createBobResponse(new JSONObject(alice.makeJsonObject(crypto, cred,500,names,"Alice")), new LocationAproximity(), 0, 3, 10);



           // assertEquals(160,bobResponse.length());
    }

}