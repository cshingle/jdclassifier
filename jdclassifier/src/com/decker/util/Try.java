package com.decker.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Try
{
	static Pattern floatPattern = Pattern.compile("[0-9]+[.]?[0-9]*");

	/**
	 * returns Null if parse failed
	 * @param string
	 * @return
	 */
	public static Integer parseInteger(String string)
	{
		if(string == null)
			return null;

		try
		{
			return Integer.parseInt(string.replaceAll("[, ]", ""));
		} catch (NumberFormatException e)
		{
			return null;
		}
	}
	/**
	 * returns default value if parse failed
	 * @param string
	 * @param defaultValue
	 * @return
	 */
	public static int parseInteger(String string,int defaultValue)
	{
		if(string == null)
			return defaultValue;

		try
		{
			return Integer.parseInt(string.replaceAll("[, ]", ""));
		} catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	public static Long parseLong(String string)
	{
		if(string == null)
			return null;

		try
		{
			return Long.parseLong(string.replaceAll("[, ]", ""));
		} catch (NumberFormatException e)
		{
			return null;
		}
	}
	public static long parseLong(String string,long defaultValue)
	{
		if(string == null)
			return defaultValue;

		try
		{
			return Long.parseLong(string.replaceAll("[, ]", ""));
		} catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	public static Boolean parseBoolean(String string)
	{
		if(string == null)
			return null;
		string = string.replaceAll("[ ]", "");

		if(string.equalsIgnoreCase("true") || string.equalsIgnoreCase("1"))
			return true;
		if(string.equalsIgnoreCase("false") || string.equalsIgnoreCase("0"))
			return false;

		return null;
	}
	public static boolean parseBoolean(String string, boolean defaultValue)
	{
		if(string == null)
			return defaultValue;
		string = string.replaceAll("[ ]", "");

		if(string.equalsIgnoreCase("true") || string.equalsIgnoreCase("1"))
			return true;
		if(string.equalsIgnoreCase("false") || string.equalsIgnoreCase("0"))
			return false;

		return defaultValue;
	}

	public static File parseFile(String string)
	{
		if(string == null)
			return null;

		try{
			File newFile = new File(string);
			if(newFile.isFile())
				return newFile;
		} catch (NullPointerException e){}
		catch (SecurityException e){} 
		return null;
	}

	public static File parseDirectory(String string)
	{
		if(string == null)
			return null;

		try{
			File newFile = new File(string);
			if(newFile.isDirectory())
				return newFile;
		} catch (NullPointerException e){}
		catch (SecurityException e){} 
		return null;
	}
	public static File parseFileOrDirectory(String string)
	{
		if(string == null)
			return null;

		try{
			File newFile = new File(string);
			if(newFile.isFile() || newFile.isDirectory())
				return newFile;
		} catch (NullPointerException e){}
		catch (SecurityException e){} 
		return null;
	}

	public static float parseFloat(String string,float defaultValue)
	{
		if(string == null || !floatPattern.matcher(string).matches())
			return defaultValue;

		try{
			return Float.parseFloat(string);
		} catch (NumberFormatException e){
			return defaultValue;
		}
	}
	public static double parseDouble(String string,double defaultValue)
	{
		if(string == null || !floatPattern.matcher(string).matches())
			return defaultValue;

		try{
			return Double.parseDouble(string);
		} catch (NumberFormatException e){
			return defaultValue;
		}
	}

	public static Float parseFloat(String string)
	{
		if(string == null || !floatPattern.matcher(string).matches())
			return null;

		try{
			return Float.parseFloat(string);
		} catch (NumberFormatException e){
			return null;
		}
	}

	public static Date parseDate(String date,String format) throws IllegalArgumentException
	{
		if(date == null)
			return null;
		try
		{
			DateFormat df = new SimpleDateFormat(format);
			return df.parse(date);
		} catch (ParseException e)
		{
			return null;
		}  catch (IllegalArgumentException e){
			throw e;
		}
	}
}
