package com.kongque.util;

import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {

	private Properties props;

	public PropertiesUtils(String fileName) {
		props = new Properties();
		InputStreamReader inputStream;
		try {
			inputStream = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName),
					"UTF-8");
			props.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getString(String key) {
		return props.getProperty(key);
	}

	public int getInt(String key) {
		return Integer.parseInt(this.getString(key));
	}

	public long getLong(String key) {
		return Long.parseLong(this.getString(key));
	}
}
