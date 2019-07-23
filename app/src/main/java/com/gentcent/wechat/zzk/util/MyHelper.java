package com.gentcent.wechat.zzk.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @author zuozhi
 * @since 2019-07-09
 */
public class MyHelper {
	public static String SDCARD_PATH = "/sdcard/more-chat/";
	
	/**
	 * 初始化文件夹
	 */
	private static void mkdir() {
		File file = new File(SDCARD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 根据路径获取文件
	 */
	public static File getFile(String str) {
		String sb = SDCARD_PATH + str;
		File file = new File(sb);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception unused) {
			}
		}
		return file;
	}
	
	/**
	 * 根据路径删除单个文件
	 */
	public static boolean delete(String str) {
		File file = new File(SDCARD_PATH + str);
		if (!file.exists() || file.isDirectory()) {
			return true; //文件不存在 在逻辑上属于成功
		}
		return file.delete();
	}
	
	/**
	 * 删除文件或文件夹
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				file.delete();
				return;
			}
			for (File a2 : listFiles) {
				deleteFile(a2);
			}
			file.delete();
		}
	}
	
	
	/**
	 * 读取文件第一行
	 *
	 * @param fileName 文件名
	 * @return 文件第一行
	 */
	public static String readLine(String fileName , String defaultVal) {
		return readFileAtFristLine(SDCARD_PATH, fileName, defaultVal);
	}
	
	/**
	 * 读取文件第一行
	 *
	 * @param path       文件路径
	 * @param fileName   文件名
	 * @param defaultVal 默认值
	 * @return 文件第一行
	 */
	private static String readFileAtFristLine(String path, String fileName, String defaultVal) {
		try {
			File file = new File(path + fileName);
			if (!file.isFile() || !file.exists()) {
				return defaultVal;
			}
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), HttpUtils.ENCODING_UTF_8);
			String readLine = new BufferedReader(inputStreamReader).readLine();
			if (TextUtils.isEmpty(readLine)) {
				XLog.e("GetString lineTxt is empty " + fileName);
			}
			inputStreamReader.close();
			if (readLine != null) {
				defaultVal = readLine;
			}
			return defaultVal;
		} catch (Exception e) {
			XLog.e("GetString error is " + Log.getStackTraceString(e));
			return defaultVal;
		}
	}
	
	/**
	 * 写入文件(单行)
	 * @param fileName 文件名
	 * @param content  内容
	 */
	public static void writeLine(String fileName, String content){
		delete(fileName);
		writeOneLineFile(SDCARD_PATH, fileName, content);
	}
	
	
	/**
	 * 写入文件(单行)
	 *
	 * @param path     路径
	 * @param fileName 文件名
	 * @param content  内容
	 */
	private static void writeOneLineFile(String path, String fileName, String content) {
		BufferedWriter bufferedWriter = null;
		try {
			mkdir();
			BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path + fileName), false), HttpUtils.ENCODING_UTF_8));
			try {
				bufferedWriter2.write(content);
				bufferedWriter2.flush();
				try {
					bufferedWriter2.close();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					XLog.e("SetString key is " + fileName + " error is " + Log.getStackTraceString(e));;
				}
			} catch (Exception e2) {
				bufferedWriter = bufferedWriter2;
				try {
					e2.printStackTrace();
					XLog.e("SetString key is " + fileName + " error is " + e2.getMessage());;
				} catch (Throwable th) {
					try {
						bufferedWriter.close();
					} catch (IOException e3) {
						e3.printStackTrace();
						XLog.e("SetString key is " + fileName + " error is " + e3.getMessage());;
					}
					throw th;
				}
			} catch (Throwable th2) {
				bufferedWriter = bufferedWriter2;
				throw th2;
			}
		} catch (Exception e4) {
			e4.printStackTrace();
			XLog.e("SetString key is " + fileName + " error is " + e4.getMessage());;
			if (bufferedWriter == null) {
				try {
					bufferedWriter.close();
				} catch (IOException e5) {
					e5.printStackTrace();
					XLog.e("SetString key is " + fileName + " error is " + e5.getMessage());;
				}
			}
		}
	}
	
	
	/**
	 * 复制单个文件
	 * @param source 来源路径
	 * @param target 目标路径
	 */
	public static void copyFile(String source, String target) {
		try {
			source = SDCARD_PATH + source;
			target = SDCARD_PATH + target;
			if (new File(source).exists()) {
				FileInputStream fileInputStream = new FileInputStream(source);
				FileOutputStream fileOutputStream = new FileOutputStream(target);
				byte[] bArr = new byte[1444];
				int i = 0;
				while (true) {
					int read = fileInputStream.read(bArr);
					if (read != -1) {
						i += read;
						fileOutputStream.write(bArr, 0, read);
					} else {
						fileInputStream.close();
						return;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	
}
