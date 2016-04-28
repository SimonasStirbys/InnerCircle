package se.gu.group1.watch;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Omar on 4/22/2016.
 */
public class ElgamalCryptoTest extends TestCase {

    public void testGetSumOfSquares() throws Exception {
        ElgamalCrypto crypto=new ElgamalCrypto();
        crypto.initializeSumOfSquares();
        ArrayList<Integer> range=crypto.getSumOfSquares(2);
        assertEquals(4,range.size());
    }
}