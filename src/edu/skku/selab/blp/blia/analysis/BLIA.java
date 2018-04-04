/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.blia.indexer.BugCorpusCreator;
import edu.skku.selab.blp.blia.indexer.BugVectorCreator;
import edu.skku.selab.blp.blia.indexer.GitCommitLogCollector;
import edu.skku.selab.blp.blia.indexer.SourceFileCorpusCreator;
import edu.skku.selab.blp.blia.indexer.SourceFileVectorCreator;
import edu.skku.selab.blp.blia.indexer.BugSourceFileVectorCreator;
import edu.skku.selab.blp.blia.indexer.StructuredSourceFileCorpusCreator;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.ExtendedIntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.DbUtil;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.utils.Util;
import logging.Foo;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BLIA {
	static final Logger logger = LoggerFactory.getLogger(BLIA.class);
	  
	public static String version = "v1.0";
	private ArrayList<Bug> bugs = null;
	private double alpha = 0;
	private double beta = 0;
	private double gamma = 0;
	private static Integer completeBugIdCount = 0;
	
	public BLIA() {
		prepareWorkingDir();
	}
	
	private String getElapsedTimeSting(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		String elpsedTimeString = (elapsedTime / 1000) + "." + (elapsedTime % 1000);
		return elpsedTimeString;
	}
	
	public void prepareAnalysisData(boolean useStrucrutedInfo, Date commitSince, Date commitUntil) throws Exception {
		logger.trace("[STARTED] Source file corpus creating.");
		long startTime = System.currentTimeMillis();
		if (!useStrucrutedInfo) {
			SourceFileCorpusCreator sourceFileCorpusCreator = new SourceFileCorpusCreator();
			sourceFileCorpusCreator.create(version);
		} else {
			StructuredSourceFileCorpusCreator structuredSourceFileCorpusCreator = new StructuredSourceFileCorpusCreator();
			structuredSourceFileCorpusCreator.create(version);
		}
		logger.trace("[DONE] Source file corpus creating.("+getElapsedTimeSting(startTime)+" sec)");

		logger.trace("[STARTED] Source file vector creating.");
		startTime = System.currentTimeMillis();
		SourceFileVectorCreator sourceFileVectorCreator = new SourceFileVectorCreator();
		sourceFileVectorCreator.createIndex(version);
		sourceFileVectorCreator.computeLengthScore(version);
		sourceFileVectorCreator.create(version);
		logger.trace("[DONE] Source file vector creating.("+getElapsedTimeSting(startTime)+" sec)");
		
		// Create SordtedID.txt
		logger.trace("[STARTED] Bug corpus creating.");
		startTime = System.currentTimeMillis();
		BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
		boolean stackTraceAnaysis = true;
		bugCorpusCreator.create(stackTraceAnaysis);
		logger.trace("[DONE] Bug corpus creating.("+ getElapsedTimeSting(startTime)+" sec)");
		
		logger.trace("[STARTED] Bug vector creating.");
		startTime = System.currentTimeMillis();
		BugVectorCreator bugVectorCreator = new BugVectorCreator();
		bugVectorCreator.create();
		logger.trace("[DONE] Bug vector creating.("+getElapsedTimeSting(startTime)+" sec)");
		
		logger.trace("[STARTED] Commit log collecting.");
		startTime = System.currentTimeMillis();
		String repoDir = Property.getInstance().getRepoDir();
		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(repoDir);
		
		boolean collectForcely = false;
		gitCommitLogCollector.collectCommitLog(commitSince, commitUntil, collectForcely);
		logger.trace("[DONE] Commit log collecting: Commit Force = "+ collectForcely+"("+getElapsedTimeSting(startTime)+" sec)");
		
		logger.trace("[STARTED] Bug-Source file vector creating.");
		startTime = System.currentTimeMillis();
		BugSourceFileVectorCreator bugSourceFileVectorCreator = new BugSourceFileVectorCreator(); 
		bugSourceFileVectorCreator.create(version);
		logger.trace("[DONE] Bug-Source file vector creating.("+ getElapsedTimeSting(startTime)+" sec)");
	}
	
	public void preAnalyze() throws Exception {
		BugDAO bugDAO = new BugDAO();
		boolean orderedByFixedDate = true;
		bugs = bugDAO.getAllBugs(orderedByFixedDate);

		// VSM_SCORE
		logger.trace("[STARTED] Source file analysis.");
		long startTime = System.currentTimeMillis();
		SourceFileAnalyzer sourceFileAnalyzer = new SourceFileAnalyzer(bugs);
		boolean useStructuredInformation = true;
		sourceFileAnalyzer.analyze(version, useStructuredInformation);
		logger.trace("[DONE] Source file analysis.("+getElapsedTimeSting(startTime)+" sec)");

		// SIMI_SCORE
		logger.trace("[STARTED] Bug repository analysis.");
		startTime = System.currentTimeMillis();
		BugRepoAnalyzer bugRepoAnalyzer = new BugRepoAnalyzer(bugs);
		bugRepoAnalyzer.analyze();
		logger.trace("[DONE] Bug repository analysis.("+getElapsedTimeSting(startTime)+" sec)");
		
		// STRACE_SCORE
		logger.trace("[STARTED] Stack-trace analysis.");
		startTime = System.currentTimeMillis();
		StackTraceAnalyzer stackTraceAnalyzer = new StackTraceAnalyzer(bugs);
		stackTraceAnalyzer.analyze();
		logger.trace("[DONE] Stack-trace analysis.("+getElapsedTimeSting(startTime)+" sec)");
		
		// COMM_SCORE
		logger.trace("[STARTED] Scm repository analysis.");
		startTime = System.currentTimeMillis();
		ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer(bugs);
		scmRepoAnalyzer.analyze(version);
		logger.trace("[DONE] Scm repository analysis.("+getElapsedTimeSting(startTime)+" sec)");
	}
	
    private void calculateBliaSourceFileScore(int bugID, boolean includeStackTrace) throws Exception {
//		HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues = integratedAnalysisValuesMap.get(bugID);
    	IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
    	
//    	System.out.printf("Before integratedAnalysisDAO.getAnalysisValues() \n");
		HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues = integratedAnalysisDAO.getAnalysisValues(bugID);
		if (null == integratedAnalysisValues) {
			return;
		}
		
//		System.out.printf("After integratedAnalysisDAO.getAnalysisValues() \n");
		// AmaLgam doesn't use normalize
		normalize(integratedAnalysisValues);
		combine(integratedAnalysisValues, alpha, beta, includeStackTrace);
		
		@SuppressWarnings("unused")
		int sourceFileCount = integratedAnalysisValues.keySet().size();
//		System.out.printf("After combine(), integratedAnalysisValues: %d\n", sourceFileCount);
//		int count = 0;
		Iterator<Integer> integratedAnalysisValuesIter = integratedAnalysisValues.keySet().iterator();
		while (integratedAnalysisValuesIter.hasNext()) {
			int sourceFileVersionID = integratedAnalysisValuesIter.next();
			
			IntegratedAnalysisValue integratedAnalysisValue = integratedAnalysisValues.get(sourceFileVersionID);
//			System.out.printf("Before updateBLIAScore(), count: %d/%d\n", count++, sourceFileCount);
			int updatedColumnCount = integratedAnalysisDAO.updateBliaSourceFileScore(integratedAnalysisValue);
//			System.out.printf("After updateBliaSourceFileScore(), count: %d/%d\n", count, sourceFileCount);
			if (0 == updatedColumnCount) {
				logger.error("[ERROR] BLIA.analyze(): BLIA and BugLocator score update failed! BugID: "+integratedAnalysisValue.getBugID()
					+", sourceFileVersionID: "+integratedAnalysisValue.getSourceFileVersionID());

				// remove following line after testing.
//				integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue);
			}
		}
    }
    
    private void calculateBliaMethodScore(int bugID) throws Exception {
//		HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues = integratedAnalysisValuesMap.get(bugID);
    	IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
    	
//    	System.out.printf("Before integratedAnalysisDAO.getAnalysisValues() \n");
		HashMap<Integer, ExtendedIntegratedAnalysisValue> integratedMethodAnalysisValues = integratedAnalysisDAO.getMethodAnalysisValues(bugID);
		if (null == integratedMethodAnalysisValues) {
			integratedMethodAnalysisValues = new HashMap<Integer, ExtendedIntegratedAnalysisValue>();
		}
		
//		System.out.printf("After integratedAnalysisDAO.getAnalysisValues() \n");
		normalizeVsmScore(integratedMethodAnalysisValues);
		combineForMethodLevel(integratedMethodAnalysisValues, gamma);
		
		Iterator<Integer> integratedMethodAnalysisValuesIter = integratedMethodAnalysisValues.keySet().iterator();
		while (integratedMethodAnalysisValuesIter.hasNext()) {
			int methodID = integratedMethodAnalysisValuesIter.next();
			
			ExtendedIntegratedAnalysisValue integratedMethodAnalysisValue = integratedMethodAnalysisValues.get(methodID);
//			System.out.printf("Before updateBLIAScore(), count: %d/%d\n", count++, sourceFileCount);
			int updatedColumnCount = integratedAnalysisDAO.updateBliaMethodScore(integratedMethodAnalysisValue);
//			System.out.printf("After updateBLIAScore(), count: %d/%d\n", count, sourceFileCount);
			if (0 == updatedColumnCount) {
				logger.error("[ERROR] BLIA.analyze(): BLIA and BugLocator score update failed! BugID: "+integratedMethodAnalysisValue.getBugID()+
						", methodID: "+integratedMethodAnalysisValue.getMethodID());

				// remove following line after testing.
//				integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue);
			}
		}
    }
	
	public void analyze(String version, boolean includeStackTrace, boolean includeMethodAnalyze) throws Exception {
		if (null == bugs) {
			BugDAO bugDAO = new BugDAO();
			bugs = bugDAO.getAllBugs(false);			
		}
		
		Property property = Property.getInstance();
		alpha = property.getAlpha();
		beta = property.getBeta();
		gamma = property.getGamma();
		
		
		logger.trace("[STARTED] BLIA.anlayze()");
		for (int i = 0; i < bugs.size(); i++) {
			long startTime = System.currentTimeMillis();
			int bugID = bugs.get(i).getID();
			calculateBliaSourceFileScore(bugID, includeStackTrace);
			logger.info("[calculateBliaSourceFileScore()] ["+i+"] Bug ID: "+bugID+" ("+Util.getElapsedTimeSting(startTime)+" sec)");
//			Runnable worker = new WorkerThread(bugs.get(i).getID());
//			executor.execute(worker);
		}
		
		if (includeMethodAnalyze) {
			MethodAnalyzer methodAnalyzer = new MethodAnalyzer(bugs);
			methodAnalyzer.analyze();
			
			for (int i = 0; i < bugs.size(); i++) {
				long startTime = System.currentTimeMillis();
				int bugID = bugs.get(i).getID();
				calculateBliaMethodScore(bugID);
				logger.info("[calculateBliaMethodScore()] ["+i+"] Bug ID: "+bugID+" ("+Util.getElapsedTimeSting(startTime)+" sec)");
//				Runnable worker = new WorkerThread(bugs.get(i).getID());
//				executor.execute(worker);
			}
			
//			executor.shutdown();
//			while (!executor.isTerminated()) {
//			}
		}
		
		logger.trace("[DONE] BLIA.anlayze()");
	}
	
	/**
	 * 
	 * @param integratedAnalysisValues
	 * @param alpha
	 * @param beta
	 * @param includeStackTrace
	 */
	private void combine(HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues, double alpha, double beta,
			boolean includeStackTrace) {
		Iterator<Integer> integratedAnalysisValuesIter = integratedAnalysisValues.keySet().iterator();
		while (integratedAnalysisValuesIter.hasNext()) {
			int sourceFileVersionID = integratedAnalysisValuesIter.next();
			IntegratedAnalysisValue integratedAnalysisValue = integratedAnalysisValues.get(sourceFileVersionID);
			
			double vsmScore = integratedAnalysisValue.getVsmScore();
			double similarityScore = integratedAnalysisValue.getSimilarityScore();
			double stackTraceScore = integratedAnalysisValue.getStackTraceScore();
			double commitLogScore = integratedAnalysisValue.getCommitLogScore();
			
			double bugLocatorScore = (1 - alpha) * (vsmScore) + alpha * similarityScore;
			integratedAnalysisValue.setBugLocatorScore(bugLocatorScore);
			
			double middleSourceFileScore = bugLocatorScore;
			if (includeStackTrace) {
				middleSourceFileScore += stackTraceScore;
			}
			integratedAnalysisValue.setMiddleSourceFileScore(middleSourceFileScore);
			
			double bliaSourceFileScore = middleSourceFileScore;
			if (bliaSourceFileScore > 0) {
				bliaSourceFileScore = (1 - beta) * bliaSourceFileScore + beta * commitLogScore;
			} else {
				bliaSourceFileScore = 0;
			}

//			if (vsmScore > 0.5) {
//				bliaScore = (1 - beta) * bliaScore + beta * commitLogScore;
//			} else if (bugLocatorScore <= 0){
//				bliaScore = 0;
//			}
			
			integratedAnalysisValue.setBliaSourceFileScore(bliaSourceFileScore);
		}
	}
	
	/**
	 * 
	 * @param integratedMethodAnalysisValues
	 * @param gamma
	 */
	private void combineForMethodLevel(HashMap<Integer, ExtendedIntegratedAnalysisValue> integratedMethodAnalysisValues, double gamma) {
		Iterator<Integer> integratedMethodAnalysisValuesIter = integratedMethodAnalysisValues.keySet().iterator();
		while (integratedMethodAnalysisValuesIter.hasNext()) {
			int methodID = integratedMethodAnalysisValuesIter.next();
			
			ExtendedIntegratedAnalysisValue integratedMethodAnalysisValue = integratedMethodAnalysisValues.get(methodID);
			double methodVsmScore = integratedMethodAnalysisValue.getVsmScore();
			double commitMethodLogScore = integratedMethodAnalysisValue.getCommitLogScore();
			
			double bliaMethodScore = 0.0;
			if (methodVsmScore > 0) {
				bliaMethodScore = (1 - gamma) * methodVsmScore + gamma * commitMethodLogScore;
			}
			
			integratedMethodAnalysisValue.setBliaMethodScore(bliaMethodScore);
		}
	}

	/**
	 * Normalize values in array from max. to min of array
	 * 
	 * @param array
	 * @return
	 */
	private void normalize(HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues) {
		double maxVsmScore = Double.MIN_VALUE;
		double minVsmScore = Double.MAX_VALUE;;
		double maxSimiScore = Double.MIN_VALUE;
		double minSimiScore = Double.MAX_VALUE;;
//		double maxCommitLogScore = Double.MIN_VALUE;
//		double minCommitLogScore = Double.MAX_VALUE;;

		
		Iterator<Integer> integratedAnalysisValuesIter = integratedAnalysisValues.keySet().iterator();
		while (integratedAnalysisValuesIter.hasNext()) {
			int sourceFileVersionID = integratedAnalysisValuesIter.next();
			IntegratedAnalysisValue integratedAnalysisValue = integratedAnalysisValues.get(sourceFileVersionID);
			double vsmScore = integratedAnalysisValue.getVsmScore();
			double simiScore = integratedAnalysisValue.getSimilarityScore();
//			double commitLogScore = integratedAnalysisValue.getCommitLogScore();
			if (maxVsmScore < vsmScore) {
				maxVsmScore = vsmScore;
			}
			if (minVsmScore > vsmScore) {
				minVsmScore = vsmScore;
			}
			if (maxSimiScore < simiScore) {
				maxSimiScore = simiScore;
			}
			if (minSimiScore > simiScore) {
				minSimiScore = simiScore;
			}
//			if (maxCommitLogScore < commitLogScore) {
//				maxCommitLogScore = commitLogScore;
//			}
//			if (minCommitLogScore > commitLogScore) {
//				minCommitLogScore = commitLogScore;
//			}	
		}
		
		double spanVsmScore = maxVsmScore - minVsmScore;
		double spanSimiScore = maxSimiScore - minSimiScore;
//		double spanCommitLogScore = maxCommitLogScore - minCommitLogScore;
		integratedAnalysisValuesIter = integratedAnalysisValues.keySet().iterator();
		while (integratedAnalysisValuesIter.hasNext()) {
			int sourceFileVersionID = integratedAnalysisValuesIter.next();
			IntegratedAnalysisValue integratedAnalysisValue = integratedAnalysisValues.get(sourceFileVersionID);
			double normalizedVsmScore = (integratedAnalysisValue.getVsmScore() - minVsmScore) / spanVsmScore;
			double normalizedSimiScore = (integratedAnalysisValue.getSimilarityScore() - minSimiScore) / spanSimiScore;
//			double normalizedCommitLogScore = (integratedAnalysisValue.getCommitLogScore() - minCommitLogScore) / spanCommitLogScore;
			integratedAnalysisValue.setVsmScore(normalizedVsmScore);
			integratedAnalysisValue.setSimilarityScore(normalizedSimiScore);
//			integratedAnalysisValue.setCommitLogScore(normalizedCommitLogScore);
		}
	}
	
	/**
	 * Normalize values of VSM score ONLY in array from max. to min of array 
	 * 
	 * @param array
	 * @return
	 */
	private void normalizeVsmScore(HashMap<Integer, ExtendedIntegratedAnalysisValue> extendedIntegratedAnalysisValues) {
		double maxVsmScore = Double.MIN_VALUE;
		double minVsmScore = Double.MAX_VALUE;;
		
		Iterator<Integer> integratedMethodAnalysisValuesIter = extendedIntegratedAnalysisValues.keySet().iterator();
		while (integratedMethodAnalysisValuesIter.hasNext()) {
			int methodID = integratedMethodAnalysisValuesIter.next();
			IntegratedAnalysisValue integratedAnalysisValue = extendedIntegratedAnalysisValues.get(methodID);
			double vsmScore = integratedAnalysisValue.getVsmScore();
			if (maxVsmScore < vsmScore) {
				maxVsmScore = vsmScore;
			}
			if (minVsmScore > vsmScore) {
				minVsmScore = vsmScore;
			}
		}
		
		double spanVsmScore = maxVsmScore - minVsmScore;
		integratedMethodAnalysisValuesIter = extendedIntegratedAnalysisValues.keySet().iterator();
		while (integratedMethodAnalysisValuesIter.hasNext()) {
			int methodID = integratedMethodAnalysisValuesIter.next();
			IntegratedAnalysisValue integratedAnalysisValue = extendedIntegratedAnalysisValues.get(methodID);
			double normalizedVsmScore = (integratedAnalysisValue.getVsmScore() - minVsmScore) / spanVsmScore;
			integratedAnalysisValue.setVsmScore(normalizedVsmScore);
		}
	}
	
    private boolean deleteDirectory(File path) {
        if(!path.exists()) {
            return false;
        }
         
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
         
        return path.delete();
    }
	
	private void prepareWorkingDir() {
		Property prop = Property.getInstance();
		String workDir = prop.getWorkDir();
		
		File dir = new File(workDir);
		if (dir.exists()) {
			deleteDirectory(dir);
		}
		
		if (false == dir.mkdir()) {
			logger.error(workDir + " can't be created!");
			
			if (false == dir.mkdir()) {
				logger.error(workDir + " can't be created again");
			}
		}
	}
	
	public void run() throws Exception {
		Property prop = Property.getInstance();
		prepareWorkingDir();
				
		long startTime = System.currentTimeMillis();
		BLIA blia = new BLIA();
		
		boolean useStrucrutedInfo = true;
		boolean includeStackTrace = true;
		
		boolean includeMethodAnalyze = true;

		DbUtil dbUtil = new DbUtil();
		String dbName = prop.getProductName();
		dbUtil.openConnetion(dbName);
		boolean commitDataIncluded = true;
		dbUtil.initializeAllData(commitDataIncluded);
		dbUtil.closeConnection();
		logger.info(prop.getProductName()+" "+prop.getAlpha()+" "+prop.getBeta());
		startTime = System.currentTimeMillis();
		logger.trace("[STARTED] BLIA prepareAnalysisData().");
		blia.prepareAnalysisData(useStrucrutedInfo, prop.getSince().getTime(), prop.getUntil().getTime());
		logger.trace("[DONE] BLIA prepareAnalysisData().(Total "+Util.getElapsedTimeSting(startTime)+" sec)");
			
		logger.trace("[STARTED] BLIA pre-anlaysis.");
		blia.preAnalyze();
		logger.trace("[DONE] BLIA pre-anlaysis.(Total "+Util.getElapsedTimeSting(startTime)+" sec)");
		
		logger.trace("[STARTED] BLIA anlaysis.");
		startTime = System.currentTimeMillis();
		blia.analyze(version, includeStackTrace, includeMethodAnalyze);
		logger.trace("[DONE] BLIA anlaysis.(Total "+ Util.getElapsedTimeSting(startTime)+" sec)");
	}


	public void runForCommit() throws Exception {
		Property prop = Property.getInstance();
		prepareWorkingDir();
				
		long startTime = System.currentTimeMillis();
		BLIA blia = new BLIA();
		
		boolean useStrucrutedInfo = true;
		boolean includeStackTrace = true;
		
		boolean includeMethodAnalyze = prop.isMethodLevel();

		DbUtil dbUtil = new DbUtil();
		String dbName = prop.getProductName();
		dbUtil.openConnetion(dbName);
		boolean commitDataIncluded = false;
		dbUtil.initializeAllData(commitDataIncluded);
		dbUtil.closeConnection();
		logger.info(prop.getProductName()+" "+prop.getAlpha()+" "+prop.getBeta());
		
		logger.trace("[STARTED] Commit log collecting.");
		startTime = System.currentTimeMillis();
		String repoDir = Property.getInstance().getRepoDir();
		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(repoDir);
		
		boolean collectForcely = false;
		gitCommitLogCollector.collectCommitLog(prop.getSince().getTime(), prop.getUntil().getTime(), collectForcely);
		logger.trace("[DONE] Commit log collecting: Commit Force = "+ collectForcely+"("+getElapsedTimeSting(startTime)+" sec)");
		
		BugCorpusCreator bcc = new BugCorpusCreator();
		ArrayList<Bug> bugList = bcc.parseXMLforCommit(true);
		for(int i = 0 ; i<bugList.size(); i++){
			Bug bug = bugList.get(i);
			double percent = (i+1.0) / bugList.size()*100;
			
			//String cur = String.valueOf(percent).split(".")[0];
			String cur="";
			
			String toggle = "\\.git";
			if(System.getProperty("os.name").equals("Linux"))
				toggle = "/.git";
			
			Git git = Git.open(new File(prop.getRepoDir().replace(toggle, "")));
			git.checkout().setName(bugList.get(i).getFixedCommitInfos().get(0).getCommitID()).setForce(true).call();
			version = bugList.get(i).getVersion();
			
			
			logger.trace("[STARTED] Source file corpus creating. "+version + " "+cur+" "+percent);
			if (!useStrucrutedInfo) {
				SourceFileCorpusCreator sourceFileCorpusCreator = new SourceFileCorpusCreator();
				sourceFileCorpusCreator.create(version);
			} else {
				StructuredSourceFileCorpusCreator structuredSourceFileCorpusCreator = new StructuredSourceFileCorpusCreator();
				structuredSourceFileCorpusCreator.createForCommit(version);
			}
			logger.trace("[DONE] Source file corpus creating.("+getElapsedTimeSting(startTime)+" sec)");
		
			logger.trace("[STARTED] Source file vector creating. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			SourceFileVectorCreator sourceFileVectorCreator = new SourceFileVectorCreator();
			sourceFileVectorCreator.createIndex(version);
			sourceFileVectorCreator.computeLengthScore(version);
			sourceFileVectorCreator.create(version);
			logger.trace("[DONE] Source file vector creating.("+getElapsedTimeSting(startTime)+" sec)");
			
			// Create SordtedID.txt
			logger.trace("[STARTED] Bug corpus creating.. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
			boolean stackTraceAnaysis = true;
			bugCorpusCreator.createForCommit(stackTraceAnaysis,bug);
			logger.trace("[DONE] Bug corpus creating.("+ getElapsedTimeSting(startTime)+" sec)");
			
			logger.trace("[STARTED] Bug vector creating.. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugVectorCreator bugVectorCreator = new BugVectorCreator();
			bugVectorCreator.create();
			logger.trace("[DONE] Bug vector creating.("+getElapsedTimeSting(startTime)+" sec)");
			
			logger.trace("[STARTED] Bug-Source file vector creating. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugSourceFileVectorCreator bugSourceFileVectorCreator = new BugSourceFileVectorCreator(); 
			bugSourceFileVectorCreator.create(bugList.get(i).getVersion());
			logger.trace("[DONE] Bug-Source file vector creating.("+ getElapsedTimeSting(startTime)+" sec)");
			
			// VSM_SCORE
			logger.trace("[STARTED] Source file analysis.. "+version+ " "+cur);
			SourceFileAnalyzer sourceFileAnalyzer = new SourceFileAnalyzer(bug);
			boolean useStructuredInformation = true;
			sourceFileAnalyzer.analyze(version, useStructuredInformation);
			logger.trace("[DONE] Source file analysis.("+getElapsedTimeSting(startTime)+" sec)");
			
			// STRACE_SCORE
			logger.trace("[STARTED] Stack-trace analysis.. "+version+ " "+cur);
			startTime = System.currentTimeMillis();
			StackTraceAnalyzer stackTraceAnalyzer = new StackTraceAnalyzer(bug);
			stackTraceAnalyzer.analyze();
			logger.trace("[DONE] Stack-trace analysis.("+getElapsedTimeSting(startTime)+" sec)");

			// COMM_SCORE
			logger.trace("[STARTED] Scm repository analysis.. "+version+ " "+cur);
			startTime = System.currentTimeMillis();
			ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer(bug);
			scmRepoAnalyzer.analyze(bug.getVersion());
			logger.trace("[DONE] Scm repository analysis.("+getElapsedTimeSting(startTime)+" sec)");		
		}
		// SIMI_SCORE
		logger.trace("[STARTED] Bug repository analysis.");
		startTime = System.currentTimeMillis();
		BugRepoAnalyzer bugRepoAnalyzer = new BugRepoAnalyzer(bugList);
		bugRepoAnalyzer.analyzeForCommit();
		logger.trace("[DONE] Bug repository analysis.("+getElapsedTimeSting(startTime)+" sec)");

		blia.analyze(version, includeStackTrace, includeMethodAnalyze);
		logger.trace("[DONE] BLIA anlaysis.(Total "+ Util.getElapsedTimeSting(startTime)+" sec)");			
	}


	public void runForCommitWithPercent() throws Exception {
		Property prop = Property.getInstance();
		prepareWorkingDir();
				
		long startTime = System.currentTimeMillis();
		BLIA blia = new BLIA();
		
		boolean useStrucrutedInfo = true;
		boolean includeStackTrace = true;
		
		boolean includeMethodAnalyze = prop.isMethodLevel();

		DbUtil dbUtil = new DbUtil();
		String dbName = prop.getProductName();
		dbUtil.openConnetion(dbName);
		boolean commitDataIncluded = false;
		dbUtil.initializeAllData(commitDataIncluded);
		dbUtil.closeConnection();
		logger.info(prop.getProductName()+" "+prop.getAlpha()+" "+prop.getBeta());
		
		logger.trace("[STARTED] Commit log collecting.");
		startTime = System.currentTimeMillis();
		String repoDir = Property.getInstance().getRepoDir();
		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(repoDir);
		
		boolean collectForcely = false;
		gitCommitLogCollector.collectCommitLog(prop.getSince().getTime(), prop.getUntil().getTime(), collectForcely);
		logger.trace("[DONE] Commit log collecting: Commit Force = "+ collectForcely+"("+getElapsedTimeSting(startTime)+" sec)");
		
		BugCorpusCreator bcc = new BugCorpusCreator();
		ArrayList<Bug> bugList = bcc.parseXMLforCommit(true);
		int start = Math.round((float)(bugList.size()*prop.getStartPercent()));
		int end = Math.round((float)(bugList.size()*prop.getEndPercent()));
		for(int i = start ; i<end; i++){
			Bug bug = bugList.get(i);
			double percent = (i+1.0) / bugList.size()*100;
			
			//String cur = String.valueOf(percent).split(".")[0];
			String cur="";
			
			String toggle = "\\.git";
			if(System.getProperty("os.name").equals("Linux"))
				toggle = "/.git";
			
			Git git = Git.open(new File(prop.getRepoDir().replace(toggle, "")));
			git.checkout().setName(bugList.get(i).getFixedCommitInfos().get(0).getCommitID()).setForce(true).call();
			version = bugList.get(i).getVersion();
			
			
			logger.trace("[STARTED] Source file corpus creating. "+version + " "+cur+" "+percent);
			if (!useStrucrutedInfo) {
				SourceFileCorpusCreator sourceFileCorpusCreator = new SourceFileCorpusCreator();
				sourceFileCorpusCreator.create(version);
			} else {
				StructuredSourceFileCorpusCreator structuredSourceFileCorpusCreator = new StructuredSourceFileCorpusCreator();
				structuredSourceFileCorpusCreator.createForCommit(version);
			}
			logger.trace("[DONE] Source file corpus creating.("+getElapsedTimeSting(startTime)+" sec)");
		
			logger.trace("[STARTED] Source file vector creating. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			SourceFileVectorCreator sourceFileVectorCreator = new SourceFileVectorCreator();
			sourceFileVectorCreator.createIndex(version);
			sourceFileVectorCreator.computeLengthScore(version);
			sourceFileVectorCreator.create(version);
			logger.trace("[DONE] Source file vector creating.("+getElapsedTimeSting(startTime)+" sec)");
			
			// Create SordtedID.txt
			logger.trace("[STARTED] Bug corpus creating.. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
			boolean stackTraceAnaysis = true;
			bugCorpusCreator.createForCommit(stackTraceAnaysis,bug);
			logger.trace("[DONE] Bug corpus creating.("+ getElapsedTimeSting(startTime)+" sec)");
			
			logger.trace("[STARTED] Bug vector creating.. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugVectorCreator bugVectorCreator = new BugVectorCreator();
			bugVectorCreator.create();
			logger.trace("[DONE] Bug vector creating.("+getElapsedTimeSting(startTime)+" sec)");
			
			logger.trace("[STARTED] Bug-Source file vector creating. "+version+ " "+cur+" "+percent);
			startTime = System.currentTimeMillis();
			BugSourceFileVectorCreator bugSourceFileVectorCreator = new BugSourceFileVectorCreator(); 
			bugSourceFileVectorCreator.create(bugList.get(i).getVersion());
			logger.trace("[DONE] Bug-Source file vector creating.("+ getElapsedTimeSting(startTime)+" sec)");
			
			// VSM_SCORE
			logger.trace("[STARTED] Source file analysis.. "+version+ " "+cur);
			SourceFileAnalyzer sourceFileAnalyzer = new SourceFileAnalyzer(bug);
			boolean useStructuredInformation = true;
			sourceFileAnalyzer.analyze(version, useStructuredInformation);
			logger.trace("[DONE] Source file analysis.("+getElapsedTimeSting(startTime)+" sec)");
			
			// STRACE_SCORE
			logger.trace("[STARTED] Stack-trace analysis.. "+version+ " "+cur);
			startTime = System.currentTimeMillis();
			StackTraceAnalyzer stackTraceAnalyzer = new StackTraceAnalyzer(bug);
			stackTraceAnalyzer.analyze();
			logger.trace("[DONE] Stack-trace analysis.("+getElapsedTimeSting(startTime)+" sec)");

			// COMM_SCORE
			logger.trace("[STARTED] Scm repository analysis.. "+version+ " "+cur);
			startTime = System.currentTimeMillis();
			ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer(bug);
			scmRepoAnalyzer.analyze(bug.getVersion());
			logger.trace("[DONE] Scm repository analysis.("+getElapsedTimeSting(startTime)+" sec)");		
		}
		// SIMI_SCORE
		logger.trace("[STARTED] Bug repository analysis.");
		startTime = System.currentTimeMillis();
		ArrayList<Bug> percentBugList = new ArrayList<Bug>();

		start = Math.round((float)(bugList.size()*prop.getStartPercent()));
		end = Math.round((float)(bugList.size()*prop.getEndPercent()));
		for(int i = start ; i<end; i++){
			Bug bug = bugList.get(i);
			percentBugList.add(bug);
		}
		BugRepoAnalyzer bugRepoAnalyzer = new BugRepoAnalyzer(bugList);
		bugRepoAnalyzer.analyzeForCommitWithPercent();
		logger.trace("[DONE] Bug repository analysis.("+getElapsedTimeSting(startTime)+" sec)");

		blia.analyze(version, includeStackTrace, includeMethodAnalyze);
		logger.trace("[DONE] BLIA anlaysis.(Total "+ Util.getElapsedTimeSting(startTime)+" sec)");			
	}
}

