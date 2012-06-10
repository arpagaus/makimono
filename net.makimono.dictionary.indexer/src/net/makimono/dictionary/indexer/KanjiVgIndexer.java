package net.makimono.dictionary.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KanjiVgIndexer {

	public Map<Integer, String> getStrokePaths(File file) throws Exception {
		Map<Integer, String> strokePaths = new HashMap<Integer, String>();

		System.out.println("Parsing " + file.getName());

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new GZIPInputStream(new FileInputStream(file)));
		NodeList kanjiNodeList = document.getElementsByTagName("kanji");

		for (int i = 0; i < kanjiNodeList.getLength(); i++) {
			Element kanjiElement = (Element) kanjiNodeList.item(i);
			Element gItem = (Element) kanjiElement.getElementsByTagName("g").item(0);
			NodeList graphNodeList = gItem.getElementsByTagName("path");

			StringBuilder paths = new StringBuilder();
			for (int j = 0; j < graphNodeList.getLength(); j++) {
				Element graphElement = (Element) graphNodeList.item(j);
				paths.append(graphElement.getAttribute("d"));
				paths.append('\n');
			}

			String codePointString = kanjiElement.getAttribute("id").replace("kvg:kanji_", "");
			int codePoint = Integer.parseInt(codePointString, 16);

			strokePaths.put(codePoint, paths.toString());
		}

		System.out.println("Finished parsing paths for " + strokePaths.size() + " kanjis");

		return strokePaths;
	}
}
