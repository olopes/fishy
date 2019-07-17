package net.botelha.fishy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.botelha.fishy.io.I18NBundleJsonTreeReader;
import net.botelha.fishy.model.I18NBundle;
import net.botelha.fishy.model.I18NEntry;
import net.botelha.fishy.model.I18NException;

class I18NBundleJsonTreeReaderTest {

	final Locale en = Locale.forLanguageTag("en");
	final Locale fr = Locale.forLanguageTag("fr");
	I18NBundleJsonTreeReader instance;
	
	@BeforeEach
	void setup() {
		this.instance = new I18NBundleJsonTreeReader();
	}
	
	@Test
	void readShouldThrowIllegalArgumentExceptionWhenArgumentIsNull() {
		assertThrows(IllegalArgumentException.class, () -> instance.read(null));
	}

	@Test
	void readShouldThrowI18NExceptionWhenArgumentIsDoesNotExists() {
		assertThrows(I18NException.class, () -> instance.read(new File("/fake/path")));
	}

	@Test
	void readShouldReadAndReturnAPropertiesI18NBundleInstanceWhenArgumentIsDirectory() {
		I18NBundle result = instance.read(new File("sample"));
		
		// Assertions on the resulting object
		
		assertNull(result.getRoot().getKey());
		
		List<I18NEntry> children = result.getRoot().getChildren();
		assertEquals(3, children.size());
		
		I18NEntry demo = children.get(0);
		assertEquals(demo.getKey(), "demo");
		I18NEntry exclusive = children.get(1);
		assertEquals(exclusive.getKey(), "exclusive");
		I18NEntry mixed = children.get(2);
		assertEquals(mixed.getKey(), "mixed");
		
		I18NEntry text = demo.getChildren().get(0);
		assertI18NEntry(text, "text", "This is a simple demonstration app for ngx-translate EN", "This is a simple demonstration app for ngx-translate FR");
		I18NEntry title = demo.getChildren().get(1);
		assertI18NEntry(title, "title", "Translation demo EN", "Translation demo FR");

		I18NEntry enOnly = mixed.getChildren().get(0);
		assertI18NEntry(enOnly, "en only", "correct EN", null);
		I18NEntry exists = mixed.getChildren().get(1);
		assertI18NEntry(exists, "exists", "yes EN", "yes FR");

		I18NEntry frOnly = exclusive.getChildren().get(0);
		assertI18NEntry(frOnly, "fr only", null, "right FR");
	}

	private void assertI18NEntry(I18NEntry entry, String key, String en, String fr) {

		assertEquals(entry.getKey(), key);
		assertEquals(entry.getTranslations().get(this.en), en);
		assertEquals(entry.getTranslations().get(this.fr), fr);
		assertTrue(entry.getChildren().isEmpty());
		
	}

	
}
