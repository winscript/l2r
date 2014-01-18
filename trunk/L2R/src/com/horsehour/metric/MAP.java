package com.horsehour.metric;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * 实现了MAP(Mean Average Precision)标准度量
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20111130
 */ 
public class MAP extends Metric{
	private int[] rel = {0, 1, 1, 1};

	public MAP(){}

	/**
	 * 度量模型的性能表现
	 */
	public double measure(List<Integer> desire, List<Double> predict){
		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);
		
		Sorter.linkedSort(score, label, true);
		int sz = label.size();
		int nRel = 0;
		double averagePrecision = 0;

		for(int i = 0; i < sz; i++){
			int r = label.get(i);

			if(rel[r] == 1){
				nRel++;
				averagePrecision += (double) nRel/(i + 1);
			}
		}

		if(nRel == 0)
			return 0;

		return averagePrecision / nRel;
	}
	
	@Override
	public String name() {
		return "MAP";
	}
}
