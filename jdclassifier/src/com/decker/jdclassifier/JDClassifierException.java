/*
2	 * Licensed to the Apache Software Foundation (ASF) under one or more
3	 * contributor license agreements.  See the NOTICE file distributed with
4	 * this work for additional information regarding copyright ownership.
5	 * The ASF licenses this file to You under the Apache License, Version 2.0
6	 * (the "License"); you may not use this file except in compliance with
7	 * the License.  You may obtain a copy of the License at
8	 * 
9	 *      http://www.apache.org/licenses/LICENSE-2.0
10	 * 
11	 * Unless required by applicable law or agreed to in writing, software
12	 * distributed under the License is distributed on an "AS IS" BASIS,
13	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
14	 * See the License for the specific language governing permissions and
15	 * limitations under the License.
16	 */
package com.decker.jdclassifier;

import java.util.Formatter;

/**
 * This is the main exception class thrown by JDClassifier. All other exceptions will extend this one.
 * @author Caleb Shingledecker
 *
 */
public class JDClassifierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5793886321783385333L;

	/**
	 * The underlying cause of this exception.
	 */
	private final Throwable cause;

	public JDClassifierException(String message)
	{
		super(message);
		this.cause = null;
	}
	public JDClassifierException(String message, Throwable throwable)
	{
		super(message,throwable);
		this.cause = throwable;
	}

	public JDClassifierException(String format,Object[] args,Throwable throwable)
	{
		super(format(format,args));
		this.cause = throwable;
	}
	protected static String format(String format, Object[] args)
	{
		try(Formatter formater = new Formatter()){
			return formater.format(format, args).toString();
		}
	}

	/**
	 * Return the underlying cause of this exception (if any).
	 */
	public Throwable getCause() {
		return (this.cause);
	}
	/**
	 * Print StackTrace to stderr
	 */
	public void printStackTrace() {
		super.printStackTrace();
		if (cause != null) {
			System.err.println("Caused by:");
			cause.printStackTrace();
		}
	}
	/**
	 * Print StackTrace to PrintStream
	 */
	public void printStackTrace(java.io.PrintStream ps) {
		super.printStackTrace(ps);
		if (cause != null) {
			ps.println("Caused by:");
			cause.printStackTrace(ps);
		}
	}
	/**
	 * Print StackTrace to PrintWriter
	 */
	public void printStackTrace(java.io.PrintWriter pw) {
		super.printStackTrace(pw);
		if (cause != null) {
			pw.println("Caused by:");
			cause.printStackTrace(pw);
		}
	}
}
