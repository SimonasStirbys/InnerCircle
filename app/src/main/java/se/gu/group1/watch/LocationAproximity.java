package se.gu.group1.watch;

import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

//import org.bouncycastle.jcajce.provider.asymmetric.ElGamal;

public class LocationAproximity {

	 ElgamalCrypto elgamal = new ElgamalCrypto();
	String name;
	public  ArrayList<CipherText> LessThan(CipherText d,int radius,PublicKey Pk){
      //  ArrayList<Integer> range=elgamal.getSumOfSquares(radius);
		ArrayList<CipherText> result=new ArrayList<>();
		Random rand=new Random();
		radius*=radius;
		int value;

		for(int i=0;i<radius-1;i++){
            value=rand.nextInt(radius)+1;
            result.add(elgamal.multWithNum(Pk,(elgamal.subtract(Pk, d, elgamal.encryption(Pk, new BigInteger(String.valueOf(i))))),new BigInteger(String.valueOf(value))));

		}
//		for(int i:range){ //-1 border not included
//			value=rand.nextInt(radius)+1;
//			result.add(elgamal.multWithNum(Pk, (elgamal.add(Pk, d,elgamal.getInverse(i,Pk))), new BigInteger(String.valueOf(value))));
//		}
		return result;
		
	}
	public  boolean InProx(ArrayList<CipherText> result,PublicKey Pk,SecretKey secretK){
		for (int i = 0; i < result.size(); i++) {
			Log.d("locationaproximity", String.valueOf( (float)i/result.size()));
			CipherText cipher = result.get(i);
			if (elgamal.decrypt(Pk, secretK, cipher)) {
                Log.d("locationaproximity", "HOORAY!!!");
				return true;
			}
		}
		return false;
	}
	public  CipherText bobComputes(PublicKey Pk,CipherText a0,CipherText a1,CipherText a2,int yB,int xB){
		BigInteger xB2=new BigInteger(String.valueOf(xB)).pow(2);
		BigInteger yB2=new BigInteger(String.valueOf(yB)).pow(2);
		BigInteger sqaures = xB2.add(yB2);
		CipherText sub1=elgamal.add(Pk,a0 , elgamal.encryption(Pk, sqaures));
		CipherText crossX=(elgamal.multWithNum(Pk, a1, new BigInteger(String.valueOf(xB))));
		CipherText crossY=elgamal.multWithNum(Pk, a2, new BigInteger(String.valueOf(yB)));
		CipherText sub2=elgamal.add(Pk, crossX,crossY);
		CipherText D=elgamal.subtract(Pk, sub1, sub2);
		return D;
	}
	
}
