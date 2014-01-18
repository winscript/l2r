package com.horsehour.function;

public class PowerFunction implements ActivationFunction{
	public float base = 0;
	
	public PowerFunction(float base){
		this.base = base;
	}

	@Override
	public double calc(double netInput) {
		return Math.pow(base, netInput);
	}

	@Override
	public double calcDerivation(double input) {
		return calc(input) * Math.log(base);
	}
}
