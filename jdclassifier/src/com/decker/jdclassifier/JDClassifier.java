package com.decker.jdclassifier;

import java.util.Map;

public class JDClassifier extends JDEngine {
	public enum JDParameter{DATABASE_NAME};


	public JDClassifier(Map<JDParameter,String> parameters) throws JDClassifierException {
		super(parameters);
	}

	public void train(AbstractTokenizer tokenizer)
	{
		while(tokenizer.hasNext())
		{
			this.setToken(tokenizer.next());
		}
	}
	
	public Result[] classify(AbstractTokenizer tokenizer) throws InterruptedException
	{
		Classifier classifier = new Classifier(tokenizer);
		return classify(classifier);
	}
	public Result[] classify(AbstractTokenizer tokenizer,int kLine,int kColumn,int maxMatches) throws InterruptedException
	{
		Classifier classifier = new Classifier(tokenizer, kLine,kColumn,maxMatches);
		return classify(classifier);
	}
	public Result[] classify(Classifier classifier) throws InterruptedException
	{
		return classifier.run(this);
	}

	/**
	 * Create a new document if not exist.
	 * @param name
	 * @param description
	 * @return
	 */
	public JDDocument createDocument(String name, String description)
	{
		return super.setDocument(new JDDocument(name, description));
	}
}
