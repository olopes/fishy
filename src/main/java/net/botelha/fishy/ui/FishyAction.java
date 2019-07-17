package net.botelha.fishy.ui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;

public class FishyAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private final transient Consumer<ActionEvent> action;
	
	public FishyAction(String actionName, Consumer<ActionEvent> action) {
		super(actionName);
		this.action = action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		action.accept(e);
	}

}
