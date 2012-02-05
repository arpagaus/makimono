package net.makimono.model;

/**
 * These enums are defined in the JMdict.dtd. They had to be transformed in
 * order to compile them:
 * <ol>
 * <li>Replace all dashes '-' with under scores '_'</li>
 * <li>Add the prefix 'JMdict_'</li>
 * </ol>
 */
public enum KanjiInfo {
	JMdict_ateji, JMdict_iK, JMdict_ik, JMdict_io, JMdict_oK
}
