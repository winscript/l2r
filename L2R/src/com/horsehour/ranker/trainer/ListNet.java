package com.horsehour.ranker.trainer;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.SampleSet;
import com.horsehour.metric.CrossEntropy;
import com.horsehour.model.Model;
import com.horsehour.neunet.Layer;
import com.horsehour.neunet.NetUser;
import com.horsehour.neunet.Network;
import com.horsehour.neunet.neuron.ListNetNeuron;
import com.horsehour.util.FileManager;

/**
 * ListNet算法
 * @author Chunheng Jiang
 * @version 2.0
 * @see Learning to rank: from pairwise approach to listwise approach,
 *  Cao, Zhe and Qin, Tao and Liu, Tie-Yan and Tsai, Ming-Feng and Li, Hang
 * @since 20131216
 */
public class ListNet extends RankTrainer{
	public Network net;
	public boolean bias = false;
	public int[] nHidden;

	public ListNet(){}

	@Override
	public void init() {
		net = new Network(NetUser.ListNet, bias, trainset.getDim(), 1, nHidden);
		valiMetric = new CrossEntropy();
	}

	@Override
	protected void learn() {
		Layer outputLayer = net.layers.get(net.layers.size() - 1);
		ListNetNeuron outputNeuron = (ListNetNeuron) outputLayer.getNeuron(0);
		
		for(SampleSet sampleSet : trainset.getSampleSets()){
			net.clearOutputs();//清除上轮迭代保存的输出列表,以免影响正常学习或检测
			for(int id = 0; id < sampleSet.size(); id++)
				net.forwardProp(sampleSet.getSample(id));
			outputNeuron.updateWeight(sampleSet);
		}

		net.updateWeight();
	}

	/**
	 * 校验模型(valiMetric交叉熵)
	 */
	protected double validate(){
		double perf = 0;
		Layer outputLayer = net.layers.get(net.layers.size() - 1);
		ListNetNeuron outputNeuron = (ListNetNeuron) outputLayer.getNeuron(0);
		
		for(SampleSet sampleSet : valiset.getSampleSets()){
			net.clearOutputs();//清除上轮迭代保存的输出列表,以免影响正常学习或检测
			for(int i = 0; i < sampleSet.size(); i++)
				net.forwardProp(sampleSet.getSample(i));

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
		
		Network net = new Network(NetUser.ListNet, false, nInput, nOutput, nHidden);
		
		net.netWeights = weight;
		return net;
	}

	@Override
	public String name() {
		return "ListNet";
	}
}