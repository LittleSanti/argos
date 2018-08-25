package com.samajackun.argos.json.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

class StringNullValueConverter implements NullValueConverter
{
	private final String value;

	public StringNullValueConverter(String value)
	{
		super();
		if (value == null)
		{
			throw new IllegalArgumentException("Null value not allowed");
		}
		this.value=value;
	}

	@Override
	public void toElement(Element parent, String propertyName)
	{
		Element newElement=parent.getOwnerDocument().createElement(propertyName);
		parent.appendChild(newElement);
		Text nullNode=parent.getOwnerDocument().createTextNode(this.value);
		newElement.appendChild(nullNode);
	}
}
