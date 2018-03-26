/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.skku.selab.blp.blia.analysis.BLIA;
import edu.skku.selab.blp.db.dao.DbUtil;
import edu.skku.selab.blp.evaluation.Evaluator;
import edu.skku.selab.blp.evaluation.EvaluatorForMethodLevel;
import edu.skku.selab.blp.utils.Util;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BLP {
	final static Logger logger = LoggerFactory.getLogger(BLP.class);
	private static void initializeDB() throws Exception {
		Property prop = Property.getInstance();
		
		DbUtil dbUtil = new DbUtil();
		String productName[] = {
				Property.ASPECTJ,
				Property.ECLIPSE,
				Property.SWT,
				Property.ZXING};
		
		for (int i = 0; i < productName.length; i++) {
			dbUtil.openConnetion(productName[i]);

			dbUtil.dropAllAnalysisTables();
			dbUtil.createAllAnalysisTables();

			prop.setProductName(productName[i]);
			dbUtil.initializeAllData();

			dbUtil.closeConnection();
		}
		
		dbUtil.openEvaluationDbConnection();

		dbUtil.dropEvaluationTable();
		dbUtil.createEvaluationTable();
		
		dbUtil.initializeExperimentResultData();
		
		dbUtil.closeConnection();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Load properties data to run BLIA
		long startTime = System.currentTimeMillis();
		logger.trace("[START] BLIA Evaluation START");
		Property prop = Property.loadInstance();
		logger.info("[DATA] "+prop.getProductName()+" " +prop.getAlpha()+" "+prop.getBeta());
		logger.trace("Start InitializeDB");
		// initialize DB and create all tables.
		initializeDB();
		logger.trace("Finish InitializeDB");

		logger.trace("Start BLIA");
		// Run BLIA algorithm
		BLIA blia = new BLIA();
		blia.run();
		logger.trace("Finish BLIA");
		
		
		String algorithmDescription = "[BLIA] alpha: " + prop.getAlpha() +
				", beta: " + prop.getBeta() + ", gamma: " + prop.getGamma() + ", pastDays: " + prop.getPastDays() +
				", cadidateLimitRate: " + prop.getCandidateLimitRate();
		logger.trace("Start Evaluator for File Level");
		// Evaluate the accuracy result of BLIA
		Evaluator evaluator1 = new Evaluator(prop.getProductName(),
				Evaluator.ALG_BLIA_FILE, algorithmDescription, prop.getAlpha(),
				prop.getBeta(), prop.getGamma(), prop.getPastDays(),
				prop.getCandidateLimitRate());
		evaluator1.evaluate();
		logger.trace("Finish Evaluator for File Level");
		
		logger.trace("Start Evaluator for Method Level");
		Evaluator evaluator2 = new EvaluatorForMethodLevel(prop.getProductName(),
				EvaluatorForMethodLevel.ALG_BLIA_METHOD, algorithmDescription, prop.getAlpha(),
				prop.getBeta(), prop.getGamma(), prop.getPastDays(),
				prop.getCandidateLimitRate());
		evaluator2.evaluate();
		logger.trace("Finish Evaluator for Method Level");
		logger.trace("[DONE] BLIA Evaluation FINISH(Total "+Util.getElapsedTimeSting(startTime)+"  sec)");
	}
}
