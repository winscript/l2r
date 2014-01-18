package com.horsehour.util;

/**
 * @author Chunheng Jiang
 * @version 1.0
 * @resume Helps to record time cost by process
 */
public class TickClock {
	private static long start = 0, end = 0;
	//开始计时
	public static void beginTick(){
		start = System.currentTimeMillis();
	}
	//结束计时
	public static void stopTick(){
		end = System.currentTimeMillis();
		showElapsed();
	}
	//显示耗时
	private static void showElapsed(){
		float elapsedSec = (float)(end - start)/1000;
		StringBuilder sb = new StringBuilder("[Time Elapsed: ");
		if(elapsedSec > 3600)
			sb.append(elapsedSec/3600 + " hours.]");
		else if(elapsedSec > 60)
			sb.append(elapsedSec/60 + " minutes.]");
		else
			sb.append(elapsedSec + " seconds.]");
		System.out.println(sb.toString());
	}
}
