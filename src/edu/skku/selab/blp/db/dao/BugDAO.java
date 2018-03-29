/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;

import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.common.Comment;
import edu.skku.selab.blp.common.Method;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.SimilarBugInfo;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugDAO extends BaseDAO {

	/**
	 * @throws Exception
	 */
	public BugDAO() throws Exception {
		super();
	}
	
	public int insertStructuredBug(Bug bug) {
		int returnValue = INVALID;
		
		// releaseDate format : "2004-10-18 17:40:00"
		try {
			
			ps = setOfDAO.stmtMap.get("insertStructuredBug");
			
			ps.setInt(1, bug.getID());
			ps.setString(2, bug.getOpenDateString());
			ps.setString(3, bug.getFixedDateString());
			BugCorpus bugCorpus = bug.getCorpus();
			ps.setString(4, bugCorpus.getSummaryPart());
			ps.setString(5, bugCorpus.getDescriptionPart());
			ps.setString(6, bug.getAllCommentsCorpus());
			ps.setInt(7, bug.getTotalCorpusCount());
			ps.setString(8, bug.getVersion());
			
			returnValue = ps.executeUpdate();
			
			ArrayList<String> stackTraceClasses = bug.getStackTraceClasses();
			if (null != stackTraceClasses) {
				for (int i = 0; i < stackTraceClasses.size(); i++) {
					insertStackTraceClass(bug.getID(), stackTraceClasses.get(i));				
				}
			}
		} catch (JdbcSQLException e) {
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return returnValue;
	}
	
	public int deleteAllBugs() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<Integer, Bug> getBugs() {
		HashMap<Integer, Bug> bugs = new HashMap<Integer, Bug>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getBugs");
			
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));
				
				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);
				
				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.put(bug.getID(), bug);
			}
			
			Iterator<Integer> bugsIter = bugs.keySet().iterator();
			while (bugsIter.hasNext()) {
				int bugID = bugsIter.next();
				bug = bugs.get(bugID);
				bug.setStackTraceClasses(getStackTraceClasses(bugID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}
	
	
	public ArrayList<Bug> getAllBugs(boolean orderedByFixedDate) {
		ArrayList<Bug> bugs = new ArrayList<Bug>();
		
		String sql = "SELECT BUG_ID, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, CMT_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO ";
		
		if (orderedByFixedDate) {
			sql += "ORDER BY FIXED_DATE";
		}
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setCommentPart(rs.getString("CMT_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.add(bug);
			}
			
			for (int i = 0; i < bugs.size(); i++) {
				bug = bugs.get(i);
				bug.setStackTraceClasses(getStackTraceClasses(bug.getID()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}
	
	public HashMap<Integer, Double> getAllNorms() {
		HashMap<Integer, Double> bugNormMap = new HashMap<Integer, Double>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			

			rs = ps.executeQuery();
			while (rs.next()) {
				int bugID = rs.getInt("BUG_ID");
				double bugNorm = rs.getDouble("COR_NORM");
				bugNormMap.put(bugID, bugNorm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugNormMap;	
	}

	public int getBugCountWithDate(String dateString) {
		
		int count = 0;
		try {
			
			ps = setOfDAO.stmtMap.get("getBugCountWithDate");
			
			ps.setString(1, dateString);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;	
	}
	
	public ArrayList<Bug> getPreviousFixedBugs(String fixedDateString, int exceptedBugID) {
		ArrayList<Bug> bugs = new ArrayList<Bug>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getPreviousFixedBugs");
			
			ps.setString(1, fixedDateString);
			ps.setInt(2, exceptedBugID);
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.add(bug);
			}
			
			for (int i = 0; i < bugs.size(); i++) {
				bug = bugs.get(i);
				bug.setStackTraceClasses(getStackTraceClasses(bug.getID()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}
	
	public Bug getBug(int bugID) {
		Bug bug = null;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bug = new Bug();
				bug.setID(bugID);
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
			}
			
			bug.setStackTraceClasses(getStackTraceClasses(bugID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bug;	
	}
	
	public Bug getDetailBug(int bugID) {		
		Bug bug = new Bug();
		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bug.setID(rs.getInt("BUG_ID"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setCommentPart(rs.getString("CMT_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
			}
			
			bug.setStackTraceClasses(getStackTraceClasses(bugID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bug;	
		
	}

	
	public int insertBugTerm(String term) throws SQLException {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			
			ps.setString(1, term);
			
			returnValue = ps.executeUpdate();
			
			String sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ?";

			ps = analysisDbConnection.prepareStatement(sql);
			
			ps.setString(1, term);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("BUG_TERM_ID");	
			}
		} catch (JdbcSQLException e) {
			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}else{
					
				String sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ?";

				ps = analysisDbConnection.prepareStatement(sql);
				ps.setString(1, term);
				
				rs = ps.executeQuery();
				if (rs.next()) {
					returnValue = rs.getInt("BUG_TERM_ID");	
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllTerms() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllTerms");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	/**
	 * Get <Source file name, Corpus sets> with product name and version
	 * 
	 * @return HashMap<Integer, String>	<Source file name, Corpus sets>
	 */
	public HashMap<Integer, String> getCorpusMap() {
		HashMap<Integer, String> corpusMap = new HashMap<Integer, String>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getCorpusMap");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				corpusMap.put(rs.getInt("BUG_ID"), rs.getString("COR"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpusMap;
	}
	
	/**
	 * Get norm value with bug ID
	 * 
	 * @param bugID		Bug ID
	 * @return double 	corpus norm value
	 */
	public double getNormValue(int bugID) {
		double norm = 0;
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getNormValue");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				norm = rs.getDouble("COR_NORM");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return norm;
	}


	
	public HashMap<String, Integer> getTermMap() {
		HashMap<String, Integer> termMap = new HashMap<String, Integer>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermMap");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				termMap.put(rs.getString("TERM"), rs.getInt("BUG_TERM_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return termMap;
	}
	
	public int getAllTermCount() {
		
		int allTermCount = 0;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getAllTermCount");
			
			rs = ps.executeQuery();
			if (rs.next()) {
				allTermCount = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allTermCount;
	}
	
	public int getBugCount() {
		
		int bugCount = 0;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bugCount = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugCount;
	}
	
	public int getSfTermID(String term) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSfTermID");
			
			ps.setString(1, term);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("SF_TERM_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public HashMap<String, AnalysisValue> getSfTermMap(int bugID) {
		
		HashMap<String, AnalysisValue> sourceFileTermMap = null;
		try {
			
			ps = setOfDAO.stmtMap.get("getSfTermMap");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if (null == sourceFileTermMap) {
					sourceFileTermMap = new HashMap<String, AnalysisValue>();
				}
				
				AnalysisValue sourceFileTermWeight = new AnalysisValue();
				sourceFileTermWeight.setTf(rs.getDouble("TF"));
				sourceFileTermWeight.setIdf(rs.getDouble("IDF"));
				
				String term = rs.getString("TERM");
				sourceFileTermMap.put(term, sourceFileTermWeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileTermMap;	
	}
	
	public int insertStackTraceClass(int bugID, String className) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertStackTraceClass");
			
			ps.setInt(1, bugID);
			ps.setString(2, className);
			
			returnValue = ps.executeUpdate();
		} catch (JdbcSQLException e) {
			e.printStackTrace();
			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllStackTraceClasses() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllStackTraceClasses");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	public ArrayList<String> getStackTraceClasses(int bugID) {
		ArrayList<String> stackTraceClasses = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getStackTraceClasses");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == stackTraceClasses) {
					stackTraceClasses = new ArrayList<String>();
				}
				
				stackTraceClasses.add(rs.getString("STRACE_CLASS"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stackTraceClasses;
	}
	
	public int insertBugSfTermWeight(AnalysisValue bugSfTermWeight) {
		int termID = getSfTermID(bugSfTermWeight.getTerm());
		
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertBugSfTermWeight");
			
			ps.setInt(1, bugSfTermWeight.getID());
			ps.setInt(2, termID);
			ps.setInt(3, bugSfTermWeight.getTermCount());
			ps.setInt(4, bugSfTermWeight.getInvDocCount());
			ps.setDouble(5, bugSfTermWeight.getTf());
			ps.setDouble(6, bugSfTermWeight.getIdf());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			
			try{
				String sql = "UPDATE BUG_SF_TERM_WGT SET (TERM_CNT, INV_DOC_CNT, TF, IDF) " +
						"= (?, ?, ?, ?) WHERE BUG_ID = ? AND SF_TERM_ID = ?";
			

				ps = analysisDbConnection.prepareStatement(sql);		
				ps.setInt(1, bugSfTermWeight.getTermCount());
				ps.setInt(2, bugSfTermWeight.getInvDocCount());
				ps.setDouble(3, bugSfTermWeight.getTf());
				ps.setDouble(4, bugSfTermWeight.getIdf());
				ps.setInt(5, bugSfTermWeight.getID());
				ps.setInt(6, termID);
				
				returnValue = ps.executeUpdate();
				
			}catch(Exception e2){
				
				e2.printStackTrace();
			}
		}
		
		return returnValue;
	}
	
	public int insertBugMthTermWeight(AnalysisValue bugMthTermWeight) {
		int termID = getSfTermID(bugMthTermWeight.getTerm());
		
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertBugMthTermWeight");
			
			ps.setInt(1, bugMthTermWeight.getID());
			ps.setInt(2, termID);
			ps.setInt(3, bugMthTermWeight.getTermCount());
			ps.setInt(4, bugMthTermWeight.getInvDocCount());
			ps.setDouble(5, bugMthTermWeight.getTf());
			ps.setDouble(6, bugMthTermWeight.getIdf());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllBugSfTermWeights() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllBugSfTermWeights");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllBugMthTermWeights() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllBugMthTermWeights");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getBugSfTermWeight(int bugID, String term) {
		AnalysisValue termWeight = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			ps.setInt(1, bugID);
			ps.setString(2, term);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				termWeight = new AnalysisValue();
				
				termWeight.setID(bugID);
				termWeight.setTerm(term);
				termWeight.setTermCount(rs.getInt("TERM_CNT"));
				termWeight.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				termWeight.setTf(rs.getDouble("TF"));
				termWeight.setIdf(rs.getDouble("IDF"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termWeight;
	}
	
	public AnalysisValue getBugMthTermWeight(int bugID, String term) {
		AnalysisValue termWeight = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			ps.setInt(1, bugID);
			ps.setString(2, term);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				termWeight = new AnalysisValue();
				
				termWeight.setID(bugID);
				termWeight.setTerm(term);
				termWeight.setTermCount(rs.getInt("TERM_CNT"));
				termWeight.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				termWeight.setTf(rs.getDouble("TF"));
				termWeight.setIdf(rs.getDouble("IDF"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termWeight;
	}
	
	public int getBugTermID(String term) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			ps.setString(1, term);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("BUG_TERM_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int insertBugTermWeight(AnalysisValue analysisValue) {
		int termID = analysisValue.getTermID();
		if (INVALID == termID) {
			termID = getBugTermID(analysisValue.getTerm());
		}
		
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertBugTerm");
			
			ps.setInt(1, analysisValue.getID());
			ps.setInt(2, termID);
			ps.setDouble(3, analysisValue.getTermWeight());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			try{
				String sql = "UPDATE BUG_TERM_WGT SET (TW) = (?) " +
						"WHERE BUG_ID = ? AND BUG_TERM_ID = ?";				
				

				ps = analysisDbConnection.prepareStatement(sql);
				ps.setDouble(1, analysisValue.getTermWeight());
				ps.setInt(2, analysisValue.getID());
				ps.setInt(3, termID);
			}catch(Exception e2){
				e2.printStackTrace();
			}
			
		}
		
		return returnValue;
	}
	
	public int deleteAllBugTermWeights() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllBugTermWeights");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getBugTermWeight(int bugID, String term) {
		AnalysisValue returnValue = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			ps.setInt(1, bugID);
			ps.setString(2, term);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = new AnalysisValue();
				
				returnValue.setID(bugID);
				returnValue.setTerm(term);
				returnValue.setTermWeight(rs.getDouble("TW"));				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public ArrayList<AnalysisValue> getBugTermWeightList(int bugID) {
		ArrayList<AnalysisValue> bugAnalysisValues = null;
		AnalysisValue bugTermWeight = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getBug");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == bugAnalysisValues) {
					bugAnalysisValues = new ArrayList<AnalysisValue>();
				}
				
				bugTermWeight = new AnalysisValue();
				bugTermWeight.setID(bugID);
				bugTermWeight.setTerm(rs.getString("TERM"));
				bugTermWeight.setTermID(rs.getInt("BUG_TERM_ID"));
				bugTermWeight.setTermWeight(rs.getDouble("TW"));
				
				bugAnalysisValues.add(bugTermWeight);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bugAnalysisValues;
	}
	
	public int insertBugFixedFileInfo(int bugID, String fileName, String version) {
		int returnValue = INVALID;
		
		try {
			SourceFileDAO2 sourceFileDAO = new SourceFileDAO2();
			int fixedSourceFileID = sourceFileDAO.getSourceFileVersionID(fileName, version);

			
			ps = setOfDAO.stmtMap.get("insertBugFixedFileInfo");
			
			ps.setInt(1, bugID);
			ps.setInt(2, fixedSourceFileID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	public int insertBugFixedMethodInfo(int bugID, Method method) {
		int returnValue = INVALID;
		
		try {
			MethodDAO2 methodDAO = new MethodDAO2();
			int fixedMethodID = methodDAO.getMethodID(method);;
			
			if (INVALID != fixedMethodID) {
				
			ps = setOfDAO.stmtMap.get("insertBugFixedMethodInfo");
				
				ps.setInt(1, bugID);
				ps.setInt(2, fixedMethodID);
				
				returnValue = ps.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	
	public int deleteAllBugFixedInfo() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllBugFixedInfo");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String sql = "DELETE FROM BUG_FIX_MTH_INFO";
		returnValue = INVALID;
		try {		

			ps = analysisDbConnection.prepareStatement(sql);
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashSet<SourceFile> getFixedFiles(int bugID) {
		HashSet<SourceFile> fixedFiles = null;
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getFixedFiles");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == fixedFiles) {
					fixedFiles = new HashSet<SourceFile>();
				}

				SourceFile sourceFile = new SourceFile();
				sourceFile.setName(rs.getString("SF_NAME"));
				sourceFile.setVersion(rs.getString("VER"));
				sourceFile.setSourceFileVersionID(rs.getInt("FIXED_SF_VER_ID"));

				fixedFiles.add(sourceFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fixedFiles;
	}
	
	public HashSet<Method> getFixedMethods(int bugID) {
		HashSet<Method> fixedMethods = null;
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getFixedMethods");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == fixedMethods) {
					fixedMethods = new HashSet<Method>();
				}

				Method method = new Method();
				method.setID(rs.getInt("MTH_ID"));
				method.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				method.setName(rs.getString("MTH_NAME"));
				method.setReturnType(rs.getString("RET_TYPE"));
				method.setParams(rs.getString("PARAMS"));
				method.setHashKey(rs.getString("HASH_KEY"));

				fixedMethods.add(method);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fixedMethods;
	}
	
	public int insertSimilarBugInfo(int bugID, int similarBugID, double similarityScore) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertSimilarBugInfo");
			
			ps.setInt(1, bugID);
			ps.setInt(2, similarBugID);
			ps.setDouble(3, similarityScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllSimilarBugInfo() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllSimilarBugInfo");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashSet<SimilarBugInfo> getSimilarBugInfos(int bugID) {
		HashSet<SimilarBugInfo> similarBugInfos = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getSimilarBugInfos");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == similarBugInfos) {
					similarBugInfos = new HashSet<SimilarBugInfo>();
				}

				SimilarBugInfo similarBugInfo = new SimilarBugInfo();
				similarBugInfo.setSimilarBugID(rs.getInt("SIMI_BUG_ID"));
				similarBugInfo.setSimilarityScore(rs.getDouble("SIMI_BUG_SCORE"));

				similarBugInfos.add(similarBugInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return similarBugInfos;
	}
	
	public int updateTotalTermCount(int bugID, int totalTermCount) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateTotalTermCount");
			
			ps.setInt(1, totalTermCount);
			ps.setInt(2, bugID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateMethodTotalTermCount(int bugID, int totalTermCount) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateMethodTotalTermCount");
			
			ps.setInt(1, totalTermCount);
			ps.setInt(2, bugID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValues(int bugID, double corpusNorm, double summaryCorpusNorm, double descriptionCorpusNorm) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateNormValues");
			
			ps.setDouble(1, corpusNorm);
			ps.setDouble(2, summaryCorpusNorm);
			ps.setDouble(3, descriptionCorpusNorm);
			
			ps.setInt(4, bugID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateMthNormValues(int bugID, double methodNorm) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateMthNormValues");
			
			ps.setDouble(1, methodNorm);
			ps.setInt(2, bugID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int insertComment(int bugID, Comment comment) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertComment");
			
			ps.setInt(1, bugID);
			ps.setInt(2, comment.getID());
			ps.setString(3, comment.getAuthor());
			ps.setString(4, comment.getCommentedDateString());
			ps.setString(5, comment.getCommentCorpus());
			
			returnValue = ps.executeUpdate();
		} catch (JdbcSQLException e) {
			e.printStackTrace();
			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllComments() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllComments");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	public ArrayList<Comment> getComments(int bugID) {
		ArrayList<Comment> comments = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getComments");
			
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == comments) {
					comments = new ArrayList<Comment>();
				}
				
				Comment comment = new Comment(rs.getInt("CMT_ID"), rs.getTimestamp("CMT_DATE"),
						rs.getString("ATHR"), rs.getString("CMT_COR"));
				comments.add(comment);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return comments;
	}
}
