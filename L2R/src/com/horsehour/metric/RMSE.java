package com.horsehour.metric;

import java.util.List;

/**
 * RMSE£º¾ù·½¸ùÎó²î£¨Root Mean Square Error£©
 * @author Chunheng Jiang
 * @version 1.0 
 * @since 20130409
 */
public class RMSE extends Metric{

	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		int sz = desire.size();
		double diffnorm = 0;
		for(int i = 0; i < sz; i++){
			double diff = desire.get(i) - predict.get(i);
			diffnorm += Math.pow(diff, 2);
		}

		return (double) Math.sqrt(diffnorm/sz);
	}

	@Override
	public String name(){
		return "RMSE";
	}
}
