package com.horsehour.metric;

import java.util.List;

/**
 * ËðÊ§º¯Êý:½»²æìØ
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131216
 */
public class CrossEntropy extends Metric{
	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		int sz = desire.size();
		double[] p = new double[sz];
		double[] q = new double[sz];
		
		double normP = 0;
		double normQ = 0;
		for(int i = 0; i < sz; i++){
			p[i] = Math.pow(Math.E, desire.get(i));
			q[i] = Math.pow(Math.E, predict.get(i));
			
			normP += p[i];
			normQ += q[i];
		}
			
		double ret = 0;
		for(int i = 0; i < sz; i++)
			ret -= (p[i] / normP) * Math.log(q[i] / normQ);   
		return ret;
	}

	@Override
	public String name() {
		return "CrossEntropy";
	}
}
