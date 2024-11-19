package com.mcnc.validator.enums;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;

public enum RuleType {

	SIZE("size", Size.class),
	MIN("min", Min.class),
	MAX("max", Max.class),
	PATTERN("pattern", Pattern.class),
	NOTEMPTY("notempty", NotEmpty.class),
	NOTNULL("notnull", NotNull.class);

	String value;
	Class<?> type;

	RuleType(String value, Class<?> type) {
		this.value = value;
		this.type = type;
	}

	public static RuleType ruleTypeOf(String value) {
		for (RuleType ruleType : RuleType.values()) {
			if (ruleType.getValue().equalsIgnoreCase(value)) { // Custom matching logic (case-insensitive in this case)
				return ruleType;
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
