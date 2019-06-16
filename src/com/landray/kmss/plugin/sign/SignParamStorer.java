package com.landray.kmss.plugin.sign;

import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.landray.kmss.plugin.Activator;
import com.landray.kmss.plugin.util.ConfigStorer;
import com.landray.kmss.plugin.util.DESUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SignParamStorer {
    private static final Log logger = LogFactory.getLog(SignParamStorer.class);

    private static SignParamStorer instance = new SignParamStorer();


    public static SignParamStorer getInstance() {
        return instance;
    }

    public void save(Map params, String[] keys,String workSpacePath)
    {
        try
        {
            Map map = new HashMap(params);

            List keyList = Arrays.asList(keys);
            if (keyList.contains("savePassword")) {
                String password = (String)map.get("password");
                if (("true".equals(map.get("savePassword"))) && (password != null)) {
                    password = encodePassword(password);
                }
                map.put("password", password);
            }

            ConfigStorer store = new ConfigStorer(getPersistenceFile(workSpacePath));
            store.update(map);
        } catch (Exception e) {
            logger.error("参数保存失败！", e);
        }
    }

    public Map load(String[] keys,String workSpacePath)
    {
        try
        {
            ConfigStorer store = new ConfigStorer(getPersistenceFile(workSpacePath));
            Map map = (Map) store.load(keys);
            String password = (String)map.get("password");
            if (password != null) {
                map.put("password", decodePassword(password));
            }
            return map;
        } catch (Exception e) {
            logger.error("参数加载失败！", e);
        }return new HashMap();
    }

    private byte[] passwordKey()
    {
        return new byte[] { 70, -113, -62, -14, 38, -123, -74, -53, -116, 8,
                25, 98, -36, 25, -74, 81, -82, 7, -63, 1, -85, -3, -50, -60 };
    }

    private String decodePassword(String password)
    {
        try {
            return new String(
                    DESUtil.decode(passwordKey(),
                            Base64.decodeBase64(password.getBytes())), "UTF-8");
        } catch (Exception e) {
            logger.error("密码解密失败！", e);
        }return null;
    }

    private String encodePassword(String password)
    {
        try
        {
            return new String(
                    Base64.encodeBase64(DESUtil.encode(passwordKey(),
                            password.getBytes("UTF-8"))));
        } catch (Exception e) {
            logger.error("密码加密失败！", e);
            e.printStackTrace();
        }
        return null;
    }

    private File getPersistenceFile(String workSpacePath)
    {
        return new File(workSpacePath+File.separator+"sign-parameters.ini");
    }
}