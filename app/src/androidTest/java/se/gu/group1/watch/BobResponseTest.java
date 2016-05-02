package se.gu.group1.watch;

import android.support.annotation.NonNull;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Omar on 4/25/2016.
 */
public class BobResponseTest extends TestCase {

    public void testCreateBobResponse() throws Exception {
        BobResponse bob = new BobResponse("Bob");
        AliceRequest alice = new AliceRequest();
        CipherText[] cred = new CipherText[3];
        ElgamalCrypto crypto = new ElgamalCrypto();
        PublicKey Pk = new PublicKey(crypto.getP(), crypto.getG(), crypto.getY());
        int xA = 0, yA = 1;
        alice.generateEncryptedLocation(crypto, Pk, cred, xA, yA);
        ArrayList<String> names = new ArrayList<>();
        names.add("Alice");
        String aliceRequest = alice.makeJsonObject(crypto, cred, 500, names, "Alice");
        JSONObject bobResponse = bob.createBobResponse(emulateGCM(aliceRequest), new LocationAproximity(), 0, 3, 10);

        assertEquals(198, bobResponse.getJSONObject("Answer_Location").getJSONArray("Answer").length());
    }

    @NonNull
    private JSONObject emulateGCM(String aliceRequest) throws JSONException {
        JSONObject jsonObject = new JSONObject(aliceRequest);
        return jsonObject.getJSONObject("Requests").getJSONObject("Cred");
    }

}