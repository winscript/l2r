package com.horsehour.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Chunheng Jiang
 * @version 1.0
 */
public class Sorter {

	/**
	 * 基于base对list排列
	 * @param base
	 * @param list
	 * @param des
	 */
	public static void linkedSort(List<Double> base, List<Integer> list, boolean des){
		int len = base.size();
		List<Pair<Double, Integer>> pairs = new ArrayList<Pair<Double, Integer>>();
		for(int i = 0; i < len; i++)
			pairs.add(new Pair<Double, Integer>(base.get(i), list.get(i)));
		
		Collections.sort(pairs);
		if(des)
			Collections.reverse(pairs);
		
		Pair<Double, Integer> pair;
		for(int i = 0; i < len; i++){
			pair = pairs.get(i);
			base.set(i, pair.getKey());
			list.set(i, pair.getValue());
		}
	}

	public static List<Integer> insersionSortJudge(List<Integer> judge, List<Double> score){
		Double[] predict = (Double[]) score.toArray(new Double[0]);

		int sz = score.size();
		for (int i = 1; i < sz; i++){
			double tmpSc = predict[i];
			int tmpJd = judge.get(i);
			int k = i - 1;
			while (k >= 0 && tmpSc > predict[k]){
				predict[k + 1] = predict[k];
				judge.set(k + 1, judge.get(k));
				k--;
			}

			predict[k + 1] = tmpSc;
			judge.set(k + 1, tmpJd);
		}
		return judge;
	}
}
