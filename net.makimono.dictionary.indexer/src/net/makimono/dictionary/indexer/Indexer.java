package net.makimono.dictionary.indexer;

import java.io.File;

import org.apache.lucene.store.Directory;

public interface Indexer {
	public void createIndex(File sourceFile, Directory luceneDirectory) throws Exception;
}
