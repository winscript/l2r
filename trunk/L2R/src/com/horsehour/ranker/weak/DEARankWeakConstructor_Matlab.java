package com.horsehour.ranker.weak;

import java.util.Arrays;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.function.ActivationFunction;
import com.horsehour.function.Log1pFunction;
import com.horsehour.math.lpsolver.Solution;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;
import com.mathworks.LPSolver;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 * 使用Matlab求解CCR模型,将结果保存到本地文件-CCRData
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
 * @since 20130513
 */
public class DEARankWeakConstructor_Matlab {
	private DataSet trainset;
	private String dest = "";

	private int oriented = 0;

	public LPSolver solver;
	public double[][] dataMatrix;
	
	public ActivationFunction activeFun = new Log1pFunction((float) Math.E);

	public double[] bound;
	public int nRow = -1, nCol = -1;
	
	public DEARankWeakConstructor_Matlab(String corpus, int foldId, int oriented){
		String src = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/train.txt"; 
		trainset = DataManager.loadDataSet(src, new L2RLineParser());
		nCol = trainset.getDim();

		dest = "F:/Research/Data/DEAData/" + corpus + "/Fold" + foldId + "/";

		this.oriented = oriented;
		if(oriented == 0)
			dest += "ODEA.txt";
		else
			dest += "IDEA.txt";
	}

	/**
	 * 求解CCR模型
	 */
	public void solveCCR(){
		try {
			solver = new LPSolver();
		} catch (MWException e) {
			e.printStackTrace();
			return;
		}

		SampleSet sampleset;
		Solution[] sols;

		StringBuffer sb;
		for(int qid = 0; qid < trainset.size(); qid++){
			sampleset = trainset.getSampleSet(qid);
			nRow = sampleset.size();
			
			prepareData(sampleset);
			
			sols = solveProblem();
			
			String query = sampleset.getSample(0).getQid();
			sb = new StringBuffer();
			
			for(int id = 0; id < sols.length; id++){
				if(sols[id] == null)
					continue;

				sb.append(sampleset.getLabel(id));
				sb.append("\t" + sols[id].obj);
				sb.append("\t" + query);

				for(double weight : sols[id].vars)
					sb.append("\t" + weight);

				sb.append("\r\n");
			}
			FileManager.writeFile(dest, sb.toString());
		}
	}

	/**
	 * 根据模型从sampleset中配置必要的数据
	 * 1. dataMatrix
	 * 2. bound
	 * @param sampleset
	 */
	private void prepareData(SampleSet sampleset){
		dataMatrix = new double[nRow][nCol];
		for(int i = 0; i < nRow; i++)
			dataMatrix[i] = sampleset.getSample(i).getFeatures();

		bound = new double[nRow];

		if(oriented == 1){
			Arrays.fill(bound, 1);
		}else{
			List<Integer> labels = sampleset.getLabelList();
			for(int i = 0; i < nRow; i++)
				bound[i] = activeFun.calc(labels.get(i));
		}
	}

	/**
	 * 求解模型
	 * @return 问题的解
	 */
	private Solution[] solveProblem(){
		Solution[] sols = new Solution[nRow];
		
		MWNumericArray bigA, boundB;
		
		bigA = new MWNumericArray(dataMatrix, MWClassID.DOUBLE);
		boundB = new MWNumericArray(bound, MWClassID.DOUBLE);

		Object[] result;
		
		try {
			result = solver.solveCCR(3, bigA, boundB, oriented);
		} catch (MWException e) {
			e.printStackTrace();
			return null;
		} finally {
			bigA.dispose();
			boundB.dispose();
		}

		MWNumericArray obj = (MWNumericArray) result[0],//目标值
				weight = (MWNumericArray) result[1],//最优权值
				flag = (MWNumericArray) result[2];//退出标识

		double[] w = new double[nCol];
		if(flag.getInt() == 1){
			for(int i = 1; i <= nRow; i++){
				for(int j = 1; j <= nCol; j++){
					w[j - 1] = weight.getDouble((i - 1) * nCol + j);
					if(w[j - 1] < 1.0E-5)
						w[j - 1] = 0;
				}
				sols[i - 1] = new Solution(obj.getDouble(i), w);				
			}
		}

		obj.dispose();
		weight.dispose();
		flag.dispose();

		return sols;
	}

	public static void main(String[] args) {
		TickClock.beginTick();

		DEARankWeakConstructor_Matlab computer;
//		String[] corpusList = {"TD2003", "TD2004", "NP2003", "NP2004", "HP2003", "HP2004"};
		String[] corpusList = {"HP2003", "HP2004"};
		for(int o = 0; o <= 1; o++){//oriented
			for(String corpus : corpusList)//corpus
				for(int i = 1; i <= 5; i++)//foldid
				{ 
					computer = new DEARankWeakConstructor_Matlab(corpus, i, o);
					computer.solveCCR();
				}
		}
		
		TickClock.stopTick();
	}
}