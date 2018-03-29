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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;

import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.db.AnalysisValue;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class SourceFileDAO extends BaseDAO {
	final static public double INIT_LENGTH_SCORE = 0.0;
	final static public int INIT_TOTAL_COUPUS_COUNT = 0;
	
	
	/**
	 * @throws Exception
	 */
	public SourceFileDAO() throws Exception {
		super();
	}
	
	public int insertSourceFile(String fileName) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertSourceFile");
			
			ps.setString(1, fileName);
			ps.setString(2, fileName);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileID(fileName);
		} 

		return returnValue;
	}
	
	public int insertSourceFile(String fileName, String className) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileID");
			
			ps.setString(1, fileName);
			ps.setString(2, className);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			if(ErrorCode.DUPLICATE_KEY_1 == ((SQLException) e).getErrorCode()) {
				returnValue = getSourceFileID(fileName, className);
			}else 
				e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileID(fileName, className);
		} 

		return returnValue;
	}
	
	public int deleteAllSourceFiles() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileID");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Integer> getSourceFiles() {
		HashMap<String, Integer> fileInfo = new HashMap<String, Integer>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFiles");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				fileInfo.put(rs.getString("SF_NAME"), rs.getInt("SF_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	public int getSourceFileCount(String version) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileCount");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public int insertVersion(String version, String releaseDate) {
		int returnValue = INVALID;
		
		// releaseDate format : "2004-10-18 17:40:00"
		try {
			
			ps = setOfDAO.stmtMap.get("insertVersion");
			
			ps.setString(1, version);
			ps.setString(2, releaseDate);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllVersions() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllVersions");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Date> getVersions() {
		HashMap<String, Date> versions = new HashMap<String, Date>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getVersions");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				versions.put(rs.getString("VER"), rs.getTimestamp("REL_DATE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versions;	
	}
	
	public String getSourceFilePath(String fileName) {
		String sourceFilePath = null;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFilePath");
			
			ps.setString(1, fileName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				sourceFilePath = rs.getString("SF_PATH");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFilePath;	
	}
	
	public int getSourceFileID(String fileName) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileID");
			
			ps.setString(1, fileName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int getSourceFileID(String fileName, String className) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileID");
			
			ps.setString(1, fileName);
			ps.setString(2, className);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int getSourceFileVersionID(int sourceFileID, String version) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_VER_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}

	public int getSourceFileVersionID(String fileName, String version) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			ps.setString(1, fileName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_VER_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public HashSet<String> getSourceFileNames(String version) {
		HashSet<String> sourceFileNames = null;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileNames");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == sourceFileNames) {
					sourceFileNames = new HashSet<String>();
				}
				sourceFileNames.add(rs.getString("SF_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileNames;	
	}
	
	public String getSourceFileName(int sourceFileVersionID) {
		String sourceFileName  = "";
		String sql = "SELECT A.SF_NAME FROM SF_INFO A, SF_VER_INFO B " +
				"WHERE B.SF_VER_ID = ? AND A.SF_ID = B.SF_ID";
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileName");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				sourceFileName = rs.getString("SF_NAME");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileName;			
	}
	
	public HashMap<String, String> getClassNames(String version) {
		HashMap<String, String> sourceFileNames = null;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getClassNames");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == sourceFileNames) {
					sourceFileNames = new HashMap<String, String>();
				}
				
				String classNameWithExtension = rs.getString("CLS_NAME");
				String className = classNameWithExtension.substring(0, classNameWithExtension.lastIndexOf("."));
				String fileName = rs.getString("SF_NAME");
				sourceFileNames.put(className, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileNames;	
	}
	
	public int insertStructuredCorpusSet(int sourceFileID, String version, SourceFileCorpus corpus, int totalCorpusCount, double lengthScore) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertStructuredCorpusSet");
			
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			ps.setString(3, corpus.getClassPart());
			ps.setString(4, corpus.getMethodPart());
			ps.setString(5, corpus.getVariablePart());
			ps.setString(6, corpus.getCommentPart());
			ps.setInt(7, totalCorpusCount);
			ps.setDouble(8, lengthScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileVersionID(sourceFileID, version);
		} 

		return returnValue;
	}

	public int insertCorpusSet(int sourceFileID, String version, SourceFileCorpus corpus, int totalCorpusCount, double lengthScore) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			ps.setString(3, corpus.getContent());
			ps.setString(4, corpus.getClassPart());
			ps.setString(5, corpus.getMethodPart());
			ps.setString(6, corpus.getVariablePart());
			ps.setString(7, corpus.getCommentPart());
			ps.setInt(8, totalCorpusCount);
			ps.setDouble(9, lengthScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileVersionID(sourceFileID, version);
		} 

		return returnValue;
	}
	
	public int deleteAllCorpuses() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllCorpusesByVersion(String version) {
				
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllCorpuses");
			
			ps.setString(1, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}


	/**
	 * Get <Source file name, CorpusMap> with product name and version
	 * 
	 * @param version		Version
	 * @return HashMap<String, SourceFileCorpus>	<Source file name, Source file corpus>
	 */
	public HashMap<String, SourceFileCorpus> getCorpusMap(String version) {
		HashMap<String, SourceFileCorpus> corpusSets = new HashMap<String, SourceFileCorpus>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getCorpusMap");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				SourceFileCorpus corpus = new SourceFileCorpus();
				corpus.setContent(rs.getString("COR"));
				corpus.setClassPart(rs.getString("CLS_COR"));
				corpus.setMethodPart(rs.getString("MTH_COR"));
				corpus.setVariablePart(rs.getString("VAR_COR"));
				corpus.setCommentPart(rs.getString("CMT_COR"));
				corpusSets.put(rs.getString("SF_NAME"), corpus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpusSets;
	}
	
	/**
	 * Get SourceFileCorpus with source file version ID
	 * 
	 * @param sourceFileVersionID	Source file version ID
	 * @return SourceFileCorpus		Source file corpus
	 */
	public SourceFileCorpus getCorpus(int sourceFileVersionID) {
		
		SourceFileCorpus corpus = null;
		try {
			
			ps = setOfDAO.stmtMap.get("getCorpus");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				corpus = new SourceFileCorpus();
				corpus.setContent(rs.getString("COR"));
				corpus.setClassPart(rs.getString("CLS_COR"));
				corpus.setMethodPart(rs.getString("MTH_COR"));
				corpus.setVariablePart(rs.getString("VAR_COR"));
				corpus.setCommentPart(rs.getString("CMT_COR"));
				corpus.setContentNorm(rs.getDouble("COR_NORM"));
				corpus.setClassCorpusNorm(rs.getDouble("CLS_COR_NORM"));
				corpus.setMethodCorpusNorm(rs.getDouble("MTH_COR_NORM"));
				corpus.setVariableCorpusNorm(rs.getDouble("VAR_COR_NORM"));
				corpus.setCommentCorpusNorm(rs.getDouble("CMT_COR_NORM"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return corpus;
	}
	
	public double getNormValue(int sourceFileVersionID) {
		
		double norm = 0;
		try {
			
			ps = setOfDAO.stmtMap.get("getNormValue");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				norm = rs.getDouble("COR_NORM");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return norm;
	}
	
	/**
	 * Get SourceFileCorpus with source file version ID
	 * 
	 * @param sourceFileVersionID	Source file version ID
	 * @return SourceFileCorpus		Source file corpus
	 */
	public SourceFileCorpus getNormValues(int sourceFileVersionID) {
		
		SourceFileCorpus corpus = null;
		try {
			
			ps = setOfDAO.stmtMap.get("getNormValue");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				corpus = new SourceFileCorpus();
				corpus.setContentNorm(rs.getDouble("COR_NORM"));
				corpus.setClassCorpusNorm(rs.getDouble("CLS_COR_NORM"));
				corpus.setMethodCorpusNorm(rs.getDouble("MTH_COR_NORM"));
				corpus.setVariableCorpusNorm(rs.getDouble("VAR_COR_NORM"));
				corpus.setCommentCorpusNorm(rs.getDouble("CMT_COR_NORM"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return corpus;
	}
	
	public HashMap<String, Integer> getSourceFileVersionIDs(String version) {
		HashMap<String, Integer> sourceFileVersionIDs = new HashMap<String, Integer>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				sourceFileVersionIDs.put(rs.getString("SF_NAME"), rs.getInt("SF_VER_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileVersionIDs;
	}
	
	public HashMap<String, Integer> getTotalCorpusLengths(String version) {
		HashMap<String, Integer> totalCorpusLengths = new HashMap<String, Integer>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getTotalCorpusLengths");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				totalCorpusLengths.put(rs.getString("SF_NAME"), rs.getInt("TOT_CNT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCorpusLengths;
	}
	
	public int updateLengthScore(String fileName, String version, double lengthScore) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateLengthScore");
			
			ps.setDouble(1, lengthScore);
			ps.setString(2, fileName);
			ps.setString(3, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateTotalCoupusCount(String fileName, String version, int totalCorpusCount) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateTotalCoupusCount");
			
			ps.setInt(1, totalCorpusCount);
			ps.setString(2, fileName);
			ps.setString(3, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValue(String fileName, String version, double corpusNorm) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateNormValue");
			
			ps.setDouble(1, corpusNorm);
			ps.setString(2, fileName);
			ps.setString(3, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValues(String fileName, String version,
			double corpusNorm, double classNorm, double methodNorm, double variableNorm, double commentNorm) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateNormValue");
			
			ps.setDouble(1, corpusNorm);
			ps.setDouble(2, classNorm);
			ps.setDouble(3, methodNorm);
			ps.setDouble(4, variableNorm);
			ps.setDouble(5, commentNorm);
			
			ps.setString(6, fileName);
			ps.setString(7, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Double> getLengthScores(String version) {
		HashMap<String, Double> lengthScores = new HashMap<String, Double>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getLengthScores");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lengthScores.put(rs.getString("SF_NAME"), rs.getDouble("LEN_SCORE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lengthScores;
	}
	
	public double getLengthScore(int sourceFileVersionID) {
		double lengthScore = INIT_LENGTH_SCORE;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getLengthScore");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				lengthScore = rs.getDouble("LEN_SCORE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lengthScore;
	}
	
	public int insertTerm(String term) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertTerm");
			
			ps.setString(1, term);
			
			returnValue = ps.executeUpdate();
		} catch (JdbcSQLException e) {			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}else{
				returnValue = 1;
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
	
	public HashMap<String, Integer> getTermMap() {
		HashMap<String, Integer> fileInfo = new HashMap<String, Integer>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermMap");
			
			
			rs = ps.executeQuery();
			while (rs.next()) {
				fileInfo.put(rs.getString("TERM"), rs.getInt("SF_TERM_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	public int getTermID(String term) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermID");
			
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
	
	public int insertImportedClasses(int sourceFileVersionID, ArrayList<String> importedClasses) {
		int returnValue = INVALID;
		
		for (int i = 0; i < importedClasses.size(); i++) {
			try {
				String importedClass = importedClasses.get(i);
				
			ps = setOfDAO.stmtMap.get("insertImportedClasses");
				
				ps.setInt(1, sourceFileVersionID);
				ps.setString(2, importedClass);
				
				returnValue = ps.executeUpdate();
			} catch (JdbcSQLException e) {
				e.printStackTrace();
				
				if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (INVALID == returnValue) {
				break;
			}
		}
		
		return returnValue;
	}
	
	public int deleteAllImportedClasses() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("deleteAllImportedClasses");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, ArrayList<String>> getAllImportedClasses(String version) {
		HashMap<String, ArrayList<String>> importedClassesMap = new HashMap<String, ArrayList<String>>();
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getAllImportedClasses");
			
			ps.setString(1, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				String sourceFilename = rs.getString("SF_NAME");
				if (importedClassesMap.containsKey(sourceFilename)) {
					ArrayList<String> importedClasses = importedClassesMap.get(sourceFilename);
					importedClasses.add(rs.getString("IMP_CLASS"));
				} else {
					ArrayList<String> importedClasses = new ArrayList<String>();
					importedClasses.add(rs.getString("IMP_CLASS"));
					
					importedClassesMap.put(sourceFilename, importedClasses);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return importedClassesMap;
	}
	
	public ArrayList<String> getImportedClasses(String version, String fileName) {
		ArrayList<String> importedClasses = null;
		
		
		try {
			
			ps = setOfDAO.stmtMap.get("getImportedClasses");
			
			ps.setString(1, version);
			ps.setString(2, fileName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if (null == importedClasses) {
					importedClasses = new ArrayList<String>();
				}
				
				importedClasses.add(rs.getString("IMP_CLASS"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return importedClasses;
	}
	
	public int insertTermWeight(AnalysisValue termWeight) {
		
		int fileVersionID = getSourceFileVersionID(termWeight.getName(), termWeight.getVersion());
		int termID = getTermID(termWeight.getTerm());
		
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("insertTerm");
			
			ps.setInt(1, fileVersionID);
			ps.setInt(2, termID);
			ps.setInt(3, termWeight.getTermCount());
			ps.setInt(4, termWeight.getInvDocCount());
			ps.setDouble(5, termWeight.getTf());
			ps.setDouble(6, termWeight.getIdf());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateTermWeight(AnalysisValue termWeight) {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("updateTermWeight");
			
			ps.setInt(1, termWeight.getTermCount());
			ps.setInt(2, termWeight.getInvDocCount());
			ps.setDouble(3, termWeight.getTf());
			ps.setDouble(4, termWeight.getIdf());
			ps.setInt(5, termWeight.getSourceFileVersionID());
			ps.setInt(6, termWeight.getTermID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllTermWeights() {
		int returnValue = INVALID;
		
		try {
			
			ps = setOfDAO.stmtMap.get("getSourceFileVersionID");
			
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getTermWeight(String fileName, String version, String term) {
		AnalysisValue returnValue = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermWeight");
			
			ps.setString(1, fileName);
			ps.setString(2, version);
			ps.setString(3, term);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = new AnalysisValue();
				
				returnValue.setName(fileName);
				returnValue.setVersion(version);
				returnValue.setTerm(term);
				returnValue.setTermCount(rs.getInt("TERM_CNT"));
				returnValue.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				returnValue.setTf(rs.getDouble("TF"));
				returnValue.setIdf(rs.getDouble("IDF"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, AnalysisValue> getTermMap(String fileName, String version) {
		HashMap<String, AnalysisValue> termMap = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermMap");
			
			ps.setString(1, fileName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == termMap) {
					termMap = new HashMap<String, AnalysisValue>();
				}
				AnalysisValue termWeight = new AnalysisValue();
				
				String term = rs.getString("TERM");
				termWeight.setName(fileName);
				termWeight.setVersion(version);
				termWeight.setTerm(term);
				termWeight.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				termWeight.setTermID(rs.getInt("SF_TERM_ID"));
				termWeight.setTermCount(rs.getInt("TERM_CNT"));
				termWeight.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				termWeight.setTf(rs.getDouble("TF"));
				termWeight.setIdf(rs.getDouble("IDF"));
				
				termMap.put(term, termWeight);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termMap;
	}
	
	public HashMap<String, AnalysisValue> getTermMap(int sourceFileVersionID) {
		HashMap<String, AnalysisValue> termMap = null;

		
		try {
			
			ps = setOfDAO.stmtMap.get("getTermMap");
			
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == termMap) {
					termMap = new HashMap<String, AnalysisValue>();
				}
				AnalysisValue analysisValue = new AnalysisValue();
				
				String term = rs.getString("TERM");
				analysisValue.setTerm(term);
				analysisValue.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				analysisValue.setTermID(rs.getInt("SF_TERM_ID"));
				analysisValue.setTermCount(rs.getInt("TERM_CNT"));
				analysisValue.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				analysisValue.setTf(rs.getDouble("TF"));
				analysisValue.setIdf(rs.getDouble("IDF"));
				
				termMap.put(term, analysisValue);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termMap;
	}
}
