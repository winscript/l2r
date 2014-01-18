package com.horsehour.datum;

/**
 * Preference记录物品评价信息二元组（itemId,rate）
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130409
 */
public class Preference implements Comparable<Preference>{
	public int itemId = -1;
	public float rate = 0;
	
	public Preference(int itemId, float rate){
		this.itemId = itemId;
		this.rate = rate;
	}

	@Override
	public int compareTo(Preference target){
		if(this.itemId > target.itemId)
			return 1;
		else if(this.itemId < target.itemId)
			return -1;
		else
			return 0;
	}
}
