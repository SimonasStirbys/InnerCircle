package se.gu.group1.watch;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Omar on 4/22/2016.
 */
public class LocationAproximityTest extends TestCase {

    public void testInProx() throws Exception {
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=0;
        int yA=0;
        int xB=0;
        int yB=1;
        CipherText a0 = crypto.encryption(Pk, new BigInteger(String.valueOf((int) Math.pow(xA, 2) + (int) Math.pow(yA, 2))));
        CipherText a1 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * xA)));
        CipherText a2 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * yA)));
            LocationAproximity locationTest=new LocationAproximity();
        CipherText D = locationTest.bobComputes(Pk, a0, a1, a2, yB, xB);
        ArrayList<CipherText> compute=locationTest.LessThan(D, 4, Pk);
        boolean result=locationTest.InProx(compute,Pk,new SecretKey(crypto.getSecretKey()));
        assertTrue(result);

    }

    public void testDistance() throws Exception {
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=0;
        int yA=0;
        int xB=0;
        int yB=1;
        CipherText a0 = crypto.encryption(Pk, new BigInteger(String.valueOf((int) Math.pow(xA, 2) + (int) Math.pow(yA, 2))));
        CipherText a1 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * xA)));
        CipherText a2 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * yA)));
        LocationAproximity locationTest=new LocationAproximity();
        CipherText D = locationTest.bobComputes(Pk, a0, a1, a2, yB, xB);
        assertEquals(false, crypto.decrypt(Pk, new SecretKey(crypto.getSecretKey()), D));


    }
    public void testDistance2() throws Exception {
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=0;
        int yA=0;
        int xB=0;
        int yB=0;
        CipherText a0 = crypto.encryption(Pk, new BigInteger(String.valueOf((int) Math.pow(xA, 2) + (int) Math.pow(yA, 2))));
        CipherText a1 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * xA)));
        CipherText a2 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * yA)));
        LocationAproximity locationTest=new LocationAproximity();
        CipherText D = locationTest.bobComputes(Pk, a0, a1, a2, yB, xB);
        assertEquals(true, crypto.decrypt(Pk, new SecretKey(crypto.getSecretKey()), D));


    }
    public void testNotInProx() throws Exception {
        ElgamalCrypto crypto=new ElgamalCrypto();
        PublicKey Pk=new PublicKey(crypto.getP(),crypto.getG(),crypto.getY());
        int xA=0;
        int yA=0;
        int xB=0;
        int yB=5;
        CipherText a0 = crypto.encryption(Pk, new BigInteger(String.valueOf((int) Math.pow(xA, 2) + (int) Math.pow(yA, 2))));
        CipherText a1 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * xA)));
        CipherText a2 = crypto.encryption(Pk, new BigInteger(String.valueOf(2 * yA)));
        LocationAproximity locationTest=new LocationAproximity();
        CipherText D = locationTest.bobComputes(Pk, a0, a1, a2, yB, xB);
        ArrayList<CipherText> compute=locationTest.LessThan(D, 4, Pk);
        boolean result=locationTest.InProx(compute,Pk,new SecretKey(crypto.getSecretKey()));
        assertFalse(result);

    }
}