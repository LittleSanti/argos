package com.samajackun.argos.json.parser;

public class UnexpectedSymbolException extends ParserException
{
	private static final long serialVersionUID=-5609582622192309854L;

	private final int character;

	public UnexpectedSymbolException(int c)
	{
		super("Unexpected symbol '" + (char)c + "'");
		this.character=c;
	}

	public int getCharacter()
	{
		return this.character;
	}
}
