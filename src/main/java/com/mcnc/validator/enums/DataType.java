package com.mcnc.validator.enums;

import java.math.BigDecimal;
import java.util.List;

public enum DataType {

	INTEGER("integer", Integer.class),
	BIGDECIMAL("bigdecimal", BigDecimal.class),
	LONG("long", Long.class),
	BOOLEAN("boolean", Boolean.class),
	STRING("string", String.class),
	LIST("list", List.class),
	OBJECT("object", Object.class);

	String value;
	Class<?> type;

	DataType(String value, Class<?> type) {
		this.value = value;
		this.type = type;
	}

	public static DataType dataTypeOf(String value) {
		for (DataType dataType : DataType.values()) {
			if (dataType.getValue().equalsIgnoreCase(value)) {  // Custom matching logic (case-insensitive in this case)
				return dataType;
			}
		}
		throw new IllegalArgumentException("No enum constant with value " + value);
	}

	public String getValue() {
		return value;
	}

	public Class<?> getType() {
		return type;
	}

}
