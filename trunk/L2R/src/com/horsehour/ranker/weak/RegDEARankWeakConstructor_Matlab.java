package com.horsehour.ranker.weak;

import java.util.Arrays;

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
 * 基于Matlab求解正则化的CCR模型,将结果保存到本地文件-CCRData
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
 * @since 20130621
 */

public class RegDEARankWeakConstructor_Matlab {
		private DataSet trainset;
		private String dest = "";

		private int oriented = 0;

		public LPSolver solver;
		public double[][] dataMatrix;
		
		public ActivationFunction activeFun = new Log1pFunction((float) Math.E);

		public double[] bound;
		public int nRow = -1, nCol = -1;
		
		public double cost = 0;

		public RegDEARankWeakConstructor_Matlab(String corpus, int foldId, int oriented){
			String src = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/train.txt"; 
			trainset = DataManager.loadDataSet(src, "utf-8", new L2RLineParser());
			nCol = trainset.getDim();

			dest = "F:/Research/Data/CCRData/" + corpus + "/Fold" + foldId + "/";
			
			this.oriented = oriented;
			if(oriented == 0){
				dest += "RegOC2R-0.1.txt";
				cost = 0.1f;
			}else{
				dest += "RegIC2R-0.1.txt";
				cost = 0.1f;
			}
		}

		/**
		 * 求解CCR模型
		 */
		public void solveRegCCR(){

			try {
				solver = new LPSolver();
			} catch (MWException e) {
				e.printStackTrace();
				return;
			}
			
			SampleSet sampleSet = null;
			Solution[] sols = null;

			StringBuffer sb = null;
			for(int qid = 0; qid < trainset.size(); qid++){
				sampleSet = trainset.getSampleSet(qid);
				nRow = sampleSet.size();
				
				prepareData(sampleSet);
				
				sols = solveProblem();
				
				String query = sampleSet.getSample(0).getQid();
				sb = new StringBuffer();
				
				for(int id = 0; id < sols.length; id++){
					if(sols[id] == null)
						continue;

					sb.append(sampleSet.getLabel(id));
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
		 * 
		 * @param sampleSet
		 */
		private void prepareData(SampleSet sampleSet){
			dataMatrix = new double[nRow][nCol];
			for(int i = 0; i < nRow; i++)
				dataMatrix[i] = sampleSet.getSample(i).getFeatures();
			
			bound = new double[nRow];
			
			if(oriented == 1)
				populateBound();
			else
				populateBound(sampleSet.getLabels());
		}

		/**
		 * 求解模型
		 * @return 问题的解
		 */
		private Solution[] solveProblem(){
			Solution[] sols = new Solution[nRow];
			
			MWNumericArray bigA = null, boundB = null;
			
			bigA = new MWNumericArray(dataMatrix, MWClassID.DOUBLE);
			boundB = new MWNumericArray(bound, MWClassID.DOUBLE);

			Object[] result = null;
			
			try {
				result = solver.solveRegCCR(3, bigA, boundB, cost, oriented);
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
						w[j - 1] = weight.getFloat((i - 1) * nCol + j);
						if(w[j - 1] < 1.0E-5)
							w[j - 1] = 0;
					}
					sols[i - 1] = new Solution(obj.getFloat(i), w);				
				}
			}

			obj.dispose();
			weight.dispose();
			flag.dispose();

			return sols;
		}

		/**
		 * 填充bound 
		 * @param label
		 */
		private void populateBound(int[] labels){
			for(int i = 0; i < nRow; i++)
				bound[i] = activeFun.calc(labels[i]);
		}
		private void populateBound(){
			Arrays.fill(bound, 1);
		}

		public static void main(String[] args) {
			TickClock.beginTick();

			RegDEARankWeakConstructor_Matlab computer = null;
			String[] corpusList = {"MQ2007", "MQ2008", "OHSUMED"};
			for(int o = 0; o <= 1; o++){//oriented
				for(String corpus : corpusList)//corpus
					for(int i = 1; i <= 5; i++)//foldid
					{ 
						computer = new RegDEARankWeakConstructor_Matlab(corpus, i, o);
						computer.solveRegCCR();
					}
			}

			TickClock.stopTick();
		}
	}
