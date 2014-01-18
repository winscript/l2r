package com.horsehour.util;

import com.mathworks.LPSolver;
import com.mathworks.LinePlot;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 * MatlabUtil调用Matlab常用的工具类,比如画图
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012/12/09
 */
public class MatlabUtil {
	
	/**
	 * 对文件中的数据画线,一列一条线
	 * @param dir
	 * @param startId
	 * @param endId
	 */
	public static void plot(String dir, int startId, int endId){
		LinePlot plotter = null;
		MWCharArray file = null;
		try {
			plotter = new LinePlot();
			file = new MWCharArray(dir);
			plotter.plotLine(file, startId, endId);
		} catch (MWException e) {
			e.printStackTrace();
			return;
		}finally{
			MWCharArray.disposeArray(file);
			if(plotter != null)
				plotter.dispose();
		}
	}

	/**
	 * 利用Matlab求解CCR模型 
	 * @param featureMatrix
	 * @param label
	 * @param ori
	 */
	public static void solve(double[][] featureMatrix, double[] label, int ori){
		LPSolver solver = null;
		MWNumericArray bigA = null;
		MWNumericArray boundB = null;
		
		bigA = new MWNumericArray(featureMatrix, MWClassID.DOUBLE);
		boundB = new MWNumericArray(label, MWClassID.DOUBLE);
		
		Object[] result = null;
		
		try {
			solver = new LPSolver();
			//第一个参数“3”表示类LPSolver返回3个输出变量
			result = solver.solveCCR(3, bigA, boundB, ori);

		} catch (MWException e) {
			e.printStackTrace();
			return;
		} finally {
			if(solver != null)
				solver.dispose();
		}

		MWNumericArray obj = (MWNumericArray) result[0];//目标值（第一个参数）
		MWNumericArray weight = (MWNumericArray) result[1];//最优权值（第二个参数）
		MWNumericArray flag = (MWNumericArray) result[2];//退出标识（第三个参数）
		
		if(flag.getInt() == 1){
			for(int i = 1; i <= featureMatrix.length; i++){
				System.out.println(obj.getDouble(i));
				for(int j = 1; j <= label.length; j++)
					System.out.println(weight.getDouble(j) + " ");
				
			}
		}

		obj.dispose();
		weight.dispose();
		flag.dispose();
	}

	public static void main(String[] args){
		TickClock.beginTick();
		
		String dataDirectory = "c:/users/dell/desktop/track.txt";
		plot(dataDirectory, 2, 3);
		
		TickClock.stopTick();
	}
}
