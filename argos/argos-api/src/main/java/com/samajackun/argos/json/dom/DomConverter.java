package com.samajackun.argos.json.dom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.model.JsonValue;

public final class DomConverter
{
	private static final DocumentBuilderFactory FACTORY=DocumentBuilderFactory.newInstance();

	public static final String DEFAULT_XSI_URI="http://www.w3.org/2001/XMLSchema-instance";

	private DomConverter()
	{
	}

	public static Document toDocument(JsonHash hash, String rootName, DomPreferences preferences)
	{
		try
		{
			DocumentBuilder builder=FACTORY.newDocumentBuilder();
			Document doc=builder.newDocument();
			fillDoc(doc, preferences);
			Element root=doc.createElement(rootName);
			setAttributes(root, preferences);
			doc.appendChild(root);
			importHashToNode(root, hash, preferences);
			return doc;
		}
		catch (ParserConfigurationException e)
		{
			throw new ServiceConfigurationError(e.toString(), e);
		}
	}

	private static void fillDoc(Document doc, DomPreferences preferences)
	{
		if (preferences.getXslHeader() != null)
		{
			ProcessingInstruction pi=doc.createProcessingInstruction("xml-stylesheet", " type=\"" + preferences.getXslHeader().getType() + "\" href=\"" + preferences.getXslHeader().getHref() + "\"");
			doc.appendChild(pi);
		}
	}

	private static void setAttributes(Element root, DomPreferences preferences)
	{
		Map<String, String> namespaces=new HashMap<>(preferences.getRootNamespaces());
		if (preferences.getNullValueConverter() instanceof XsiNullValueConverter)
		{
			if (!namespaces.containsKey("xsi"))
			{
				namespaces.put("xsi", DEFAULT_XSI_URI);
			}
		}
		for (Map.Entry<String, String> namespace : namespaces.entrySet())
		{
			String name=namespace.getKey() == null
				? "xmlns"
				: ("xmlns:" + namespace.getKey());
			root.setAttribute(name, namespace.getValue());
		}
	}

	static void importHashToNode(Element parent, JsonHash input, DomPreferences preferences)
	{
		Map<String, JsonValue> map=input.asHash();
		for (Map.Entry<String, JsonValue> entry : map.entrySet())
		{
			createAndImportToNode(parent, entry.getKey(), entry.getValue(), preferences);
		}
	}

	static void createAndImportToNode(Element parent, String propertyName, JsonValue input, DomPreferences preferences)
	{
		switch (input.getType())
		{
			case NULL:
				preferences.getNullValueConverter().toElement(parent, propertyName);
				break;
			case ARRAY:
				List<JsonValue> array=input.asArray();
				preferences.getArrayConverter().toDom(parent, propertyName, array, preferences);
				break;
			case CONSTANT:
				Element newElement=parent.getOwnerDocument().createElement(propertyName);
				Text text=parent.getOwnerDocument().createTextNode(input.asConstant().toString());
				newElement.appendChild(text);
				parent.appendChild(newElement);
				break;
			case HASH:
				Element newElement2=parent.getOwnerDocument().createElement(propertyName);
				Map<String, JsonValue> map=input.asHash();
				for (Map.Entry<String, JsonValue> entry : map.entrySet())
				{
					createAndImportToNode(newElement2, entry.getKey(), entry.getValue(), preferences);
				}
				parent.appendChild(newElement2);
				break;
		}
	}

	// static void importToNode(Element parent, JsonValue input, DomPreferences preferences)
	// {
	// switch (input.getType())
	// {
	// case ARRAY:
	// List<JsonValue> array=input.asArray();
	// preferences.getArrayConverter().toDom(parent, array, preferences);
	// break;
	// case CONSTANT:
	// Text text=parent.getOwnerDocument().createTextNode(input.asConstant().toString());
	// parent.appendChild(text);
	// break;
	// case HASH:
	// Map<String, JsonValue> map=input.asHash();
	// for (Map.Entry<String, JsonValue> entry : map.entrySet())
	// {
	// createAndImportToNode(parent, entry.getKey(), entry.getValue(), preferences);
	// }
	// break;
	// case NULL:
	// preferences.getNullValueConverter().toElement(parent, "pollas");
	// break;
	// }
	// }
}
