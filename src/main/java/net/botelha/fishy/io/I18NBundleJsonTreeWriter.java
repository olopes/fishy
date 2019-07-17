package net.botelha.fishy.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.GsonBuilder;

import net.botelha.fishy.I18NException;
import net.botelha.fishy.model.I18NBundle;
import net.botelha.fishy.model.I18NEntry;

public class I18NBundleJsonTreeWriter implements I18NBundleWriter {
	
	public void write(I18NBundle bundle, File bundleFolder) {
		if (bundle == null) throw new IllegalArgumentException();
		if (bundleFolder == null) throw new IllegalArgumentException();
		if (!bundleFolder.isDirectory()) throw new I18NException("Bad bundle "+bundleFolder);

		// write entries for each locale
		for (Locale locale : bundle.getLocales()) {
			File outputFile = new File(bundleFolder, locale.toString()+".json");

			Map<String, Object> json = new LinkedHashMap<>();
			makeJson(bundle.getRoot().getChildren(), json, locale);
			try (PrintStream writer = new PrintStream(outputFile, "UTF-8")) {
				new GsonBuilder()
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create()
					.toJson(json, writer);
			} catch (IOException e) {
				throw new I18NException("Read KO", e);
			}
			
		}
		
	}
	
	private void makeJson(List<I18NEntry> children, Map<String,Object> json, Locale locale) {
		for (I18NEntry child : children) {
			Object value;
			if(child.getChildren().isEmpty()) {
				value = child.getTranslations().getOrDefault(locale, "");
			} else {
				Map<String,Object> childJson = new LinkedHashMap<>();
				makeJson(child.getChildren(), childJson, locale);
				value = childJson;
			}
			json.put(child.getKey(), value);
		}
	}
}
