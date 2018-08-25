package com.samajackun.argos.json.serializer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.samajackun.argos.json.model.JsonArray;
import com.samajackun.argos.json.model.JsonConstant;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonNull;
import com.samajackun.argos.json.serializer.JsonSerializer;
import com.samajackun.argos.json.serializer.SerializerException;

public class JsonSerializerTest
{
	private String serialize(JsonHash json)
		throws IOException,
		SerializerException
	{
		StringWriter out=new StringWriter(4096);
		JsonSerializer.getInstance().serialize(json, out);
		return out.toString();
	}

	@Test
	public void serializeEmpty()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		assertEquals("{}", serialize(input));
	}

	@Test
	public void serializeOneEntryNull()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", JsonNull.getInstance());
		assertEquals("{\"mes\":null}", serialize(input));
	}

	@Test
	public void serializeOneEntry()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero"));
		assertEquals("{\"mes\":\"enero\"}", serialize(input));
	}

	@Test
	public void serializeTwoEntries()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero"));
		input.put("dia", new JsonConstant("lunes"));
		assertEquals("{\"mes\":\"enero\",\"dia\":\"lunes\"}", serialize(input));
	}

	@Test
	public void serializeArrayWithOneItem()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		JsonArray array=new JsonArray();
		array.add(new JsonConstant("enero"));
		input.put("mes", array);
		assertEquals("{\"mes\":[\"enero\"]}", serialize(input));
	}

	@Test
	public void serializeArrayWithTwoItems()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		JsonArray array=new JsonArray();
		array.add(new JsonConstant("enero"));
		array.add(new JsonConstant("febrero"));
		input.put("mes", array);
		assertEquals("{\"mes\":[\"enero\",\"febrero\"]}", serialize(input));
	}

	@Test
	public void serializeOneStringWithQuote()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero\"febrero"));
		assertEquals("{\"mes\":\"enero\\\"febrero\"}", serialize(input));
	}

}
