package com.samajackun.argos.json.dom;

import org.w3c.dom.Element;

class XsiNullValueConverter implements NullValueConverter
{
	@Override
	public void toElement(Element parent, String propertyName)
	{
		Element newElement=parent.getOwnerDocument().createElement(propertyName);
		newElement.setAttribute("xsi:nil", "true");
		parent.appendChild(newElement);
	}

}
