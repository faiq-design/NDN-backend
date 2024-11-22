package com.marksman.census.service;

import javax.servlet.http.HttpServletResponse;

public interface FileDownloadService
{

	public void downloadImage(String imagePath, HttpServletResponse response);

	public void downloadDssBuild(HttpServletResponse response);
	
	public void downloadAsmBuild(HttpServletResponse response);

	public void downloadSisBuild(HttpServletResponse response);

	public void downloadTmBuild(HttpServletResponse response);
	
	

}
