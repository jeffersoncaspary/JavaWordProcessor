/**
 * Authors: Ben Caspary and Harrison Barrett
 * 
 * CSC 335, Project 2: Lil Lexi
 * 
 * File name: LilLexi.java
 * 
 * Files Used: LilLexiUI.java, LilLexiDoc.java, LilLexiControl.java
 */

package UI;

/**
 * Lil Lexi Document Editor
 * 
 */

public class LilLexi
{
	static private LilLexiDoc currentDoc = null;

	public static void main(String args[])
	{		
		if (currentDoc == null)
			currentDoc = new LilLexiDoc();
		LilLexiUI lexiUI = new LilLexiUI();
		lexiUI.setCurrentDoc( currentDoc );
		lexiUI.setToWindows();  // change to Mac if running on a Mac 
		currentDoc.setUI(lexiUI);
		LilLexiControl lexiControl = new LilLexiControl( currentDoc );
		lexiUI.setController( lexiControl );
		lexiUI.start();
	} 
}