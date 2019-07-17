package net.botelha.fishy.ui;

import java.util.Locale;

public class ValueChangedEvent {

	private final TranslationField source;
	private final Locale locale;
	private final String value;

	public ValueChangedEvent(final TranslationField source, final Locale locale, final String value) {
		this.source = source;
		this.locale = locale;
		this.value = value;
	}

	public TranslationField getSource() {
		return source;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getValue() {
		return value;
	}

}
