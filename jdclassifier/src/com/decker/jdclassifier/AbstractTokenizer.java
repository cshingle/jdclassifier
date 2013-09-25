package com.decker.jdclassifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTokenizer {
	protected static Logger logger = LoggerFactory.getLogger(AbstractTokenizer.class);
	public static Set<String> stopwords;
	
	static Object object = new Object();
	static{
		stopwords = DBMaker.newTempHashSet();
		String stopwordsfile = AbstractTokenizer.class.getPackage().getName().replaceAll("[.]", "/") + "/stopwords";
		BufferedReader input = null;
		InputStream in = null;
		try{
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			in = classLoader.getResourceAsStream(stopwordsfile);

			input = new BufferedReader(new InputStreamReader(in,"utf8"));
			String line;
			while ((line = input.readLine()) != null) {
				stopwords.add(line.trim());
			}
		} catch (UnsupportedEncodingException e) {

		} catch (IOException e) {
			logger.error("Error Creating Stopwords database from stopwords file",e);
		}finally {
			if(input != null)
				try {
					input.close();
				} catch (IOException e) {}
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {}
		}
	}

	public abstract boolean hasNext();
	public abstract Token next();
}
