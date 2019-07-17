package net.botelha.fishy.ui;

import java.util.EventListener;

@FunctionalInterface
public interface TranslationChangedListener extends EventListener {

	void translationChanged(TranslationChangedEvent event);
}
