package com.commander4j.watchdog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.Logger;

import com.commander4j.autolab.AutoLab;
import com.commander4j.autolab.StartStop;
import com.commander4j.resources.JRes;

public class WatchDog extends Thread
{

	private boolean run = true;
	private Logger logger = org.apache.logging.log4j.LogManager.getLogger((WatchDog.class));
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private Integer port = 8000;

	public WatchDog(int port)
	{
		this.port = port;
		logger.debug("WatchDog Instance Created.");
	}

	public void shutdown()
	{
		try
		{
			clientSocket.close();
		}
		catch (Exception e)
		{

		}
		this.run = false;
	}

	public void run()
	{

		AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_starting"));
		logger.debug("WatchDog Instance Started.");

		run = true;

		while (run == true)
		{

			try
			{
				serverSocket = new ServerSocket(port, 1);
				AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_listening_on_port") + " : " + String.valueOf(port));
				logger.debug("WatchDog listening on port " + " : " + String.valueOf(port));

				while (true)
				{

					clientSocket = serverSocket.accept();

					String clientIP = clientSocket.getInetAddress().getHostAddress();
					if (run)
					{
					AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_connection_detected") + " : " + clientIP);
					}

					try
					{
						clientSocket.setSoTimeout(5000);

						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						String message = in.readLine();

						if ("shutdown requested".equalsIgnoreCase(message))
						{


							if (run)
							{
								AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_shutdown_request_detected") + " : " + String.valueOf(port));
								logger.debug("WatchDog shutdown request detected.");

								StartStop.autolab.requestStop();

								run = false;
							}
						}
						else
						{
							System.out.println("Unexpected message: " + message);
						}
					}
					catch (SocketTimeoutException e)
					{
						AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_request_timeout") + " : " + String.valueOf(port));
					}
					catch (IOException e)
					{
						System.out.println("I/O error with " + clientIP + ": " + e.getMessage());
					}
					finally
					{
						try
						{
							clientSocket.close();
						}
						catch (IOException e)
						{
							// Ignore
						}
					}

				}
			}
			catch (IOException e1)
			{

			}

		}

		AutoLab.systemNotify.appendToMessage(JRes.getText("watchdog_stopping"));
		logger.debug("WatchDog Instance Stopped.");
	}

}
