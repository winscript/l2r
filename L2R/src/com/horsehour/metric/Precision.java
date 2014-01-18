package com.horsehour.metric;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * ¾«¶È
 * @author Chunheng Jiang
 * @version 4.0
 * @since 20131204
 * @see <a href="http://research.microsoft.com/en-us/um/beijing/
 * projects/letor/LETOR4.0/Evaluation/Eval-Score-4.0.pl.txt">LETOR4.0 Eval</a>
 */
public class Precision extends Metric{
	private int k = 10;
	private int[] rel = {0, 1, 1, 1};
	
	public Precision(){}

	public Precision(int k){
		this.k = k;
	}
	
	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		double[] topP = getTopKPrecision(desire, predict);
		return topP[k - 1];
	}

	public double[] getTopKPrecision(List<Integer> desire, List<Double> predict){
		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);
		
		Sorter.linkedSort(score, label, true);
		
		return getTopKPrecision(label);
	}

	private double[] getTopKPrecision(List<Integer> label){
		double[] precisionAtN = new double[k];
		int sz = label.size();
		int nRel = 0;
		for(int i = 0; i < k; i++){
			int r = 0;
			if(i < sz)
				r = label.get(i);

			if(rel[r] == 1)
				nRel++;

			precisionAtN[i] = (double) nRel/(i + 1);
		}

		return precisionAtN;
	}

	@Override
	public String name() {
		return "P@" + k;
	}
}
