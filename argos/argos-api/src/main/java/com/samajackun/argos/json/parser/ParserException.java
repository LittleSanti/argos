package com.samajackun.argos.json.parser;

public class ParserException extends Exception
{
	private static final long serialVersionUID=-1288193422128699122L;

	public ParserException()
	{
	}

	public ParserException(String message)
	{
		super(message);
	}

	public ParserException(Throwable cause)
	{
		super(cause);
	}

	public ParserException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
