package com.horsehour.filter;

/**
 * 定义了解析行数据的插件
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130327
 */
public interface LineParserFilter {
	public Object parse(String line);
	public String name();
}