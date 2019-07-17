package net.botelha.fishy.ui;

import java.util.EventListener;

@FunctionalInterface
public interface ValueChangedListener extends EventListener {

	void valueChanged(ValueChangedEvent event);
	
}
