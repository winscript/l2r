package com.horsehour.math.lpsolver;

import java.util.Arrays;

import com.horsehour.datum.SampleSet;

/**
 * Simplex单纯形法
 * <p>给定A∈R^{m*n}, b∈R^m, c∈R^n, 求解线性规划问题：<br/>
 * maximum cx<br/>
 * subject to<br/>
 * Ax <= b, x>=0<br/>
 * 如果b>=0,则x=0就是一个基本可行解.<br/></p>
 * <p>
 * 创建的单纯形表有m+1行,n+m+1列,其中第m+n列是b(rhs),目标函数在第m行,
 * 从第m列到m+n-1列都是松弛变量. 
 * </p>
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130506
 * @see http://algs4.cs.princeton.edu/65reductions/Simplex.java.html
 */
public class Simplex {
	private static final float epsilon = 1.0E-20F;

	//单纯形表
	private double[][] tableaux;

	//约束的个数(m),原问题变量个数(n)
	private int m, n;
	
	//basis[i]对应于第i行的基本量
	private int[] basis;

	/**
	 * sets up the simplex tableaux
	 * @param A
	 * @param b
	 * @param c
	 */
	public Simplex(double[][] A, double[] b, double[] c) {
		m = b.length;
		n = c.length;

		tableaux = new double[m + 1][n + m + 1];
		
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				tableaux[i][j] = A[i][j];
		
		for (int i = 0; i < m; i++)
			tableaux[i][n + i] = 1;
		
		for (int j = 0; j < n; j++)
			tableaux[m][j] = c[j];
		
		for (int i = 0; i < m; i++)
			tableaux[i][m + n] = b[i];

		basis = new int[m];
		for (int i = 0; i < m; i++) 
			basis[i] = n + i;

		solve();//求解模型

		//检查最优性条件(optimality conditions)
		assert check(A, b, c);

	}

	/**
	 * run simplex algorithm starting from initial BFS
	 */
	private void solve() {
		while (true) {

			// find entering column q
			int q = bland();
			if (q == -1) break;  // optimal

			// find leaving row p
			int p = minRatioRule(q);
			if (p == -1) 
				throw new RuntimeException("Linear program is unbounded");

			// pivot
			pivot(p, q);

			// update basis
			basis[p] = q;
		}
	}

	/**
	 * @return lowest index of a non-basic column with a positive cost
	 */
	private int bland() {
		for (int j = 0; j < m + n; j++)
			if (tableaux[m][j] > 0) return j;
		return -1;  // optimal
	}

	/**
	 * index of a non-basic column with most positive cost
	 * @return
	 */
	public int dantzig() {
		int q = 0;
		for (int j = 1; j < m + n; j++)
			if (tableaux[m][j] > tableaux[m][q]) q = j;

		if (tableaux[m][q] <= 0) return -1;  // optimal
		else return q;
	}

	/**
	 * find row p using min ratio rule (-1 if no such row)
	 * @param q
	 * @return
	 */
	private int minRatioRule(int q) {
		int p = -1;
		for (int i = 0; i < m; i++) {
			if (tableaux[i][q] <= 0) continue;
			else if (p == -1) p = i;
			else if ((tableaux[i][m+n] / tableaux[i][q]) < (tableaux[p][m+n] / tableaux[p][q])) p = i;
		}
		return p;
	}

	/**
	 * pivot on entry (p, q) using Gauss-Jordan elimination
	 * @param p
	 * @param q
	 */
	private void pivot(int p, int q) {

		// everything but row p and column q
		for (int i = 0; i <= m; i++)
			for (int j = 0; j <= m + n; j++)
				if (i != p && j != q) tableaux[i][j] -= tableaux[p][j] * tableaux[i][q] / tableaux[p][q];

		// zero out column q
		for (int i = 0; i <= m; i++)
			if (i != p) tableaux[i][q] = 0;

		// scale row p
		for (int j = 0; j <= m + n; j++)
			if (j != q) tableaux[p][j] /= tableaux[p][q];
		tableaux[p][q] = 1;
	}

	/**
	 * @return optimal objective value
	 */
	public double value() {
		return -tableaux[m][m+n];
	}

	/**
	 * @return primal solution vector
	 */
	public double[] primal() {
		double[] x = new double[n];
		for (int i = 0; i < m; i++)
			if (basis[i] < n) x[basis[i]] = tableaux[i][m+n];
		return x;
	}

	/**
	 * @return dual solution vector
	 */
	public double[] dual() {
		double[] y = new double[m];
		for (int i = 0; i < m; i++)
			y[i] = -tableaux[m][n+i];
		return y;
	}


	/**
	 * 判断原问题是否可行
	 * @param A
	 * @param b
	 * @return true if primal feasible, false else
	 */
	private boolean isPrimalFeasible(double[][] A, double[] b) {
		double[] x = primal();

		// check that x >= 0
		for (int j = 0; j < x.length; j++) {
			if (x[j] < 0.0) {
				System.out.println("x[" + j + "] = " + x[j] + " is negative");
				return false;
			}
		}

		// check that Ax <= b
		for (int i = 0; i < m; i++) {
			double sum = 0.0;
			for (int j = 0; j < n; j++) {
				sum += A[i][j] * x[j];
			}
			if (sum > b[i] + epsilon) {
				System.out.println("not primal feasible");
				System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断对偶问题是否可行（Dual Feasible）
	 * @param A
	 * @param c
	 * @return true if dual feasible, false else
	 */
	private boolean isDualFeasible(double[][] A, double[] c) {
		double[] y = dual();

		// check that y >= 0
		for (int i = 0; i < y.length; i++) {
			if (y[i] < 0.0) {
				System.out.println("y[" + i + "] = " + y[i] + " is negative");
				return false;
			}
		}

		// check that yA >= c
		for (int j = 0; j < n; j++) {
			double sum = 0.0;
			for (int i = 0; i < m; i++) {
				sum += A[i][j] * y[i];
			}
			if (sum < c[j] - epsilon) {
				System.out.println("not dual feasible");
				System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
				return false;
			}
		}
		return true;
	}

	// check that optimal value = cx = yb
	private boolean isOptimal(double[] b, double[] c) {
		double[] x = primal();
		double[] y = dual();
		double value = value();

		// check that value = cx = yb
		double value1 = 0;
		for (int j = 0; j < x.length; j++)
			value1 += c[j] * x[j];
		double value2 = 0;
		for (int i = 0; i < y.length; i++)
			value2 += y[i] * b[i];
		if (Math.abs(value - value1) > epsilon || Math.abs(value - value2) > epsilon) {
			System.out.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
			return false;
		}

		return true;
	}

	/**
	 * 检查最优性条件
	 * @param A
	 * @param b
	 * @param c
	 * @return 通过则true，否则为false
	 */
	private boolean check(double[][]A, double[] b, double[] c) {
		return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
	}

	/**
	 * 打印单纯形表
	 */
	public void show() {
		System.out.println("M = " + m);
		System.out.println("N = " + n);
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= m + n; j++) {
				System.out.printf("%7.2f ", tableaux[i][j]);
			}
			System.out.println();
		}
		System.out.println("value = " + value());
		for (int i = 0; i < m; i++)
			if (basis[i] < n) System.out.println("x_" + basis[i] + " = " + tableaux[i][m+n]);
		System.out.println();
	}

	/******************************************************************************************/
	/************************************ 使用Simplex求解DEA模型 **********************************/
	/******************************************************************************************/
	/**
	 * 根据DEA模型计算各个决策单元的相对效率值
	 * @param data
	 * @param bound
	 * @return relative efficiency
	 */
	public double[] getRelativeEfficiency(double[][] data, double[] bound){
		int nRow = data.length;
		int nCol = data[0].length - 1;

		double[] reScore = new double[nRow];
		double[] c = new double[nCol];

		Simplex simplex = null;
		for(int i = 0; i < nRow; i++){
			c = Arrays.copyOf(data[i], nCol);
			simplex = new Simplex(data, bound, c);
			reScore[i] = simplex.value();
		}
		return reScore;
	}

	/**
	 * 计算各SampleSet中全部sample的相对效率分值
	 * @param sampleSet
	 * @return 相对效率分值
	 */
	public double[] getRelativeEfficiency(SampleSet sampleSet){
		int nRow = sampleSet.size(), nCol = sampleSet.getDim();
		double[][] data = new double[nRow][nCol];
		for(int i = 0; i < nRow; i++)
			data[i] = sampleSet.getSample(i).getFeatures();  

		double[] bound = new double[nRow];
		Arrays.fill(bound, 1);
		
		return getRelativeEfficiency(data, bound); 
	}
	
	/**
	 * 计算得到的权重
	 * @param sampleSet
	 * @return 最优权值
	 */
	public static double[][] getPrimalWeight(SampleSet sampleSet){
		int nRow = sampleSet.size(), nCol = sampleSet.getDim();
		double[][] data = new double[nRow][nCol];
		for(int i = 0; i < nRow; i++)
			data[i] = sampleSet.getSample(i).getFeatures();

		double[] bound = new double[nRow];
		Arrays.fill(bound, 1);
		
		double[][] primalWeight = new double[nRow][nCol];
		Simplex simplex;
		for(int i = 0; i < nRow; i++){
			simplex = new Simplex(data, bound, data[i]);
			primalWeight[i] = simplex.primal();
		}
		return primalWeight;
	}
}