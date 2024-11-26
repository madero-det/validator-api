package com.mcnc.validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidationRule {
	private String ruleType;
	private String ruleValue;
	private String errorMessage;
}
