package net.makimono.dictionary.searcher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Meaning;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import android.util.Log;

public class ExampleSearcher extends AbstractSearcher<ExampleEntry> {

	private static final String[] ALL_FIELDS = new String[] { ExampleFieldName.SENTENCE_JA.name(), ExampleFieldName.SENTENCE_DE.name(), ExampleFieldName.SENTENCE_EN.name(),
			ExampleFieldName.SENTENCE_ES.name(), ExampleFieldName.SENTENCE_FR.name() };

	public ExampleSearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	@Override
	public List<ExampleEntry> search(String queryString) throws IOException {
		try {
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_35, ALL_FIELDS, new StandardAnalyzer(Version.LUCENE_35));
			Query query = queryParser.parse(queryString);
			return getEntriesForQuery(query);
		} catch (ParseException e) {
			Log.e(ExampleSearcher.class.getSimpleName(), "Failed to parse query string '" + queryString + "'", e);
			return Collections.emptyList();
		}
	}

	@Override
	protected IndexFieldName getIndexFieldName(String fieldName) {
		return ExampleFieldName.valueOf(fieldName);
	}

	@Override
	protected ExampleEntry getEntryForDocument(Document document) throws IOException {
		ExampleEntry entry = new ExampleEntry();
		for (Fieldable f : document.getFields()) {
			ExampleFieldName fieldName = ExampleFieldName.valueOf(f.name());
			if (fieldName.isMeaning()) {
				entry.getMeanings().add(new Meaning(f.stringValue(), fieldName.getLanguage()));
			}
		}
		return entry;
	}

}
