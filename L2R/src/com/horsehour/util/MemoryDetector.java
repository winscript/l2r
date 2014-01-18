package com.horsehour.util;

public class MemoryDetector {
	public static void showMemoryUsed(String flag){
	    Runtime rt = Runtime.getRuntime();
	    long free = rt.freeMemory();
	    long total = rt.totalMemory();
	    long used = total - free;
	    System.out.println(flag + ": " + total + "\t" + free + "\t" + used);
	}
}