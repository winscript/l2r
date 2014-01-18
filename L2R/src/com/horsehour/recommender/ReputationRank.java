package com.horsehour.recommender;

import java.util.Arrays;

import com.horsehour.math.MathLib;
import com.horsehour.util.TickClock;

/**
 * 迭代计算评价人、受评物品的权重或信誉值
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130629
 * @see Cristobald De Kerchove and Paul Van Dooren. Reputation systems and optimization. 
 * Siam News, 41(2):1–3, 2008.
 */
public class ReputationRank {
	public double[][] rateMatrix;
	
	protected double[] weight;
	protected double[] reputation;
	
	private double discrep = 0;
	
	private int nRater = 0, nItem = 0;
	
	public int nIter = 0;
	
	public ReputationRank(double[][] rateMatrix){
		this.rateMatrix = rateMatrix;
		nRater = rateMatrix.length;
		nItem = rateMatrix[0].length;
		
		weight = new double[nRater];
		reputation = new double[nItem];
		
	}
	
	public void init(){
		Arrays.fill(weight, 1);
		discrep = nItem + 0.5;
	}

	/**
	 * 计算物品的信誉值
	 */
	public void calcReputation(){
		MathLib.sumNormalize(weight);//标准化
		for(int j = 0; j < nItem; j++){
			double weightedsum = 0;
			for(int i = 0; i < nRater; i++)
				weightedsum += rateMatrix[i][j] * weight[i];
			
			reputation[j] = weightedsum;
		}
	}

	/**
	 * 更新评价人的权重
	 */
	public void updateWeight(){
		for(int i = 0; i < nRater; i++){
			double diffsum = 0;
			for(int j = 0; j < nItem; j++)
				diffsum += Math.pow(rateMatrix[i][j] - reputation[j],2);
			
			weight[i] = discrep - diffsum; 
		}
	}

	public void run(){
		init();
		for(int i = 0; i < nIter; i++){
			track();
			calcReputation();
			updateWeight();
		}
	}
	
	public void track(){
		System.out.print("w = (");
		for(int i = 0; i < nRater; i++)
			System.out.print(weight[i] + " ");
		System.out.println(")");
		
		System.out.print("r = (");
		for(int j = 0; j < nItem; j++)
			System.out.print(reputation[j] + " ");
		System.out.println(")");
	}
	
	public static void main(String[] args) {
		TickClock.beginTick();
		
		ReputationRank rank = null;
		double[][] rateMatrix = {{0.42,0.34},{0.45,0.33},{0.28,0.49}};

		rank = new ReputationRank(rateMatrix);
		
		rank.nIter = 15;
		rank.run();
		
		TickClock.stopTick();
	}
}
