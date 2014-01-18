package com.horsehour.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * MAP与NDCG@1之和
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20140104
 */
public class MAPNDCG extends Metric{
	protected int[] gains = {0, 1, 3, 7, 15, 31};
	protected int[] rel = {0, 1, 1, 1};
	
	/**
	 * ndcg@k = sum(i:(2^desire[i] - 1)/log(i + 1))/IdealDCG, where i implies the
	 * position in permutation based on predicted scores, i = 1,2,...,k;
	 * IdealDCG means the prediction is perfectly consistent with desire scores
	 */
	public double measure(List<Integer> desire, List<Double> predict){
		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);
		
		Sorter.linkedSort(score, label, true);//基于score对label降序排列
		double dcg = gains[label.get(0)];
		
		int sz = label.size();
		int nRel = 0;
		double ap = 0;
		for(int i = 0; i < sz; i++){
			int r = label.get(i);

			if(rel[r] == 1){
				nRel++;
				ap += (double) nRel/(i + 1);
			}
		}
		
		double map = 0;
		if(nRel == 0)//表明真实列表等级相同
			map = 0;
		else
			map = ap/nRel;

		Collections.sort(label, Collections.reverseOrder());
		double idcg = gains[label.get(0)];
		if(idcg == 0)
			dcg = 0;//表明真实列表等级相同
		else
			dcg /= idcg;
		
		return map + dcg;
	}

	@Override
	public String name() {
		return "MAPNDCG";
	}
	
}
