package net.makimono.searcher;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class KanjiSearcher extends AbstractSearcher<KanjiEntry> {

	public KanjiSearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	@Override
	public List<KanjiEntry> search(String queryString) throws IOException {
		List<KanjiEntry> kanjiEntries = getKanjiEntries(queryString);
		if (!kanjiEntries.isEmpty()) {
			return kanjiEntries;
		}
		return super.search(queryString);
	}

	public List<KanjiEntry> getKanjiEntries(String string) throws IOException {
		if (string == null || string.equals("")) {
			return Collections.emptyList();
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
		TermQuery query = new TermQuery(new Term(KanjiFieldName.LITERAL.name(), literal));

		TopDocs topDocs = getIndexSearcher().search(query, 1);
		if (topDocs.totalHits > 0) {
			return getKanjiEntryForDocument(getIndexSearcher().doc(topDocs.scoreDocs[0].doc));
		}
		return null;
	}

	@Override
	protected KanjiEntry getEntryByDocId(int doc) throws IOException {
		return getKanjiEntryForDocument(getIndexSearcher().doc(doc));
	}

	KanjiEntry getKanjiEntryForDocument(Document document) {
		KanjiEntry entry = new KanjiEntry();
		entry.setLiteral(document.getFieldable(KanjiFieldName.LITERAL.name()).stringValue());
		entry.setCodePoint(ByteBuffer.wrap(document.getFieldable(KanjiFieldName.CODE_POINT.name()).getBinaryValue()).getInt());

		Fieldable fieldable = document.getFieldable(KanjiFieldName.GRADE.name());
		if (fieldable != null) {
			entry.setGrade(fieldable.getBinaryValue()[0]);
		}
		fieldable = document.getFieldable(KanjiFieldName.FREQUENCY.name());
		if (fieldable != null) {
			entry.setFrequency(ByteBuffer.wrap(fieldable.getBinaryValue()).getShort());
		}
		fieldable = document.getFieldable(KanjiFieldName.JLPT.name());
		if (fieldable != null) {
			entry.setJlpt(fieldable.getBinaryValue()[0]);
		}

		entry.setRadical(ByteBuffer.wrap(document.getFieldable(KanjiFieldName.RADICAL.name()).getBinaryValue()).getShort());
		entry.setStrokeCount(document.getFieldable(KanjiFieldName.STROKE_COUNT.name()).getBinaryValue()[0]);

		entry.getOnYomi().addAll(getStringsForField(document, KanjiFieldName.ONYOMI));
		entry.getKunYomi().addAll(getStringsForField(document, KanjiFieldName.KUNYOMI));
		entry.getNanori().addAll(getStringsForField(document, KanjiFieldName.NANORI));
		entry.getPinyin().addAll(getStringsForField(document, KanjiFieldName.PINYIN));
		entry.getHangul().addAll(getStringsForField(document, KanjiFieldName.HANGUL));

		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_EN.name()), Language.en));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_FR.name()), Language.fr));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_ES.name()), Language.es));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_PT.name()), Language.pt));

		return entry;
	}

	private ArrayList<String> getStringsForField(Document document, KanjiFieldName field) {
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

	@Override
	protected IndexFieldName getIndexFieldName(String fieldName) {
		return KanjiFieldName.valueOf(fieldName);
	}
}
