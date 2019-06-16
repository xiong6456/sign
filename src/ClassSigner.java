import com.landray.kmss.plugin.sign.SignTool;
import com.landray.kmss.plugin.sign.SignToolFactory;

import java.util.HashMap;
import java.util.List;

public class ClassSigner extends SignWrapper {
	public static void main(String[] args) throws Throwable {
		String[] inFiles={"F:\\java2\\workspace\\nxkj\\WebContent\\WEB-INF\\classes\\com\\landray\\kmss\\util\\ArrayUtil.class","F:\\java2\\workspace\\nxkj\\WebContent\\login_single.jsp",
		"F:\\java2\\workspace\\sign\\WebContent\\WEB-INF\\classes\\ClassSigner.class"};
		String outPath = "C:\\Users\\hevin\\Desktop\\web";
		signFiles(inFiles,  outPath);
		System.exit(0);
	}

	public static void generator(List files, String svnAddr, String username, String password, String webPath) throws Throwable{
		signFiles(files, svnAddr, username, password, webPath);
	}

	public static String regist(String username, String password, String description, String checked) throws Throwable{
		HashMap reqParam = new HashMap();
		reqParam.put("username", username);
		reqParam.put("password", password);
		reqParam.put("description", description);
		reqParam.put("savePassword", checked);
		SignTool signTool = SignToolFactory.build();
		return signTool.registMachine(reqParam);
	}
}
