package com.samajackun.argos.json.dom;

import org.w3c.dom.Element;

interface NullValueConverter
{
	public void toElement(Element parent, String propertyName);
}
