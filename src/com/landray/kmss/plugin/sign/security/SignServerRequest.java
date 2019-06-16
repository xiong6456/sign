package com.landray.kmss.plugin.sign.security;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignServerRequest {

   private static String prepareData(JSONObject data) throws Exception {
      data.put("id", UUID.randomUUID().toString());
      data.put("version", "2014.08.21.001");
      data.put("hardware", HardwareGather.gather());
      data.put("hardware_name", InetAddress.getLocalHost().getHostName());
      byte[] dataBs = HttpSecurity.encrypt(data.toString().getBytes("UTF-8"));
      return new String(Base64.encodeBase64(dataBs), "UTF-8");
   }

   public static JSONObject request(JSONObject data)  {
      String postData;
      try {
         postData = prepareData(data);
      } catch (Throwable var3) {
         var3.printStackTrace();
         return buildError("获取本地信息错误");
      }

      JSONObject result = doPost((String) data.get("method"), postData);
      if (!"false".equals(result.get("success")) && !data.get("id").equals(result.get("id"))) {
         return buildError("服务端返回信息不匹配");
      } else {
         result.remove("id");
         return result;
      }
   }

   private static JSONObject doPost(String method, String data)  {
      HttpClient client = new DefaultHttpClient();
      client.getParams().setParameter("http.connection.timeout", 60000);
      client.getParams().setParameter("http.socket.timeout", 60000);

      byte[] response;
      try {
         HttpPost post = new HttpPost("http://verify.landray.com.cn/action");
         List params = new ArrayList();
         params.add(new BasicNameValuePair("method", method));
         params.add(new BasicNameValuePair("data", data));
         post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
         HttpResponse res = client.execute(post);
         int statusCode = res.getStatusLine().getStatusCode();
         if (200 != statusCode) {
            return buildError("服务端响应状态错误：" + statusCode);
         }

         HttpEntity entity = res.getEntity();
         if (entity == null || !entity.isStreaming()) {
            return buildError("无法读取服务端返回数据");
         }

         InputStream in = entity.getContent();
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            byte[] bs = new byte[4096];

            while(true) {
               int len;
               if ((len = in.read(bs)) == -1) {
                  response = out.toByteArray();
                  break;
               }

               out.write(bs, 0, len);
            }
         } finally {
            try {
               in.close();
            } catch (Exception var23) {
            }

            try {
               out.close();
            } catch (Exception var22) {
            }

         }
      } catch (Exception var26) {
         return buildError("无法链接到云服务器，请检查本地是否能连外网");
      }

      try {
         byte[] bs = HttpSecurity.decrypt(Base64.decodeBase64(response));
         return JSONObject.fromObject(new String(bs, "UTF-8"));
//         return new JSONObject(new String(bs, "UTF-8"));
      } catch (Exception var24) {
         return buildError("服务端返回数据格式不正确");
      }
   }

   public static JSONObject buildError(String message)  {
      JSONObject rtn = new JSONObject();
      rtn.put("success", "false");
      rtn.put("errorMessage", message);
      rtn.put("errorCode", "local");
      return rtn;
   }
}
