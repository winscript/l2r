package com.horsehour.function;


//Hyperbolic function also refers to hyperbolic tangent function 
public class HyperbolicFunction implements ActivationFunction {
	private float a = 1.0f;
	private float b = 1.0f;
	
	public HyperbolicFunction(){
		super();
	}
	
	public HyperbolicFunction(float a, float b){
		this.a = a;
		this.b = b;
	}
	public double calc(double netInput) {
		return (double) (a* Math.tanh(b* netInput));
	}

	@Override
	public double calcDerivation(double input) {
		double ret = 0.0;
		if(a == 0)
			System.err.println("Zero divisor.");
		else{
			ret = calc(input);
			ret = (b/a)*(a - ret) * (a + ret);
		}
		return ret;
	}
}