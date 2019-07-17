package net.botelha.fishy.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class I18NEntry {

	private final String key;
	private final Map<Locale, String> translations;
	private final List<I18NEntry> children;

	public I18NEntry(String key) {
		this.key = key;
		this.translations = new HashMap<>();
		this.children = new ArrayList<>();
	}

	public String getKey() {
		return key;
	}

	public Map<Locale, String> getTranslations() {
		return translations;
	}

	public List<I18NEntry> getChildren() {
		return children;
	}

}
