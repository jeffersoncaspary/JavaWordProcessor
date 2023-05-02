/**
 * Authors: Ben Caspary and Harrison Barrett
 * 
 * CSC 335, Project 2: Lil Lexi
 * 
 * File name: LilLexiDoc.java
 * 
 * Files Used: LilLexiUI.java, java.util
 * 
 * Files Used In: LilLexiUI.java, LilLexi.java, LilLexiControl.java
 */

package UI;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.*;

import org.eclipse.swt.widgets.Canvas;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.util.ArrayList;


/**
 * ---- LilLexiDoc class
 * 
 * This class creates a Lil Lexi document that keeps,
 * stores, and modifies all of the data for the individual document.
 * Its main functionalities include writing to and editing the document.
 */
public class LilLexiDoc 
{
	private LilLexiUI ui; // ---- 
	private List<Glyph> glyphs;
	public int index, row, col;
	private List<Integer> rowSizes;
	private List<String> dictionary;
	private Stack<String> undo;	
	private Stack<String> redo;	
	private int stackFlag;
	/**
	 * Constructor
	 */
	public LilLexiDoc() 
	{
		glyphs = new ArrayList<Glyph>();
		index = 0;
		row = 0;
		col = 0;
		rowSizes = new ArrayList<>();
		rowSizes.add(0);
		dictionary = this.createDictionary("words");
		stackFlag = 0;
		undo = new Stack<>(); 
		redo = new Stack<>(); 
	}
	
	/**
	 * setUI
	 */
	public void setUI(LilLexiUI ui) {this.ui = ui;}

	/**
	 * add a char
	 */
	public void add(char c) 
	{
		if(index<0) {
			index = 0;
		}
		glyphs.add(index, new Glyph(c));
		glyphs.get(glyphs.size()-1).setIndex(index);
		glyphs.get(glyphs.size()-1).setRow(row);
		glyphs.get(glyphs.size()-1).setCol(col);
		
		System.out.printf("Char: %c\nIndex: %d\nRow: %d\nCol: %d\n", c, index, row, col);
		System.out.println();
		
		index++;
		
		if(c == '\n') {
			rowSizes.add(0);
			row++;
			col = 0;
		}
		else if (col == 39) {
			rowSizes.add(0);
			row++;
			col = 0;
		}
		else {
			rowSizes.set(row, rowSizes.get(row)+1);
			col++;
		}
		ui.updateUI();
		if (stackFlag == 0) {
			char added = glyphs.get(index-1).getChar();
			undo.add("added " + added);}
	}
	
	public void remove() {
		char removed;
		if (glyphs.size() != 0) {
			removed = glyphs.get(index-1).getChar();
			glyphs.remove(index-1);
			index--;
			if(rowSizes.get(row) == 0) {
			rowSizes.remove(row);
			row--;
			col = rowSizes.get(row);
			}
			else if(col == 0) {
			row--;
			col = 0;
			}
			else {
			rowSizes.set(row, rowSizes.get(row)-1);
			col--;
			}
			if (stackFlag == 0) {
			undo.add("removed " + removed);
			}
			ui.updateUI();
		}
	}
	
	public void moveRight() {
		if (index + 1 <= glyphs.size()) 
			{
			if (stackFlag == 0) {
				undo.add("moved right");}
			index++;
			if(col == rowSizes.get(row)) {
				row++;
				col = 0;
			}
			else
				col++;
		}
	}
	
	public void moveLeft() {
		if (index - 1 >= 0) 
			{
			if (stackFlag == 0) {
				undo.add("moved left");}
			index--;
			if(col == 0) {
				row--;
				col = rowSizes.get(row);	
			}
			else
				col--;
		}
	}
	//s
	public void moveUp() {
		if (row == 0)
			index = 0;
		else {
			if (stackFlag == 0) {
				undo.add("moved up");}
			int prevRowIndex = rowSizes.get(row-1);
			int rowIndex = rowSizes.get(row);
			if (rowIndex > prevRowIndex) {
				index -= (rowIndex + 1);
				col = prevRowIndex;
			}
			else
				index -= (prevRowIndex+1);
			row--;
		}
	}
	
	public void moveDown() {
		if (row == rowSizes.size()-1) {
			index = glyphs.size();
			col = rowSizes.get(row);
		}
		else {
			if (stackFlag == 0) {
				undo.add("moved down");}
			for(int i = index; i < glyphs.size(); i++) {
				if(i >= 0 && i < glyphs.size() ) {
					if (col == glyphs.get(i).getCol()) {
						row++;
						break;
					}
					index++;
			}}
			System.out.println(index);
		}
	}
	
	public List<String> createDictionary(String file_name){
		ArrayList<String> buildDictionary = new ArrayList<String>(10000);
		File file = new File(file_name);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			buildDictionary.add(line);
			}
		return buildDictionary;
	}
	
	public boolean spellCheck(String word) {
		for(int i = 0; i < dictionary.size(); i++) {
			if(word.toLowerCase().equals(dictionary.get(i).toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	public List<List<Integer>> getWordIndexes() {
		List<List<Integer>> wordInd = new ArrayList<List<Integer>>();
		List<Integer> temp = new ArrayList<Integer>();
		for (Glyph g: glyphs) {
			if(g.getChar() != '\n' && g.getChar() != ' ')
				temp.add(g.getIndex());
			else {
				wordInd.add(temp);
				temp = new ArrayList<Integer>();
			}
		}
		return wordInd;
	}
	
	public void setRLtoChars() {
		List<List<Integer>> pos = this.getWordIndexes();
		for (int i = 0; i < pos.size(); i++) {
			String word = "";
			for (int j = 0; j < pos.get(i).size(); j++)
				word += glyphs.get(pos.get(i).get(j)).getChar();
			if (!this.spellCheck(word)) {
				for (int j = 0; j < pos.get(i).size(); j++)
					glyphs.get(pos.get(i).get(j)).setRedLine(true);
			}
			else {
				for (int j = 0; j < pos.get(i).size(); j++)
					glyphs.get(pos.get(i).get(j)).setRedLine(false);
			}
		}
	}
	
	public void undo() {
		if(undo.isEmpty() == false) {
			stackFlag = 1;
			String action = undo.pop();
			if(action.length() >= 7) {
				if(action.substring(0, 6).equals("added ")) {
					char removed = action.charAt(6);
					this.remove();
					redo.add("removed " + removed);
				}
			}
			if(action.length() >= 9) {
				if(action.substring(0, 8).equals("removed ")) {
					this.add(action.charAt(8));
					redo.add("added " + action.charAt(8));
				}
				if(action.substring(0, 8).equals("moved up")) {
					this.moveDown();
					redo.add("moved down");
				}
			}
			if(action.length() >= 10) {
				if(action.substring(0, 10).equals("moved down")) {
					this.moveUp();
					redo.add("moved up");
				}
				if(action.substring(0, 10).equals("moved left")) {
					this.moveRight();
					redo.add("moved right");
				}
			}
			if(action.length() >= 11) {
				if(action.substring(0, 11).equals("moved right")) {
					this.moveLeft();
					redo.add("moved left");
				}
			}
		}
		stackFlag = 0;
		ui.updateUI();
	}
	
	public void redo() {
		if(redo.isEmpty() == false) {
			stackFlag = 1;
			String action = redo.pop();
			if(action.length() >= 7) {
				
				if(action.substring(0, 6).equals("added ")) {
					System.out.println("yooo");
					char removed = action.charAt(6);
					this.remove();
					undo.add("removed " + removed);
				}
			}
			if(action.length() >= 9) {
				if(action.substring(0, 8).equals("removed ")) {
					this.add(action.charAt(8));
					undo.add("added " + action.charAt(8));
				}
				if(action.substring(0, 8).equals("moved up")) {
					this.moveDown();
					undo.add("moved down");
				}
			}
			if(action.length() >= 10) {
				if(action.substring(0, 10).equals("moved down")) {
					this.moveUp();
					undo.add("moved up");
				}
				if(action.substring(0, 10).equals("moved left")) {
					this.moveRight();
					undo.add("moved right");
				}
			}
			if(action.length() >= 11) {
				if(action.substring(0, 11).equals("moved right")) {
					this.moveLeft();
					undo.add("moved left");
				}
			}
		}
		stackFlag = 0;
		ui.updateUI();
	}
	
	/**
	 * gets
	 */
	public List<Glyph> getGlyphs(){return glyphs;}
	
	public void resetDoc(){
		glyphs = new ArrayList<Glyph>();
		index = 0;
		row = 0;
		col = 0;
		rowSizes = new ArrayList<>();
		rowSizes.add(0);
		dictionary = this.createDictionary("words");
		stackFlag = 0;
		undo = new Stack<>(); 
		redo = new Stack<>(); 
	}
}

/**
 * Glyph
 */
class Glyph 
{
	private char c;
	private boolean redLine, cursor;
	private int index, row, col;
	
	public Glyph(char c) 
	{
		this.c = c;
		this.redLine = false;
		this.cursor = true;
	}

	public char getChar() {return c;}
	public void setChar(char c) {this.c = c;}
	
	public int getIndex() {return index;}
	public void setIndex(int i) {this.index = i;}
	
	public int getRow() {return row;}
	public void setRow(int row) {this.row = row;}
	
	public int getCol() {return col;}
	public void setCol(int col) {this.col = col;}
	
	public boolean getCursor() {return this.cursor;}
	public void setCursor(boolean cursor) {this.cursor = cursor;}
	
	public boolean getredLine() {return redLine;}
	public void setRedLine(boolean rl) {this.redLine = rl;}
	
}






