package com.samajackun.argos.json.serializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonValue;

public final class XmlSerializer
{
	private static final XmlSerializer INSTANCE=new XmlSerializer();

	public static XmlSerializer getInstance()
	{
		return INSTANCE;
	}

	private XmlSerializer()
	{
	}

	public void serialize(JsonHash json, Writer out)
		throws IOException,
		SerializerException
	{
		Map<String, JsonValue> map=json.asHash();
		if (map.size() != 1)
		{
			throw new SerializerException("Only one root node is allowed");
		}
		serializeHash(map, out);
	}

	private void serializeHash(Map<String, JsonValue> map, Writer out)
		throws IOException,
		SerializerException
	{
		for (Iterator<Map.Entry<String, JsonValue>> iterator=map.entrySet().iterator(); iterator.hasNext();)
		{
			Map.Entry<String, JsonValue> entry=iterator.next();
			if (entry.getValue() != null)
			{
				serializeJsonValue(entry.getKey(), entry.getValue(), out);
			}
		}
	}

	private void serializeJsonValue(String name, JsonValue input, Writer out)
		throws IOException,
		SerializerException
	{
		out.write('<');
		out.write(name);
		switch (input.getType())
		{
			case ARRAY:
				out.write('>');
				serializeArray(input.asArray(), out);
				break;
			case NULL:
			case CONSTANT:
				if (input.asConstant() == null)
				{
					out.write(" null=\"1\">");
				}
				else
				{
					out.write('>');
					serializeObject(input.asConstant(), out);
				}
				break;
			case HASH:
				out.write('>');
				serializeHash(input.asHash(), out);
				break;
		}
		out.write("</");
		out.write(name);
		out.write('>');
	}

	private void serializeObject(Object constant, Writer out)
		throws IOException
	{
		if (constant instanceof String)
		{
			out.write(escapeText((String)constant));
		}
		else if (constant instanceof Boolean)
		{
			out.write(((Boolean)constant).toString());
		}
		else if (constant instanceof Number)
		{
			out.write(((Number)constant).toString());
		}
		else if (constant == null)
		{
			out.write("null");
		}
		else
		{
			out.write(escapeText(constant.toString()));
		}
	}

	private void serializeArray(List<JsonValue> list, Writer out)
		throws IOException,
		SerializerException
	{
		for (Iterator<JsonValue> iterator=list.iterator(); iterator.hasNext();)
		{
			JsonValue value=iterator.next();
			serializeJsonValue("_$", value, out);
		}
	}

	/**
	 * Escape from the input string all the XML textnode-relative control chars
	 * (ampersand &amp; and lower-than &lt;).
	 *
	 * @param str Input string.
	 * @return Escapped string.
	 */
	private static String escapeText(String str)
	{
		// 1ª pasada: análisis
		int len=str.length();
		char c;
		int i=0;
		for (; i < len; i++)
		{
			c=str.charAt(i);
			if (c == '&' || c == '<')
			{
				break;
			}
		}
		if (i < len)
		{
			java.util.Collection<Integer> spots=new java.util.ArrayList<>(Math.max(10, Math.min(100, len / 100)));
			int escapedLen=0;
			for (; i < len; i++)
			{
				c=str.charAt(i);
				switch (c)
				{
					case '&':
						escapedLen+=5;
						spots.add(Integer.valueOf(i));
						break;
					case '<':
						escapedLen+=4;
						spots.add(Integer.valueOf(i));
						break;
					default:
						break;
				}
			}

			// if (spots.size()>0)
			{
				// 2ª pasada: escapeo sólo si es necesario
				escapedLen+=len - spots.size();
				StringBuffer stb=new StringBuffer(escapedLen);
				int i0=0;
				for (java.util.Iterator<Integer> iterator=spots.iterator(); iterator.hasNext();)
				{
					i=(iterator.next()).intValue();
					stb.append(str.substring(i0, i));
					c=str.charAt(i);
					if (c == '&')
					{
						stb.append("&amp;");
					}
					else if (c == '<')
					{
						stb.append("&lt;");
					}
					i0=i + 1;
				}
				if (i0 < len)
				{
					stb.append(str.substring(i0));
				}
				str=stb.toString();
			}
		}
		return str;
	}

}
