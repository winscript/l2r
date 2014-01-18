package com.horsehour.filter;

import com.horsehour.datum.Sample;

/**
 * 解析的数据格式：
 * <p>0.1,0.9,0.1,...,0.3,1</p>
 * 最后一列是类标
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131102
 */
public class CVSLineParser implements LineParserFilter{

	@Override
	public Sample parse(String line) {
		String[] subs = line.split(",");
		int len = subs.length;
		double[] feature = new double[len - 1];
		for(int i = 0; i < len - 1; i++)
			feature[i] = Double.parseDouble(subs[i]);
		int label = Integer.parseInt(subs[len-1]);

		return new Sample(feature, label);
	}

	@Override
	public String name() {
		return "CVSLineParser";
	}
}
