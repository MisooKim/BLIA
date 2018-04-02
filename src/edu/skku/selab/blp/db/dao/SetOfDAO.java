package edu.skku.selab.blp.db.dao;

import java.sql.PreparedStatement;
import java.util.HashMap;

public class SetOfDAO extends BaseDAO{
	
	public static HashMap<String, PreparedStatement> stmtMap = new HashMap<String, PreparedStatement>();

	public SetOfDAO() throws Exception{
		
		String sql = "bug.setID(rs.getInt(BUG_ID));";
		PreparedStatement ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStructuredBug",ps);
		sql = " INSERT INTO BUG_INFO (BUG_ID, OPEN_DATE, FIXED_DATE, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, VER)   VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStructuredBug",ps);
		sql = " DELETE FROM BUG_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugs",ps);
		sql = " SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " SELECT BUG_ID, COR_NORM FROM BUG_INFO ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " SELECT COUNT(BUG_ID) FROM BUG_INFO  WHERE FIXED_DATE < ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugCountWithDate",ps);
		sql = " SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO  WHERE FIXED_DATE < ? AND BUG_ID ! ? ORDER BY FIXED_DATE;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getPreviousFixedBugs",ps);
		sql = " SELECT OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, VER FROM BUG_INFO WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER  FROM BUG_INFO WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " INSERT INTO BUG_TERM_INFO (TERM) VALUES (?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " DELETE FROM BUG_TERM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllTerms",ps);
		sql = " SELECT BUG_ID, COR  FROM BUG_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCorpusMap",ps);
		sql = " SELECT COR_NORM  FROM BUG_INFO  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue",ps);
		sql = " SELECT TERM, BUG_TERM_ID FROM BUG_TERM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);
		sql = " SELECT COUNT(BUG_TERM_ID) FROM BUG_TERM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllTermCount",ps);
		sql = " SELECT COUNT(BUG_ID) FROM BUG_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " SELECT SF_TERM_ID FROM SF_TERM_INFO WHERE TERM  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSfTermID",ps);
		sql = " SELECT A.TF, A.IDF, B.TERM FROM BUG_SF_TERM_WGT A, SF_TERM_INFO B WHERE A.BUG_ID  ? AND A.SF_TERM_ID  B.SF_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSfTermMap",ps);
		sql = " INSERT INTO BUG_STRACE_INFO (BUG_ID, STRACE_CLASS) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStackTraceClass",ps);
		sql = " DELETE FROM BUG_STRACE_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllStackTraceClasses",ps);
		sql = " SELECT STRACE_CLASS FROM BUG_STRACE_INFO  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getStackTraceClasses",ps);
		sql = " INSERT INTO BUG_SF_TERM_WGT (BUG_ID, SF_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF)  VALUES (?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugSfTermWeight",ps);
		sql = " INSERT INTO BUG_MTH_TERM_WGT (BUG_ID, MTH_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF)  VALUES (?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugMthTermWeight",ps);
		sql = " DELETE FROM BUG_SF_TERM_WGT;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllBugSfTermWeights",ps);
		sql = " DELETE FROM BUG_MTH_TERM_WGT;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllBugMthTermWeights",ps);
		sql = " SELECT C.TERM_CNT, C.INV_DOC_CNT, C.TF, C.IDF FROM SF_TERM_INFO B, BUG_SF_TERM_WGT C  WHERE C.BUG_ID  ? AND  B.TERM  ? AND  B.SF_TERM_ID  C.SF_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " SELECT C.TERM_CNT, C.INV_DOC_CNT, C.TF, C.IDF FROM SF_TERM_INFO B, BUG_MTH_TERM_WGT C  WHERE C.BUG_ID  ? AND  B.TERM  ? AND  B.SF_TERM_ID  C.MTH_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " INSERT INTO BUG_TERM_WGT (BUG_ID, BUG_TERM_ID, TW)  VALUES (?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugTerm",ps);
		sql = " DELETE FROM BUG_TERM_WGT;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllBugTermWeights",ps);
		sql = " SELECT C.TW FROM BUG_INFO A, BUG_TERM_INFO B, BUG_TERM_WGT C  WHERE A.BUG_ID  ? AND A.BUG_ID  C.BUG_ID AND B.TERM  ? AND B.BUG_TERM_ID  C.BUG_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " SELECT B.TERM, C.BUG_TERM_ID, C.TW FROM BUG_TERM_INFO B, BUG_TERM_WGT C  WHERE C.BUG_ID  ? AND B.BUG_TERM_ID  C.BUG_TERM_ID ORDER BY C.BUG_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBug",ps);
		sql = " INSERT INTO BUG_FIX_SF_INFO (BUG_ID, FIXED_SF_VER_ID) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugFixedFileInfo",ps);
		sql = " INSERT INTO BUG_FIX_MTH_INFO (BUG_ID, FIXED_MTH_ID) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertBugFixedMethodInfo",ps);
		sql = " DELETE FROM BUG_FIX_SF_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllBugFixedInfo",ps);
		sql = " SELECT A.SF_NAME, B.VER, C.FIXED_SF_VER_ID FROM SF_INFO A, SF_VER_INFO B, BUG_FIX_SF_INFO C   WHERE C.BUG_ID  ? AND C.FIXED_SF_VER_ID  B.SF_VER_ID AND A.SF_ID  B.SF_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getFixedFiles",ps);
		sql = " SELECT A.MTH_ID, A.SF_VER_ID, A.MTH_NAME, A.RET_TYPE, A.PARAMS, A.HASH_KEY FROM MTH_INFO A, BUG_FIX_MTH_INFO B   WHERE B.BUG_ID  ? AND B.FIXED_MTH_ID  A.MTH_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getFixedMethods",ps);
		sql = " INSERT INTO SIMI_BUG_ANAYSIS (BUG_ID, SIMI_BUG_ID, SIMI_BUG_SCORE) VALUES (?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertSimilarBugInfo",ps);
		sql = " DELETE FROM SIMI_BUG_ANAYSIS;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllSimilarBugInfo",ps);
		sql = " SELECT SIMI_BUG_ID, SIMI_BUG_SCORE FROM SIMI_BUG_ANAYSIS   WHERE BUG_ID  ? AND SIMI_BUG_SCORE ! 0.0;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSimilarBugInfos",ps);
		sql = " UPDATE BUG_INFO SET TOT_CNT  ?  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateTotalTermCount",ps);
		sql = " UPDATE BUG_INFO SET MTH_TOT_CNT  ?  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMethodTotalTermCount",ps);
		sql = " UPDATE BUG_INFO SET COR_NORM  ?, SMR_COR_NORM  ?, DESC_COR_NORM  ?  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateNormValues",ps);
		sql = " UPDATE BUG_INFO SET MTH_NORM  ?  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMthNormValues",ps);
		sql = " INSERT INTO BUG_CMT_INFO (BUG_ID, CMT_ID, ATHR, CMT_DATE, CMT_COR) VALUES (?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertComment",ps);
		sql = " DELETE FROM BUG_CMT_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllComments",ps);
		sql = " SELECT CMT_ID, ATHR, CMT_DATE, CMT_COR FROM BUG_CMT_INFO  WHERE BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getComments",ps);
		sql = "sql  INSERT INTO COMM_SF_INFO (COMM_ID, COMM_SF, COMM_TYPE) VALUES (?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertCommitInfo",ps);
		sql = "int commitType  rs.getInt(COMM_TYPE);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllCommitInfo",ps);
		sql = "String commitFile  rs.getString(COMM_SF);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitMethods",ps);
		sql = "commitInfo.setCommitDate(rs.getTimestamp(COMM_DATE));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitInfo",ps);
		sql = "String commitID  rs.getString(COMM_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = "String commitID  rs.getString(COMM_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = "String fixedFile  rs.getString(COMM_SF);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = " INSERT INTO COMM_INFO (COMM_ID, COMM_DATE, MSG, COMMITTER) VALUES (?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertCommitInfo",ps);
		sql = " DELETE FROM COMM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllCommitInfo",ps);
		sql = " SELECT COMM_SF, COMM_TYPE FROM COMM_SF_INFO   WHERE COMM_ID  ? ORDER BY COMM_TYPE;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = " SELECT COMM_SF, COMM_MTH FROM COMM_MTH_INFO   WHERE COMM_ID  ? ORDER BY COMM_SF;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitMethods",ps);
		sql = " SELECT COMM_DATE, MSG, COMMITTER FROM COMM_INFO   WHERE COMM_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitInfo",ps);
		sql = " SELECT count(COMM_ID) FROM COMM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = " SELECT COMM_ID, COMM_DATE, MSG, COMMITTER FROM COMM_INFO   ORDER BY COMM_DATE;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllCommitInfos",ps);
		sql = " SELECT COMM_ID, COMM_DATE, MSG, COMMITTER FROM COMM_INFO   ORDER BY COMM_DATE;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = " DELETE FROM COMM_SF_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCommitFiles",ps);
		sql = " DELETE FROM COMM_MTH_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllCommitMethodInfo",ps);
		sql = " SELECT COMM_SF, COMM_MTH FROM COMM_MTH_INFO   WHERE COMM_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getFixedCommitInfo",ps);
		sql = " DELETE FROM EXP_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertExperimentResult",ps);
		sql = " INSERT INTO EXP_INFO (TOP1, TOP5, TOP10, TOP1_RATE, TOP5_RATE, TOP10_RATE, MRR, MAP, PROD_NAME, ALG_NAME, ALG_DESC, ALPHA, BETA, GAMMA, PAST_DAYS, EXP_DATE)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertExperimentResult",ps);
		sql = "if (fileName.contains(.java)) {} else if (-1 ! fixedFileName.lastIndexOf(org.osgi)) {fixedFileName  fixedFileName.substring(fixedFileName.lastIndexOf(org.osgi), fixedFileName.length());";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertAnalysisVaule",ps);
		sql = "sql  DELETE FROM INT_MTH_ANALYSIS;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("fixFileName",ps);
		sql = "resultValue.setMethodID(rs.getInt(MTH_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMethodAnalysisValues",ps);
		sql = "resultValue.setSourceFileVersionID(rs.getInt(SF_VER_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAnalysisValues",ps);
		sql = "resultValue.setFileName(rs.getString(SF_NAME));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugLocatorRankedValues",ps);
		sql = "resultValue.setSourceFileVersionID(rs.getInt(SF_VER_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBliaSourceFileRankedValues",ps);
		sql = "resultValue.setSourceFileVersionID(rs.getInt(SF_VER_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMiddleSourceFileRankedValues",ps);
		sql = "resultValue.setMethodID(rs.getInt(MTH_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBliaMethodRankedValues",ps);
		sql = " INSERT INTO INT_ANALYSIS (BUG_ID, SF_VER_ID, VSM_SCORE, SIMI_SCORE, BL_SCORE, STRACE_SCORE, COMM_SCORE, MID_SF_SCORE, BLIA_SF_SCORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertAnalysisVaule",ps);
		sql = " INSERT INTO INT_MTH_ANALYSIS (BUG_ID, MTH_ID, VSM_SCORE, COMM_SCORE, BLIA_MTH_SCORE) VALUES (?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertMethodAnalysisVaule",ps);
		sql = " UPDATE INT_ANALYSIS SET VSM_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateVsmScore",ps);
		sql = " UPDATE INT_MTH_ANALYSIS SET VSM_SCORE  ? WHERE BUG_ID  ? AND MTH_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMethodVsmScore",ps);
		sql = " UPDATE INT_ANALYSIS SET SIMI_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateSimilarScore",ps);
		sql = " UPDATE INT_ANALYSIS SET BL_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateBugLocatorScore",ps);
		sql = " UPDATE INT_ANALYSIS SET BL_SCORE  ?, MID_SF_SCORE  ?, BLIA_SF_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateBliaSourceFileScore",ps);
		sql = " UPDATE INT_MTH_ANALYSIS SET BLIA_MTH_SCORE  ? WHERE BUG_ID  ? AND MTH_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateBliaMethodScore",ps);
		sql = " UPDATE INT_ANALYSIS SET STRACE_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateStackTraceScore",ps);
		sql = " UPDATE INT_ANALYSIS SET MID_SF_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateMiddleSourceFileScore",ps);
		sql = " UPDATE INT_ANALYSIS SET COMM_SCORE  ? WHERE BUG_ID  ? AND SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateCommitLogScore",ps);
		sql = "case aspectj:case eclipse:if (-1 ! fixedFileName.lastIndexOf(org.eclipse)) {fixedFileName  fixedFileName.substring(fixedFileName.lastIndexOf(org.eclipse), fixedFileName.length());";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("fixFileName",ps);
		sql = " SELECT A.MTH_ID, A.VSM_SCORE, A.COMM_SCORE, A.BLIA_MTH_SCORE FROM INT_MTH_ANALYSIS A  WHERE A.BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMethodAnalysisValues",ps);
		sql = " SELECT A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.COMM_SCORE, A.MID_SF_SCORE, A.BLIA_SF_SCORE FROM INT_ANALYSIS A  WHERE A.BUG_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAnalysisValues",ps);
		sql = " SELECT C.SF_NAME, B.VER, A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.BLIA_SF_SCORE FROM INT_ANALYSIS A, SF_VER_INFO B, SF_INFO C  WHERE A.BUG_ID  ? AND A.SF_VER_ID  B.SF_VER_ID AND B.SF_ID  C.SF_ID AND A.BL_SCORE ! 0 ORDER BY A.BL_SCORE DESC ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBugLocatorRankedValues",ps);
		sql = " SELECT A.SF_VER_ID, A.BLIA_SF_SCORE FROM INT_ANALYSIS A  WHERE A.BUG_ID  ? AND A.BLIA_SF_SCORE ! 0  ORDER BY A.BLIA_SF_SCORE DESC ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBliaSourceFileRankedValues",ps);
		sql = " SELECT A.SF_VER_ID, A.MID_SF_SCORE FROM INT_ANALYSIS A  WHERE A.BUG_ID  ? AND A.MID_SF_SCORE ! 0  ORDER BY A.MID_SF_SCORE DESC ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMiddleSourceFileRankedValues",ps);
		sql = " SELECT A.MTH_ID, A.BLIA_MTH_SCORE FROM INT_MTH_ANALYSIS A  WHERE A.BUG_ID  ? AND A.BLIA_MTH_SCORE ! 0  ORDER BY A.BLIA_MTH_SCORE DESC ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getBliaMethodRankedValues",ps);
		sql = "returnValue  rs.getInt(MTH_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertMethod",ps);
		sql = "Method method  new Method(rs.getInt(MTH_ID), sourceFileVersionID,rs.getString(MTH_NAME), rs.getString(RET_TYPE), rs.getString(PARAMS),rs.getString(HASH_KEY));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMethods",ps);
		sql = "int sourceFileVersionID  rs.getInt(SF_VER_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllMethods",ps);
		sql = "";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllMethods",ps);
		sql = " INSERT INTO MTH_INFO (SF_VER_ID, MTH_NAME, RET_TYPE, PARAMS, HASH_KEY) VALUES (?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertMethod",ps);
		sql = " SELECT MTH_ID FROM MTH_INFO  WHERE HASH_KEY  ? AND MTH_NAME  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMethodID",ps);
		sql = " SELECT MTH_ID, MTH_NAME, RET_TYPE, PARAMS, HASH_KEY FROM MTH_INFO WHERE SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getMethods",ps);
		sql = " SELECT MTH_ID, SF_VER_ID, MTH_NAME, RET_TYPE, PARAMS, HASH_KEY FROM MTH_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllMethods",ps);
		sql = " DELETE FROM MTH_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllMethods",ps);
		sql = "fileInfo.put(rs.getString(SF_NAME), rs.getInt(SF_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertSourceFile",ps);
		sql = "versions.put(rs.getString(VER), rs.getTimestamp(REL_DATE));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileCount",ps);
		sql = "sourceFilePath  rs.getString(SF_PATH);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFilePath",ps);
		sql = "returnValue  rs.getInt(SF_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = "returnValue  rs.getInt(SF_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = "returnValue  rs.getInt(SF_VER_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = "returnValue  rs.getInt(SF_VER_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = "sourceFileNames.add(rs.getString(SF_NAME));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileNames",ps);
		sql = "sourceFileName  rs.getString(SF_NAME);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileName",ps);
		sql = "String classNameWithExtension  rs.getString(CLS_NAME);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getClassNames",ps);
		sql = "corpus.setContent(rs.getString(COR));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStructuredCorpusSet",ps);
		sql = "corpus.setContent(rs.getString(COR));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCorpus",ps);
		sql = "norm  rs.getDouble(COR_NORM);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue",ps);
		sql = "corpus.setContentNorm(rs.getDouble(COR_NORM));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue",ps);
		sql = "sourceFileVersionIDs.put(rs.getString(SF_NAME), rs.getInt(SF_VER_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = "totalCorpusLengths.put(rs.getString(SF_NAME), rs.getInt(TOT_CNT));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTotalCorpusLengths",ps);
		sql = "lengthScores.put(rs.getString(SF_NAME), rs.getDouble(LEN_SCORE));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateLengthScore",ps);
		sql = "lengthScore  rs.getDouble(LEN_SCORE);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getLengthScore",ps);
		sql = "fileInfo.put(rs.getString(TERM), rs.getInt(SF_TERM_ID));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertTerm",ps);
		sql = "returnValue  rs.getInt(SF_TERM_ID);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermID",ps);
		sql = "String sourceFilename  rs.getString(SF_NAME);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertImportedClasses",ps);
		sql = "importedClasses.add(rs.getString(IMP_CLASS));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getImportedClasses",ps);
		sql = "returnValue.setTermCount(rs.getInt(TERM_CNT));";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertTerm",ps);
		sql = "String term  rs.getString(TERM);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);
		sql = "String term  rs.getString(TERM);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);
		sql = " INSERT INTO SF_INFO (SF_NAME, CLS_NAME) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertSourceFile",ps);
		sql = " INSERT INTO SF_INFO (SF_NAME, CLS_NAME) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = " DELETE FROM SF_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = " SELECT SF_NAME, SF_ID FROM SF_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFiles",ps);
		sql = " SELECT COUNT(SF_VER_ID) FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID  B.SF_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileCount",ps);
		sql = " INSERT INTO VER_INFO (VER, REL_DATE) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertVersion",ps);
		sql = " DELETE FROM VER_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllVersions",ps);
		sql = " SELECT VER, REL_DATE FROM VER_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getVersions",ps);
		sql = " SELECT SF_PATH FROM SF_INFO  WHERE SF_NAME  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFilePath",ps);
		sql = " SELECT SF_ID FROM SF_INFO  WHERE SF_NAME  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = " SELECT SF_ID FROM SF_INFO  WHERE SF_NAME  ? AND CLS_NAME  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileID",ps);
		sql = " SELECT SF_VER_ID FROM SF_VER_INFO  WHERE SF_ID  ? AND VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " SELECT B.SF_VER_ID FROM SF_INFO A, SF_VER_INFO B  WHERE A.SF_NAME  ? AND B.VER  ? AND A.SF_ID  B.SF_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " SELECT A.SF_NAME FROM SF_INFO A, SF_VER_INFO B  WHERE B.VER  ? AND A.SF_ID  B.SF_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileNames",ps);
		sql = "String sourceFileName  ;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileName",ps);
		sql = " SELECT A.CLS_NAME, A.SF_NAME FROM SF_INFO A, SF_VER_INFO B  WHERE B.VER  ? AND A.SF_ID  B.SF_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getClassNames",ps);
		sql = " INSERT INTO SF_VER_INFO (SF_ID, VER, CLS_COR, MTH_COR, VAR_COR, CMT_COR, TOT_CNT, LEN_SCORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertStructuredCorpusSet",ps);
		sql = " INSERT INTO SF_VER_INFO (SF_ID, VER, COR, CLS_COR, MTH_COR, VAR_COR, CMT_COR, TOT_CNT, LEN_SCORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " DELETE FROM SF_VER_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " UPDATE SF_VER_INFO  SET COR null,  CLS_CORnull,  MTH_CORnull, VAR_CORnull, CMT_CORnull, TOT_CNTnull, LEN_SCOREnull, COR_NORMnull, CLS_COR_NORMnull, MTH_COR_NORMnull, VAR_COR_NORMnull, CMT_COR_NORMnull  WHERE VER <> ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllCorpuses",ps);
		sql = " SELECT A.SF_NAME, B.COR, B.CLS_COR, B.MTH_COR, B.VAR_COR, B.CMT_COR  FROM SF_INFO A, SF_VER_INFO B  WHERE A.SF_ID  B.SF_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCorpusMap",ps);
		sql = " SELECT COR, CLS_COR, MTH_COR, VAR_COR, CMT_COR, COR_NORM, CLS_COR_NORM, MTH_COR_NORM, VAR_COR_NORM, CMT_COR_NORM   FROM SF_VER_INFO B  WHERE SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getCorpus",ps);
		sql = " SELECT COR_NORM  FROM SF_VER_INFO B  WHERE SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue",ps);
		sql = " SELECT COR_NORM, CLS_COR_NORM, MTH_COR_NORM, VAR_COR_NORM, CMT_COR_NORM  FROM SF_VER_INFO B  WHERE SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getNormValue",ps);
		sql = " SELECT A.SF_NAME, B.SF_VER_ID  FROM SF_INFO A, SF_VER_INFO B  WHERE A.SF_ID  B.SF_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " SELECT A.SF_NAME, B.TOT_CNT  FROM SF_INFO A, SF_VER_INFO B  WHERE A.SF_ID  B.SF_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTotalCorpusLengths",ps);
		sql = " UPDATE SF_VER_INFO SET LEN_SCORE  ?  WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE  A.SF_ID  B.SF_ID  AND A.SF_NAME  ? AND B.VER  ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateLengthScore",ps);
		sql = " UPDATE SF_VER_INFO SET TOT_CNT  ?  WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID  B.SF_ID  AND A.SF_NAME  ? AND B.VER  ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateTotalCoupusCount",ps);
		sql = " UPDATE SF_VER_INFO SET COR_NORM  ?  WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID  B.SF_ID  AND A.SF_NAME  ? AND B.VER  ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateNormValue",ps);
		sql = " UPDATE SF_VER_INFO SET COR_NORM  ?, CLS_COR_NORM  ?, MTH_COR_NORM  ?, VAR_COR_NORM  ?, CMT_COR_NORM  ?  WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID  B.SF_ID  AND A.SF_NAME  ? AND B.VER  ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateNormValue",ps);
		sql = " SELECT A.SF_NAME, B.LEN_SCORE  FROM SF_INFO A, SF_VER_INFO B  WHERE A.SF_ID  B.SF_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getLengthScores",ps);
		sql = " SELECT LEN_SCORE  FROM SF_VER_INFO  WHERE SF_VER_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getLengthScore",ps);
		sql = " INSERT INTO SF_TERM_INFO (TERM) VALUES (?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertTerm",ps);
		sql = " DELETE FROM SF_TERM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllTerms",ps);
		sql = " SELECT TERM, SF_TERM_ID FROM SF_TERM_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);
		sql = " SELECT SF_TERM_ID FROM SF_TERM_INFO WHERE TERM  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermID",ps);
		sql = " INSERT INTO SF_IMP_INFO (SF_VER_ID, IMP_CLASS) VALUES (?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertImportedClasses",ps);
		sql = " DELETE FROM SF_IMP_INFO;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("deleteAllImportedClasses",ps);
		sql = " SELECT A.SF_NAME, C.IMP_CLASS  FROM SF_INFO A, SF_VER_INFO B, SF_IMP_INFO C  WHERE A.SF_ID  B.SF_ID AND  B.SF_VER_ID  C.SF_VER_ID AND B.VER  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getAllImportedClasses",ps);
		sql = " SELECT C.IMP_CLASS  FROM SF_INFO A, SF_VER_INFO B, SF_IMP_INFO C  WHERE A.SF_ID  B.SF_ID AND  B.SF_VER_ID  C.SF_VER_ID AND B.VER  ? AND A.SF_NAME  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getImportedClasses",ps);
		sql = " INSERT INTO SF_TERM_WGT (SF_VER_ID, SF_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF)  VALUES (?, ?, ?, ?, ?, ?);";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("insertTerm",ps);
		sql = " UPDATE SF_TERM_WGT SET TERM_CNT  ?, INV_DOC_CNT  ?, TF  ?, IDF  ? WHERE SF_VER_ID  ? AND SF_TERM_ID  ?;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("updateTermWeight",ps);
		sql = " DELETE FROM SF_TERM_WGT;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getSourceFileVersionID",ps);
		sql = " SELECT D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF FROM SF_INFO A, SF_VER_INFO B, SF_TERM_INFO C, SF_TERM_WGT D  WHERE A.SF_NAME  ? AND A.SF_ID  B.SF_ID AND  B.VER  ? AND B.SF_VER_ID  D.SF_VER_ID AND C.TERM  ? AND  C.SF_TERM_ID  D.SF_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermWeight",ps);
		sql = " SELECT C.TERM, D.SF_VER_ID, D.SF_TERM_ID, D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF FROM SF_INFO A, SF_VER_INFO B, SF_TERM_INFO C, SF_TERM_WGT D  WHERE A.SF_NAME  ? AND A.SF_ID  B.SF_ID AND  B.VER  ? AND B.SF_VER_ID  D.SF_VER_ID AND  C.SF_TERM_ID  D.SF_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);
		sql = " SELECT C.TERM, D.SF_VER_ID, D.SF_TERM_ID, D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF FROM SF_TERM_INFO C, SF_TERM_WGT D  WHERE D.SF_VER_ID  ? AND C.SF_TERM_ID  D.SF_TERM_ID;";
		ps = analysisDbConnection.prepareStatement(sql);
		stmtMap.put("getTermMap",ps);

	}
}
