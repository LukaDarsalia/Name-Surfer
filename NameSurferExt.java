
/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.program.*;

import java.awt.event.*;
import javax.swing.*;



public class NameSurferExt extends Program implements NameSurferConstantsExt {

	/* Method: init() */
	/**
	 * This method has the responsibility for reading in the data base and
	 * initializing the interactors at the bottom of the window.
	 */

	private JTextField input;
	private JButton graph;
	private PopUpWindow window;
	private JButton clear;
	private JButton customization;
	private NameSurferGraphExt graphics;
	private NameSurferDataBaseExt data = new NameSurferDataBaseExt(NAMES_DATA_FILE);
	

	public void init() {
		// You fill this in, along with any helper methods //
		JLabel label = new JLabel("Name");
		add(label, SOUTH);

		input = new JTextField(10);
		add(input, SOUTH);

		graph = new JButton("Graph");
		add(graph, SOUTH);

		clear = new JButton("Clear");
		add(clear, SOUTH);
		
		customization = new JButton("Customization");
		add(customization, SOUTH);
		input.addActionListener(this);
		addActionListeners();

		graphics = new NameSurferGraphExt();
		add(graphics);
	}

	/* Method: actionPerformed(e) */
	/**
	 * This class is responsible for detecting when the buttons are clicked, so
	 * you will have to define a method to respond to button actions.
	 */
	public void actionPerformed(ActionEvent e) {
		// You fill this in //
		if ((e.getSource() == input || e.getSource() == graph) && input.getText().length() != 0) {
			if (data.findEntry(input.getText().toLowerCase()) != null) {
				graphics.addEntry(data.findEntry(input.getText().toLowerCase()));
				graphics.update();
				input.setText("");
			}

		} else if (e.getSource() == clear) {
			graphics.clear();
           
		}else if(e.getSource() == customization && window==null ){
			window = new PopUpWindow(graphics);

		}else if(e.getSource() == customization && window!=null ){
			if(window.isOpened() == false){
				window = new PopUpWindow(graphics);
			}
		}
	}
	

}
