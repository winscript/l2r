package com.horsehour.neunet;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.model.Model;
import com.horsehour.neunet.neuron.Neuron;

/**
 * @author Chunheng Jiang
 * @version 3.0
 * @see Simon Haykin, Neural Networks and Learning Machines, 3rd edition, p122-38 
 * @since 20131216
 */
public class Network extends Model{
	private static final long serialVersionUID = 3033820560795032478L;
	
	public List<Layer> layers;
	public List<List<Double>> netWeights;
	public int numLayer = 2;//At least(输入层、输出层)

	protected NetUser user;
	protected boolean bias = false;
	protected int numOutputNeuron = 1;

	public Network(Network net){
		layers = net.layers;
		numLayer = net.numLayer;
		numOutputNeuron = net.numOutputNeuron;
		bias = net.bias;
		user = net.user;
		netWeights = net.netWeights;
	}

	/**
	 * @param user
	 * @param nInput
	 * @param nHidden
	 * @param nOutput
	 * @param bias
	 */
	public Network(NetUser user, boolean bias, int nInput, int nOutput, int... nHidden){
		this.user = user;
		this.bias = bias;

		this.numOutputNeuron = nOutput;
		layers = new ArrayList<Layer>();
		
		if(bias)
			nInput += 1;//plus bias

		layers.add(new Layer(nInput, user));//input layer

		int nHiddenLayer;
		if(nHidden == null)
			nHiddenLayer = 0;
		else
			nHiddenLayer = nHidden.length;
		numLayer = nHiddenLayer + 2;
		
		for(int i = 0; i < nHiddenLayer; i++)
			layers.add(new Layer(nHidden[i], user));//hidden layer
				
		layers.add(new Layer(nOutput, user));//output layer
		
		connectNet();
	}

	/**
	 * 连接网络
	 */
	private void connectNet(){
		Layer inputLayer = layers.get(0);
		inputLayer.connectTo(layers.get(1));//连接输入层(包括bias,若有)与隐藏层
		
		if(bias){
			Neuron biasNeuron = inputLayer.getNeuron(inputLayer.size() - 1);
			for(int id = 1; id < numLayer - 1; id++)
				biasNeuron.connectTo(layers.get(id + 1));//连接bias neuron到后继各层的neuron
		}
		
		for(int id = 1; id < numLayer - 1; id++){
			Layer layer = layers.get(id);
			layer.connectTo(layers.get(id + 1));//连接前一层与下一层
		}
	}
	
	/**
	 * 向网络中Feed数据,并向前传播
	 * @param sample
	 */
	public void forwardProp(Sample sample){
		//Feed data
		Layer inputLayer = layers.get(0);
		int numBias = bias ? 1 : 0;

		List<Neuron> neurons = inputLayer.getNeurons();
		for(int idx = 0; idx < neurons.size() - numBias; idx++)
			neurons.get(idx).addOutput(sample.getFeature(idx));

		if(bias)
			neurons.get(neurons.size() - 1).addOutput(1.0f);//for bias

		//Propagate data
		for(int idx = 1; idx < numLayer; idx++)
			layers.get(idx).propagate();
	}
	
	/**
	 * 清空所有结点的输出列表中的数据为下轮循环做准备
	 * 教训：使用容器时一定要在不使用时将其清空,不然内存将迅速溢出
	 */
	public void clearOutputs(){
		for(Layer layer : layers)
			for(Neuron neuron : layer.getNeurons())
				neuron.clearOutputs();
	}
	
	/**
	 * 获取输出层结点数目
	 * @return numOutputNeuron
	 */
	public int getNumOutputNeuron(){
		return numOutputNeuron;
	}
	
	/**
	 * 指定id的layer
	 * @param layerId
	 * @return layer at layerId
	 */
	public Layer getLayer(int layerId){
		return layers.get(layerId);
	}
	
	/**
	 * 设置Learning Rate
	 * @param lr
	 */
	public void setLearningRate(float lr) {
		for(Layer layer : layers)
			for(Neuron neuron : layer.getNeurons())
				neuron.setLearningRate(lr);
	}
	
	/**
	 * 取得神经元的Learning Rate
	 * @return learning rate of neuron
	 */
	public float getLearningRate() {
		return layers.get(0).getNeuron(0).getLearningRate(); 
	}
	
	/**
	 * 保存当前的网络权值: 每一层保存到一个List中
	 */
	public void updateWeight(){
		netWeights = new ArrayList<List<Double>>();
		for(int idx = 0; idx < numLayer - 1; idx ++){
			List<Double> weights = new ArrayList<Double>();
			
			for(Neuron neuron : layers.get(idx).getNeurons()){
				List<Connector> outCon = neuron.getOutputConnectors();
				for(int id = 0; id < outCon.size(); id++)
					weights.add(outCon.get(id).getWeight());
			}
			netWeights.add(weights);
		}
	}

	/**
	 * 将netWeights中的权重重新赋予网络
	 */
	public void reweight(){
		for(int idx = 0; idx < numLayer - 1; idx ++){
			List<Double> weights = netWeights.get(idx);
			int count = 0;			
			for(Neuron neuron : layers.get(idx).getNeurons()){
				for(int id = 0, sz = neuron.getOutputConnectors().size(); id < sz; id++){
					neuron.getOutputConnectors().get(id).setWeight(weights.get(count));
					count++;
				}
			}
		}
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < numLayer - 1; i ++){
			List<Double> weights = netWeights.get(i);
			for(double weight : weights)
				sb.append(weight + "\t");
			sb.append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * 预测数据集
	 * @param dataset
	 * @return 预测分值
	 */
	public double[][] predict(DataSet dataset){
		reweight();
		return super.predict(dataset);
	}

	/**
	 * @param sampleset
	 * @return 预测分值
	 */
	public double[] predict(SampleSet sampleset){
		clearOutputs();
		Sample sample;
		for(int id = 0; id < sampleset.size(); id++){
			sample = sampleset.getSample(id);
			forwardProp(sample);
		}
		List<Double> predict;
		Layer outputLayer;
		Neuron outputNeuron;

		outputLayer = layers.get(layers.size() - 1);
		outputNeuron = outputLayer.getNeuron(0);

		predict = outputNeuron.getOutputList();

		return MathLib.listToArray(predict);
	}

	@Override
	public double predict(Sample sample) {
		return 0;
	}
}