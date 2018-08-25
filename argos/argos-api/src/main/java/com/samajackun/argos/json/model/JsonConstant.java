package com.samajackun.argos.json.model;

import java.util.List;
import java.util.Map;

public class JsonConstant implements JsonValue
{
	private final Object value;

	public JsonConstant(Object value)
	{
		super();
		this.value=value;
	}

	@Override
	public Type getType()
	{
		return Type.CONSTANT;
	}

	@Override
	public List<JsonValue> asArray()
	{
		throw new IllegalArgumentException("It is a constant, not an array");
	}

	@Override
	public Map<String, JsonValue> asHash()
	{
		throw new IllegalArgumentException("It is a constant, not a hash");
	}

	@Override
	public Object asConstant()
	{
		return this.value;
	}
}
