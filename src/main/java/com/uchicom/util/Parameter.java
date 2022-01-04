// (c) 2014 uchicom
package com.uchicom.util;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter
 * 入力チェックを各サーバプで用意する。
 *
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Parameter {

    private Map<String, String> parameterMap = new HashMap<>();
    public Parameter(String[] args) {
        setParameters(args);
    }
    private void setParameters(String[] args) {
    	for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-") && i < args.length - 1) {
				parameterMap.put(args[i].substring(1), args[++i]);
			} else {
				parameterMap.put(args[i], args[i]);
			}
		}
    }
    public void put(String key, String value) {
    	parameterMap.put(key, value);
    }
	public String get(String key) {
		return parameterMap.get(key);
	}
	public File getFile(String key) {
		return new File(parameterMap.get(key));
	}
	public Integer getInteger(String key) {
		String value = parameterMap.get(key);
		if (value == null) {
			return null;
		} else {
			return Integer.valueOf(value);
		}
	}
	public int getInt(String key) {
		return Integer.parseInt(parameterMap.get(key));
	}
	public boolean is(String key) {
		return parameterMap.containsKey(key);
	}
	public LocalDate getLocalDate(String key) {
		if (!parameterMap.containsKey(key)) {
			return null;
		}
		return LocalDate.parse(parameterMap.get(key));
	}
}
