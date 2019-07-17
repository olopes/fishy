package net.botelha.fishy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.botelha.fishy.io.I18NBundleJsonTreeReader;
import net.botelha.fishy.io.I18NBundleJsonTreeWriter;

public class FishyWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	FishyPanel panel = new FishyPanel();
	
	Action openAction = new FishyAction("Open", this::openActionPerformed);
	
	Action saveAction = new FishyAction("Save", this::saveActionPerformed);
	
	Action quitAction = new FishyAction("Quit", this::quitActionPerformed);
	
	File openedFile;
	
	public FishyWindow() {
		setupUI();
	}

	private void setupUI() {
		setTitle("Fishy Translator - Please open a bundle");
		panel = new FishyPanel();
		panel.addPropertyChangeListener("bundleModified", this::bundleModified);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FishyWindow.this.windowClosing();
			}
		});
		getContentPane().setLayout(new BorderLayout(3,3));
		setJMenuBar(createMenu());
		getContentPane().add(panel, BorderLayout.CENTER);
		setMinimumSize(new Dimension(500, 150));
		setPreferredSize(new Dimension(640, 480));
		setSize(new Dimension(640, 480));
	}
	
	private void windowClosing() {
		if(panel.isModified()) {
			int confirmed = JOptionPane.showConfirmDialog(this, 
					"Are you sure you want to exit the program?", "Exit Program Message Box",
					JOptionPane.YES_NO_OPTION);
			if (confirmed != JOptionPane.YES_OPTION) {
				return;
			}
		}
		dispose();
	}
	
	private void bundleModified(PropertyChangeEvent e) {
		if((Boolean)e.getNewValue()) {
			setTitle("Fishy Translator - modified");
		} else {
			setTitle("Fishy Translator - saved");
		}
	}
	
	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		// Open
		JMenuItem open = new JMenuItem(openAction);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(open);
		
		// Save
		JMenuItem save = new JMenuItem(saveAction);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(save);
		
		// filler
		fileMenu.addSeparator();
		
		// Quit
		JMenuItem quit = new JMenuItem(quitAction);
		quit.setMnemonic(KeyEvent.VK_Q);
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		fileMenu.add(quit);
		
		menuBar.add(fileMenu);
		
		return menuBar;
	}

	private void openActionPerformed(ActionEvent e) {
		
		JFileChooser jfc = new JFileChooser(openedFile == null ? new File(".") : openedFile);
		jfc.setSelectedFile(openedFile);
		jfc.setDialogTitle("Please select the translations folder");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setAcceptAllFileFilterUsed(false);

		if (jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) { 
			return;
		}
		
		this.openedFile = jfc.getSelectedFile();
		this.setTitle("Fishy Translator");
		panel.setBundle(new I18NBundleJsonTreeReader().read(this.openedFile));
	}
	
	private void saveActionPerformed(ActionEvent e) {
		new I18NBundleJsonTreeWriter().write(panel.getBundle(), this.openedFile);
		this.panel.bundleSaved();
	}
	
	private void quitActionPerformed(ActionEvent e) {
		windowClosing();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FishyWindow().setVisible(true));
	}

}
