package com.mcnc.validator.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mcnc.validator.model.MData;

@Mapper
public interface ApiSpecsMapper {
	MData getApiSpecsByTrCode(MData inputData);
	List<MData> getApiField(MData inputData);
	List<MData> getValidationField(MData inputData);
}