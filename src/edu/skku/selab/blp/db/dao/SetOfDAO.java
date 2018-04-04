package edu.skku.selab.blp.db.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class SetOfDAO extends BaseDAO{
	
	public SetOfDAO() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	public static HashMap<String, PreparedStatement> stmtMap = new HashMap<String, PreparedStatement>();

	public HashMap<String, PreparedStatement> getStmtMap(){
		return stmtMap;
	}
	public void setStmtMap() throws SQLException{
		
		String sql = "INSERT INTO BUG_INFO (BUG_ID, OPEN_DATE, FIXED_DATE, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, VER)   VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStructuredBug",ps);
		
		sql= "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugs",ps);
		
		sql = "SELECT BUG_ID, COR_NORM FROM BUG_INFO ";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllNorms", ps);
		
		sql = "SELECT COUNT(BUG_ID) FROM BUG_INFO " +
				"WHERE FIXED_DATE <= ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugCountWithDate", ps);
		
		sql = "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO " +
				"WHERE FIXED_DATE <= ? AND BUG_ID != ? ORDER BY FIXED_DATE";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getPreviousFixedBugs", ps);
		
		sql = "SELECT OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, VER FROM BUG_INFO WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug", ps);
		
		sql = "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER "
				+ "FROM BUG_INFO WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getDetailBug", ps);
		
		sql = "INSERT INTO BUG_TERM_INFO (TERM) VALUES (?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTerm", ps);
		
		sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTerm2", ps);
		
		sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTerm3", ps);
		
		sql = "SELECT BUG_ID, COR " +
				"FROM BUG_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCorpusMap", ps);
		
		sql = "SELECT COR_NORM " +
				"FROM BUG_INFO " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue", ps);
		
		sql = "SELECT TERM, BUG_TERM_ID FROM BUG_TERM_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap", ps);
		
		sql = "SELECT COUNT(BUG_TERM_ID) FROM BUG_TERM_INFO";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllTermCount", ps);
		
		sql = "SELECT SF_TERM_ID FROM SF_TERM_INFO WHERE TERM = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSfTermID", ps);
		
		sql = "SELECT A.TF, A.IDF, B.TERM FROM BUG_SF_TERM_WGT A, SF_TERM_INFO B WHERE A.BUG_ID = ? AND A.SF_TERM_ID = B.SF_TERM_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSfTermMap", ps);
		
		sql = "INSERT INTO BUG_STRACE_INFO (BUG_ID, STRACE_CLASS) VALUES (?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStackTraceClass", ps);
		
		sql = "SELECT STRACE_CLASS "+
				"FROM BUG_STRACE_INFO " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses", ps);
		
		sql = "INSERT INTO BUG_SF_TERM_WGT (BUG_ID, SF_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF) " +
					"VALUES (?, ?, ?, ?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugSfTermWeight", ps);
		
		sql = "UPDATE BUG_SF_TERM_WGT SET (TERM_CNT, INV_DOC_CNT, TF, IDF) " +
				"= (?, ?, ?, ?) WHERE BUG_ID = ? AND SF_TERM_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugSfTermWeight2", ps);
		
		sql ="INSERT INTO BUG_MTH_TERM_WGT (BUG_ID, MTH_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugMthTermWeight", ps);
		
		sql = "SELECT C.TERM_CNT, C.INV_DOC_CNT, C.TF, C.IDF "+
				"FROM SF_TERM_INFO B, BUG_SF_TERM_WGT C " +
				"WHERE C.BUG_ID = ? AND " +
				"B.TERM = ? AND " +
				"B.SF_TERM_ID = C.SF_TERM_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugSfTermWeight", ps);

		sql = "SELECT C.TERM_CNT, C.INV_DOC_CNT, C.TF, C.IDF "+
				"FROM SF_TERM_INFO B, BUG_MTH_TERM_WGT C " +
				"WHERE C.BUG_ID = ? AND " +
				"B.TERM = ? AND " +
				"B.SF_TERM_ID = C.MTH_TERM_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugMthTermWeight", ps);
		
		sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugTermID", ps);
		
		sql = "INSERT INTO BUG_TERM_WGT (BUG_ID, BUG_TERM_ID, TW) " +
				"VALUES (?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTermWeight", ps);
		
		sql = "UPDATE BUG_TERM_WGT SET (TW) = (?) " +
				"WHERE BUG_ID = ? AND BUG_TERM_ID = ?";	
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTermWeight2", ps);
		
		sql = "SELECT C.TW "+
				"FROM BUG_INFO A, BUG_TERM_INFO B, BUG_TERM_WGT C " +
				"WHERE A.BUG_ID = ? AND "+
				"A.BUG_ID = C.BUG_ID AND B.TERM = ? AND B.BUG_TERM_ID = C.BUG_TERM_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugTermWeight", ps);
		
		sql = "SELECT B.TERM, C.BUG_TERM_ID, C.TW "+
				"FROM BUG_TERM_INFO B, BUG_TERM_WGT C " +
				"WHERE C.BUG_ID = ? AND B.BUG_TERM_ID = C.BUG_TERM_ID ORDER BY C.BUG_TERM_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugTermWeightList", ps);
		
		sql = "INSERT INTO BUG_FIX_SF_INFO (BUG_ID, FIXED_SF_VER_ID) VALUES (?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugFixedFileInfo", ps);
		
		sql = "INSERT INTO BUG_FIX_MTH_INFO (BUG_ID, FIXED_MTH_ID) VALUES (?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugFixedMethodInfo", ps);
		
		sql = "SELECT A.SF_NAME, B.VER, C.FIXED_SF_VER_ID FROM SF_INFO A, SF_VER_INFO B, BUG_FIX_SF_INFO C " + 
				"WHERE C.BUG_ID = ? AND C.FIXED_SF_VER_ID = B.SF_VER_ID AND A.SF_ID = B.SF_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getFixedFiles", ps);
		
		sql = "SELECT A.MTH_ID, A.SF_VER_ID, A.MTH_NAME, A.RET_TYPE, A.PARAMS, A.HASH_KEY FROM MTH_INFO A, BUG_FIX_MTH_INFO B " + 
				"WHERE B.BUG_ID = ? AND B.FIXED_MTH_ID = A.MTH_ID";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getFixedMethods", ps);
		
		sql = "INSERT INTO SIMI_BUG_ANAYSIS (BUG_ID, SIMI_BUG_ID, SIMI_BUG_SCORE) VALUES (?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertSimilarBugInfo", ps);		
		
		sql = "SELECT SIMI_BUG_ID, SIMI_BUG_SCORE FROM SIMI_BUG_ANAYSIS " + 
				"WHERE BUG_ID = ? AND SIMI_BUG_SCORE != 0.0";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSimilarBugInfos", ps);
		
		sql = "UPDATE BUG_INFO SET TOT_CNT = ? " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateTotalTermCount", ps);
		
		sql = "UPDATE BUG_INFO SET MTH_TOT_CNT = ? " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMethodTotalTermCount", ps);
		
		sql = "UPDATE BUG_INFO SET COR_NORM = ?, SMR_COR_NORM = ?, DESC_COR_NORM = ? " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateNormValues", ps);
		
		sql = "UPDATE BUG_INFO SET MTH_NORM = ? " +
				"WHERE BUG_ID = ?";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMthNormValues", ps);		
		
		sql = "INSERT INTO BUG_CMT_INFO (BUG_ID, CMT_ID, ATHR, CMT_DATE, CMT_COR) VALUES (?, ?, ?, ?, ?)";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertComment", ps);
		
		sql = "SELECT CMT_ID, ATHR, CMT_DATE, CMT_COR "+
					"FROM BUG_CMT_INFO " +
					"WHERE BUG_ID = ?";
			
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getComments", ps);
	}
}
