import com.landray.kmss.plugin.sign.SignTool;
import com.landray.kmss.plugin.sign.SignToolFactory;
import org.apache.batik.swing.gvt.Interactor;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract public class SignWrapper {
    private static String WIN_SEPARATOR = new String("\\");
    private static String LINUX_SEPARATOR = new String("/");

    private static final String[] SAVEKEYS = new String[]{"username", "savePassword", "password", "output", "zip",
            "scope"};

    static Map result;

    static Map loadParams() {
        result = new HashMap();
        return result;
    }

    static Map loadParams(String svnAddr,String username,String password) {
        result = new HashMap();
        result.put("svnAddr", svnAddr);
        result.put("scope", "selected");
        result.put("username", username);
        result.put("password", password);
        result.put("modules", "third/nx");
        return result;
    }

    static void copy(InputStream in, OutputStream out, boolean closeOut) throws IOException {
        try {
            byte[] bs = new byte[8192];

            int len;
            while ((len = in.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
        } finally {
            if (closeOut) {
                try {
                    out.close();
                } catch (Exception var13) {
                }
            }

            try {
                in.close();
            } catch (Exception var12) {
            }

        }

    }

    public static void createParentDir(String path) throws Exception {
        String systemSeparator = File.separator;
        if (systemSeparator.equals(WIN_SEPARATOR)) {
            createParentDirWIN(path);
        } else if (systemSeparator.equals(LINUX_SEPARATOR)) {
            createParentDirLinux(path);
        }
    }

    // Windows
    public static void createParentDirWIN(String path) throws Exception {

        // Split中特殊字符分割：
        // http://blog.csdn.net/myfmyfmyfmyf/article/details/37592711
        // \ 用 “\\\\”
        String[] pathArr = path.split("\\\\");

        StringBuffer tmpPath = new StringBuffer();
        for (int i = 0; i < pathArr.length; i++) {
            tmpPath.append(pathArr[i]).append(WIN_SEPARATOR);

            if (0 == i)
                continue;

            File file = new File(tmpPath.toString());

            if (!file.exists()) {
                file.mkdir();
                System.out.println("当前创建的目录是 : " + tmpPath.toString());
            }

        }
    }

    // Linux
    public static void createParentDirLinux(String path) throws Exception {

        String[] pathArr = path.split(LINUX_SEPARATOR);

        StringBuffer tmpPath = new StringBuffer();
        for (int i = 0; i < pathArr.length; i++) {
            tmpPath.append(pathArr[i]).append(LINUX_SEPARATOR);

            File file = new File(tmpPath.toString());
            if (!file.exists()) {
                file.mkdir();
                System.out.println("当前创建的目录是 : " + tmpPath.toString());
            }
        }
    }

    public static void signFiles(String inFiles, String outPath) throws Throwable {
        SignTool signTool = SignToolFactory.build();
        signTool.signInit(loadParams());

        String s = inFiles;
        String[] split = null;

        split = s.split("WebContent");
        //递归创建输出目录
        createParentDir(outPath + split[1].substring(0, split[1].lastIndexOf(File.separator)));
        File file = new File(s);
        if (!file.exists()) {
            throw new RuntimeException(" file not exists:" + file.getAbsolutePath());
        }
        InputStream sign = signTool.sign(file);
        if (sign == null) {
            throw new RuntimeException(" sign has failed: " + file.getAbsolutePath());
        }

        if (s.contains(".class") || s.contains(".xml") || s.contains(".jar")) {
            copy(sign, new FileOutputStream(outPath + split[1] + ".sign"), true);
        }
        copy(new FileInputStream(file.getAbsolutePath()), new FileOutputStream(outPath + split[1]), true);
    }

    public static void signFiles(String[] inFiles, String outPath) throws Throwable {
        SignTool signTool = SignToolFactory.build();
        signTool.signInit(loadParams());

        String s;
        String[] split = null;

        for (int i = 0; i < inFiles.length; i++) {
            s = inFiles[i];
            split = s.split("WebContent");
            //递归创建输出目录
            createParentDir(outPath + split[1].substring(0, split[1].lastIndexOf(File.separator)));
            File file = new File(s);
            if (!file.exists()) {
                throw new RuntimeException(" file not exists:" + file.getAbsolutePath());
            }
            InputStream sign = signTool.sign(file);
            if (sign == null) {
                throw new RuntimeException(" sign has failed: " + file.getAbsolutePath());
            }

            if (s.contains(".class") || s.contains(".xml") || s.contains(".jar")) {
                copy(sign, new FileOutputStream(outPath + split[1] + ".sign"), true);
            }
            copy(new FileInputStream(file.getAbsolutePath()), new FileOutputStream(outPath + split[1]), true);
        }
    }

    public static void signFiles(List<String> files, String svnAddr, String username, String password, String outPath) throws Throwable {
        SignTool signTool = SignToolFactory.build();
        signTool.signInit(loadParams(svnAddr, username, password));

        String[] split = null;

        Iterator iterator = files.iterator();
        while (iterator.hasNext()) {
            String next =  (String)iterator.next();
            split = next.split("WebContent");
            //递归创建输出目录
            createParentDir(outPath + split[1].substring(0, split[1].lastIndexOf(File.separator)));
            File file = new File(next);
            if (!file.exists()) {
                throw new RuntimeException(" file not exists:" + file.getAbsolutePath());
            }
            InputStream sign = signTool.sign(file);
            if (sign == null) {
                throw new RuntimeException(" sign has failed: " + file.getAbsolutePath());
            }

            if (next.contains(".class") || next.contains(".xml") || next.contains(".jar")) {
                copy(sign, new FileOutputStream(outPath + split[1] + ".sign"), true);
            }
            copy(new FileInputStream(file.getAbsolutePath()), new FileOutputStream(outPath + split[1]), true);
        }
    }
}
