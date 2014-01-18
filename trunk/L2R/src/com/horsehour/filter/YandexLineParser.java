package com.horsehour.filter;

import java.util.Map;

import com.horsehour.datum.Sample;
/**
 * YandexLineParser解析如下格式的数据
 * <p>label 1:0.2 5:0.1 ... 145:0.23 # qid</p>
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131101
 */
public class YandexLineParser implements LineParserFilter{

	@Override
	public Map<Float, Sample> parse(String line) {
		
		return null;
	}

	@Override
	public String name() {
		return "YandexLineParser";
	}
}
