/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.common.component;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapperDependencys {
	/** 每一个statementId 更新依赖的statementId集合 */
	public static Map<String, Set<String>> OBSERVERS;

	@PostConstruct
	public void init() {
		OBSERVERS = new ConcurrentHashMap<String, Set<String>>();
		InputStream inputStream;
		try {
			inputStream = Resources.getResourceAsStream("config/dependencys.xml");
			XPathParser parser = new XPathParser(inputStream);
			List<XNode> statements = parser.evalNodes("/dependencies/statements/statement");
			for (XNode node : statements) {
				Set<String> temp = new HashSet<String>();
				List<XNode> obs = node.evalNodes("observer");
				for (XNode observer : obs) {
					temp.add(observer.getStringAttribute("id"));
				}
				OBSERVERS.put(node.getStringAttribute("id"), temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
