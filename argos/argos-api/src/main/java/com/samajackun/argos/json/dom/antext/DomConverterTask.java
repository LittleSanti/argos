package com.samajackun.argos.json.dom.antext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.nio.charset.Charset;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileNameMapper;
import org.w3c.dom.Document;

import com.samajackun.argos.json.dom.DomConverter;
import com.samajackun.argos.json.dom.DomPreferences;
import com.samajackun.argos.json.dom.XslHeader;
import com.samajackun.argos.json.model.JsonHash;
import com.samajackun.argos.json.parser.JsonParser;
import com.samajackun.argos.json.parser.ParserException;

public class DomConverterTask extends MatchingTask
{
	public enum NullValueConverterTxt {
		NULL_STRING, EMPTY, OMIT
	};

	private File input;

	private File output;

	private boolean overwrite;

	private Charset inputEncoding=Charset.defaultCharset();

	private Charset outputEncoding=Charset.defaultCharset();

	private String outputRoot="json";

	private final DomPreferences preferences=new DomPreferences();

	private File baseDir;

	private File destDir;

	private Mapper mapperElement;

	private FileNameMapper fileNameMapper;

	private Transformer transformer;

	public FileNameMapper getFileNameMapper()
	{
		return this.fileNameMapper;
	}

	public void addMapper(Mapper mapper)
	{
		if (this.mapperElement != null)
		{
			throw new BuildException("Cannot define more than one mapper");
		}
		else
		{
			this.mapperElement=mapper;
		}
	}

	public void add(FileNameMapper fileNameMapper)
		throws BuildException
	{
		Mapper mapper=new Mapper(getProject());
		mapper.add(fileNameMapper);
		addMapper(mapper);
	}

	public File getBaseDir()
	{
		return this.baseDir;
	}

	public void setBaseDir(File baseDir)
	{
		System.out.println("Quién es el cabrón que llama aquí: " + baseDir);
		this.baseDir=baseDir;
	}

	public File getDestDir()
	{
		return this.destDir;
	}

	public void setDestDir(File destDir)
	{
		this.destDir=destDir;
	}

	public void setNullValueConverter(NullValueConverterTxt nullValueConverterTxt)
	{
		switch (nullValueConverterTxt)
		{
			case EMPTY:
				this.preferences.setNullValueConverter(DomPreferences.EMPTY_STRING_NULL_VALUE_CONVERTER);
				break;
			case NULL_STRING:
				this.preferences.setNullValueConverter(DomPreferences.NULL_STRING_NULL_VALUE_CONVERTER);
				break;
			case OMIT:
				this.preferences.setNullValueConverter(DomPreferences.OMITTER_NULL_VALUE_CONVERTER);
				break;
		}
	}

	public void setDefaultNamespace(String ns)
	{
		this.preferences.setDefaultRootNamespace(ns);
	}

	public void setArrayItemNodeName(String arrayItemNodeName)
	{
		this.preferences.setArrayConverter(DomPreferences.createMultiChildNodeArrayConverter(arrayItemNodeName));
	}

	public Charset getInputEncoding()
	{
		return this.inputEncoding;
	}

	public void setInputEncoding(Charset inputEncoding)
	{
		this.inputEncoding=inputEncoding;
	}

	public Charset getOutputEncoding()
	{
		return this.outputEncoding;
	}

	public void setOutputEncoding(String outputEncodingTxt)
	{
		this.outputEncoding=Charset.forName(outputEncodingTxt);
	}

	public String getOutputRoot()
	{
		return this.outputRoot;
	}

	public void setOutputRoot(String outputRoot)
	{
		this.outputRoot=outputRoot;
	}

	public File getInput()
	{
		return this.input;
	}

	public void setInput(File input)
	{
		this.input=input;
	}

	public File getOutput()
	{
		return this.output;
	}

	public void setOutput(File output)
	{
		this.output=output;
	}

	public void addConfiguredRootNamespace(Namespace namespace)
	{
		this.preferences.putRootNamespace(namespace.getKey(), namespace.getUri());
	}

	public void addConfiguredXslHeader(XslHeader xslHeader)
	{
		if (this.preferences.getXslHeader() != null)
		{
			throw new BuildException("One XSL header already set");
		}
		this.preferences.setXslHeader(xslHeader);
	}

	@Override
	public void execute()
		throws BuildException
	{
		this.transformer=createTransformer();
		if (this.input != null)
		{
			if (this.output == null)
			{
				this.output=new File(this.destDir, this.input.getAbsolutePath() + ".xml");
			}
			convert(this.input, this.output);
		}
		if (this.baseDir != null)
		{
			if (this.destDir == null)
			{
				this.destDir=this.baseDir;
			}
			DirectoryScanner scanner=getDirectoryScanner(this.baseDir);
			String[] list=scanner.getIncludedFiles();
			if (this.mapperElement != null)
			{
				for (String includedFileName : list)
				{
					File input=new File(this.baseDir, includedFileName);
					FileNameMapper mapper=this.mapperElement.getImplementation();
					String[] outputFileNames=mapper.mapFileName(input.getAbsolutePath());
					if (outputFileNames != null)
					{
						if (outputFileNames.length > 1)
						{
							log("Output filenames for input file " + input.getAbsolutePath() + " return more than one name. Using first.", Project.MSG_WARN);
						}
						File output=new File(this.destDir, outputFileNames[0]);
						convert(input, output);
					}
					else
					{
						log("Output filenames for input file " + input.getAbsolutePath() + " return null.", Project.MSG_ERR);
					}
				}
			}
			else
			{
				if (list != null && list.length > 0)
				{
					log("A set of " + list.length + " files was specified, but no mapper defined.", Project.MSG_ERR);
				}
			}
		}
	}

	private Transformer createTransformer()
	{
		try
		{
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("encoding", this.outputEncoding.name());
			return transformer;
		}
		catch (TransformerConfigurationException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}

	private void convert(File input2, File output2)
	{
		File outputParentDir=output2.getParentFile();
		if (!outputParentDir.exists())
		{
			outputParentDir.mkdirs();
		}
		if (!outputParentDir.exists())
		{
			throw new BuildException("Unable to create output directory " + outputParentDir.getAbsolutePath());
		}
		else
		{
			log("Converting JSON file " + input2.getAbsolutePath() + " to XML file " + output2.getAbsolutePath(), Project.MSG_DEBUG);
			if (output2.exists())
			{
				if (this.overwrite)
				{
					log("Overwritting output file " + output2.getAbsolutePath(), Project.MSG_DEBUG);
				}
				else
				{
					throw new BuildException("Output file " + output2.getAbsolutePath() + " already exists.");
				}
			}
			try (PushbackReader reader=new PushbackReader(new InputStreamReader(new FileInputStream(input2), this.inputEncoding)); OutputStream out=new FileOutputStream(output2))
			{
				JsonHash hash=new JsonParser().parse(reader);
				Document doc=DomConverter.toDocument(hash, this.outputRoot, this.preferences);
				serialize(doc, out);
				log("OK. JSON file " + input2.getAbsolutePath() + " successfully converted to XML file " + output2.getAbsolutePath());
			}
			catch (ParserException e)
			{
				log("Error parsing as JSON file " + input2.getAbsolutePath() + ": " + e.getMessage(), Project.MSG_ERR);
				throw new BuildException(e);
			}
			catch (IOException | TransformerException e)
			{
				throw new BuildException(e.toString(), e);
			}
		}
	}

	/**
	 * Serialize a Document onto an OutputStream.
	 *
	 * @param doc Source document.
	 * @param out Target OutputStream.
	 * @exception java.io.IOException If an error occured while writing.
	 * @exception javax.xml.transform.TransformerException If an error occurred while serializing.
	 */
	private void serialize(org.w3c.dom.Document doc, java.io.OutputStream out)
		throws java.io.IOException,
		javax.xml.transform.TransformerException
	{
		javax.xml.transform.Result result=new javax.xml.transform.stream.StreamResult(out);
		javax.xml.transform.Source source=new javax.xml.transform.dom.DOMSource(doc);
		this.transformer.transform(source, result);
	}

	public boolean isOverwrite()
	{
		return this.overwrite;
	}

	public void setOverwrite(boolean overwrite)
	{
		this.overwrite=overwrite;
	}
}
