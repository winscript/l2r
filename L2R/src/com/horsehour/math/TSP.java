package com.horsehour.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.horsehour.datum.DataManager;
import com.horsehour.util.TickClock;

/**
 * 基于模拟退火算法解决货郎担问题Traveling Salesman Problem
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130714
 */
public class TSP{
	private int[] permutatePrevious;
	private int[] permutatePost;

	private double[][] blockDists;
	public double shortestDist = 0;

	public int nCities = 0;

	public TSP(String cityFile){
		loadCities(cityFile);
	}

	/**
	 * 加载城市地图数据
	 */
	private void loadCities(String cityFile){
		List<double[]> datum = DataManager.loadDatum(cityFile, "utf-8", "\t");
		nCities = datum.size();
		blockDists = new double[nCities][nCities];
		for(int i = 0; i < nCities; i++)
			blockDists[i] = Arrays.copyOf(datum.get(i), nCities);
	}

	public void init(){
		permutatePrevious = new int[nCities];
		for (int i = 0; i < nCities; i++)
			permutatePrevious[i] = i;
	}
	
	/**
	 * @param permut
	 * @return 排列permut的所有节点的距离，包括回路
	 */
	private float getDistance(int[] permut){
		float dist = 0;
		for(int i = 0; i < nCities - 1; i++)
			dist += blockDists[permut[i]][permut[i+1]];

		dist += blockDists[permut[nCities - 1]][0];//回路距离
		
		return dist;
	}

	/**
	 * 产生新的排列
	 * @param permute
	 */
	private void disturbPermut(int[] permute){
		permutatePost = Arrays.copyOf(permute, nCities);

		int i1 = 0, i2 = 0;
		while(i1 == i2){
			i1 = (int) MathLib.randUniform(0, nCities);
			i2 = (int) MathLib.randUniform(0, nCities);
		}
		
		permutatePost[i1] = permute[i2];
		permutatePost[i2] = permute[i1];
	}

	private void acceptPermut(int[] permut){
		permutatePrevious = Arrays.copyOf(permutatePost, nCities);
	}

	/**
	 * 搜索
	 */
	public void search(){
		int iter = 100;

		float temperature = 10000;
		float minTemperature = 0.00001f;

		float boltzman = 0.9999f;

		float distDiff = 0;

		Random rand = new Random();
		
		float dist = getDistance(permutatePrevious), prob = 0;
		boolean accept = false;
		
		List<Float> searchList = new ArrayList<Float>();
		while((temperature > minTemperature)||(iter > 0)){
			disturbPermut(permutatePrevious);
			distDiff = getDistance(permutatePost) - dist;
			
			prob = (float) Math.exp(-distDiff/temperature);
			accept = ((distDiff < 0)||(distDiff * (prob - rand.nextFloat())>= 0));
			
			if (accept){
				acceptPermut(permutatePost);
				dist = distDiff + dist;
			}

			temperature *= boltzman;
			iter --;
			
			searchList.add(dist);
		}
		
		shortestDist = dist;
	}
	
	/**
	 * @return 行程路线
	 */
	public String travelRoute(){
		String travelRoute = "";

		for(int i = 0; i < nCities - 1; i++)
			travelRoute += permutatePrevious[i] + " -> ";

		travelRoute += permutatePrevious[nCities - 1];
		
		return travelRoute;
	}

	public static void main(String[] args){
		TickClock.beginTick();
		
		String citymapFile = "F:/SystemBuilding/Data/TravelingSalesmanProblem.txt";
		TSP problem = new TSP(citymapFile);
		
		problem.init();
		problem.search();
		
		System.out.println("Salesman's Travel Route:\n" + problem.travelRoute());
		System.out.println("Route Length:" + problem.shortestDist);
		
		TickClock.stopTick();
	}
}