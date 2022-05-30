 	package SortedList;
 	import java.lang.IndexOutOfBoundsException;


/**
 * Implementation of a SortedList using a SinglyLinkedList
 * @author Fernando J. Bermudez and Juan O. Lopez
 * @author Orlando G. Mercado Tellado
 * @version 2.0
 * @since 10/16/2021
 */
public class SortedLinkedList<E extends Comparable<? super E>> extends AbstractSortedList<E> {

	@SuppressWarnings("unused")
	private static class Node<E> {

		private E value;
		private Node<E> next;

		public Node(E value, Node<E> next) {
			this.value = value;
			this.next = next;
		}

		public Node(E value) {
			this(value, null); // Delegate to other constructor
		}

		public Node() {
			this(null, null); // Delegate to other constructor
		}

		public E getValue() {
			return value;
		}

		public void setValue(E value) {
			this.value = value;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public void clear() {
			value = null;
			next = null;
		}				
	} // End of Node class

	
	private Node<E> head; // First DATA node (This is NOT a dummy header node)
	
	public SortedLinkedList() {
		head = null;
		currentSize = 0;
	}

	@Override
	public void add(E e) {
		/* Special case: Be careful when the new value is the smallest */
		Node<E> newNode = new Node<>(e);
		Node<E> curNode;
		/* if value is null, throw IllegalArgumentException*/
		if(e == null)
		{
			throw(new IllegalArgumentException("Value cannot be null."));
		}
		if(this.size() == 0) //if list is empty place the node as the head
		{
			this.head = newNode;
			this.currentSize++;
		}
		else
		{
			int smaOrBig = this.head.getValue().compareTo(newNode.getValue());
			if(smaOrBig > 0) //newNode value is the smallest on the list
			{
				Node<E> oldHead = this.head; //save the old head's reference
				this.head = newNode; //newNode becomes the head of the list
				this.head.setNext(oldHead); 
				this.currentSize++;
			}
			else if(smaOrBig < 0) // newNode value is bigger than the current head
			{
				for(curNode = this.head; curNode != null; curNode = curNode.getNext()) //Traverse the list until newNode finds its position
				{
					if(curNode.getNext() == null) //curNode is the last one on the list. The newNode becomes the last.
					{
						curNode.setNext(newNode);
						newNode.setNext(null);
						this.currentSize++;
						break;
					}
					/** Compares two nodes. If true, then newNode gets add between both nodes. */
					if((curNode.getValue().compareTo(newNode.getValue()) < 0) && (curNode.getNext().getValue().compareTo(newNode.getValue()) >= 0))
					{
						Node<E> next = curNode.getNext();
						curNode.setNext(newNode);
						newNode.setNext(next);
						this.currentSize++;
						break;
					}
				}
			}
			else //smaOrBig == 0, newNode and header have the same value. The newNode becomes the second on the list.
			{
				Node<E> next = this.head.getNext();
				this.head.setNext(newNode);
				newNode.setNext(next);
				this.currentSize++;
			}
		}

	}

	@Override
	public boolean remove(E e) {
		/* Special case: Be careful when the value is found at the head node */
		int fIndex = firstIndex(e); //first index of the element in the list
		if(fIndex != -1) //if element is in the list
		{
			this.removeIndex(fIndex); //remove the node
			return true; 
		}
		
		return false; //The node with value e was not on the list
	}

	@Override
	public E removeIndex(int index) {
		/* Special case: Be careful when index = 0 */
		Node<E> rmNode, curNode;
		E value = null;
		int i = 0;
		if(index < 0 || index > this.size())
		{
			throw(new IndexOutOfBoundsException("Index should be bigger than or equal to 0 and less than the list size."));
		}
		
		else if(index == 0) //if targeted node is the first in the list
		{
			rmNode = this.head;
			Node<E> next = this.head.getNext(); //node that will be the new head of the list
			E rmValue = rmNode.getValue(); //save the value of the node to be remove
			rmNode.clear(); //delete the node
			this.head = next;
			this.currentSize--;
			return rmValue; 
		}
		for(curNode = this.head; curNode != null; curNode = curNode.getNext(), i++)
		{
			if(i+1 == index) // Next node is the targeted node. 
			{
				rmNode = curNode.getNext(); // node to remove 
				value = rmNode.getValue(); // save the value of the node to be remove
				Node<E> next = rmNode.getNext(); //save next node reference
				curNode.setNext(next);
				rmNode.clear(); //delete the node
				this.currentSize--;
				break; 
			}
		}
		
		return value;
	}

	@Override
	public int firstIndex(E e) {
		int target = -1;
		int i = 0;
		for(Node<E> curNode = this.head; curNode != null; curNode = curNode.getNext(), i++)
		{
			/* Compare elements*/
			if(e.compareTo(curNode.getValue()) == 0)
			{
				/* Element was found*/
				target = i; 
				break;
			}
		}
		return target;
	}
	
	@Override
	public E get(int index) {
		E value = null;
		int i = 0;
		if(index < 0 || index > this.size())
		{
			throw(new IndexOutOfBoundsException("Index should be bigger than or equal to 0 and less than the list size."));
		}
		for(Node<E> curNode = this.head; curNode != null; curNode = curNode.getNext(), i++)
		{
			if(i == index)
			{
				value = curNode.getValue(); //save the value of the node at index position
				break;
			}
		}
		return value;
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		int index = 0;
		E[] theArray = (E[]) new Comparable[size()]; // Cannot use Object here
		for(Node<E> curNode = this.head; index < size() && curNode  != null; curNode = curNode.getNext(), index++) {
			theArray[index] = curNode.getValue();
		}
		return theArray;
	}

}
