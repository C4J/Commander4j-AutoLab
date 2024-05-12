package com.commander4j.autolab;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.commander4j.utils.JWait;

public class ShutdownHook extends Thread
{
	JWait wait = new JWait();
	public static Logger logger = org.apache.logging.log4j.LogManager.getLogger((ShutdownHook.class));

	@Override
	public void run()
	{

		StartStop.autolab.requestStop();
		StartStop.autolab.interrupt();

		while (StartStop.autolab.isAlive())
		{

			wait.oneSec();
		}

		File running = new File(System.getProperty("user.dir") + File.separator + "running" + File.separator + "AutoLab.isRunning");
		
		logger.debug("Removing run flag : "+running.getAbsoluteFile());

		try
		{
			FileUtils.forceDelete(running);
		}
		catch (IOException e)
		{

		}
		running=null;
		
		File shutdown = new File(System.getProperty("user.dir") + File.separator + "running" + File.separator + "AutoLab.shutdown");
		
		try
		{
			FileUtils.forceDelete(shutdown);
			logger.debug("Removing shutdown flag : "+shutdown.getAbsoluteFile());
		}
		catch (IOException e)
		{

		}
		shutdown=null;
		

		LogManager.shutdown();

	}

}
