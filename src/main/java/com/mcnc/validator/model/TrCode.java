package com.mcnc.validator.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrCode {
	private String apiName;
	private String updatedDate;
	private String updatedTime;
	private List<Property> properties;
}
