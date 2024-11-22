package com.mcnc.validator.enums;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public enum RuleType {

	VALID("valid", Valid.class),
	NOTNULL("notnull", NotNull.class),
	NOTEMPTY("notempty", NotEmpty.class),
	SIZE("size", Size.class),
	MIN("min", Min.class),
	MAX("max", Max.class),
	DECIMALMIN("decimalmin", DecimalMin.class),
	DECIMALMAX("decimalmax", DecimalMax.class),
	PATTERN("pattern", Pattern.class);

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
