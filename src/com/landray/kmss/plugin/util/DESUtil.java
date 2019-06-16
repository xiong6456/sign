package com.landray.kmss.plugin.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class DESUtil {
   private static final String ALGORITHM_NAME = "DESede";

   public static byte[] decode(byte[] keybyte, byte[] src) throws Exception {
      SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
      Cipher c = Cipher.getInstance("DESede", new BouncyCastleProvider());
      c.init(2, deskey);
      return c.doFinal(src);
   }

   public static byte[] encode(byte[] keybyte, byte[] src) throws Exception {
      SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
      Cipher c = Cipher.getInstance("DESede", new BouncyCastleProvider());
      c.init(1, deskey);
      return c.doFinal(src);
   }

   public static byte[] generateKey() throws Exception {
      KeyGenerator generator = KeyGenerator.getInstance("DESede");
      generator.init(new SecureRandom());
      return generator.generateKey().getEncoded();
   }
}
