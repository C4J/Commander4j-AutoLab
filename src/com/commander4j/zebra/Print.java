package com.commander4j.zebra;

import java.io.*;
import java.net.*;
import org.apache.logging.log4j.Logger;

import com.commander4j.autolab.AutoLab;
import com.commander4j.prodLine.ProdLine;
import com.commander4j.resources.JRes;

/**
 * @author dave
 *
 */
public class Print extends Thread
{
	private String ipAddress = "0.0.0.0";
	private int portNumber = 9100;
	private String printerName = "";
	private boolean run = true;
	private boolean dataReady = false;
	private String data = "";
	private Socket clientSocket;
	private String errorMessage = "";
	private String uuid = "";
	private String lastMessage = "";
	private Logger logger = org.apache.logging.log4j.LogManager.getLogger((Print.class));
	private boolean firstError = true;
	private OutputStreamWriter outToServer;

	public Print(String uuid, String name)
	{
		this.uuid = uuid;
		setPrinterName(name);
		setThreadName();
		logger.debug("[" + getUuid() + "] {" + getName() + "} Print Instance Created.");
	}

	public void appendNotification(String message)
	{
		if (message.equals(lastMessage) == false)
		{
			((ProdLine) AutoLab.threadList_ProdLine.get(uuid)).appendNotification(message);
			lastMessage = message;
		}
	}

	public void setNotification(String message)
	{
		((ProdLine) AutoLab.threadList_ProdLine.get(uuid)).setNotification(message);
	}

	public void shutdown()
	{
		logger.debug("[" + getUuid() + "] {" + getName() + "} Print Thread Shutdown Requested.");
		run = false;
	}

	public void setPrinterName(String name)
	{
		this.printerName = name;
		setThreadName();
	}

	public void setThreadName()
	{
		setName("Print " + getPrinterName() + " (" + ipAddress + ":" + String.valueOf(getPortNumber()) + ")");
	}

	public String getPrinterName()
	{
		return printerName;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
		setThreadName();
		logger.debug("[" + getUuid() + "] {" + getName() + "} IP Address set to " + getIpAddress());
	}

	public int getPortNumber()
	{
		return portNumber;
	}

	public void setPortNumber(int portNumber)
	{
		this.portNumber = portNumber;
		setThreadName();
		logger.debug("[" + getUuid() + "] {" + getName() + "} Port set to " + getPortNumber());
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public void setDataReady(boolean dataReady)
	{
		this.dataReady = dataReady;
	}

	public boolean isDataReady()
	{
		return dataReady;
	}

	public boolean setData(String data)
	{
		boolean result = false;

		if (isDataReady() == false)
		{
			this.data = data;
			result = true;
		}

		return result;
	}

	public String getData()
	{
		return this.data;
	}

	public void run()
	{

		run = true;
		logger.debug("[" + getUuid() + "] {" + getName() + "} Thread Started...");

		while (run == true)
		{

			try
			{

				if (isDataReady())
				{

					if (AutoLab.config.isSuppressLabelPrint())
					{
						setDataReady(false);
						logger.debug("[" + getUuid() + "] {" + getName() + "} PRINT SUPPRESSED - see config.xml ");
						appendNotification("PRINT SUPPRESSED - see config.xml");
					}
					else
					{

						try
						{
							if (firstError)
							{
								appendNotification(JRes.getText("sending_data_to_printer") + " [" + getPrinterName() + "] [" + getIpAddress() + "]:" + getPortNumber() + "]");
								// firstError = false;
							}

							this.clientSocket = new Socket(this.ipAddress, this.portNumber);

							outToServer = new OutputStreamWriter(clientSocket.getOutputStream());

							logger.debug("<<<-----------------------------)>>");
							logger.debug("writeBytes start of data stream");
							logger.debug("<<<-----------------------------)>>");
							logger.debug(this.getData());

							outToServer.write(this.getData());
							outToServer.flush();

							outToServer.close();
							logger.debug("<<<-----------------------------)>>");
							logger.debug("<<<writeBytes end of data stream>>>");
							logger.debug("<<<-----------------------------)>>");

							clientSocket.close();

							setErrorMessage("");

							setDataReady(false);

							appendNotification("Print complete.");
							firstError = true;
						}
						catch (UnknownHostException e)
						{
							setErrorMessage(e.getLocalizedMessage());
							if (firstError)
							{
								appendNotification(JRes.getText("error_sending_data_to_printer") + " : " + e.getLocalizedMessage());
								logger.debug("Error Sending data to printer : " + e.getLocalizedMessage());
								firstError = false;
							}
						}
						catch (IOException e)
						{
							setErrorMessage(e.getLocalizedMessage());
							if (firstError)
							{
								appendNotification(JRes.getText("error_sending_data_to_printer") + " : " + e.getLocalizedMessage());
								logger.debug("Error Sending data to printer : " + e.getLocalizedMessage());
								firstError = false;
							}
						}
						finally
						{

							if (outToServer != null)
							{
								
								try
								{
									outToServer.close();
								}
								catch (IOException e)
								{
									logger.debug("Error closing OutputStreamWriter : " + e.getLocalizedMessage());
								}

								outToServer = null;
							}

							if (this.clientSocket != null)
							{
								if (this.clientSocket.isConnected())
								{
									try
									{
										this.clientSocket.close();
									}
									catch (IOException e)
									{
										logger.debug("Error closing Socket : " + e.getLocalizedMessage());
									}
								}

								this.clientSocket = null;
							}
						}
					}
				}

				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				logger.debug("[" + getUuid() + "] {" + getName() + "} Interrupted Exception " + e.getLocalizedMessage());
				run = false;
			}
		}

		logger.debug("[" + getUuid() + "] {" + getName() + "} Thread Stopped...");
	}
}
