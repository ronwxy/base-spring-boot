package cn.jboost.springboot.common;


import java.util.HashMap;
import java.util.Map;

/**
 * chain invoke to build a map;
 * @param <K>
 * @param <V>
 */
public class MapBuilder<K, V> {

	private final Map<K, V> _internal;


	public MapBuilder(Map<K, V> map) {
		_internal = map;
	}

	public MapBuilder() {
		_internal = new HashMap<>();
	}


	public MapBuilder(K key, V value) {
		this();
		_internal.put(key, value);
	}

	public MapBuilder<K, V> append(K key, V value) {
		_internal.put(key, value);
		return this;
	}


	public Map<K, V> build() {
		return _internal;
	}


}
