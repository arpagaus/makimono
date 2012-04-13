package net.makimono.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.makimono.searcher.KanjiFieldName;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KanjiVgIndexer {

	private IndexSearcher indexSearcher;
	private IndexWriter indexWriter;

	private int totalCount;

	public void enhanceIndex(File file, Directory luceneDirectory) throws Exception {
		System.out.println("Enhancing kanji index with SVG paths");

		indexWriter = new IndexWriter(luceneDirectory, new IndexWriterConfig(Version.LUCENE_35, new SimpleAnalyzer(Version.LUCENE_35)));
		indexSearcher = new IndexSearcher(IndexReader.open(luceneDirectory));

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
			String literal = String.valueOf(Character.toChars(codePoint));

			enhanceIndexDocument(literal, paths.toString());
		}

		indexWriter.forceMerge(1);
		indexWriter.commit();
		indexWriter.close();

		System.out.println("Added SVG paths to " + totalCount + " kanjis");
	}

	private void enhanceIndexDocument(String literal, String paths) throws IOException {
		TermQuery query = new TermQuery(new Term(KanjiFieldName.LITERAL.name(), literal));
		TopDocs topDocs = indexSearcher.search(query, 1);
		if (topDocs.totalHits > 0) {
			org.apache.lucene.document.Document document = indexSearcher.doc(topDocs.scoreDocs[0].doc);
			indexWriter.deleteDocuments(query);

			byte[] binary = CompressionTools.compress(paths.getBytes("UTF-8"));
			document.add(new Field(KanjiFieldName.STROKE_PATHS.name(), binary));
			indexWriter.addDocument(document);

			totalCount++;
		}
	}
}
