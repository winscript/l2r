package com.horsehour.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * ÊµÏÖ±ê×¼¶ÈÁ¿Normalized Discount Cumulative Gain(NDCG)
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131201
 * @see <a href="http://research.microsoft.com/en-us/um/beijing/
 * projects/letor/LETOR4.0/Evaluation/Eval-Score-4.0.pl.txt">LETOR4.0 Eval</a>
 */
public class NDCG extends DCG{
	
	public NDCG(int k){
		super(k);
	}

	/**
	 * ndcg@k = sum(i:(2^desire[i] - 1)/log(i + 1))/IdealDCG, where i implies the
	 * position in permutation based on predicted scores, i = 1,2,...,k;
	 * IdealDCG means the prediction is perfectly consistent with desire scores
	 */
	public double measure(List<Integer> desire, List<Double> predict){
		if(k > desire.size())
			return 0;

		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);
		Sorter.linkedSort(score, label, true);//»ùÓÚscore¶Ôlabel½µÐòÅÅÁÐ
		
		double[] dcg = getTopKDCG(label);
		Collections.sort(label, Collections.reverseOrder());
		double[] idcg = getTopKDCG(label);

		double r = idcg[k - 1];
		if(r == 0)
			return 1;
		else
			return dcg[k - 1]/r;
	}

	@Override
	public String name(){
		return "NDCG@" + k;
	}
}