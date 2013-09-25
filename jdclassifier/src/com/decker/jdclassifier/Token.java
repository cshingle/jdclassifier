package com.decker.jdclassifier;

import java.util.Map.Entry;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple4;

public class Token {
	public final String document;
	public final String artifact;
	public final int line;
	public final int column;
	protected final int weight;
	
	public Token(JDDocument document,String artifact,int line,int column)
	{
		this(document.name,artifact,line,column);
	}
	public Token(String documentName,String artifact, int line,int column)
	{
		this.document = documentName.toLowerCase();
		this.artifact = artifact;
		this.line = line;
		this.column = column;
		this.weight = 1;
	}
	Token(Entry<Tuple4<String, Integer, Integer,Object>, Integer> entry)
	{
		this.artifact = entry.getKey().a;
		this.line = entry.getKey().b;
		this.column = entry.getKey().c;
		this.document = (String) entry.getKey().d;
		this.weight = entry.getValue();
	}
	Token(Token token,int weight)
	{
		this.document = token.document;
		this.artifact = token.artifact;
		this.line = token.line;
		this.column = token.column;
		this.weight = weight;
	}

	Fun.Tuple4<String,Integer,Integer,Object> getKey()
	{
		return new Fun.Tuple4<String,Integer,Integer,Object>(this.artifact, this.line, this.column,this.document);
	}
	Integer getValue()
	{
		return weight;
	}
}
