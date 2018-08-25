package com.samajackun.argos.json.dom;

public class XslHeader
{
	private String type="text/xsl";

	private String href;

	public XslHeader(String href)
	{
		super();
		this.href=href;
	}

	public XslHeader(String type, String href)
	{
		super();
		this.type=type;
		this.href=href;
	}

	public XslHeader()
	{
		super();
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(String type)
	{
		this.type=type;
	}

	public String getHref()
	{
		return this.href;
	}

	public void setHref(String href)
	{
		this.href=href;
	}

}
