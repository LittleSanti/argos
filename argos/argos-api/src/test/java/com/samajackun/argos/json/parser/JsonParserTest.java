package com.samajackun.argos.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.samajackun.argos.json.model.JsonArray;
import com.samajackun.argos.json.model.JsonConstant;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonValue;
import com.samajackun.argos.json.parser.JsonParser;
import com.samajackun.argos.json.parser.ParserException;

public class JsonParserTest
{
	private JsonHash parse(String json)
		throws IOException,
		ParserException
	{
		PushbackReader reader=new PushbackReader(new StringReader(json));
		return new JsonParser().parse(reader);
	}

	@Test
	public void parseEmptyJson()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(0, map.size());
	}

	@Test
	public void parseEntryString()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\"}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("mes");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals("enero", ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseEntryNull()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":null}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("mes");
		assertNotNull(value);
		assertEquals(JsonValue.Type.NULL, value.getType());
		assertNull(((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberInteger()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":2018}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(2018L, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberPositiveInteger()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":+2018}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(2018L, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberNegativeInteger()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":-2018}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(-2018L, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberDecimal()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":2.018}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(2.018d, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberPositiveExponent()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":2E1}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(20.0d, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberNegativeExponent()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":2E-1}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(0.2d, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryNumberDecimalExponent()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"año\":2.1E-1}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("año");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(0.21d, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryBooleanTrue()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"single\":true}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("single");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(true, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseOneEntryBooleanFalse()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"single\":false}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value=hash.asHash().get("single");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals(false, ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseTwoEntries()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\",\"dia\":\"lunes\"}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(2, map.size());
		JsonValue value;
		value=hash.asHash().get("mes");
		assertNotNull(value);
		assertTrue(value instanceof JsonConstant);
		assertEquals("enero", ((JsonConstant)value).asConstant());
		value=hash.asHash().get("dia");
		assertNotNull(value);
		assertEquals(JsonValue.Type.CONSTANT, value.getType());
		assertEquals("lunes", ((JsonConstant)value).asConstant());
	}

	@Test
	public void parseArrayEmpty()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[]}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value;
		value=hash.asHash().get("meses");
		assertNotNull(value);
		assertEquals(JsonValue.Type.ARRAY, value.getType());
		List<JsonValue> list=((JsonArray)value).asArray();
		assertEquals(0, list.size());
	}

	@Test
	public void parseArrayWithOneItem()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\"]}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value;
		value=hash.asHash().get("meses");
		assertNotNull(value);
		assertEquals(JsonValue.Type.ARRAY, value.getType());
		List<JsonValue> list=((JsonArray)value).asArray();
		assertEquals(1, list.size());
		assertEquals(JsonValue.Type.CONSTANT, list.get(0).getType());
		assertEquals("enero", (list.get(0)).asConstant());
	}

	@Test
	public void parseArrayWithTwoItems()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",\"febrero\"]}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value;
		value=hash.asHash().get("meses");
		assertNotNull(value);
		assertEquals(JsonValue.Type.ARRAY, value.getType());
		List<JsonValue> list=((JsonArray)value).asArray();
		assertEquals(2, list.size());
		assertEquals(JsonValue.Type.CONSTANT, list.get(0).getType());
		assertEquals("enero", (list.get(0)).asConstant());
		assertEquals("febrero", (list.get(1)).asConstant());
	}

	@Test
	public void parseArrayWithStringBooleanAndNumberItems()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",2,true,false]}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value;
		value=hash.asHash().get("meses");
		assertNotNull(value);
		assertEquals(JsonValue.Type.ARRAY, value.getType());
		List<JsonValue> list=((JsonArray)value).asArray();
		assertEquals(4, list.size());
		assertEquals(JsonValue.Type.CONSTANT, list.get(0).getType());
		assertEquals("enero", (list.get(0)).asConstant());
		assertEquals(2L, (list.get(1)).asConstant());
		assertEquals(true, (list.get(2)).asConstant());
		assertEquals(false, (list.get(3)).asConstant());
	}

	@Test
	public void parseArrayWithComplexItems()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[{\"enero\":{\"dias\":31}},\"febrero\"]}");
		Map<String, JsonValue> map=hash.asHash();
		assertEquals(1, map.size());
		JsonValue value;
		value=hash.asHash().get("meses");
		assertNotNull(value);
		assertEquals(JsonValue.Type.ARRAY, value.getType());
		List<JsonValue> list=((JsonArray)value).asArray();
		assertEquals(2, list.size());
		assertEquals(JsonValue.Type.HASH, list.get(0).getType());
		Map<String, JsonValue> hash2=list.get(0).asHash();
		assertNotNull(hash2.get("enero"));
		Map<String, JsonValue> hash3=hash2.get("enero").asHash();
		assertEquals(31L, hash3.get("dias").asConstant());
	}
}
