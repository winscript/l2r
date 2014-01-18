package com.horsehour.ranker.trainer;

import com.horsehour.model.Model;

/**
 * PRank实现了排名算法PRanking
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20120714
 * @see Koby Crammer, Yoram Singer, et al. Pranking with ranking. 
 *      Advances in neural information processing systems, 14:641–647, 2001. 
 */
public class PRank extends RankTrainer{

	@Override
	public void init() {}

	@Override
	protected void learn() {
		
	}

	@Override
	public void updateModel() {
		
	}

	@Override
	public void storeModel() {
		
	}

	@Override
	public Model loadModel(String modelFile) {
		return null;
	}
	
	@Override
	public String name() {
		return "PRank." + trainMetric.name();
	}
}
