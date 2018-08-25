package com.samajackun.argos.json.model;

import java.util.List;
import java.util.Map;

public interface JsonValue
{
	public enum Type {
		CONSTANT, NULL, HASH, ARRAY
	};

	public Type getType();

	public List<JsonValue> asArray();

	public Map<String, JsonValue> asHash();

	public Object asConstant();
}
