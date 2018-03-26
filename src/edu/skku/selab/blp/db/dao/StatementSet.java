package edu.skku.selab.blp.db.dao;

import java.sql.PreparedStatement;
import java.util.HashMap;

public class StatementSet extends BaseDAO{
	protected HashMap<String, PreparedStatement> psSet = new HashMap<String, PreparedStatement>();
	static PreparedStatement ps =null;
	static String sql = "";
	public StatementSet() throws Exception {
		super();
		String sql = "INSERT INTO BUG_INFO (BUG_ID, OPEN_DATE, FIXED_DATE, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, VER)" +
				 " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		psSet.put("insertStructuredBug", ps);
		
		sql = "DELETE FROM BUG_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		psSet.put("deleteAllBugs", ps);
		
		sql = "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		psSet.put("getBugs", ps);
		
		sql = "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO ";
		
		
	}
	
	public PreparedStatement getPS(String key){
		return psSet.get(key);
	}

}
