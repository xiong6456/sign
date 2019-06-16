import com.landray.kmss.plugin.Activator;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import com.landray.kmss.plugin.sign.SignTool;
import com.landray.kmss.plugin.sign.SignToolFactory;

import java.io.FileOutputStream;
import java.util.HashMap;

public class LicenseGenerator extends SignWrapper {
	public static String generator(String versionField,String username,String password,String webPath) throws Throwable {
		SignTool signTool = SignToolFactory.build();
		HashMap reqParam = new HashMap();
		reqParam.put("username", username);
		reqParam.put("password", password);
		reqParam.put("licenseFile", versionField);
		reqParam.put("licenseFiles", versionField);
		JSONObject result = signTool.license(reqParam);

		if (!"false".equals(result.get("success"))) {
			byte[] bs = Base64.decodeBase64(result.getString("license").getBytes("UTF-8"));
			FileOutputStream fileOutputStream = new FileOutputStream(webPath+versionField);
			fileOutputStream.write(bs);
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		String rtnStr = (String) result.get("errorMessage");
		return result.toString();
	}

}
