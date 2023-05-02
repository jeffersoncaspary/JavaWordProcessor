/**
 * Authors: Ben Caspary and Harrison Barrett
 * 
 * CSC 335, Project 2: Lil Lexi
 * 
 * File name: LilLexiControl.java
 * 
 * Files Used: LilLexiDoc.java
 * 
 * Files Used In: LilLexiUI.java, LilLexi.java
 * 
 */

package UI;

/**
 * LilLexiControl Class
 * 
 * This class acts as a controller for the Lil Lexi Document
 */
public class LilLexiControl 
{
	public LilLexiDoc currentDoc;
	/*
	 * Constructor
	 */
	public LilLexiControl( LilLexiDoc doc )
	{
		this.currentDoc = doc;
	}
	
	// ---- calls currentDoc.add(c) where c is the parameter passed in
	void add( char c ) 
	{	
		currentDoc.add(c);
	}	
	// ---- calls currentDoc.remove()
	void backSpace() {
		currentDoc.remove();
	}
	// ---- calls currentDoc.moveRight()
	void moveRight() {
		currentDoc.moveRight();
	}
	// ---- calls currentDoc.moveLeft()
	void moveLeft() {
		currentDoc.moveLeft();
	}
	// ---- calls currentDoc.Up()
	void moveUp() {
		currentDoc.moveUp();
	}
	// ---- calls currentDoc.moveDown()
	void moveDown() {
		currentDoc.moveDown();
	}
	// ---- calls currentDoc.undo()
	void undo() {
		currentDoc.undo();
	}
	// ---- calls currentDoc.redo()
	void redo() {
		currentDoc.redo();
	}

	/**
	 * quitEditor  user quits
	 */
	void  quitEditor() 
	{ 
		System.exit(0); 
	}	
}






