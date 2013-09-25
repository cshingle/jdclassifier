package com.decker.jdclassifier;

public class Result extends JDDocument implements Comparable<Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3437633001897848541L;
	public final double scoring;
	public final int matches;

	Result(JDDocument document,double score, int matches ) {
		super(document.name,document.description,document.maxWeight);
		this.scoring = score/document.maxWeight;
		this.matches = matches;
	}

	@Override
	public int compareTo(Result that) {
		if(this.scoring > that.scoring)
			return -1;
		if(this.scoring < that.scoring)
			return 1;
		
		if(this.matches > that.matches)
			return -1;
		if(this.matches < that.matches)
			return 1;
		
		return 0;
	}
	


}
