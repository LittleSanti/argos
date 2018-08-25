package com.samajackun.argos.json.dom;

import java.util.List;

import org.w3c.dom.Element;

import com.samajackun.argos.json.model.JsonValue;

class MultiChildNodeArrayConverter implements ArrayConverter
{
	private final String itemNodeName;

	public MultiChildNodeArrayConverter(String itemNodeName)
	{
		super();
		this.itemNodeName=itemNodeName;
	}

	@Override
	public void toDom(Element parent, String propertyName, List<JsonValue> array, DomPreferences preferences)
	{
		Element newParent=parent.getOwnerDocument().createElement(propertyName);
		String newElementName=(".".equals(this.itemNodeName))
			? parent.getNodeName()
			: this.itemNodeName;
		for (JsonValue item : array)
		{
			DomConverter.createAndImportToNode(newParent, newElementName, item, preferences);
		}
		parent.appendChild(newParent);
	}

}
