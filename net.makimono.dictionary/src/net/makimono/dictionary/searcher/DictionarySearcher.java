package net.makimono.dictionary.searcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.makimono.dictionary.model.DictionaryEntry;

import org.apache.lucene.document.Document;

public class DictionarySearcher extends AbstractSearcher<DictionaryEntry> {

	public DictionarySearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	@Override
	protected DictionaryEntry getEntryForDocument(Document document) throws IOException {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(document.getBinaryValue("entry")));
			DictionaryEntry entry = DictionaryEntry.readEntry(inputStream);
			return entry;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected IndexFieldName getIndexFieldName(String fieldName) {
		return DictionaryFieldName.valueOf(fieldName);
	}
}
