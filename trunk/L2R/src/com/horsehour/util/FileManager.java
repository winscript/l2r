package com.horsehour.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * FileManager文件管理工具类：基本的文件读写、重命名、删除、解析等
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20110601
 */
public class FileManager {
	
	/**
	 * @param src
	 * @return 读取文件数据 
	 */
	public static String readFile(File src){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(src));
			String line = "";
			while((line=reader.readLine()) != null)
				sb.append(line + "\r\n");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * @param src
	 * @return 读取文件数据
	 */
	public static String readFile(String src){
		return readFile(new File(src));
	}
	
	/**
	 * @param src
	 * @param enc
	 * @return 根据指定的编码读取文件数据
	 */
	public static String readFile(String src, String enc){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(src), enc));
			String line = "";
			while((line = reader.readLine()) != null)
				sb.append(line).append("\r\n");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * 向文件写数据
	 * @param dest
	 * @param content
	 */
	public static void writeFile(String dest, String content){
		writeFile(dest, content, true);
	}
	
	/**
	 * 向文件写入输入
	 * @param dest
	 * @param content
	 * @param append
	 */
	public static void writeFile(String dest, String content, boolean append){
		BufferedWriter writer = null;
		File destFile = new File(dest);
		try {
			if(!destFile.exists()) 
				destFile.createNewFile();
			
			writer = new BufferedWriter(new FileWriter(destFile, append));
			writer.write(content);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 将内容以指定编码写入文件
	 * @param dest
	 * @param content
	 * @param enc
	 */
	public static void writeFile(String dest, String content, String enc){
		BufferedWriter writer = null;
		File destFile = new File(dest);
		try {
			if(!destFile.exists()) 
				destFile.createNewFile();

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), enc));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * @param dir
	 * @return 获取指定目录下的所有文件名
	 */
	public static File[] getFileList(String dir){
		File root = new File(dir); 
		File[] files = null;
		if(dir.isEmpty()){
			System.out.println("The directory isnot exist!");
			return null;
		}
		
		if(root.isDirectory())
			files = root.listFiles();
		else{
			files = new File[1];
			files[0] = root; 
		}
		
		return files;
	}
	
	/**
	 * 将指定目录下的全部文件合并到一个文件中
	 * @param dir
	 * @param dest
	 */
	public static void merge(String dir, String dest){
		File directory = new File(dir);
		File[] files = directory.listFiles();
		for(File file : files)
			writeFile(dest, readFile(file));
	}
	
	/**
	 * 重命名
	 * @param src
	 * @param dest
	 * @return 命名成功返回true,否则返回false
	 */
	public static boolean rename(String src, String dest){
		File file = new File(src);
		return file.renameTo(new File(dest));
	}
	
	/**
	 * 删除目录下全部文件
	 * @param dir
	 */
	public static void deleteDir(String dir){
		File[] files = new File(dir).listFiles();
		for(File file : files)
			file.delete();
	}
	
	/**
	 * 拷贝文件
	 * @param src The source file.
	 * @param dest The copied file.
	 */
	public static void copyFile(String src, String dest){
			FileInputStream fis  = null;
			FileOutputStream fos = null;

			try {
				fis = new FileInputStream(new File(src));
				fos = new FileOutputStream(new File(dest));

				byte[] buf = new byte[40960];
				int i = 0;
				while ((i = fis.read(buf)) != -1)
					fos.write(buf, 0, i);

				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
	}
	
	/**
	 * 拷贝目录
	 * @param srcDir The source directory.
	 * @param destDir The target directory.
	 * @param files The files to be copied, names but not directory.
	 */
	public static void copyFiles(String srcDir, String destDir, List<String> files){
		for(int i = 0; i < files.size(); i++)
			copyFile(srcDir + files.get(i), destDir + files.get(i));
	}
	
	
	private static final int BUF_SIZE = 51200;
	
	/**
	 * 解压文件
	 * @param gzFile 压缩文件
	 * @param dirOutput	解压到文件
	 * @return true if succeed, false otherwise.
	 */
	@SuppressWarnings("resource")
	public static boolean gunzipFile(File gzFile, File dirOutput) {
		// Create a buffered gzip input stream to the archive file.
		GZIPInputStream gzip_in_stream;
		FileInputStream in = null;
		try {
			in = new FileInputStream(gzFile);
			BufferedInputStream source = new BufferedInputStream (in);
			gzip_in_stream = new GZIPInputStream(source);

			String file_input_name = gzFile.getName ();
			String file_output_name = file_input_name.substring (0, file_input_name.length () - 3);

			File output_file = new File (dirOutput, file_output_name);

			byte[] input_buffer = new byte[BUF_SIZE];

			int len = 0;
			FileOutputStream out = new FileOutputStream(output_file);
			BufferedOutputStream dest = new BufferedOutputStream (out, BUF_SIZE);

			while ((len = gzip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1)
				dest.write (input_buffer, 0, len);
			
			dest.flush ();
			out.close ();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean gunzipFile(String gzFile, String dirOutput){
		return gunzipFile(new File(gzFile), new File(dirOutput));
	}
	
	/**
	 * 压缩文件
	 * @param zipFile 添加文件
	 * @param gzipFilename 压缩文件名称
	 * @return true if succeeds, false otherwise
	 */
	public static boolean gzipFile(String zipFile, String gzipFilename){
		try {
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(gzipFilename));
			FileInputStream in = new FileInputStream(zipFile);
			byte[] buf = new byte[BUF_SIZE];
			
			int len;
			
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			
			in.close();

			out.finish();
			out.close();
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 将对象序列化为二进制,保存到指定文件中
	 * @param o
	 * @param dest
	 */
	public static void serialize(Object o, String dest){
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(dest, false));
			oos.writeObject(o);
			oos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 反序列化操作
	 * @param src
	 * @return 序列化的对象
	 */
	@SuppressWarnings("resource")
	public static Object deSerialize(String src){
		ObjectInputStream ois = null;
		Object o = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(src));
			o = ois.readObject();
			ois.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return o;
	}

	/**
	 * 读取文件中的键值对(一行一对,无重复)
	 * @param src
	 * @param enc
	 * @param map
	 */
	public static void loadResource(String src, String enc, HashMap<String,String> map){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			String line = "";
			int idx = 0;
			while((line = br.readLine()) != null){
				line = line.trim();
				idx = line.indexOf("=");
				if(idx>1)
					map.put(line.substring(0, idx), line.substring(idx+1));
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 读取文件中键值对（循环读取,有重复）,count行构成一组
	 * @param br
	 * @param map
	 * @param count
	 */
	public static void loadResource(BufferedReader br, HashMap<String,String> map, int count){
		String line = "";
		int idx = 0;
		try {
			while((line = br.readLine()) != null && map.size() < count){
				idx = line.indexOf("=");
				if(idx != -1){
					line = line.trim();
					map.put(line.substring(0, idx), line.substring(idx+1));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 加载属性文件到Properties对象
	 * @param propFile
	 * @param prop
	 */
	public static void loadResource(String propFile, Properties prop){
		FileInputStream fis;
		try {
			fis = new FileInputStream(propFile);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 逐行保存文件中的内容到lines
	 * @param src
	 * @param lines
	 */
	public static void readLines(String src, List<String> lines){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			String line = "";
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;
				else
					lines.add(line);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 一行一个数据
	 * @param src
	 * @return
	 */
	public static List<Double> readLines(String src){
		List<Double> lines = new ArrayList<Double>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			String line = "";
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;
				else
					lines.add(Double.parseDouble(line));
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return lines;
	}
	
	/**
	 * 逐行写入
	 * @param datum
	 * @param dest
	 */
	public static void writeLines(List<Double> datum, String dest){
		StringBuffer sb = new StringBuffer();
		int m = datum.size();
		for(int i = 0; i < m; i++)
			sb.append(datum.get(i) + "\r\n");
		
		FileManager.writeFile(dest, sb.toString());
	}
	
	/**
	 * 逐行写入
	 * @param datum
	 * @param dest
	 */
	public static void writeLines(double[] datum, String dest){
		StringBuffer sb = new StringBuffer();
		int m = datum.length;
		for(int i = 0; i < m; i++)
			sb.append(datum[i] + "\r\n");
		
		FileManager.writeFile(dest, sb.toString());
	}

	/**
	 * 转化-将文件中的键值对保存为HashMap<String,Vector<String>>类型，并序列化，比如提取导师信息
	 * @param src
	 * @param enc
	 * @param dest
	 */
	public static void serializeList(String src, String enc, String dest){
		HashMap<String,String> list = new HashMap<String,String>();
		HashMap<String,Vector<String>> bulk = new HashMap<String,Vector<String>>();
		FileManager.loadResource(src,enc, list);
		Set<String> keySet = list.keySet();
		for(String key : keySet){
			String[] entries = list.get(key).split("\t");
			Vector<String> val = new Vector<String>();
			for(String entry : entries)
				val.add(entry);
			bulk.put(key, val);
		}
		serialize(bulk, dest);
	}
}