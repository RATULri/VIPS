package org.fit.vips;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.Viewport;
import org.w3c.dom.Document;

public class Vips {
	private URL _url = null;
	private DOMAnalyzer _domAnalyzer = null;
	private BrowserCanvas _browserCanvas = null;
	private Viewport _viewport = null;

	private boolean _graphicsOutput = false;
	private boolean _outputToFolder = false;
	private boolean _outputEscaping = true;
	private int _pDoC = 11;
	private String _filename = "";
	private	int sizeThresholdWidth = 350;
	private	int sizeThresholdHeight = 400;

	private PrintStream originalOut = null;
	long startTime = 0;
	long endTime = 0;

	public Vips()
	{
	}

	public void enableGraphicsOutput(boolean enable)
	{
		_graphicsOutput = enable;
	}

	public void enableOutputToFolder(boolean enable)
	{
		_outputToFolder = enable;
	}

	public void setPredefinedDoC(int value)
	{
		if (value <= 0 || value > 11)
		{
			System.err.println("pDoC value must be between 1 and 11! Not " + value + "!");
			return;
		}
		else
		{
			_pDoC = value;
		}
	}

	public void setUrl(String url)
	{
		try
		{
			if (url.startsWith("http://") || url.startsWith("https://"))
				_url = new URL(url);
			else
				_url = new URL("http://" + url);
		}
		catch (Exception e)
		{
			System.err.println("Invalid address: " + url);
		}
	}

	private void getDomTree(URL urlStream)
	{
		DocumentSource docSource = null;
		try
		{
			docSource = new DefaultDocumentSource(urlStream);
			DOMSource parser = new DefaultDOMSource(docSource);

			Document domTree = parser.parse();
			_domAnalyzer = new DOMAnalyzer(domTree, _url);
			_domAnalyzer.attributesToStyles();
			_domAnalyzer.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
			_domAnalyzer.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
			_domAnalyzer.getStyleSheets();
		}
		catch (Exception e)
		{
			System.err.print(e.getMessage());
		}
	}

	private void getViewport()
	{
		_browserCanvas = new BrowserCanvas(_domAnalyzer.getRoot(),
				_domAnalyzer, new java.awt.Dimension(1000, 600), _url);
		_viewport = _browserCanvas.getViewport();
	}

	private void exportPageToImage()
	{
		try
		{
			BufferedImage page = _browserCanvas.getImage();
			String filename = System.getProperty("user.dir") + "/page.png";
			ImageIO.write(page, "png", new File(filename));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String generateFolderName()
	{
		String outputFolder = "";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		outputFolder += sdf.format(cal.getTime());
		outputFolder += "_";
		outputFolder += _url.getHost().replaceAll("\\.", "_").replaceAll("/", "_");

		return outputFolder;
	}

	private void performSegmentation() throws IOException {
		startTime = System.nanoTime();
		int numberOfIterations = 10;
		int pageWidth = _viewport.getWidth();
		int pageHeight = _viewport.getHeight();

		if (_graphicsOutput)
			exportPageToImage();

		VipsSeparatorGraphicsDetector detector;
		VipsParser vipsParser = new VipsParser(_viewport);
		VisualStructureConstructor constructor = new VisualStructureConstructor(_pDoC);
		constructor.setGraphicsOutput(_graphicsOutput);

		for (int iterationNumber = 1; iterationNumber < numberOfIterations+1; iterationNumber++)
		{
			detector = new VipsSeparatorGraphicsDetector(pageWidth, pageHeight);

			vipsParser.setSizeThresholdHeight(sizeThresholdHeight);
			vipsParser.setSizeThresholdWidth(sizeThresholdWidth);

			vipsParser.parse();

			VipsBlock vipsBlocks = vipsParser.getVipsBlocks();

			if (iterationNumber == 1)
			{
				if (_graphicsOutput)
				{
					detector.setVipsBlock(vipsBlocks);
					detector.fillPool();
					detector.saveToImage("blocks" + iterationNumber);
					detector.setCleanUpSeparators(0);
					detector.detectHorizontalSeparators();
					detector.detectVerticalSeparators();
					detector.exportHorizontalSeparatorsToImage();
					detector.exportVerticalSeparatorsToImage();
					detector.exportAllToImage();
				}

				constructor.setVipsBlocks(vipsBlocks);
				constructor.setPageSize(pageWidth, pageHeight);
			}
			else
			{
				vipsBlocks = vipsParser.getVipsBlocks();
				constructor.updateVipsBlocks(vipsBlocks);

				if (_graphicsOutput)
				{
					detector.setVisualBlocks(constructor.getVisualBlocks());
					detector.fillPool();
					detector.saveToImage("blocks" + iterationNumber);
				}
			}

			constructor.constructVisualStructure();

			if (iterationNumber <= 5 )
			{
				sizeThresholdHeight -= 50;
				sizeThresholdWidth -= 50;

			}
			if (iterationNumber == 6)
			{
				sizeThresholdHeight = 100;
				sizeThresholdWidth = 100;
			}
			if (iterationNumber == 7)
			{
				sizeThresholdHeight = 80;
				sizeThresholdWidth = 80;
			}
			if (iterationNumber == 8)
			{
				sizeThresholdHeight = 40;
				sizeThresholdWidth = 10;
			}
			if (iterationNumber == 9)
			{
				sizeThresholdHeight = 1;
				sizeThresholdWidth = 1;
			}

		}

		constructor.normalizeSeparatorsMinMax();

		VipsOutput vipsOutput = new VipsOutput(_pDoC);
		vipsOutput.setEscapeOutput(_outputEscaping);
		vipsOutput.setOutputFileName(_filename);
		vipsOutput.writeXML(constructor.getVisualStructure(), _viewport);

		endTime = System.nanoTime();

		long diff = endTime - startTime;

		System.out.println("Execution time of VIPS: " + diff + " ns; " +
				(diff / 1000000.0) + " ms; " +
				(diff / 1000000000.0) + " sec");
	}

	public void startSegmentation(String url)
	{
		setUrl(url);

		startSegmentation();
	}

	public void startSegmentation()
	{
		try
		{
			_url.openConnection();

			getDomTree(_url);
			startTime = System.nanoTime();
			getViewport();

			String outputFolder = "";
			String oldWorkingDirectory = "";
			String newWorkingDirectory = "";

			if (_outputToFolder)
			{
				outputFolder = generateFolderName();

				if (!new File(outputFolder).mkdir())
				{
					System.err.println("Something goes wrong during directory creation!");
				}
				else
				{
					oldWorkingDirectory = System.getProperty("user.dir");
					newWorkingDirectory += oldWorkingDirectory + "/" + outputFolder + "/";
					System.setProperty("user.dir", newWorkingDirectory);
				}
			}

			performSegmentation();

			if (_outputToFolder)
				System.setProperty("user.dir", oldWorkingDirectory);
		}
		catch (Exception e)
		{
			System.err.println("Something's wrong!");
			e.printStackTrace();
		}
	}
}
