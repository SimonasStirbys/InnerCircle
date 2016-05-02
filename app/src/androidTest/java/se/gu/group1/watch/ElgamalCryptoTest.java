package se.gu.group1.watch;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class ElgamalCryptoTest extends TestCase {

    public void testGetSumOfSquaresR0() throws Exception {
        List<Integer> range = givenRangeForR(0);
        assertEquals(asList(0), range);
    }

    public void testGetSumOfSquaresR1() throws Exception {
        List<Integer> range = givenRangeForR(1);
        assertEquals(asList(0, 1, 2), range);
    }

    public void testGetSumOfSquaresR2() throws Exception {
        List<Integer> range = givenRangeForR(2);
        assertEquals(asList(0, 1, 2, 4, 5, 8), range);
    }

    public void testGetSumOfSquaresR3() throws Exception {
        List<Integer> range = givenRangeForR(3);
        assertEquals(asList(0, 1, 2, 4, 5, 8, 9, 10, 13, 18), range);
    }

    private List<Integer> givenRangeForR(int r) {
        ElgamalCrypto crypto = new ElgamalCrypto();
        crypto.initializeSumOfSquares();
        return crypto.getSumOfSquares(r);
    }
}