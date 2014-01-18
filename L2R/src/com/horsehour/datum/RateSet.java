package com.horsehour.datum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.horsehour.math.MathLib;


/**
 * RateSet是推荐系统中使用的数据集，每一行是个三元组(userId,itemId,rate)
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130409
 */
public class RateSet {
	public Map<Integer, List<Preference>> rateData = null;
	public Map<Integer, List<Integer>> map = null;

	public RateSet(){
		rateData = new HashMap<Integer, List<Preference>>();
		map = new HashMap<Integer, List<Integer>>();
	}

	/**
	 * 添加preference
	 * @param usrId
	 * @param preference
	 */
	public void addPreference(int usrId, Preference pref) {
		List<Preference> prefList = null;
		
		if(rateData.containsKey(usrId))
			prefList = rateData.get(usrId);
		else
			prefList = new ArrayList<Preference>();

		prefList.add(pref);
		rateData.put(usrId, prefList);
	}
	
	/**
	 * @param usrId
	 * @return 根据用户的id取得其preference列表
	 */
	public List<Preference> getPrefList(int usrId){
		List<Preference> prefList = null;
		if(rateData.containsKey(usrId))
			prefList = rateData.get(usrId);
		return prefList;
	}
	
	/**
	 * @param usrId
	 * @return 获取用户的评分列表
	 */
	public float[] getRateList(int usrId){
		List<Preference> prefList = getPrefList(usrId);
		float[] rates = new float[prefList.size()];
		for(int i = 0; i < rates.length; i++)
			rates[i] = prefList.get(i).rate;
		return rates; 
	}

	/**
	 * @param userId
	 * @return 根据用户的历史评分，计算平均评分
	 */
	public float getAverageRateByUser(int userId){
		float[] rates = getRateList(userId);
		return MathLib.sum(rates)/rates.length;
	}
	
	/**
	 * @param itemId
	 * @return 根据物品的历史评分，计算平均评分
	 */
	public float getAverageRateOfItem(int itemId){
		List<Integer> userList = map.get(itemId);
		float[] rates = new float[userList.size()];
		List<Preference> prefList = null;
		for(int i = 0; i < rates.length; i++){
			prefList = rateData.get(userList.get(i));
			rates[i] = searchRate(itemId, prefList); 				
		}
		return MathLib.sum(rates)/rates.length;
	}
	
	/**
	 * @param itemId
	 * @param prefList
	 * @return 从prefList找到itemId对应的rate
	 */
	private float searchRate(int itemId, List<Preference> prefList){
		Preference pref = null;
		float rate = 0;
		for(int i = 0; i < prefList.size(); i++){
			pref = prefList.get(i);
			if(itemId == pref.itemId)
				rate = pref.rate;
		}
		return rate;
	}
	/**
	 * @return 取得rate的数目
	 */
	public int getNRates(){
		int nRates = 0;
		Set<Integer> userset = rateData.keySet();
		for(int usrId : userset)
			nRates += getNRates(usrId);
		
		return nRates;
	}
	
	/**
	 * @param usrId
	 * @return 取得指定用户的评价数目
	 */
	public int getNRates(int usrId){
		if(rateData.containsKey(usrId))
			return rateData.get(usrId).size();
		else
			return 0;
	}
	/**
	 * @return 取得参与评分的用户数目
	 */
	public int getNUsers(){
		return rateData.size();
	}
}