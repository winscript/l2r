package com.horsehour.metric;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * 实现标准度量Discount Cumulative Gain(DCG)
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20131201
 * @see <a href="http://research.microsoft.com/en-us/um/beijing/
 * projects/letor/LETOR4.0/Evaluation/Eval-Score-4.0.pl.txt">LETOR4.0 Eval</a>
 */
public class DCG extends Metric {
	protected int k = 10;
	protected int[] gains = {0, 1, 3, 7, 15, 31};

	public DCG(int k){
		this.k = k;
	}

	@Override
	public double measure(List<Integer> desire, List<Double> predict){
		return getTopKDCG(desire, predict)[k - 1];
	}

	public double[] getTopKDCG(List<Integer> desire, List<Double> predict){
		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);

		Sorter.linkedSort(score, label, true);//基于score对label降序排列
		return getTopKDCG(label);
	}

	/**
	 * dcg@k = sum(i:(2^desire[i] - 1)/log(i + 1)), where i implies the
	 * position in permutation based on predicted scores, i = 1,2,...,k;
	 * @param label 排序后的真实标签
	 * @return
	 */
	protected double[] getTopKDCG(List<Integer> label){
		double[] dcg = new double[k];
		int sz = label.size();
		dcg[0] = gains[label.get(0)];
		
		for(int i = 1; i < k; i++)
		{
			int r = 0;
			if(i < sz)
				r = label.get(i);

			dcg[i] = dcg[i - 1] + gains[r] * Math.log(2)/Math.log(i + 1);
		}

		return dcg;
	}

	@Override
	public String name() {
		return "DCG@" + k;
	}
}
