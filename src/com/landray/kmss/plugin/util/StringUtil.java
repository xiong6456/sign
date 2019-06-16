package com.landray.kmss.plugin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
   public static String XMLEscape(String src) {
      if (src == null) {
         return null;
      } else {
         String rtnVal = src.replaceAll("&", "&amp;");
         rtnVal = rtnVal.replaceAll("\"", "&quot;");
         rtnVal = rtnVal.replaceAll("<", "&lt;");
         rtnVal = rtnVal.replaceAll(">", "&gt;");
         return rtnVal;
      }
   }

   public static String getParameter(String query, String param) {
      Pattern p = Pattern.compile("&" + param + "=([^&]*)");
      Matcher m = p.matcher("&" + query);
      return m.find() ? m.group(1) : null;
   }

   public static Map getParameterMap(String query, String splitStr) {
      Map rtnVal = new HashMap();
      if (isNull(query)) {
         return rtnVal;
      } else {
         String[] parameters = query.split("\\s*" + splitStr + "\\s*");

         for(int i = 0; i < parameters.length; ++i) {
            int j = parameters[i].indexOf(61);
            if (j > -1) {
               rtnVal.put(parameters[i].substring(0, j), new String[]{parameters[i].substring(j + 1)});
            }
         }

         return rtnVal;
      }
   }

   public static String setQueryParameter(String query, String param, String value) {
      String rtnVal = null;

      try {
         String m_query = isNull(query) ? "" : "&" + query;
         String m_param = "&" + param + "=";
         String m_value = URLEncoder.encode(value, "UTF-8");
         Pattern p = Pattern.compile(m_param + "[^&]*");
         Matcher m = p.matcher(m_query);
         if (m.find()) {
            rtnVal = m.replaceFirst(m_param + m_value);
         } else {
            rtnVal = m_query + m_param + m_value;
         }

         rtnVal = rtnVal.substring(1);
      } catch (UnsupportedEncodingException var9) {
         var9.printStackTrace();
      }

      return rtnVal;
   }

   public static String replace(String srcText, String fromStr, String toStr) {
      if (srcText == null) {
         return null;
      } else {
         StringBuffer rtnVal = new StringBuffer();
         String rightText = srcText;

         for(int i = srcText.indexOf(fromStr); i > -1; i = rightText.indexOf(fromStr)) {
            rtnVal.append(rightText.substring(0, i));
            rtnVal.append(toStr);
            rightText = rightText.substring(i + fromStr.length());
         }

         rtnVal.append(rightText);
         return rtnVal.toString();
      }
   }

   public static String linkString(String leftStr, String linkStr, String rightStr) {
      if (isNull(leftStr)) {
         return rightStr;
      } else {
         return isNull(rightStr) ? leftStr : leftStr + linkStr + rightStr;
      }
   }

   public static boolean isNull(String str) {
      return str == null || str.trim().length() == 0;
   }

   public static boolean isNotNull(String str) {
      return !isNull(str);
   }

   public static String getString(String s) {
      return s == null ? "" : (s.equals("null") ? "" : s);
   }
}
