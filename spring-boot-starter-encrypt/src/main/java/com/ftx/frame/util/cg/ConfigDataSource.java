/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.cg;


/**
 * <p>
 * 数据源配置
 * </p>
 * 
 * @author se7en
 * @Date 2016-11-04
 */
public enum ConfigDataSource {
	MYSQL("mysql", "show tables", "show table status", "show full fields from %s", "NAME", "COMMENT" ,"FIELD","TYPE","COMMENT","KEY","show views"), 
	ORACLE("oracle", 
			"SELECT * FROM USER_TABLES", 
			"SELECT * FROM USER_TAB_COMMENTS",
			"SELECT A.COLUMN_NAME, A.DATA_TYPE, B.COMMENTS  FROM USER_TAB_COLUMNS A, " +
			"USER_COL_COMMENTS B " +
			"WHERE A.TABLE_NAME=B.TABLE_NAME " +
			"AND A.COLUMN_NAME = B.COLUMN_NAME " +
			"AND A.TABLE_NAME='%s'",
			"TABLE_NAME", "COMMENTS" ,"COLUMN_NAME","DATA_TYPE","COMMENTS","COLUMN_NAME",
			"SELECT * FROM USER_VIEWS"),
	POSTGRESQL("postgresql", 
			"select table_name from information_schema.tables where table_catalog = '%s' and table_schema = '%s'", 
			"select table_name,'table' as table_type,obj_description((table_schema||'.'||table_name)::regclass, 'pg_class') as comments from information_schema.tables where table_catalog = '%s' and table_schema = '%s' ", 
			"select s.column_name as column_name,s.data_type ,coalesce(col_description(c.oid,ordinal_position) ,s.column_name) as comments from information_schema.columns s,pg_class c where s.table_name = '%s' and s.table_name = c.relname and s.table_schema = '%s' ", 
			"table_name", "comments" ,"column_name","data_type","comments","column_name",
			"select table_name from information_schema.views where table_catalog = '%s' and table_schema = '%s'");
	
	private final String db;
	private final String tablesSql;
	private final String tableCommentsSql;
	private final String tableFieldsSql;
	private final String tableName;
	private final String tableComment;
	private final String fieldName;
	private final String fieldType;
	private final String fieldComment;
	private final String fieldKey;
	private final String viewsSql;
	
	ConfigDataSource(final String db, final String tablesSql, final String tableCommentsSql,
			final String tableFieldsSql, final String tableName, final String tableComment, final String fieldName,
			final String fieldType, final String fieldComment, final String fieldKey,final String viewsSql) {
		this.db = db;
		this.tablesSql = tablesSql;
		this.tableCommentsSql = tableCommentsSql;
		this.tableFieldsSql = tableFieldsSql;
		this.tableName = tableName;
		this.tableComment = tableComment;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldComment = fieldComment;
		this.fieldKey = fieldKey;
		this.viewsSql = viewsSql;
	}

	/**
	 * <p>
	 * 获取数据库类型（默认 MySql）
	 * </p>
	 * 
	 * @param dbType
	 *            数据库类型字符串
	 * @return
	 */
	public static ConfigDataSource getConfigDataSource(String dbType) {
		for (ConfigDataSource dt : ConfigDataSource.values()) {
			if (dt.getDb().equals(dbType)) {
				return dt;
			}
		}
		return MYSQL;
	}

	public String getDb() {
		return db;
	}

	public String getTablesSql() {
		return tablesSql;
	}

	public String getTableCommentsSql() {
		return tableCommentsSql;
	}

	public String getTableFieldsSql() {
		return tableFieldsSql;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getFieldComment() {
		return fieldComment;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public String getViewsSql() {
		return viewsSql;
	}

}
