package com.commander4j.config;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.commander4j.utils.JUtility;
import com.commander4j.xml.JXMLDocument;

public class Config
{
	private Logger logger = org.apache.logging.log4j.LogManager.getLogger((Config.class));
	private JXMLDocument xmlDoc;
	private String filename = "";
	private String path = "";
	private String enabled = "";
	private HashMap<String, ProdLineDefinition> config_ProdLines = new HashMap<String, ProdLineDefinition>();
	private HashMap<String,String> batch_Formats = new HashMap<String,String>();
	private HashMap<String,String> fieldNamesLookup = new HashMap<String,String>();
	private HashMap<String,String> systemKeys = new HashMap<String,String>();
	private HashMap<String,String> formats = new HashMap<String,String>();
	private HashMap<String,String> clone = new HashMap<String,String>();
	private String labelSyncPath = "";
	private int labelSyncFrequency=10;
	private String dataSetPath = "";
	private String ssccSequencePath = "";
	private String outputPath = "";
	private boolean suppress_Label_Print = false;
	private boolean suppress_Output_Message = false;
	private boolean emailEnabled = false;
	private String SSCCPerPallet = "2";
	private String LabelsPerSSCC = "2";

	private String SSCCPerSemiPallet = "2";
	private String LabelsPerSemiSSCC = "4";

	private JUtility util = new JUtility();
	private boolean labelaryEnabled = false;
	private String labelaryURL = "";
	private int WatchDogPort = 8000;
	
	public String getLabelaryURL()
	{
		return labelaryURL;
	}

	public void setLabelaryURL(String labelaryURL)
	{
		this.labelaryURL = labelaryURL;
	}

	public boolean isLabelaryEnabled()
	{
		return labelaryEnabled;
	}

	public void setLabelaryEnabled(boolean labelaryEnabled)
	{
		this.labelaryEnabled = labelaryEnabled;
	}
	
	public boolean isEmailEnabled()
	{
		return emailEnabled;
	}

	public void setEmailEnabled(boolean emailEnable)
	{
		this.emailEnabled = emailEnable;
	}

	public HashMap<String, ProdLineDefinition> getConfigProdLines()
	{
		return config_ProdLines;
	}

	public void setConfigProdLines(HashMap<String, ProdLineDefinition> config_ProdLines)
	{
		this.config_ProdLines = config_ProdLines;
	}
	
	public void setWatchDogPort(String port)
	{
		try
		{
			WatchDogPort=Integer.parseInt(port);
			if (WatchDogPort==0)
			{
				WatchDogPort =  8000;
			}	
		}
		catch (NumberFormatException ex)
		{
			WatchDogPort =  8000;
		}
	}

	public int getWatchDogPort()
	{
		return WatchDogPort;
	}
	
	public Config()
	
	{
		logger.debug("");
		setPath(System.getProperty("user.dir") + File.separator + "xml" + File.separator + "config" + File.separator);
		setFilename("config.xml");

		xmlDoc = new JXMLDocument(getPath() + getFilename());
		
		setEmailEnabled(Boolean.valueOf(xmlDoc.findXPath("/config/email/@enabled")));
		logger.debug("Config - Email Enabled               [" + isEmailEnabled() + "]");
		
		setSuppressLabelPrint(Boolean.valueOf(xmlDoc.findXPath("/config/debug/@suppressLabelPrint")));
		logger.debug("Config - Suppress Label Print        [" + isSuppressLabelPrint() + "]");
		
		setSuppressOutputMessage(Boolean.valueOf(xmlDoc.findXPath("/config/debug/@suppressOutputMessage")));
		logger.debug("       - Suppress Output XML         [" + isSuppressOutputMessage() + "]");
		
		setWatchDogPort(xmlDoc.findXPath("/config/watchDog/@port"));
		logger.debug("       - WatchDogPort                [" + String.valueOf(getWatchDogPort()) + "]");
		
		setLabelSyncPath(util.formatPath(xmlDoc.findXPath("/config/paths/labelSync/@path")));
		logger.debug("       - Label Sync Path             [" + getLabelSyncPath() + "]");	
		
		setLabelSyncFrequency(Integer.valueOf(xmlDoc.findXPath("/config/paths/labelSync/@frequency")));
		logger.debug("       - Label Sync Frequency        [" + getLabelSyncFrequency() + "]");
		
		setDataSetPath(util.formatPath(xmlDoc.findXPath("/config/paths/dataSet/@path")));
		logger.debug("       - DataSet Path                [" + getDataSetPath() + "]");
		
		setOutputPath(util.formatPath(xmlDoc.findXPath("/config/paths/output/@path")));
		logger.debug("       - Output Path                 [" + getOutputPath() + "]");
		
		setSSCSequencePath(util.formatPath(xmlDoc.findXPath("/config/paths/ssccSequence/@path")));
		logger.debug("       - SSCC Sequence Path          [" + getSSCSequencePath() + "]");
		
		setSSCCPerPallet(xmlDoc.findXPath("/config/labels/pallet/@ssccPerPallet"));
		logger.debug("       - SSCC Per Pallet            [" + getSSCCPerPallet() + "]");
		
		setLabelsPerSSCC(xmlDoc.findXPath("/config/labels/pallet/@labelsPerSSCC"));
		logger.debug("       - Labels Per SSCC            [" + getLabelsPerSSCC() + "]");
				
		setSSCCPerSemiPallet(xmlDoc.findXPath("/config/labels/semiPallet/@ssccPerPallet"));
		logger.debug("       - SEMI PALLET Per Print       [" + getSSCCPerSemiPallet() + "]");
		
		setLabelsPerSemiSSCC(xmlDoc.findXPath("/config/labels/semiPallet/@labelsPerSSCC"));
		logger.debug("       - SEMI PALLET Print Total     [" + getLabelsPerSemiSSCC() + "]");
		
		setLabelaryEnabled(Boolean.valueOf(xmlDoc.findXPath("/config/labelary/@enabled")));
		logger.debug("Config - Labelary Enabled            [" + isLabelaryEnabled() + "]");
		
		setLabelaryURL(xmlDoc.findXPath("/config/labelary/url"));
		logger.debug("Config - Labelary URL                [" + getLabelaryURL() + "]");
		
		logger.debug("");

		int seq = 1;

		while (xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/@enabled").equals("") == false)
		{
			enabled = xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/@enabled");

			if (enabled.toUpperCase().equals("Y"))
			{
				ProdLineDefinition prodLine = new ProdLineDefinition();

				prodLine.setProdLine_Name(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/@name"));
				logger.debug("Line   - Production Line      [" + prodLine.getProdLine_Name() + "]");

				prodLine.setModbus_Name(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/@name"));
				logger.debug("         Modbus Name          [" + prodLine.getModbus_Name() + "]");

				prodLine.setModbus_IPAddress(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/ipAddress"));
				logger.debug("         Modbus IP Address    [" + prodLine.getModbus_IPAddress() + "]");

				prodLine.setModbus_Port(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/portNumber"));
				logger.debug("         Modbus Port          [" + prodLine.getModbus_Port() + "]");

				prodLine.setModbus_Coil_Address(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/coilAddress"));
				logger.debug("         Modbus Coil          [" + prodLine.getModbus_Coil_Address() + "]");
				
				prodLine.setSemiPallet_Modbus_Coil_Address(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/semiPalletCoilAddress"));
				logger.debug("         semiPallet Coil      [" + prodLine.getSemiPallet_Modbus_Coil_Address() + "]");

				prodLine.setModbus_Timeout(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/timeout"));
				logger.debug("         Modbus Timeout       [" + prodLine.getModbus_Timeout() + "]");
				
				prodLine.setModbus_Retry(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/retryDelay"));
				logger.debug("         Modbus Retry         [" + prodLine.getModbus_Retry() + "]");

				prodLine.setModbus_Coil_Trigger_Value(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/modbus/coilTriggerValue"));
				logger.debug("         Coil Trigger Value   [" + prodLine.getModbus_Coil_Trigger_Value() + "]");

				prodLine.setPrinter_Name(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/printer//@name"));
				logger.debug("         Printer Name         [" + prodLine.getPrinter_Name() + "]");

				prodLine.setSscc_Filename(xmlDoc.findXPath("/config/productionLines/productionLine[" + String.valueOf(seq) + "]/SSCC//@filename"));
				logger.debug("         SSCC Seq Filename    [" + prodLine.getSscc_Filename() + "]");
				
				config_ProdLines.put(prodLine.getProdLine_Name(), prodLine);
				logger.debug("");
			}

			seq++;
		}
		logger.debug("Config - Loaded " + config_ProdLines.size() + " Production Lines.");
		
		seq = 1;
		String customerID="";
		String batchFormat="";

		logger.debug("Batch Formats");
		while (xmlDoc.findXPath("/config/batchFormats/customer[" + String.valueOf(seq) + "]/@id").equals("") == false)
		{
			customerID = xmlDoc.findXPath("/config/batchFormats/customer[" + String.valueOf(seq) + "]/@id");
			batchFormat = xmlDoc.findXPath("/config/batchFormats/customer[" + String.valueOf(seq) + "]/@pattern");
			
			batch_Formats.put(customerID, batchFormat);
			logger.debug("Customer ID=["+customerID+"]  Batch Format = ["+batchFormat+"]");
			
			seq++;
		}
		logger.debug("");	
		
		seq = 1;
		String csvFieldname="";
		String dbFieldnsame="";

		logger.debug("Fieldname Translation Matrix");

		while (xmlDoc.findXPath("/config/fieldNamesLookup/fieldName[" + String.valueOf(seq) + "]/@csv").equals("") == false)
		{
			csvFieldname = xmlDoc.findXPath("/config/fieldNamesLookup/fieldName[" + String.valueOf(seq) + "]/@csv");
			dbFieldnsame = xmlDoc.findXPath("/config/fieldNamesLookup/fieldName[" + String.valueOf(seq) + "]/@db");
			
			fieldNamesLookup.put(csvFieldname, dbFieldnsame);
			logger.debug("CSV Field=["+csvFieldname+"]  DB Field = ["+dbFieldnsame+"]");
			
			seq++;
		}
		logger.debug("");	

		
		seq = 1;
		String key="";
		String value="";

		logger.debug("System Keys");

		while (xmlDoc.findXPath("/config/controls/system[" + String.valueOf(seq) + "]/@key").equals("") == false)
		{
			key = xmlDoc.findXPath("/config/controls/system[" + String.valueOf(seq) + "]/@key");
			value = xmlDoc.findXPath("/config/controls/system[" + String.valueOf(seq) + "]/@value");
			
			systemKeys.put(key, value);
			logger.debug("System Key=["+key+"]  Value = ["+value+"]");
			
			seq++;
		}
		logger.debug("");
		
		seq = 1;
		String field="";
		String format="";

		logger.debug("Formats");

		while (xmlDoc.findXPath("/config/formats/field[" + String.valueOf(seq) + "]/@name").equals("") == false)
		{
			field = xmlDoc.findXPath("/config/formats/field[" + String.valueOf(seq) + "]/@name");
			format = xmlDoc.findXPath("/config/formats/field[" + String.valueOf(seq) + "]/@format");
			
			formats.put(field, format);
			logger.debug("Field=["+field+"]  Format = ["+format+"]");
			
			seq++;
		}
		logger.debug("");
		
		seq = 1;
		String from="";
		String to="";

		logger.debug("Clone");

		while (xmlDoc.findXPath("/config/clone/field[" + String.valueOf(seq) + "]/@from").equals("") == false)
		{
			from = xmlDoc.findXPath("/config/clone/field[" + String.valueOf(seq) + "]/@from");
			to = xmlDoc.findXPath("/config/clone/field[" + String.valueOf(seq) + "]/@to");
			
			clone.put(from, to);
			logger.debug("From=["+field+"]  To = ["+to+"]");
			
			seq++;
		}
		logger.debug("");

	}
	
	public String getDBFieldnameFromCSVFieldname(String field)
	{
		String result=field;
		
		if (fieldNamesLookup.containsKey(field))
		{
			result = fieldNamesLookup.get(field);		
		}
		
		return result;
	}
	
	public String getCustomerBatchFormat(String customerID)
	{
		String result="";
		
		if (batch_Formats.containsKey(customerID))
		{
			result = batch_Formats.get(customerID);
		}
		
		return result;
	}
	
	public String getSystemKey(String id)
	{
		String result="";
		
		if (systemKeys.containsKey(id))
		{
			result = systemKeys.get(id);
		}

		return result;
	}
	
	public String getCloneFieldName(String field)
	{
		String result = "";
		
		if (clone.containsKey(field))
		{
			result = clone.get(field);
		}
		
		return result;
	}
	
	public String getFormat(String field)
	{
		String result = "";
		
		if (formats.containsKey(field))
		{
			result = formats.get(field);
		}
		
		return result;
	}
	
	public boolean isSuppressLabelPrint()
	{
		return suppress_Label_Print;
	}

	public void setSuppressLabelPrint(boolean suppress_Label_Print)
	{
		this.suppress_Label_Print = suppress_Label_Print;
	}

	public boolean isSuppressOutputMessage()
	{
		return suppress_Output_Message;
	}

	public void setSuppressOutputMessage(boolean suppress_Output_Message)
	{
		this.suppress_Output_Message = suppress_Output_Message;
	}

	public String getSSCSequencePath()
	{
		return ssccSequencePath;
	}

	public void setSSCSequencePath(String ssccSequencePath)
	{
		if (util.replaceNullStringwithBlank(ssccSequencePath).equals(""))
		{
			this.ssccSequencePath = System.getProperty("user.dir") + File.separator + "xml" + File.separator + "sscc" + File.separator;
		}
		else
		{
			this.ssccSequencePath = ssccSequencePath;
		}
		
		if (this.ssccSequencePath.endsWith(File.separator)==false)
		{
			this.ssccSequencePath = this.ssccSequencePath+File.separator;
		}

	}
	
	public String getDataSetPath()
	{
		return dataSetPath;
	}

	public void setDataSetPath(String dataSetPath)
	{
		this.dataSetPath = dataSetPath;
	}

	public String getOutputPath()
	{
		return outputPath;
	}

	public void setOutputPath(String outPath)
	{
		this.outputPath = outPath;
	}

	public String getLabelSyncPath()
	{
		return labelSyncPath;
	}

	public void setLabelSyncPath(String labelSyncPath)
	{
		this.labelSyncPath = labelSyncPath;
	}

	public int getLabelSyncFrequency()
	{
		return labelSyncFrequency;
	}

	public void setLabelSyncFrequency(int labelSyncFrequency)
	{
		this.labelSyncFrequency = labelSyncFrequency;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getSSCCPerPallet()
	{
		return SSCCPerPallet;
	}

	public void setSSCCPerPallet(String palletPerPrint)
	{
		SSCCPerPallet = palletPerPrint;
	}

	public String getLabelsPerSSCC()
	{
		return LabelsPerSSCC;
	}

	public void setLabelsPerSSCC(String palletTotal)
	{
		LabelsPerSSCC = palletTotal;
	}

	public String getSSCCPerSemiPallet()
	{
		return SSCCPerSemiPallet;
	}

	public void setSSCCPerSemiPallet(String semiPalletPerPrint)
	{
		SSCCPerSemiPallet = semiPalletPerPrint;
	}

	public String getLabelsPerSemiSSCC()
	{
		return LabelsPerSemiSSCC;
	}

	public void setLabelsPerSemiSSCC(String semiPalletTotal)
	{
		LabelsPerSemiSSCC = semiPalletTotal;
	}



}
