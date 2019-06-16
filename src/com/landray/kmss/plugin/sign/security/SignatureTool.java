package com.landray.kmss.plugin.sign.security;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

public class SignatureTool {
   private Signature signature;
   private byte[] signInfo;
   private byte[] customInfo;

   public String signInit(Map params) {
      try {
         JSONObject data = this.buildParam(params, new String[]{"username", "password", "svnAddr", "modules"});
         data.put("method", "sign");
         JSONObject result = SignServerRequest.request(data);
         if ("false".equals(result.get("success"))) {
            return result.getString("errorMessage");
         } else {
            byte[] timeBs = result.getString("time").getBytes();
            byte[] userBs = result.getString("info").getBytes();
            this.customInfo = Base64.decodeBase64(result.getString("customerInfo").getBytes("UTF-8"));
            this.signInfo = new byte[2 + timeBs.length + userBs.length];
            this.signInfo[0] = (byte)(timeBs.length - 128);
            System.arraycopy(timeBs, 0, this.signInfo, 2, timeBs.length);
            this.signInfo[1] = (byte)(userBs.length - 128);
            System.arraycopy(userBs, 0, this.signInfo, 2 + timeBs.length, userBs.length);
            byte[] signKey = Base64.decodeBase64(result.getString("signKey").getBytes("UTF-8"));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(signKey);
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);
            this.signature = Signature.getInstance("MD5withRSA", new BouncyCastleProvider());
            this.signature.initSign(priKey);
            return null;
         }
      } catch (Throwable var10) {
         var10.printStackTrace();
         return "获取签名服务信息失败";
      }
   }

   public InputStream sign(File file) throws Throwable {
      try {
         InputStream in = new FileInputStream(file);
         this.signature.update(this.signInfo, 2, this.signInfo.length - 2);
         this.signature.update(this.customInfo);
         byte[] bs = new byte[8192];

         int len;
         while((len = in.read(bs)) != -1) {
            this.signature.update(bs, 0, len);
         }

         byte[] sign = this.signature.sign();
         bs = new byte[this.signInfo.length + sign.length];
         System.arraycopy(this.signInfo, 0, bs, 0, this.signInfo.length);
         System.arraycopy(sign, 0, bs, this.signInfo.length, sign.length);
         return new ByteArrayInputStream(bs);
      } catch (Throwable var6) {
				throw var6;
      }
   }

   public JSONObject license(Map params)   {
      try {
         JSONObject data = this.buildParam(params, new String[]{"username", "password", "licenseFile"});
         data.put("method", "license");
         return SignServerRequest.request(data);
      } catch (Throwable var3) {
         return SignServerRequest.buildError("临时License下载失败");
      }
   }

   public String registMachine(Map params) {
      try {
         JSONObject data = this.buildParam(params, new String[]{"username", "password", "description"});
         data.put("method", "registMachine");
         JSONObject result = SignServerRequest.request(data);
         return "false".equals(result.get("success")) ? result.getString("errorMessage") : null;
      } catch (Throwable var4) {
         return "注册本机信息失败";
      }
   }

   private JSONObject buildParam(Map params, String[] keys)  {
      JSONObject data = new JSONObject();
      String[] var7 = keys;
      int var6 = keys.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         String key = var7[var5];
         String value = (String)params.get(key);
         if (value != null) {
            data.put(key, value);
         }
      }

      return data;
   }
}
