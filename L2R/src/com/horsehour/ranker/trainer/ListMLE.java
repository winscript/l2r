package com.horsehour.ranker.trainer;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.metric.CrossEntropy;
import com.horsehour.model.Model;
import com.horsehour.neunet.Layer;
import com.horsehour.neunet.NetUser;
import com.horsehour.neunet.Network;
import com.horsehour.neunet.neuron.ListMLENeuron;
import com.horsehour.util.FileManager;

/**
 * <p>It implements the Listwise algorithm ListMLE which
 * uses Maximum Likelihood Estimate Loss and deploys
 * Neural Network for training.It can be considered as
 * an extension of ListNet.</p>
 *
 * @author Chunheng Jiang
 * @version 1.0
 * @see Xia, F., T.-Y. Liu, et al. (2008). 
 * Listwise Approach to Learning to Rank - Theory and Algorithm. 
 * ICML '08: Proceedings of the 25th International Conference on Machine learning.
 * @since 20121126
 */
public class ListMLE extends RankTrainer {
	public Network net;
	public int topk = 5;
	public int[] nHidden;//隐藏层节点数目
	public float learningRate = 0.0005f;
	
	public ListMLE(){}

	@Override
	public void init() {
		net = new Network(NetUser.ListMLE, false, trainset.getDim(), 1, nHidden);
		net.setLearningRate(learningRate);

		valiMetric = new CrossEntropy();
	}

	@Override
	protected void learn() {
		Layer outputLayer = net.getLayer(net.layers.size() - 1);
		ListMLENeuron outputNeuron = (ListMLENeuron)outputLayer.getNeuron(0);
		int[] rank;//排名列表

		for(SampleSet sampleSet : trainset.getSampleSets()){
			net.clearOutputs();//清除上轮迭代保存的输出列表,以免影响正常学习或检测
			for(int i = 0; i < sampleSet.size(); i++)
				net.forwardProp(sampleSet.getSample(i));
			
			rank = MathLib.getRank(sampleSet.getLabels(), false);
			outputNeuron.updateWeight(topk, rank);
		}

		net.updateWeight();
	}

	/**
	 * 校验模型
	 */
	protected double validate(){
		double perf = 0;
		Layer outputLayer = net.getLayer(net.layers.size() - 1);
		ListMLENeuron outputNeuron = (ListMLENeuron)outputLayer.getNeuron(0);
		
		for(SampleSet sampleSet : valiset.getSampleSets()){
			net.clearOutputs();//清除上轮迭代保存的输出列表,以免影响正常学习或检测
			for(int id = 0; id < sampleSet.size(); id++)
				net.forwardProp(sampleSet.getSample(id));

			perf += valiMetric.measure(sampleSet.getLabelList(), outputNeuron.getOutputList());
		}
		return -perf/valiset.size();
	}

	@Override
	public void updateModel() {
		net.updateWeight();
		bestModel = new Network(net);
	}

	@Override
	public void storeModel() {
		FileManager.writeFile(modelFile, bestModel.toString());
	}

	@Override
	public Model loadModel(String modelFile) {
		List<double[]> lines = DataManager.loadDatum(modelFile, "\t");
		List<List<Double>> weight = new ArrayList<List<Double>>();
		int sz = lines.size();
		int nInput = -1;
		int nOutput = -1;
		int[] nHidden = new int[sz - 2];
		for(int i = 0; i < sz; i++){
			int len = lines.get(i).length;
			if(i == 0)
				nInput = len;
			else if(i == sz - 1)
				nOutput = len;
			else
				nHidden[i - 1] = len;

			List<Double> w = new ArrayList<Double>();
			for(int j = 0; j < lines.size(); j++)
				w.add(lines.get(i)[j]);
			
			weight.set(i, w);
		}
		
		Network net = new Network(NetUser.ListMLE, false, nInput, nOutput, nHidden);
		
		net.netWeights = weight;
		return net;
	}

	@Override
	public String name() {
		return "ListMLE";
	}
}