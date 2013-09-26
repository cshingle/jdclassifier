package com.decker.jdclassifier;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenizer extends AbstractTokenizer {
	Queue<Token> tokenQueue = new ArrayDeque<Token>();
	Token buffer = null;
	int line = 0;
	
	public StringTokenizer(String[] strings)
	{
		Pattern pattern = Pattern.compile("\\b[A-Za-z0-9_'-]{2,32}\\b|^[A-Za-z0-9_'-]{2,32}\\b|\\b[A-Za-z0-9_'-]{2,32}$");
		for(String string : strings)
		{
			Matcher matcher = pattern.matcher(string);
			while(matcher.find())
			{
				String match = matcher.group();
				if(!stopwords.contains(match))
					this.tokenQueue.add(new Token(string.toLowerCase(), line, matcher.start()));
			}
			line++;				
		}			
	}
	
	@Override
	public boolean hasNext() {
		if(buffer != null)
			return true;
		buffer = this.tokenQueue.poll();
		return buffer != null;
	}

	@Override
	public Token next() {
		if(buffer == null)
			hasNext();
		
		Token token = this.buffer;
		this.buffer = null;
		return token;
	}

}
