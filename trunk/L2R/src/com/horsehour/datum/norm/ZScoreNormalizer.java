package com.horsehour.datum.norm;

import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;

public class ZScoreNormalizer extends Normalizer{
	
	public ZScoreNormalizer(){}
	
	/**
	 * 数据集标准化-query based
	 * @param sampleSet
	 */
	public void normalize(SampleSet sampleSet){
		int dim = sampleSet.getSample(0).getDim();
		double[] mean = new double[dim];
		//每个特征下的均值
		for(int fid = 0; fid < dim; fid++){
			for(int qid = 0; qid < sampleSet.size(); qid++)
				mean[fid] += sampleSet.getSample(qid).getFeature(fid);
			
			mean[fid] /= sampleSet.size();
		}
		
		for(int fid = 0; fid < dim; fid++){
			double std = 0;//每个特征下的标准差
			for(Sample sample : sampleSet.getSamples()){
				double val = sample.getFeature(fid) - mean[fid];  
				std += val * val;
			}
			std =  Math.sqrt(std/(sampleSet.size() - 1));

			//标准化
			if(std > 0)
				for(Sample sample : sampleSet.getSamples()){
					double val = (sample.getFeature(fid) - mean[fid])/std;
					sample.setFeature(fid, val);
				}
		}
	}
}