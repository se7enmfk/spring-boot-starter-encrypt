/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.common.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ftx.frame.util.date.DateUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * model日期转换json日期  
 * @author se7en
 * @date   2016-10-15
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date date, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		gen.writeString(DateUtil.dateToString(date, SystemConfig.DATE_FORMAT));
	}

}