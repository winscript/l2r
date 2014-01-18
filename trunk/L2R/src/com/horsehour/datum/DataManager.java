package com.horsehour.datum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.horsehour.filter.LineParserFilter;
import com.horsehour.function.ActivationFunction;
import com.horsehour.math.MathLib;
import com.horsehour.util.FileManager;

/**
 * DataManager执行数据的基本管理-加载、预处理、选择特征、增添特征等
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130327
 */
public class DataManager {
	/**
	 * 数据集预处理:剔除label完全相同的query、特征值全部为零的文档
	 * @param dataset
	 */
	public static void preprocess(DataSet dataset){
		for(int idx = 0; idx < dataset.size(); idx++){
			SampleSet sampleset = dataset.getSampleSet(idx);
			float average = 0;
			List<Integer> labels = sampleset.getLabelList();
			for(float label : labels)
				average += label;
			
			average /= sampleset.size();
			
			boolean tie = true;
			for(int label : labels){
				if(label != average){
					tie = false;
					break;
				}
			}
			
			if(tie){
				dataset.removeSampleSet(idx);
				idx--;
			}
		}
	}

	/**
	 * 给指定数据集添加新的特征
	 * @param dataset
	 * @param newFeature
	 */
	public static void addFeature(DataSet dataset, float[][] newFeature){
		SampleSet sampleSet;
		for(int qid = 0; qid < dataset.size(); qid++){
			sampleSet = dataset.getSampleSet(qid);
			for(int i = 0; i < sampleSet.size(); i++)
				sampleSet.getSample(i).addFeature(newFeature[qid][i]);
		}
	}

	/**
	 * 检索数据集中部分特征构成新数据集
	 * @param dataset
	 * @param fids
	 * @return new data set based on fids
	 */
	public static DataSet retrievalData(DataSet dataset, int[] fids){
		SampleSet sampleset;
		for(int i = 0; i < dataset.size(); i++){
			sampleset = dataset.getSampleSet(i);
			sampleset = retrievalSample(sampleset, fids);
		}
		return dataset;
	}

	/**
	 * 检索数据集中部分特征构成新数据集
	 * @param sampleset
	 * @param fids
	 * @return new sample set based on fids
	 */
	public static SampleSet retrievalSample(SampleSet sampleset, int[] fids){
		Sample sample;
		for(int i = 0; i < sampleset.size(); i++){
			sample = sampleset.getSample(i);
			for(int j = 0; j < fids.length; j++)
				sample = new Sample(sample, fids);
		}
		return sampleset;
	}

	/**
	 * 给数据集标签添加服从均匀分布的随机扰动项（Stochastic Disturbance Term)
	 * <p>TODO:均衡训练数据集-各相关等级的文档数目，各检索词关联文档的数目</p>
	 * <p>TODO:修改学习模型，融入模糊性</p>
	 * <p>TODO:参考ELECTRE III方法</p>
	 * @param dataset
	 */
	public static void polishLabel(DataSet dataset){
		SampleSet sampleSet;
		for(int qid = 0; qid < dataset.size(); qid++){
			sampleSet = dataset.getSampleSet(qid);
			for(int sid = 0; sid < sampleSet.size(); sid++){
				int label = sampleSet.getLabel(sid);
				label += MathLib.randUniform(-0.01f, 0.01f);
				sampleSet.getSample(sid).setLabel(label);
			}
		}
	}
	
	/**
	 * 根据func对数据集中的label做变换
	 * @param dataset
	 * @param func
	 * @param newLabels
	 */
	public static double[][] transLabel(DataSet dataset, ActivationFunction func){
		double[][] newLabels = new double[dataset.size()][];
		for(int qid = 0; qid < dataset.size(); qid++){
			SampleSet sampleSet = dataset.getSampleSet(qid);

			int sz = sampleSet.size();
			newLabels[qid] = new double[sz];

			for(int sid = 0; sid < sz; sid++)
				newLabels[qid][sid] = func.calc(sampleSet.getLabel(sid));

			MathLib.normalize(newLabels[qid]);
		}
		return newLabels;
	}
	
	/**
	 * 从数据文件载入DataSet对象
	 * @param src
	 * @param enc
	 * @param lineParser
	 * @return dataset
	 */
	public static DataSet loadDataSet(String src, String enc, LineParserFilter lineParser){
		BufferedReader br;
		int dim = 0, totalNum = 0;
		List<SampleSet> samplesets = new ArrayList<SampleSet>();
		
		SampleSet sampleset;
		Sample sample = null;
		String line = "";
		String qid = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				sample = (Sample) lineParser.parse(line.trim());//使用lineParser解析行
				if(qid.equals(sample.getQid())){
					sampleset = samplesets.get(samplesets.size() - 1);
					sampleset.addSample(sample);
				}else{
					sampleset = new SampleSet();
					sampleset.addSample(sample);
					samplesets.add(sampleset);
					qid = sample.getQid();
				}
				totalNum++;
			}
			dim = sample.getDim();//Assume the data set is consistent
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return new DataSet(samplesets, dim, totalNum);
	}

	public static DataSet loadDataSet(String src, LineParserFilter lineParser){
		return loadDataSet(src, "utf-8", lineParser);
	}
	/**
	 * <p>从src中导出数据,数据默认格式:<br/>
	 * col1 col2 col3 ....<br/>
	 * 数据列间由delim分隔
	 * </p>
	 * 
	 * @param src
	 * @param enc
	 * @param delim
	 * @return datum in src
	 */
	public static List<double[]> loadDatum(String src, String enc, String delim){
		BufferedReader br;
		
		int nCol = 0;
		
		List<double[]> datum = new ArrayList<double[]>();
		
		String line = "";
		String[] entries = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				entries = line.trim().split(delim);
				nCol = entries.length;
				
				double[] data = new double[nCol];
				for(int i = 0; i < nCol; i++)
					data[i] = Double.parseDouble(entries[i]);
				
				datum.add(data);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return datum;
	}
	
	public static List<double[]> loadDatum(String src, String delim){
		return loadDatum(src, "utf-8", delim);
	}
	/**
	 * @param src
	 * @param enc
	 * @param lineParser
	 * @return 加载样本集合
	 */
	public static SampleSet loadSampleSet(String src, String enc, LineParserFilter lineParser){
		BufferedReader br;
		
		SampleSet sampleset = new SampleSet();
		Sample sample;
		String line = "";
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				sample = (Sample) lineParser.parse(line.trim());//使用lineParser解析行
				sampleset.addSample(sample);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return sampleset;
	}
	
	public static SampleSet loadSampleSet(String src, LineParserFilter lineParser){
		return loadSampleSet(src, "utf-8", lineParser);
	}
	/**
	 * @param src
	 * @param enc
	 * @return 载入rate set同时保存userid同itemid的映射表（itemid为主键）
	 */
	public static RateSet loadRateSet(String src, String enc){
		BufferedReader br = null;
		RateSet rateset = new RateSet();
		
		Preference pref = null;
		int key = -1;
		List<Integer> val = null;
		
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				String[] subs = line.trim().split("\t");
				if(subs.length < 3)
					continue;

				pref = new Preference(Integer.parseInt(subs[1]),Float.parseFloat(subs[2]));
				rateset.addPreference(Integer.parseInt(subs[0]), pref);

				//保存itemid userid映射表
				key = Integer.parseInt(subs[1]);//item id
				if(rateset.map.containsKey(key))
					val = rateset.map.get(key);
				else
					val = new ArrayList<Integer>();
				
				val.add(Integer.parseInt(subs[0]));
				rateset.map.put(key, val);
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return rateset;
	}

	public static RateSet loadRateSet(String src){
		return loadRateSet(src, "utf-8");
	}

	/**
	 * @param src
	 * @param enc
	 * @return user-item pair map
	 */
	public static Map<Integer,List<Integer>> loadUserItemMap(String src, String enc){
		BufferedReader br = null;
		//LinkedHashMap先进先出
		Map<Integer, List<Integer>> map = new LinkedHashMap<Integer, List<Integer>>();
		List<Integer> val = null;
		
		int key = -1;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				String[] subs = line.trim().split("\t");

				if(subs.length < 2)
					continue;
				
				key = Integer.parseInt(subs[0]);
				if(map.containsKey(key))
					val = map.get(key);
				else
					val = new ArrayList<Integer>();

				val.add(Integer.parseInt(subs[1]));
				map.put(key, val);
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	public static Map<Integer,List<Integer>> loadUserItemMap(String src){
		return loadUserItemMap(src, "utf-8");
	}
	
	/**
	 * 将数据分割成m份
	 * @param src
	 */
	public static void splitData(String src, String enc, int m){
		BufferedReader br = null;
		List<String> lineList = null;
		int key = -1, preKey = -1;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src),enc));
			while((line = br.readLine()) != null){
				String[] subs = line.trim().split("\t");
				if(subs.length < 2)
					continue;
				
				key = Integer.parseInt(subs[0]);
				if(preKey != key){
					if(preKey != -1)
						randomSplit(lineList, m, src);
					
					lineList = new ArrayList<String>();
					preKey = key;
				}
				lineList.add(line.trim());
			}
			br.close();
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

	public static void splitData(String src, int m){
		splitData(src, "utf-8", m);
	}

	/**
	 * 将src中的lineList随机分配到m个文件中
	 * @param lineList
	 * @param m
	 * @param src
	 */
	private static void randomSplit(List<String> lineList, int m, String src) {
		int num = Math.round(lineList.size()/m);
		String dest = new File(src).getParent();
		
		StringBuffer sb;
		String line;
		for(int i = 0; i < m - 1; i++){
			Random rand = new Random();
			sb = new StringBuffer();
			int idx;
			for(int j = 0; j < num; j++){
				idx = rand.nextInt(lineList.size());
				line = lineList.remove(idx);
				sb.append(line + "\r\n");
			}
			FileManager.writeFile(dest + "/S" + (i + 1) + ".txt", sb.toString());
		}
		
		sb = new StringBuffer();
		for(int k = 0; k < lineList.size(); k++){
			line = lineList.get(k);
			sb.append(line + "\r\n");
		}
		FileManager.writeFile(dest + "/S" + m + ".txt", sb.toString());
	}
	
	/***
	 * 将dataset(in src)中的描述文本清除
	 * @param src
	 * @param dest
	 */
	public static void cleanDataSet(String src, String dest, LineParserFilter lineParser){
		DataSet dataset = loadDataSet(src, lineParser);
		int sz = dataset.size();
		for(int i = 0; i < sz; i++){
			SampleSet sampleset = dataset.getSampleSet(i);
			StringBuffer sb = new StringBuffer();
			int m = sampleset.size();
			for(int j = 0; j < m; j++){
				Sample sample = sampleset.getSample(j);
				sb.append(sample.getLabel() + "\t" + sample.getQid());
				for(double feature : sample.getFeatures())
					sb.append("\t" + feature);
				sb.append("\r\n");
			}
			
			FileManager.writeFile(dest, sb.toString());
		}
	}
}
