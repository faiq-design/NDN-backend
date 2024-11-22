package com.marksman.census.logging;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.service.SyncServiceImpl;
import com.marksman.census.util.FileUtils;

public class SyncLogging
{
	@Autowired
	ServletContext servletContext;
	protected static Logger logger = Logger.getLogger(SyncServiceImpl.class);

	public void saveLog(final int surveyorId, final int shopId, final String date, final String data)
	{

		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{

				String filePath = getServersContentUrl(date, surveyorId) + shopId + "_"
						+ System.currentTimeMillis() + ".txt";
				String temp = data + "\n";
				try
				{
					FileUtils.writeFileString(temp, filePath);

				}
				catch (IOException e)
				{
					logger.error(e, e);
				}
				catch (Exception e)
				{
					logger.error(e, e);
				}

			}
		});
		thread.start();
	}

	public void saveLog(final int surveyorId, final int shopId, final String date,
			final String data, final String logType)
	{

		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{

				String filePath = getServersContentUrl(date, surveyorId, logType) + shopId + "_"
						+ System.currentTimeMillis() + ".txt";
				String temp = data + "\n";
				try
				{
					FileUtils.writeFileString(temp, filePath);

				}
				catch (IOException e)
				{
					logger.error(e, e);
				}
				catch (Exception e)
				{
					logger.error(e, e);
				}

			}
		});
		thread.start();
	}

	// save log filese for dsr app
	public void saveDsrLog(final int surveyorId, final int shopId, final String date,
			final String data)
	{

		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{

				String filePath = getServersContentUrlForDsr(date, surveyorId) + shopId + "_"
						+ System.currentTimeMillis() + ".txt";
				String temp = data + "\n";
				try
				{
					FileUtils.writeFileString(temp, filePath);

				}
				catch (IOException e)
				{
					logger.error(e, e);
				}
				catch (Exception e)
				{
					logger.error(e, e);
				}

			}
		});
		thread.start();
	}

	private String getServersContentUrl(String date, int surveyorId)
	{

		String path = servletContext.getRealPath("/") + File.separator + "survey_logs"
				+ File.separator + date + File.separator + surveyorId + File.separator;
		if (!FileUtils.isExistsFolder(path))
		{
			FileUtils.createFolders(path);
		}
		return path;
	}

	private String getServersContentUrl(String date, int surveyorId, String logType)
	{

		String path = servletContext.getRealPath("/") + File.separator + logType + File.separator
				+ date + File.separator + surveyorId + File.separator;
		if (!FileUtils.isExistsFolder(path))
		{
			FileUtils.createFolders(path);
		}
		return path;
	}

	// save log filese for dsr app
	private String getServersContentUrlForDsr(String date, int surveyorId)
	{

		String path = servletContext.getRealPath("/") + File.separator + "survey_logs_dsr"
				+ File.separator + date + File.separator + surveyorId + File.separator;
		if (!FileUtils.isExistsFolder(path))
		{
			FileUtils.createFolders(path);
		}
		return path;
	}

	public void saveLogEndActivity(final int tmId, final int ActivityId, final String date,
			final String data)
	{

		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{

				String filePath = getServersContentUrlForEndActivity(tmId, date, ActivityId)
						+ " TM Id:" + tmId + "_" + ActivityId + "_" + System.currentTimeMillis()
						+ ".txt";
				String temp = data + "\n";
				try
				{
					FileUtils.writeFileString(temp, filePath);

				}
				catch (IOException e)
				{
					logger.error(e, e);
				}
				catch (Exception e)
				{
					logger.error(e, e);
				}

			}
		});
		thread.start();
	}

	private String getServersContentUrlForEndActivity(int tmId, String date, int ActivityId)
	{

		String path = servletContext.getRealPath("/") + File.separator
				+ "activity_feedbackform_json_logs" + File.separator + date + File.separator + tmId
				+ File.separator;
		if (!FileUtils.isExistsFolder(path))
		{
			FileUtils.createFolders(path);
		}
		return path;
	}

}
