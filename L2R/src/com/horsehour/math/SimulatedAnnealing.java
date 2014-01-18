package com.horsehour.math;

import java.util.Random;

/**
 * 模拟退火算法：标准形式是最小化
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130714
 */
public class SimulatedAnnealing {
	public int iter = 100;

	public double temperature = 10000.0;
	public double minTemperature = 0.00001;

	public double boltzman = 0.9999;

	protected ParticleState previousState = null;
	protected ParticleState disturbedState = null;
	
	public SimulatedAnnealing(){}

	public void init(ParticleState state){
		previousState = state;
	}
	
	/**
	 * @param state
	 * @return 给定粒子状态下的能量 
	 */
	public double calcEnergy(ParticleState state){
		return 0;
	}
	
	/**
	 * 粒子状态发生扰动
	 * @param initState
	 * @param initEnergy 初始状态的能量
	 * @return 扰动引发的状态能量改变
	 */
	public double disturbState(ParticleState initState, double initEnergy){
		//最佳选择-根据目标函数，确定增量方式改变
		disturbedState = null;
		return calcEnergy(disturbedState) - initEnergy;
	}

	/**
	 * 接受并更新到新状态
	 * @param state
	 * @return 初始状态更新
	 */
	public void acceptState(ParticleState state){
		previousState = state;
	}
	
	public void simulate(){
		double energy = calcEnergy(previousState), energyDiff = 0, prob = 0;
		
		Random rand = new Random();
		boolean accept = false;

		while((temperature > minTemperature)||(iter > 0)){
			energyDiff = disturbState(previousState, energy);

			prob = Math.exp(-energyDiff/(boltzman*temperature));//TODO:这里是否需要boltzman常数
			
			accept = ((energyDiff < 0)||(energyDiff * (prob - rand.nextDouble())>= 0));
			if(accept){
				acceptState(disturbedState);
				energy += energyDiff;
			}

			//降温退火
			temperature *= boltzman;
			
			iter--;
		}
	}

	/**
	 * 粒子状态
	 * @author Chunheng Jiang
	 * @version 1.0
	 * @since 20130714
	 */
	protected class ParticleState{
		
	};
}
