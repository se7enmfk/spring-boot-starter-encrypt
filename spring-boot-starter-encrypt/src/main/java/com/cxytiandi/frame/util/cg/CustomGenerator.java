/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.cg;

import com.ftx.frame.util.cg.AutoGenerator;
import com.ftx.frame.util.cg.ConfigDataSource;
import com.ftx.frame.util.cg.ConfigGenerator;
import com.ftx.frame.util.properties.PropertiesUtil;

public class CustomGenerator {
    public static void main(String[] args) {
        com.ftx.frame.util.cg.ConfigGenerator configGenerator = new ConfigGenerator();
        com.ftx.frame.util.cg.AutoGenerator autoGenerator = new AutoGenerator();

        PropertiesUtil p = PropertiesUtil.getInstance("database");
        // 配置 ORACLE 连接
        configGenerator.setDbDriverName(p.getProperty("database.jdbc.driverClassName"));
        configGenerator.setDbUrl(p.getProperty("database.jdbc.url"));
        configGenerator.setDbUser(p.getProperty("database.jdbc.username"));
        configGenerator.setDbPassword(p.getProperty("database.jdbc.password"));
        configGenerator.setDbName(p.getProperty("database.name"));
        configGenerator.setDbSchema(p.getProperty("database.schema"));
        configGenerator.setConfigDataSource(ConfigDataSource.POSTGRESQL);


		/* 此处设置 String 类型数据库ID，默认Long类型 */
        //cg.setConfigIdType(ConfigIdType.STRING);

        // 配置表主键策略
        //cg.setIdType(IdType.INPUT);

        // 配置导出路径
        //configGenerator.setSaveDir("f:/aaa");
        // configGenerator.setSaveDir("C:\\111\\");
        // configGenerator.setSaveDir("C:\\FTX\\FTX-Out-Framework\\ftx-server\\");
        configGenerator.setSaveDir("C:\\FTX\\FTX-Out-Framework\\ftx-server\\");
        /*
		 * 指定生成表名（默认，所有表）
		 */
        configGenerator.setTableNames(new String[]{"wkf_process_definition"});

        //构建者模型
        configGenerator.setBuliderModel(true);

        // 配置包名
        configGenerator.setEntityPackage("com.ftx.biz.%s.entity");// entity 实体包路径
        configGenerator.setMapperPackage("com.ftx.biz.%s.mapper");// mapper 映射文件路径
        configGenerator.setServicePackage("com.ftx.biz.%s.service");// service 层路径
        configGenerator.setControllerPackage("com.ftx.biz.%s.controller");// controller

        configGenerator.setAuthor("se7en zhou");

        // 生成代码
        autoGenerator.run(configGenerator);
    }
}
