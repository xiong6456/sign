package com.landray.kmss.plugin.sign.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class HttpSecurity {
   private static byte[] desDecode(byte[] keybyte, byte[] src) throws Exception {
      SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
      Cipher c = Cipher.getInstance("DESede", new BouncyCastleProvider());
      c.init(2, deskey);
      return c.doFinal(src);
   }

   private static byte[] desEncode(byte[] keybyte, byte[] src) throws Exception {
      SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
      Cipher c = Cipher.getInstance("DESede", new BouncyCastleProvider());
      c.init(1, deskey);
      return c.doFinal(src);
   }

   private static byte[] rsaSign(byte[] data, byte[] privateKey) throws Exception {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      KeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
      PrivateKey priKey = keyFactory.generatePrivate(keySpec);
      Signature signature = Signature.getInstance("MD5withRSA", new BouncyCastleProvider());
      signature.initSign(priKey);
      signature.update(data);
      return signature.sign();
   }

   private static boolean rsaVerify(byte[] data, byte[] publicKey, byte[] sign) throws Exception {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      KeySpec keySpec = new X509EncodedKeySpec(publicKey);
      PublicKey pubKey = keyFactory.generatePublic(keySpec);
      Signature signature = Signature.getInstance("MD5withRSA", new BouncyCastleProvider());
      signature.initVerify(pubKey);
      signature.update(data);
      return signature.verify(sign);
   }

   public static byte[] encrypt(byte[] bs) throws Exception {
      byte[] data = desEncode(KeyStore.getHttpDESKey(), bs);
      byte[] sign = rsaSign(data, KeyStore.getHttpSignKey());
      byte[] result = new byte[128 + data.length];
      System.arraycopy(sign, 0, result, 0, 128);
      System.arraycopy(data, 0, result, 128, data.length);
      return result;
   }

   public static byte[] decrypt(byte[] bs) throws Exception {
      byte[] sign = new byte[128];
      byte[] data = new byte[bs.length - 128];
      System.arraycopy(bs, 0, sign, 0, 128);
      System.arraycopy(bs, 128, data, 0, data.length);
      if (!rsaVerify(data, KeyStore.getHttpVerifyKey(), sign)) {
         throw new Exception("签名错误");
      } else {
         return desDecode(KeyStore.getHttpDESKey(), data);
      }
   }
}
