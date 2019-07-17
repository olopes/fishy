package net.botelha.fishy.ui;

import java.awt.Component;
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
	
	public I18NEntryTreeRenderer() {
		this.locales = new ArrayList<>();
	}
	
	public void setLocales(List<Locale> locales) {
		this.locales.clear();
		this.locales.addAll(locales);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		I18NEntry entry = (I18NEntry) value;
		
		this.setText(String.format("%s (%d/%d)", entry.getKey(), entry.getTranslations().values().stream().filter(StringUtils::isNotBlank).count(), locales.size()));
		return this;
	}
}