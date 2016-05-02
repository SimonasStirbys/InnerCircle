package se.gu.group1.watch;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ElgamalCrypto {


    private final BigInteger secretKey;
    private final BigInteger p;
    private final BigInteger g;
    private final BigInteger y;
    private final Random sc = new SecureRandom();

    private static final Map<Integer, List<Integer>> map = new HashMap<>();


    public ElgamalCrypto() {
        secretKey = new BigInteger(1024, sc);
        p = BigInteger.probablePrime(1024, sc); // prime
        g = new BigInteger(1024, sc); // generator
        y = g.modPow(secretKey, p);

        if(map.size() == 0) {
            initializeSumOfSquares();
        }
    }
    /*public static void main(String[] args) throws IOException {


		//
		// public key calculation
		//
		System.out.println("secretKey = " + secretKey);
		 // Y
		//
		// Encryption
		//
		System.out.print("Enter your Big Number message -->");
		Scanner scan = new Scanner(System.in);
		String s = scan.nextLine();

		PublicKey Pk = new PublicKey(p, g, y);
		SecretKey secretK = new SecretKey(secretKey);
		*//*CipherText cipher = encryption(Pk, s);
		CipherText cipher2 = encryption(Pk, "10");
		CipherText resultAdd = add(Pk, cipher, cipher2);
		CipherText resultSub = subtract(Pk, cipher2, cipher);
		CipherText multWith5=multWithNum(Pk, cipher, 5);
		
		int result = decrypt(Pk, secretK, resultAdd);
		System.out.println("substart is " + decrypt(Pk, secretK, resultSub));
		System.out.println("Multiply is " + decrypt(Pk, secretK, multWith5));
		System.out.println("Result is " + result);*//*
		//
		// Decryption
		//
		*//*
		 * BigInteger crmodp = (C0.multiply(C0).mod(p)).modPow(secretKey, p);
		 * BigInteger d = crmodp.modInverse(p); BigInteger ad =
		 * d.multiply(C1.multiply(C1).mod(p)).mod(p); Multiplication
		 *//*

	}*/

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

    public CipherText getInverse(int i, PublicKey pk) {
        return subtract(pk, encryption(pk, new BigInteger(String.valueOf(0))), encryption(pk, new BigInteger(String.valueOf(i)))); // store in Hashmap

    }

    public List<Integer> getSumOfSquares(int max) {
        return map.get(max);
    }

    public void initializeSumOfSquares() {
        /* Original python code:

        r = int(sqrt(self.r))
        for i in range(0, r):
            limit = int(sqrt(r ** 2 - i ** 2))
            for j in range(i, limit):
                sum_of_square = j ** 2 + i ** 2
                sos_queue.put(sum_of_square)
        */

        int max = 100;
        for (int r = 0; r < max; r++) {
            List<Integer> sumOfSquares = new ArrayList<>();
            for (int i = 0; i <= r; i++) {
                int limit = (int) Math.ceil(Math.sqrt(Math.pow(r, 2) - Math.pow(i, 2)));
                for (int j = i; j <= limit; j++) {
                    int sum_of_square = (int) (Math.pow(j, 2) + Math.pow(i, 2));
                    sumOfSquares.add(sum_of_square);
                }
            }

            if(r != 0) {
                sumOfSquares.add((int) (2 * Math.pow(r, 2)));
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
