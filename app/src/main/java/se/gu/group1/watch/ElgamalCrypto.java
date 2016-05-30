package se.gu.group1.watch;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;


public class ElgamalCrypto {


    private final BigInteger secretKey;
    private final BigInteger p;
    private final BigInteger g;
    private final BigInteger y;
    private final Random sc = new SecureRandom();

    private static final Map<Integer, List<Integer>> map = new HashMap<>();
    private static final Map<Integer, CipherText> negative = new HashMap<>();
    PublicKey Pk;

    public ElgamalCrypto() {
        secretKey = new BigInteger(1024, sc);
        p = BigInteger.probablePrime(1024, sc); // prime
        g = new BigInteger(1024, sc); // generator
        y = g.modPow(secretKey, p);
    }
    

    public CipherText encryption(PublicKey Pk, BigInteger m) {
        CipherText cipher = new CipherText();
        BigInteger X = Pk.g.modPow(m, Pk.p); // additive
        BigInteger r = new BigInteger(1024, sc);
        BigInteger C1 = X.multiply(Pk.y.modPow(r, Pk.p)).mod(Pk.p);
        BigInteger C0 = Pk.g.modPow(r, Pk.p);
		/*System.out.println("Plaintext = " + X);
		System.out.println("r = " + r);
		System.out.println("EC = " + C1);
		System.out.println("b^r mod p = " + C0);*/
        cipher.C0 = C0;
        cipher.C1 = C1;
        return cipher;
    }

    public CipherText add(PublicKey Pk, CipherText cipherA, CipherText cipherB) {
        BigInteger C0 = (cipherA.C0.multiply(cipherB.C0).mod(Pk.p));

        BigInteger C1 = cipherA.C1.multiply(cipherB.C1).mod(Pk.p);
        return new CipherText(C0, C1);
    }

    public CipherText subtract(PublicKey Pk, CipherText cipherA, CipherText cipherB) {

        BigInteger C0 = (cipherA.C0.multiply(cipherB.C0.modInverse(Pk.p))).mod(Pk.p);

        BigInteger C1 = cipherA.C1.multiply(cipherB.C1.modInverse(Pk.p)).mod(Pk.p);
        return new CipherText(C0, C1);
    }

    public CipherText multWithNum(PublicKey Pk, CipherText cipherA, BigInteger num) {

        BigInteger C0 = (cipherA.C0.modPow(num, Pk.p));

        BigInteger C1 = (cipherA.C1.modPow(num, Pk.p));

        return new CipherText(C0, C1);
    }

    public boolean decrypt(PublicKey Pk, SecretKey secretKey, CipherText cipher) {
        BigInteger crmodp = (cipher.C0).modPow(secretKey.secretKey, Pk.p);
        BigInteger d = crmodp.modInverse(Pk.p);
        BigInteger ad = d.multiply(cipher.C1).mod(Pk.p);
	/*	System.out.println("\n\nc^r mod p = " + crmodp);
		System.out.println("d = " + d);
		System.out.println("Alice decodes: " + ad);*/
        return ad.equals(BigInteger.ONE);
		/*int i;
		for (i = 0; i < 100; i++) {
			if (Pk.g.modPow(new BigInteger(String.valueOf(i)), Pk.p).equals(ad)) {
			//	System.out.println("Alice decodes :" + i);
				break;
			}
		}
		return i;
	}*/

    }

    public int decryptText(PublicKey Pk, SecretKey secretKey, CipherText cipher) {
        BigInteger crmodp = (cipher.C0).modPow(secretKey.secretKey, Pk.p);
        BigInteger d = crmodp.modInverse(Pk.p);
        BigInteger ad = d.multiply(cipher.C1).mod(Pk.p);
	/*	System.out.println("\n\nc^r mod p = " + crmodp);
		System.out.println("d = " + d);
		System.out.println("Alice decodes: " + ad);*/
        //return ad.equals(BigInteger.ONE);
        int i;
        for (i = 0; i < 100; i++) {
            if (Pk.g.modPow(new BigInteger(String.valueOf(i)), Pk.p).equals(ad)) {
                //	System.out.println("Alice decodes :" + i);
                break;
            }
        }
        return i;
    }

    public CipherText getInverse(int i) {
        return negative.get(i);
    }

    public List<Integer> getSumOfSquares(int max) {
        return map.get(max);
    }


    public void readFromFile(){
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        //File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File root = new File(Environment.getExternalStorageDirectory(), "Squares");
        File file = new File(root,"SumOfSquares.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }







    }

    public void initializeSumOfSquares(PublicKey pk) {
        /* Original python code:

        r = int(sqrt(self.r))
        for i in range(0, r):
            limit = int(sqrt(r ** 2 - i ** 2))
            for j in range(i, limit):
                sum_of_square = j ** 2 + i ** 2
                sos_queue.put(sum_of_square)
        */
            int max = 125;
            for (int r = 0; r < max; r += 25) {
                List<Integer> sumOfSquares = new ArrayList<>();
                for (int i = 0; i <= r; i++) {
                    int limit = (int) Math.ceil(Math.sqrt(Math.pow(r, 2) - Math.pow(i, 2)));
                    for (int j = i; j <= limit; j++) {
                        int sum_of_square = (int) (Math.pow(j, 2) + Math.pow(i, 2));
                        if(!sumOfSquares.contains(sum_of_square))
                        sumOfSquares.add(sum_of_square);
                        if(!negative.containsKey(sum_of_square))
                        negative.put(sum_of_square,subtract(pk, encryption(pk, new BigInteger(String.valueOf(0))), encryption(pk, new BigInteger(String.valueOf(sum_of_square)))));
                    }
                }

                if (r != 0) {
                    int sum_of_square = (int) (2 * Math.pow(r, 2));
                    sumOfSquares.add(sum_of_square);
                    if(!negative.containsKey(sum_of_square))
                        negative.put(sum_of_square,subtract(pk, encryption(pk, new BigInteger(String.valueOf(0))), encryption(pk, new BigInteger(String.valueOf(sum_of_square)))));

                }

                Collections.sort(sumOfSquares);
                map.put(r, sumOfSquares);


            }
    }

    public BigInteger getP() {
        return p;
    }


    public BigInteger getG() {
        return g;
    }


    public BigInteger getY() {
        return y;
    }


    public BigInteger getSecretKey() {
        return secretKey;
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

class SecretKey {
    public BigInteger secretKey;

    public SecretKey(BigInteger secretKey) {
        this.secretKey = secretKey;
    }
}

class CipherText {
    public BigInteger C0;
    public BigInteger C1;

    public CipherText() {

    }

    public CipherText(BigInteger C0, BigInteger C1) {
        this.C0 = C0;
        this.C1 = C1;
    }

}

class PublicKey {
    public BigInteger p, g, y;

    public PublicKey(BigInteger p, BigInteger g, BigInteger y) {
        this.p = p;
        this.g = g;
        this.y = y;
    }
}

