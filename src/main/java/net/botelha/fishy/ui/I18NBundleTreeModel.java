package net.botelha.fishy.ui;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.botelha.fishy.model.I18NEntry;

public class I18NBundleTreeModel implements TreeModel {
	private I18NEntry root;
	private final Deque<TreeModelListener> listeners = new ConcurrentLinkedDeque<>();
	
	public I18NBundleTreeModel() {
		this.root = new I18NEntry(null);
	}
	
	@Override
	public Object getRoot() {
		return root;
	}
	
	public void setRoot(I18NEntry root) {
		this.root = root;
		fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot())));
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((I18NEntry)parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((I18NEntry)parent).getChildren().size();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((I18NEntry)node).getChildren().isEmpty();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((I18NEntry)parent).getChildren().indexOf(child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	protected void fireTreeNodesChanged(TreeModelEvent e) {
		listeners.stream().forEach(l -> l.treeNodesChanged(e));
	}

	protected void fireTreeNodesInserted(TreeModelEvent e) {
		listeners.stream().forEach(l -> l.treeNodesInserted(e));
	}

	protected void fireTreeNodesRemoved(TreeModelEvent e) {
		listeners.stream().forEach(l -> l.treeNodesRemoved(e));
	}

	protected void fireTreeStructureChanged(TreeModelEvent e) {
		listeners.stream().forEach(l -> l.treeStructureChanged(e));
	}

	public void entryChanged(Object source, TreePath path) {
		fireTreeNodesChanged(new TreeModelEvent(source, path));
	}

}
