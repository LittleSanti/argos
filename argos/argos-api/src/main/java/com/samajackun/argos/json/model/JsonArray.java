package com.samajackun.argos.json.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonArray implements JsonValue
{
	private final List<JsonValue> list=new ArrayList<>();

	public void add(JsonValue value)
	{
		this.list.add(value);
	}

	@Override
	public Type getType()
	{
		return Type.ARRAY;
	}

	@Override
	public List<JsonValue> asArray()
	{
		return this.list;
	}

	@Override
	public Map<String, JsonValue> asHash()
	{
		throw new IllegalArgumentException("It is a hash, not a constant");
	}

	@Override
	public Object asConstant()
	{
		throw new IllegalArgumentException("It is an array, not a constant");
	}
}
