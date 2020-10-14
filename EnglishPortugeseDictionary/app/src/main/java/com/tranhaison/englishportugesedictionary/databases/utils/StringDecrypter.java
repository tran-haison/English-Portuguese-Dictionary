package com.tranhaison.englishportugesedictionary.databases.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StringDecrypter {

    private static String algorithm = "AES/ECB/PKCS5Padding";
    private static boolean development = false;
    private static String key = "cTlQVDJRV0wyVGtzM3U5UG1OdyY2TGEyTHRoSm1LNlMkJiphP1pld2VyUiQ2d1piWS1YbnJVZVZYJTNGWFBUKw==";
    private static String key2 = "";
    private static boolean open = true;
    public static final String EVENT_PARAM_VALUE_NO = "0";
    public static final String UTF8 = "UTF8";

    /**
     * Decrypt a string from db
     * @param str
     * @return
     */
    public static String decrypt(String str) {
        if (!open) {
            return str;
        }

        if (str.trim().equals("")) {
            return "";
        }

        try {
            int length = generateKey2().length() * 2;
            String str2 = "";
            if (!development) {
                String str3 = str2;
                for (int i = 0; i < str.length(); i++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str3);
                    sb.append((char) (str.charAt(i) - (i % length)));
                    str3 = sb.toString();
                }
                str = str3;
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(generateKey().getBytes("UTF-8"), "AES");
            Cipher instance = Cipher.getInstance(algorithm);
            byte[] bArr = new byte[16];
            for (int i2 = 0; i2 < 16; i2++) {
                bArr[i2] = 0;
            }
            instance.init(2, secretKeySpec);
            return new String(instance.doFinal(Base64.decode(str, 0)), UTF8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String generateKey() {
        try {
            int i = 0;
            String str = new String(Base64.decode(key, 0), UTF8);
            if (development) {
                str = new String(Base64.decode(key2, 0), UTF8);
            } else if (str.length() > 16) {
                String str2 = "";
                int length = str.length() / 16;
                while (true) {
                    if (i >= str.length()) {
                        break;
                    } else if (str2.length() >= 16) {
                        break;
                    } else {
                        if (i % length == 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(str2);
                            sb.append(str.charAt(i));
                            sb.append("");
                            str2 = sb.toString();
                        }
                        i++;
                    }
                }
                str = str2;
            }
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String generateKey2() {
        try {
            String str = new String(Base64.decode(key, 0), UTF8);
            if (development) {
                str = new String(Base64.decode(key2, 0), UTF8);
            }
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Decrypt a string from its type
     * @param type
     * @param id
     * @return
     */
    public static String decryptEncryptedCode(String type, int id) {
        if (!open) {
            return type;
        }

        if (type.trim().equals("")) {
            return "";
        }

        if (type.equals(EVENT_PARAM_VALUE_NO)) {
            return EVENT_PARAM_VALUE_NO;
        }

        int i2 = id % 10;
        double d = id;
        Double.isNaN(d);
        double pow = Math.pow(10.0d, (int) Math.log10(1.0d * d));
        Double.isNaN(d);
        int i3 = (int) (d / pow);
        int parseInt = Integer.parseInt(type.substring(2));

        StringBuilder sb = new StringBuilder();
        sb.append((parseInt - i3) / (i2 + 1));
        sb.append("");

        return sb.toString();
    }
}
