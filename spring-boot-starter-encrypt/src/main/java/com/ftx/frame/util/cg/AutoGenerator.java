/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.cg;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ftx.frame.util.date.DateUtil;
import com.ftx.frame.util.string.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * <p>
 * 映射文件自动生成类
 * </p>
 *
 * @author hubin yanghu
 * @Date 2016-06-12
 */
@Service
public class AutoGenerator {

    private ConfigGenerator config;

    public ConfigGenerator getConfig() {
        return config;
    }

    public void setConfig(ConfigGenerator config) {
        this.config = config;
    }

    private static String PATH_CONTROLLER = null;
    private static String PATH_ENTITY = null;
    private static String PATH_MAPPER = null;
    private static String PATH_XML = null;
    private static String PATH_SERVICE = null;
    private static String PATH_SERVICE_IMPL = null;

    private static String ENTITYPACKAGE;
    private static String MAPPERPACKAGE;
    private static String XMLPACKAGE;
    private static String SERVICEPACKAGE;
    private static String SERVICEIMPLPACKAGE;
    private static String CONTROLLERPACKAGE;

    private static boolean FILE_OVERRIDE = false;

    private static final String JAVA_SUFFIX = ".java";
    private static final String XML_SUFFIX = ".xml";

    private static boolean IS_VIEW = false;


    /**
     * run 执行
     */
    @Transactional
    public void run(ConfigGenerator config) {


        IS_VIEW = false;
        if (config == null) {
            throw new MybatisPlusException(" ConfigGenerator is null. ");
        } else if (config.getIdType() == null) {
            throw new MybatisPlusException("ConfigGenerator IdType is null");
        }

        /**
         * 新生成的文件是否覆盖现有文件
         */
        FILE_OVERRIDE = config.isFileOverride();

        setConfig(config);
        /**
         * 开启生成映射关系
         */
        generate();

        /**
         * 自动打开生成文件的目录
         * <p>
         * 根据 osName 执行相应命令
         * </p>
         */
        /*try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                if (osName.contains("Mac")) {
                    Runtime.getRuntime().exec("open " + config.getSaveDir());
                } else if (osName.contains("Windows")) {
                    Runtime.getRuntime().exec("cmd /c start " + config.getSaveDir());
                } else {
                    System.err.println("save dir:" + config.getSaveDir());
                }
            }
            System.out.println(" generate success! ");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 根据包名转换成具体路径
     *
     * @param packageName
     * @return
     */
    private static String getPathFromPackageName(String packageName) {
        if (StringUtil.isEmpty(packageName)) {
            return "";
        }
        return packageName.replace(".", File.separator);
    }

    /**
     * 生成文件地址
     *
     * @param segment 文件地址片段
     * @return
     */
    private static String getFilePath(String savePath, String segment) {
        File folder = new File(savePath + File.separator + segment);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getPath();
    }

    /**
     * 生成映射文件
     */
    public void generate() {
        /**
         * 检查文件夹是否存在
         */

        config.setSaveDirJava(StringUtil.concat(config.getSaveDir(), "\\src\\main\\java"));
        config.setSaveDirXml(StringUtil.concat(config.getSaveDir(), "\\src\\main\\resources"));
        File gf1 = new File(config.getSaveDirJava());
        File gf2 = new File(config.getSaveDirXml());
        if (!gf1.exists()) {
            gf1.mkdirs();
        }
        if (!gf2.exists()) {
            gf2.mkdirs();
        }

        Connection conn = null;
        try {
            /**
             * 创建连接
             */
            Class.forName(config.getDbDriverName());
            conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());


            Map<String, String> tableComments = getTableComment(conn);
            /**
             * 根据配置获取应该生成文件的表信息
             */
            List<String> tables = getTables(conn);
            if (null == tables) {
                return;
            }
            for (String table : tables) {
                generate(conn, tableComments, table, config.getConfigDataSource(), gf1, gf2);
            }

            /**
             * 根据配置获取应该生成文件的视图信息
             */
            /*List<String> views = getViews(conn);
            if (null == views) {
                return;
            }
            for (String table : views) {
                IS_VIEW = true;
                generate(conn, tableComments, table, config.getConfigDataSource(), gf1, gf2);
            }*/


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据是否覆盖标志决定是否覆盖当前已存在的文件
     *
     * @param dirPath
     * @param beanName
     * @param suffix
     * @return
     */
    private boolean valideFile(String dirPath, String beanName, String suffix) {
        File file = new File(dirPath, beanName + suffix);
        return !file.exists() || FILE_OVERRIDE;
    }

    /**
     * 检查配置的所有表是否在dbTables里存在.
     * <ul>
     * <li>如果未配置，则返回所有表</li>
     * <li>所有都存在直接返回custTables.</li>
     * <li>如果发现有不存在表名直接返回null。</li>
     * </ul>
     *
     * @return
     * @throws SQLException
     */
    private List<String> getTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();
        System.out.println(String.format(config.getConfigDataSource().getTablesSql(), config.getDbName(), config.getDbSchema()));
        PreparedStatement pstate = conn.prepareStatement(String.format(config.getConfigDataSource().getTablesSql(), config.getDbName(), config.getDbSchema()));
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            tables.add(results.getString(1).toUpperCase());
        }

        String[] tableNames = config.getTableNames();
        if (null == tableNames || tableNames.length == 0) {
            return tables;
        }

        // 循环判断是否配置的所有表都在当前库中存在
        List<String> custTables = Arrays.asList(tableNames);
        List<String> notExistTables = new ArrayList<String>();
        for (String tb : custTables) {
            if (!tables.contains(tb.toUpperCase())) {
                notExistTables.add(tb);
            }
        }
        if (notExistTables.size() == 0) {
            return custTables;
        }
        // 如果有错误的表名，打印到控制台，且返回null
        System.err.println("tablename " + notExistTables.toString() + " is not exist!! ==> stop generate!!");
        return null;
    }

    /**
     * 检查配置的所有表是否在dbTables里存在.
     * <ul>
     * <li>如果未配置，则返回所有表</li>
     * <li>所有都存在直接返回custTables.</li>
     * <li>如果发现有不存在表名直接返回null。</li>
     * </ul>
     *
     * @return
     * @throws SQLException
     */
    private List<String> getViews(Connection conn) throws SQLException {
        List<String> views = new ArrayList<String>();
        System.out.println(config.getConfigDataSource().getViewsSql());
        PreparedStatement pstate = conn.prepareStatement(String.format(config.getConfigDataSource().getViewsSql(), config.getDbName(), config.getDbSchema()));
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            views.add(results.getString(1));
        }

        String[] tableNames = config.getTableNames();
        if (null == tableNames || tableNames.length == 0) {
            return views;
        }

        // 循环判断是否配置的所有表都在当前库中存在
        List<String> custTables = Arrays.asList(tableNames);
        List<String> notExistTables = new ArrayList<String>();
        for (String tb : custTables) {
            if (!views.contains(tb)) {
                notExistTables.add(tb);
            }
        }
        if (notExistTables.size() == 0) {
            return custTables;
        }
        // 如果有错误的表名，打印到控制台，且返回null
        System.err.println("tablename " + notExistTables.toString() + " is not exist!! ==> stop generate!!");
        return null;
    }

    /**
     * 生成 beanName
     *
     * @param table 表名
     * @return beanName
     */
    private String getBeanName(String table, boolean includePrefix) {
        StringBuilder sb = new StringBuilder();
        if (table.contains("_")) {
            String[] tables = table.split("_");
            int l = tables.length;
            int s = 0;
            if (includePrefix) {
                s = 1;
            }
            for (int i = s; i < l; i++) {
                String temp = tables[i].trim();
                sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1).toLowerCase());
            }
        } else {
            sb.append(table.substring(0, 1).toUpperCase()).append(table.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private String processType(String type) {
        if (config.getConfigDataSource() == ConfigDataSource.ORACLE) {
            return oracleProcessType(type);
        }
        if (config.getConfigDataSource() == ConfigDataSource.POSTGRESQL) {
            return pgProcessType(type);
        }

        return mysqlProcessType(type);
    }

    /**
     * MYSQL字段类型转换
     *
     * @param type 字段类型
     * @return
     */
    private String mysqlProcessType(String type) {
        String t = type.toLowerCase();
        if (t.contains("char")) {
            return "String";
        } else if (t.contains("bigint")) {
            return "Long";
        } else if (t.contains("int")) {
            return "Integer";
        } else if (t.contains("date")) {
            return "Date";
        } else if (t.contains("timestamp")) {
            return "Timestamp";
        } else if (t.contains("text")) {
            return "String";
        } else if (t.contains("bit")) {
            return "Boolean";
        } else if (t.contains("decimal")) {
            return "BigDecimal";
        } else if (t.contains("blob")) {
            return "byte[]";
        } else if (t.contains("float")) {
            return "Float";
        } else if (t.contains("double")) {
            return "Double";
        } else if (t.contains("json") || t.contains("enum")) {
            return "String";
        }
        return null;
    }

    /**
     * ORACLE字段类型转换
     *
     * @param type 字段类型
     * @return
     */
    private String oracleProcessType(String type) {
        String t = type.toUpperCase();
        if (t.contains("CHAR")) {
            return "String";
        } else if (t.contains("DATE")) {
            return "Date";
        } else if (t.contains("TIMESTAMP")) {
            return "Timestamp";
        } else if (t.contains("NUMBER")) {
            return "BigDecimal";
            //return "Double";
        } else if (t.contains("FLOAT")) {
            return "BigDecimal";
        } else if (t.contains("BLOB")) {
            return "byte[]";
        } else if (t.contains("RAW")) {
            return "byte[]";
        }
        return null;
    }

    /**
     * ORACLE字段类型转换
     *
     * @param type 字段类型
     * @return
     */
    private String pgProcessType(String type) {
        String t = type.toUpperCase();
        if (t.contains("CHARACTER")) {
            return "String";
        } else if (t.contains("DATE")) {
            return "Date";
        } else if (t.contains("TIMESTAMP")) {
            return "Timestamp";
        } else if (t.contains("NUMERIC")) {
            return "BigDecimal";
        } else if (t.contains("FLOAT")) {
            return "BigDecimal";
        } else if (t.contains("INT")) {
            return "Long";
        } else if (t.contains("BLOB")) {
            return "Object";
        } else if (t.contains("RAW")) {
            return "byte[]";
        }
        return null;
    }

    /**
     * 字段是否为日期类型
     *
     * @param types 字段类型列表
     * @return
     */
    private boolean isDate(List<String> types) {
        for (String type : types) {
            String t = type.toLowerCase();
            if (t.contains("date")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字段是否为日期时间类型
     *
     * @param types 字段类型列表
     * @return
     */
    private boolean isTimestamp(List<String> types) {
        for (String type : types) {
            String t = type.toLowerCase();
            if (t.contains("timestamp")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字段是否为浮点数类型
     *
     * @param types 字段类型列表
     * @return
     */
    private boolean isDecimal(List<String> types) {
        for (String type : types) {
            if (type.toLowerCase().contains("decimal") || type.toLowerCase().contains("number") || type.toLowerCase().contains("numeric")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字段处理
     *
     * @param field 表字段
     * @return
     */
    private String processField(String field) {
        /*
         * 驼峰命名直接返回
         */
        if (!field.contains("_")) {
            if (StringUtils.isUpperCase(field)) {
                /*
                 * 纯大写命名，转为小写属性
                 */
                return field.toLowerCase();
            }
            return field;
        }

        /*
         * 处理下划线分割命名字段
         */
        StringBuilder sb = new StringBuilder();
        String[] fields = field.split("_");
        sb.append(fields[0].toLowerCase());
        for (int i = 1; i < fields.length; i++) {
            String temp = fields[i];
            sb.append(temp.substring(0, 1).toUpperCase());
            sb.append(temp.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * 构建类上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
        bw.write("/**\n");
        bw.write(" * " + text+"\n");
        bw.write(" *\n");
        bw.write(" * @author " + config.getAuthor()+"\n");
        bw.write(" * @since " + DateUtil.getCurrDateTimeStr()+"\n");
        bw.write(" */\n");
        return bw;
    }

    /**
     * 构建类最上面的注释
     *
     * @param bw
     * @return
     * @throws IOException
     */
    private BufferedWriter buildClassCommentTop(BufferedWriter bw) throws IOException {
        bw.write("/*");
        bw.newLine();
        bw.write(" * Copyright (c) 2017- Ftecx Corp.");
        bw.newLine();
        bw.write(" * All rights reserved.");
        bw.newLine();
        bw.write(" */");
        bw.newLine();
        return bw;
    }

    /**
     * 生成实体类
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildEntityBean(List<String> columns, List<String> types, List<String> comments, String tableComment,
                                 Map<String, IdInfo> idMap, String table, String beanName) throws IOException {
        File beanFile = new File(PATH_ENTITY, beanName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile), "utf-8"));
        bw = buildClassCommentTop(bw);
        bw.write("package " + ENTITYPACKAGE + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import java.io.Serializable;");
        bw.newLine();
        if (isDate(types) || isTimestamp(types)) {
            bw.write("import com.fasterxml.jackson.databind.annotation.JsonSerialize;");
            bw.newLine();
        }
        if (isDate(types)) {
            bw.write("import java.util.Date;");
            bw.newLine();
            bw.write("import com.ftx.frame.common.component.JsonDateSerializer;");
            bw.newLine();
        }
        if (isTimestamp(types)) {
            bw.write("import java.sql.Timestamp;");
            bw.newLine();
            bw.write("import com.ftx.frame.common.component.JsonTimestampSerializer;");
            bw.newLine();
        }

        if (isDecimal(types)) {
            bw.write("import java.math.BigDecimal;");
            bw.newLine();
        }
        bw.newLine();
        if (config.getIdType() != IdType.ID_WORKER && !IS_VIEW) {
            bw.write("import com.baomidou.mybatisplus.enums.IdType;");
            bw.newLine();
        }
        bw.newLine();
        /*
         * 开启 BaseEntity 导入自定义包
         */
        if (null != config.getConfigBaseEntity() && null != config.getConfigBaseEntity().getPackageName()
                && !config.getEntityPackage().equals(config.getConfigBaseEntity().getPackageName())) {
            bw.write("import " + config.getConfigBaseEntity().getPackageName() + ";");
            bw.newLine();
        }
        bw.write("import com.baomidou.mybatisplus.annotations.TableField;");
        bw.newLine();
        if (null == config.getConfigBaseEntity() && !IS_VIEW) {
            bw.write("import com.baomidou.mybatisplus.annotations.TableId;");
            bw.newLine();
        }
        if (table.contains("_")) {
            bw.write("import com.baomidou.mybatisplus.annotations.TableName;");
            bw.newLine();
        }
        bw.write("import com.ftx.biz.common.model.BaseObject;\n\n");
        bw = buildClassComment(bw, tableComment);
        /* 包含下划线注解 */
        if (table.contains("_")) {
            bw.write("@TableName(\"" + table + "\")");
            bw.newLine();
        }

        /**
         * 实体类名处理，开启 BaseEntity 继承父类
         */
        if (null != config.getConfigBaseEntity()) {
            bw.write("public class " + beanName + " extends " + config.getConfigBaseEntity().getClassName() + " {");
        } else {
            bw.write("public class " + beanName + " extends BaseObject implements Serializable {");
        }
        bw.newLine();
        bw.newLine();
        bw.write("\t@TableField(exist = false)");
        bw.newLine();
        bw.write("\tprivate static final long serialVersionUID = 1L;");
        bw.newLine();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            bw.newLine();
            bw.write("\t/** " + comments.get(i) + " */");
            bw.newLine();
            /*
             * 判断ID 添加注解 <br> isLine 是否包含下划线
             */
            String column = columns.get(i);
            String field = config.namingOfHump ? processField(column) : column.toLowerCase();
            boolean isLine = column.contains("_");
            IdInfo idInfo = idMap.get(column);
            if (idInfo != null) {
                // @TableId(value = "test_id", type = IdType.AUTO)
                bw.write("\t@TableId");
                String idType = toIdType();
                if (column.equals("ID") || column.equals("id"))
                    idType = "type = IdType.AUTO";
                if (isLine) {
                    if (config.isDbColumnUnderline()) {
                        // 排除默认自增
                        if (null != idType) {
                            bw.write("(");
                            bw.write(idType);
                            bw.write(")");
                        }
                    } else {
                        //bw.write("(value = \"" + column + "\"");
                        bw.write("(");
                        if (null != idType) {
                            //	bw.write(", ");
                            bw.write(idType);
                        }
                        bw.write(")");
                    }
                } else if (null != idType) {
                    bw.write("(");
                    bw.write(idType);
                    bw.write(")");
                }
                bw.newLine();
            } else if (isLine && !config.isDbColumnUnderline()) {
                // @TableField(value = "test_type", exist = false)
                bw.write("\t@TableField");
                bw.newLine();
            }

            if (types.get(i).toLowerCase().contains("date")) {
                bw.write("\t@JsonSerialize(using = JsonDateSerializer.class)");
                bw.newLine();
            }
            if (types.get(i).toLowerCase().contains("timestamp")) {
                bw.write("\t@JsonSerialize(using = JsonTimestampSerializer.class)");
                bw.newLine();
            }
            bw.write("\tprivate " + processType(types.get(i)) + " " + field + ";");
            bw.newLine();
        }

        /*
         * 字段常量处理
         */
        this.buildEntityBeanColumnBaseConstant(columns, types, comments, bw, size);
        bw.newLine();

        /*
         * 生成get 和 set方法
         */
        for (int i = 0; i < size; i++) {
            String _tempType = processType(types.get(i));
            String _tempField = config.namingOfHump ? processField(columns.get(i)) : columns.get(i).toLowerCase();

            String _field = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
            bw.newLine();
            bw.write("\tpublic " + _tempType + " get" + _field + "() {");
            bw.newLine();
            bw.write("\t\treturn this." + _tempField + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            /* 是否为构建者模型 */
            if (config.isBuliderModel()) {
                bw.write("\tpublic " + beanName + " set" + _field + "(" + _tempType + " " + _tempField + ") {");
                bw.newLine();
                bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
                bw.newLine();
                bw.write("\t\treturn this;");
            } else {
                bw.write("\tpublic void set" + _field + "(" + _tempType + " " + _tempField + ") {");
                bw.newLine();
                bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
            }
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
        }

        bw.newLine();
        bw.write("}");
        bw.newLine();
        bw.flush();
        bw.close();
    }

    private void buildEntityBeanColumnBaseConstant(List<String> columns, List<String> types, List<String> comments,
                                                   BufferedWriter bw, int size) throws IOException {
        /*
         * 【实体】是否生成字段常量（默认 false）
         */
        if (config.isColumnBaseConstant()) {
            for (int i = 0; i < size; i++) {
                String _tempType = processType(types.get(i));
                String _column = columns.get(i);
                bw.newLine();
                bw.write("\tpublic static final " + _tempType + " " + _column.toUpperCase() + " = \"" + _column + "\";");
            }
            bw.newLine();
        }
    }

    public String toIdType() {
        if (config.getIdType() == IdType.AUTO) {
            return "type = IdType.AUTO";
        } else if (config.getIdType() == IdType.INPUT) {
            return "type = IdType.INPUT";
        } else if (config.getIdType() == IdType.UUID) {
            return "type = IdType.UUID";
        }
        return null;
    }

    /**
     * 构建Mapper文件
     *
     * @param beanName
     * @param mapperName
     * @throws IOException
     */
    private void buildMapper(String beanName, String mapperName) throws IOException {
        File mapperFile = new File(PATH_MAPPER, mapperName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
        bw = buildClassCommentTop(bw);
        bw.write("package " + MAPPERPACKAGE + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import " + ENTITYPACKAGE + "." + beanName + ";");
        bw.newLine();
        bw.write("import com.baomidou.mybatisplus.mapper.BaseMapper;\n\n");

        bw = buildClassComment(bw, beanName + " 表数据库接口");
        if (config.getConfigIdType() == ConfigIdType.STRING) {
            bw.write("public interface " + mapperName + " extends BaseMapper<" + beanName + "> {");
        } else {
            bw.write("public interface " + mapperName + " extends AutoMapper<" + beanName + "> {");
        }
        bw.newLine();
        bw.newLine();

        // ----------定义mapper中的方法End----------
        bw.newLine();
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建实体类映射XML文件
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildMapperXml(List<String> columns, List<String> types, List<String> comments,
                                Map<String, IdInfo> idMap, String mapperName, String mapperXMLName) throws IOException {
        File mapperXmlFile = new File(PATH_XML, mapperXMLName + ".xml");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile), "utf-8"));
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write(
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        bw.newLine();
        bw.write("<mapper namespace=\"" + MAPPERPACKAGE + "." + mapperName + "\">");
        bw.newLine();
        bw.newLine();
        bw.write("\t<cache type=\"com.ftx.frame.util.redisCache.LoggingRedisCache\"  />");
        bw.newLine();
        bw.newLine();


        /*
         * 下面开始写SqlMapper中的方法
         */
        //	buildSQL(bw, idMap, columns);

        bw.write("</mapper>");
        bw.flush();
        bw.close();
    }

    /**
     * 构建controller文件
     *
     * @param beanName
     * @param serviceName
     * @throws IOException
     */
    private void buildController(String beanName, String controllerName, String serviceName, String moduleName) throws IOException {
        String serviceNameUse = StringUtil.capitalizeLow(serviceName);
        String beanNameUse = StringUtil.capitalizeLow(beanName);
        String bean = "";
        if (!IS_VIEW) {
            bean = StringUtil.capitalizeLow(beanName.substring(moduleName.length(), beanName.length()));
        } else {
            bean = "v" + StringUtil.capitalizeLow(beanName.substring(moduleName.length() + 1, beanName.length()));
        }
        String request_mapping = "/" + moduleName + "/" + bean;
        File controllerFile = new File(PATH_CONTROLLER, controllerName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(controllerFile), "utf-8"));
        bw = buildClassCommentTop(bw);
        bw.write("package " + CONTROLLERPACKAGE + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.newLine();
        bw.write("import org.springframework.beans.factory.annotation.Autowired;\n");
        bw.write("import org.springframework.web.bind.annotation.RequestMapping;\n");
        bw.write("import org.springframework.web.bind.annotation.PostMapping;\n");
        bw.write("import org.springframework.web.bind.annotation.RestController;\n\n");
        bw.write("import com.baomidou.mybatisplus.plugins.Page;\n");
        bw.write("import " + ENTITYPACKAGE + "." + beanName + ";\n");
        bw.write("import " + SERVICEPACKAGE + "." + serviceName + ";\n");
        bw.write("import com.ftx.biz.common.controller.BaseController;\n");
        bw.write("import com.ftx.biz.common.model.BaseResult;\n");
        bw.write("import org.slf4j.Logger;\n");
        bw.write("import org.slf4j.LoggerFactory;\n\n");
        bw = buildClassComment(bw, beanName + " 表数据控制层");
        bw.write("@RestController");
        bw.newLine();
        bw.write("@RequestMapping(\"/\")");
        bw.newLine();
        bw.write("public class " + controllerName + " extends BaseController {\n\n");
        bw.write("\tprivate Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");
        bw.write("\t@Autowired");
        bw.newLine();
        bw.write("\tprivate " + serviceName + " " + serviceNameUse + ";");
        bw.newLine();
        if (!IS_VIEW) {
            bw.newLine();
            bw.write("\t/** insert or update */");
            bw.newLine();
            bw.write("\t@PostMapping(value = \"" + request_mapping + "\")");
            bw.newLine();
            bw.write("\tpublic BaseResult<" + beanName + "> save(" + beanName + " " + beanNameUse + "){");
            bw.newLine();
            bw.write("\t\treturn " + serviceNameUse + ".save(" + beanNameUse + ");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
        }
        request_mapping = "/" + moduleName + "/" + bean+"One";
        bw.newLine();
        bw.write("\t/** select one entity */");
        bw.newLine();
        bw.write("\t@PostMapping(value = \"" + request_mapping + "\")");
        bw.newLine();
        bw.write("\tpublic " + beanName + " getOne(" + beanName + " " + beanNameUse + "){");
        bw.newLine();
        bw.write("\t\treturn " + serviceNameUse + ".getOne(" + beanNameUse + ");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        request_mapping = "/" + moduleName + "/" + bean + "List";
        bw.newLine();
        bw.write("\t/** select entity List  */");
        bw.newLine();
        bw.write("\t@PostMapping(value = \"" + request_mapping + "\")");
        bw.newLine();
        bw.write("\tpublic List<" + beanName + "> retrieveList(" + beanName + " " + beanNameUse + "){");
        bw.newLine();
        bw.write("\t\treturn " + serviceNameUse + ".retrieveList(" + beanNameUse + ");");
        bw.newLine();
        bw.write("\t}\n");
        request_mapping = "/" + moduleName + "/" + bean + "Page";
        bw.write("\t/** select entity page  */\n");
        bw.write("\t@PostMapping(value = \"" + request_mapping + "\")\n");
        bw.write("\tpublic Page<" + beanName + "> retrievePage(Page<" + beanName + "> page," + beanName + " " + beanNameUse + "){\n");
        bw.write("\t\treturn " + serviceNameUse + ".retrievePage(page," + beanNameUse + ");\n");
        bw.write("\t}\n\n");
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建service文件
     *
     * @param beanName
     * @param serviceName
     * @throws IOException
     */
    private void buildService(String beanName, String serviceName) throws IOException {
        File serviceFile = new File(PATH_SERVICE, serviceName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile), "utf-8"));
        bw = buildClassCommentTop(bw);
        bw.write("package " + SERVICEPACKAGE + ";\n\n");
        bw.write("import " + ENTITYPACKAGE + "." + beanName + ";\n");
        String superService = config.getSuperService();
        bw.write("import " + superService + ";\n\n");

        bw = buildClassComment(bw, beanName + " 表服务层接口");
        superService = superService.substring(superService.lastIndexOf(".") + 1);
        bw.write("public interface " + serviceName + " extends " + superService + "<" + beanName + "> {\n\n\n");
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建service实现类文件
     *
     * @param beanName
     * @param serviceImplName
     * @param mapperName
     * @throws IOException
     */
    private void buildServiceImpl(String beanName, String serviceImplName, String serviceName, String mapperName)
            throws IOException {
        File serviceFile = new File(PATH_SERVICE_IMPL, serviceImplName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile), "utf-8"));
        bw = buildClassCommentTop(bw);
        bw.write("package " + SERVICEIMPLPACKAGE + ";\n\n");
        bw.write("import org.springframework.stereotype.Service;\n\n");
        bw.write("import " + MAPPERPACKAGE + "." + mapperName + ";\n");
        bw.write("import " + ENTITYPACKAGE + "." + beanName + ";\n");
        bw.write("import " + SERVICEPACKAGE + "." + serviceName + ";\n");

        String superServiceImpl = config.getSuperServiceImpl();
        bw.write("import " + superServiceImpl + ";\n");
        bw.write("import org.slf4j.Logger;\n");
        bw.write("import org.slf4j.LoggerFactory;\n\n");

        bw = buildClassComment(bw, beanName + " 表服务层接口实现类");
        bw.write("@Service\n");
        superServiceImpl = superServiceImpl.substring(superServiceImpl.lastIndexOf(".") + 1);
        bw.write("public class " + serviceImplName + " extends " + superServiceImpl + "<" + mapperName + ", " + beanName
                + "> implements " + serviceName + " {\n\n");
        bw.write("\tprivate Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");

        // ----------定义service中的方法End----------
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 获取所有的数据库表注释
     *
     * @return
     * @throws SQLException
     */
    private Map<String, String> getTableComment(Connection conn) throws SQLException {
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = conn.prepareStatement(String.format(config.getConfigDataSource().getTableCommentsSql(), config.getDbName(), config.getDbSchema()));
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            maps.put(results.getString(config.getConfigDataSource().getTableName()),
                    results.getString(config.getConfigDataSource().getTableComment()));
        }
        return maps;
    }

    private void generate(Connection conn, Map<String, String> tableComments, String table, ConfigDataSource cfds, File gf1, File gf2) throws SQLException, IOException {

        List<String> columns = new ArrayList<String>();
        List<String> types = new ArrayList<String>();
        List<String> comments = new ArrayList<String>();
        /* ID 是否存在,不考虑联合主键设置 */
        boolean idExist = false;
        Map<String, IdInfo> idMap = new HashMap<String, IdInfo>();
        String tableFieldsSql = String.format(config.getConfigDataSource().getTableFieldsSql(), table, config.getDbSchema());
        ResultSet results = conn.prepareStatement(tableFieldsSql).executeQuery();
        while (results.next()) {
            String field = results.getString(config.getConfigDataSource().getFieldName());

            /* 开启 baseEntity 跳过公共字段 */
            if (null != config.getConfigBaseEntity() && config.getConfigBaseEntity().includeColumns(field)) {
                continue;
            }

            columns.add(field);
            types.add(results.getString(config.getConfigDataSource().getFieldType()));
            comments.add(results.getString(config.getConfigDataSource().getFieldComment()));
            if (cfds == ConfigDataSource.MYSQL && !idExist) {
                /* MYSQL 主键ID 处理方式 */
                String key = results.getString(config.getConfigDataSource().getFieldKey());
                if ("PRI".equals(key)) {
                    boolean autoIncrement = false;
                    if ("auto_increment".equals(results.getString("EXTRA"))) {
                        autoIncrement = true;
                    }
                    idExist = true;
                    idMap.put(field, new IdInfo(field, autoIncrement));
                }
            }
        }

        if (cfds == ConfigDataSource.ORACLE) {
            /* ORACLE 主键ID 处理方式 */
            String idSql = String.format(
                    "SELECT A.COLUMN_NAME FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND B.CONSTRAINT_TYPE = 'P' AND A.TABLE_NAME = '%s'",
                    table);
            ResultSet rs = conn.prepareStatement(idSql).executeQuery();
            while (rs.next() && !idExist) {
                String field = rs.getString(config.getConfigDataSource().getFieldKey());
                idExist = true;
                idMap.put(field, new IdInfo(field, false));
            }
        } else if (cfds == ConfigDataSource.POSTGRESQL) {
            /* POSTGRESQL 主键ID 处理方式 */
            String idSql = String.format(
                    "select COLUMN_NAME from INFORMATION_SCHEMA.constraint_column_usage WHERE TABLE_NAME = '%s'",
                    table);
            ResultSet rs = conn.prepareStatement(idSql).executeQuery();
            while (rs.next() && !idExist) {
                String field = rs.getString(config.getConfigDataSource().getFieldKey());
                idExist = true;
                idMap.put(field, new IdInfo(field, false));
            }
        }
        String beanName = getBeanName(table, config.isDbPrefix());
        String mapperName = String.format(config.getMapperName(), beanName);
        String mapperXMLName = String.format(config.getMapperXMLName(), beanName);
        String serviceName = String.format(config.getServiceName(), beanName);
        String serviceImplName = String.format(config.getServiceImplName(), beanName);
        String controllerName = String.format(config.getControllerName(), beanName);
        String moduleName = "";
        table = table.toUpperCase();
        if (table.contains("V_")&&table.indexOf("V_")==0){
            table = table.substring(2,table.length());
            moduleName = table.substring(0,table.indexOf("_")).toLowerCase();
        }
        else
            moduleName = (table.substring(0, table.indexOf("_")).toLowerCase());
        String saveDir = gf1.getPath();
        String saveDirXml = gf2.getPath();
        ENTITYPACKAGE = String.format(config.getEntityPackage(), moduleName);
        MAPPERPACKAGE = String.format(config.getMapperPackage(), moduleName);
        XMLPACKAGE = String.format(config.getXmlPackage(), moduleName);
        SERVICEPACKAGE = String.format(config.getServicePackage(), moduleName);
        SERVICEIMPLPACKAGE = String.format(config.getServiceImplPackage(), moduleName);
        CONTROLLERPACKAGE = String.format(config.getControllerPackage(), moduleName);

        PATH_ENTITY = getFilePath(saveDir, getPathFromPackageName(ENTITYPACKAGE));
        PATH_MAPPER = getFilePath(saveDir, getPathFromPackageName(MAPPERPACKAGE));
        PATH_XML = getFilePath(saveDir, getPathFromPackageName(XMLPACKAGE));
        PATH_XML = getFilePath(saveDirXml, getPathFromPackageName(XMLPACKAGE));
        PATH_SERVICE = getFilePath(saveDir, getPathFromPackageName(SERVICEPACKAGE));
        PATH_SERVICE_IMPL = getFilePath(saveDir, getPathFromPackageName(SERVICEIMPLPACKAGE));
        PATH_CONTROLLER = getFilePath(saveDir, getPathFromPackageName(CONTROLLERPACKAGE));

        /**
         * 根据文件覆盖标志决定是否生成映射文件
         */
        if (valideFile(PATH_CONTROLLER, beanName, JAVA_SUFFIX)) {
            buildController(beanName, controllerName, serviceName, moduleName);
        }
        if (valideFile(PATH_ENTITY, beanName, JAVA_SUFFIX)) {
            if (ConfigDataSource.POSTGRESQL == cfds)
                buildEntityBean(columns, types, comments, tableComments.get(table.toLowerCase()), idMap, table, beanName);
            else
                buildEntityBean(columns, types, comments, tableComments.get(table), idMap, table, beanName);
        }
        if (valideFile(PATH_MAPPER, mapperName, JAVA_SUFFIX)) {
            buildMapper(beanName, mapperName);
        }
        if (valideFile(PATH_XML, mapperXMLName, XML_SUFFIX)) {
            buildMapperXml(columns, types, comments, idMap, mapperName, mapperXMLName);
        }
        if (valideFile(PATH_SERVICE, serviceName, JAVA_SUFFIX)) {
            buildService(beanName, serviceName);
        }
        if (valideFile(PATH_SERVICE_IMPL, serviceImplName, JAVA_SUFFIX)) {
            buildServiceImpl(beanName, serviceImplName, serviceName, mapperName);
        }

    }

    class IdInfo {
        private String value;
        private boolean autoIncrement;

        public IdInfo(String value, boolean autoIncrement) {
            this.value = value;
            this.autoIncrement = autoIncrement;

        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
