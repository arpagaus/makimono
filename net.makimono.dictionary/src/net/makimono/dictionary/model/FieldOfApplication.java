package net.makimono.dictionary.model;

/**
 * These enums are defined in the JMdict.dtd. They had to be transformed in
 * order to compile them:
 * <ol>
 * <li>Replace all dashes '-' with under scores '_'</li>
 * <li>Add the prefix 'JMdict_'</li>
 * </ol>
 */
public enum FieldOfApplication {
	JMdict_anat, JMdict_archit, JMdict_astron, JMdict_baseb, JMdict_biol, JMdict_bot, JMdict_Buddh, JMdict_bus, JMdict_chem, JMdict_comp, JMdict_econ, JMdict_engr, JMdict_finc, JMdict_food, JMdict_geol, JMdict_geom, JMdict_law, JMdict_ling, JMdict_MA, JMdict_math, JMdict_med, JMdict_mil, JMdict_music, JMdict_physics, JMdict_Shinto, JMdict_sports, JMdict_sumo, JMdict_zool
}
