package com.horsehour.ranker.trainer;

import java.util.Arrays;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.function.PowerFunction;
import com.horsehour.math.MathLib;
import com.horsehour.model.EnsembleModel;
import com.horsehour.model.FeatureModel;
import com.horsehour.util.FileManager;

/**
 * RankCosine is another Listwise learning to rank approach based on boost framework.
 * The cosine loss function defined is derived from cosine distance/similarity.
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012/12/16
 * @see Qin, T., X.-D. Zhang, et al. (2008). "Query-level loss functions 
 * for information retrieval." Information Processing and Management: an 
 * International Journal 44(2): 838–855.
 */

public class RankCosine extends RankTrainer{
	
	private double[][] weight_1, weight_2;
	private double[][] weakPredict, boostPredict;
	private double[][] normLabel;

	public RankCosine(){}

	/**
	 * 初始化
	 */
	public void init(){
		plainModel = new EnsembleModel();
		bestModel = new EnsembleModel();
		
		normLabel = DataManager.transLabel(trainset, new PowerFunction(2));
		initWeight();
	}

	/**
	 * 初始化weight_1, weight_2
	 */
	private void initWeight(){
		int nTrain = trainset.size();
		weight_1 = new double[nTrain][];
		weight_2 = new double[nTrain][];
		
		for(int qid = 0; qid < nTrain; qid++){
			int sz = trainset.getSampleSet(qid).size();
			weight_1[qid] = new double[sz];
			Arrays.fill(weight_1[qid], (double)1/sz);
			weight_2[qid] = Arrays.copyOf(weight_1[qid], sz);
		}
	}

	@Override
	protected void learn() {
		weakLearn();
		boostPredict();
	}

	/**
	 * 学习Weak Ranker
	 */
	public void weakLearn(){
		int id = 0;
		double preLoss = 0, loss = 0;
		float preWeight = 1.0f, weight = 1.0f;
		int sz = trainset.size();
		
		weakPredict = new double[sz][];
		
		int dim = trainset.getDim();
		for(int fid = 0; fid < dim; fid++){
			SampleSet sampleSet = null;
			for(int qid = 0; qid < sz; qid++){
				sampleSet = trainset.getSampleSet(qid);
				weakPredict[qid] = MathLib.listToArray(sampleSet.getFeatureList(fid));
			}

			if(((EnsembleModel) plainModel).size() > 0){
				updateWeight();
				weight = weightWeakRanker();
			}

			loss = calcLoss(weight);
			if(fid == 0)
				preLoss = loss;
			if(loss < preLoss){
				preWeight = weight;
				preLoss = loss;
				id = fid;
			}
		}
		
		((EnsembleModel) plainModel).addMember(new FeatureModel(id), preWeight);
	}

	/**
	 * 使用Boost Ranker预测训练集
	 */
	public void boostPredict(){
		int m = ((EnsembleModel) plainModel).size();
		double weakWeight = ((EnsembleModel) plainModel).getWeight(m-1);
		
		DataSet trainingSet = trainset; 
		int nTrain = trainingSet.size();

		if(((EnsembleModel) plainModel).size() == 1){//The first round
			boostPredict = new double[nTrain][];
			for(int qid = 0; qid < nTrain; qid++)
				boostPredict[qid] = Arrays.copyOf(weakPredict[qid], weakPredict[qid].length);
			return;
		}

		for(int qid = 0; qid < nTrain; qid++)
			boostPredict[qid] = MathLib.linearCombinate(boostPredict[qid], 1.0f, 
					weakPredict[qid], weakWeight);
	}

	/**
	 * 根据当前Boost Ranker和Weak Ranker Candidate计算损失
	 * @param weakWeight
	 * @return cosineloss
	 */
	public double calcLoss(double weakWeight){
		double loss = 0;
		int sz = trainset.size();
		if(((EnsembleModel) plainModel).size() == 0){//The first round
			for(int qid = 0; qid < sz; qid++)
				loss += calcCosineLoss(normLabel[qid], weakPredict[qid]);
			return loss;
		}

		for(int qid = 0; qid < sz; qid++){
			double[] predict = MathLib.linearCombinate(boostPredict[qid], 1.0f, 
					weakPredict[qid], weakWeight);
			loss += calcCosineLoss(normLabel[qid], predict);
		}
		return loss;
	}
	
	/**
	 * 计算余弦损失
	 * @param desire
	 * @param predict
	 * @return cosine loss
	 */
	public double calcCosineLoss(double[] desire, double[] predict){
		double simCosine = MathLib.getSimCosine(desire, predict);
		return 0.5 * (1 - simCosine);
	}
	
	/**
	 * 计算Weak Ranker的权重
	 * @return weight of weak ranker
	 */
	public float weightWeakRanker(){
		double a = 0, b = 0, c = 0, d = 0;
		double[] temp;
		int sz = trainset.size();
		for(int qid = 0; qid < sz; qid++){
			a += MathLib.innerProduct(weight_1[qid], weakPredict[qid]);
			c = MathLib.innerProduct(normLabel[qid], weakPredict[qid]);
			d = MathLib.innerProduct(weakPredict[qid], weakPredict[qid]);
			
			temp = MathLib.linearCombinate(weakPredict[qid], c, normLabel[qid], -1 * d);
			b += MathLib.innerProduct(weight_1[qid], temp);
		}
		float weight = 0;
		
		if(b != 0)
			weight = (float) (a/b);
		return weight;
	}
	
	/**
	 * 根据当前Boost Ranker，更新权重weight_1, weight_2
	 */
	public void updateWeight(){
		double a = 0, b = 0;
		int sz = trainset.size();
		for(int qid = 0; qid < sz; qid++){
			a = MathLib.innerProduct(normLabel[qid], boostPredict[qid]);
			b = MathLib.getEuclideanNorm(boostPredict[qid]);
			
			weight_1[qid] = MathLib.linearCombinate(boostPredict[qid], a, normLabel[qid], -1 * b);
			weight_1[qid] = MathLib.scale(weight_1[qid], (float)Math.pow(b, 0.75));

			weight_2[qid] = MathLib.scale(boostPredict[qid], (float)Math.pow(b, 0.75));
		}
	}

	@Override
	public void updateModel() {
		bestModel = (EnsembleModel) plainModel.copy();
	}
	
	@Override
	public void storeModel() {
		FileManager.writeFile(modelFile, bestModel.toString(), false);
	}

	@Override
	public EnsembleModel loadModel(String modelFile) {
		EnsembleModel  model = new EnsembleModel();
		List<double[]> lines = DataManager.loadDatum(modelFile, "\t");
		int sz = lines.size();
		for(int i = 0; i < sz; i++){
			double[] line = lines.get(i);
			model.addMember(new FeatureModel((int)line[0]), line[1]);
		}
		return model;
	}
	
	@Override
	public String name() {
		return "RankCosine." + trainMetric.name();
	}
}