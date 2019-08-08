package com.zzd.model.utils;

import java.util.HashMap;
import java.util.Map;

public class CommonConfigure {
	/**
	 * map of parameters
	 */
	public static Map<String, Object> configure=new HashMap<String, Object>();
	/**
	 * number of nodes in the equivalent tree causality
	 */
	public static int caseNum=0;
	/**
	 * flag index of VNP (variable need to be predicted)
	 */
	public static int mainFlag=0;
	/**
	 * Configure model parameters
	 * @param key : parameter name
	 * @param value : parameter value
	 */
	public static void configurePara(String key, Object value){
		configure.put(key, value);
	}
	
	public static Object getPara(String key){
		return configure.get(key);
	}
}
