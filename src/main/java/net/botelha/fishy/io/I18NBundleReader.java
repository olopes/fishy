package net.botelha.fishy.io;

import java.io.File;

import net.botelha.fishy.model.I18NBundle;

public interface I18NBundleReader {

	I18NBundle read(File input);
	
}
