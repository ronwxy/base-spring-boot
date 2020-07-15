package cn.jboost.springboot.common.util;


import java.util.HashMap;
import java.util.Map;

/**
 * chain invoke to build a map;
 * @param <K>
 * @param <V>
 */
public class MapBuilder<K, V> {

	private final Map<K, V> map;


	public MapBuilder(Map<K, V> map) {
		this.map = map;
	}

	public MapBuilder() {
		map = new HashMap<>();
	}


	public MapBuilder(K key, V value) {
		this();
		map.put(key, value);
	}

	public MapBuilder<K, V> append(K key, V value) {
		map.put(key, value);
		return this;
	}


	public Map<K, V> build() {
		return map;
	}


}
