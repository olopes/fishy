package net.botelha.fishy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import net.botelha.fishy.model.I18NBundle;
import net.botelha.fishy.model.I18NEntry;

public class FishyPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private I18NBundle bundle = new I18NBundle(Collections.emptyList());
	
	private I18NBundleTreeModel treeModel;
	private JTree tree;
	private I18NEntryTreeRenderer treeCellRenderer;
	private TranslationsPanel translations;
	private boolean modified = false;
	private JTextField currentPath;

	public FishyPanel() {
		setupUI();
	}

	private void setupUI() {
		this.treeModel = new I18NBundleTreeModel();
		this.treeCellRenderer = new I18NEntryTreeRenderer();
		this.tree = new JTree(this.treeModel);
		this.tree.setCellRenderer(this.treeCellRenderer);
		this.tree.setRootVisible(false);
		
		TreeSelectionModel selectionModel = this.tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectionModel.addTreeSelectionListener(e -> treeNodeSelected(e.getPath()));
		
		this.currentPath = new JTextField();
		this.currentPath.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0), "setTreePath");
		this.currentPath.getActionMap().put("setTreePath", new FishyAction("setTreePath", this::setTreePath));
		
		JPanel treeAndPathPanel = new JPanel(new BorderLayout());
		treeAndPathPanel.add(new JScrollPane(this.tree), BorderLayout.CENTER);
		treeAndPathPanel.add(this.currentPath, BorderLayout.SOUTH);
		
		this.translations = new TranslationsPanel();
		this.translations.addTranslationChangedListener(this::translationChanged);
		
		this.setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treeAndPathPanel, new JScrollPane(this.translations));
		splitPane.setDividerLocation(150);
		splitPane.getLeftComponent().setMinimumSize(new Dimension(150, 150));
		splitPane.getRightComponent().setMinimumSize(new Dimension(250, 150));
		this.add(splitPane, BorderLayout.CENTER);
		expandAllNodes(0, this.tree.getRowCount());
	}
	
	public I18NBundle getBundle() {
		return bundle;
	}
	
	public void setBundle(I18NBundle bundle) {
		this.bundle = bundle;
		bundleUpdated();
	}
	

	private void bundleUpdated() {
		this.treeCellRenderer.setLocales(bundle.getLocales());
		this.translations.setLocales(bundle.getLocales());
		this.treeModel.setRoot(this.bundle.getRoot());
		expandAllNodes(0, this.tree.getRowCount());
	}

	private void expandAllNodes(int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			this.tree.expandRow(i);
		}

		if (this.tree.getRowCount() != rowCount) {
			expandAllNodes(rowCount, this.tree.getRowCount());
		}
	}
	
	private void translationChanged(TranslationChangedEvent event) {
		if(!this.modified ) {
			this.modified = true;
			this.firePropertyChange("bundleModified", false, true);
		}
		
		if(StringUtils.isEmpty(event.getOldValue()) != StringUtils.isEmpty(event.getNewValue())) {
			this.treeModel.entryChanged(this.translations, event.getPath());
		}
	}
	
	private void treeNodeSelected(TreePath path) {
		translations.setI18NPath(path);
		String pathString = Arrays.stream(path.getPath()).map(o -> ((I18NEntry)o).getKey()).filter(s -> s != null).collect(Collectors.joining("."));
		this.currentPath.setText(pathString);
	}
	
	private void setTreePath(ActionEvent e) {
		System.out.println("Current path = "+this.currentPath.getText());
	}
	
	public boolean isModified() {
		return this.modified;
	}
	
	public void bundleSaved() {
		if(this.modified ) {
			this.modified = false;
			this.firePropertyChange("bundleModified", true, false);
		}
	}

}
