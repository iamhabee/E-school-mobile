package com.efulltech.efupay.e_school;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by wangfeiteng on 2018-04-04.
 */

public class DES {
    //加密
    public static byte[] Encrypt(byte[] data, byte[] key){
        byte[] result = null ;
        try {
            SecureRandom sr = new SecureRandom();
            SecretKeyFactory keyFactory;
            DESKeySpec dks = new DESKeySpec(key);
            keyFactory = SecretKeyFactory.getInstance("org.example.orafucharles.e_school.DES");
            SecretKey secretkey = keyFactory.generateSecret(dks);
            //创建Cipher对象
            Cipher cipher = Cipher.getInstance("org.example.orafucharles.e_school.DES/ECB/NoPadding");
            //初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretkey,sr);
            //加解密
            result = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }
    //解密
    public static byte[] Decryption(byte[] data, byte[] key){
        byte[] result = null ;
        try {
            SecureRandom sr = new SecureRandom();
            SecretKeyFactory keyFactory;
            DESKeySpec dks = new DESKeySpec(key);
            keyFactory = SecretKeyFactory.getInstance("org.example.orafucharles.e_school.DES");
            SecretKey secretkey = keyFactory.generateSecret(dks);
            //创建Cipher对象
            Cipher cipher = Cipher.getInstance("org.example.orafucharles.e_school.DES/ECB/NoPadding");
            //初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretkey,sr);
            //加解密
            result = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }
    //加密
    public static byte[] Encrypt(String strInput,String strPassword){
        byte[] result = null ;
        try {
            SecureRandom sr = new SecureRandom();
            SecretKeyFactory keyFactory;
            DESKeySpec dks = new DESKeySpec(strPassword.getBytes());
            keyFactory = SecretKeyFactory.getInstance("org.example.orafucharles.e_school.DES");
            SecretKey secretkey = keyFactory.generateSecret(dks);
            //创建Cipher对象
            Cipher cipher = Cipher.getInstance("org.example.orafucharles.e_school.DES/ECB/NoPadding");
            //初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretkey,sr);
            //加解密
            result = cipher.doFinal(strInput.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        //String strOutPut=new String(result);

        return result;
    }
    //解密
    public static String Decryption(byte[] bInput,String strPassword){
        byte[] result = null ;
        try {
            SecureRandom sr = new SecureRandom();
            SecretKeyFactory keyFactory;
            DESKeySpec dks = new DESKeySpec(strPassword.getBytes());
            keyFactory = SecretKeyFactory.getInstance("org.example.orafucharles.e_school.DES");
            SecretKey secretkey = keyFactory.generateSecret(dks);
            //创建Cipher对象
            Cipher cipher = Cipher.getInstance("org.example.orafucharles.e_school.DES/ECB/NoPadding");
            //初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretkey,sr);
            //加解密
            result = cipher.doFinal(bInput);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        String strOutPut=new String(result);

        return strOutPut;
    }

}
