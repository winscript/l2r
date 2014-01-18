package com.horsehour.model;

import java.util.Arrays;

import com.horsehour.datum.Sample;
import com.horsehour.math.MathLib;

/**
 * 逻辑回归模型
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131115
 */
public class LogisticModel extends Model{
	private static final long serialVersionUID = -3862122431045366453L;
	
	public double[] omega;

	public LogisticModel(double[] omega){
		this.omega = Arrays.copyOf(omega, omega.length);
	}

	@Override
	public double predict(Sample sample) {
		double predict = MathLib.innerProduct(omega, sample.getFeatures());
		predict = (double) (1.0/Math.exp(-predict));
		return predict;
	}

	/**
	 * 更新参数:omega += delta
	 * @param delta
	 */
	public void updateWeight(double[] delta){
		omega = MathLib.add(omega, delta);
	}
}