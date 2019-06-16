package com.landray.kmss.plugin.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    public static File[] searchFiles(String s) {
        return searchFiles("./", s);
    }

    public static File[] searchFiles(String dir, String s) {
        File file = new File(dir);
        s = s.replace('.', '#');
        s = s.replaceAll("#", "\\\\.");
        s = s.replace('*', '#');
        s = s.replaceAll("#", ".*");
        s = s.replace('?', '#');
        s = s.replaceAll("#", ".?");
        s = "^" + s + "$";
        Pattern p = Pattern.compile(s);
        List list = filePattern(file, p);
        if (list == null) {
            return null;
        } else {
            File[] rtn = new File[list.size()];
            list.toArray(rtn);
            return rtn;
        }
    }

    private static List filePattern(File file, Pattern p) {
        if (file == null) {
            return null;
        } else {
            ArrayList list;
            if (file.isFile()) {
                Matcher fMatcher = p.matcher(file.getName());
                if (fMatcher.matches()) {
                    list = new ArrayList();
                    list.add(file);
                    return list;
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    list = new ArrayList();

                    for (int i = 0; i < files.length; ++i) {
                        List rlist = filePattern(files[i], p);
                        if (rlist != null) {
                            list.addAll(rlist);
                        }
                    }

                    return list;
                }
            }

            return null;
        }
    }

    public static boolean compareFile(InputStream in1, InputStream in2) throws IOException {
        if (in1.available() != in2.available()) {
            return false;
        } else {
            byte[] b1 = new byte[65536];
            byte[] b2 = new byte[65536];

            for (int n = in1.read(b1); n > 0; n = in1.read(b1)) {
                in2.read(b2);

                for (int i = 0; i < n; ++i) {
                    if (b1[i] != b2[i]) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static void writeFile(File file, String content) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);

        try {
            fos.write(content.getBytes("UTF-8"));
        } finally {
            fos.close();
        }

    }

    public static String readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String content = "";
        if(!file.exists()){
            return content;
        }
        byte[] bs = new byte[1024];
        try {
            while (fis.read() != -1) {
                String str = new String(bs, 0, fis.read());
                content += str;
            }
        } finally {
            fis.close();
        }
        return content;
    }

    public static void copyFile(InputStream source, File target) throws IOException {
        if (!target.exists()) {
            target.getParentFile().mkdirs();
            target.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(target);

        try {
            copyFile((InputStream) source, (OutputStream) fos);
        } finally {
            fos.close();
            source.close();
        }

    }

    public static void copyFile(InputStream source, OutputStream target) throws IOException {
        int BUFSIZE = 65536;
        byte[] buf = new byte[BUFSIZE];

        int i;
        while ((i = source.read(buf)) > -1) {
            target.write(buf, 0, i);
        }

    }

    public static void copyFile(String fromPath, String toPath) throws Exception {
        File fromFile = null;
        if (fromPath != null) {
            fromFile = new File(fromPath);
            if (!fromFile.exists()) {
                fromFile = null;
            }
        }

        File toFile = new File(toPath);
        if (fromFile == null) {
            if (toFile.exists() && toFile.delete()) {
                deleteEmptyFolder(toFile.getParentFile());
            }
        } else {
            if (!toFile.exists()) {
                File parent = toFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                toFile.createNewFile();
            }

            int BUFSIZE = 65536;
            FileInputStream fis = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(toFile);

            try {
                byte[] buf = new byte[BUFSIZE];

                int i;
                while ((i = fis.read(buf)) > -1) {
                    fos.write(buf, 0, i);
                }
            } finally {
                fis.close();
                fos.close();
            }
        }

    }

    public static void deleteEmptyFolder(File folder) {
        if (folder.list().length == 0 && folder.delete()) {
            deleteEmptyFolder(folder.getParentFile());
        }

    }

    private static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }

        File dirFile = new File(sPath);
        if (dirFile.exists() && dirFile.isDirectory()) {
            boolean flag = true;
            File[] files = dirFile.listFiles();

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                return dirFile.delete();
            }
        } else {
            return false;
        }
    }

    public static boolean deleteFolder(String sPath) {
        File file = new File(sPath);
        if (!file.exists()) {
            return false;
        } else {
            return file.isFile() ? deleteFile(sPath) : deleteDirectory(sPath);
        }
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }

        return flag;
    }

    public static String checkFileName(String path) {
        Pattern pattern = Pattern.compile("^[\\s\\$\\.\\-\\w]+$");
        Matcher matcher = null;
        File file = new File(path);
        File[] files = file.listFiles();
        StringBuffer result = new StringBuffer();
        File[] var9 = files;
        int var8 = files.length;

        for (int var7 = 0; var7 < var8; ++var7) {
            File fl = var9[var7];
            if (fl.isDirectory()) {
                matcher = pattern.matcher(fl.getName());
                if (!matcher.matches()) {
                    result.append("Path:" + fl.getPath() + "\r\n");
                }

                result = result.append(checkFileName(fl.toString()));
            } else {
                matcher = pattern.matcher(fl.getName());
                if (!matcher.matches()) {
                    result.append("Path:" + fl.getPath() + "\r\n");
                }
            }
        }

        return result.toString();
    }

    public static void jar(String jarFileName, String inputFilePath, String base) {
        JarOutputStream jarOutputStream = null;

        try {
            File inputFile = new File(inputFilePath);
            jarOutputStream = new JarOutputStream(new FileOutputStream(jarFileName));
            jar(jarOutputStream, inputFile, base);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            try {
                jarOutputStream.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

    }

    private static void jar(JarOutputStream jarOutputStream, File inputFile, String base) throws IOException {
        int b;
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            jarOutputStream.putNextEntry(new JarEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";

            for (b = 0; b < files.length; ++b) {
                jar(jarOutputStream, files[b], base + files[b].getName());
            }
        } else {
            jarOutputStream.putNextEntry(new JarEntry(base));
            FileInputStream in = null;

            try {
                in = new FileInputStream(inputFile);

                while ((b = in.read()) != -1) {
                    jarOutputStream.write(b);
                }
            } finally {
                in.close();
            }
        }

    }

    public String getFileText(String Path) throws Exception {
        File file = new File(Path);
        String text = "";
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

        for (String str = null; (str = bf.readLine()) != null; text = text + str + "\n") {
        }

        bf.close();
        return text;
    }

    public String getFileText(InputStream inputStream) throws Exception {
        String text = "";
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        for (String str = null; (str = bf.readLine()) != null; text = text + str + "\n") {
        }

        bf.close();
        return text;
    }
}
