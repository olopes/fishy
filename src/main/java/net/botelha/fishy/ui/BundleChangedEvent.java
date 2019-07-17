package net.botelha.fishy.ui;

public class BundleChangedEvent {
	final FishyPanel source;

	public BundleChangedEvent(final FishyPanel source) {
		this.source = source;
	}

	public FishyPanel getSource() {
		return source;
	}

}
