package net.makimono.dictionary.searcher;

import java.io.File;
import java.io.IOException;

import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Meaning;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

public class ExampleSearcher extends AbstractSearcher<ExampleEntry> {

	public ExampleSearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
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
