package prerna.ds;

import java.util.Iterator;
import java.util.Stack;

public class IndexTreeIterator implements Iterator<TreeNode> {

	private Stack<TreeNode> nodeStack;
	
	/**
	 * Constructor for the IndexTreeIterator
	 * 
	 * @param typeRoot			
	 */
	public IndexTreeIterator(TreeNode typeRoot) {
		nodeStack = new Stack<>();
		if(typeRoot!=null) {
			addNextNodesToStack(typeRoot.getLeft(typeRoot));
		}
	}
	
	@Override
	/**
	 * Perform an In-Order depth-first-search (DFS)
	 */
	public boolean hasNext() {
		return !nodeStack.isEmpty();
	}
	
	@Override
	public TreeNode next() {
		if(!hasNext()) {
			throw new IndexOutOfBoundsException("No more nodes in Index Tree.");
		}

		TreeNode returnNode = nodeStack.pop();
		
		if(returnNode.rightSibling != null) {
			addNextNodesToStack(returnNode.rightSibling);
		} else if(returnNode.rightChild != null) {
			addNextNodesToStack(returnNode.rightChild);
		}

		if(returnNode.instanceNode.size() + returnNode.filteredInstanceNode.size() == 0) {
			//FIXME: this will cause an error in the event the last node in the Index Tree is 'logically deleted'
			return next();
		}
		return returnNode;
	}

	// Note: when updating to Java 8, no longer need to override remove() method
	@Override
	public void remove() {

	}
	
	/**
	 * Index tree's siblings share left/right children except for the most left and most right
	 * Most left has a left child that is not shared
	 * Most right has a right child that is not shared
	 * Using logic to only add left child and when the node does not have a right-sibling to add the right child
	 */
	private void addNextNodesToStack(TreeNode parentNode) {
		nodeStack.push(parentNode);
		
		if(parentNode.leftChild != null) {
			addNextNodesToStack(parentNode.leftChild);
		}
	}
}
