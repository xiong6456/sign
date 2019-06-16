package com.landray.kmss.plugin.sign;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface SignTool {
   String signInit(Map var1) throws Throwable;

   InputStream sign(File var1) throws Throwable;

   JSONObject license(Map var1) throws Throwable;

   String registMachine(Map var1);

   void close();
}
