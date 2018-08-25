package com.samajackun.argos.json.model;

public final class JsonNull extends JsonConstant
{
	private static final JsonNull INSTANCE=new JsonNull();

	public static JsonNull getInstance()
	{
		return INSTANCE;
	}

	private JsonNull()
	{
		super(null);
	}

	@Override
	public Type getType()
	{
		return Type.NULL;
	}
}
