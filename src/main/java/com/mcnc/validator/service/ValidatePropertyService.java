package com.mcnc.validator.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcnc.validator.model.MData;
import com.mcnc.validator.model.Property;
import com.mcnc.validator.model.TrCode;
import com.mcnc.validator.model.ValidationRule;
import com.mcnc.validator.utils.DynamicEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ValidatePropertyService {

	private Validator validator;

	public void validateProperty(MData inputData) {

		// Initialize validator (typically injected in Spring)
		try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
			factoryBean.afterPropertiesSet();
			validator = factoryBean.getValidator();
		}

		// Mock metadata from the database
		List<Property> properties = List.of(
			new Property("TRN10100521", "withdrawalAccountNo", "", "string", "request", true, List.of(
				new ValidationRule("notnull", "", "Withdrawal account number cannot be null."),
				new ValidationRule("notempty", "", "Withdrawal account number must not be empty.")
			), null),
			new Property("TRN10100521", "transactionCurrencyCode", "", "string", "request", true, List.of(
				new ValidationRule("notnull", "", "Transaction currency code cannot be null."),
				new ValidationRule("notempty", "", "Transaction currency code cannot be empty."),
				new ValidationRule("pattern", "^[USD|KHR]{3}$", "Transaction currency code must be USD or KHR.")
			), null),
			new Property("TRN10100521", "transferList", "", "list", "request", true, List.of(
				new ValidationRule("valid", "", "Transfer list is invalid."),
				new ValidationRule("size", "2,10", "Transfer list must have between 2 and 10 items.")
			), List.of(
				new Property("TRN10100521", "receiverAccountNo", "transferList", "string", "request", true, List.of(
					new ValidationRule("notnull", "", "Recipient account number cannot be null."),
					new ValidationRule("notempty", "", "Recipient account number must not be empty.")
				), null),
				new Property("TRN10100521", "amount", "transferList", "bigdecimal", "request", true, List.of(
					new ValidationRule("notnull", "", "Transaction amount cannot be null."),
					new ValidationRule("decimalmin", "0.01", "Transaction amount must be greater than or equal to 0.01")
				), null)
 			)),
			new Property("TRN10100521", "verifyQRCode", "", "object", "request", true, List.of(
				new ValidationRule("notnull", "", "Verify code cannot be null."),
				new ValidationRule("valid", "", "Verify code is invalid.")
			), List.of(
				new Property("TRN10100521", "verifyCode", "verifyCode", "string", "request", true, List.of(
					new ValidationRule("notnull", "", "OTP code cannot be null."),
					new ValidationRule("notempty", "", "OTP code must not be empty."),
					new ValidationRule("size", "6,6", "OTP code must be 6 digits.")
				), null)
			))
		);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		TrCode trCode = mapper.convertValue(inputData.getMData("header"), TrCode.class);

		// Generate the dynamic class
		Class<?> dynamicClass = DynamicEntity.generate(DynamicEntity.capitalize(trCode.getApiName() + trCode.getUpdatedDate() + trCode.getUpdatedTime()), trCode.getProperties());

		try {
			
			// Convert the input data to the dynamic class
			ObjectMapper dynamicMapper = new ObjectMapper();
			Object instance = dynamicMapper.convertValue(inputData.getMData("body"), dynamicClass);

			// Validate the dynamic instance
			this.validateDynamicInstance(instance);
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
		}

	}

	private void validateDynamicInstance(Object instance) {
		Set<ConstraintViolation<Object>> violations = validator.validate(instance);

		if (!violations.isEmpty()) {
			log.info(UUID.randomUUID().toString());
		}

		// Handle validation errors
		for (ConstraintViolation<Object> violation : violations) {
			log.info(violation.getMessage() + " (" + violation.getPropertyPath() + ")");
		}

	}

}
