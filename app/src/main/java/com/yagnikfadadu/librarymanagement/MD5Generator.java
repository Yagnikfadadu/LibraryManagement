package com.yagnikfadadu.librarymanagement;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Generator {
    public static String getMD5(String string){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            md5.update(string.getBytes());

            byte[] hashBytes = md5.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String hashHex = sb.toString();
            return hashHex;

        } catch (NoSuchAlgorithmException e) {
            return "Error";
        }
    }
}

