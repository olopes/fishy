package net.botelha.fishy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
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
	private JTextField searchBox;
	private JPopupMenu treeContextMenu;
	
	/* tree context menu actions */
	private Action treeInsert = new FishyAction("treeInsert", this::insertNewEntry);
	private Action treeClone = new FishyAction("treeClone", this::duplicateEntry);
	private Action treeRename = new FishyAction("treeRename", this::renameEntry);
	private Action treeDelete = new FishyAction("treeDelete", this::deleteEntry);
	private Action treeGoTo = new FishyAction("treeGoTo", this::gotoEntry);

	public FishyPanel() {
		setupUI();
	}

	private void setupUI() {
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK), "treeGoTo");
		this.getActionMap().put("treeGoTo", treeGoTo);
		
		createPopupMenu();
		
		createEntryTree();
		
		createQuickSearchBox();
		
		createTranslationsPanel();
		
		this.setLayout(new BorderLayout());
		this.add(createSplitPanel(), BorderLayout.CENTER);
		
	}

	public I18NBundle getBundle() {
		return bundle;
	}
	
	public void setBundle(I18NBundle bundle) {
		this.bundle = bundle;
		bundleUpdated();
	}
	
	private void createPopupMenu() {
		JPopupMenu popup = new JPopupMenu("Fishy");
		popup.add(createMenuItem("New entry", treeInsert));
		popup.addSeparator();
		popup.add(createMenuItem("Duplicate entry", treeClone));
		popup.add(createMenuItem("Rename entry", treeRename));
		popup.add(createMenuItem("Delete entry", treeDelete));
		popup.addSeparator();
		popup.add(createMenuItem("Go to entry", treeGoTo));
		this.treeContextMenu = popup;
	}
	private JMenuItem createMenuItem(String label, Action action) {
		JMenuItem item = new JMenuItem(action);
		item.setText(label);
		return item;
	}

	private void createEntryTree() {
		this.treeModel = new I18NBundleTreeModel();
		this.treeModel.addTreeModelListener(new I18NTreeModelListener());
		this.treeCellRenderer = new I18NEntryTreeRenderer();
		
		final JTree tree = new JTree(this.treeModel);
		tree.setCellRenderer(this.treeCellRenderer);
		tree.setRootVisible(false);
		tree.setEnabled(false);
		
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "treeDelete");
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "treeRename");
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), "treeInsert");
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "treeInsert");
		tree.getActionMap().put("treeDelete", treeDelete);
		tree.getActionMap().put("treeRename", treeRename);
		tree.getActionMap().put("treeInsert", treeInsert);
		
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (tree.isEnabled() && SwingUtilities.isRightMouseButton(e)) {
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					tree.setSelectionPath(path);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (tree.isEnabled() && SwingUtilities.isRightMouseButton(e)) {
					treeContextMenu.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		TreeSelectionModel selectionModel = tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectionModel.addTreeSelectionListener(e -> treeNodeSelected(e.getPath()));
		
		this.tree = tree;
		
		expandAllNodes(0, tree.getRowCount());
	}

	private void createQuickSearchBox() {
		final JTextField searchBox = new JTextField();
		searchBox.setEnabled(false);
		searchBox.getDocument().addDocumentListener(new TextChangedListener(e -> searchTree()));
		searchBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchBox.selectAll();
			}
		});
		searchBox.setToolTipText("Quick search");
		this.searchBox = searchBox;
	}

	private void createTranslationsPanel() {
		this.translations = new TranslationsPanel();
		this.translations.addTranslationChangedListener(this::translationChanged);
	}

	private JSplitPane createSplitPanel() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				createTreeAndSearchPanel(), new JScrollPane(this.translations));
		splitPane.setDividerLocation(150);
		splitPane.getLeftComponent().setMinimumSize(new Dimension(150, 150));
		splitPane.getRightComponent().setMinimumSize(new Dimension(250, 150));
		return splitPane;
	}

	private JPanel createTreeAndSearchPanel() {
		JPanel treeAndPathPanel = new JPanel(new BorderLayout());
		treeAndPathPanel.add(new JScrollPane(this.tree), BorderLayout.CENTER);
		treeAndPathPanel.add(this.searchBox, BorderLayout.NORTH);
		return treeAndPathPanel;
	}

	private void bundleUpdated() {
		this.searchBox.setEnabled(!bundle.getLocales().isEmpty());
		this.tree.setEnabled(!bundle.getLocales().isEmpty());
		this.treeCellRenderer.setLocales(bundle.getLocales());
		this.treeCellRenderer.setRoot(bundle.getRoot());
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
		bundleModified();
		
		if(StringUtils.isEmpty(event.getOldValue()) != StringUtils.isEmpty(event.getNewValue())) {
			this.treeModel.entryChanged(this.translations, event.getPath());
		}
	}
	
	private void treeNodeSelected(TreePath path) {
		translations.setI18NPath(path);
	}
	
	private void searchTree() {
		String query = this.searchBox.getText();
		this.treeCellRenderer.setFilter(query);
		this.tree.repaint();
	}
	
	private void insertNewEntry(ActionEvent evt) {
		TreePath path = tree.getSelectionPath();
		String pathString = path == null ? "" : treePathToString(path);
		String newName = JOptionPane.showInputDialog(this, "Enter the name of the new entry:", pathString);
		if(newName == null) {
			return;
		}
		TreePath newPath = treeModel.findTreePath(newName);
		if(newPath != null) {
			JOptionPane.showMessageDialog(this, String.format("Entry '%s' already exists.", newName));
		} else {
			treeModel.findOrCreateTreePath(newName);
		}
	}
	
	private void duplicateEntry(ActionEvent evt) {
		TreePath selectedPath = this.tree.getSelectionPath();
		if(selectedPath == null) return;
		String oldName = treePathToString(selectedPath);
		
		String newName = JOptionPane.showInputDialog(this, "Enter the new name:", oldName);

		if (StringUtils.isBlank(newName)) {
			JOptionPane.showMessageDialog(this, "The entry name can't be blank.");
			return;
		}
		
		TreePath existingPath = this.treeModel.findTreePath(newName);
		if(existingPath != null) {
			JOptionPane.showMessageDialog(this, "The entered name already exists.");
			return;
		}
		
		this.treeModel.duplicateEntry(selectedPath, newName);
	}
	
	private void renameEntry(ActionEvent evt) {
		TreePath selectedPath = this.tree.getSelectionPath();
		if(selectedPath == null) return;
		I18NEntry entry = (I18NEntry) selectedPath.getLastPathComponent();
		
		String newName = JOptionPane.showInputDialog(this, "Enter the new name:", entry.getKey());

		if (StringUtils.isBlank(newName)) {
			JOptionPane.showMessageDialog(this, "The entry name can't be blank.");
			return;
		}
		
		I18NEntry parent = (I18NEntry) selectedPath.getParentPath().getLastPathComponent();
		if(parent.getChildren().stream().anyMatch(e -> StringUtils.equals(newName, e.getKey()))) {
			JOptionPane.showMessageDialog(this, "The entered name already exists.");
			return;
		}
		
		this.treeModel.renameEntry(selectedPath, newName);
	}
	
	private void deleteEntry(ActionEvent evt) {
		TreePath selectedPath = this.tree.getSelectionPath();
		if(selectedPath != null && confirmDeleteAction(selectedPath)) {
			this.treeModel.deletePath(selectedPath);
		}
	}
	
	private boolean confirmDeleteAction(TreePath path) {
		String pathString = treePathToString(path);
		String message = String.format("Are you sure you want to delete the entry \"%s\" ?", pathString); 
		int selectedOption = JOptionPane.showConfirmDialog(this, message, "Delete", JOptionPane.OK_CANCEL_OPTION);
		return selectedOption == JOptionPane.OK_OPTION;
	}
	
	private void gotoEntry(ActionEvent evt) {
		String selectedName = JOptionPane.showInputDialog(this, "Go to entry:");
		if(selectedName == null) {
			return;
		}
		TreePath selectedPath = treeModel.findTreePath(selectedName);
		if(selectedPath != null) {
			this.tree.setSelectionPath(selectedPath);
		} else if(JOptionPane.showConfirmDialog(this, String.format("Entry was '%s' not found. Do you want to create a new one?", selectedName)) == JOptionPane.OK_OPTION) {
			treeModel.findOrCreateTreePath(selectedName);
		}
	}
	
	private String treePathToString(TreePath path) {
		String pathString = Arrays.stream(path.getPath()).map(o -> ((I18NEntry)o).getKey()).filter(s -> s != null).collect(Collectors.joining("."));
		return pathString;
	}

	public boolean isModified() {
		return this.modified;
	}
	
	private void bundleModified() {
		if(this.modified) return;
		this.modified = true;
		this.firePropertyChange("bundleModified", false, true);
	}
	
	public void bundleSaved() {
		if(!this.modified) return;
		this.modified = false;
		this.firePropertyChange("bundleModified", true, false);
	}

	private class I18NTreeModelListener implements TreeModelListener {
		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			bundleModified();
			expandAllNodes(0, tree.getRowCount());
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			bundleModified();
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			bundleModified();
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			bundleModified();
		}
	}
	
	private static class TextChangedListener implements DocumentListener {
		final Consumer<DocumentEvent> changeListener;
		
		TextChangedListener(Consumer<DocumentEvent> changeListener) {
			this.changeListener = changeListener;
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			this.changeListener.accept(e);
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			this.changeListener.accept(e);
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			this.changeListener.accept(e);
		}
	}

}
