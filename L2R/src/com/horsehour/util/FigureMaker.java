package com.horsehour.util;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;

import com.horsehour.math.MathLib;

/**
 * 使用JMathPlot绘制图形
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130714
 * @see http://jmathtools.berlios.de/doku.php?id=jmathplot:tutorial
 */
public class FigureMaker {

	/**
	 * 绘制曲线
	 * @param x
	 * @param y
	 */
	public static void makeLine(double[] x, double[] y){
		Plot2DPanel panel = new Plot2DPanel();
		panel.addLinePlot("Line", x, y);
		addPanelToFrame(panel);
	}

	/**
	 * 绘制散点图
	 * @param x
	 * @param y
	 */
	public static void makeScatter(double[] x, double[] y){
		Plot2DPanel panel = new Plot2DPanel();
		panel.addScatterPlot("Scatter", x, y);
		addPanelToFrame(panel);
	}
	
	/**
	 * 绘制直方图
	 * @param x
	 * @param n-slices
	 */
	public static void makeHistogram(double[] x, int n){
		Plot2DPanel panel = new Plot2DPanel();
		panel.addHistogramPlot("Histogram", x, n);
		addPanelToFrame(panel);
	}

	/**
	 * 绘制3D网格图
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void makeGrid(double[] x, double[] y, double[][] z){
		Plot3DPanel panel = new Plot3DPanel();
		panel.addGridPlot("", x, y, z);
		addPanelToFrame(panel);
	}

	/**
	 * 向Frame中添加Panel以显示与控制
	 * @param panel
	 */
	private static void addPanelToFrame(PlotPanel panel){
		JFrame frame = new JFrame("Frame");
		frame.setContentPane(panel);
		frame.setSize(500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args){
		TickClock.beginTick();

		double[] data = new double[1000000];
		for(int i = 0; i < data.length; i++)
			data[i] = MathLib.randNorm(0, 1);
		
		FigureMaker.makeHistogram(data,50);

		TickClock.stopTick();
	}
}
