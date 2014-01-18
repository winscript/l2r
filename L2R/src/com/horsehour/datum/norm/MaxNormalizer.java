package com.horsehour.datum.norm;

import java.util.Collections;
import java.util.List;

import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;

/**
 * MaxNormalizer
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130101
 */
public class MaxNormalizer extends Normalizer{

	@Override
	public void normalize(SampleSet sampleSet) {
		List<Sample> samples = sampleSet.getSamples();
		List<Double> features;
		int dim = sampleSet.getSample(0).getDim();
		
		for(int fid = 0; fid < dim; fid++){
			features = sampleSet.getFeatureList(fid);
			double maxFeature = Collections.max(features);
			if(maxFeature > 0)
				for(int id = 0; id < samples.size(); id++)
					samples.get(id).setFeature(fid, features.get(id)/maxFeature);
		}
	}
}
