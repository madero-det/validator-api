package com.mcnc.validator.service;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcnc.validator.model.MData;
import com.mcnc.validator.model.TrCode;
import com.mcnc.validator.utils.DynamicEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ValidatePropertyService {

	private Validator validator;
	private final ApiSpecsService apiSpecsService;

	public String validateProperty(MData inputData) {

		// Initialize validator (typically injected in Spring)
		try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
			factoryBean.afterPropertiesSet();
			validator = factoryBean.getValidator();
		}

		// Get API specs by trCode from the database
		MData apiSpecsByTrCode = apiSpecsService.getApiSpecsByTrCode(inputData.getMData("header"));
 
		// Convert the API specs to a TrCode object
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		TrCode trCode = mapper.convertValue(apiSpecsByTrCode, TrCode.class);

		// Generate a dynamic class based on the API specs
		Class<?> dynamicClass = DynamicEntity.generate(DynamicEntity.capitalize(trCode.getApiName() + trCode.getUpdatedDate() + trCode.getUpdatedTime() + trCode.getMessageType()), trCode.getProperties());

		try {
			
			// Convert the input data to the dynamic class
			ObjectMapper dynamicMapper = new ObjectMapper();
			Object instance = dynamicMapper.convertValue(inputData.getMData("body"), dynamicClass);

			// Validate the dynamic instance
			return this.validateDynamicInstance(instance);
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());

			String regex = "Unrecognized field\\s+\"[^\"]+\"";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(e.getMessage());

			return matcher.find() ? matcher.group() : "Invalid input data";
		}

	}

	private String validateDynamicInstance(Object instance) {
		Set<ConstraintViolation<Object>> violations = validator.validate(instance);
		String logId = null;
		if (!violations.isEmpty()) {
			logId = UUID.randomUUID().toString();
		}

		// Handle validation errors
		for (ConstraintViolation<Object> violation : violations) {
			log.info(logId + " - " + violation.getMessage() + " (" + violation.getPropertyPath() + ")");
		}

		return logId;
	}

}
