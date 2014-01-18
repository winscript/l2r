package com.horsehour.util;

/**
 * 定义泛型类对,基于key排序
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131125
 */
public class Pair<K extends Comparable<? super K>, V> implements Comparable<Pair<K, V>>{
	private K key;
	private V value;

	public Pair(K k, V v)
	{
		key = k;
		value = v;
	}

	public K getKey(){
		return key;
	}
	
	public V getValue()
	{
		return value;
	}

	@Override
	public int compareTo(Pair<K, V> pair) {
		return this.key.compareTo(pair.key);
	}
	
	public String toString(){
		return "(" + key.toString() + "," + value.toString() + ")"; 
	}
}
