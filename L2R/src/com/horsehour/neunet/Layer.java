package com.horsehour.neunet;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.neunet.neuron.ListMLENeuron;
import com.horsehour.neunet.neuron.ListNetNeuron;
import com.horsehour.neunet.neuron.Neuron;
import com.horsehour.neunet.neuron.RankNetNeuron;

/**
 * 神经网络层-输入层、隐藏层和输出层
 * @author Chunheng Jiang
 * @version 1.0
 */
public class Layer {
	private List<Neuron> neurons = null;
	private int size = 0;
	
	//Construction based on the user
	public Layer(int size, NetUser user) {
		this.size = size;
		neurons = new ArrayList<Neuron>();
		
		for(int i = 0; i < size; i++){
			if(user == NetUser.RankNet)//For RankNet
				neurons.add(new RankNetNeuron());
			
			else if(user == NetUser.ListNet)//For ListNet
				neurons.add(new ListNetNeuron());
			
			else if(user == NetUser.ListMLE)//For ListMLE
				neurons.add(new ListMLENeuron());
			
			else
				neurons.add(new Neuron());
		}
	}

	//连接Layer-Build Net
	public void connectTo(Layer destLayer) {
		for(Neuron neuron : neurons)
			neuron.connectTo(destLayer);
	}
	
	//传播数据信号
	public void propagate(){
		for(Neuron neuron : neurons)
			neuron.propagate();
	}
	
	//计算每层结点的输出
	public void calcOutput(){
		for(Neuron neuron : neurons)
			neuron.calcOutput();
	}
	
	//计算隐藏结点的Local Gradient
	public void calcLocalGradient() {
		for(Neuron neuron : neurons)
			neuron.calcLocalGradient();
	}
	
	//计算输出结点的Local Gradient
	public void calcOutputLocalGradient(int[] labels) {
		Neuron neuron = null;
		for(int idx = 0; idx < neurons.size(); idx++){
			neuron = neurons.get(idx);
			neuron.calcOutputLocalGradient(labels[idx]);
		}
	}
	
	//RankNet:计算Local Gradient
	public void calcLocalGradient(int current, int[] pair) {
		for(Neuron neuron : neurons)
			((RankNetNeuron)neuron).calcLocalGradient(current, pair);
	}
	
	//RankNet:更新权重
	public void updateWeight(int current, int[] pair){
		for(Neuron neuron : neurons)
			((RankNetNeuron)neuron).updateWeight(current, pair);
	}
	
	//更新连接权重
	public void updateWeight() {
		for(Neuron neuron : neurons)
			neuron.updateWeight();
	}
	
	//取得本层全部结点列表
	public List<Neuron> getNeurons() {
		return neurons;
	}
	
	//取得指定id的结点
	public Neuron getNeuron(int idx) {
		return neurons.get(idx); 
	}
	
	//取得本层的结点数目
	public int size() {
		return size;
	}
}