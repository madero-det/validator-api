package com.mcnc.validator.utils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcnc.validator.enums.DataType;
import com.mcnc.validator.enums.RuleType;
import com.mcnc.validator.model.Property;
import com.mcnc.validator.model.ValidationRule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

public class DynamicEntity {

	private static final Map<String, Class<?>> classesLoaded = new HashMap<>();

	private DynamicEntity() {
		throw new IllegalStateException("Utility class");
	}

	public static Class<?> generate(String className, List<Property> properties) {

		// Check if the class has already been loaded
		if (classesLoaded.containsKey(className)) {
			return classesLoaded.get(className);
		}

		// Start building the class
		Builder<?> builder = new ByteBuddy()
				.subclass(Object.class)
				.name(className);

		// Add fields and method based on the validation rules
		for (Property property : properties) {
			String fieldName = property.getFieldName();
			Class<?> dataType = DataType.dataTypeOf(property.getDataType()).getType();
			Generic fieldType = null;

			if (dataType == Object.class) {
				dataType = DynamicEntity.generate(className + "_" + DynamicEntity.capitalize(property.getFieldName()), property.getChildProperties());
				fieldType = OfNonGenericType.ForLoadedType.of(dataType);
			} else if (dataType == List.class) {
				dataType = DynamicEntity.generate(className + "_" + DynamicEntity.capitalize(property.getFieldName()), property.getChildProperties());
				fieldType = TypeDescription.Generic.Builder
					.parameterizedType(List.class, dataType)
					.build();
			} else {
				fieldType = OfNonGenericType.ForLoadedType.of(dataType);
			}

			// Add fields and methods
			builder = builder
				.defineMethod("get" + DynamicEntity.capitalize(fieldName), fieldType, Modifier.PUBLIC)
				.intercept(FieldAccessor.ofField(fieldName))
				.defineMethod("set" + DynamicEntity.capitalize(fieldName), void.class, Modifier.PUBLIC)
				.withParameters(fieldType)
				.intercept(FieldAccessor.ofField(fieldName));

			Optional<?> fieldBuilder = builder.defineField(fieldName, fieldType, Modifier.PRIVATE);

			// Add validation annotations based on the metadata
			if (Boolean.TRUE.equals(property.isRequired())) {
				builder = DynamicEntity.addAnnotationValidateField(fieldBuilder, property);
			}

		}

		// Build and load the class
		Class<?> dynamicClass = builder
				.make()
				.load(DynamicEntity.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();

		// Cache the loaded class
		classesLoaded.put(className, dynamicClass);

		// Return the generated class
		return dynamicClass;
	}

	public static String capitalize(String fieldName) {  
		if (fieldName == null || fieldName.isEmpty()) {
			return fieldName;
		}
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	private static Builder<?> addAnnotationValidateField(Optional<?> fieldBuilder, Property property) {

		for (ValidationRule rule : property.getValidationRules()) {
			fieldBuilder = DynamicEntity.applyValidationRule(fieldBuilder, rule);
		}

		return fieldBuilder;
	}

	private static Optional<?> applyValidationRule(Optional<?> fieldBuilder, ValidationRule rule) {
		switch (RuleType.ruleTypeOf(rule.getRuleType().toLowerCase())) {
			case RuleType.VALID:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(Valid.class)
						.build());
			case RuleType.NOTNULL:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(NotNull.class)
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.NOTEMPTY:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(NotEmpty.class)
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.PATTERN:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(Pattern.class)
						.define("regexp", rule.getRuleValue())
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.DECIMALMIN:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(DecimalMin.class)
						.define("value", rule.getRuleValue())
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.DECIMALMAX:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(DecimalMax.class)
						.define("value", rule.getRuleValue())
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.SIZE:
				String[] sizeRange = rule.getRuleValue().split(",");
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(Size.class)
						.define("min", Integer.parseInt(sizeRange[0].trim()))
						.define("max", Integer.parseInt(sizeRange[1].trim()))
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.MIN:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(Min.class)
						.define("min", rule.getRuleValue())
						.define("message", rule.getErrorMessage())
						.build());
			case RuleType.MAX:
				return fieldBuilder.annotateField(AnnotationDescription.Builder.ofType(Max.class)
						.define("min", rule.getRuleValue())
						.define("message", rule.getErrorMessage())
						.build());
			default:
				throw new IllegalArgumentException("Unknown validation rule: " + rule.getRuleType());
		}
	}

}