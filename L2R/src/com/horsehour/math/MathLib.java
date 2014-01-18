package com.horsehour.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A tiny mathematical utility
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131208
 */
public class MathLib {
	/**
	 * 计算欧式范数
	 * @param array
	 * @return sqrt(∑array[i]^2)
	 */
	public static double getEuclideanNorm(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i] * array[i];

		return Math.sqrt(sum);
	}
	public static float getEuclideanNorm(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i] * array[i];

		return (float) Math.sqrt(sum);
	}
	
	/**
	 * 计算加权欧式范数
	 * @param array
	 * @param weight
	 * @return sqrt(∑w[i]*arr[i]^2)
	 */
	public static double getWeightedEuclideanNorm(double[] array, double[] weight){
		double wsum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * array[i] * array[i];
		
		return Math.sqrt(wsum);
	}
	
	public static float getWeightedEuclideanNorm(float[] array, float[] weight){
		float wsum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * array[i] * array[i];
		
		return (float) Math.sqrt(wsum);
	}
	
	/**
	 * 计算L2范数
	 * @param array
	 * @return ∑array[i]^2
	 */
	public static double getL2Norm(double[] array){
		return getEuclideanNorm(array);
	}
	public static float getL2Norm(float[] array){
		return getEuclideanNorm(array);
	}
	
	/**
	 * 计算L1范数
	 * @param array
	 * @return ∑|array[i]|
	 */
	public static double getL1Norm(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += Math.abs(array[i]);
		
		return sum;
	}
	public static float getL1Norm(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += Math.abs(array[i]);
		
		return sum;
	}
	
	/**
	 * 计算内积
	 * @param arr1
	 * @param arr2
	 * @return ∑arr1[i]*arr2[i]
	 */
	public static double innerProduct(double[] arr1, double[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double sum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			sum += arr1[i] * arr2[i];
		
		return sum;
	}
	
	public static float innerProduct(float[] arr1, float[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float sum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			sum += arr1[i] * arr2[i];
		
		return sum;
	}
	/**
	 * 计算加权内积
	 * @param arr1
	 * @param arr2
	 * @return ∑w[i]arr1[i]*arr2[i]
	 */
	public static double weightedInnerProduct(double[] arr1, double[] arr2, double[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double wsum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * arr1[i] * arr2[i];

		return wsum;
	}
	
	public static float weightedInnerProduct(float[] arr1, float[] arr2, float[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float wsum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * arr1[i] * arr2[i];

		return wsum;
	}
	
	/**
	 * 加和
	 * @param array
	 * @return ∑array[i]
	 */
	public static double sum(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	public static float sum(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	public static int sum(int[] array){
		int sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	/**
	 * 标准化,基于欧式范数(2范数)
	 * @param array
	 */
	public static void normalize(double[] array){
		double norm = getEuclideanNorm(array);
		if(norm > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= norm;
		}
	}
	
	public static void normalize(float[] array){
		double norm = getEuclideanNorm(array);
		if(norm > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= norm;
		}
	}
	/**
	 * 标准化,基于和
	 * @param array
	 */
	public static void sumNormalize(double[] array){
		double sum = sum(array);
		if(sum > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= sum;
		}
	}
	
	public static void sumNormalize(float[] array){
		float sum = sum(array);
		if(sum > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= sum;
		}
	}
	/**
	 * 标准化,基于最大值
	 * @param array
	 */
	public static void maxNormalize(double[] array){
		double max = getMax(array);
		if(max > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= max;			
		}
	}
	public static void maxNormalize(float[] array){
		float max = getMax(array);
		if(max > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= max;
		}
	}
	
	/**
	 * zscore标准化
	 * @param array
	 */
	public static void zscoreNormalize(double[] array){
		int n = array.length;
		double mean = sum(array)/n;
		double std = 0;
		for(int i = 0; i < n; i++){
			double val = array[i] - mean;
			std += val * val;
		}
		if(std > 0){
			std = Math.sqrt(std/(n - 1));
			for(int i = 0; i < n; i++)
				array[i] = (array[i] - mean)/std;
		}
	}

	public static void zscoreNormalize(float[] array){
		int n = array.length;
		float mean = sum(array)/n;
		float std = 0;
		for(int i = 0; i < n; i++){
			float val = array[i] - mean;
			std += val * val;
		}
		if(std > 0){
			std = (float) Math.sqrt(std/(n - 1));
			for(int i = 0; i < n; i++)
				array[i] = (array[i] - mean)/std;
		}
	}
	
	/**
	 * 寻找最大的数
	 * @param array
	 * @return largest
	 */
	public static double getMax(double[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		double max = array[0];
		if(array.length == 1)
			return max;

		int len = array.length;
		for(int i = 1; i < len; i++)
			max = (max > array[i]) ? max : array[i];

		return max;
	}

	public static float getMax(float[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		float max = array[0];
		
		if(array.length == 1)
			return max;

		int len = array.length;
		for(int i = 1; i < len; i++)
			max = (max > array[i]) ? max : array[i];

		return max;
	}
	
	/**
	 * 寻找最小的数
	 * @param array
	 * @return smallest
	 */
	public static double getMin(double[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		double min = array[0];
		if(array.length == 1)
			return min;

		int len = array.length;
		for(int i = 1; i < len; i++)
			min = (min < array[i]) ? min : array[i];

		return min;
	}

	public static float getMin(float[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		float min = array[0];
		if(array.length == 1)
			return min;

		int len = array.length;
		for(int i = 1; i < len; i++)
			min = (min < array[i]) ? min : array[i];

		return min;
	}
	
	/**
	 * 计算余弦相似度
	 * @param arr1
	 * @param arr2
	 * @return 余弦值
	 */
	public static double getSimCosine(double[] arr1, double[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double norm1 = 0;
		double norm2 = 0;
		double inprod = 0;
		int n = arr1.length;
		for(int i = 0; i < n; i++){
			inprod += arr1[i] * arr2[i];
			norm1 += arr1[i] * arr1[i];
			norm2 += arr2[i] * arr2[i];
		}

		double ret = 0;
		if(norm1 * norm2 > Double.MIN_VALUE)
			ret = inprod / (Math.sqrt(norm1 * norm2));
		return ret;
	}
	public static float getSimCosine(float[] arr1, float[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float norm1 = 0;
		float norm2 = 0;
		float inprod = 0;
		int n = arr1.length;
		for(int i = 0; i < n; i++){
			inprod += arr1[i] * arr2[i];
			norm1 += arr1[i] * arr1[i];
			norm2 += arr2[i] * arr2[i];
		}

		float ret = 0;
		if(norm1 * norm2 > Float.MIN_VALUE)
			ret = (float) (inprod / (Math.sqrt(norm1 * norm2)));
		return ret;
	}

	/**
	 * 计算加权余弦相似度
	 * @param arr1
	 * @param arr2
	 * @param weight
	 * @return weighted cosine similarity
	 */
	public static float getWeightedSimCosine(float[] arr1, float[] arr2, float[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}

		float ret = 0;
		if(arr1.length == arr2.length && arr2.length == weight.length){
			float norm1 = 0;
			float norm2 = 0;
			float inprod = 0;
			int n = arr1.length;
			for(int i = 0; i < n; i++){
				inprod += arr1[i] * arr2[i] * weight[i];
				norm1 += arr1[i] * arr1[i] * weight[i];
				norm2 += arr2[i] * arr2[i] * weight[i];
			}

			if(norm1 * norm2 > Float.MIN_VALUE)
				ret = (float) (inprod / (Math.sqrt(norm1 * norm2)));
		}else{
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}
		return ret;
	}

	public static double getWeightedSimCosine(double[] arr1, double[] arr2, double[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}

		double ret = 0;
		if(arr1.length == arr2.length && arr2.length == weight.length){
			double norm1 = 0;
			double norm2 = 0;
			double inprod = 0;
			int n = arr1.length;
			for(int i = 0; i < n; i++){
				inprod += arr1[i] * arr2[i] * weight[i];
				norm1 += arr1[i] * arr1[i] * weight[i];
				norm2 += arr2[i] * arr2[i] * weight[i];
			}

			if(norm1 * norm2 > Float.MIN_VALUE)
				ret =  inprod / (Math.sqrt(norm1 * norm2));
		}else{
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		return ret;
	}
	
	/**
	 * 计算两个字符串的Hamming距离（等长字符串，相同位置不同字符的个数）
	 * @param str1
	 * @param str2
	 * @return 不同字符的个数
	 */
	public static int hammingDistance(String str1, String str2){
		int len = str1.length();
		if(len != str2.length()){
			System.err.println("Inconsistent Dimensions.");
			System.exit(0);
		}

		int concordant = 0;
		for(int i = 0; i < len; i++){
			if(str1.charAt(i) == str2.charAt(i)) 
				concordant++;
		}
		return len - concordant;
	}
	
	/**
	 * 计算两个数组的欧式距离
	 * @param arr1
	 * @param arr2
	 * @return sqrt(∑(arr1[i] - arr2[i]^2))
	 */
	public static double euclideanDistance(double[] arr1, double[] arr2){
		return getEuclideanNorm(diff(arr1, arr2));
	}
	public static float euclideanDistance(float[] arr1, float[] arr2){
		return getEuclideanNorm(diff(arr1, arr2));
	}

	/**
	 * 放大
	 * @param array
	 * @param factor
	 * @return 数组放大
	 */
	public static double[] scale(double[] array, double factor){
		int len = array.length;
		double arr[] = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = array[i] * factor;

		return arr;
	}

	public static float[] scale(float[] array, float factor){
		int len = array.length;
		float arr[] = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = array[i] * factor;
		
		return arr;
	}

	/**
	 * 加和
	 * @param arr1
	 * @param arr2
	 * @return arr[i] = arr1[i] + arr2[i]
	 */
	public static double[] add(double[] arr1, double[] arr2){
		int len = arr1.length;
		double[] arr = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] + arr2[i];

		return arr;
	}

	public static float[] add(float[] arr1, float[] arr2){
		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] + arr2[i];
		return arr;
	}
	/**
	 * 差
	 * @param arr1
	 * @param arr2
	 * @return arr[i] = arr1[i] - arr2[i]
	 */
	public static double[] diff(double[] arr1, double[] arr2){
		int len = arr1.length;
		double[] arr = new double[len];
		
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] - arr2[i];
		
		return arr;
	}
	
	public static float[] diff(float[] arr1, float[] arr2){
		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] - arr2[i];
		
		return arr;
	}
	/**
	 * 线性组合
	 * @param arr1
	 * @param f1
	 * @param arr2
	 * @param f2
	 * @return arr[i] = arr1[i]*f1 + arr2[i]*f2
	 */
	public static double[] linearCombinate(double[] arr1, double f1, double[] arr2, double f2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		int len = arr1.length;
		double[] arr = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] * f1 + arr2[i] * f2;
		
		return arr;
	}
	
	public static float[] linearCombinate(float[] arr1, float f1, float[] arr2, float f2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] * f1 + arr2[i] * f2;

		return arr;
	}
	
	/**
	 * 链表转换为数组
	 * @param <K>
	 * @param list
	 * @return array[i] = list.get(i)
	 */
	public static double[] listToArray(List<Double> list){
		int len = list.size();
		double[] array = new double[len];
		for(int i = 0; i < len; i++)
			array[i] = list.get(i);
	
		return array;
	}

	/**
	 * 将数组转存为链表
	 * @param array
	 * @return list.get(i) = array[i]
	 */
	public static List<Double> arrayToList(double[] array){
		List<Double> list = new ArrayList<Double>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		return list;
	}
	public static List<Float> arrayToList(float[] array){
		List<Float> list = new ArrayList<Float>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		return list;
	}

	/**
	 * 四舍五入
	 * @param valueToRound
	 * @param numberOfDecimalPlaces
	 * @return 精确到小数点后某几位 
	 */
	public static double round(double valueToRound, int numberOfDecimalPlaces)
	{
	    double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
	    double interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}
	
	public static float round(float valueToRound, int numberOfDecimalPlaces){
		float multipicationFactor = (float)Math.pow(10, numberOfDecimalPlaces);
	    float interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}

	/**
	 * 随机生成指定范围内的十进制数,并要求精确到小数点后precision位
	 * @param lowerBound
	 * @param upperBound
	 * @param nDigit
	 * @return random number in [lowerBound, upperBound] with precision
	 */
	public static double rand(double lowerBound, double upperBound, int nDigit){
		double rand = lowerBound + Math.random() * (upperBound - lowerBound);
		StringBuilder digits = new StringBuilder();
		while(nDigit-- > 0)
			digits.append("0");
		
		DecimalFormat dFormat = new DecimalFormat("#." + digits.toString());
		return new Double(dFormat.format(rand));
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @param num
	 * @return 从[lowerB,upperB]中随机抽取num个互不相同的整数
	 */
	public static List<Integer> randUnique(int lowerB, int upperB, int num){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = upperB; i >= lowerB; i--)
			numbers.add(i);
		
		Collections.shuffle(numbers);
		return numbers.subList(0, num);
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @return 从[lowerB,upperB]中随机抽取1个整数
	 */
	public static int rand(int lowerB, int upperB){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = upperB; i >= lowerB; i--)
			numbers.add(i);
		
		Collections.shuffle(numbers);
		return numbers.get(0);
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @param num
	 * @return 从[lowerB,upperB]随机抽取num个整数
	 */
	public static List<Integer> rand(int lowerB, int upperB, int num){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < num; i++)
			numbers.add(rand(lowerB, upperB));

		return numbers;
	}

	/**
	 * 生成服从高斯分布的随机数据
	 * @param miu
	 * @param sigma
	 * @return x~N(miu, sigma)
	 */
	public static double randNorm(double miu, double sigma){
		Random rand = new Random();
		return miu + rand.nextGaussian() * sigma;
	}
	
	/**
	 * 生成服从均匀分布的随机数据
	 * @param lowerBound
	 * @param upperBound
	 * @return x ~ U(lowerBound,upperBound)
	 */
	public static float randUniform(float lowerBound, float upperBound){
		return (float) (lowerBound + Math.random()*(upperBound - lowerBound));
	}
	
	/**
	 * 生成给定长度的随机概率分布
	 * @param len
	 * @return prob distribution
	 */
	public static float[] randDistribution(int len){
		float[] distr = new float[len];
		for(int i = 0; i < len; i++)
			distr[i] = randUniform(0,1);
		
		sumNormalize(distr);
		
		return distr;
	}

	/**
	 * 生成指定大小的行随机方阵
	 * @param dim
	 * @return 随机方阵
	 */
	public static float[][] stochasticMatrix(int dim){
		float[][] matrix = new float[dim][dim];
		for(int i = 0; i < dim; i++)
			matrix[i] = randDistribution(dim);
		
		return matrix;
	}
	
	/**
	 * 获取数组array升序（ascend = true)/降序（ascend = false)排名列表
	 * @param array
	 * @param ascend
	 * @return rank of array in ascending or descending order
	 */
	public static int[] getRank(double[] array, boolean ascend){
		int sz = array.length;
		int[] rank = new int[sz];
		for(int i = 0; i < sz; i++)
			rank[i] = i;
		for(int i = 0; i < sz - 1; i++){
			int max = i;
			for(int j = i + 1; j < sz; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}
	
	public static int[] getRank(float[] array, boolean ascend){
		int sz = array.length;
		int[] rank = new int[sz];
		for(int i = 0; i < sz; i++)
			rank[i] = i;
		for(int i = 0; i < sz - 1; i++){
			int max = i;
			for(int j = i + 1; j < sz; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	} 
	
	/**
	 * 获取list升序（ascend = true)/降序（ascend = false)排名列表
	 * @param list
	 * @param ascend
	 * @return rank of list in ascending or descending order
	 */
	public static int[] getRank(List<Double> list, boolean ascend){
		int len = list.size();
		int[] rank = new int[len];
		for(int i = 0; i < len; i++)
			rank[i] = i;
		
		for(int i = 0; i < len - 1; i++){
			int max = i;
			for(int j = i + 1; j < len; j++){
				if(ascend){
					if(list.get(rank[max]) > list.get(rank[j]))
						max = j;
					
				}else{
					if(list.get(rank[max]) <  list.get(rank[j]))
						max = j;
				}
			}
			
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}

	public static int[] getRank(int[] array, boolean ascend){
		int len = array.length;
		int[] rank = new int[len];
		for(int i = 0; i < len; i++)
			rank[i] = i;
		
		for(int i = 0; i < len - 1; i++){
			int max = i;
			for(int j = i + 1; j < len; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}

	/**
	 * @param array
	 * @return array的均值
	 */
	public static double mean(double[] array){
		int len = array.length;
		return sum(array)/len; 
	}
	public static float mean(float[] array){
		int len = array.length;
		return sum(array)/len; 
	}
	
	/**
	 * @param array
	 * @return array的中值
	 */
	public static double median(double[] array){
		List<Double> list = new ArrayList<Double>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		
		Collections.sort(list);
		return array[len/2];
	}
	
	public static float median(float[] array){
		List<Float> list = new ArrayList<Float>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);

		Collections.sort(list);
		return array[len/2];
	}
	
	/**
	 * @param array
	 * @return array的方差
	 */
	public static double var(double[] array){
		int len = array.length;
		if(len == 1)
			return 0;
		
		double mean = mean(array), sum = 0;
		
		for(int i = 0; i < len; i++)
			sum += (array[i] - mean) * (array[i] - mean);

		return sum/(len - 1);  
	}
	
	/**
	 * @param array
	 * @return array的标准差
	 */
	public static double stdVar(double[] array){
		return Math.sqrt(var(array));
	}

	/**
	 * 建立字母与数字之间的映射关系
	 * <p>使用26个英文字母编码正整数,类似于26进制(无零元)：<br/>
	 * a-z: 1-26<br/>
	 * aa-az: 27-52<br/>
	 * ba-bz: 53-78<br/>
	 * ......<br/>
	 * aaa-zzz: 703-18278<br/>
	 * 
	 * 比如：微软Excel表列名称使用的就是这种编码方式
	 * </p>
	 * 
	 * 将索引数值转换为字母串
	 * @param num
	 * @return alpha represents the num
	 */
	public static String num2alpha(int num){
		if(num < 1)
			System.err.println("Idx is less than 1!");
		
		List<Integer> remainderList = new ArrayList<Integer>();

		int remainder = 0;
		//取余定元
		while((remainder = num % 26) != num){
			num = (num - remainder)/26;
			remainderList.add(remainder);
		}
		remainderList.add(remainder);

		int product = 1;//判断零元存在否
		for(int i = 0; i < remainderList.size(); i++)
			product *= remainderList.get(i);

		if(product == 0)
			//借位替换头部零元
			while(remainderList.get(remainderList.size() - 1) > 0){
				for(int j = remainderList.size() - 1; j >= 0; j--){
					if(remainderList.get(j) == 0){
						remainderList.set(j, 26);
						remainder = remainderList.get(j + 1);
						remainderList.set(j + 1, remainder - 1);
					}
				}
			}
		
		//转换成字母
		String alpha = "";
		for(int i = remainderList.size() - 1; i >= 0; i--){
			remainder = remainderList.get(i);
			if(remainder == 0)//剔除尾部零元
				continue;
			
			alpha += (char)(remainder + 64);
		}
		return alpha;
	}

	/**
	 * 将字母串转换为索引数值
	 * @param alpha
	 * @return num represented by alpha
	 */
	public static int alpha2num(String alpha){
		alpha = alpha.toUpperCase();
		int idx = 0, len = alpha.length();
		for(int i = 0; i < len; i++)
			idx += (int)(alpha.codePointAt(i) - 64) * Math.pow(26, len - i - 1);
		return idx;
	}
	
	/**
	 * <p><b>BitCompute执行位计算,并以位的形式显示:</b></p>
	 * <p>首先介绍几种基本的位运算, HashCode, BitSet和MessageDigest</p>
	 * <p>对于32位(4byte)的Integer数字,最高位若是1,则表示负数,比如1,其二进制编码：<br/>
	 * 00000000 00000000 00000000 00000001<br/>
	 * 若对其左移31位(1 << 31),即<br/>
	 * 10000000 00000000 00000000 00000000,表示负数,而如果左移30位,有<br/>
	 * 01000000 00000000 00000000 00000000,表示整数2^30<br/>
	 * 我们知道最大的整型数是2^31-1=Integer.MAX_VALUE=2,147,483,647,二进制形式为：<br/>
	 * 01111111 11111111 11111111 11111111<br/>
	 * 在此基础上+1:<br/>
	 *   01111111 11111111 11111111 11111111 ( 2^31-1 )<br/>
	 * + 00000000 00000000 00000000 00000001 ( 1 )<br/>
	 * = 10000000 00000000 00000000 00000000 ( 1 << 31 )</p>
	 * 
	 * <p>如果将所有的整型数字连成环状,从0开始顺时针依次为1,2,...,则Integer.MAX_VALUE与<br/>
	 * Integer.MIN_VALUE相邻,从而会出现看似矛盾的计算结果: 2,147,483,647 + 1 = <br/>
	 * -2,147,483,648, 2,147,483,647 + 2 = -2,147,483,647<br/>
	 * 由于任何类型的数字在计算机中都有一个表示范围,如果超出这个范围就会出现异常,<br/>
	 * 但若通过强制转换到能够表示更大范围的类型时就可以解决异常问题,比如:<br/>
	 * (long) Integer.MAX_VALUE + 1 = 2,147,483,648</p>
	 * 
	 * @param n
	 * @return 二进制字串
	 */
	public static String getBinary(int n){
		String bin = Integer.toBinaryString(n);
		int len = bin.length();
		int missed = 0;
		if((missed = Integer.SIZE - len) > 0){
			for(int i = 0; i < missed; i++)
				bin = "0" + bin;
		}

		return bin;
	}

	/**
	 * @param n
	 * @return 二进制字串
	 */
	public static String getBinary(long n){
		String bin = Long.toBinaryString(n);
		int len = bin.length();
		int missed = 0;
		if((missed = Long.SIZE - len) > 0){
			for(int i = 0; i < missed; i++)
				bin = "0" + bin;
		}
		return bin;
	}
	
	/**
	 * 将二进制转化为十六进制
	 * <p>每四位一组换算到到十六进制，比如<br/>
	 * bin: 1001 0010 1100<br/>
	 * Dec:  9    2    12<br/>
	 * Hex:  9    2    c
	 * </p>
	 * @param bin
	 * @return hex of bin
	 */
	public static String bin2hex(String bin){
		// i -> dec2hex[i]
		char[] dec2hex = {'0', '1', '2', '3', '4', 
				'5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};

		int len = bin.length(), mod = len % 4;
		StringBuffer hashcode = new StringBuffer();
		
		String subbin = "";		
		if(mod > 0){
			subbin = bin.substring(0, mod);
			hashcode.append(dec2hex[bin2dec(subbin)]);
			if(mod == len)
				return hashcode.toString();
			
			bin = bin.substring(mod);
		}

		for(int i = 0; i < bin.length()/4; i++){
			subbin = bin.substring(4 * i, 4 * i + 4);
			hashcode.append(dec2hex[bin2dec(subbin)]);
		}
		return hashcode.toString();
	}
	
	/**
	 * 从二进制转化为十进制
	 * @param bin
	 * @return dec of bin
	 */
	public static int bin2dec(String bin){
		int len = bin.length();
		int dec = 0;
		for(int i = 0; i < len; i++)
			dec += (bin.charAt(i) - 48) * Math.pow(2, len - i - 1);
		return dec;
	}
}
