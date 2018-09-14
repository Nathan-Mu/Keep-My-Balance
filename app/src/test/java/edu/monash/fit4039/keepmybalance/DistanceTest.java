package edu.monash.fit4039.keepmybalance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nathan on 22/5/17.
 */
public class DistanceTest {

    @Test
    public void getDistance() throws Exception {
        double d = Distance.getDistance(0, 0, 1, 1);
        Assert.assertEquals(157425.537108412, d);
    }

}