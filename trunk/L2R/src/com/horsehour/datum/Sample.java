package com.horsehour.datum;

import java.util.Arrays;

/**
 * Sample抽象的数据样本，基本数据成员：
 * y：<x1,x2,...,xn>
 * 其中，y表示样本的标记
 * 
 * 数据样本在文件中的保存格式，如下所示：
 * 0 qid:167 1:0.34 2:1.0 ... 45:0.20 ...
 * 包含三部分，第一列是样本标记/相关等级，第二列表示样本所属的检索词id，后续列表示样本特征/属性值
 * 
 * @author Chunheng Jiang
 * @version 1.0
 */
public class Sample implements Comparable<Sample>{
	private int dim = 0;
	private int label = 0;
	private String qid = "";
	private double[] features;
	
	/**
	 * 根据已知或者随机产生的数据构造样本数据
	 * @param features
	 * @param label
	 */
	public Sample(double[] features, int label){
		this.dim = features.length;
		this.features = Arrays.copyOf(features, dim);
		this.label = label;
	}
	
	public Sample(double[] features, int label, String qid){
		this(features, label);
		this.qid = qid;
	}

	/**
	 * 使用部分特征构造新的Sample对象
	 * @param sample
	 * @param fids
	 */
	public Sample(Sample sample, int[] fids) {
		this.dim = fids.length;
		this.label = sample.label;
		this.features = new double[dim];
		double[] oldFeatures = sample.features;
		for(int i = 0; i < dim; i++)
			features[i] = oldFeatures[fids[i]];
	}

	/**
	 * 读取特征
	 */
	public double[] getFeatures(){
		return features;
	}
	
	/**
	 * 读取指定维度的特征值
	 * @param featureId
	 * @return given feature
	 */
	public double getFeature(int featureId){
		return features[featureId];
	}
	
	/**
	 * 重设指定维度的特征
	 * @param featureId
	 * @param val
	 */
	public void setFeature(int featureId, double val){
		features[featureId] = val;
	}

	/**
	 * 扩展样本特征
	 * @param f
	 */
	public void addFeature(double f){
		double[] precFeature;
		dim += 1;
		precFeature = Arrays.copyOf(features, dim);
		precFeature[dim - 1] = f;
		features = Arrays.copyOf(precFeature, dim);
	}
	
	/**
	 * 数据样本的Label
	 * @return lable of sample
	 */
	public int getLabel(){
		return label;
	}
	
	/**
	 * 设置label
	 * @param label
	 */
	public void setLabel(int label){
		this.label = label;
	}
	
	/**
	 * 数据样本的维数
	 * @return dimension of sample
	 */
	public int getDim(){
		return dim;
	}

	/**
	 * 数据样本所属的数据集合
	 * @return query_id the sample belongs to
	 */
	public String getQid(){
		return qid;
	}

	@Override
	public int compareTo(Sample sample) {
		return Integer.compare(sample.getLabel(), label);
	}
}