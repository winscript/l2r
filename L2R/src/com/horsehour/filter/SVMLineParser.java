package com.horsehour.filter;

import com.horsehour.datum.Sample;

/**
 * SVMLineParser解析SVM类型的分类数据,格式如下
 * <p>label	1:0.7	2:0.2	5:0.1	13:-1</p>
 * <p>label:(-1,+1,...)表示类别,此后表示特征列表,可能不完全显示,缺失的表示特征值为0,
 * 一般假设或者至少第一行的最后一个特征的id就是维数</p>
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130509
 */
public class SVMLineParser implements LineParserFilter{
	private int dim = 0;
	
	public SVMLineParser(){}

	@Override
	public Sample parse(String line) {
		String[] segments = line.trim().split(" ");
		int len = segments.length;
		String[] pair = segments[len - 1].split(":");
		dim = Integer.parseInt(pair[0]);

		int label = Integer.parseInt(segments[0].replace("+", ""));
		double[] features = new double[dim];
		for(int i = 1; i < segments.length; i++){
			pair = segments[i].split(":");
			features[Integer.parseInt(pair[0]) - 1] = Double.parseDouble(pair[1]); 
		}
		return new Sample(features, label, "");
	}
	
	@Override
	public String name() {
		return "SVMLineParser";
	}
}
