package net.makimono.dictionary.model;

/**
 * These enums are defined in the JMdict.dtd. They had to be transformed in
 * order to compile them:
 * <ol>
 * <li>Replace all dashes '-' with under scores '_'</li>
 * <li>Add the prefix 'JMdict_'</li>
 * </ol>
 */
public enum PartOfSpeech {
	JMdict_adj_f, JMdict_adj_i, JMdict_adj_ku, JMdict_adj_na, JMdict_adj_nari, JMdict_adj_no, JMdict_adj_pn, JMdict_adj_shiku, JMdict_adj_t, JMdict_adv, JMdict_adv_to, JMdict_aux_adj, JMdict_aux, JMdict_aux_v, JMdict_conj, JMdict_ctr, JMdict_exp, JMdict_int, JMdict_n_adv, JMdict_n, JMdict_n_pref, JMdict_n_suf, JMdict_n_t, JMdict_num, JMdict_pn, JMdict_pref, JMdict_prt, JMdict_suf, JMdict_v1, JMdict_v2a_s, JMdict_v2h_s, JMdict_v2r_s, JMdict_v2y_s, JMdict_v4b, JMdict_v4h, JMdict_v4k, JMdict_v4r, JMdict_v5aru, JMdict_v5b, JMdict_v5g, JMdict_v5k, JMdict_v5k_s, JMdict_v5m, JMdict_v5n, JMdict_v5r_i, JMdict_v5r, JMdict_v5s, JMdict_v5t, JMdict_v5u, JMdict_v5u_s, JMdict_vi, JMdict_vk, JMdict_vn, JMdict_vr, JMdict_vs_c, JMdict_vs_i, JMdict_vs, JMdict_vs_s, JMdict_vt, JMdict_vz;

	public static boolean exists(String enumName) {
		for (PartOfSpeech p : values()) {
			if (p.name().equals(enumName)) {
				return true;
			}
		}
		return false;
	}
}
