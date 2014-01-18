package com.horsehour.function;


public class IdentityFunction implements ActivationFunction {

	@Override
	public double calc(double netInput) {
		return netInput;
	}

	@Override
	public double calcDerivation(double input) {
		return 1.0;
	}
}
