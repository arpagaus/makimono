package net.makimono.searcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.spell.TermFreqIterator;

public class DictionaryTermFreqIterator implements TermFreqIterator {
	private final TermEnum termEnum;

	public Set<String> includedFields = Collections.emptySet();

	public DictionaryTermFreqIterator(TermEnum termEnum) {
		this.termEnum = termEnum;
	}

	public void setIncludedFields(Collection<String> includedFields) {
		this.includedFields = new HashSet<String>(includedFields);
	}

	@Override
	public boolean hasNext() {
		try {
			boolean next = termEnum.next();
			while (next) {
				Term term = termEnum.term();
				if (includeField(term.field()) && noTrailingSpace(term.text())) {
					return true;
				}
				next = termEnum.next();
			}
			return next;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean noTrailingSpace(String text) {
		return !text.endsWith(" ");
	}

	private boolean includeField(String field) {
		return includedFields.contains(field);
	}

	@Override
	public String next() {
		return termEnum.term().text();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float freq() {
		return termEnum.docFreq();
	}
}