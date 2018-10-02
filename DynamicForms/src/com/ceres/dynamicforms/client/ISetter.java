package com.ceres.dynamicforms.client;

public interface ISetter<K, V> {
	void set(V instance, K value);
}
