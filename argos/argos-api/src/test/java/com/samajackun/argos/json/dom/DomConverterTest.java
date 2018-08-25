package com.samajackun.argos.json.dom;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ServiceConfigurationError;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Test;
import org.w3c.dom.Document;

import com.samajackun.argos.json.dom.DomConverter;
import com.samajackun.argos.json.dom.DomPreferences;
import com.samajackun.argos.json.dom.StringNullValueConverter;
import com.samajackun.argos.json.dom.XslHeader;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.parser.JsonParser;
import com.samajackun.argos.json.parser.ParserException;

public class DomConverterTest
{
	private JsonHash parse(String json)
		throws IOException,
		ParserException
	{
		PushbackReader reader=new PushbackReader(new StringReader(json));
		return new JsonParser().parse(reader);
	}

	/**
	 * Serialize a Document onto an OutputStream.
	 *
	 * @param doc Source document.
	 * @param out Target OutputStream.
	 * @exception java.io.IOException If an error occured while writing.
	 * @exception javax.xml.transform.TransformerConfigurationException If the serializer could not be instantiated.
	 * @exception javax.xml.transform.TransformerException If an error occurred while serializing.
	 */
	private static String serialize(org.w3c.dom.Document doc)
		throws java.io.IOException
	{
		try
		{
			javax.xml.transform.Transformer transformer=javax.xml.transform.TransformerFactory.newInstance().newTransformer();
			StringWriter out=new StringWriter(4096);
			javax.xml.transform.Result result=new javax.xml.transform.stream.StreamResult(out);
			javax.xml.transform.Source source=new javax.xml.transform.dom.DOMSource(doc);
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.transform(source, result);
			return out.toString();
		}
		catch (IllegalArgumentException | TransformerFactoryConfigurationError | TransformerException e)
		{
			throw new ServiceConfigurationError(e.toString(), e);
		}
	}

	@Test
	public void toDocumentEntryString()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\"}");
		Document doc=DomConverter.toDocument(hash, "json", new DomPreferences());
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><mes>enero</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayEmpty()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[]}");
		Document doc=DomConverter.toDocument(hash, "json", new DomPreferences());
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><meses/></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayWithComplexItems()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[{\"enero\":{\"dias\":31}},\"febrero\"]}");
		Document doc=DomConverter.toDocument(hash, "json", new DomPreferences());
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><meses><item><enero><dias>31</dias></enero></item><item>febrero</item></meses></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayWithNulls()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",null,\"febrero\"]}");
		Document doc=DomConverter.toDocument(hash, "json", new DomPreferences());
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><meses><item>enero</item><item>null</item><item>febrero</item></meses></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayWithNullsUsingEmptyStringNullConverter()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",null,\"febrero\"]}");
		DomPreferences preferences=new DomPreferences();
		preferences.setNullValueConverter(DomPreferences.EMPTY_STRING_NULL_VALUE_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", preferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><meses><item>enero</item><item/><item>febrero</item></meses></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayWithNullsUsingOmitterNullConverter()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",null,\"febrero\"]}");
		DomPreferences preferences=new DomPreferences();
		preferences.setNullValueConverter(DomPreferences.OMITTER_NULL_VALUE_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", preferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><meses><item>enero</item><item>febrero</item></meses></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayWithNullsUsingXsiNullConverter()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"meses\":[\"enero\",null,\"febrero\"]}");
		DomPreferences preferences=new DomPreferences();
		preferences.setNullValueConverter(DomPreferences.XSI_NULL_VALUE_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", preferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><meses><item>enero</item><item xsi:nil=\"true\"/><item>febrero</item></meses></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentArrayUsingSameNode()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":[\"enero\",\"febrero\",\"marzo\"]}");
		DomPreferences preferences=new DomPreferences();
		preferences.setArrayConverter(DomPreferences.REPEATED_NODE_ARRAY_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", preferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><mes>enero</mes><mes>febrero</mes><mes>marzo</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentEntryNullAsNull()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":null}");
		Document doc=DomConverter.toDocument(hash, "json", new DomPreferences());
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><mes>null</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentEntryNullAsString()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":null}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.setNullValueConverter(new StringNullValueConverter("x"));
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><mes>x</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentEntryNullAsEmpty()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":null}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.setNullValueConverter(DomPreferences.EMPTY_STRING_NULL_VALUE_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json><mes/></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentEntryNullOmitted()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":null}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.setNullValueConverter(DomPreferences.OMITTER_NULL_VALUE_CONVERTER);
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json/>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentWithXsl()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\"}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.setXslHeader(new XslHeader("my.xsl"));
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><?xml-stylesheet type=\"text/xsl\" href=\"my.xsl\"?><json><mes>enero</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentWithOneRootNamespace()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\"}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.putRootNamespace("myns", "myuri");
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json xmlns:myns=\"myuri\"><mes>enero</mes></json>";
		assertEquals(expected, xml);
	}

	@Test
	public void toDocumentWithTwoRootNamespaces()
		throws IOException,
		ParserException
	{
		JsonHash hash=parse("{\"mes\":\"enero\"}");
		DomPreferences domPreferences=new DomPreferences();
		domPreferences.putRootNamespace("myns1", "myuri1");
		domPreferences.putRootNamespace("myns2", "myuri2");
		Document doc=DomConverter.toDocument(hash, "json", domPreferences);
		String xml=serialize(doc);
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><json xmlns:myns1=\"myuri1\" xmlns:myns2=\"myuri2\"><mes>enero</mes></json>";
		assertEquals(expected, xml);
	}
}
