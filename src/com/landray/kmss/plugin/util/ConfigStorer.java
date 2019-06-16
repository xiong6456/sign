package com.landray.kmss.plugin.util;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ConfigStorer {
   private File file;

   public ConfigStorer(File file) {
      this.file = file;
   }

   public JSONObject load() throws IOException {
      if (!this.file.exists()) {
         return new JSONObject();
      } else {
         FileInputStream in = new FileInputStream(this.file);

         try {
            int len = in.available();

            byte[] bs;
            for(bs = new byte[len]; len > 0; len -= in.read(bs, bs.length - len, len)) {
            }

//            JSONObject var5 = new JSONObject(new String(bs, "UTF-8"));
            JSONObject var5 = JSONObject.fromObject(new String(bs, "UTF-8"));
            return var5;
         } finally {
            try {
               in.close();
            } catch (IOException var9) {
            }

         }
      }
   }

   public JSONObject load(String[] keys) throws Exception {
      JSONObject json = this.load();
      JSONObject rtnJson = new JSONObject();
      String[] var7 = keys;
      int var6 = keys.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         String key = var7[var5];
         rtnJson.put(key, json.get(key));
      }

      return rtnJson;
   }

   private void saveJson(JSONObject json) throws Exception {
      if (!this.file.exists()) {
         this.file.getParentFile().mkdirs();
         this.file.createNewFile();
      }

      FileOutputStream out = new FileOutputStream(this.file);

      try {
         byte[] bs = json.toString(4).getBytes("UTF-8");
         out.write(bs);
         out.flush();
      } finally {
         try {
            out.close();
         } catch (IOException var8) {
         }

      }

   }

   public void save(Map map) throws Exception {
      JSONObject json = new JSONObject();
      if (map instanceof JSONObject) {
         json = (JSONObject)map;
      } else {
//         json = new JSONObject(map);
         json.putAll(map);
      }

      this.saveJson(json);
   }

   public void save(Map map, String[] keys) throws Exception {
      JSONObject json = new JSONObject();
      String[] var7 = keys;
      int var6 = keys.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         String key = var7[var5];
         json.put(key, map.get(key));
      }

      this.saveJson(json);
   }

   public void update(Map map) throws Exception {
      JSONObject json = this.load();
//      new JSONObject(map);
      json.putAll(map);
      this.saveJson(json);
   }

   public void update(Map map, String[] keys) throws Exception {
      JSONObject json = this.load();
      String[] var7 = keys;
      int var6 = keys.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         String key = var7[var5];
         json.put(key, map.get(key));
      }

      this.saveJson(json);
   }
}
