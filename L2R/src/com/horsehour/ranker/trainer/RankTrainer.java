package com.horsehour.ranker.trainer;

import java.util.Properties;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.metric.MAP;
import com.horsehour.metric.Metric;
import com.horsehour.model.Model;

/**
 * RankTrainer抽象排名函数的训练机
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130311
 */
public abstract class RankTrainer{
	public DataSet trainset;
	public DataSet valiset;

	public Metric trainMetric;
	public Metric valiMetric = new MAP();
	
	public Model plainModel;//在习模型
	public Model bestModel;//习得模型
	
	public String modelFile;
	public int nIter = 200;
	
	public Properties prop; 

	/**
	 * 初始化
	 */
	public abstract void init();
	
	/**
	 * 训练
	 */
	public void train(){
		init();

		double vali = 0;
		double bestvali = -1;
		for(int iter = 0; iter < nIter; iter++){
			learn();//学习
			vali = validate();//验证

			if(vali > bestvali){
				bestvali = vali;
				updateModel();
			}
		}
		storeModel();
	}
	
	/**
	 * 学习模型
	 */
	protected abstract void learn();

	/**
	 * 使用指定度量指标计算模型在给定数据集上的性能
	 * @param dataset
	 * @param metric
	 * @return 模型性能
	 */
	protected double validate(DataSet dataset, Metric metric){
		double perf = 0;
		double[] predict;
		int m = dataset.size();
		SampleSet sampleset;

		for(int i = 0; i < m; i++){
			sampleset = dataset.getSampleSet(i);
			predict = plainModel.predict(sampleset);
			perf += metric.measure(sampleset.getLabels(), predict);
		}

		return perf/m;
	}

	/**
	 * 计算模型在验证集上的性能
	 * @return 模型性能
	 */
	protected double validate(){
		return validate(valiset, valiMetric);
	}
	
	/**
	 * 更新模型
	 */
	public abstract void updateModel();

	/**
	 * 保存模型
	 */
	public abstract void storeModel();
	
	/**
	 * 从文件中载入模型
	 * @param modelFile
	 * @return
	 */
	public abstract Model loadModel(String modelFile);

	/**
	 * @return name
	 */
	public abstract String name();
}
