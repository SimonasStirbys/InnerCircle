package se.gu.group1.watch;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

//import org.bouncycastle.jcajce.provider.asymmetric.ElGamal;

public class LocationAproximity {
	static BigInteger secretKey, p, g, y;
	static Random sc = new SecureRandom();
	static ElgamalCrypto elgamal = new ElgamalCrypto();
	static PublicKey Pk;
	static SecretKey secretK;
/*	public static void main(String[] args) {
		
		secretKey = new BigInteger(1024, sc);
		p = BigInteger.probablePrime(1024, sc); // prime
		g = new BigInteger(1024, sc); // generator
		y = g.modPow(secretKey, p); // Y
		Pk = new PublicKey(p, g, y);
		secretK = new SecretKey(secretKey);
		//0.0011473767999258601690107832429
		///6.8389867075511722537449081653362e-6
		double var=146159.48310176897; // divide by 10
		
		int xA =(int) (136.48829937777776 * var);
		int yA = (int)(77.49588352927762*var);
		int xB=(int)(136.4881484088889*var);
		int yB=(int)(77.49474612789042*var);
		double distance=Math.sqrt(Math.pow(xA-xB, 2)+Math.pow(yA-yB, 2));
		System.out.println(distance);
		System.out.println(167.7/distance);
		CipherText a0 = elgamal.encryption(Pk, (int)Math.pow(xA, 2) + (int)Math.pow(yA, 2));
		CipherText a1 = elgamal.encryption(Pk, 2 * xA);
		CipherText a2 = elgamal.encryption(Pk, 2 * yA);
		*//*CipherText sub1=elgamal.add(Pk,a0 , elgamal.encryption(Pk, (int)Math.pow(yA, 2) + (int)Math.pow(yB, 2)));
		CipherText crossX=(elgamal.multWithNum(Pk, a1, new BigInteger(String.valueOf(xB))));
		CipherText crossY=elgamal.multWithNum(Pk, a2, new BigInteger(String.valueOf(yB)));
		CipherText sub2=elgamal.add(Pk, crossX,crossY);	
		CipherText D=elgamal.subtract(Pk, sub1, sub2);*//*
		CipherText D=bobComputes(Pk, a0, a1, a2, yA, yB, xB);
		ArrayList<CipherText> result=LessThan(D, 20);
		System.out.println("Result of InProx "+InProx(result));
		
	}*/
	public static ArrayList<CipherText> LessThan(CipherText d,int radius,PublicKey Pk){
		ArrayList<CipherText> range=new ArrayList<>();
		Random rand=new Random();
		radius*=radius;
		int value;
		
		for(int i=0;i<radius-1;i++){ //-1 border not included
			value=rand.nextInt(radius)+1;
			range.add(elgamal.multWithNum(Pk,(elgamal.subtract(Pk, d, elgamal.encryption(Pk, i))),new BigInteger(String.valueOf(value))));
		}
		return range;
		
	}
	public static boolean InProx(ArrayList<CipherText> result){
		for(CipherText cipher:result)
		if(elgamal.decrypt(Pk, secretK, cipher)){
			return true;
			
		}
		return false;
	}
	public static CipherText bobComputes(PublicKey Pk,CipherText a0,CipherText a1,CipherText a2,int yA,int yB,int xB){
		CipherText sub1=elgamal.add(Pk,a0 , elgamal.encryption(Pk, (int)Math.pow(yA, 2) + (int)Math.pow(yB, 2)));
		CipherText crossX=(elgamal.multWithNum(Pk, a1, new BigInteger(String.valueOf(xB))));
		CipherText crossY=elgamal.multWithNum(Pk, a2, new BigInteger(String.valueOf(yB)));
		CipherText sub2=elgamal.add(Pk, crossX,crossY);	
		CipherText D=elgamal.subtract(Pk, sub1, sub2);
		return D;
	}
	
}
