package com.samajackun.argos.json.dom;

import java.util.HashMap;
import java.util.Map;

public class DomPreferences
{
	public static final NullValueConverter NULL_STRING_NULL_VALUE_CONVERTER=new StringNullValueConverter("null");

	public static final NullValueConverter EMPTY_STRING_NULL_VALUE_CONVERTER=new StringNullValueConverter("");

	public static final NullValueConverter OMITTER_NULL_VALUE_CONVERTER=new OmitterNullValueConverter();

	public static final NullValueConverter XSI_NULL_VALUE_CONVERTER=new XsiNullValueConverter();

	public static final ArrayConverter REPEATED_NODE_ARRAY_CONVERTER=new RepeatedNodeArrayConverter();

	private NullValueConverter nullValueConverter=NULL_STRING_NULL_VALUE_CONVERTER;

	private ArrayConverter arrayConverter=createMultiChildNodeArrayConverter("item");
	// private String arrayItemNodeName="item";

	private XslHeader xslHeader;

	private final Map<String, String> rootNamespaces=new HashMap<>();

	public NullValueConverter getNullValueConverter()
	{
		return this.nullValueConverter;
	}

	public static ArrayConverter createMultiChildNodeArrayConverter(String nodeName)
	{
		return new MultiChildNodeArrayConverter(nodeName);
	}

	public void setNullValueConverter(NullValueConverter nullValueConverter)
	{
		this.nullValueConverter=nullValueConverter;
	}

	public XslHeader getXslHeader()
	{
		return this.xslHeader;
	}

	public void setXslHeader(XslHeader xslHeader)
	{
		this.xslHeader=xslHeader;
	}

	public Map<String, String> getRootNamespaces()
	{
		return this.rootNamespaces;
	}

	public String putRootNamespace(String key, String uri)
	{
		return this.rootNamespaces.put(key, uri);
	}

	public String setDefaultRootNamespace(String uri)
	{
		return this.rootNamespaces.put(null, uri);
	}

	public String getDefaultRootNamespace(String uri)
	{
		return this.rootNamespaces.get(null);
	}

	public ArrayConverter getArrayConverter()
	{
		return this.arrayConverter;
	}

	public void setArrayConverter(ArrayConverter arrayConverter)
	{
		this.arrayConverter=arrayConverter;
	}
}
