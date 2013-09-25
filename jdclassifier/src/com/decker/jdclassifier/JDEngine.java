package com.decker.jdclassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple4;

import com.decker.jdclassifier.Classifier.Classify;
import com.decker.jdclassifier.JDClassifier.JDParameter;

public abstract class JDEngine {
	protected final File dbFile;
	protected final DB db;
	protected final ConcurrentNavigableMap<Fun.Tuple4<String,Integer,Integer,Object>,Integer> knnMap;
	protected final ConcurrentNavigableMap<String,JDDocument> documentMap;
	final ThreadPoolExecutor queryPool;

	protected JDEngine(Map<JDParameter,String> parameters) throws JDClassifierException
	{
		String database = parameters.get(JDClassifier.JDParameter.DATABASE_NAME);

		if(database == null || database.length() < 2)
			throw new JDClassifierException(String.format("Invalid Database Name",database));

		this.dbFile = new File(database);

		boolean exists = this.dbFile.exists();
		this.db = DBMaker
				.newFileDB(dbFile)
				.closeOnJvmShutdown()
				.transactionDisable()
				.make();

		if(!exists)
		{
			this.knnMap = db.createTreeMap("KNNMap").keySerializer(BTreeKeySerializer.TUPLE4).make();
			this.documentMap = db.createTreeMap("DocumentMap").make();
		}
		else
		{
			this.knnMap = db.getTreeMap("KNNMap");
			this.documentMap = db.getTreeMap("DocumentMap");
		}

		this.queryPool = new ThreadPoolExecutor(8,8,90,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
	}

	protected Token setToken(Token token)
	{
		if(this.knnMap.putIfAbsent(token.getKey(), token.getValue()) != null)
		{
			Integer currentWeight;
			while(true)
			{
				currentWeight = this.knnMap.get(token.getKey());
				if(this.knnMap.replace(token.getKey(), currentWeight,currentWeight + 1))
					break;
			}
		}
		
		increaseWeight(token.document,token.weight);
		return token;
	}
	protected Token reduceToken(Token token)
	{
		//TODO make this atomic
		//TODO create a way to reduce the Document maxWeight
		//Synchronize this for now. This is only necessary if the same token is set by two threads.
		synchronized(this.knnMap)
		{
			Integer weight = this.knnMap.get(token.getKey());
			if(weight != null)
			{
				Token newToken = new Token(token,weight - token.weight);
				if(newToken.weight > 0)
				{
					this.knnMap.put(newToken.getKey(),newToken.getValue());
					return newToken;
				}
				else
				{
					this.knnMap.remove(newToken.getKey());
					return null;
				}
			}
		}
		return token;
	}
	public JDDocument getDocument(String documentName)
	{
		return this.documentMap.get(documentName.toLowerCase());
	}
	public JDDocument[] getDocuments()
	{
		List<JDDocument> documents = new ArrayList<JDDocument>();
		for(Entry<String,JDDocument> entry :this.documentMap.entrySet())
			documents.add(entry.getValue());

		return documents.toArray(new JDDocument[0]);
	}

	protected JDDocument increaseWeight(String documentName,int maxWeight)
	{
		JDDocument newDocument = new JDDocument(documentName,"",maxWeight);
		if(this.documentMap.putIfAbsent(documentName, newDocument) != null)
		{
			JDDocument currentDocument;
			while(true)
			{
				currentDocument = this.documentMap.get(documentName);
				if(currentDocument != null)
					newDocument = new JDDocument(currentDocument,maxWeight);

				if(currentDocument.maxWeight >= maxWeight)
					return currentDocument;
				if(this.documentMap.replace(documentName, currentDocument,newDocument))
					return newDocument;
			}
		}
		return newDocument;
	}
	
	protected JDDocument setDocument(JDDocument document)
	{
		JDDocument oldDocument = this.documentMap.putIfAbsent(document.name.toLowerCase(), document);
		if(oldDocument != null)
			return oldDocument;
		return this.documentMap.get(document.name.toLowerCase());

	}
	/**
	 * This is slow since it has to brute force the collection
	 * @param document
	 */
	protected void removeDocument(JDDocument document)
	{
		for(Fun.Tuple4<String,Integer,Integer,Object> key : this.knnMap.keySet())
			if(((String)key.d).equalsIgnoreCase(document.name))
				this.knnMap.remove(key);

		this.documentMap.remove(document.name);
	}

	Future<?> submitQuery(Classify classify)
	{
		return this.queryPool.submit(classify);
	}
	KNNToken[] getKNNTokens(Token token,int kLine,int kColumn, int maxMatches)
	{
		//search database
		Map<Tuple4<String, Integer, Integer,Object>, Integer> submap = this.knnMap.subMap(new Fun.Tuple4<String,Integer,Integer,Object>(token.artifact, token.line - kLine, token.column - kColumn,null), new Fun.Tuple4<String,Integer,Integer,Object>(token.artifact, token.line + kLine,token.column + kColumn,Fun.HI()));

		//build list
		ArrayList<KNNToken> tokenList = new ArrayList<KNNToken>(submap.size());
		for(Entry<Tuple4<String,Integer,Integer,Object>, Integer> entry : submap.entrySet())
			tokenList.add(new KNNToken(token,entry));

		//abort if no NN found
		if(tokenList.isEmpty())
			return new KNNToken[0];

		//sort list by distance then truncate to manageable size
		Collections.sort(tokenList);
		if(tokenList.size() > maxMatches)
			tokenList.subList(maxMatches, tokenList.size()).clear();	

		//if worst match is further then best match adjust scores.
		double maxDistance = 0;
		if(tokenList.get(0).distance < tokenList.get(tokenList.size() - 1).distance)
			maxDistance = tokenList.get(tokenList.size() - 1).distance;
		for(KNNToken knn : tokenList)
			knn.calculateScore(maxDistance);

		return tokenList.toArray(new KNNToken[0]);
	}
	/**
	 * This is slow since it has to brute force the collection.
	 * @param documentName
	 * @return
	 */
	protected Token[] getDocumentTokens(String documentName)
	{
		List<Token> list = new ArrayList<Token>();
		for(Entry<Tuple4<String,Integer,Integer,Object>, Integer> entry : this.knnMap.entrySet())
			if(entry.getKey().d instanceof String && documentName.equalsIgnoreCase((String) entry.getKey().d))
				list.add(new Token(entry));

		return list.toArray(new Token[0]);
	}

	protected void flush()
	{
		this.db.commit();
	}
	protected void compact()
	{
		this.db.compact();
		this.flush();
	}

	public void close()
	{
		this.queryPool.shutdown();
		this.db.commit();
		this.db.close();
	}
}
