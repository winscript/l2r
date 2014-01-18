package com.horsehour.evaluate;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.datum.norm.Normalizer;
import com.horsehour.datum.norm.SumNormalizer;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.filter.LineParserFilter;
import com.horsehour.math.MathLib;
import com.horsehour.metric.MAP;
import com.horsehour.metric.Metric;
import com.horsehour.metric.NDCG;
import com.horsehour.metric.Precision;
import com.horsehour.model.EnsembleModel;
import com.horsehour.model.Model;
import com.horsehour.ranker.trainer.RankTrainer;
import com.horsehour.util.FileManager;

/**
 * 抽象的排名函数评测机制
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131219
 */
public class RankEvaluator {
	public RankTrainer ranker;

	public String predictFile;
	public String evalFile;

	public String trainFile;
	public String valiFile;
	public String testFile;

	public String database;
	public String evalbase;

	public boolean storePredict = false;
	public boolean preprocess = false;
	public boolean normalize = false;//标准化处理

	public Normalizer normalizer = new SumNormalizer();

	public DataSet trainset;
	public DataSet valiset;
	public DataSet testset;

	public Metric[] trainMetrics;
	public Metric[] testMetrics;
	public String[] corpus;

	public int nIter;
	public int code;
	public int kcv;//k-cross-validation

	public RankEvaluator(){
		int m = 21;
		testMetrics = new Metric[m];
		testMetrics[10] = new MAP();

		for(int k = 0; k < 10; k++){
			testMetrics[k] = new NDCG(k + 1);
			testMetrics[k + 11] = new Precision(k + 1);
		}
	}

	/**
	 * 加载训练、验证和测试数据集
	 */
	public void loadDataSet(){
		LineParserFilter lineParser = new L2RLineParser();

		if(!trainFile.isEmpty())
			trainset = DataManager.loadDataSet(trainFile, lineParser);
		if(!valiFile.isEmpty())
			valiset = DataManager.loadDataSet(valiFile, lineParser);
		if(!testFile.isEmpty())
			testset = DataManager.loadDataSet(testFile, lineParser);

		if(trainset != null)
			if(preprocess)
				DataManager.preprocess(trainset);

		if(normalize){
			if(trainset != null)
				normalizer.normalize(trainset);

			if(valiset != null)
				normalizer.normalize(valiset);

			if(testset != null)
				normalizer.normalize(testset);
		}
	}

	/**
	 * 加载数据集
	 * @param file
	 * @param normalize
	 */
	public DataSet loadDataSet(String file, boolean normalize){
		DataSet dataset;
		dataset = DataManager.loadDataSet(file, new L2RLineParser());

		if(normalize)
			normalizer.normalize(dataset);

		return dataset;
	}

	/**
	 * 使用指定模型预测数据样本分值,写入指定文件
	 * @param trainer
	 * @param modelFile
	 * @param dataFile
	 * @param output
	 */
	public void predict(RankTrainer trainer, String modelFile, String dataFile, String output){
		Model model = trainer.loadModel(modelFile);
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * 使用指定模型预测数据样本分值,写入指定文件
	 * @param modelFile
	 * @param dataFile
	 * @param output
	 */
	public void predict(String modelFile, String dataFile, String output){
		Model model = ranker.loadModel(modelFile);
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}


	/**
	 * 将模型预测数据样本的分值,写入指定文件
	 * @param model
	 * @param dataFile
	 * @param output
	 */
	public void predict(Model model, String dataFile, String output){
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * 将模型预测数据样本的分值,写入指定文件
	 * @param model
	 * @param dataset
	 * @param output
	 */
	public void predict(Model model, DataSet dataset, String output){
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * 根据排名函数的预测结果、数据集标记的类别/等级, 使用指定指标评估预测精度,写入指定文件
	 * @param ranker
	 * @param predictFile
	 * @param dataFile
	 * @param metrics
	 * @param output
	 */
	public void eval(String predictFile, String dataFile, Metric[] metrics, String output)
	{
		List<String> predictLines = new ArrayList<String>(); 
		FileManager.readLines(predictFile, predictLines);

		DataSet dataset = loadDataSet(dataFile, normalize);
		int k = metrics.length;
		double[] perf = new double[k];

		int count = 0;
		int m = dataset.size();
		for(int i = 0; i < m; i++){
			List<Double> predict = new ArrayList<Double>();
			SampleSet sampleset = dataset.getSampleSet(i);
			int n = sampleset.size();
			for(int j = 0; j < n; j++){
				predict.add(Double.parseDouble(predictLines.get(count)));
				count++;
			}

			for(int j = 0; j < k; j++)
				perf[j] += metrics[j].measure(sampleset.getLabelList(), predict);
		}

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < k; i++)
			sb.append(perf[i]/m + "\t");
		sb.append("\r\n");

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * 根据排名函数的预测结果、数据集标记的类别/等级, 使用指定指标评估预测精度,写入指定文件
	 * @param ranker
	 * @param predict
	 * @param dataFile
	 * @param metrics
	 * @param output
	 */
	public void eval(double[][] predict, DataSet dataset, Metric[] metrics, String output)
	{
		int k = metrics.length;
		double[] perf = new double[k];

		int m = dataset.size();
		for(int i = 0; i < m; i++){
			SampleSet sampleset = dataset.getSampleSet(i);
			for(int j = 0; j < k; j++)
				perf[j] += metrics[j].measure(sampleset.getLabels(), predict[i]);
		}

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < k; i++)
			sb.append(perf[i]/m + "\t");
		sb.append("\r\n");

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * 将预测结果写入指定文件
	 * @param predict
	 * @param predictFile
	 */
	public void store(double[][] predict, String predictFile){
		StringBuffer sb = new StringBuffer();
		int size = predict.length;
		for(int i = 0; i < size; i++){
			int n = predict[i].length;

			for(int j = 0; j < n; j++)
				sb.append(predict[i][j] + "\r\n");
		}
		FileManager.writeFile(predictFile, sb.toString());
	}

	/**
	 * 根据集成模型在验证集指定指标上的表现选择模型,并使用它在测试集上进行测试
	 * @param ens
	 * @param valiset
	 * @param metric
	 * @param evalFile
	 * @return 最佳集成模型基本模型的个数-1
	 */
	public int selectModel(Model ens, DataSet valiset, DataSet testset, 
			Metric metric, String evalFile)
	{
		int sz = ((EnsembleModel) ens).size();
		int m = valiset.size();
		double[][] prediction = new double[m][];
		
		int bestId = -1;
		double best = 0;
		for(int i = 0; i < sz; i++){
			Model weak = ((EnsembleModel) ens).getModel(i);
			double alpha = ((EnsembleModel) ens).getWeight(i);

			double perf = 0;
			SampleSet sampleset;
			for(int j = 0; j < m; j++){
				sampleset = valiset.getSampleSet(j);
				if(prediction[j] == null)
					prediction[j] = new double[sampleset.size()];
				
				prediction[j] = MathLib.linearCombinate(prediction[j], 1.0,
						weak.predict(sampleset), alpha);

				perf += metric.measure(sampleset.getLabels(), prediction[j]);
			}

			perf /= m;

			if(perf > best){
				best = perf;
				bestId = i;
			}
		}

		//在验证集上表现最佳的集成模型
		Model model = ((EnsembleModel) ens).getSubEnsemble(0, bestId);
		prediction = model.predict(testset);
		eval(prediction, testset, testMetrics, evalFile);

		return bestId;
	}
}