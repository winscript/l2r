package com.horsehour.model;

import java.io.Serializable;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.util.DeepCopy;

/**
 * 抽象模型-分类、聚类、回归与排名
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20130311
 * @since 20131115
 */
public abstract class Model implements Serializable{
	private static final long serialVersionUID = 716029837150978339L;

	/**
	 * 预测数据集
	 * @param dataset
	 * @return 预测结果
	 */
	public double[][] predict(DataSet dataset){
		int sz = dataset.size();
		double[][] scores = new double[sz][];
		for(int i = 0; i < sz; i++)
			scores[i] = predict(dataset.getSampleSet(i));
		return scores;
	}

	/**
	 * 预测数据集
	 * @param dataset
	 * @return 检索词层级上的标准化预测分值
	 */
	public double[][] normPredict(DataSet dataset){
		int sz = dataset.size();
		double[][] scores = new double[sz][];
		for(int i = 0; i < sz; i++)
			scores[i] = normPredict(dataset.getSampleSet(i));
		return scores;
	}

	/**
	 * 预测单个检索词关联的多个文档的相关分值
	 * @param sampleset
	 * @return 相关分值列表
	 */
	public double[] predict(SampleSet sampleset){
		int sz = sampleset.size();
		double[] score = new double[sz];

		for(int i = 0; i < sz; i++)
			score[i] = predict(sampleset.getSample(i));
		
		return score;
	}

	/**
	 * 预测单个检索词关联文档上的相关分值
	 * @param sampleset
	 * @return 检索词层级上的标准化预测结果
	 */
	public double[]	normPredict(SampleSet sampleset){
		int sz = sampleset.size();
		double[] score = new double[sz];

		for(int i = 0; i < sz; i++)
			score[i] = predict(sampleset.getSample(i));
		
		//默认使用最大值标准化方法,一般而言标准化不影响文档排名,但对于集成模型有影响
		MathLib.maxNormalize(score);
		return score;
	}

	/**
	 * 预测单个样本
	 * @param sample
	 * @return 单个样本的预测分值
	 */
	public abstract double predict(Sample sample);

	public Model copy(){
		return (Model) DeepCopy.copy(this);
	}
}
