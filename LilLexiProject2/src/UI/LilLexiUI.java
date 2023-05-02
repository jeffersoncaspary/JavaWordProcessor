/**
 * Authors: Ben Caspary and Harrison Barrett
 * 
 * CSC 335, Project 2: Lil Lexi
 * 
 * File name: LilLexiUI.java
 * 
 * Files Used: LilLexiDoc.java, LilLexiControl.java, java.util
 * 
 * Files Used In: LilLexiDoc.java, LilLexi.java
 */

package UI;

import java.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;

import java.util.List;


/* ---- LilLexiUI Class
 * 
 * This class acts as a UI for a Lil Lexi Document. It uses multiple 
 * objects. They all are used to create a controllable UI of a 
 * LilLexiDoc object. This UI is designed to act as an editable document
 * with multiple abilities:
 *  - scrolling
 *  - writing to the canvas with abilities to
 *  	- write characters (including space and new line)
 *  	- delete characters
 *  	- shift left, right, up, or down through characters
 *  	- press 'enter' to go to new line
 *  	- undo previous action
 *  	- redo previous undone action
 *  - select from a list of items in a menu containing
 *  	- File (save, create new, or exit file)
 *  		> Save
 *  		> New
 *  		> Exit
 *  	- Edit 
 *  		> Undo
 *  		> Redo
 *    	- Insert (select and place onto canvas by click)
 *  		> Image (Apple, Chicken, Duck)
 *  		> Rectangle(Wide Rectangle, Square, Tall Rectangle)
 *  	- Help
 *  		> Get Help
 */
public class LilLexiUI {
	private LilLexiDoc currentDoc;
	private LilLexiControl lexiControl;
	private Display display;
	private Shell shell;
	private Label statusLabel;
	private Canvas canvas;
	// ---- should be set to true for MacBook users in LilLexi.java.main
	private boolean isMac; 
	private boolean isClicked;
	private int imageIndex = 0;
	private int shapeIndex = 0;

	/**
	 * Constructor
	 */
	public LilLexiUI() {
		// ---- create the window and the shell
		Display.setAppName("Lil Lexi");
		display = new Display();
		shell = new Shell(display);
		shell.setText("Lil Lexi");
		shell.setSize(800, 900);
		shell.setLayout(new GridLayout());
		isMac = false;
		isClicked = false;
		imageIndex = 0;
		shapeIndex = 0;

	}

	/**
	 * start the editor
	 */

	public void start() {
		// ---- create widgets for the interface
		Composite upperComp = new Composite(shell, SWT.NO_FOCUS);
		Composite lowerComp = new Composite(shell, SWT.NO_FOCUS);

		// ---- canvas for the document
		canvas = new Canvas(upperComp, SWT.NONE);
		canvas.setSize(800, 700);

		canvas.addPaintListener(e -> {
			Rectangle rect = shell.getClientArea();
			e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(rect.x, rect.y, rect.width, rect.height);

			List<Glyph> glyphs = currentDoc.getGlyphs();
			int row = 0;
			int column = 0;
			for (Glyph g : glyphs) {
				// ---- for drawing red lines under misspelled words
				if(g.getredLine()) {
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
					e.gc.drawLine(column, row+40, column+18, row + 40);
				}
				else {
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					e.gc.drawLine(column, row+10, column+18, row + 10);
				}
				
				// ---- for getting rid of the line at previous positions
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawLine(column, row+40, column, row + 10);
				
				// ---- sets size, font, and color of characters
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
				Font font = new Font(display, "Courier", 12, SWT.BOLD);
				e.gc.setFont(font);
				
				// ---- for newLine
				if (g.getChar() == '\n') {
					column = 0;
					row += 32;
				} 
				else {
					// ---- draws glyph to canvas
					e.gc.drawString("" + g.getChar(), column, row + 10);
					// ---- sets column of where to draw next glyph
					column = (column + 18) % (40 * 18);
					// ---- sets row to next row if glyph is at the new line
					if (column == 0)
						row += 32;
				}
				
			}
			// ---- draws cursor to canvas
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			e.gc.drawLine(column, row+40, column, row + 10);
		});

		// ---- implements mouse listener object
		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
			}
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		// ---- implements key listener object
		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {

				// ---- removes previous glyph if 'backspace' is pressed
				if (e.keyCode == 8)
					lexiControl.backSpace();
				// ---- goes to new line if 'enter' is pressed
				else if (e.keyCode == 13) {
					lexiControl.add('\n');
				}
				// ---- undoes the previous action if 'control' is pressed
				else if (e.keyCode == 4194304)
					lexiControl.undo();
				// ---- calls 'lexiControl.moveRight() if 'right arrow' is pressed
				else if (e.keyCode == 16777220)
					lexiControl.moveRight();
				// ---- calls 'lexiControl.moveLeft() if 'left arrow' is pressed
				else if (e.keyCode == 16777219)
					lexiControl.moveLeft();
				// ---- calls 'lexiControl.moveDown() if 'down arrow' is pressed
				else if (e.keyCode == 16777218)
					lexiControl.moveDown();
				// ---- calls 'lexiControl.moveRight() if 'up arrow' is pressed
				else if (e.keyCode == 16777217)
					lexiControl.moveUp();
				// ---- calls 'lexiControl.add() if any other glyph excluding 'shift' is pressed
				else if (e.keyCode != 131072)
					lexiControl.add(e.character);
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});
		Slider slider = new Slider(canvas, SWT.VERTICAL);
		Rectangle clientArea = canvas.getClientArea();
		slider.setBounds(clientArea.width - 40, clientArea.y + 10, 32, clientArea.height);
		slider.setLayoutData(canvas.getLayoutData());
		slider.addListener(SWT.Selection, event -> {
			String string = "SWT.NONE";
			switch (event.detail) {
			case SWT.DRAG:
				string = "SWT.DRAG";
				canvas.scroll(0, -10, 0, 0, 700, 800, false);
				break;
			case SWT.HOME:
				string = "SWT.HOME";
				break;
			case SWT.END:
				string = "SWT.END";
				break;
			case SWT.ARROW_DOWN:
				string = "SWT.ARROW_DOWN";
				break;
			case SWT.ARROW_UP:
				string = "SWT.ARROW_UP";
				break;
			case SWT.PAGE_DOWN:
				string = "SWT.PAGE_DOWN";
				break;
			case SWT.PAGE_UP:
				string = "SWT.PAGE_UP";
				break;
			}
		});

		// ---- status label 
		lowerComp.setLayout(new RowLayout());
		statusLabel = new Label(lowerComp, SWT.NONE);

		FontData[] fD = statusLabel.getFont().getFontData();
		fD[0].setHeight(24);
		statusLabel.setFont(new Font(display, fD[0]));
		statusLabel.setText("Ready to edit!");
		statusLabel.setSize(1000, 1000);

		// ---- main menu
		Menu menuBar, fileMenu, insertMenu, helpMenu, editMenu, imageMenu, shapeMenu;
		MenuItem fileMenuHeader, insertMenuHeader, helpMenuHeader, fileExitItem, fileSaveItem, helpGetHelpItem,
				editMenuHeader;
		MenuItem insertImageItem, insertRectItem, editUndoItem, editRedoItem, fileNewItem;

		menuBar = new Menu(shell, SWT.BAR);

		fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("File");
		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
		fileSaveItem.setText("Save");
		fileNewItem = new MenuItem(fileMenu, SWT.PUSH);
		fileNewItem.setText("New");
		fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("Exit");

		editMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		editMenuHeader.setText("Edit");
		editMenu = new Menu(shell, SWT.DROP_DOWN);
		editMenuHeader.setMenu(editMenu);

		editUndoItem = new MenuItem(editMenu, SWT.PUSH);
		editUndoItem.setText("Undo");
		editRedoItem = new MenuItem(editMenu, SWT.PUSH);
		editRedoItem.setText("Redo");

		insertMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		insertMenuHeader.setText("Insert");
		insertMenu = new Menu(shell, SWT.DROP_DOWN);
		insertMenuHeader.setMenu(insertMenu);

		insertImageItem = new MenuItem(insertMenu, SWT.CASCADE);
		insertImageItem.setText("Image");
		imageMenu = new Menu(shell, SWT.DROP_DOWN);
		shapeMenu = new Menu(shell, SWT.DROP_DOWN);
		insertImageItem.setMenu(imageMenu);
		insertRectItem = new MenuItem(insertMenu, SWT.CASCADE);
		insertRectItem.setText("Rectangle");
		insertRectItem.setMenu(shapeMenu);

		// pushes images onto the screen
		// adds menuItems
		MenuItem apple = new MenuItem(imageMenu, SWT.PUSH);
		apple.setText("apple");
		MenuItem chicken = new MenuItem(imageMenu, SWT.PUSH);
		chicken.setText("Chicken");
		MenuItem duck = new MenuItem(imageMenu, SWT.PUSH);
		duck.setText("Duck");

		//
		MenuItem wideRectangle = new MenuItem(shapeMenu, SWT.PUSH);
		wideRectangle.setText("Wide Rectangle");
		MenuItem square = new MenuItem(shapeMenu, SWT.PUSH);
		square.setText("Square");
		MenuItem tallRectangle = new MenuItem(shapeMenu, SWT.PUSH);
		tallRectangle.setText("Tall Rectangle");

		helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("Help");
		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader.setMenu(helpMenu);

		helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
		helpGetHelpItem.setText("Get Help");

		editUndoItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				lexiControl.undo();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				lexiControl.undo();
			}
		});
		editRedoItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				lexiControl.redo();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				lexiControl.redo();
			}
		});

		editRedoItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				lexiControl.redo();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				lexiControl.redo();
			}
		});

		fileExitItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				display.dispose();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				shell.close();
				display.dispose();
			}
		});
		fileNewItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				newDoc();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				newDoc();
			}
		});
		fileSaveItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		helpGetHelpItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		wideRectangle.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				shapeIndex = 1;
				Rectangle rect = new Rectangle(20, 20, 200, 75);
				paintRectangle(rect, shapeIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				shapeIndex = 1;
				Rectangle rect = new Rectangle(20, 20, 200, 75);
				paintRectangle(rect, shapeIndex);
			}
		});
		
		tallRectangle.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				shapeIndex = 2;
				Rectangle rect = new Rectangle(20, 20, 75, 200);
				paintRectangle(rect, shapeIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				shapeIndex = 2;
				Rectangle rect = new Rectangle(20, 20, 75, 200);
				paintRectangle(rect, shapeIndex);
			}
		});
		
		square.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				shapeIndex = 3;
				Rectangle rect = new Rectangle(20, 20, 150, 150);
				paintRectangle(rect, shapeIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				shapeIndex = 3;
				Rectangle rect = new Rectangle(20, 20, 150, 150);
				paintRectangle(rect, shapeIndex);
			}
		});
		
		apple.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				imageIndex = 1;
				Image apple = new Image(display, "apple.jpg");
				paintImage(apple, 1);
			}
			public void widgetDefaultSelected(SelectionEvent event) {
				imageIndex = 1;
				Image apple = new Image(display, "apple.jpg");
				paintImage(apple, 1);
				
			}
		});

		chicken.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				imageIndex = 2;
				Image chicken = new Image(display, "chicken.jpg");
				paintImage(chicken, 2);
			}
			public void widgetDefaultSelected(SelectionEvent event) {
				imageIndex = 2;
				Image chicken = new Image(display, "chicken.jpg");
				paintImage(chicken, 2);
			}
		});
		
		duck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				imageIndex = 3;
				Image duck = new Image(display, "duck.jpg");
				paintImage(duck, 3);
			}
			public void widgetDefaultSelected(SelectionEvent event) {
				imageIndex = 3;
				Image duck = new Image(display, "duck.jpg");
				paintImage(duck, 3);
			}
		});

		/*
		 * This snippet of code below seems to crash if we run it on any windows machine
		 * So we added a boolean for the user to pick if they are running this on
		 * windows or mac os.
		 */
		if (this.isMac == true) {
			Menu systemMenu = Display.getDefault().getSystemMenu();
			MenuItem[] mi = systemMenu.getItems();
			mi[0].addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
				}
			});
		}

		shell.setMenuBar(menuBar);

		// ---- event loop
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch()) {
			}
		display.dispose();
	}

	/**
	 * updateUI
	 */
	public void updateUI() {
		currentDoc.setRLtoChars();
		canvas.redraw();
	}
	
	public void paintRectangle(Rectangle rec, int index) {
		statusLabel.setText("Click to place");
		MouseListener ml = new MouseListener() {
			public void mouseDown(MouseEvent e) {
				while (isClicked == false && shapeIndex == index) {
					canvas.addPaintListener(v -> {
						rec.x = e.x;	rec.y = e.y;
						v.gc.setBackground(new Color(e.display, 100, 0, 150));
						v.gc.drawRectangle(rec);
						v.gc.fillRectangle(rec.x, rec.y, rec.width, rec.height);
					});
					canvas.redraw();
					isClicked();
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {}
		};
		canvas.addMouseListener(ml);
		isClicked = false;
		updateUI();
	}
	
	public void paintImage(Image im, int index) {
		statusLabel.setText("Click to place");
		MouseListener ml = new MouseListener() {
			public void mouseDown(MouseEvent e) {
				while (isClicked == false && imageIndex == index) {
					canvas.addPaintListener(v -> {
						v.gc.drawImage(im, e.x, e.y);
					});
					canvas.redraw();
					isClicked();
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {}
		};
		canvas.addMouseListener(ml);
		isClicked = false;
		updateUI();
	}
	
	public void isClicked() {
		isClicked = true;
		this.statusLabel.setText("Ready to type!");
		imageIndex = 0;
		shapeIndex = 0;
	}

	public void newDoc() {
		shell = new Shell(display);
		shell.setText("Lil Lexi");
		shell.setSize(800, 900);
		shell.setLayout(new GridLayout());
		currentDoc.resetDoc();
		canvas.redraw();
		Rectangle rect = shell.getClientArea();
		canvas.addPaintListener(e -> {
			e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(rect.x, rect.y, rect.width, rect.height);
			List<Glyph> glyphs = currentDoc.getGlyphs();
			int row = 0;
			int column = 0;
			for (Glyph g : glyphs) {
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawLine(column, row+42, column, row + 10);
				if(g.getredLine()) {
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
					e.gc.drawLine(column, row+40, column+18, row + 40);
				}
				else {
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					e.gc.drawLine(column, row+10, column+18, row + 10);
				}
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
				Font font = new Font(display, "Courier", 12, SWT.BOLD);
				e.gc.setFont(font);
				if (g.getChar() == '\n') {
					column = 0;
					row += 32;
				} else {
					e.gc.drawString("" + g.getChar(), column, row + 10);
					column = (column + 18) % (40 * 18);
					if (column == 0)
						row += 32;
				}
			}
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			e.gc.drawLine(column, row+42, column, row + 10);
	});
		
	}

	/**
	 * setCurrentDoc
	 */
	public void setCurrentDoc(LilLexiDoc cd) {
		currentDoc = cd;
	}

	/**
	 * setController
	 */
	public void setController(LilLexiControl lc) {
		lexiControl = lc;
	}

	public void removeMouseListener(MouseListener m) {
		canvas.removeMouseListener(m);
	}

	public void setToMac() {
		this.isMac = true;
	}

	public void setToWindows() {
		this.isMac = false;
		return;
	}

}
