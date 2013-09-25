package com.decker.jdclassifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Classifier {
	private final AbstractTokenizer tokenizer;
	private final Semaphore running = new Semaphore(Integer.MAX_VALUE, false);
	final int kLine;
	final int kColumn;
	final int maxMatches;
	final AtomicInteger tasks = new AtomicInteger(0);

	final ConcurrentHashMap<String,Double> scoreMap = new ConcurrentHashMap<String,Double>();
	final ConcurrentHashMap<String,Integer> matchMap = new ConcurrentHashMap<String,Integer>();

	public Classifier(AbstractTokenizer tokenizer)
	{
		this(tokenizer,5,5,15);
	}
	public Classifier(AbstractTokenizer tokenizer,int kLine,int kColumn, int maxMatches)
	{
		this.tokenizer = tokenizer;
		this.kLine = kLine;
		this.kColumn = kColumn;
		this.maxMatches = maxMatches;
	}

	Result[] run(JDEngine engine) throws InterruptedException
	{
		while(tokenizer.hasNext())
			engine.submitQuery(new Classify(engine,tokenizer.next()));
		
		this.running.acquire(Integer.MAX_VALUE);
		
		List<Result> documents = new ArrayList<>();
		
		for(Entry<String, Double> entry : this.scoreMap.entrySet())
		{
			JDDocument document = engine.getDocument(entry.getKey());
			documents.add(new Result(document, entry.getValue(), matchMap.get(entry.getKey())));
		}

		Collections.sort(documents);
		return documents.toArray(new Result[0]);
	}

	class Classify implements Runnable
	{
		final JDEngine engine;
		final Token token;

		Classify(JDEngine engine,Token token)
		{
			this.engine = engine;
			this.token = token;

			running.acquireUninterruptibly();
		}

		@Override
		public void run() {
			KNNToken[] knnTokens = this.engine.getKNNTokens(token, kLine, kColumn, maxMatches);

			HashSet<String> documents = new HashSet<String>();
			for(KNNToken knnToken : knnTokens)
			{
				//Atomically set scores for each knn token found
				if(scoreMap.putIfAbsent(knnToken.document, knnToken.score) != null)
				{
					Double currentScore;
					while(true)
					{
						currentScore = scoreMap.get(knnToken.document);
						if(scoreMap.replace(knnToken.document, currentScore, currentScore + knnToken.score))
							break;
					}
				}

				documents.add(knnToken.document);
			}
			//Atomically increment the number of matches
			for(String document : documents)
				if(matchMap.putIfAbsent(document, 1) != null)
				{
					Integer currentMatches;
					while(true)
					{
						currentMatches = matchMap.get(document);
						if(matchMap.replace(document, currentMatches, currentMatches + 1));
						break;
					}
				}
			running.release();
		}
	}
}
