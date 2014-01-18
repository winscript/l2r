package com.horsehour.filter;

import com.horsehour.datum.Sample;

/**
 * L2RLineParser解析如下格式的数据:
 * <p>0 qid:167 1:0.34 2:1.0 ... 45:0.20 ...</p>
 * <p>1 qid:167 1:0.71 2:0.05 ... 45:0.10 ...</p>
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130327
 */
public class L2RLineParser implements LineParserFilter{
	/**
	 * 从文本数据行中解析出label，qid和特征值
	 * @param line
	 */
	@Override
	public Sample parse(String line) {
		int idx = line.indexOf('#'); 
		idx = (idx == -1) ? line.length() : idx;
		line = line.substring(0, idx).trim();

		String[] segments = line.split(" ");

		int label = Integer.parseInt(segments[0]);
		String qid = segments[1].split(":")[1];

		int dim = segments.length - 2;
		double[] features = new double[dim];

		String val = "";
		for(int i = 2; i < segments.length; i++){
			val = segments[i].split(":")[1];
			features[i-2] = Double.parseDouble(val); 
		}
		return new Sample(features, label, qid);
	}

	@Override
	public String name() {
		return "L2RLineParser";
	}
}
