package com.horsehour.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.horsehour.util.Sorter;

/**
 * 计算所有位置NDCG的平均值
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20140102
 */
public class MeanNDCG extends Metric{
	protected int k;
	protected int[] gains = {0, 1, 3, 7, 15, 31};
	protected double[] omega;

	public MeanNDCG(){
		this(10);//in default
	}

	public MeanNDCG(double[] weight){
		k = weight.length;
		omega = Arrays.copyOf(weight, k);
	}

	public MeanNDCG(int k){
		this.k = k;
		omega = new double[k];
		Arrays.fill(omega, 1);
	}

	@Override
	public double measure(List<Integer> desire, List<Double> predict){
		List<Integer> label = new ArrayList<Integer>();
		List<Double> score = new ArrayList<Double>();
		label.addAll(desire);
		score.addAll(predict);

		Sorter.linkedSort(score, label, true);//基于score对label降序排列
		double[] dcg = getDCG(label);
		Collections.sort(label, Collections.reverseOrder());
		double[] idcg = getDCG(label);
		
		if(idcg[0] == 0)//表明所有的标记相等均为0,则任意评分都是正确的
			return 1;
		
		double weightsum = 0;
		double norm = 0;
		int len = desire.size();
		for(int i = 0; i < len; i++){
			double r = idcg[i]; 
			if(r == 0)//idcgs in following must also be zeros(rank list is too long) 
				break;

			weightsum += omega[i] * dcg[i] / r;
			norm += omega[i];
		}

		if(norm == 0)
			return weightsum/len;
		
		return weightsum/norm;
	}

	/**
	 * dcg@k = sum(i:(2^desire[i] - 1)/log(i + 1)), where i implies the
	 * position in permutation based on predicted scores, i = 1,2,...,k;
	 * @param label 排序后的真实标签
	 * @return dcg list at any position
	 */
	protected double[] getDCG(List<Integer> label){
		int len = label.size();
		if(k > len)
			k = len;

		double[] dcg = new double[k];

		dcg[0] = gains[label.get(0)];
		for(int i = 1; i < k; i++){
			int r = label.get(i);
			dcg[i] = dcg[i - 1] + gains[r] * Math.log(2)/Math.log(i + 1);
		}
		return dcg;
	}

	@Override
	public String name() {
		return "MeanNDCG@" + k;
	}
}
