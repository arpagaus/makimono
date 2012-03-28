package net.makimono.searcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.makimono.model.DictionaryEntry;

import org.apache.lucene.document.Document;

public class DictionarySearcher extends AbstractSearcher<DictionaryEntry> {

	public DictionarySearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	@Override
	protected DictionaryEntry getEntryByDocId(int docId) throws IOException {
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
	protected IndexFieldName getIndexFieldName(String fieldName) {
		return DictionaryFieldName.valueOf(fieldName);
	}
}
