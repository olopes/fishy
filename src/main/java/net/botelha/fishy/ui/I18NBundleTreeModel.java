package net.botelha.fishy.ui;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

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
		// do nothing. path is not "mutable"
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

	public TreePath findTreePath(String pathStr) {
		// TODO refactor this code
		String[] paths = pathStr.split("\\.");
		I18NEntry [] treePath = new I18NEntry[paths.length+1];
		treePath[0] = root;
		for(int i = 0; i < paths.length; i++) {
			I18NEntry current = findEntryWithKey(treePath[i], paths[i]);
			if(current == null) {
				return null;
			}
			treePath[i+1] = current;
		}
		return new TreePath(treePath);
	}
	
	public TreePath findOrCreateTreePath(String pathStr) {
		// TODO refactor this code - use another object to handle path stuff
		String[] paths = pathStr.split("\\.");
		I18NEntry [] treePath = new I18NEntry[paths.length+1];
		treePath[0] = root;
		int newEntry = -1;
		for(int i = 0; i < paths.length; i++) {
			I18NEntry current = findEntryWithKey(treePath[i], paths[i]);
			if(current == null) {
				if(newEntry == -1)
					newEntry = i;
				treePath[i].getChildren().add(current = new I18NEntry(paths[i]));
			}
			treePath[i+1] = current;
		}
		
		if(newEntry != -1) {
			I18NEntry [] newPath = new I18NEntry[newEntry+1];
			System.arraycopy(treePath, 0, newPath, 0, newEntry+1);
			newPath[newEntry].getChildren().sort((a,b)->StringUtils.compare(a.getKey(),  b.getKey()));
			TreeModelEvent event = new TreeModelEvent(this, newPath, null, null);
			fireTreeStructureChanged(event);
		}
		
		return new TreePath(treePath);
	}

	private I18NEntry findEntryWithKey(I18NEntry parent, String key) {
		return parent
				.getChildren()
				.stream()
				.filter(child -> StringUtils.equals(child.getKey(), key))
				.findFirst()
				.orElse(null);
	}

	public void deletePath(TreePath path) {
		I18NEntry entry = (I18NEntry)path.getLastPathComponent();
		
		// leave root alone
		if (entry == root) return;
		
		TreePath parentPath = path.getParentPath();
		I18NEntry parent = (I18NEntry) parentPath.getLastPathComponent();
        int childIndex = parent.getChildren().indexOf(entry);
        parent.getChildren().remove(entry);
        
        TreeModelEvent event = new TreeModelEvent(this, parentPath, new int [] {childIndex}, new Object[] {entry});
        fireTreeNodesRemoved(event);
	}

	public void renameEntry(TreePath path, String newName) {
		I18NEntry entry = (I18NEntry)path.getLastPathComponent();
		
		// leave root alone
		if (entry == root) return;
		
		TreePath parentPath = path.getParentPath();
		I18NEntry parent = (I18NEntry) parentPath.getLastPathComponent();
		I18NEntry replacement = new I18NEntry(newName);
		replacement.getChildren().addAll(entry.getChildren());
		replacement.getTranslations().putAll(entry.getTranslations());
		
		parent.getChildren().remove(entry);
		parent.getChildren().add(replacement);
		parent.getChildren().sort((a,b)->StringUtils.compare(a.getKey(),  b.getKey()));
		TreeModelEvent event = new TreeModelEvent(this, parentPath, null, null);
		fireTreeStructureChanged(event);
	}
	
	public void duplicateEntry(TreePath path, String newLocation) {
		I18NEntry clonedEntry = deepCopy((I18NEntry)path.getLastPathComponent());
		
		TreePath newPath = findOrCreateTreePath(newLocation);
		I18NEntry newEntry = (I18NEntry)newPath.getLastPathComponent();
		newEntry.getChildren().addAll(clonedEntry.getChildren());
		newEntry.getTranslations().putAll(clonedEntry.getTranslations());
		
		I18NEntry parent = (I18NEntry)newPath.getParentPath().getLastPathComponent();
		parent.getChildren().sort((a,b)->StringUtils.compare(a.getKey(),  b.getKey()));
		
		TreeModelEvent event = new TreeModelEvent(this, newPath.getParentPath(), null, null);
		fireTreeStructureChanged(event);
	}

	private I18NEntry deepCopy(I18NEntry source) {
		I18NEntry target = new I18NEntry(source.getKey());
		target.getTranslations().putAll(source.getTranslations());
		
		for(I18NEntry child : source.getChildren()) {
			target.getChildren().add(deepCopy(child));
		}
		
		return target;
	}
}

