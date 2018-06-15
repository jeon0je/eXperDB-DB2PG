package com.k4m.experdb.db2pg.convert.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.k4m.experdb.db2pg.common.LogUtils;
import com.k4m.experdb.db2pg.convert.vo.ConvertVO;

public class MySqlConvertMapper extends ConvertMapper<MySqlConvertMapper> {
	
	protected MySqlConvertMapper()  {
		try {
			init();
		} catch (FileNotFoundException e) {
			LogUtils.error("convert_map.json not found", MySqlConvertMapper.class, e);
		} catch (IOException e) {
			LogUtils.error("io error", MySqlConvertMapper.class, e);
		} catch (ParseException e) {
			LogUtils.error("json parse error", MySqlConvertMapper.class, e);
		}
	}
	
	@Override
	protected void init() throws FileNotFoundException, IOException, ParseException {
		JSONParser jsonParser = new JSONParser();
		
		JSONObject convMapObj = (JSONObject)jsonParser.parse(new InputStreamReader(MySqlConvertMapper.class.getResourceAsStream("/convert_map.json")));
		
		convertPatternValues = new ArrayList<ConvertVO>(30);
		convertDefaultValues = new ArrayList<ConvertVO>(5);
		for(Object key : convMapObj.keySet().toArray()) {
			JSONObject jobj = (JSONObject)convMapObj.get(key);
			String toValue = (String)jobj.get("postgres");
			JSONArray asValues = (JSONArray) jobj.get("mysql");
			if(toValue != null && asValues != null) {
				for (Object asValue : asValues) {
					if(asValue instanceof String) {
						ConvertVO convVal = new ConvertVO((String)asValue,toValue);
						if(convVal.getPattern() != null) convertPatternValues.add(convVal);
						else convertDefaultValues.add(convVal);
					}
				}
			}
		}
	}
	@Override
	public List<ConvertVO> getDefaultList() {
		return convertDefaultValues;
	}

	@Override
	public List<ConvertVO> getPatternList() {
		return convertPatternValues;
	}

	@Override
	public MySqlConvertMapper getMapper() {
		return this;
	}
	
}