package net.botelha.fishy.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.lang3.StringUtils;

import net.botelha.fishy.model.I18NEntry;

public class I18NEntryTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private final List<Locale> locales;
	private I18NEntry root;
	private String filter;
	private Font regular;
	private Font match;
	private Font noMatch;
	
	public I18NEntryTreeRenderer() {
		this.locales = new ArrayList<>();
	}
	
	public void setLocales(List<Locale> locales) {
		this.locales.clear();
		this.locales.addAll(locales);
	}
	
	public void setRoot(I18NEntry root) {
		this.root = root;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if(this.regular == null) {
			this.regular = getFont();
			this.match = this.regular.deriveFont(Font.BOLD);
			this.noMatch = this.regular.deriveFont(Font.ITALIC);
		}
		
		I18NEntry entry = (I18NEntry) value;
		
		this.setText(String.format("%s (%d/%d)", entry.getKey(), entry.getTranslations().values().stream().filter(StringUtils::isNotBlank).count(), locales.size()));
		
		if(StringUtils.isEmpty(filter) || entry == root) {
			setFont(this.regular);
			return this;
		}
		
		if(entryMatches(entry)) {
			setForeground(Color.BLACK);
			setFont(this.match);
		} else {
			setForeground(Color.GRAY);
			setFont(this.noMatch);
		}
		
		return this;
	}

	private boolean entryMatches(I18NEntry entry) {
		if(entry == root) return true;
		
		if(StringUtils.contains(entry.getKey(), filter)) return true;
		
		for(Locale locale : locales) {
			String translation = entry.getTranslations().get(locale);
			if(StringUtils.contains(translation, filter)) return true;
		}
		return false;
	}
}