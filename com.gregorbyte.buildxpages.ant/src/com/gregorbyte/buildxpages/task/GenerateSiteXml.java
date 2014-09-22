package com.gregorbyte.buildxpages.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerateSiteXml  {

	private String eclipseFolder = null;
	private List<String> features = new ArrayList<String>();

	public GenerateSiteXml(String eclipseFolder) {
		this.eclipseFolder = eclipseFolder;
	}

	public void execute() throws FileNotFoundException, TransformerException,
			ParserConfigurationException {

		File eclipseDirFile = new File(this.eclipseFolder);
		if (!eclipseDirFile.exists()) {
			throw new FileNotFoundException(
					"Can't find Eclipse Directory, does not exists");
		}
		if (!eclipseDirFile.isDirectory()) {
			throw new IllegalArgumentException(
					"Supplied argument must be a directory");
		}
		String featuresDir = this.eclipseFolder + File.separator + "features";

		File featuresDirFile = new File(featuresDir);
		if (!featuresDirFile.exists()) {
			throw new FileNotFoundException("features sub directory not found");
		}
		if (!featuresDirFile.isDirectory()) {
			throw new IllegalArgumentException(
					"Directory must have sub-directory called features");
		}
		String[] featureJars = featuresDirFile.list();
		for (String subfile : featureJars) {
			if (subfile.endsWith(".jar")) {
				this.features.add(subfile);
			}
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("site");
		doc.appendChild(rootElement);
		for (String featureName : this.features) {
			if (featureName.endsWith(".jar") && featureName.contains("_")) {
				String[] bits = featureName.split("_");

				String id = bits[0];
				String version = bits[1].substring(0, bits[1].lastIndexOf('.'));
				String url = "features/" + featureName;

				Element feature = doc.createElement("feature");
				rootElement.appendChild(feature);

				feature.setAttribute("url", url);
				feature.setAttribute("id", id);
				feature.setAttribute("version", version);
			}
		}
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(eclipseDirFile,
				"site.xml"));

		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		transformer.transform(source, result);

		System.out.println("File saved!");
	}

	public static void main(String[] args) throws FileNotFoundException,
			TransformerException, ParserConfigurationException {
		if (args.length != 1) {
			System.out.println("Wrong number of arguments");
			System.out
					.println("expects update site root directory containing features directory");
		}
		String updateSiteFolder = args[0];

		System.out.println("Running on " + updateSiteFolder);

		GenerateSiteXml gen = new GenerateSiteXml(updateSiteFolder);
		gen.execute();
	}
}
