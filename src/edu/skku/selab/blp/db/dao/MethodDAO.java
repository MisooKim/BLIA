/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.util.ArrayList;
import java.util.HashMap;

import edu.skku.selab.blp.common.Method;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class MethodDAO extends BaseDAO {

	/**
	 * @throws Exception
	 */
	public MethodDAO() throws Exception {
		super();
	}
	
	public int insertMethod(Method method) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertMethod");
			
			ps.setInt(1, method.getSourceFileVersionID());
			if(method.getName().length() > 126){
				method.setName(method.getName().substring(0, 126));
			}
			ps.setString(2, method.getName());			
			ps.setString(3, method.getReturnType());
			ps.setString(4, method.getParams());
			ps.setString(5, method.getHashKey());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getMethodID(method);
		} 

		return returnValue;
	}
	
	public int getMethodID(Method method) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getMethodID");
			
			ps.setString(1, method.getHashKey());
			ps.setString(2, method.getName());
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("MTH_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	// <Hash key, Method>
	public HashMap<String, Method> getMethods(int sourceFileVersionID) {
		HashMap<String, Method> methodInfo = new HashMap<String, Method>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getMethods");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				Method method = new Method(rs.getInt("MTH_ID"), sourceFileVersionID,
						rs.getString("MTH_NAME"), rs.getString("RET_TYPE"), rs.getString("PARAMS"),
						rs.getString("HASH_KEY"));
				methodInfo.put(rs.getString("HASH_KEY"), method);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return methodInfo;
	}
	
	// <Hash key, Method>
	public HashMap<Integer, ArrayList<Method>> getAllMethods() {
		HashMap<Integer, ArrayList<Method>> methodMap = new HashMap<Integer, ArrayList<Method>>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getAllMethods");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				int sourceFileVersionID = rs.getInt("SF_VER_ID");
				Method method = new Method(rs.getInt("MTH_ID"), sourceFileVersionID,
						rs.getString("MTH_NAME"), rs.getString("RET_TYPE"), rs.getString("PARAMS"),
						rs.getString("HASH_KEY"));
				ArrayList<Method> methods = methodMap.get(sourceFileVersionID);
				if (methods == null) {
					methods = new ArrayList<Method>();
				}
				methods.add(method);
				methodMap.put(sourceFileVersionID, methods);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return methodMap;
	}
	
	public int deleteAllMethods() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllMethods");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
}
