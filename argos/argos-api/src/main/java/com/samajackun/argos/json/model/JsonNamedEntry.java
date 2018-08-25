package com.samajackun.argos.json.model;

public class JsonNamedEntry
{
	private final String name;

	private final JsonValue value;

	public JsonNamedEntry(String name, JsonValue value)
	{
		super();
		this.name=name;
		this.value=value;
	}

	public String getName()
	{
		return this.name;
	}

	public JsonValue getValue()
	{
		return this.value;
	}
}
