package net.botelha.fishy.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import net.botelha.fishy.model.I18NEntry;

public class TranslationsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final List<Locale> locales;
	private final Map<Locale, TranslationField> fields;
	private final Deque<TranslationChangedListener> changeListeners = new ConcurrentLinkedDeque<>();
	private transient TreePath path = null;
	
	public TranslationsPanel() {
		this.locales = new ArrayList<>();
		this.fields = new HashMap<>();
		setupUI();
	}

	private void setupUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(320, 120));
		localesUpdated();
	}
	
	public void setLocales(List<Locale> newLocales) {
		this.locales.clear();
		this.locales.addAll(newLocales);
		localesUpdated();
	}
	
	private void localesUpdated() {
		
		if(!fields.isEmpty() ) {
			fields.values().forEach(field -> this.remove(field));
			fields.clear();
		}
		
		for(Locale locale : locales) {
			TranslationField field = new TranslationField(locale);
			field.addValueChangedListener(this::translationChanged);
			field.setEnabled(false);
			field.setMinimumSize(new Dimension(300, 100));
			field.setPreferredSize(new Dimension(300, 100));
			field.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));			
			
			fields.put(locale, field);
			this.add(field);
		}
		this.revalidate();
	}
	
	
	public void setI18NPath(TreePath path) {
		this.path = path;
		this.fields.values().forEach(field -> {
			field.setEnabled(path != null);
			field.setValue(path == null ? null : ((I18NEntry) path.getLastPathComponent()).getTranslations());
		});
	}
	
	private void translationChanged(ValueChangedEvent event) {
		if(this.path == null) return;
		I18NEntry entry = (I18NEntry) path.getLastPathComponent();
		String newValue = event.getValue();
		String oldValue = entry.getTranslations().put(event.getLocale(), event.getValue());
		
		fireTranslationChanged(new TranslationChangedEvent(path, event.getLocale(), oldValue, newValue));
	}
	
	public void addTranslationChangedListener(TranslationChangedListener listener) {
		this.changeListeners.add(listener);
	}
	
	public void removeTranslationChangedListener(TranslationChangedListener listener) {
		this.changeListeners.remove(listener);
	}
	
	protected void fireTranslationChanged(TranslationChangedEvent event) {
		this.changeListeners.forEach(l -> l.translationChanged(event));
	}
	
}
