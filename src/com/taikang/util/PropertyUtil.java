/**
 * @Company：泰康人寿保险股份有限公司
 * Copyright Taikanglife.All Rights Reserved 2014
 */
package com.taikang.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @Package: com.taikanglife.map.util
 * @ClassName: PropertyUtil
 * @Description: 读配置文件
 * @author maoyl05
 * @date: 2014-6-16
 * @version V1.0
 */
public class PropertyUtil {

	private static Logger log = Logger.getLogger(PropertyUtil.class);

	private static Properties props_const;
	
	static {
		log.info("==初始化配置文件==");
		try {
			props_const = loadProps("classpath:conf.properties");
		} catch (IOException e) {
			log.error("==PropertyUtil加载资源配置信息出错==" + e);
		}
	}

	/**
	 * 初始化配置文件容器
	 * @throws IOException 
	 */
	private static Properties loadProps(String propertyFileNames) throws IOException {
		// --一次性加载多个配置文件到一个句柄中
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(propertyFileNames);
		Properties props = new Properties();
		for (Resource resource : resources) {
			PropertiesLoaderUtils.fillProperties(props, resource);
		}
		return props;
	}

	/**
	 * @Title: getConstants
	 * @Description: 读取常量配置信息
	 *
	 * @param key 常量key
	 * @return 常量值
	 */
	public static String getConstants(String key) {
		return props_const.getProperty(key);
	}
}
