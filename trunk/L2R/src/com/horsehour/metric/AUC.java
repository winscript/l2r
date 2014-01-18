package com.horsehour.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.horsehour.util.Pair;

/**
 * Area Under Curve of ROC
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131125
 */
public class AUC extends Metric{

	public AUC(){}

	@Override
	public double measure(List<Integer> desire, List<Double> predict)
	{
		int nPositive = 0;
		int nNegative = 0;

		int len = predict.size();
		List<Pair<Double, Integer>> list = new ArrayList<Pair<Double, Integer>>();
		for (int i = 0; i < len; i++) {
			int label = desire.get(i);
			if(label == 0)
				nNegative++;

			list.add(new Pair<Double, Integer>(predict.get(i), label));
		}

		nPositive = len - nNegative;

		Collections.sort(list);

		float fp = 0;
		float tp = 0;
		float fpPrev = 0;
		float tpPrev = 0;
		double area = 0;
		double fPrev = Double.MIN_VALUE;

		Pair<Double, Integer> pair;
		for(int i = 0; i < len; i++)
		{
			pair = list.get(i);
			
			double curF = pair.getKey();
			
			if (curF != fPrev) {
				area += Math.abs(fp - fpPrev) * ((tp + tpPrev) / 2.0);
				fPrev = curF;
				fpPrev = fp;
				tpPrev = tp;
			}

			int label = pair.getValue();
			if (label == 1)
				tp++;
			else
				fp++;
		}

		area += Math.abs(nNegative - fpPrev) * ((nPositive + tpPrev) / 2.0);
		area /= ((double) nPositive * nNegative);
		return area;
	}

	@Override
	public String name() {
		return "AUC";
	}

	public static void main(String[] args){
		List<Double> predict = new ArrayList<Double>();
		List<Integer> label = new ArrayList<Integer>();
		predict.add(1.6); predict.add(2.5); predict.add(3.3); predict.add(3.3);
		label.add(0); label.add(1); label.add(0); label.add(0);
		
		System.out.println(new AUC().measure(label, predict));
	}
}
