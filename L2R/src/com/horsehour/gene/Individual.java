package com.horsehour.gene;

import java.util.BitSet;
/**
 * Individual即个体,是遗传算法中的基本单位
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012-12-18
 */
public class Individual {
	private BitSet genes = null;
	private int geneLen = 0;
	
	private double lowerBound = 0;
	private int nDigit = 0;
	
	public Individual(int geneLen){
		this.genes = new BitSet(geneLen);
	}

	/**
	 * 将十进制数转化为二进制数串
	 * @param dec
	 * @return such as 01000100
	 */
	public void encode(double dec){
		//根据与lowerBound相差的倍数,确定二进制数串
		int diff = (int)((dec - lowerBound) * Math.pow(10, nDigit));
		String bin = Integer.toBinaryString(diff);
		
		genes.clear();
		
		for(int idx = 0; idx < bin.length(); idx++){
			if('1' == bin.charAt(idx))
				genes.set(idx);
		}
	}
	
	/**
	 * 将二进制转化为十进制
	 * @return
	 */
	public double decode(){
		double dec = 0;
		for(int i = 0; i < genes.length(); i++){
			if(genes.get(i))
				dec+=Math.pow(2, geneLen-1-i);
		}
		return dec/Math.pow(10, nDigit)+lowerBound;
	}
	
	/**
	 * 与其他个体交换指定范围的基因序列
	 * @param individual
	 * @param startId
	 * @param endId
	 */
	public void exchangeRange(Individual individual, int startId, int endId){
		if(endId < startId)
			return;

		for(int idx = startId; idx <= endId; idx++){
			if(genes.get(idx)^individual.genes.get(idx))
				if(individual.genes.get(idx)){
					genes.set(idx);
					individual.genes.clear(idx);
				}else{
					genes.clear(idx);
					individual.genes.set(idx);
				}
		}
	}
	
	/**
	 * 确定指定位置上的二进制符号0/1
	 * @param idx
	 * @return binary digit
	 */
	public int getGeneAt(int idx){
		if(genes.get(idx))
			return 1;

		return 0;
	}
}
