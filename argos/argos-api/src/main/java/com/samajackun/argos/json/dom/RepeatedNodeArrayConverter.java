package com.samajackun.argos.json.dom;

import java.util.List;

import org.w3c.dom.Element;

import com.samajackun.argos.json.model.JsonValue;

class RepeatedNodeArrayConverter implements ArrayConverter
{
	@Override
	public void toDom(Element parent, String propertyName, List<JsonValue> array, DomPreferences preferences)
	{
		for (JsonValue item : array)
		{
			DomConverter.createAndImportToNode(parent, propertyName, item, preferences);
		}
	}
}
