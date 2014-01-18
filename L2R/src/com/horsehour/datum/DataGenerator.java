package com.horsehour.datum;

import java.util.Random;

public class DataGenerator {

	private static float theta = -0.8f;
	
	//产生指定数目、指定维数的随机数据 
	public static void generate(SampleSet sampleSet, int numSample, int dim){
		
		Random rand = new Random();
		double[] features = new double[dim];
		
		for(int i = 0; i < numSample; i++){
			for(int d = 0; d < dim; d++)
				features[d] = rand.nextFloat();
			
			sampleSet.addSample(new Sample(features, getLabel(features)));
		}
	}

	//确定Label
	//规则：如果2*x1 - e^x2 > theta, 则为正例，反之为反例
	private static int getLabel(double[] features){
		int label = (2* features[0] - Math.exp(features[1]) > theta)? 1 : -1;
		return label;
	}
}
