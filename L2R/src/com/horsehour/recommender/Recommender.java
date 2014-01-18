package com.horsehour.recommender;

import java.util.List;
import java.util.Map;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.RateSet;
import com.horsehour.math.MathLib;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;

/**
 * Recommender推荐模型
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130410
 */
public class Recommender {
	public RateSet trainset, valiset;
	public Map<Integer, List<Integer>> predicted;
	public float alpha = 0.2f;
	
	public Recommender(String train, String predict, String enc){
		trainset = DataManager.loadRateSet(train, enc);
		predicted = DataManager.loadUserItemMap(predict,enc); 
	}

	/**
	 * 计算用户的评价评分和物品的平均评分
	 * @param pairs
	 * @param guessFile
	 */
	public void predict(String guessFile){
		StringBuffer sb = new StringBuffer();
		float scoreUser, scoreItem, score;
		List<Integer> itemList = null;
		for(int usrId : predicted.keySet()){
			scoreUser = trainset.getAverageRateByUser(usrId);
			itemList = predicted.get(usrId);
			int itemId;
			for(int i = 0; i < itemList.size(); i++){
				itemId = itemList.get(i);
				scoreItem = trainset.getAverageRateOfItem(itemId);
//				sb.append(usrId + "\t" + itemId + "\t" + scoreUser + "\t" + scoreItem + "\r\n");
				
				score = alpha * scoreUser + (1 - alpha) * scoreItem;
				score = MathLib.round(score, 4);
				sb.append(usrId + "\t" + itemId + "\t" + score + "\r\n");
			}
			FileManager.writeFile(guessFile, sb.toString());
			sb = new StringBuffer();
		}
	}

	public static void main(String[] args){
		TickClock.beginTick();

		String train = "F:/Research/Data/MovieRecSys/training_set.txt";
		String predict = "F:/Research/Data/MovieRecSys/predict.txt";
		String gusFile = "F:/Research/Data/MovieRecSys/gus.txt";
		String enc = "utf-8";

		Recommender rec = new Recommender(train, predict, enc);
		rec.predict(gusFile);
		
		TickClock.stopTick();
	}
}
