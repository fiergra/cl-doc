package com.ceres.dynamicforms.client;

public interface IGetter<K, V> {
	K get(V instance);
}
