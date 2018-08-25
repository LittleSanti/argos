package com.samajackun.argos.json.serializer;

import java.io.IOException;
import java.io.Writer;

import com.samajackun.argos.json.model.JsonHash;

public final class Serializer
{
	private static final Serializer INSTANCE=new Serializer();

	public static Serializer getInstance()
	{
		return INSTANCE;
	}

	private Serializer()
	{
	}

	public void serializeToJson(JsonHash json, Writer out)
		throws IOException,
		SerializerException
	{
		JsonSerializer.getInstance().serialize(json, out);
	}

	public void serializeToXml(JsonHash json, Writer out)
		throws IOException,
		SerializerException
	{
		XmlSerializer.getInstance().serialize(json, out);
	}
}
