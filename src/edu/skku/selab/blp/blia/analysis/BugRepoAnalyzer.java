/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.SimilarBugInfo;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugRepoAnalyzer {
	static final Logger logger = LoggerFactory.getLogger(BugRepoAnalyzer.class);
	private ArrayList<Bug> bugs;
	private HashMap<Integer, HashSet<SourceFile>> fixedFilesMap;
	private HashMap<Integer, HashSet<SimilarBugInfo>> similarBugInfosMap;
	
    public BugRepoAnalyzer() {
    	bugs = null;
    }
    
    public BugRepoAnalyzer(ArrayList<Bug> orderedBugs) {
    	bugs = orderedBugs;
    }

    public BugRepoAnalyzer(Bug bug) {
    	bugs = new ArrayList<Bug>();
    	bugs.add(bug);
    }
    
    private void prepareData() throws Exception {
		BugDAO bugDAO = new BugDAO();
		fixedFilesMap = new HashMap<Integer, HashSet<SourceFile>>(); 
		similarBugInfosMap = new HashMap<Integer, HashSet<SimilarBugInfo>>();
		for (int i = 0; i < bugs.size(); i++) {
			Bug bug = bugs.get(i);
			int bugID = bug.getID();
			HashSet<SourceFile> fixedFiles = bugDAO.getFixedFiles(bugID);
			fixedFilesMap.put(bugID, fixedFiles);
			
			HashSet<SimilarBugInfo> similarBugInfos = bugDAO.getSimilarBugInfos(bugID);
			similarBugInfosMap.put(bugID, similarBugInfos);
		}
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
        		calculateSimilarScore(bug);
        	} catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
        
        private void calculateSimilarScore(Bug bug) throws Exception {
    		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();	
    		SourceFileDAO sourceFileDAO = new SourceFileDAO(); 
    		
    		int bugID = bug.getID();
    		HashMap<Integer, Double> similarScores = new HashMap<Integer, Double>(); 
    		HashSet<SimilarBugInfo> similarBugInfos = similarBugInfosMap.get(bugID);
    		if (null != similarBugInfos) {
    			Iterator<SimilarBugInfo> similarBugInfosIter = similarBugInfos.iterator();
    			while (similarBugInfosIter.hasNext()) {
    				SimilarBugInfo similarBugInfo = similarBugInfosIter.next();
    				
    				HashSet<SourceFile> fixedFiles = fixedFilesMap.get(similarBugInfo.getSimilarBugID());
    				if (null != fixedFiles) {
    					int fixedFilesCount = fixedFiles.size();
    					double singleValue = similarBugInfo.getSimilarityScore() / fixedFilesCount;
    					Iterator<SourceFile> fixedFilesIter = fixedFiles.iterator();
    					while (fixedFilesIter.hasNext()) {
    						SourceFile fixedFile = fixedFilesIter.next();
    						
    						int sourceFileVersionID = fixedFile.getSourceFileVersionID();
    						if (null != similarScores.get(sourceFileVersionID)) {
    							double similarScore = similarScores.get(sourceFileVersionID).doubleValue() + singleValue;
    							similarScores.remove(sourceFileVersionID);
    							similarScores.put(sourceFileVersionID, Double.valueOf(similarScore));
    						} else {
    							similarScores.put(sourceFileVersionID, Double.valueOf(singleValue));
    						}
    					}				
    				}
    			}
    			
    	   		TreeSet<Double> simiScoreSet = new TreeSet<Double>();
        		LinkedList<IntegratedAnalysisValue> integratedAnalysisValueList = new LinkedList<IntegratedAnalysisValue>();
    			
    			Iterator<Integer> similarScoresIter = similarScores.keySet().iterator();
    			while (similarScoresIter.hasNext()) {
    				int sourceFileVersionID = similarScoresIter.next();
    				double similarScore = similarScores.get(sourceFileVersionID).doubleValue();
    				
    				IntegratedAnalysisValue integratedAnalysisValue = new IntegratedAnalysisValue();
    				integratedAnalysisValue.setBugID(bugID);
    				integratedAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
    				integratedAnalysisValue.setSimilarityScore(similarScore);

    				simiScoreSet.add(similarScore);
    				integratedAnalysisValueList.add(integratedAnalysisValue);
    				
    			}
    			
        		double limitSimiScore = 0;
        		int candidateLimitSize = Integer.MAX_VALUE;
        		if (Property.getInstance().getCandidateLimitRate() != 1.0) {
        			candidateLimitSize = (int) (Property.getInstance().getFileCount() * Property.getInstance().getCandidateLimitRate());
        		}
        		
        		if (simiScoreSet.size() > candidateLimitSize) {
        			limitSimiScore = (Double) (simiScoreSet.descendingSet().toArray()[candidateLimitSize -1]);
        		}
        		
        		for (IntegratedAnalysisValue integratedAnalysisValue:integratedAnalysisValueList) {
    				if (integratedAnalysisValue.getVsmScore() >= limitSimiScore) {
    					int updatedColumenCount = integratedAnalysisDAO.updateSimilarScore(integratedAnalysisValue);
    					
    					if (0 == updatedColumenCount) {
    						integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue);
    					}
    				}
    			}
    		}
        }

    }
    

	/**
	 * Analyze similarity between a bug report and its previous bug reports. Then write similarity scores to SimiScore.txt
	 * ex.) Bug ID; Target bug ID#1:Similarity score	Target big ID#2:Similarity score 
	 * 
	 * (non-Javadoc)
	 * @see edu.skku.selab.blp.analysis.IAnalyzer#analyze()
	 */
	public void analyze() throws Exception {
		computeSimilarity();
		prepareData();
		
		ExecutorService executor = Executors.newFixedThreadPool(Property.THREAD_COUNT);

		for (int i = 0; i < bugs.size(); i++) {
			// calculate term count, IDC, TF and IDF
			Runnable worker = new WorkerThread(bugs.get(i));
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
	
	public void computeSimilarity() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectorsExceptComments = getVectorsExceptComments();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectors = getVectors();
		
        for(int i = 0; i < bugs.size(); i++) {
        	Bug bug = bugs.get(i);
        	int firstBugID = bug.getID();
        	ArrayList<AnalysisValue> firstBugVector = bugVectorsExceptComments.get(firstBugID);
        	
        	String openDateString = bug.getOpenDateString();
        	ArrayList<Bug> targetBugs = null;
        	int targetIndex = 0;
        	if (1 < bugDAO.getBugCountWithDate(openDateString)) {
        		targetBugs = bugDAO.getPreviousFixedBugs(openDateString, firstBugID);
        		targetIndex = targetBugs.size();
        	} else {
        		targetBugs = bugs;
        		targetIndex = i;
        	}
        	
            for(int j = 0; j < targetIndex; j++) {
            	int secondBugID = targetBugs.get(j).getID();
            	ArrayList<AnalysisValue> secondBugVector = bugVectors.get(secondBugID);
            	
            	double similarityScore = getCosineValue(firstBugVector, secondBugVector);
            	
            	bugDAO.insertSimilarBugInfo(firstBugID, secondBugID, similarityScore);
            }
        }
	}
	
	public void computeSimilarityForCommitWithPercent() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectorsExceptComments = getVectorsExceptCommentsForCommitWithPercent();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectors = getVectors();

		int start = Math.round((float)(bugs.size()*Property.getInstance().getStartPercent()));
		int end = Math.round((float)(bugs.size()*Property.getInstance().getEndPercent()));
		for(int i = start ; i<end; i++){
        	Bug bug = bugs.get(i);
        	int firstBugID = bug.getID();
        	ArrayList<AnalysisValue> firstBugVector = bugVectorsExceptComments.get(firstBugID);
        	
        	String openDateString = bug.getOpenDateString();
        	ArrayList<Bug> targetBugs = null;
        	int targetIndex = 0;
        	if (1 < bugDAO.getBugCountWithDate(openDateString)) {
        		targetBugs = bugDAO.getPreviousFixedBugs(openDateString, firstBugID);
        		targetIndex = targetBugs.size();
        	} else {
        		targetBugs = bugs;
        		targetIndex = i;
        	}
        	
            for(int j = start; j < targetIndex; j++) {
            	int secondBugID = targetBugs.get(j).getID();
            	ArrayList<AnalysisValue> secondBugVector = bugVectors.get(secondBugID);
            	
            	double similarityScore = getCosineValue(firstBugVector, secondBugVector);
            	
            	bugDAO.insertSimilarBugInfo(firstBugID, secondBugID, similarityScore);
            }
        }
	}
	
	/**
	 * Get cosine value from two vectors
	 * 
	 * @param firstVector
	 * @param secondVector
	 * @return
	 */
	private double getCosineValue(ArrayList<AnalysisValue> firstBugVector, ArrayList<AnalysisValue> secondBugVector) throws Exception {
		double len1 = 0.0;
		double len2 = 0.0;
		double product = 0.0;

		int startTermID = firstBugVector.get(0).getTermID() < secondBugVector.get(0).getTermID() ?
				firstBugVector.get(0).getTermID() : secondBugVector.get(0).getTermID(); 
		int endTermID = firstBugVector.get(firstBugVector.size() - 1).getTermID() > secondBugVector.get(secondBugVector.size() - 1).getTermID() ?
				firstBugVector.get(firstBugVector.size() - 1).getTermID() :
				secondBugVector.get(secondBugVector.size() - 1).getTermID();
		
		double firstTermWeight = 0.0;
		double secondTermWeight = 0.0;
		int j = 0;
		int k = 0;
		for (int i  = startTermID; i <= endTermID; i++) {
			firstTermWeight = 0.0;
			secondTermWeight = 0.0;

			if (j < firstBugVector.size()) {
				if (i == firstBugVector.get(j).getTermID()) {
					firstTermWeight = firstBugVector.get(j++).getTermWeight();
					len1 += firstTermWeight * firstTermWeight;
				}
			}
			
			if (k < secondBugVector.size()) {
				if (i == secondBugVector.get(k).getTermID()) {
					secondTermWeight = secondBugVector.get(k++).getTermWeight();
					len2 += secondTermWeight * secondTermWeight;
				}
			}
			
			product += firstTermWeight * secondTermWeight;
		}
		return ((double) product / (Math.sqrt(len1) * Math.sqrt(len2)));
	}
	
	/**
	 * Get bug vector value 
	 * 
	 * @return <bug ID, <Corpus ID, AnalysisValue>> 
	 * @throws IOException
	 */
	public HashMap<Integer, ArrayList<AnalysisValue>> getVectors() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectors = new HashMap<Integer, ArrayList<AnalysisValue>>();
		for (int i = 0; i < bugs.size(); i++) {
			int bugID = bugs.get(i).getID();
			bugVectors.put(bugID, bugDAO.getBugTermWeightList(bugID));			
		}
		
		return bugVectors;
	}
	
	/**
	 * Get bug vector value except comments in bug report
	 * 
	 * @return <bug ID, <Corpus ID, AnalysisValue>> 
	 * @throws IOException
	 */
	public HashMap<Integer, ArrayList<AnalysisValue>> getVectorsExceptComments() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectors = new HashMap<Integer, ArrayList<AnalysisValue>>();
		
		for(int i = 0 ; i<bugs.size(); i++){
			Bug currentBug = bugs.get(i);
			int bugID = currentBug.getID();
			
			String descriptionPart = currentBug.getCorpus().getDescriptionPart();
			HashSet<String> descriptionCorpus = null;
			if (descriptionPart.length() > 0) {
				descriptionPart = descriptionPart.trim();
				String[] commentCorpusList = descriptionPart.split(" ");
				descriptionCorpus = new HashSet<String>();

				for (int j = 0; j < commentCorpusList.length; j++) {
					descriptionCorpus.add(commentCorpusList[j]);
				}
			}
			
			ArrayList<AnalysisValue> bugTermWeightList = bugDAO.getBugTermWeightList(bugID);
			for (int j = 0; j < bugTermWeightList.size(); j++) {
				if (descriptionCorpus != null && !descriptionCorpus.contains(bugTermWeightList.get(j).getTerm())) {
					bugTermWeightList.remove(j--);
				}
			}
			
			bugVectors.put(bugID, bugDAO.getBugTermWeightList(bugID));			
		}
		
		return bugVectors;
	}

	public HashMap<Integer, ArrayList<AnalysisValue>> getVectorsExceptCommentsForCommitWithPercent() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, ArrayList<AnalysisValue>> bugVectors = new HashMap<Integer, ArrayList<AnalysisValue>>();
		
		int start = Math.round((float)(bugs.size()*Property.getInstance().getStartPercent()));
		int end = Math.round((float)(bugs.size()*Property.getInstance().getEndPercent()));
		for(int i = start ; i<end; i++){
			Bug currentBug = bugs.get(i);
			int bugID = currentBug.getID();
			
			String descriptionPart = currentBug.getCorpus().getDescriptionPart();
			HashSet<String> descriptionCorpus = null;
			if (descriptionPart.length() > 0) {
				descriptionPart = descriptionPart.trim();
				String[] commentCorpusList = descriptionPart.split(" ");
				descriptionCorpus = new HashSet<String>();

				for (int j = 0; j < commentCorpusList.length; j++) {
					descriptionCorpus.add(commentCorpusList[j]);
				}
			}
			
			ArrayList<AnalysisValue> bugTermWeightList = bugDAO.getBugTermWeightList(bugID);
			for (int j = 0; j < bugTermWeightList.size(); j++) {
				if (descriptionCorpus != null && !descriptionCorpus.contains(bugTermWeightList.get(j).getTerm())) {
					bugTermWeightList.remove(j--);
				}
			}
			
			bugVectors.put(bugID, bugDAO.getBugTermWeightList(bugID));			
		}
		
		return bugVectors;
	}

	 


	/**
	 * Analyze similarity between a bug report and its previous bug reports. Then write similarity scores to SimiScore.txt
	 * ex.) Bug ID; Target bug ID#1:Similarity score	Target big ID#2:Similarity score 
	 * 
	 * (non-Javadoc)
	 * @see edu.skku.selab.blp.analysis.IAnalyzer#analyze()
	 */
	public void analyzeForCommit() throws Exception {
		computeSimilarity();
		prepareData();
		
		ExecutorService executor = Executors.newFixedThreadPool(Property.THREAD_COUNT);

		for (int i = 0; i < bugs.size(); i++) {
			// calculate term count, IDC, TF and IDF
			Runnable worker = new WorkerThreadForCommit(bugs.get(i));
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
	
	public void analyzeForCommitWithPercent() throws Exception {
		computeSimilarityForCommitWithPercent();
		prepareData();
		
		ExecutorService executor = Executors.newFixedThreadPool(Property.THREAD_COUNT);
		int start = Math.round((float)(bugs.size()*Property.getInstance().getStartPercent()));
		int end = Math.round((float)(bugs.size()*Property.getInstance().getEndPercent()));
		for(int i = start ; i<end; i++){
			// calculate term count, IDC, TF and IDF
			Runnable worker = new WorkerThreadForCommit(bugs.get(i));
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
	
	private class WorkerThreadForCommit implements Runnable {
    	private Bug bug;
    	
        public WorkerThreadForCommit(Bug bug){
            this.bug = bug;
        }
     
        @Override
        public void run() {
			// Compute similarity between Bug report & source files
        	
        	try {
        		calculateSimilarScore(bug);
        	} catch (Exception e) {
        		logger.error(e.getMessage());
        	}
        }
        
        private void calculateSimilarScore(Bug bug) throws Exception {
    		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
    		SourceFileDAO sourceFileDAO = new SourceFileDAO(); 
			BugDAO bugDAO = new BugDAO();
    		
    		int bugID = bug.getID();
    		HashMap<Integer, Double> similarScores = new HashMap<Integer, Double>(); 
    		HashSet<SimilarBugInfo> similarBugInfos = similarBugInfosMap.get(bugID);
    		if (null != similarBugInfos) {
    			Iterator<SimilarBugInfo> similarBugInfosIter = similarBugInfos.iterator();
    			while (similarBugInfosIter.hasNext()) {
    				SimilarBugInfo similarBugInfo = similarBugInfosIter.next();
    				
    				HashSet<SourceFile> fixedFiles = fixedFilesMap.get(similarBugInfo.getSimilarBugID());
    				if (null != fixedFiles) {
    					int fixedFilesCount = fixedFiles.size();
    					double singleValue = similarBugInfo.getSimilarityScore() / fixedFilesCount;
    					Iterator<SourceFile> fixedFilesIter = fixedFiles.iterator();
    					while (fixedFilesIter.hasNext()) {
    						SourceFile fixedFile = fixedFilesIter.next();
    						int sourceFileID = sourceFileDAO.getSourceFileID(fixedFile.getName());
    						
    						int sourceFileVersionID = fixedFile.getSourceFileVersionID();
    						if (null != similarScores.get(sourceFileVersionID)) {
    							double similarScore = similarScores.get(sourceFileVersionID).doubleValue() + singleValue;
    							similarScores.remove(sourceFileVersionID);
    							similarScores.put(sourceFileID, Double.valueOf(similarScore));
    						} else {
    							similarScores.put(sourceFileID, Double.valueOf(singleValue));
    						}
    					}				
    				}
    			}
    			
    	   		TreeSet<Double> simiScoreSet = new TreeSet<Double>();
        		LinkedList<IntegratedAnalysisValue> integratedAnalysisValueList = new LinkedList<IntegratedAnalysisValue>();
    			
    			Iterator<Integer> similarScoresIter = similarScores.keySet().iterator();
    			while (similarScoresIter.hasNext()) {
    				int sourceFileID = similarScoresIter.next();
    				double similarScore = similarScores.get(sourceFileID).doubleValue();
    				
    				IntegratedAnalysisValue integratedAnalysisValue = new IntegratedAnalysisValue();
    				integratedAnalysisValue.setBugID(bugID);
    				integratedAnalysisValue.setSourceFileVersionID(sourceFileID);
    				integratedAnalysisValue.setSimilarityScore(similarScore);

    				simiScoreSet.add(similarScore);
    				integratedAnalysisValueList.add(integratedAnalysisValue);
    				
    			}
    			
        		double limitSimiScore = 0;
        		int candidateLimitSize = Integer.MAX_VALUE;
        		if (Property.getInstance().getCandidateLimitRate() != 1.0) {
        			candidateLimitSize = (int) (Property.getInstance().getFileCount() * Property.getInstance().getCandidateLimitRate());
        		}
        		
        		if (simiScoreSet.size() > candidateLimitSize) {
        			limitSimiScore = (Double) (simiScoreSet.descendingSet().toArray()[candidateLimitSize -1]);
        		}
        		
        		for (IntegratedAnalysisValue integratedAnalysisValue:integratedAnalysisValueList) {
    				if (integratedAnalysisValue.getVsmScore() >= limitSimiScore) {
    					int sourceFileID = integratedAnalysisValue.getSourceFileVersionID();
    					int sourceFileVerID = sourceFileDAO.getSourceFileVersionID(sourceFileID, bug.getVersion());
    					integratedAnalysisValue.setSourceFileVersionID(sourceFileVerID);
    					int updatedColumenCount = integratedAnalysisDAO.updateSimilarScore(integratedAnalysisValue);
    					
    					
    				}
    			}
    		}
        }

    }
    
	
}
