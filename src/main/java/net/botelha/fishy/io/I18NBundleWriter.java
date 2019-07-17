package net.botelha.fishy.io;

import java.io.File;

import net.botelha.fishy.model.I18NBundle;

public interface I18NBundleWriter {

	void write(I18NBundle bundle, File output);
	
}
