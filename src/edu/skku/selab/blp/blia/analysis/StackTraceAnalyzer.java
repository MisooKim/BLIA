/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO2;
import edu.skku.selab.blp.db.dao.SourceFileDAO2;


/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class StackTraceAnalyzer {
	static final Logger logger = LoggerFactory.getLogger(StackTraceAnalyzer.class);
	private final static double DEFAULT_BOOST_SCORE = 0.1;
	private ArrayList<Bug> bugs;
	private HashMap<String, HashMap<String, String>> classNamesMap = null;
	
	public StackTraceAnalyzer() {
		bugs = null;
	}
	
    public StackTraceAnalyzer(ArrayList<Bug> bugs) {
    	this.bugs = bugs;
    }
    

    public StackTraceAnalyzer(Bug bug) {
    	this.bugs = new ArrayList<Bug>();
    	bugs.add(bug);
    }
    
    private class WorkerThread implements Runnable {
    	private Bug bug;
    	
        public WorkerThread(Bug bug){
            this.bug = bug;
        }
     
        @Override
        public void run() {
			// Compute similarity between Bug report & source files
        	
        	try {
        		insertDataToDb();
        	} catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
        
        private void insertDataToDb() throws Exception {
    		SourceFileDAO2 sourceFileDAO = new SourceFileDAO2();
    		IntegratedAnalysisDAO2 integratedAnalysisDAO = new IntegratedAnalysisDAO2();
        	
			String version = bug.getVersion();
			HashMap<String, IntegratedAnalysisValue> stackTraceAnalysisValues = new HashMap<String, IntegratedAnalysisValue>();
			HashMap<String, IntegratedAnalysisValue> importedClassAnalysisValues = new HashMap<String, IntegratedAnalysisValue>();
			ArrayList<String> stackTraceClasses = bug.getStackTraceClasses();
			
			if (null == stackTraceClasses) {
				return;
			}
			
			HashMap<String, String> classNames = classNamesMap.get(version);
			for (int j = 0; j < stackTraceClasses.size(); j++) {
				int currentRank = j + 1;
				
				String className = stackTraceClasses.get(j);
				String fileName;
				if (!stackTraceAnalysisValues.containsKey(className) && classNames.containsKey(className)) {
					IntegratedAnalysisValue stackTraceAnalysisValue = new IntegratedAnalysisValue();
					stackTraceAnalysisValue.setBugID(bug.getID());
					
					fileName = classNames.get(className);
					int sourceFileVersionID = sourceFileDAO.getSourceFileVersionID(fileName, version);
					stackTraceAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
					
					if (j < 10) {
						// set boostscore for class in stack-trace within rank 10
						stackTraceAnalysisValue.setStackTraceScore(1.0 / currentRank);
					} else {
						// set boostscore, 0.1 for class in stack-trace above rank 10
						stackTraceAnalysisValue.setStackTraceScore(DEFAULT_BOOST_SCORE);
					}
					stackTraceAnalysisValues.put(className, stackTraceAnalysisValue);
				}
			}
			
			for (int j = 0; j < stackTraceClasses.size(); j++) {
				String className = stackTraceClasses.get(j);
				String fileName = classNames.get(className);
				ArrayList<String> importedClasses = sourceFileDAO.getImportedClasses(version, fileName);
				
				if (null == importedClasses) {
					continue;
				}
				
				// find import files and filter them => set C
				// give boostscore to set C		
				for (int k = 0; k < importedClasses.size(); k++) {
					if (!stackTraceAnalysisValues.containsKey(className) &&
							!importedClassAnalysisValues.containsKey(className) && classNames.containsKey(className)) {
						IntegratedAnalysisValue importedClassAnalysisValue = new IntegratedAnalysisValue();
						importedClassAnalysisValue.setBugID(bug.getID());
						
						int sourceFileVersionID = sourceFileDAO.getSourceFileVersionID(fileName, version);
						importedClassAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
						importedClassAnalysisValue.setStackTraceScore(DEFAULT_BOOST_SCORE);
						
						importedClassAnalysisValues.put(className, importedClassAnalysisValue);
					}
				}
				
				Iterator<IntegratedAnalysisValue> analysisValuesIter = stackTraceAnalysisValues.values().iterator();
				while (analysisValuesIter.hasNext()) {
					integratedAnalysisDAO.updateStackTraceScore(analysisValuesIter.next());
				}
				
				analysisValuesIter = importedClassAnalysisValues.values().iterator();
				while (analysisValuesIter.hasNext()) {
					integratedAnalysisDAO.updateStackTraceScore(analysisValuesIter.next());
				}
			}
        }
    }
	
	public void analyze() throws Exception {
		SourceFileDAO2 sourceFileDAO = new SourceFileDAO2();
		
		classNamesMap = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < bugs.size(); i++) {
			Bug bug = bugs.get(i);
			String version = bug.getVersion();
			
			if (!classNamesMap.containsKey(version)) {
				HashMap<String, String> classNames = sourceFileDAO.getClassNames(version);
				classNamesMap.put(version, classNames);
			}
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(Property.THREAD_COUNT);
		for (int i = 0; i < bugs.size(); i++) {
			Runnable worker = new WorkerThread(bugs.get(i));
			executor.execute(worker);
		}
		
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
}
