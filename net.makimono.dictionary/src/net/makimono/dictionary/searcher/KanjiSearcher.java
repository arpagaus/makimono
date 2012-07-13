package net.makimono.dictionary.searcher;

import java.io.File;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;

import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;

import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class KanjiSearcher extends AbstractSearcher<KanjiEntry> {

	public KanjiSearcher(File dictionaryPath) throws IOException {
		super(dictionaryPath);
	}

	public List<KanjiEntry> searchByRadicals(Collection<String> radicals, Integer minStrokes, Integer maxStrokes) throws IOException {
		BooleanQuery query = new BooleanQuery();
		for (String radical : radicals) {
			query.add(new TermQuery(new Term(KanjiFieldName.RADICAL.name(), radical)), Occur.MUST);
		}

		if (minStrokes != null || maxStrokes != null) {
			NumericRangeQuery<Integer> strokeRangeQuery = NumericRangeQuery.newIntRange(KanjiFieldName.STROKE_COUNT.name(), minStrokes, maxStrokes, true, true);
			query.add(strokeRangeQuery, Occur.MUST);
		}

		return getEntriesForQuery(query);
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
				KanjiEntry kanjiEntry = getKanjiEntry(literal);
				if (kanjiEntry != null && !entries.contains(kanjiEntry)) {
					entries.add(kanjiEntry);
				}
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

	KanjiEntry getKanjiEntryForDocument(Document document) throws IOException {
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

		entry.setRadical(ByteBuffer.wrap(document.getFieldable(KanjiFieldName.MAIN_RADICAL.name()).getBinaryValue()).getShort());
		entry.setStrokeCount(Byte.valueOf(document.getFieldable(KanjiFieldName.STROKE_COUNT.name()).stringValue()));

		entry.getRadicals().addAll(getStringsForField(document, KanjiFieldName.RADICAL));

		entry.getOnYomi().addAll(getStringsForField(document, KanjiFieldName.ONYOMI));
		entry.getKunYomi().addAll(getStringsForField(document, KanjiFieldName.KUNYOMI));
		entry.getNanori().addAll(getStringsForField(document, KanjiFieldName.NANORI));
		entry.getPinyin().addAll(getStringsForField(document, KanjiFieldName.PINYIN));
		entry.getHangul().addAll(getStringsForField(document, KanjiFieldName.HANGUL));

		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_EN.name()), Language.en));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_FR.name()), Language.fr));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_ES.name()), Language.es));
		entry.getMeanings().addAll(getMeanings(document.getFieldables(KanjiFieldName.MEANING_PT.name()), Language.pt));

		entry.getStrokePaths().addAll(getLines(document.getFieldables(KanjiFieldName.STROKE_PATHS.name())));

		return entry;
	}

	private ArrayList<String> getStringsForField(Document document, KanjiFieldName field) {
		ArrayList<String> strings = new ArrayList<String>();
		for (Fieldable f : document.getFieldables(field.name())) {
			if (f.isStored()) {
				strings.add(f.stringValue());
			}
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

	private Collection<? extends String> getLines(Fieldable[] fieldables) throws IOException {
		List<String> lines = new ArrayList<String>();
		for (Fieldable field : fieldables) {
			try {
				byte[] binary = CompressionTools.decompress(field.getBinaryValue());
				String text = new String(binary, "UTF-8");
				lines.addAll(Arrays.asList(text.split("\n")));
			} catch (DataFormatException e) {
				throw new IOException(e);
			}
		}
		return lines;
	}

	@Override
	protected IndexFieldName getIndexFieldName(String fieldName) {
		return KanjiFieldName.valueOf(fieldName);
	}
}
