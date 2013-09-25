package com.decker.jdclassifier;

import java.util.Map.Entry;

import org.mapdb.Fun.Tuple4;

public class KNNToken extends Token implements Comparable<KNNToken>{
	/**
	 * The distance this token is from the n;
	 */
	public final double distance;
	/**
	 * weight * (1-(this.distance/maxDistance))
	 */
	public double score = -1;

	KNNToken(Token sample, Entry<Tuple4<String, Integer, Integer, Object>, Integer> entry) {
		super(entry);

		// C = Square Root of A squared + B Squared.
		this.distance = Math.pow(Math.pow(Math.abs(this.line - sample.line),2.0) + Math.pow(Math.abs(this.column - sample.column),2.0),0.5);
	}
	
	void calculateScore(double maxDistance)
	{
		if(maxDistance > 0)
			this.score = this.weight * (1-(this.distance/maxDistance));
		else
			this.score = this.weight;
	}

	@Override
	public int compareTo(KNNToken other) {
		//sort by score first.
		if(this.score > other.score)
			return -1;
		if(this.score < other.score)
			return 1;

		//if score has not been calculated or is a tie sort by distance
		if(this.distance < other.distance)
			return -1;
		if(this.distance > other.distance)
			return 1;
		return 0;
	}

}
