/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.common.FileDetector;
import edu.skku.selab.blp.common.FileParser;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.utils.Stem;
import edu.skku.selab.blp.utils.Stopword;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class StructuredSourceFileCorpusCreator extends SourceFileCorpusCreator {
	public SourceFileCorpus create(File file) {
		FileParser parser = new FileParser(file);
		String fileName = parser.getPackageName();
		if (fileName.trim().equals("")) {
			fileName = file.getName();
		} else {
			fileName = (new StringBuilder(String.valueOf(fileName)))
					.append(".").append(file.getName()).toString();
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		
		// parser.getImportedClassed() function should be called before calling parser.getContents()
		ArrayList<String> importedClasses = parser.getImportedClasses();
		
		String classPart = parser.getStructuredContentWithFullyIdentifier(FileParser.CLASS_PART);		
		String classContents[] = parser.getStructuredContent(FileParser.CLASS_PART);
		classPart += " " + stemContent(classContents);

		String methodPart = parser.getStructuredContentWithFullyIdentifier(FileParser.METHOD_PART);	
		String methodContents[] = parser.getStructuredContent(FileParser.METHOD_PART);
		methodPart += " " + stemContent(methodContents);

		String variablePart = parser.getStructuredContentWithFullyIdentifier(FileParser.VARIABLE_PART);	
		String variableContents[] = parser.getStructuredContent(FileParser.VARIABLE_PART);
		variablePart += " " + stemContent(variableContents);

		String commentContents[] = parser.getStructuredContent(FileParser.COMMENT_PART);
		String commentPart = stemContent(commentContents);
		
		String sourceCodeContent = classPart + " " + methodPart + " " + variablePart + " " + commentPart;
		
		SourceFileCorpus corpus = new SourceFileCorpus();
		corpus.setJavaFilePath(file.getAbsolutePath());
		corpus.setJavaFileFullClassName(fileName);
		corpus.setImportedClasses(importedClasses);
		corpus.setContent(sourceCodeContent);
		corpus.setClassPart(classPart);
		corpus.setMethodPart(methodPart);
		corpus.setVariablePart(variablePart);
		corpus.setCommentPart(commentPart);
		return corpus;
    }
	
	////////////////////////////////////////////////////////////////////	
	/* (non-Javadoc)
	 * @see edu.skku.selab.blia.indexer.ICorpus#create()
	 */
	public void create(String version) throws Exception {
		Property property = Property.getInstance();
		FileDetector detector = new FileDetector("java");
		File files[] = detector.detect(property.getSourceCodeDir());
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		String productName = property.getProductName();
		int totalCoupusCount = SourceFileDAO.INIT_TOTAL_COUPUS_COUNT;
		double lengthScore = SourceFileDAO.INIT_LENGTH_SCORE;

		// debug code
//		System.out.printf("Source code dir: %s\n", property.getSourceCodeDir());
//		
//		FileWriter tempWriter = new FileWriter(".\\temp.txt");
//		for (int i = 0; i < files.length; i++) {
//			tempWriter.write("[" + (i+1) + "] " + files[i].getAbsolutePath() + "\n"); 
//			System.out.printf("[%d] %s\n", i + 1, files[i].getAbsolutePath());
//		}
//		tempWriter.close();

		int count = 0;
		TreeSet<String> nameSet = new TreeSet<String>();
		File afile[];
		int j = (afile = files).length;
		for (int i = 0; i < j; i++) {
			File file = afile[i];
			
			SourceFileCorpus corpus = create(file);

			if (corpus != null && !nameSet.contains(corpus.getJavaFileFullClassName())) {
				String fileName = corpus.getJavaFileFullClassName();
				if (!corpus.getJavaFileFullClassName().endsWith(".java")) {
					fileName += ".java";
				}
				
				String filePath = file.getAbsolutePath().replace("\\", ".");
				filePath = filePath.replace("/", ".");
				
				// Wrong file that has invalid package or path 
				if (!filePath.endsWith(fileName)) {
//					System.err.printf("[StructuredSourceFileCorpusCreator.create()] %s, %s\n", filePath, fileName);
					continue;
				}
				
				sourceFileDAO.insertSourceFile(fileName, productName);
				sourceFileDAO.insertCorpusSet(fileName, productName, version, corpus, totalCoupusCount, lengthScore);
				sourceFileDAO.insertImportedClasses(fileName, productName, version, corpus.getImportedClasses());
				nameSet.add(corpus.getJavaFileFullClassName());
				count++;
			}
		}

		property.setFileCount(count);
	}
	
}
