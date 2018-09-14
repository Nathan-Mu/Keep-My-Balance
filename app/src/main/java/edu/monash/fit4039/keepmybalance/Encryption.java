package edu.monash.fit4039.keepmybalance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nathan on 9/5/17.
 */

public class Encryption {

    //encrypt to SHA512
    //resource: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
    public static String encryptToSHA(String string) {
        byte[] digesta = null;
        String strDes = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-512");
            alga.update(string.getBytes());
            digesta = alga.digest();
            strDes = bytes2Hex(digesta);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return strDes;
    }

    //encrypt to SHA
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
