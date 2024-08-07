package com.commander4j.prodLine;

import org.apache.logging.log4j.Logger;

import com.commander4j.autolab.AutoLab;
import com.commander4j.dataset.DataSet;
import com.commander4j.modbus.Modbus;
import com.commander4j.notifier.JFrameNotifier;
import com.commander4j.notifier.JFramePreview;
import com.commander4j.notifier.TrayIconProdLineStatus;
import com.commander4j.resources.JRes;
import com.commander4j.utils.JUnique;
import com.commander4j.utils.JWait;
import com.commander4j.zebra.Print;

public class ProdLine extends Thread
{

	private String modbusName;
	private String modbusIPAddress;
	private int modbusPortNumber;
	private int modbusTimeOut;
	private int modbusRetryDelay;
	private int modbusCoil;
	private int semiPalletCoilAddress;
	private boolean modbusPrintOnValue;
	private String prodLineName="";
	private String printerName;
	private String printerIPAddress;
	private int printerPortNumber;
	private boolean run = true;
	private JWait wait = new JWait();
	private String remoteDataSetPath="";
	private String uuid = "";
	private JUnique unique = new JUnique();
	public DataSet dataset1;
	public Print print1;
	private String ssccSequenceFilename;
	private Logger logger = org.apache.logging.log4j.LogManager.getLogger((ProdLine.class));
	public JFrameNotifier prodLineNotify;
	public JFramePreview prodLinePreview;
	
	public ProdLine(String prodLineName,String modbusName,String modbusIPAddress,int modbusPortNumber,int modbusCoil,int modbusTimeout,boolean modbusPrintOnValue,String printerName,String remoteDataSetPath,String ssccSequenceFilename,int semiPalletCoilAddress,int modbusRetryDelay)
	{
		this.prodLineName=prodLineName;
		this.modbusName = modbusName;
		this.modbusIPAddress = modbusIPAddress;
		this.modbusPortNumber = modbusPortNumber;
		this.modbusCoil = modbusCoil;
		this.modbusTimeOut = modbusTimeout;
		this.modbusPrintOnValue = modbusPrintOnValue;
		this.printerName = printerName;
		this.remoteDataSetPath = remoteDataSetPath;
		this.uuid = unique.getUniqueID();
		this.ssccSequenceFilename = ssccSequenceFilename;
		this.semiPalletCoilAddress = semiPalletCoilAddress;
		this.modbusRetryDelay = modbusRetryDelay;
				
		setName("ProdLine "+prodLineName);	
		
		logger.debug("["+getUuid()+"] {"+getName()+"} Instance Created.");
	}
	
	public void shutdown()
	{
		run = false;
	}
	
	public void appendNotification(String message)
	{
		prodLineNotify.appendToMessage(message);
	}
	
	public void setNotification(String message)
	{
		prodLineNotify.setMessage(message);
	}

	public String getSsccSequenceFilename()
	{
		return ssccSequenceFilename;
	}

	public void setSsccSequenceFilename(String ssccSequenceFilename)
	{
		this.ssccSequenceFilename = ssccSequenceFilename;
	}

	public String getProdLineName()
	{
		return prodLineName;
	}

	public void setProdLineName(String prodLineName)
	{
		this.prodLineName = prodLineName;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
	
	public String getRemoteDataSetPath()
	{
		return remoteDataSetPath;
	}

	public void setRemoteDataSetPath(String remoteDataSetPath)
	{
		this.remoteDataSetPath = remoteDataSetPath;
	}

	public String getModbusName()
	{
		return modbusName;
	}

	public void setModbusName(String modbusName)
	{
		this.modbusName = modbusName;
	}

	public String getModbusIPAddress()
	{
		return modbusIPAddress;
	}

	public void setModbusIPAddress(String modbusIPAddress)
	{
		this.modbusIPAddress = modbusIPAddress;
	}

	public int getModbusPortNumber()
	{
		return modbusPortNumber;
	}

	public void setModbusPortNumber(int modbusPortNumber)
	{
		this.modbusPortNumber = modbusPortNumber;
	}

	public int getModbusTimeOut()
	{
		return modbusTimeOut;
	}
	
	public int getModbusRetryDelay()
	{
		return modbusRetryDelay;
	}

	public void setModbusTimeOut(int modbusTimeOut)
	{
		this.modbusTimeOut = modbusTimeOut;
	}
	
	public void setModbusRetryDelay( int modbusRetryDelay)
	{
		this.modbusRetryDelay = modbusRetryDelay;
	}

	public int getModbusCoil()
	{
		return modbusCoil;
	}
	
	public int getSemiPalletCoil()
	{
		return semiPalletCoilAddress;
	}

	public void setModbusCoil(int modbusAddress)
	{
		this.modbusCoil = modbusAddress;
	}

	public void setSemiPalletCoil(int semiPalletCoilAddress)
	{
		this.semiPalletCoilAddress = semiPalletCoilAddress;
	}
	
	public boolean isModbusPrintOnValue()
	{
		return modbusPrintOnValue;
	}

	public void setModbusPrintOnValue(boolean modbusPrintOnValue)
	{
		this.modbusPrintOnValue = modbusPrintOnValue;
	}

	public String getPrinterName()
	{
		return printerName;
	}

	public void setPrinterName(String printerName)
	{
		this.printerName = printerName;
	}

	public String getPrinterIPAddress()
	{
		return printerIPAddress;
	}

	public void setPrinterIPAddress(String printerIPAddress)
	{
		this.printerIPAddress = printerIPAddress;
	}

	public int getPrinterPortNumber()
	{
		return printerPortNumber;
	}

	public void setPrinterPortNumber(int printerPortNumber)
	{
		this.printerPortNumber = printerPortNumber;
	}

	public void run()
	{

		run = true;
		
		prodLineNotify = new JFrameNotifier(getUuid(),getProdLineName(),JRes.getText("starting")+" ["+getProdLineName()+"]");
		prodLineNotify.setVisible(true);
		
		if (AutoLab.config.isLabelaryEnabled() == true)
		{
			prodLinePreview = new JFramePreview(getUuid(),getProdLineName(),JRes.getText("starting")+" ["+getProdLineName()+"]");
			prodLinePreview.setVisible(true);
		}


		logger.debug("["+getUuid()+"] {"+getName()+"} Thread Started...");
		
		AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_STARTUP, "Startup");

		
		AutoLab.start_SSCC_Thread(getUuid(),getSsccSequenceFilename());
		prodLineNotify.appendToMessage(JRes.getText("starting_background_process")+ " SSCC");
		
		dataset1 = new DataSet(getUuid(),getProdLineName(),getPrinterName(),getRemoteDataSetPath());
		dataset1.start();
		prodLineNotify.appendToMessage(JRes.getText("starting_background_process")+ " DataSet");


		print1 = new Print(getUuid(),getPrinterName());
		print1.start();
		prodLineNotify.appendToMessage(JRes.getText("starting_background_process")+ " "+JRes.getText("printer"));
		
		Modbus modbus1;
		modbus1 = new Modbus(getUuid(),getName()+" "+getModbusName(),getModbusIPAddress(),getModbusPortNumber(),getModbusTimeOut(),getModbusCoil(),isModbusPrintOnValue(),getSsccSequenceFilename(),getSemiPalletCoil(),getModbusRetryDelay());
		modbus1.start();
		prodLineNotify.appendToMessage(JRes.getText("starting_background_process")+ " Modbus");
		
		AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_OK, "");
		
		while (run == true)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				run=false;
			}
		}
		

		
		try
		{
			if (AutoLab.JVMShuttingDown==false)
			{
				AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_SHUTDOWN4, "Shutdown Stage 4");
			}
			
			int retries=0;
			while (modbus1.isAlive() && (retries<10))
			{
				modbus1.shutdown();
				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException e)
				{

				}
			}
			if (modbus1.isAlive())
			{
				modbus1.interrupt();
			}

		}
		catch (Exception ex1)
		{
			logger.debug("["+getUuid()+"] {"+getName()+"} Exception : "+ex1.getLocalizedMessage());
		}
		
		if (AutoLab.JVMShuttingDown==false)
		{
			prodLineNotify.appendToMessage(JRes.getText("stopped_background_process")+" Modbus");
		}
		
		try
		{
			if (AutoLab.JVMShuttingDown==false)
			{
				AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_SHUTDOWN3, "Shutdown Stage 3");
			}
			while (print1.isAlive())
			{
				print1.shutdown();
				wait.milliSec(250);
			}
		}
		catch (Exception ex1)
		{
			logger.debug("["+getUuid()+"] {"+getName()+"} Exception : "+ex1.getLocalizedMessage());
		}
		
		if (AutoLab.JVMShuttingDown==false)
		{
			prodLineNotify.appendToMessage(JRes.getText("stopped_background_process")+" "+JRes.getText("printer"));
			AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_SHUTDOWN2, "Shutdown 2");
		}
		
		try
		{
			while (dataset1.isAlive())
			{
				dataset1.shutdown();
				wait.milliSec(250);
			}
		}
		catch (Exception ex1)
		{
			logger.debug("["+getUuid()+"] {"+getName()+"} Exception : "+ex1.getLocalizedMessage());
		}

		if (AutoLab.JVMShuttingDown==false)
		{
			prodLineNotify.appendToMessage(JRes.getText("stopped_background_process")+" DataSet");
			
			AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_SHUTDOWN1, "Shutdown 1");
			
			AutoLab.stop_SSCC_Thread(getSsccSequenceFilename());
		}
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{

		}

		if (AutoLab.config.isLabelaryEnabled() == true)
		{
			if (AutoLab.JVMShuttingDown==false)
			{
				prodLinePreview.setVisible(false);
				prodLinePreview.dispose();
				prodLinePreview=null;
			}
		}
		
		if (AutoLab.JVMShuttingDown==false)
		{
			prodLineNotify.appendToMessage(JRes.getText("stopped_background_process")+" SSCC");
			
			AutoLab.updateTrayIconStatus(getUuid()).setStatus(TrayIconProdLineStatus.status_SHUTDOWN, "Shutdown");
		}
		
		logger.debug("["+getUuid()+"] {"+getName()+"} Thread Stopped...");
		
		if (AutoLab.JVMShuttingDown==false)
		{
			prodLineNotify.setVisible(false);
			prodLineNotify.dispose();
			prodLineNotify=null;
		}

	}
	
}
