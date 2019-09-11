package four.level.expand.using.recyclerview.list;

import java.util.List;

/**
 * Base element of the expandable list.
 */
public interface ExpandableListOperation {

    /**
     * Expand or collapse node.
     *
     * @param expanded new state
     */
    void setExpanded(boolean expanded);

    /**
     * Add child to node.
     *
     * @param child child node
     */
    int insert(ExpandableListOperation child);

    /**
     * Add child to node.
     *
     * @param index index at which child is to be insterted
     * @param child child node
     */
    int insert(int index, ExpandableListOperation child);

    /**
     * Remove child from node.
     *
     * @param child child node
     */
    int delete(ExpandableListOperation child);

    /**
     * Remove all child nodes.
     */
    void clear();

    /**
     * Get current node state.
     *
     * @return current state
     */
    boolean isExpanded();

    /**
     * Set parent for this node.
     * <br>
     * <b>This should only be called by parents and only on their direct children.</b>
     *
     * @param parent new parent for this node
     */
    void setParent(ExpandableListOperation parent);

    /**
     * @return the parent of this node. Can be {@code null} if node has no parent.
     */
    ExpandableListOperation getParent();

    /**
     * Get children attached to this node.
     *
     * @return attached children
     */
    List<? extends ExpandableListOperation> getChildren();

    /**
     * Add an observer to this node.
     *
     * @param observer observer
     */
    void addObserver(Observer observer);

    /**
     * Remove an observer to this node.
     *
     * @param observer observer
     */
    void removeObserver(Observer observer);

    /**
     * Observer for events.
     */
    interface Observer {
        /**
         * Called when node is inserted to it's {@link ExpandableListOperation#getParent() parent}.
         *
         * @param child inserted child
         */
        void onInserted(ExpandableListOperation child);

        /**
         * Called when {@link ExpandableListOperation#isExpanded() state} of the node is changed.
         *
         * @param child changed node
         */
        void onChanged(ExpandableListOperation child);

        /**
         * Called when {@code child} is removed from {@code oldParent}.
         *
         * @param oldParent old parent
         * @param child     removed child
         */
        void onDeleted(ExpandableListOperation oldParent, ExpandableListOperation child);
    }
}
