package me.nusimucat.roadmap.util;

/**Binary search tree */
public class BinaryTreeNode <T> {
    BinaryTreeNode<T> parentNode; 
    BinaryTreeNode<T> leftNode; 
    BinaryTreeNode<T> rightNode; 
    /**Idk how to explain this one */
    int key; 
    /**What you want to store in each node */
    T value; 
    /**Amount of all child nodes of this node (includes itself) */
    int size; 

    /**
     * Creates a new tree
     * @param key {@link BinaryTreeNode#key}
     * @param value {@link BinaryTreeNode#value}
     */
    public BinaryTreeNode (int key, T value) {
        this.parentNode = null; 
        this.leftNode = null; 
        this.rightNode = null; 
        this.key = key; 
        this.value = value; 
        this.size = 1; 
    }

    /**
     * Adds a child to the current tree
     * @param key {@link BinaryTreeNode#key}
     * @param value {@link BinaryTreeNode#value}
     */
    public void addChild (int key, T value) {
        if (this.size == 0) {
            this.key = key; 
            this.value = value; 
            this.size++; 
            return; 
        }
        this.size++; 

        if (key < this.key) { // do left side
            if (this.leftNode != null) {
                this.leftNode.addChild(key, value);
            } else { // this.leftnode == null
                BinaryTreeNode<T> childNode = new BinaryTreeNode<T>(key, value); 
                childNode.parentNode = this; 
                this.leftNode = childNode; 
            }
        } else if (key > this.key) { // do right side
            if (this.rightNode != null) {
                this.rightNode.addChild(key, value);
            } else { // this.rightNode == null
                BinaryTreeNode<T> childNode = new BinaryTreeNode<T>(key, value); 
                childNode.parentNode = this; 
                this.rightNode = childNode; 
            }       
        }
    }

    /**
     * Pops the node with the smallest value from the tree, and return it
     * @return the node with the smallest value
     */
    public BinaryTreeNode<T> removeSmallestNode () {
        if (this.parentNode != null) { // if parent exists
            return this.parentNode.removeSmallestNode(); 
        }

        BinaryTreeNode<T> smallestNode = this; 
        while (this.leftNode != null) {
            smallestNode = this.leftNode; 
        } // Gets the left-most (smallest) node

        if (smallestNode.rightNode != null) {
            smallestNode.rightNode.parentNode = smallestNode.parentNode;
            if (smallestNode.rightNode.key > smallestNode.parentNode.rightNode.key) {
                smallestNode.parentNode.leftNode = smallestNode.parentNode.rightNode; 
                smallestNode.rightNode.parentNode.leftNode = smallestNode.rightNode; 
            } 
            smallestNode.parentNode.leftNode = smallestNode.rightNode; 
        } else smallestNode.parentNode.leftNode = null; 
        smallestNode.parentNode = null; 
        return smallestNode; 
    }
}