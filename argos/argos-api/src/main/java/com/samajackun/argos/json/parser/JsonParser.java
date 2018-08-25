package com.samajackun.argos.json.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.samajackun.argos.json.model.JsonArray;
import com.samajackun.argos.json.model.JsonConstant;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonNamedEntry;
import com.samajackun.argos.json.model.JsonNull;
import com.samajackun.argos.json.model.JsonValue;

public class JsonParser
{
	private enum State1 {
		INITIAL, END, EXPECTING_COMMA_OR_CLOSING_KEY,
	};

	private enum State2 {
		INITIAL, EXPECTING_COLON, END
	};

	private enum State3 {
		INITIAL, EXPECTING_COMMA, END, EXPECTING_VALUE
	};

	private enum State4 {
		INITIAL, READ_SIGN, READ_PERIOD, READING_INT, READ_E, READ_E_SIGN, READING_EXP
	};

	private enum State5 {
		INITIAL, READ_T, READ_TR, READ_TRU, READ_F, READ_FA, READ_FAL, READ_FALS,
	};

	private enum State6 {
		INITIAL, READ_N, READ_NU, READ_NUL
	}

	public JsonHash parse(Reader reader)
		throws IOException,
		ParserException
	{
		return parse(new PushbackReader(reader));
	}

	public JsonHash parse(PushbackReader reader)
		throws IOException,
		ParserException
	{
		JsonHash hash=new JsonHash();
		State1 state=State1.INITIAL;
		int c;
		while (state != State1.END)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				switch (state)
				{
					case INITIAL:
						if (Character.isWhitespace(c))
						{
						}
						else if (c == '{')
						{
							JsonNamedEntry entry=parseEntry(reader);
							if (entry != null)
							{
								hash.put(entry.getName(), entry.getValue());
							}
							state=State1.EXPECTING_COMMA_OR_CLOSING_KEY;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case EXPECTING_COMMA_OR_CLOSING_KEY:
						if (Character.isWhitespace(c))
						{
						}
						else if (c == ',')
						{
							JsonNamedEntry entry=parseEntry(reader);
							if (entry != null)
							{
								hash.put(entry.getName(), entry.getValue());
							}
						}
						else if (c == '}')
						{
							state=State1.END;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case END:
						break;
				}
			}
		}
		return hash;
	}

	private JsonNamedEntry parseEntry(PushbackReader reader)
		throws IOException,
		ParserException
	{
		JsonNamedEntry entry=null;
		String name=null;
		JsonValue value=null;
		State2 state=State2.INITIAL;
		int c;
		while (state != State2.END)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				switch (state)
				{
					case INITIAL:
						if (Character.isWhitespace(c))
						{
						}
						// else if (c == '}')
						// {
						// reader.unread(c);
						// state=State2.END;
						// }
						else if (c == '\"')
						{
							name=parseQuotedText(reader);
							state=State2.EXPECTING_COLON;
						}
						else
						{
							reader.unread(c);
							state=State2.END;
							// throw new UnexpectedTokenException(c);
						}
						break;
					case EXPECTING_COLON:
						if (Character.isWhitespace(c))
						{
						}
						else if (c == ':')
						{
							value=parseValue(reader);
							entry=new JsonNamedEntry(name, value);
							state=State2.END;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					default:
						throw new IllegalStateException();
				}
			}
		}
		if (state != State2.END)
		{
			throw new ParserException();
		}
		return entry;
	}

	private JsonValue parseValue(PushbackReader reader)
		throws ParserException,
		IOException
	{
		JsonValue value=null;
		int c;
		while (value == null)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				if (Character.isWhitespace(c))
				{
				}
				else if (c == '[')
				{
					reader.unread(c);
					value=parseArray(reader);
				}
				else if (c == '{')
				{
					reader.unread(c);
					value=parse(reader);
				}
				else if (c == '\"')
				{
					String text=parseQuotedText(reader);
					value=new JsonConstant(text);
				}
				else if (Character.isDigit(c) || c == '-' || c == '+')
				{
					reader.unread(c);
					Number number=parseNumber(reader);
					value=new JsonConstant(number);
				}
				else if (c == 't' || c == 'f')
				{
					reader.unread(c);
					Boolean bool=parseBoolean(reader);
					value=new JsonConstant(bool);
				}
				else if (c == 'n')
				{
					reader.unread(c);
					value=parseNull(reader);
				}
				else
				{
					throw new UnexpectedSymbolException(c);
				}
			}
		}
		return value;
	}

	private Boolean parseBoolean(PushbackReader reader)
		throws IOException,
		EndOfInputException,
		UnexpectedSymbolException
	{
		Boolean value=null;
		int c;
		State5 state=State5.INITIAL;
		while (value == null)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				switch (state)
				{
					case INITIAL:
						if (c == 't')
						{
							state=State5.READ_T;
						}
						else if (c == 'f')
						{
							state=State5.READ_F;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_T:
						if (c == 'r')
						{
							state=State5.READ_TR;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_TR:
						if (c == 'u')
						{
							state=State5.READ_TRU;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_TRU:
						if (c == 'e')
						{
							value=true;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_F:
						if (c == 'a')
						{
							state=State5.READ_FA;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_FA:
						if (c == 'l')
						{
							state=State5.READ_FAL;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_FAL:
						if (c == 's')
						{
							state=State5.READ_FALS;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_FALS:
						if (c == 'e')
						{
							value=false;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
				}
			}
		}
		return value;
	}

	private JsonNull parseNull(PushbackReader reader)
		throws IOException,
		EndOfInputException,
		UnexpectedSymbolException
	{
		JsonNull value=null;
		int c;
		State6 state=State6.INITIAL;
		while (value == null)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				switch (state)
				{
					case INITIAL:
						if (c == 'n')
						{
							state=State6.READ_N;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_N:
						if (c == 'u')
						{
							state=State6.READ_NU;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_NU:
						if (c == 'l')
						{
							state=State6.READ_NUL;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_NUL:
						if (c == 'l')
						{
							value=JsonNull.getInstance();
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
				}
			}
		}
		return value;
	}

	private JsonArray parseArray(PushbackReader reader)
		throws ParserException,
		IOException
	{
		JsonArray array=new JsonArray();
		int c;
		State3 state=State3.INITIAL;
		while (state != State3.END)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				if (Character.isWhitespace(c))
				{

				}
				else
				{
					switch (state)
					{
						case INITIAL:
							if (c == '[')
							{
								state=State3.EXPECTING_VALUE;
							}
							else
							{
								throw new UnexpectedSymbolException(c);
							}
							break;
						case EXPECTING_VALUE:
							if (c == ']')
							{
								state=State3.END;
							}
							else
							{
								reader.unread(c);
								JsonValue value=parseValue(reader);
								state=State3.EXPECTING_COMMA;
								array.add(value);
							}
							break;
						case EXPECTING_COMMA:
							if (c == ',')
							{
								state=State3.EXPECTING_VALUE;
							}
							else if (c == ']')
							{
								state=State3.END;
							}
							else
							{
								throw new UnexpectedSymbolException(c);
							}
							break;
						case END:
							break;
					}
				}
			}
		}
		return array;
	}

	private String parseQuotedText(PushbackReader reader)
		throws IOException,
		ParserException
	{
		String name=null;
		StringBuilder stb=new StringBuilder(20);
		int c=0;
		while (name == null)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				if (c != '\"')
				{
					stb.append((char)c);
				}
				else
				{
					name=stb.toString();
				}
			}
		}
		return name;
	}

	private Number parseNumber(PushbackReader reader)
		throws IOException,
		EndOfInputException,
		UnexpectedSymbolException
	{
		Number value=null;
		StringBuilder stb=new StringBuilder(20);
		int c;
		State4 state=State4.INITIAL;
		while (value == null)
		{
			c=reader.read();
			if (c < 0)
			{
				throw new EndOfInputException();
			}
			else
			{
				switch (state)
				{
					case INITIAL:
						if (Character.isWhitespace(c))
						{
						}
						else if (c == '+' || c == '-')
						{
							state=State4.READ_SIGN;
						}
						else if (c == '.')
						{
							state=State4.READ_PERIOD;
						}
						else if (Character.isDigit(c))
						{
							state=State4.READING_INT;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_SIGN:
						if (c == '+' || c == '-')
						{
						}
						else if (c == '.')
						{
							state=State4.READ_PERIOD;
						}
						else if (Character.isDigit(c))
						{
							state=State4.READING_INT;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READING_INT:
						if (Character.isWhitespace(c))
						{
							value=Long.parseLong(stb.toString());
						}
						// else if (c == '}' || c == ',' || c == ']')
						// {
						// reader.unread(c);
						// value=Long.parseLong(stb.toString());
						// }
						else if (c == '.')
						{
							state=State4.READ_PERIOD;
						}
						else if (c == 'E' || c == 'e')
						{
							state=State4.READ_E;
						}
						else if (Character.isDigit(c))
						{
						}
						else
						{
							reader.unread(c);
							value=Long.parseLong(stb.toString());
							// throw new UnexpectedTokenException(c);
						}
						break;
					case READ_PERIOD:
						if (Character.isWhitespace(c))
						{
							value=Double.parseDouble(stb.toString());
						}
						// else if (c == '}' || c == ',' || c == ']')
						// {
						// reader.unread(c);
						// value=Double.parseDouble(stb.toString());
						// }
						else if (c == 'E' || c == 'e')
						{
							state=State4.READ_E;
						}
						else if (Character.isDigit(c))
						{
						}
						else
						{
							reader.unread(c);
							value=Double.parseDouble(stb.toString());
							// throw new UnexpectedTokenException(c);
						}
						break;
					case READ_E:
						if (c == '+' || c == '-')
						{
							state=State4.READ_E_SIGN;
						}
						else if (Character.isDigit(c))
						{
							state=State4.READING_EXP;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READ_E_SIGN:
						if (Character.isDigit(c))
						{
							state=State4.READING_EXP;
						}
						else
						{
							throw new UnexpectedSymbolException(c);
						}
						break;
					case READING_EXP:
						if (Character.isWhitespace(c))
						{
							value=Double.parseDouble(stb.toString());
						}
						// else if (c == '}' || c == ',' || c == ']')
						// {
						// reader.unread(c);
						// value=Double.parseDouble(stb.toString());
						// }
						else if (Character.isDigit(c))
						{
						}
						else
						{
							reader.unread(c);
							value=Double.parseDouble(stb.toString());
							// throw new UnexpectedTokenException(c);
						}
						break;
					default:
						break;
				}
				stb.append((char)c);
			}
		}
		return value;
	}
}
