package net.botelha.fishy.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.LocaleUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.botelha.fishy.I18NException;
import net.botelha.fishy.model.I18NBundle;
import net.botelha.fishy.model.I18NEntry;

public class I18NBundleJsonTreeReader implements I18NBundleReader {
	
	static class Pair {
		final Locale locale;
		final JsonObject json;
		
		Pair(File file) {
			this.locale = jsonFileToLocale(file);
			try (Reader inputStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				this.json = (JsonObject) new JsonParser().parse(inputStream);				
			} catch (IOException e) {
				throw new I18NException("Read KO", e);
			}
		}
	}
	
	public I18NBundle read(File bundleFolder) {
		if (bundleFolder == null) throw new IllegalArgumentException();
		if (!bundleFolder.isDirectory()) throw new I18NException("Bad bundle "+bundleFolder);

		File [] files = bundleFolder.listFiles(I18NBundleJsonTreeReader::jsonLocaleFilter);
		
		List<Pair> bundleContents = Arrays.stream(files)
				.map(Pair::new)
				.collect(Collectors.toList());

		List<Locale> locales = bundleContents
				.stream()
				.map(p -> p.locale)
				.sorted((a,b) -> a.toString().compareTo(b.toString()))
				.collect(Collectors.toCollection(ArrayList::new));
		
		I18NBundle root = new I18NBundle(locales);
		
		parseJsonToI18NBundle(bundleContents, root);
		
		return root;
	}
	
	static boolean jsonLocaleFilter(File file) {
		return file.getName().matches("[a-z][a-z]([_-][a-zA-Z][a-zA-Z])?\\.json") 
				&& LocaleUtils.isAvailableLocale(jsonFileToLocale(file));
	}
	
	static Locale jsonFileToLocale(File file) {
		return LocaleUtils.toLocale(file.getName().replace(".json", ""));
	}
	
	static URL toURL(URI uri) {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			throw new I18NException("Bad URI "+uri, e);
		}
	}
	
	private void parseJsonToI18NBundle(List<Pair> bundleContents, I18NBundle root) {
		for (Pair p : bundleContents) {
			parseJsonObject(p.locale, p.json, root.getRoot());
		}
	}

	private void parseJsonObject(Locale locale, JsonObject json, I18NEntry parent) {
		for(String key : json.keySet()) {
			I18NEntry child = parent
					.getChildren()
					.stream()
					.filter(e -> key.equals(e.getKey()))
					.findAny()
					.orElseGet(() -> {
						I18NEntry newEntry = new I18NEntry(key);
						parent.getChildren().add(newEntry);
						return newEntry;
					});
			JsonElement element = json.get(key);
			if(element.isJsonPrimitive()) {
				child.getTranslations().put(locale, element.getAsString());
			} else if(element.isJsonObject()) {
				parseJsonObject(locale, element.getAsJsonObject(), child);
			} else {
				throw new I18NException("Kaput!");
			}
		}
		parent.getChildren().sort( (a, b) -> a.getKey().compareTo(b.getKey()));
	}
}
