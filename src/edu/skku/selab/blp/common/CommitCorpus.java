package edu.skku.selab.blp.common;

public class CommitCorpus {
	
	private String msgPart;
	private String diffPart;
	private double msgCorpusNorm;
	private double diffCorpusNorm;
	public String getMsgPart() {
		return msgPart;
	}
	public void setMsgPart(String msgPart) {
		this.msgPart = msgPart;
	}
	public String getDiffPart() {
		return diffPart;
	}
	public void setDiffPart(String diffPart) {
		this.diffPart = diffPart;
	}
	public double getMsgCorpusNorm() {
		return msgCorpusNorm;
	}
	public void setMsgCorpusNorm(double msgCorpusNorm) {
		this.msgCorpusNorm = msgCorpusNorm;
	}
	public double getDiffCorpusNorm() {
		return diffCorpusNorm;
	}
	public void setDiffCorpusNorm(double diffCorpusNorm) {
		this.diffCorpusNorm = diffCorpusNorm;
	}
	@Override
	public String toString() {
		return "CommitCorpus [msgPart=" + msgPart + ", diffPart=" + diffPart + ", msgCorpusNorm=" + msgCorpusNorm
				+ ", diffCorpusNorm=" + diffCorpusNorm + "]";
	}
	
	

}
