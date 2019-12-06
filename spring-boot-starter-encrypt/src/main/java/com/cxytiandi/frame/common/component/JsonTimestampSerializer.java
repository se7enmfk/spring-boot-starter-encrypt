/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.common.component;

import com.cxytiandi.frame.util.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * model日期时间转换json日期时间
 * @author se7en
 * @date   2016-10-15
 */
@Component
public class JsonTimestampSerializer extends JsonSerializer<Timestamp> {

	@Override
	public void serialize(Timestamp timestamp, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		gen.writeString(DateUtil.timestampToString(timestamp,
				SystemConfig.DATETIME_FORMAT));
	}

}
