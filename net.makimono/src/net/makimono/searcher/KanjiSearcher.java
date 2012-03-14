package net.makimono.searcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class KanjiSearcher implements Closeable, Searcher {

	private Directory dictionaryDirectory;
	private IndexSearcher indexSearcher;

	public KanjiSearcher(File dictionaryPath) throws IOException {
		this.dictionaryDirectory = new SimpleFSDirectory(dictionaryPath);
	}

	private IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			indexSearcher = new IndexSearcher(IndexReader.open(dictionaryDirectory, true));
		}
		return indexSearcher;
	}

	@Override
	public List<KanjiEntry> search(String queryString) throws IOException {
		if (queryString == null || queryString.equals("")) {
			return Collections.emptyList();
		}
		queryString = queryString.toLowerCase();

		BooleanQuery query = new BooleanQuery();

		for (KanjiDictionaryFields field : KanjiDictionaryFields.values()) {
			PhraseQuery phraseQuery = new PhraseQuery();
			phraseQuery.add(new Term(field.name(), queryString));
			query.add(phraseQuery, Occur.SHOULD);
		}

		ArrayList<KanjiEntry> entries = new ArrayList<KanjiEntry>();
		IndexSearcher searcher = getIndexSearcher();
		TopDocs topDocs = searcher.search(query, 100);
		for (ScoreDoc d : topDocs.scoreDocs) {
			KanjiEntry entry = getKanjiEntryForDocument(getIndexSearcher().doc(d.doc));
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
		}
		return entries;
	}

	public List<KanjiEntry> getKanjiEntries(String string) throws IOException {
		if (string == null || string.equals("")) {
			return new ArrayList<KanjiEntry>(0);
		}

		ArrayList<KanjiEntry> entries = new ArrayList<KanjiEntry>();
		for (int i = 0; i < string.length(); i++) {
			int codePoint = string.codePointAt(i);
			UnicodeBlock block = UnicodeBlock.of(codePoint);
			if (block.equals(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
				String literal = String.valueOf(Character.toChars(codePoint));
				entries.add(getKanjiEntry(literal));
			}
		}
		return entries;
	}

	public KanjiEntry getKanjiEntry(String literal) throws IOException {
		TermQuery query = new TermQuery(new Term(KanjiDictionaryFields.LITERAL.name(), literal));

		TopDocs topDocs = getIndexSearcher().search(query, 1);
		if (topDocs.totalHits > 0) {
			return getKanjiEntryForDocument(getIndexSearcher().doc(topDocs.scoreDocs[0].doc));
		}
		return null;
	}

	KanjiEntry getKanjiEntryForDocument(Document document) {
		KanjiEntry entry = new KanjiEntry();
		entry.setLiteral(document.getFieldable(KanjiDictionaryFields.LITERAL.name()).stringValue());
		entry.setCodePoint(ByteBuffer.wrap(document.getFieldable(KanjiDictionaryFields.CODE_POINT.name()).getBinaryValue()).getInt());

		Fieldable fieldable = document.getFieldable(KanjiDictionaryFields.GRADE.name());
		if (fieldable != null) {
			entry.setGrade(fieldable.getBinaryValue()[0]);
		}
		fieldable = document.getFieldable(KanjiDictionaryFields.FREQUENCY.name());
		if (fieldable != null) {
			entry.setFrequency(ByteBuffer.wrap(fieldable.getBinaryValue()).getShort());
		}
		fieldable = document.getFieldable(KanjiDictionaryFields.JLPT.name());
		if (fieldable != null) {
			entry.setJlpt(fieldable.getBinaryValue()[0]);
		}

		entry.setRadical(ByteBuffer.wrap(document.getFieldable(KanjiDictionaryFields.RADICAL.name()).getBinaryValue()).getShort());
		entry.setStrokeCount(document.getFieldable(KanjiDictionaryFields.STROKE_COUNT.name()).getBinaryValue()[0]);

		entry.getOnYomi().addAll(getStringsForField(document, KanjiDictionaryFields.ONYOMI));
		entry.getKunYomi().addAll(getStringsForField(document, KanjiDictionaryFields.KUNYOMI));
		entry.getNanori().addAll(getStringsForField(document, KanjiDictionaryFields.NANORI));
		entry.getPinyin().addAll(getStringsForField(document, KanjiDictionaryFields.PINYIN));
		entry.getHangul().addAll(getStringsForField(document, KanjiDictionaryFields.HANGUL));

		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiDictionaryFields.MEANING_EN.name()), Language.en));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiDictionaryFields.MEANING_FR.name()), Language.fr));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiDictionaryFields.MEANING_ES.name()), Language.es));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiDictionaryFields.MEANING_PT.name()), Language.pt));

		return entry;
	}

	private ArrayList<String> getStringsForField(Document document, KanjiDictionaryFields field) {
		ArrayList<String> strings = new ArrayList<String>();
		for (Fieldable f : document.getFieldables(field.name())) {
			strings.add(f.stringValue());
		}
		return strings;
	}

	private List<? extends Meaning> getMeanings(Fieldable[] fieldables, Language lang) {
		ArrayList<Meaning> meanings = new ArrayList<Meaning>();
		for (Fieldable f : fieldables) {
			meanings.add(new Meaning(f.stringValue(), lang));
		}
		return meanings;
	}

	public void close() throws IOException {
		IOException exception = null;
		if (indexSearcher != null) {
			try {
				indexSearcher.close();
			} catch (IOException e) {
				exception = e;
			}
			try {
				indexSearcher.getIndexReader().close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			} finally {
				indexSearcher = null;
			}
		}
		if (dictionaryDirectory != null) {
			try {
				dictionaryDirectory.close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

}