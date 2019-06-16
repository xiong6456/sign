//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.landray.kmss.plugin.sign.security;

import com.landray.kmss.plugin.Activator;
import net.sf.json.JSONArray;
import org.hyperic.sigar.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HardwareGather {
	private JSONArray result = new JSONArray();
	private Sigar sigar = new Sigar();

	private HardwareGather() {
	}

	private static void _checkLib() throws IOException {
		String path = Activator.PluginPath + "kmss/sigar";
		System.setProperty("sigar.libPath", path);
		File file = new File(path + "/sigar-file.txt");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			InputStream in = Activator.class.getResourceAsStream("/data/sigar-file.txt");

			String[] fileNames;
			try {
				int len = in.available();
				byte[] bs = new byte[len];

				while (true) {
					if (len <= 0) {
						fileNames = (new String(bs, "UTF-8")).split("\r\n");
						break;
					}

					len -= in.read(bs, bs.length - len, len);
				}
			} finally {
				in.close();
			}

			String[] var7 = fileNames;
			int var6 = fileNames.length;

			for (int var19 = 0; var19 < var6; ++var19) {
				String fileName = var7[var19];
				file = new File(path + "/" + fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				FileOutputStream out = new FileOutputStream(file);
				in = Activator.class.getResourceAsStream("/data/" + fileName);

				try {
					byte[] bs = new byte[8192];

					int len;
					while ((len = in.read(bs)) != -1) {
						out.write(bs, 0, len);
					}
				} finally {
					out.close();
					in.close();
				}
			}
		}

	}

	public static JSONArray gather() throws Exception {
		checkLib();
		HardwareGather gather = new HardwareGather();
		gather.run();
		return gather.result;
	}

	private static void checkLib() throws IOException {
		return;
	}

	private void run() throws SigarException {
		Sigar sigar = new Sigar();

		try {
			this.cpu();
			this.memory();
			this.file();
			this.net();
		} finally {
			sigar.close();
		}

	}

	private void cpu() throws SigarException {
		Map<String, Integer> mhz = new HashMap();
		Map<String, Integer> model = new HashMap();
		CpuInfo[] var6;
		int var5 = (var6 = this.sigar.getCpuInfoList()).length;

		for (int var4 = 0; var4 < var5; ++var4) {
			CpuInfo cpuInfo = var6[var4];
			String key = String.valueOf(cpuInfo.getMhz());
			if (mhz.containsKey(key)) {
				mhz.put(key, (Integer) mhz.get(key) + 1);
			} else {
				mhz.put(key, 1);
			}

			key = cpuInfo.getModel();
			if (model.containsKey(key)) {
				model.put(key, (Integer) model.get(key) + 1);
			} else {
				model.put(key, 1);
			}
		}

		Iterator var9 = mhz.entrySet().iterator();

		Entry entry;
		while (var9.hasNext()) {
			entry = (Entry) var9.next();
			this.result.add("cpu:" + (String) entry.getKey() + " * " + entry.getValue());
		}

		var9 = model.entrySet().iterator();

		while (var9.hasNext()) {
			entry = (Entry) var9.next();
			this.result.add("cpu model:" + (String) entry.getKey() + " * " + entry.getValue());
		}

	}

	private void memory() throws SigarException {
		Mem mem = this.sigar.getMem();
		this.result.add("memory:" + mem.getTotal());
	}

	private void file() throws SigarException {
		FileSystem[] var4;
		int var3 = (var4 = this.sigar.getFileSystemList()).length;

		for (int var2 = 0; var2 < var3; ++var2) {
			FileSystem fs = var4[var2];
			if (fs.getType() == 2) {
				try {
					FileSystemUsage usage = this.sigar.getFileSystemUsage(fs.getDirName());
					this.result.add("file:" + fs.getSysTypeName() + " " + usage.getTotal());
				} catch (Exception var6) {
				}
			}
		}

	}

	private void net() throws SigarException {
		String[] var4;
		int var3 = (var4 = this.sigar.getNetInterfaceList()).length;

		for (int var2 = 0; var2 < var3; ++var2) {
			String name = var4[var2];
			NetInterfaceConfig cfg = this.sigar.getNetInterfaceConfig(name);
			if (!"127.0.0.1".equals(cfg.getAddress()) && (cfg.getFlags() & 8L) == 0L && !"00:00:00:00:00:00".equals(cfg.getHwaddr()) && !cfg.getDescription().contains(" Virtual ")) {
				String info = "mac:" + cfg.getHwaddr();
				if (!this.result.contains(info)) {
					this.result.add(info);
				}
			}
		}

	}
}
