package net.botelha.fishy.ui;

import java.util.Locale;

import javax.swing.tree.TreePath;

public class TranslationChangedEvent {

	private final TreePath path;
	private final Locale locale;
	private final String newValue;
	private final String oldValue;

	public TranslationChangedEvent(TreePath path, Locale locale, String oldValue, String newValue) {
		super();
		this.path = path;
		this.locale = locale;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	public TreePath getPath() {
		return path;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getNewValue() {
		return newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

}
