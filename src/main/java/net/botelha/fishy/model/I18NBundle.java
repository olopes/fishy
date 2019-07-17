package net.botelha.fishy.model;

import java.util.List;
import java.util.Locale;

public class I18NBundle {

	private final List<Locale> locales;
	private final I18NEntry root;

	public I18NBundle(List<Locale> locales) {
		this.locales = locales;
		this.root = new I18NEntry(null);
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public I18NEntry getRoot() {
		return root;
	}

}
