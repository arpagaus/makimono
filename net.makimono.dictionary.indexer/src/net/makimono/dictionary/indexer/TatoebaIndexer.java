package net.makimono.dictionary.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.makimono.dictionary.model.Language;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class TatoebaIndexer implements Indexer {

	private Map<String, String> languageMap = new HashMap<String, String>(Language.values().length);;

	private void initCountryCodeMapping() {
		for (Language language : Language.values()) {
			if (language != Language.ru && language != Language.pt) {
				languageMap.put(new Locale(language.name()).getISO3Language(), language.name());
			}
		}
		languageMap.put(new Locale("ja").getISO3Language(), "ja");
	}

	private Map<Integer, Set<Integer>> links = new HashMap<Integer, Set<Integer>>();
	private Set<Integer> okTagged = new HashSet<Integer>();
	private Set<Integer> japaneseSentences = new HashSet<Integer>();

	/**
	 * groupId / (language / sentence)
	 */
	private Map<Integer, Map<Language, String>> sentences = new HashMap<Integer, Map<Language, String>>();

	private IndexWriter indexWriter;

	@SuppressWarnings("serial")
	private Map<String, AtomicInteger> languageCount = new HashMap<String, AtomicInteger>() {
		{
			put(Language.en.name(), new AtomicInteger());
			put(Language.de.name(), new AtomicInteger());
			put(Language.es.name(), new AtomicInteger());
			put(Language.fr.name(), new AtomicInteger());
			put(Language.ja.name(), new AtomicInteger());
		}
	};

	public TatoebaIndexer(Properties properties) {
		initCountryCodeMapping();

		String tagsFile = properties.getProperty("tagsFile");
		if (StringUtils.isNotBlank(tagsFile)) {
			try {
				System.out.println("Parsing " + tagsFile);
				BufferedReader reader = new BufferedReader(new FileReader(tagsFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] columns = line.split("\t");
					if ("OK".equals(columns[1])) {
						okTagged.add(Integer.parseInt(columns[0]));
					}
				}
				reader.close();
				System.out.println("Finished parsing " + tagsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String linksFile = properties.getProperty("linksFile");
		if (StringUtils.isNotBlank(linksFile)) {
			try {
				System.out.println("Parsing " + linksFile);
				BufferedReader reader = new BufferedReader(new FileReader(linksFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] ids = line.split("\t");
					int id1 = Integer.parseInt(ids[0]);
					int id2 = Integer.parseInt(ids[1]);

					Set<Integer> set = links.get(id1);
					if (set == null) {
						set = links.get(id2);
					}
					if (set == null) {
						set = new HashSet<Integer>();
					}
					set.add(id1);
					set.add(id2);
					links.put(id1, set);
					links.put(id2, set);
				}
				reader.close();
				System.out.println("Finished parsing " + linksFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void createIndex(File sourceFile, Directory luceneDirectory) throws Exception {
		System.out.println("Parsing " + sourceFile.getName());

		findJapaneseSentences(sourceFile);
		addSentences(sourceFile);
		writeIndex(luceneDirectory);

		System.out.println("languageCount: " + languageCount);
	}

	private void findJapaneseSentences(File sourceFile) throws IOException {
		new SentecesFileParser() {
			@Override
			protected void handleLine(int id, String language, String sentence) {
				if ("jpn".equals(language)) {
					japaneseSentences.add(id);
				}
			}
		}.run(sourceFile);
	}

	private void addSentences(File sourceFile) throws IOException {
		new SentecesFileParser() {
			@Override
			protected void handleLine(int id, String language, String sentence) throws IOException {
				if (languageMap.containsKey(language) && containsAny(japaneseSentences, links.get(id))) {
					String iso2Language = languageMap.get(language);

					int groupId = Collections.min(links.get(id));
					addSentence(groupId, iso2Language, sentence);
				}

			}
		}.run(sourceFile);
	}

	private static boolean containsAny(Set<Integer> primarySet, Set<Integer> secondarySet) {
		if (secondarySet == null) {
			return false;
		}
		for (Integer i : secondarySet) {
			if (primarySet.contains(i)) {
				return true;
			}
		}
		return false;
	}

	private void addSentence(int groupId, String language, String sentence) throws IOException {
		languageCount.get(language).incrementAndGet();

		Map<Language, String> map = sentences.get(groupId);
		if (map == null) {
			map = new HashMap<Language, String>();
			sentences.put(groupId, map);
		}
		map.put(Language.valueOf(language), sentence);
	}

	private void writeIndex(Directory luceneDirectory) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new JapaneseAnalyzer(Version.LUCENE_35));
		indexWriter = new IndexWriter(luceneDirectory, config);

		for (Map<Language, String> map : sentences.values()) {
			if (!map.containsKey(Language.ja)) {
				continue;
			}

			Document document = new Document();
			for (Entry<Language, String> entry : map.entrySet()) {
				document.add(new Field("SENTENCE_" + entry.getKey().name().toUpperCase(), entry.getValue(), Store.YES, Index.ANALYZED));
			}
			indexWriter.addDocument(document);
		}

		indexWriter.forceMerge(1);
		indexWriter.commit();
		indexWriter.close();
		System.out.println("Index closed");
	}

	private static abstract class SentecesFileParser {

		void run(File sourceFile) throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split("\t");
				int id = Integer.parseInt(columns[0]);
				String language = columns[1];
				String sentence = columns[2];

				handleLine(id, language, sentence);
			}
			reader.close();
		}

		abstract void handleLine(int id, String language, String sentence) throws IOException;
	}

}
