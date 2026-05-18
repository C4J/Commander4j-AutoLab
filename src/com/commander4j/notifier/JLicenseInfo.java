package com.commander4j.notifier;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import com.commander4j.autolab.AutoLab;
import com.commander4j.xml.JXMLDocument;

public class JLicenseInfo implements Comparable<JLicenseInfo>
{
	public static int width_description = 42;
	public static int width_version = 22;
	public String description;
	public String licenceFilename;
	public String version;
	public String type;
	private static JXMLDocument xmlMessage;

	public JLicenseInfo()
	{

	}

	public JLicenseInfo(String desc, String filename, String version, String type)
	{
		this.description = desc;
		this.licenceFilename = filename;
		this.version = version;
		this.type = type;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLicenceFilename()
	{
		return licenceFilename;
	}

	public void setLicenceFilename(String licenceFilename)
	{
		this.licenceFilename = licenceFilename;
	}

	private String pad(String input, int size)
	{
		String result = input;

		if (result == null)
		{
			result = "";
		}

		if (result.length() >= size)
		{
			result = result.substring(0, size - 1) + " ";
		}
		else
		{
			while (result.length() < size)
			{
				result = result + " ";
			}
		}

		return result;
	}

	public String toString()
	{
		return pad(description, width_description) + pad(version, width_version) + type;
	}

	public LinkedList<JLicenseInfo> getLicences()
	{
		HashMap<String, String> licenceTypes = new HashMap<String, String>();

		licenceTypes.clear();

		String filename = "." + File.separator + "lib" + File.separator + "license" + File.separator + "LicenseInfo.xml";
		xmlMessage = new JXMLDocument(filename);

		LinkedList<JLicenseInfo> typeList = new LinkedList<JLicenseInfo>();

		// Get Licence Info
		int position = 1;
		while (xmlMessage.findXPath("//info/licenses/license[" + String.valueOf(position) + "]/type").trim().equals("") == false)
		{
			String type = xmlMessage.findXPath("//info/licenses/license[" + String.valueOf(position) + "]/type").trim();
			String file = xmlMessage.findXPath("//info/licenses/license[" + String.valueOf(position) + "]/filename").trim();

			licenceTypes.put(type, file);

			position++;
		}

		if (licenceTypes.containsKey("GNU General Public License V2") == false)
		{
			licenceTypes.put("GNU General Public License V2", "GNU General Public License V2.txt");
		}

		// Get Library Info
		position = 1;
		while (xmlMessage.findXPath("//info/libraries/library[" + String.valueOf(position) + "]/description").trim().equals("") == false)
		{
			String description = xmlMessage.findXPath("//info/libraries/library[" + String.valueOf(position) + "]/description").trim();
			String type = xmlMessage.findXPath("//info/libraries/library[" + String.valueOf(position) + "]/type").trim();
			String version = xmlMessage.findXPath("//info/libraries/library[" + String.valueOf(position) + "]/version").trim();

			String lfilename = licenceTypes.get(type);
			if (lfilename == null)
			{
				lfilename = "";
			}

			JLicenseInfo test = new JLicenseInfo(description, lfilename, version, type);

			typeList.add(test);
			position++;
		}

		JLicenseInfo me = new JLicenseInfo("AutoLab4j", "GNU General Public License V2.txt", AutoLab.version, "GNU General Public License V2");

		typeList.add(me);

		Collections.sort(typeList);

		return typeList;
	}

	@Override
	public int compareTo(JLicenseInfo o)
	{
		String comparedDesc = o.description;

		return this.description.toUpperCase().compareTo(comparedDesc.toUpperCase());
	}

}
