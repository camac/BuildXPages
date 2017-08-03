package com.gregorbyte.buildxpages.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerateWidgetXml {

	private String eclipseFolder = null;
	private File eclipseDirFile = null;

	private List<FeatureDetails> features = new ArrayList<FeatureDetails>();

	private String updateSiteServer = null;
	private String updateSiteFilepath = null;
	private String updateSiteUrl = null;

	private String widgetId = null;
	private String widgetTitle = null;

	private String featureDescription = null;
	private String featureName = null;
	private String featureId = null;

	public static final String PROVIDER_ID = "com.ibm.rcp.toolbox.prov.provider.ToolboxProvisioning";

	public static final String ELEMENT_INSTALL = "install";
	public static final String ELEMENT_INSTALLFEATURE = "installFeature";
	public static final String ELEMENT_REQUIREMENTS = "requirements";
	public static final String ELEMENT_FEATURE = "feature";
	public static final String ELEMENT_INSTALLMANIFEST = "installManifest";

	public static final String ELEMENT_WCC = "webcontextConfiguration";
	public static final String ELEMENT_PALLETEITEM = "palleteItem";
	public static final String ELEMENT_PREFERENCES = "preferences";
	public static final String ELEMENT_DATA = "data";

	public static final String ATTR_DESCRIPTION = "description";
	public static final String ATTR_ID = "id";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_MATCH = "match";
	public static final String ATTR_SHARED = "shared";
	public static final String ATTR_VERSION = "version";

	public static final String ATTR_HIDETHUMB = "hideThumbnail";
	public static final String ATTR_IMAGEURL = "imageUrl";
	public static final String ATTR_PROVID = "providerId";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_URL = "url";

	public static final String MATCH_PERFECT = "perfect"; // Must match Exactly.
															// e.g for version
															// 1.0.0, only 1.0.0
															// will match
	public static final String MATCH_EQUIVALENT = "equivalent"; // 1.0.0 will
																// also match
																// 1.0.1, 1.0.2
																// etc but not
																// 1.1.0
	public static final String MATCH_COMPATIBLE = "compatible"; // 1.0.0 will
																// also match
																// 1.0.1, 1.1.0,
																// but not 2.0.0
	public static final String MATCH_GREATEROREQUAL = "greaterOrEqual"; // 1.0.0
																		// will
																		// also
																		// match
																		// 1.0.1,
																		// 1.1.0
																		// and
																		// 2.0.0
																		// and
																		// so on

	public static final String ACTION_INSTALL = "install";
	public static final String ACTION_INSTALLONLY = "installonly";
	public static final String ACTION_ENABLE = "enable";
	public static final String ACTION_DISABLE = "disable";
	public static final String ACTION_UNINSTALL = "uninstall";
	public static final String ACTION_IGNORE = "ignore";

	public GenerateWidgetXml(String eclipseFolder) {
		this.eclipseFolder = eclipseFolder;
	}

	private File getEclipseDirFile() {

		if (this.eclipseDirFile == null) {
			this.eclipseDirFile = new File(this.eclipseFolder);
		}

		return this.eclipseDirFile;

	}

	public void execute() throws FileNotFoundException, TransformerException,
			ParserConfigurationException {

		File eclipseDirFile = getEclipseDirFile();
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
				// this.features.add(subfile);
			}
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("site");
		doc.appendChild(rootElement);
		// for (String featureName : this.features) {
		// if (featureName.endsWith(".jar") && featureName.contains("_")) {
		// String[] bits = featureName.split("_");
		//
		// String id = bits[0];
		// String version = bits[1].substring(0, bits[1].lastIndexOf('.'));
		// String url = "features/" + featureName;
		//
		// Element feature = doc.createElement("feature");
		// rootElement.appendChild(feature);
		//
		// feature.setAttribute("url", url);
		// feature.setAttribute("id", id);
		// feature.setAttribute("version", version);
		// }
		// }
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

	private void gatherFeatures() throws FileNotFoundException {

		File eclipseDirFile = getEclipseDirFile();
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

			if (subfile.endsWith(".jar") && subfile.contains("_")) {

				String[] bits = subfile.split("_");

				String id = bits[0];
				String versionFull = bits[1].substring(0,
						bits[1].lastIndexOf('.'));
				String version = versionFull.substring(0,
						versionFull.lastIndexOf('.'));

				FeatureDetails fd = new FeatureDetails();
				fd.id = id;
				fd.version = version;
				fd.match = MATCH_GREATEROREQUAL;

				features.add(fd);

			}
		}

	}

	public void generate() throws ParserConfigurationException,
			FileNotFoundException, TransformerException {

		Document doc = createWidgetDocument();

		doc.setXmlStandalone(false);

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		File eclipseDirFile = getEclipseDirFile();
		StreamResult result = new StreamResult(new File(eclipseDirFile,
				"widget.xml"));

		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");

		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		transformer.transform(source, result);

	}

	private Document createWidgetDocument()
			throws ParserConfigurationException, TransformerException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(ELEMENT_WCC);
		doc.appendChild(rootElement);

		rootElement.setAttribute(ATTR_VERSION, "1.1");

		// Create PalleteItem element
		Element piElement = doc.createElement(ELEMENT_PALLETEITEM);
		rootElement.appendChild(piElement);

		piElement.setAttribute(ATTR_HIDETHUMB, "false");
		piElement.setAttribute(ATTR_ID, widgetId);
		piElement.setAttribute(ATTR_IMAGEURL, "");
		piElement.setAttribute(ATTR_PROVID, PROVIDER_ID);
		piElement.setAttribute(ATTR_TITLE, widgetTitle);
		piElement.setAttribute(ATTR_URL, updateSiteUrl);

		// Create Pref Elemnet
		Element prefElement = doc.createElement(ELEMENT_PREFERENCES);
		piElement.appendChild(prefElement);

		// Create Data Element
		Element dataElement = doc.createElement(ELEMENT_DATA);
		piElement.appendChild(dataElement);

		// Create InstallManifest Element
		Element installManifestElement = doc
				.createElement(ELEMENT_INSTALLMANIFEST);
		dataElement.appendChild(installManifestElement);

		String installManifest = getInstallManifest();
		CDATASection cdata = doc.createCDATASection(installManifest);

		installManifestElement.appendChild(cdata);

		return doc;

	}

	private String getInstallManifest() throws TransformerException,
			ParserConfigurationException {

		Document doc = createInstallManifest();

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		String output = "\n" + writer.getBuffer().toString() + "\n";
		return output.replaceAll("\r", "");

	}

	private Document createInstallManifest()
			throws ParserConfigurationException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// Create Root Element
		Element rootElement = doc.createElement(ELEMENT_INSTALL);
		doc.appendChild(rootElement);

		// Create Install Feature Element
		Element installFeatureElement = doc
				.createElement(ELEMENT_INSTALLFEATURE);
		rootElement.appendChild(installFeatureElement);

		installFeatureElement
				.setAttribute(ATTR_DESCRIPTION, featureDescription);
		installFeatureElement.setAttribute(ATTR_NAME, featureName);
		installFeatureElement.setAttribute(ATTR_ID, featureId);

		// Create Requirements Element
		Element reqElement = doc.createElement(ELEMENT_REQUIREMENTS);
		installFeatureElement.appendChild(reqElement);

		for (FeatureDetails featureDetails : features) {

			Element featElement = doc.createElement(ELEMENT_FEATURE);
			reqElement.appendChild(featElement);

			featElement.setAttribute(ATTR_ID, featureDetails.id);
			featElement.setAttribute(ATTR_MATCH, featureDetails.match);
			if (featureDetails.shared) {
				featElement.setAttribute(ATTR_SHARED, "true");
			} else {
				featElement.setAttribute(ATTR_SHARED, "false");
			}
			featElement.setAttribute(ATTR_VERSION, featureDetails.version);

		}

		return doc;

	}

	private class FeatureDetails {

		private String id;
		private String match;
		private boolean shared = true;
		private String version;

	}

	public static void main(String[] args) throws FileNotFoundException,
			TransformerException, ParserConfigurationException {

		String updateSiteFolder = "C:\\Users\\cgregor\\Desktop\\JobHubNotesPlugins";

		GenerateWidgetXml gen = new GenerateWidgetXml(updateSiteFolder);

		gen.featureDescription = "Job Hub Sidebar";
		gen.featureName = "Job hub Sidebar";
		gen.featureId = "Jord_JobHubSidebar";

		gen.updateSiteUrl = "nrpc://ausdata.jord.com.au/__ca257d240010baad/site.xml";

		gen.widgetId = "Jord_JobHubSidebar";
		gen.widgetTitle = "Job Hub Sidebar";

		gen.gatherFeatures();
		gen.generate();

		// gen.debugInstallManifest();

	}
}
