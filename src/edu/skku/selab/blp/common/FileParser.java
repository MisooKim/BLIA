/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.skku.selab.blp.utils.Splitter;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class FileParser {
	private CompilationUnit compilationUnit;
	private String sourceString;
	
	public static final int CLASS_PART = 1;
	public static final int METHOD_PART = 2;
	public static final int VARIABLE_PART = 3;
	public static final int COMMENT_PART = 4;
	public static final int ALL_PART = 5;
	
	public FileParser(File file) {
		compilationUnit = null;
		ASTCreator creator = new ASTCreator();
		creator.getFileContent(file);
		compilationUnit = creator.getCompilationUnit();
		sourceString = creator.getContent();
	}

	public int getLinesOfCode() {
		deleteNoNeededNode();
		String lines[] = compilationUnit.toString().split("\n");
		int len = 0;
		String as[];
		int j = (as = lines).length;
		for (int i = 0; i < j; i++) {
			String strLine = as[i];
			if (!strLine.trim().equals(""))
				len++;
		}

		return len;
	}
	
	private String[] splitContent(String content) {
		String tokensInSourceCode[] = Splitter.splitSourceCode(content);
		StringBuffer sourceCodeContentBuffer = new StringBuffer();
		String as[];
		int j = (as = tokensInSourceCode).length;
		for (int i = 0; i < j; i++) {
			String token = as[i];
			sourceCodeContentBuffer.append((new StringBuilder(String.valueOf(token))).append(" ").toString());
		}

		String processedConent = sourceCodeContentBuffer.toString().toLowerCase();
		return processedConent.split(" ");
	}
	
	public String[] getStructuredContent(int type) {
		String content = "";
//		System.out.println(content);
		
		switch (type) {
		case CLASS_PART:
			content =  getAllClassNames();
			break;
		case METHOD_PART:
			content =  getAllMethodNames();
			break;
		case VARIABLE_PART:
			content =  getAllVariableNames();
			break;
		case COMMENT_PART:
			content =  getAllComments();
			break;
		case ALL_PART:
		default:
			content = getAllStructuredInfos();
			break;
		}
		
		return splitContent(content);
	}
	
	public String[] getStructuredContent() {
		String allStructuredInfos = getAllStructuredInfos();
//		System.out.println(allStructuredInfos);
		
		String tokensInSourceCode[] = Splitter.splitSourceCode(allStructuredInfos);
		StringBuffer sourceCodeContentBuffer = new StringBuffer();
		String as[];
		int j = (as = tokensInSourceCode).length;
		for (int i = 0; i < j; i++) {
			String token = as[i];
			sourceCodeContentBuffer.append((new StringBuilder(String.valueOf(token))).append(" ").toString());
		}

		String content = sourceCodeContentBuffer.toString().toLowerCase();
		return content.split(" ");
	}
	
	public String[] getContent() {
		String tokensInSourceCode[] = Splitter.splitSourceCode(deleteNoNeededNode());
		StringBuffer sourceCodeContentBuffer = new StringBuffer();
		String as[];
		int j = (as = tokensInSourceCode).length;
		for (int i = 0; i < j; i++) {
			String token = as[i];
			sourceCodeContentBuffer.append((new StringBuilder(String.valueOf(token))).append(" ").toString());
		}

		String content = sourceCodeContentBuffer.toString().toLowerCase();
		return content.split(" ");
	}

	public String[] getClassNameAndMethodName() {
		String content = (new StringBuilder(String.valueOf(getAllClassNames()))).append(" ")
				.append(getAllMethodNames()).toString().toLowerCase();
		return content.split(" ");
	}

	public String getPackageName() {
		return compilationUnit.getPackage() != null ?
				compilationUnit.getPackage().getName().getFullyQualifiedName() : "";
	}
	
	public String getAllVariableNames() {
    	final ArrayList<String> structuredInfoList = new ArrayList<String>();
    	
    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(SingleVariableDeclaration node)
            {
//            	System.out.printf("single variable Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});
    	
    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(VariableDeclarationFragment node)
            {
//            	System.out.printf("variable declaration Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});
    	
		String allStructuredInfoNames = "";
		for (Iterator<String> iterator = structuredInfoList.iterator(); iterator.hasNext();) {
			String structuredInfoName = (String) iterator.next();
			allStructuredInfoNames = (new StringBuilder(String.valueOf(allStructuredInfoNames)))
					.append(structuredInfoName).append(" ").toString();
		}
	
		return allStructuredInfoNames.trim();
	}
	
	public String getAllComments() {
		final ArrayList<String> structuredInfoList = new ArrayList<String>();
		
	   	for (Comment comment : (List<Comment>) compilationUnit.getCommentList()) {
    		comment.accept(new ASTVisitor() {
                public boolean visit(Javadoc node)
                {
//                	System.out.printf("ClassNames: %s, comment text: %s\n", getAllClassNames(), node.toString());
                	String javadocComment = node.toString();
                	javadocComment = javadocComment.split("[/][*][*]")[1];
                	javadocComment = javadocComment.split("[*][/]")[0];
                	String[]  commentLines = javadocComment.split("\n");
                	
                	for (String line : commentLines) {
                		if (line.contains("@author") || line.contains("@version") || line.contains("@since") ) {
                			continue;
                		}
                    	String[] words = line.split("[*\\s]");
                    	for (String word : words) {
                    		if (word.length() > 0) {
                    			if ( (word.equalsIgnoreCase("@param")) || (word.equalsIgnoreCase("@return")) || (word.equalsIgnoreCase("@exception")) ||
                    					(word.equalsIgnoreCase("@see")) || (word.equalsIgnoreCase("@serial")) || (word.equalsIgnoreCase("@deprecated")) )  {
                    				continue;
                    			}
                    			
    	                    	structuredInfoList.add(word);                		
//    	                		System.out.printf("ClassNames: %s, javadocComment text: %s\n", getAllClassNames(), word);
                    		}
                    	}
                	}
                    return super.visit(node);
                }
        	});
        	
    		comment.accept(new ASTVisitor() {
                public boolean visit(LineComment node)
                {
                	int beginIndex = node.getStartPosition();
                	int endIndex = beginIndex + node.getLength(); 
                	String lineComment = sourceString.substring(beginIndex + 2, endIndex).trim();
                	
                	String[] words = lineComment.split("[\\s]");
                	for (String word : words) {
                		if (word.length() > 0) {
//	                		System.out.printf("ClassNames: %s, BlockComment text: %s\n", getAllClassNames(), word);
                			structuredInfoList.add(word);                		
                		}
                	}
                    return super.visit(node);
                }
        	});
        	
    		comment.accept(new ASTVisitor() {
                public boolean visit(BlockComment node)
                {
                	int beginIndex = node.getStartPosition();
                	int endIndex = beginIndex + node.getLength(); 
                	String blockComment = sourceString.substring(beginIndex, endIndex);
                	blockComment = blockComment.split("[/][*]")[1];
                	blockComment = blockComment.split("[*][/]")[0];
                	
//                	System.out.printf("ClassNames: %s, original BlockComment text: %s\n", getAllClassNames(), blockComment);
                	String[] words = blockComment.split("[*\\s]");
                	for (String word : words) {
                		if (word.length() > 0) {
//	                		System.out.printf("ClassNames: %s, BlockComment text: %s\n", getAllClassNames(), word);
                			structuredInfoList.add(word);                		
                		}
                	}
                    return super.visit(node);
                }
        	});
		}
    
		String allStructuredInfoNames = "";
		for (Iterator<String> iterator = structuredInfoList.iterator(); iterator.hasNext();) {
			String structuredInfoName = (String) iterator.next();
			allStructuredInfoNames = (new StringBuilder(String.valueOf(allStructuredInfoNames)))
					.append(structuredInfoName).append(" ").toString();
		}
	
		return allStructuredInfoNames.trim();
	}
	
	public String getAllStructuredInfos() {
    	final ArrayList<String> structuredInfoList = new ArrayList<String>();

    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(TypeDeclaration node)
            {
//            	System.out.printf("class Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});

    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(EnumDeclaration node)
            {
//            	System.out.printf("enum Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});

    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(EnumConstantDeclaration node)
            {
//            	System.out.printf("enum constant Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});

    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node)
            {
//            	System.out.printf("method Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});
    	
    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(SingleVariableDeclaration node)
            {
//            	System.out.printf("single variable Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});
    	
    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(VariableDeclarationFragment node)
            {
//            	System.out.printf("variable declaration Node: %s\n", node.getName().getIdentifier());
            	structuredInfoList.add(node.getName().getIdentifier());
                return super.visit(node);
            }
    	});
    	
    	for (Comment comment : (List<Comment>) compilationUnit.getCommentList()) {
    		comment.accept(new ASTVisitor() {
                public boolean visit(Javadoc node)
                {
//                	System.out.printf("ClassNames: %s, comment text: %s\n", getAllClassNames(), node.toString());
                	String javadocComment = node.toString();
                	javadocComment = javadocComment.split("[/][*][*]")[1];
                	javadocComment = javadocComment.split("[*][/]")[0];
                	String[]  commentLines = javadocComment.split("\n");
                	
                	for (String line : commentLines) {
                		if (line.contains("@author") || line.contains("@version") || line.contains("@since") ) {
                			continue;
                		}
                    	String[] words = line.split("[*\\s]");
                    	for (String word : words) {
                    		if (word.length() > 0) {
                    			if ( (word.equalsIgnoreCase("@param")) || (word.equalsIgnoreCase("@return")) || (word.equalsIgnoreCase("@exception")) ||
                    					(word.equalsIgnoreCase("@see")) || (word.equalsIgnoreCase("@serial")) || (word.equalsIgnoreCase("@deprecated")) )  {
                    				continue;
                    			}
                    			
    	                    	structuredInfoList.add(word);                		
//    	                		System.out.printf("ClassNames: %s, javadocComment text: %s\n", getAllClassNames(), word);
                    		}
                    	}
                	}
                    return super.visit(node);
                }
        	});
        	
    		comment.accept(new ASTVisitor() {
                public boolean visit(LineComment node)
                {
                	int beginIndex = node.getStartPosition();
                	int endIndex = beginIndex + node.getLength(); 
                	String lineComment = sourceString.substring(beginIndex + 2, endIndex).trim();
                	
                	String[] words = lineComment.split("[\\s]");
                	for (String word : words) {
                		if (word.length() > 0) {
//	                		System.out.printf("ClassNames: %s, BlockComment text: %s\n", getAllClassNames(), word);
                			structuredInfoList.add(word);                		
                		}
                	}
                    return super.visit(node);
                }
        	});
        	
    		comment.accept(new ASTVisitor() {
                public boolean visit(BlockComment node)
                {
                	int beginIndex = node.getStartPosition();
                	int endIndex = beginIndex + node.getLength(); 
                	String blockComment = sourceString.substring(beginIndex, endIndex);
                	blockComment = blockComment.split("[/][*]")[1];
                	blockComment = blockComment.split("[*][/]")[0];
                	
//                	System.out.printf("ClassNames: %s, original BlockComment text: %s\n", getAllClassNames(), blockComment);
                	String[] words = blockComment.split("[*\\s]");
                	for (String word : words) {
                		if (word.length() > 0) {
//	                		System.out.printf("ClassNames: %s, BlockComment text: %s\n", getAllClassNames(), word);
                			structuredInfoList.add(word);                		
                		}
                	}
                    return super.visit(node);
                }
        	});
		}
    
		String allStructuredInfoNames = "";
		for (Iterator<String> iterator = structuredInfoList.iterator(); iterator.hasNext();) {
			String structuredInfoName = (String) iterator.next();
			allStructuredInfoNames = (new StringBuilder(String.valueOf(allStructuredInfoNames)))
					.append(structuredInfoName).append(" ").toString();
		}
	
		return allStructuredInfoNames.trim();
    }

	private String getAllMethodNames() {
		ArrayList<String> methodNameList = new ArrayList<String>();
		for (int i = 0; i < compilationUnit.types().size(); i++) {
			TypeDeclaration type = (TypeDeclaration) compilationUnit.types().get(i);
			MethodDeclaration methodDecls[] = type.getMethods();
			MethodDeclaration methodDeclaration[];
			int k = (methodDeclaration = methodDecls).length;
			for (int j = 0; j < k; j++) {
				MethodDeclaration methodDecl = methodDeclaration[j];
				String methodName = methodDecl.getName().getFullyQualifiedName();
				methodNameList.add(methodName);
			}

		}

		String allMethodName = "";
		for (Iterator<String> iterator = methodNameList.iterator(); iterator.hasNext();) {
			String methodName = (String) iterator.next();
			allMethodName = (new StringBuilder(String.valueOf(allMethodName)))
					.append(methodName).append(" ").toString();
		}

		return allMethodName.trim();
	}
	
	private String getAllClassNames() {
		ArrayList<String> classNameList = new ArrayList<String>();
		for (int i = 0; i < compilationUnit.types().size(); i++) {
			TypeDeclaration type = (TypeDeclaration) compilationUnit.types().get(i);
			String name = type.getName().getFullyQualifiedName();
			classNameList.add(name);
		}

		String allClassName = "";
		for (Iterator<String> iterator = classNameList.iterator(); iterator.hasNext();) {
			String className = (String) iterator.next();
			allClassName = (new StringBuilder(String.valueOf(allClassName))).append(className).append(" ").toString();
		}

		return allClassName.trim();
	}

	private String deleteNoNeededNode() {
		compilationUnit.accept(new ASTVisitor() {
			public boolean visit(AnnotationTypeDeclaration node) {
				if (node.isPackageMemberTypeDeclaration())
					node.delete();
				return super.visit(node);
			}
		});
		
		compilationUnit.accept(new ASTVisitor() {
			public boolean visit(PackageDeclaration node) {
				node.delete();
				return super.visit(node);
			}
		});
		
		compilationUnit.accept(new ASTVisitor() {
			public boolean visit(ImportDeclaration node) {
				node.delete();
				return super.visit(node);
			}
		});
		
		return compilationUnit.toString();
	}
	
    public ArrayList<String> getImportedClasses()
    {
    	final ArrayList<String> importedClasses = new ArrayList<String>();
    	
    	compilationUnit.accept(new ASTVisitor() {
            public boolean visit(ImportDeclaration node)
            {
//            	System.out.printf("imported Node: %s\n", node.getName().toString());
            	importedClasses.add(node.getName().toString());
                return super.visit(node);
            }
    	});
    	
    	return importedClasses;
    }
}