package com.decker.jdclassifier;

import java.io.Serializable;

public class JDDocument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5758178224533992445L;
	public final String name;
	public final String description;
	final int maxWeight;
	
	public JDDocument(String name,String description)
	{
		this.name = name.toLowerCase();
		this.description = description;
		this.maxWeight = 0;
	}
	JDDocument(String name,String description,int maxWeight)
	{
		this.name = name.toLowerCase();
		this.description = description;
		this.maxWeight = maxWeight;
	}
	JDDocument(JDDocument document,int newWeight)
	{
		this(document.name.toLowerCase(),document.description,newWeight);
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) 
        	return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;

        JDDocument document = (JDDocument) o;

        if(name != null && !name.equalsIgnoreCase(document.name) )
        	return false;
        
        if (description != null && !description.equals(document.description)) 
        	return false;
        
        if(maxWeight != document.maxWeight)
        	return false;

        return true;
    }
}
