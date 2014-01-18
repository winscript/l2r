package com.horsehour.ranker.weak;

import java.util.Arrays;

import org.opensourcedea.dea.DEAProblem;
import org.opensourcedea.dea.ModelType;
import org.opensourcedea.dea.SolverReturnStatus;
import org.opensourcedea.dea.VariableOrientation;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.function.ActivationFunction;
import com.horsehour.function.Log1pFunction;
import com.horsehour.math.lpsolver.Solution;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;

/**
 * 使用Open Source DEA包计算CCR模型,将结果保存到本地文件-CCRData
 * <p>根据特殊的CCR模型,计算各个决策单元的相对最优权值,并保存到本地文件</p>
 * <p>特别地,应用到排序学习问题,计算训练数据集中所有检索词-文档对的最优权值并保存</p>
 * <p>数据保存格式与l2r原始数据类似，格式如下<br/>
 * 1 0.89 902 0.29 0.03 ...<br/>
 * 0 0.38 902 0.04 0.09 ...<br/>
 * 第一列表示文档的真实评分,第二列表示相对权值,第三列表示检索词id,后续各列表示相应的最优权值向量
 * </p>
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130508
 * @see http://www.opensourcedea.org/index.php?title=Open_Source_DEA
 */
public class DEARankWeakConstructor_OSDEA {
	private DataSet trainset = null;
	private String dest = "";
	
	private int oriented = 0;
	
	protected VariableOrientation[] varOrientation = null;
	protected double[][] dataMatrix = null;
	
	public int const_b = 1;
	public ActivationFunction trnsFunc = new Log1pFunction((float) Math.E);

	private DEAProblem problem = null;

	public DEARankWeakConstructor_OSDEA(String corpus, int foldId, int oriented){
		String src = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/train.txt"; 
		trainset = DataManager.loadDataSet(src, "utf-8", new L2RLineParser());
		dest = "F:/Research/Data/CCRData/" + corpus + "/Fold" + foldId + "/";

		this.oriented = oriented;
		if(oriented == 0)
			dest += "ODEA.txt";
		else
			dest += "IDEA.txt";
	}

	/***
	 * 求解某个检索词下所有文档构成的CCR模型
	 */
	public void solveCCR(){
		int nRow = 0, nVar = trainset.getDim() + 1;
		initProblem(nVar);
		
		Solution[] sols = null;
		SampleSet sampleSet = null;
		for(int qid = 0; qid < trainset.size(); qid++){
			sampleSet = trainset.getSampleSet(qid);
			nRow = sampleSet.size();
			
			designProblem(nRow, nVar);
			populateDataMatrix(sampleSet);
			
			double[][] efficientWeight = null;
			double[] weight = null;
			
			try {
				problem.solve();
				
				SolverReturnStatus status = problem.getOptimisationStatus();
				
				if(SolverReturnStatus.OPTIMAL_SOLUTION_FOUND.equals(status)){
					
					efficientWeight = problem.getWeight();
					sols = new Solution[nRow];

					for(int id = 0; id < nRow; id++){
						weight = Arrays.copyOf(efficientWeight[id], nVar - 1);
						if(Double.isNaN(weight[0]))
							Arrays.fill(weight, 0);

						sols[id] = new Solution(problem.getObjective(id), weight);
					}
				}
			} catch (Exception e){
				return;
			}
			archiveSolution(sampleSet, sampleSet.getSample(0).getQid(), sols);
		}
	}
	
	/**
	 * 将求解结果保存到本地文件
	 * @param sb
	 */
	private void archiveSolution(SampleSet sampleset, String query, Solution[] sols){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < sols.length; i++){
			sb.append(sampleset.getLabel(i));
			sb.append("\t" + sols[i].obj);
			sb.append("\t" + query);

			for(double weight : sols[i].vars)
				sb.append("\t" + weight);

			sb.append("\r\n");
		}
		FileManager.writeFile(dest, sb.toString());
	}
	
	/**
	 * 初始化问题-初始化各个变量的orientation(input/output)
	 * @param nVar
	 */
	protected void initProblem(int nVar){
		varOrientation = new VariableOrientation[nVar];
		if(oriented == 0){
			Arrays.fill(varOrientation, VariableOrientation.INPUT);
			varOrientation[nVar - 1] = VariableOrientation.OUTPUT;
		}else{
			Arrays.fill(varOrientation, VariableOrientation.OUTPUT);
			varOrientation[nVar - 1] = VariableOrientation.INPUT;
		}
	}

	/**
	 * 设计DEA模型
	 * @param nDMU
	 * @param dim
	 */
	protected void designProblem(int nDMU, int dim){
		problem = new DEAProblem(nDMU, dim);
		dataMatrix = new double[nDMU][dim];//The last is output
		
		problem.setVariableOrientations(varOrientation);
		problem.setDataMatrix(dataMatrix);

		if(oriented == 0)
			problem.setModelType(ModelType.CCR_O);//output oriented
		else
			problem.setModelType(ModelType.CCR_I);//input oriented
	}
	
	/**
	 * 使用样本集合填充problem所需数据
	 * @param sampleSet
	 */
	protected void populateDataMatrix(SampleSet sampleSet){
		int nDMU = sampleSet.size(),
				dim = sampleSet.getDim();
		for(int idx = 0; idx < nDMU; idx++){
			for(int fid = 0; fid < dim; fid++)
				dataMatrix[idx][fid] = sampleSet.getSample(idx).getFeature(fid);
			
			dataMatrix[idx][dim] = 
					(oriented == 0) ? trnsFunc.calc(sampleSet.getSample(idx).getLabel()) : const_b;
		}
	}
	
	public static void main(String[] args) {
		TickClock.beginTick();
		
		DEARankWeakConstructor_OSDEA computer = null;
		String[] corpusList = {"MQ2007", "MQ2008", "OHSUMED"};
		for(int o = 0; o <= 1; o++){//oriented
			for(String corpus : corpusList)//corpus
				for(int i = 1; i <= 5; i++)//foldid
				{ 
					computer = new DEARankWeakConstructor_OSDEA(corpus, i, o);
					computer.solveCCR();
				}
		}

		TickClock.stopTick();
	}
}
