package com.mcnc.validator.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mcnc.validator.enums.DataType;
import com.mcnc.validator.mapper.ApiSpecsMapper;
import com.mcnc.validator.model.MData;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApiSpecsService {

	private final ApiSpecsMapper apiSpecsMapper;

	public MData getApiSpecsByTrCode(MData inputData) {
		MData apiSpecsByTrCode = apiSpecsMapper.getApiSpecsByTrCode(inputData);
		apiSpecsByTrCode.setString("messageType", inputData.getString("messageType"));

		inputData.setString("parentFieldName", "");
		apiSpecsByTrCode.setListMData("properties", this.getApiField(inputData));
		
		return apiSpecsByTrCode;
	}

	private List<MData> getApiField(MData inputData) {
		List<MData> apiFields = apiSpecsMapper.getApiField(inputData);

		for (MData mData : apiFields) {
			Class<?> dataType = DataType.dataTypeOf(mData.getString("dataType")).getType();
			
			inputData.setString("fieldName", mData.getString("fieldName"));
			mData.setListMData("validationRules", apiSpecsMapper.getValidationField(inputData));
			
			if (dataType == Object.class || dataType == List.class) {
				inputData.setString("parentFieldName", mData.getString("fieldName"));
				mData.setListMData("childProperties", this.getApiField(inputData));
			} else {
				mData.setListMData("childProperties", List.of());
			}

		}

		return apiFields;
	}

}
