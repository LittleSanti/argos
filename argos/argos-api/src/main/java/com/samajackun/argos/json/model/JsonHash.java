package com.samajackun.argos.json.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonHash implements JsonValue
{
	public Map<String, JsonValue> map=new LinkedHashMap<>();

	public JsonValue put(String name, JsonValue object)
	{
		return this.map.put(name, object);
	}

	@Override
	public Type getType()
	{
		return Type.HASH;
	}

	public Set<String> keySet()
	{
		return this.map.keySet();
	}

	public JsonValue get(String key)
	{
		return this.map.get(key);
	}

	@Override
	public List<JsonValue> asArray()
	{
		throw new IllegalArgumentException("It is a hash, not an array");
	}

	@Override
	public Map<String, JsonValue> asHash()
	{
		return this.map;
	}

	@Override
	public Object asConstant()
	{
		throw new IllegalArgumentException("It is a hash, not an array");
	}
}
