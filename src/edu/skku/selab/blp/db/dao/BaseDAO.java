/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.h2.jdbcx.JdbcConnectionPool;

import edu.skku.selab.blp.Property;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BaseDAO {
	protected static Connection analysisDbConnection = null;
	protected static Connection evaluationDbConnection = null;
	protected PreparedStatement ps = null;
	protected ResultSet rs = null;
	protected static HashMap<String, PreparedStatement> stmtMap = null;
		
	final public static int INVALID = -1;
	final static String DEFAULT_DB_NAME = "sample";
	
	
	public BaseDAO() throws Exception {
		
		Property property = Property.getInstance();
		
		String dbName = BaseDAO.DEFAULT_DB_NAME;
		if (property != null) {
			dbName = property.getProductName();
		}
		
//		if(stmtMap == null && property.getSet() != null){
//			property.setStmtMap();
//			stmtMap = property.getStmtMap();
//		}

		openConnection(dbName);
	}

	public static void openEvaluationDbConnection() throws Exception {
		if (null == evaluationDbConnection) {
			Class.forName("org.h2.Driver");
			String connectionURL = "jdbc:h2:file:./db/evaluation";
			JdbcConnectionPool connectionPool = JdbcConnectionPool.create(connectionURL, "sa", "");
//			connectionPool.setMaxConnections();
			evaluationDbConnection = connectionPool.getConnection();
		}
	}
	
	public static void openConnection(String dbName) throws Exception {
		openEvaluationDbConnection();
		if (null == analysisDbConnection) {
			Class.forName("org.h2.Driver");
			String connectionURL = "jdbc:h2:file:./db/" + dbName;
			JdbcConnectionPool connectionPool = JdbcConnectionPool.create(connectionURL, "sa", "");
			analysisDbConnection = connectionPool.getConnection();
		}
	}
	public static void closeConnection() throws Exception {
		if (null != analysisDbConnection) {
			analysisDbConnection.close();
			analysisDbConnection = null;
		}
		
		if (null != evaluationDbConnection) {
			evaluationDbConnection.close();
			evaluationDbConnection = null;
		}
	}
	
	public static Connection getAnalysisDbConnection() {
		return analysisDbConnection;
	}
	
	public static Connection getEvaluationDbConnection() {
		return evaluationDbConnection;
	}
}
