package four.level.expand.using.recyclerview.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Utility structure to represent expandable list.
 */
public class ExpandableList extends Node implements List<ExpandableListOperation> {

    /**
     * Actual list of nodes. It is a flatten representation of current expanded nodes.
     */
    private final List<ExpandableListOperation> list = new ArrayList<>();

    /**
     * To avoid calling invalidate() multiple times
     */
    private boolean ignoreInvalidate = false;

    public ExpandableList() {
    }

    public ExpandableList(Collection<? extends ExpandableListOperation> children) {
        insertAll(children);
    }

    public void insertAll(Collection<? extends ExpandableListOperation> children) {
        insertAll(0, children);
    }

    public void insertAll(int index, Collection<? extends ExpandableListOperation> children) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("index=" + index + " is out of bounds: [0, " + size() + "]");
        }
        ignoreInvalidate = true;
        int count = 0;
        for (ExpandableListOperation node : children) {
            insert(index + count, node);
            count++;
        }
        ignoreInvalidate = false;
        invalidate();
    }

    /**
     * Expand/collapse all child nodes of this list.
     *
     * @param expanded state
     */
    @Override
    public void setExpanded(boolean expanded) {
        ignoreInvalidate = true;
        for (ExpandableListOperation node : children) {
            node.setExpanded(expanded);
        }
        ignoreInvalidate = false;
        invalidate();
    }

    /**
     * @return {@code true}, if all nodes are expanded, {@code false} otherwise
     */
    @Override
    public boolean isExpanded() {
        for (ExpandableListOperation node : children) {
            if (!node.isExpanded()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Expand parent node.
     *
     * @param node node
     * @return count of nodes added to the current list (returns {@code 0} if parent was already expanded)
     */
    public int expand(ExpandableListOperation node) {
        if (!isChild(node)) {
            throw new IllegalArgumentException(node + " is not a member of this list");
        }
        if (!node.isExpanded()) {
            int old = list.size();
            node.setExpanded(true);
            return list.size() - old;
        }
        return 0;
    }

    /**
     * Collapse parent node.
     *
     * @param node node
     * @return count of nodes removed from the current list (returns {@code 0} if parent was already collapsed)
     */
    public int collapse(ExpandableListOperation node) {
        if (!isChild(node)) {
            throw new IllegalArgumentException();
        }
        if (node.isExpanded()) {
            int old = list.size();
            node.setExpanded(false);
            return old - list.size();
        }
        return 0;
    }

    /**
     * Collapse all parent nodes.
     */
    public void collapseAll() {
        setExpanded(false);
    }

    /**
     * Expand all parent nodes.
     */
    public void expandAll() {
        setExpanded(true);
    }

    /**
     * @return total number of all nodes (including collapsed)
     */
    public int absoluteSize() {
        // -1 because we don't need the root to be included
        return getAbsoluteSize(this) - 1;
    }

    /**
     * Count of the children in this list.
     * <br>
     * Shorthand for {@code getChildren().size()}.
     *
     * @return count of children in this list
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Get child at {@code index}.
     *
     * @param index index of the child
     * @return child at {@code index}
     */
    public ExpandableListOperation getChild(int index) {
        return children.get(index);
    }

    /**
     * Invalidate tree to recalculate list content and positions.
     */
    public void invalidate() {
        list.clear();
        for (ExpandableListOperation parent : children) {
            addInternal(parent);
        }
    }

    /**
     * Expand or collapse all nodes, recursively.
     *
     * @param expanded state
     */
    public void setExpandedDeep(boolean expanded) {
        ignoreInvalidate = true;
        setExpandedDeep(expanded, this);
        ignoreInvalidate = false;
        invalidate();
    }

    private void setExpandedDeep(boolean expanded, ExpandableListOperation node) {
        node.setExpanded(expanded);
        for (ExpandableListOperation child : node.getChildren()) {
            setExpandedDeep(expanded, child);
        }
    }

    /**
     * {@link ExpandableList} cannot have parent.
     */
    @Deprecated
    @Override
    public void setParent(ExpandableListOperation parent) {
        throw new UnsupportedOperationException(ExpandableList.class.getSimpleName() + " cannot have parent");
    }

    /**
     * {@link ExpandableList} cannot have parent.
     *
     * @return always null
     */
    @Override
    public ExpandableListOperation getParent() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Compute absolute size of the {@code node}.
     *
     * @param node node
     * @return absolute size of node, including collapsed nodes.
     */
    protected int getAbsoluteSize(ExpandableListOperation node) {
        // include node
        int count = 1;
        for (ExpandableListOperation child : node.getChildren()) {
            count += getAbsoluteSize(child);
        }
        return count;
    }

    /**
     * Add {@code node} and it's children (if {@code node} is {@link ExpandableListOperation#isExpanded() expanded}) to the {@link #list}.
     *
     * @param node node
     */
    protected void addInternal(ExpandableListOperation node) {
        list.add(node);
        if (node.isExpanded()) {
            for (ExpandableListOperation child : node.getChildren()) {
                addInternal(child);
            }
        }
    }

    @Override
    public void onInserted(ExpandableListOperation child) {
        if (!ignoreInvalidate) {
            invalidate();
        }
        super.onInserted(child);
    }

    @Override
    public void onChanged(ExpandableListOperation node) {
        if (!ignoreInvalidate) {
            invalidate();
        }
        super.onChanged(node);
    }

    @Override
    public void onDeleted(ExpandableListOperation fromParent, ExpandableListOperation child) {
        if (!ignoreInvalidate) {
            invalidate();
        }
        super.onDeleted(fromParent, child);
    }

    /**
     * Check if {@code node} is reachable from this list.
     *
     * @param node node
     * @return is {@code node} reachable from this list ({@code node} is direct or indirect child of this list)
     */
    protected boolean isChild(ExpandableListOperation node) {
        ExpandableListOperation parent = node.getParent();
        while (parent != null) {
            if (parent == this) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printNode(sb, this);
        return sb.toString();
    }

    private static void printNode(StringBuilder sb, ExpandableListOperation node) {
        boolean hasParent = node.getParent() != null;
        if (hasParent) {
            // create parents chain
            List<ExpandableListOperation> parents = new ArrayList<>(1);
            ExpandableListOperation p = node.getParent();
            while (p != null) {
                parents.add(0, p);
                p = p.getParent();
            }
            // the root node comes first
            for (ExpandableListOperation parent : parents) {
                if (parent.getParent() != null) {
                    List<? extends ExpandableListOperation> children = parent.getParent().getChildren();
                    if (children.get(children.size() - 1) != parent) {
                        // if parent is not the last node of it's parent - print pipe
                        sb.append("|  ");
                        continue;
                    }
                }
                // for other nodes print spaces
                sb.append("   ");
            }
            List<? extends ExpandableListOperation> children = node.getParent().getChildren();
            if (children.get(children.size() - 1) == node) {
                // check if node is last child of it's parent
                sb.append("└");
            } else {
                sb.append("├");
            }
        }
        sb.append(node.isExpanded() ? "+ " : "─ ");
        sb.append(node.getClass().getSimpleName());
        if (hasParent) {
            // print parent's index
            sb.append(String.format(" [%d] ", node.getParent().getChildren().indexOf(node)));
        }
        sb.append("\n");
        // print children of the node
        for (ExpandableListOperation child : node.getChildren()) {
            printNode(sb, child);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // List
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get actual list size, which contains all expanded nodes.
     *
     * @return actual list size
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Get element at {@code index} from actual list.
     *
     * @param index index of element
     * @return element at {@code index}
     */
    @Override
    public ExpandableListOperation get(int index) {
        return list.get(index);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public ExpandableListOperation[] toArray() {
        ExpandableListOperation[] array = new ExpandableListOperation[list.size()];
        return list.toArray(array);
    }

    /**
     * @see #toArray()
     */
    @Override
    public <E> E[] toArray(E[] ts) {
        return list.toArray(ts);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int indexOf(ExpandableListOperation child) {
        return list.indexOf(child);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public Iterator<ExpandableListOperation> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<ExpandableListOperation> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<ExpandableListOperation> listIterator(int index) {
        return new ListItr(index);
    }

    @Override
    public List<ExpandableListOperation> subList(int from, int to) {
        return Collections.unmodifiableList(list.subList(from, to));
    }

    /**
     * Any modifications must be made by methods in {@link ExpandableList}.
     */
    private class ListItr implements ListIterator<ExpandableListOperation> {
        int cursor;

        public ListItr(int startFrom) {
            if (startFrom < 0 || startFrom > size()) {
                throw new IndexOutOfBoundsException("startFrom=" + startFrom + " is not within bounds: [0; " + size() + "]");
            }
            this.cursor = startFrom;
        }

        @Override
        public boolean hasNext() {
            return cursor < size() - 1;
        }

        @Override
        public ExpandableListOperation next() {
            if (cursor >= size()) {
                throw new NoSuchElementException();
            }
            return get(cursor++);
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public ExpandableListOperation previous() {
            if (cursor <= 0) {
                throw new NoSuchElementException();
            }
            return get(--cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(ExpandableListOperation node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(ExpandableListOperation node) {
            throw new UnsupportedOperationException();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unsupported operations
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @deprecated Unsupported. Use {@link #insert(ExpandableListOperation)}.
     */
    @Deprecated
    @Override
    public boolean add(ExpandableListOperation node) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported. Use {@link #insert(int, ExpandableListOperation)}.
     */
    @Deprecated
    @Override
    public void add(int index, ExpandableListOperation node) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported. Use {@link #delete(ExpandableListOperation)}.
     */
    @Deprecated
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported. Use {@link #insertAll(Collection)}.
     */
    @Deprecated
    @Override
    public boolean addAll(Collection<? extends ExpandableListOperation> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported. Use {@link #insertAll(int, Collection)}.
     */
    @Deprecated
    @Override
    public boolean addAll(int index, Collection<? extends ExpandableListOperation> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported.
     */
    @Deprecated
    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported.
     */
    @Deprecated
    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported.
     */
    @Deprecated
    @Override
    public ExpandableListOperation set(int index, ExpandableListOperation node) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported. Use {@link #delete(int)}.
     */
    @Deprecated
    @Override
    public ExpandableListOperation remove(int i) {
        throw new UnsupportedOperationException();
    }
}