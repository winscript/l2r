package com.horsehour.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Metric定义了query-level标准度量的基本行为
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20130409
 */

public abstract class Metric {
	protected List<Integer> desire;//真实列表
	protected List<Double> predict;//预测列表
	
	public Metric(){}

	/**
	 * @param desire
	 * @param predict
	 * @return 评价算法的性能指标
	 */
	public abstract double measure(List<Integer> desire, List<Double> predict);

	public double measure(int[] desire, double[] predict){
		List<Integer> desireList = new ArrayList<Integer>();
		List<Double> predictList = new ArrayList<Double>();
		
		for(int i = 0; i < desire.length; i++){
			desireList.add(desire[i]);
			predictList.add(predict[i]);
		}
		return measure(desireList, predictList);
	}

	/**
	 * @return Metric名称
	 */
	public abstract String name();
}
