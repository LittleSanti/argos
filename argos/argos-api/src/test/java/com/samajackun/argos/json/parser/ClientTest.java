package com.samajackun.argos.json.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Currency;

import org.junit.Test;

import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.parser.JsonParser;
import com.samajackun.argos.json.parser.ParserException;

public class ClientTest
{
	public double convert(Currency src, Currency tgt)
		throws IOException,
		ParserException
	{
		// https://free.currencyconverterapi.com/api/v5/convert?q=USD_EUR&compact=y
		String coordinates=src.getCurrencyCode() + "_" + tgt.getCurrencyCode();
		URL url=new URL("https://free.currencyconverterapi.com/api/v5/convert?q=" + coordinates + "&compact=y");
		try (Reader reader=new InputStreamReader(url.openStream(), "ISO-8859-1"))
		{
			JsonHash hash=new JsonParser().parse(reader);
			String s=hash.get(coordinates).asHash().get("val").asConstant().toString();
			double rate=Double.parseDouble(s);
			System.out.printf("1 %s = %f %s\n", src.getSymbol(), rate, tgt.getSymbol());
			return rate;
		}
	}

	@Test
	public void dollarToEuro()
		throws IOException,
		ParserException
	{
		convert(Currency.getInstance("USD"), Currency.getInstance("EUR"));
	}

	@Test
	public void britishPundToEuro()
		throws IOException,
		ParserException
	{
		convert(Currency.getInstance("GBP"), Currency.getInstance("EUR"));
	}
}
