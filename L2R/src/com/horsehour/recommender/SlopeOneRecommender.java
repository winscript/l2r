package com.horsehour.recommender;

import java.util.*;

/**
 * Daniel Lemire
 * A simple implementation of the weighted slope one algorithm for item-based collaborative filtering. 
 * Revised by Marco Ponzi on March 29th 2007
 */

public class SlopeOneRecommender {
	public Map<Integer,Map<Integer,Float>> mData;
	
	public Map<Integer,Map<Integer,Float>> mDiffMatrix;
	public Map<Integer,Map<Integer,Integer>> mFreqMatrix;

	public static Integer[] mAllItems;

	public SlopeOneRecommender(Map<Integer,Map<Integer,Float>> data) {
		mData = data;
		buildDiffMatrix();    
	}

	/**
	 * Based on existing data, and using weights,
	 * try to predict all missing ratings.
	 * The trick to make this more scalable is to consider
	 * only mDiffMatrix entries having a large  (>1) mFreqMatrix
	 * entry.
	 *
	 * It will output the prediction 0 when no prediction is possible.
	 */
	public Map<Integer,Float> predict(Map<Integer,Float> user) {
		HashMap<Integer,Float> predictions = new HashMap<Integer,Float>();
		HashMap<Integer,Integer> frequencies = new HashMap<Integer,Integer>();
		for (Integer j : mDiffMatrix.keySet()) {
			frequencies.put(j,0);
			predictions.put(j,0.0f);
		}
		for (Integer j : user.keySet()) {
			for (Integer k : mDiffMatrix.keySet()) {
				try {
					float newval = ( mDiffMatrix.get(k).get(j).floatValue() + user.get(j).floatValue() ) * mFreqMatrix.get(k).get(j).intValue();
					predictions.put(k, predictions.get(k)+newval);
					frequencies.put(k, frequencies.get(k)+mFreqMatrix.get(k).get(j).intValue());
				} catch(NullPointerException e) {}
			}
		}
		HashMap<Integer,Float> cleanpredictions = new HashMap<Integer,Float>();
		for (Integer j : predictions.keySet()) {
			if (frequencies.get(j)>0) {
				cleanpredictions.put(j, predictions.get(j).floatValue()/frequencies.get(j).intValue());
			}
		}
		for (Integer j : user.keySet()) {
			cleanpredictions.put(j,user.get(j));
		}
		return cleanpredictions;
	}

	/**
	 * Based on existing data, and not using weights,
	 * try to predict all missing ratings.
	 * The trick to make this more scalable is to consider
	 * only mDiffMatrix entries having a large  (>1) mFreqMatrix
	 * entry.
	 */
	public Map<Integer,Float> weightlesspredict(Map<Integer,Float> user) {
		HashMap<Integer,Float> predictions = new HashMap<Integer,Float>();
		HashMap<Integer,Integer> frequencies = new HashMap<Integer,Integer>();
		for (Integer j : mDiffMatrix.keySet()) {
			predictions.put(j,0.0f);
			frequencies.put(j,0);
		}
		for (Integer j : user.keySet()) {
			for (Integer k : mDiffMatrix.keySet()) {
				//System.out.println("Average diff between "+j+" and "+ k + " is "+mDiffMatrix.get(k).get(j).floatValue()+" with n = "+mFreqMatrix.get(k).get(j).floatValue());
				float newval = ( mDiffMatrix.get(k).get(j).floatValue() + user.get(j).floatValue() ) ;
				predictions.put(k, predictions.get(k)+newval);
			}
		}
		for (Integer j : predictions.keySet()) {
			predictions.put(j, predictions.get(j).floatValue()/user.size());
		}
		for (Integer j : user.keySet()) {
			predictions.put(j,user.get(j));
		}
		return predictions;
	}

	/**
	 * 构建物品成对评分差值矩阵（平均分值）
	 */
	public void buildDiffMatrix() {
		mDiffMatrix = new HashMap<Integer,Map<Integer,Float>>();
		mFreqMatrix = new HashMap<Integer,Map<Integer,Integer>>();

		for(Map<Integer,Float> preference : mData.values()) {
			for(Map.Entry<Integer,Float> entry: preference.entrySet()) {
				if(!mDiffMatrix.containsKey(entry.getKey())) {
					mDiffMatrix.put(entry.getKey(), new HashMap<Integer,Float>());
					mFreqMatrix.put(entry.getKey(), new HashMap<Integer,Integer>());
				}
				for(Map.Entry<Integer,Float> entry2: preference.entrySet()) {
					int oldcount = 0;
					if(mFreqMatrix.get(entry.getKey()).containsKey(entry2.getKey()))
						oldcount = mFreqMatrix.get(entry.getKey()).get(entry2.getKey()).intValue();
					float olddiff = 0.0f;
					if(mDiffMatrix.get(entry.getKey()).containsKey(entry2.getKey()))
						olddiff = mDiffMatrix.get(entry.getKey()).get(entry2.getKey()).floatValue();
					float observeddiff = entry.getValue() - entry2.getValue();
					mFreqMatrix.get(entry.getKey()).put(entry2.getKey(),oldcount + 1);
					mDiffMatrix.get(entry.getKey()).put(entry2.getKey(),olddiff+observeddiff);          
				}
			}
		}
		for (Integer j : mDiffMatrix.keySet()) {
			for (Integer i : mDiffMatrix.get(j).keySet()) {
				float oldvalue = mDiffMatrix.get(j).get(i).floatValue();
				int count = mFreqMatrix.get(j).get(i).intValue();
				mDiffMatrix.get(j).put(i,oldvalue/count);
			}
		}
	}
	public static void main(String args[]){
		Map<Integer,Map<Integer,Float>> data = new HashMap<Integer,Map<Integer,Float>>();
		SlopeOneRecommender so = new SlopeOneRecommender(data);
		HashMap<Integer,Float> user = new HashMap<Integer,Float>();
		so.predict(user);
	}
}