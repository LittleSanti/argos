package com.samajackun.argos.json.serializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonValue;

public final class JsonSerializer
{
	private static final JsonSerializer INSTANCE=new JsonSerializer();

	public static JsonSerializer getInstance()
	{
		return INSTANCE;
	}

	private JsonSerializer()
	{
	}

	public void serialize(JsonHash json, Writer out)
		throws IOException,
		SerializerException
	{
		serializeHash(json.asHash(), out);
	}

	private void serializeHash(Map<String, JsonValue> map, Writer out)
		throws IOException
	{
		out.write("{");
		for (Iterator<Map.Entry<String, JsonValue>> iterator=map.entrySet().iterator(); iterator.hasNext();)
		{
			Map.Entry<String, JsonValue> entry=iterator.next();
			if (entry.getValue() != null)
			{
				out.write('\"');
				out.write(escapeQuote(entry.getKey()));
				out.write("\":");
				serializeJsonValue(entry.getValue(), out);
				if (iterator.hasNext())
				{
					out.write(',');
				}
			}
		}
		out.write("}");
	}

	private void serializeJsonValue(JsonValue input, Writer out)
		throws IOException
	{
		switch (input.getType())
		{
			case ARRAY:
				serialize(input.asArray(), out);
				break;
			case NULL:
			case CONSTANT:
				serializeObject(input.asConstant(), out);
				break;
			case HASH:
				serializeHash(input.asHash(), out);
				break;
		}
	}

	private void serializeObject(Object constant, Writer out)
		throws IOException
	{
		if (constant == null)
		{
			out.write("null");
		}
		else
		{
			out.write('\"');
			if (constant instanceof String)
			{
				out.write(escapeQuote((String)constant));
			}
			else if (constant instanceof Boolean)
			{
				out.write(((Boolean)constant).toString());
			}
			else if (constant instanceof Number)
			{
				out.write(((Number)constant).toString());
			}
			else
			{
				out.write(escapeQuote(constant.toString()));
			}
			out.write('\"');
		}
	}

	private void serialize(List<JsonValue> list, Writer out)
		throws IOException
	{
		out.write('[');
		for (Iterator<JsonValue> iterator=list.iterator(); iterator.hasNext();)
		{
			JsonValue value=iterator.next();
			serializeJsonValue(value, out);
			if (iterator.hasNext())
			{
				out.write(',');
			}
		}
		out.write(']');
	}

	private static String escapeQuote(String name)
	{
		return name.replaceAll("\"", "\\\\\"");
	}
}
