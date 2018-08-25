package com.samajackun.argos.json.serializer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.samajackun.argos.json.model.JsonArray;
import com.samajackun.argos.json.model.JsonConstant;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonNull;
import com.samajackun.argos.json.serializer.SerializerException;
import com.samajackun.argos.json.serializer.XmlSerializer;

public class XmlSerializerTest
{
	private String serialize(JsonHash json)
		throws IOException,
		SerializerException
	{
		StringWriter out=new StringWriter(4096);
		XmlSerializer.getInstance().serialize(json, out);
		return out.toString();
	}

	@Test
	public void serializeEmpty()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root></root>", serialize(root));
	}

	@Test
	public void serializeOneEntryNull()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", JsonNull.getInstance());
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes null=\"1\"></mes></root>", serialize(root));
	}

	@Test
	public void serializeOneEntry()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero"));
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes>enero</mes></root>", serialize(root));
	}

	@Test
	public void serializeTwoEntries()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero"));
		input.put("dia", new JsonConstant("lunes"));
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes>enero</mes><dia>lunes</dia></root>", serialize(root));
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
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes><_$>enero</_$></mes></root>", serialize(root));
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
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes><_$>enero</_$><_$>febrero</_$></mes></root>", serialize(root));
	}

	@Test
	public void serializeOneStringWithQuote()
		throws IOException,
		SerializerException
	{
		JsonHash input=new JsonHash();
		input.put("mes", new JsonConstant("enero\"febrero"));
		JsonHash root=new JsonHash();
		root.put("root", input);
		assertEquals("<root><mes>enero\"febrero</mes></root>", serialize(root));
	}

}
