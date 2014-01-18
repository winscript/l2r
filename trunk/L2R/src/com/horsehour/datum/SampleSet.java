package com.horsehour.datum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.horsehour.math.MathLib;

/**
 * SampleSet样本数据集
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20131024 样本子集抽取
 */
public class SampleSet{
	private List<Sample> samples;
	
	public SampleSet(){
		samples = new ArrayList<Sample>();
	}
	
	/**
	 * 取得样本数目
	 * @return samples' size
	 */
	public int size(){
		return samples.size();
	}
	
	/**
	 * 取得全部样本
	 * @return all the samples in sampleset
	 */
	public List<Sample> getSamples(){
		return samples;
	}

	/**
	 * @param num
	 * @param with replacement
	 * @return 重采样（num个样本）
	 */
	public SampleSet resample(int num, boolean with){
		int sz = size(), id;
		SampleSet ss = new SampleSet();
		if(with){
			for(int i = 0; i < num; i++){
				id = MathLib.rand(0, sz - 1);
				ss.addSample(samples.get(id));
			}
		}else if(num < sz){
			List<Integer> ids = MathLib.randUnique(0, sz-1, num);
			Collections.sort(ids);

			for(int i = num - 1; i >= 0; i--)
				ss.addSample(samples.remove(i));
		}
		
		return ss;
	}

	/**
	 * Retrieves and removes num sample from samples
	 * @param num
	 * @return 从samples中抽离num个样本
	 */
	public SampleSet pollSamples(int num){
		int sz = size();
		if(num > sz)
			return null;

		List<Integer> ids = MathLib.randUnique(0, sz-1, num);
		Collections.sort(ids);
		
		SampleSet ss = new SampleSet();
		for(int i = num - 1; i >= 0; i--)
			ss.addSample(samples.remove(i));

		return ss;
	}

	/**
	 * 添加样本
	 * @param sample
	 */
	public void addSample(Sample sample){
		samples.add(sample);
	}
	
	/**
	 * 取得全部样本的label
	 * @return all the label of sample in sampleset
	 */
	public List<Integer> getLabelList() {
		List<Integer> labels = new ArrayList<Integer>();
		for(Sample sample : samples)
			labels.add(sample.getLabel());
		
		return labels;
	}

	public int[] getLabels() {
		int sz = size();
		int[] labels = new int[sz];
		for(int i = 0; i < sz; i++)
			labels[i] = getLabel(i);
		return labels;
	}
	
	/**
	 * @return 样本类标集合
	 */
	public List<Integer> getUniqueLabels(){
		TreeSet<Integer> labels = new TreeSet<Integer>();
		labels.addAll(getLabelList());
		List<Integer> group = new ArrayList<Integer>();
		Iterator<Integer> iter = labels.iterator();
		while(iter.hasNext())
			group.add(iter.next());
		return group;
	}

	/**
	 * 取得指定位置样本的label
	 * @param idx
	 * @return given sample's label
	 */
	public int getLabel(int idx){
		return samples.get(idx).getLabel();
	}
	
	/**
	 * 取得指定位置的样本
	 * @param idx
	 * @return sample at idx
	 */
	public Sample getSample(int idx) {
		return samples.get(idx);
	}
	
	public void removeSample(int idx){
		samples.remove(idx);
	}
	/**
	 * 取得维数
	 * @return sample's dimension
	 */
	public int getDim() {
		return samples.get(0).getDim();
	}
	
	/**
	 * 获取指定全部样本的指定维的特征值
	 * @param fid
	 * @return feature list at fid
	 */
	public List<Double> getFeatureList(int fid){
		List<Double> featureVals = new ArrayList<Double>();
		for(Sample sample : samples)
			featureVals.add(sample.getFeature(fid));
		
		return featureVals;
	}
	
	public double[] getFeatures(int fid){
		int sz = size();
		double[] featureValue = new double[sz];
		for(int i = 0; i < sz; i++)
			featureValue[i] = samples.get(i).getFeature(fid);
		return featureValue;
	}
	/**
	 * @param fid
	 * @param theta
	 * @return 抽取特征值大于theta的样本列表
	 */
	public List<Integer> getSampleIndex(int fid, double theta){
		List<Integer> idx = new ArrayList<Integer>();
		int i = 0;
		for(double val : getFeatureList(fid)){
			if(val > theta)
				idx.add(i);
			i++;
		}
		return idx;
	}
	
	/**
	 * @param subset
	 * @return 从中选取指定的一个子集
	 */
	public SampleSet subset(List<Integer> subset){
		SampleSet sub = new SampleSet();
		for(int i : subset)
			sub.addSample(samples.get(i));
		return sub;
	}
	
	/**
	 * 统计指定类标下的样本个数
	 * @param labels
	 * @return 样本分布
	 */
	public int[] getDistribute(List<Integer> labels){
		int m = samples.size(), n = labels.size();
		Map<Integer, Integer> stat = new HashMap<Integer, Integer>();
		for(int label : labels)
			stat.put(label, 0);
		
		int count = 0;
		for(int i = 0; i < m; i++){
			int label = samples.get(i).getLabel();
			if(stat.containsKey(label)){
				count = stat.get(label);
				stat.put(label, count + 1);
			}
		}

		int[] distr = new int[n];
		for(int i = 0; i < n; i++)
			distr[i] = stat.get(labels.get(i));
		
		return distr;
	}
	
	/**
	 * 根据标签进行分组
	 * @return 按照标签分组结果
	 */
	public List<List<Integer>> group(){
		List<Integer> levelList = new ArrayList<Integer>();//相关等级列表
		List<List<Integer>> cluster = new ArrayList<List<Integer>>();//每个相关等级对应的文档列表

		int n = size();
		for(int i = 0; i < n; i++){
			int level = getLabel(i);
			int idx = levelList.indexOf(level);
			if(idx == -1){
				levelList.add(level);
				cluster.add(new ArrayList<Integer>());
				idx = cluster.size() - 1;
			}
			cluster.get(idx).add(i);
		}
		return cluster;
	}
}