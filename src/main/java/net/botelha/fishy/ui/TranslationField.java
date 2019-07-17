package net.botelha.fishy.ui;

import java.awt.BorderLayout;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TranslationField extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private final JTextArea textarea;
	private final Locale locale;
	private final Deque<ValueChangedListener> changeListeners = new ConcurrentLinkedDeque<>();
	private transient boolean preventEvent = false;
	
	public TranslationField(Locale locale) {
		this.locale = locale;
		this.textarea = new JTextArea();
		setupUI();
	}

	private void setupUI() {
		String title = String.format("%s - %s", this.locale, this.locale.getDisplayName(this.locale));
		this.setBorder(BorderFactory.createTitledBorder(title));

		this.textarea.setRows(3);
		this.textarea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				fireValueChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				fireValueChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				fireValueChanged();
			}
		});
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(this.textarea), BorderLayout.CENTER);
		
	}
	
	public void setValue(Map<Locale, String> translations) {
		this.preventEvent = true;
		setEnabled(translations != null);
		if(translations == null) {
			this.textarea.setText("");
		} else {
			this.textarea.setText(translations.getOrDefault(locale, ""));
		}
		this.preventEvent = false;
	}
	
	public void addValueChangedListener(ValueChangedListener listener) {
		this.changeListeners.add(listener);
	}
	
	public void removeValueChangedListener(ValueChangedListener listener) {
		this.changeListeners.remove(listener);
	}
	
	protected void fireValueChanged() {
		if(preventEvent) return;
		ValueChangedEvent event = new ValueChangedEvent(this, this.locale, this.textarea.getText());
		this.changeListeners.forEach(l -> l.valueChanged(event));
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.textarea.setEnabled(enabled);
		this.textarea.setEditable(enabled);
	}
	
}
