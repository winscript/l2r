package com.horsehour.test;

import com.horsehour.ranker.weak.WeakGenerator;

/**
 * Test code for weak generation
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130625
 */
public class Test_WeakGenerator {
	public static void main(String[] args){
		WeakGenerator wg = new WeakGenerator();
		String workspace = "";
		String type = "";
		wg.solve(workspace, type);
	}
}
