package com.mcnc.validator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcnc.validator.model.MData;
import com.mcnc.validator.service.ValidatePropertyService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ValidatorApiSpecsController {

	private final ValidatePropertyService validatePropertyService;

	@PostMapping("/retrieves")
	public MData retrieveListApiSpecs(@RequestBody MData inputData) {
		String logId = validatePropertyService.validateProperty(inputData);
		return logId == null ? new MData() : new MData().setString("logId", logId);
	}

}
