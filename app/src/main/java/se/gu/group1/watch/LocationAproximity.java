package se.gu.group1.watch;

import android.content.Context;
import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//import org.bouncycastle.jcajce.provider.asymmetric.ElGamal;

public class LocationAproximity {

    ElgamalCrypto elgamal = new ElgamalCrypto();

    public ArrayList<CipherText> LessThan(CipherText d, int radius, PublicKey Pk) {
        final long start = System.nanoTime();
        List<Integer> range = elgamal.getSumOfSquares(radius);
        ArrayList<CipherText> result = new ArrayList<>();
        Random rand = new Random();

        for (int i : range) {
            //CipherText subtractBy = elgamal.encryption(Pk, new BigInteger(String.valueOf(i)));// add to map
            //CipherText negattive = elgamal.multWithNum(Pk, subtractBy, new BigInteger(String.valueOf("-1")));
            CipherText subtraction = elgamal.add(Pk, d, elgamal.getInverse(i));
            //CipherText subtraction = elgamal.subtract(Pk, d, subtractBy);
            BigInteger random = new BigInteger(Pk.p.bitCount(), rand);
            CipherText randomized = elgamal.multWithNum(Pk, subtraction, random);
            result.add(randomized);
        }
        Collections.shuffle(result);
        final long end = System.nanoTime();
        System.out.println("LessThan Took: " + ((end - start) / 1000000000) + "s);
        return result;
    }

    public boolean InProx(ArrayList<CipherText> result, PublicKey Pk, SecretKey secretK) {
        final long start = System.nanoTime();
        for (int i = 0; i < result.size(); i++) {
            Log.d("locationaproximity", String.valueOf((float) i / result.size()));
            CipherText cipher = result.get(i);
            if (elgamal.decrypt(Pk, secretK, cipher)) {
                Log.d("locationaproximity", "HOORAY!!!");
                return true;
            }
        }
        final long end = System.nanoTime();
        System.out.println("InProx Took: " + ((end - start) / 1000000000) + "s);
        return false;
    }

    public CipherText bobComputes(PublicKey Pk, CipherText a0, CipherText a1, CipherText a2, int yB, int xB) {
        BigInteger xB2 = new BigInteger(String.valueOf(xB)).pow(2);
        BigInteger yB2 = new BigInteger(String.valueOf(yB)).pow(2);
        BigInteger sqaures = xB2.add(yB2);
        CipherText sub1 = elgamal.add(Pk, a0, elgamal.encryption(Pk, sqaures));
        CipherText crossX = (elgamal.multWithNum(Pk, a1, new BigInteger(String.valueOf(xB))));
        CipherText crossY = elgamal.multWithNum(Pk, a2, new BigInteger(String.valueOf(yB)));
        CipherText sub2 = elgamal.add(Pk, crossX, crossY);
        CipherText D = elgamal.subtract(Pk, sub1, sub2);
        return D;
    }

}
