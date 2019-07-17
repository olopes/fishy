package net.botelha.fishy.model;

public class I18NException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public I18NException(String message) {
		super(message);
	}

	public I18NException(String message, Throwable t) {
		super(message, t);
	}

}
