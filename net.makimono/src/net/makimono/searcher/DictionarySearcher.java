package net.makimono.searcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.makimono.model.DictionaryEntry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class DictionarySearcher extends AbstractSearcher {

	private static final int MAX_SIZE = 20;

	public DictionarySearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	public List<DictionaryEntry> search(String queryString) throws IOException {
		if (queryString == null || queryString.equals("")) {
			return Collections.emptyList();
		}
		queryString = queryString.toLowerCase();

		BooleanQuery booleanQuery = new BooleanQuery();
		for (DictionaryFieldName field : DictionaryFieldName.values()) {
			if (!field.isMeaning() || getLanguages().contains(field.getLanguage())) {
				PrefixQuery prefixQuery = new PrefixQuery(new Term(field.name(), queryString));
				if (field.isAnalyzed()) {
					prefixQuery.setBoost(Float.MIN_NORMAL);
				} else {
					TermQuery termQuery = new TermQuery(new Term(field.name(), queryString));
					termQuery.setBoost(Short.MAX_VALUE);
					booleanQuery.add(termQuery, Occur.SHOULD);
				}
				booleanQuery.add(prefixQuery, Occur.SHOULD);
			}
		}

		ArrayList<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();
		IndexSearcher searcher = getIndexSearcher();
		TopDocs topDocs = searcher.search(booleanQuery, MAX_SIZE);
		for (ScoreDoc d : topDocs.scoreDocs) {
			DictionaryEntry entry = getByDocId(d.doc);
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
		}
		return entries;
	}

	public DictionaryEntry getByDocId(int docId) throws IOException {
		Document document = getIndexSearcher().doc(docId);
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(document.getBinaryValue("entry")));
			DictionaryEntry entry = DictionaryEntry.readEntry(inputStream);
			entry.setDocId(docId);
			return entry;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Set<? extends IndexFieldName> getFieldNames() {
		return new HashSet<IndexFieldName>(Arrays.asList(DictionaryFieldName.values()));
	}
}
