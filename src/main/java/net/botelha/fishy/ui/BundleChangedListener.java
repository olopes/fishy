package net.botelha.fishy.ui;

@FunctionalInterface
public interface BundleChangedListener {

	void bundleChanged(BundleChangedEvent event);
	
}
