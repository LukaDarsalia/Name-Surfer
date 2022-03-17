
/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;


import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class NameSurferGraphExt extends GCanvas implements NameSurferConstantsExt, ComponentListener {

	private static final int TEXTMARGIN = 5;
	
	/** Saves every entry of drawn graph*/
	private ArrayList<NameSurferEntryExt> data = new ArrayList<>();
	
	/** GOvals and its labels for hover method */
	private Map<GOval, GLabel> labels = new HashMap<>();
	private Map<NameSurferEntryExt, Integer> predicted = new HashMap<>();
	
	/** If hover method is not on, its true, otherwise false*/
	private boolean isNotSelected = true;
	
	private static final int fontSize = 14;

	/**
	 * Creates a new NameSurferGraph object that displays the data.
	 */
	public NameSurferGraphExt() {
		addComponentListener(this);
		// You fill in the rest //

	}

	/**
	 * Clears the list of name surfer entries stored inside this class.
	 */
	public void clear() {
		// You fill this in //
		removeAll();
		data = new ArrayList<>();
		update();
	}

	/* Method: addEntry(entry) */
	/**
	 * Adds a new NameSurferEntryExt to the list of entries on the display. Note
	 * that this method does not actually draw the graph, but simply stores the
	 * entry; the graph is drawn by calling update.
	 */
	public void addEntry(NameSurferEntryExt entry) {
		// You fill this in //
		if(data.contains(entry)==false){
			data.add(entry);
		}
	}
	
	/**
	 * Draws background of the canvas
	 */
	private void drawBackground(){
		for (int i = 0; i < NDECADES; i++) {
			GLine line = new GLine(i * getWidth() / NDECADES, 0, i * getWidth() / NDECADES, getHeight());
			int year = 1900 + i * 10;
			GLabel txt = new GLabel("" + year);
			
			int size = labelResize(txt);
			
			txt.setFont(new Font("Baskerville", Font.PLAIN, size));
			add(txt, i * getWidth() / NDECADES + TEXTMARGIN, getHeight() - txt.getDescent());
			add(line);
		}
		GLine top = new GLine(0, GRAPH_MARGIN_SIZE, getWidth(), GRAPH_MARGIN_SIZE);
		add(top);
		GLine bottom = new GLine(0, getHeight() - GRAPH_MARGIN_SIZE, getWidth(), getHeight() - GRAPH_MARGIN_SIZE);
		add(bottom);
	}

	/**
	 * Updates the display image by deleting all the graphical objects from the
	 * canvas and then reassembling the display according to the list of
	 * entries. Your application must call update after calling either clear or
	 * addEntry; update is also called whenever the size of the canvas changes.
	 */
	public void update() {
		// You fill this in //
		removeAll();
		
		drawBackground();
		for (NameSurferEntryExt e : data) {
			
			drawGraphs(e, data.indexOf(e), isNotSelected);
			if(predicted.containsKey(e)){
				drawPredict(e, predicted.get(e), data.indexOf(e));
			}
		}
	}

	/**
	 * Pre Condition - User has entered a name
	 * 
	 * Post Condition - Graph is displayed according to the name's data
	 * 
	 * @param e
	 *            entry that consists of the name and the data
	 * @param n
	 *            which entry is drawing
	 */
	private void drawGraphs(NameSurferEntryExt e, int n, boolean labelVisible) {
		for (int i = 0; i < NDECADES - 1; i++) {
			double y = getHeight() - (GRAPH_MARGIN_SIZE);
			double nextY = y;
			if (e.getRank(i) != 0) {
				y = (GRAPH_MARGIN_SIZE) + (e.getRank(i) * (getHeight() - 2 * GRAPH_MARGIN_SIZE) / MAX_RANK);
			}
			if (e.getRank(i + 1) != 0) {
				nextY = (GRAPH_MARGIN_SIZE) + (e.getRank(i + 1) * (getHeight() - 2 * GRAPH_MARGIN_SIZE) / MAX_RANK);
			}

			drawTxt(e, i, y, labelVisible);
			if (i == NDECADES - 2) {
				drawTxt(e, i + 1, nextY, labelVisible);
			}

			GLine line = new GLine(i * getWidth() / NDECADES, y, (i + 1) * getWidth() / NDECADES, nextY);
			line.setColor(linePainter(n));
			add(line);
		}
	}

	/**
	 * Pre Condition - Graph is displayed
	 * 
	 * Post Condition - Labels are displayed
	 * 
	 * @param e
	 *            entry which labels must be displayed
	 * @param i
	 *            on which line's label must be painted(starting from zero)
	 * @param y
	 *            coordinate of the label
	 */
	private void drawTxt(NameSurferEntryExt e, int i, double y, boolean visible) {
		String string = e.getName().substring(0,1).toUpperCase() + e.getName().substring(1).toLowerCase() + " *";
		if (e.getRank(i) != 0) {
			string = e.getName().substring(0,1).toUpperCase() + e.getName().substring(1).toLowerCase() + " " + e.getRank(i);
		}
		GLabel txt = new GLabel(string);
		int size = labelResize(txt);
		txt.setFont(new Font("Baskerville", Font.PLAIN, size));
		double radius = txt.getWidth() / 2;
		GOval circle = new GOval(radius, radius);
		labels.put(circle, txt);
		circle.setVisible(false);
		circle.sendForward();
		add(circle, i * getWidth() / NDECADES - radius / 2, y - radius / 2);
		if (isNotSelected == false) {
			onHoverLabels(circle);
		}

		if ((e.getRank(i) < e.getRank(i + 1)) || (e.getRank(i) > e.getRank(i + 1) && e.getRank(i + 1) == 0)
				|| e.getRank(i) == e.getRank(i + 1) || i + 1 == NDECADES) {
			add(txt, i * getWidth() / NDECADES + TEXTMARGIN, y - txt.getDescent() - TEXTMARGIN);
		} else {
			add(txt, i * getWidth() / NDECADES + TEXTMARGIN, y + txt.getAscent() + TEXTMARGIN);
		}
		txt.setVisible(visible);
	}
	
	/**
	 * Finds font size that can fit in the gaps of vertical lines 
	 * @param txt GLabel
	 * @return Font size
	 */
	private int labelResize(GLabel txt) {
		int size = fontSize;
		while(getWidth()/NDECADES<txt.getWidth()+TEXTMARGIN){
			size--;
			txt.setFont("-"+size);
		}
		return size;
	}
	
	/**
	 * Pre Condition - User chose that he wants the Labels to appear on hover
	 * 
	 * Post Condition - and they behave that way
	 * @param circle
	 */
	private void onHoverLabels(GOval circle) {
		circle.addMouseListener(new MouseAdapter() {
			private GObject ent;
			public void mouseEntered(MouseEvent e) {
				if (getElementAt(e.getX(), e.getY()).toString().substring(0, 5).equals("GOval")) {
					labels.get(getElementAt(e.getX(), e.getY())).setVisible(true);
					ent = getElementAt(e.getX(), e.getY());
				}

			}
			public void mouseExited(MouseEvent e) {
				if (ent != null) {
					labels.get(ent).setVisible(false);
				}

			}

		});

	}

	/**
	 * It is setter for isNotSelected
	 * @param hide
	 */
	public void toggleTheLabels(boolean hide) {
		isNotSelected = hide;
		update();
	}

	

	/**
	 * It is getter for isNotSelected
	 * @return isNotSelected boolean
	 */
	public boolean getSelected() {
		return !isNotSelected;
	}

	/**
	 * Pre Condition - Graph is displayed
	 * 
	 * Post Condition - Graph is painted
	 * 
	 * @param n
	 *            which graph is displayed
	 * @return Color according to n
	 */
	private Color linePainter(int n) {
		switch (n % 4) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.RED;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.YELLOW;
		}
		return null;
	}

	/**
	 * Getter for array of all graphs that are drawn
	 * @return ArrayList of graphs
	 */
	public ArrayList<NameSurferEntryExt> getAllGraphs() {
		return data;
	}

	/**
	 * Setter for array of all graphs that are drawn
	 * @param d ArrayList of graphs
	 */
	public void setAllGraphs(ArrayList<NameSurferEntryExt> d) {
		data = d;
		update();
	}
	
	/**
	 * Pre Condition - User has selected predict check box
	 * 
	 * Post Condition - Corresponding graph is drawn with its label
	 * @param e NameSurferEntryExt 
	 * @param rank of the predicted graph part
	 * @param index on which index the graph is in all the graphs data
	 */
	private void drawPredict(NameSurferEntryExt e, int rank, int index){
		double y = getHeight() - (GRAPH_MARGIN_SIZE);
		if (e.getRank(NDECADES-1) != 0) {
			y = (GRAPH_MARGIN_SIZE) + (e.getRank(NDECADES-1) * (getHeight() - 2 * GRAPH_MARGIN_SIZE) / MAX_RANK);
		}
		
		double nextY = y;
		
		if (rank != 0) {
			nextY = (GRAPH_MARGIN_SIZE) + (rank * (getHeight() - 2 * GRAPH_MARGIN_SIZE) / MAX_RANK);
		}
		
		GLine line = new GLine(getWidth()-getWidth()/NDECADES, y, getWidth(), nextY);
		line.setColor(linePainter(index));
		add(line);
		String name=e.getName().substring(0,1).toUpperCase()+e.getName().substring(1).toLowerCase() ;
		String txt=name + " *";
		if(rank!=0){
			txt=name+" "+rank;
		}
		GLabel label =new GLabel(txt);
		int size = labelResize(label);
		label.setFont(new Font("Baskerville", Font.PLAIN, size));
		add(label, getWidth()-label.getWidth(),nextY);
		
	}
	
	/**
	 * Adds specific graph to predicted map
	 * @param e
	 * @param rank
	 */
	public void setPredicted(NameSurferEntryExt e, int rank) {
		predicted.put(e, rank);
		update();
	}
	
	/**
	 * Getter for predicted map
	 * @return Map
	 */
	public Map<NameSurferEntryExt, Integer> getPredicted() {
		return predicted;
	}
	
	/**
	 * Pre Condition - User has unchecked predict check box
	 * 
	 * Post Condition - graph is removed from predicted map
	 * @param e graph
	 */
	public void unPredict(NameSurferEntryExt e) {
		predicted.remove(e);
		update();
	}

	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		update();
	}

	public void componentShown(ComponentEvent e) {
	}

	
}
