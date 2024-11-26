package com.mcnc.validator.utils;

public class DynamicClassLoader extends ClassLoader {

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class<?> defineDynamicClass(String name, byte[] b, int off, int len) {
		return defineClass(name, b, off, len);
	}

}
