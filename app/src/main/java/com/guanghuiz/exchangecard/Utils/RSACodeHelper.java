package com.guanghuiz.exchangecard.Utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Guanghui on 28/2/16.
 */
public class RSACodeHelper {
    public RSAPublicKey mPublicKey;
    public RSAPrivateKey mPrivateKey;

    public void initKey(){
        KeyPairGenerator keyPairGen = null;
        try{
            // set the algorithm for encryption
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            // number of digits in miyao
            keyPairGen.initialize(512);
            //key pair
            KeyPair keyPair = keyPairGen.generateKeyPair();

            // get public and private key
            mPublicKey = (RSAPublicKey) keyPair.getPublic();
            mPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        } catch (NoSuchAlgorithmException e){

        }
    }

    // get public key
    public static PublicKey getPublicKey(String key) throws Exception{
        byte[] keyBytes = base64Dec(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    // GET PRIVATE KEY
    public static PrivateKey getPrivateKey(String key) throws Exception
    {
        byte[] keyBytes = base64Dec(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    // encrypt the
    public String encrypt(String passwd) {
        String strEncrypt = null;
        try {
            // 实例化加解密类
            Cipher cipher = Cipher.getInstance("RSA");

            // 明文
            byte[] plainText = passwd.getBytes();

            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, mPublicKey);
            //将明文转化为根据公钥加密的密文，为byte数组格式
            byte[] enBytes = cipher.doFinal(plainText);
            //为了方便传输我们可以将byte数组转化为base64的编码
            strEncrypt = base64Enc(enBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            return strEncrypt;
        }
    }

    public String decrypt(String encString) {
        Cipher cipher = null;
        String strDecrypt = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, mPrivateKey);
            //先将转为base64编码的加密后的数据转化为byte数组
            byte[] enBytes = base64Dec(encString);
            //解密称为byte数组，应该为字符串数组最后转化为字符串
            byte[] deBytes = cipher.doFinal(enBytes);

            strDecrypt = new String(deBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            return strDecrypt;
        }

    }

    //base64编码
    public static String base64Enc(byte[] enBytes) {
        return Base64.encodeToString(enBytes, Base64.DEFAULT);
    }
    //base64解码
    public static byte[] base64Dec(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

    // 存储公钥：String strPublicKey = base64Enc(mPublicKey.getEncoded())
    // 存储私钥：String strPrivateKey = base64Enc(mPrivateKey.getEncoded())

    // 还原公钥：RSAPublicKey publicKey = (RSAPublicKey)getPublicKey(strPublicKey);
    // 还原私钥：RSAPrivateKey privateKey = (RSAPrivateKey)getPrivateKey(strPrivateKey );
}
