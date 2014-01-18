package com.horsehour.datum.norm;

import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;

public class SumNormalizer extends Normalizer{
	public SumNormalizer(){}
	
	/**
	 * 数据集标准化-query based
	 * @param sampleSet
	 */
	public void normalize(SampleSet sampleSet){
		int dim = sampleSet.getSample(0).getDim();
		double[] sum = new double[dim];
		//每个特征的和值
		for(int fid = 0; fid < dim; fid++){
			for(int qid = 0; qid < sampleSet.size(); qid++)
				sum[fid] += sampleSet.getSample(qid).getFeature(fid);
		}
		
		for(int fid = 0; fid < dim; fid++){
			//标准化
			if(sum[fid] > 0)
				for(Sample sample : sampleSet.getSamples()){
					double val = sample.getFeature(fid)/sum[fid];
					sample.setFeature(fid, val);
				}
		}
	}
}
