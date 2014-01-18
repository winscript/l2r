package com.horsehour.metric;

import java.util.List;

import com.horsehour.math.MathLib;


/**
 * CosineSimMetric是基于余弦相似度计算两个向量的相似程度
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130409
 */
public class CosineSimilarity extends Metric{

	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		int sz = predict.size();
		double[] predictL = new double[sz];
		double[] label = new double[sz];
		for(int i = 0; i < sz; i++){
			predictL[i] = predict.get(i);
			label[i] = desire.get(i);
		}
		
		return MathLib.getSimCosine(label, predictL);
	}

	@Override
	public String name() {
		return "CosineSim";
	}
}
