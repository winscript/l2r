package com.horsehour.function;


public class LogisticFunction implements ActivationFunction {
	private float a = 1.0f;
	
	public LogisticFunction(){}
	
	//a should be positive
	public LogisticFunction(float a){
		this.a = a;
	}
	public double calc(double input){
		return (double) (1.0/(1.0 + Math.exp(-a * input)));
	}

	@Override
	public double calcDerivation(double input) {
		double ret = calc(input);
		return a * ret * (1- ret);
	}
}