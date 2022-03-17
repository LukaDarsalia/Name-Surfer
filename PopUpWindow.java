
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import acm.gui.TablePanel;
import acm.program.Program;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a pop up window for Customization It gets the NameSurferGraphExt and
 * therefore knows which graphs are displayed at that time
 * 
 * @author DarsalBrdzeni
 *
 */
public class PopUpWindow extends Program implements NameSurferConstantsExt {
	/** Instance Variables */
	private NameSurferGraphExt graphics;
	/**
	 * In the North there will be a check box for change labels behavior and in
	 * the center there will be graphs check boxes
	 */
	private JPanel northPanel;
	private TablePanel panel;
	private JCheckBox box;
	private JFrame frame;
	private final int WIDTH = 500;
	private final int HEIGHT = 600;
	private final int HGAP = 30;
	private final int VGAP = 0;
	/** Check boxes names */
	private final String GRAPHDISPLAYLABEL = "Display";
	private final String GRAPHPREDICTIONLABEL = "Pediction";

	/** Becomes false when user will close the window */
	private boolean isOpened = true;
	/** Every drawn graph */
	private ArrayList<NameSurferEntryExt> data;
	/** Map of Display check boxes */
	private Map<JCheckBox, NameSurferEntryExt> check = new HashMap<>();
	/** Map of Predict check boxes */
	private Map<JCheckBox, NameSurferEntryExt> predict = new HashMap<>();
	/** Stores the data of name and its regression function */
	private Map<NameSurferEntryExt, RegressionFunction> regData = new HashMap<>();
	/** Stores the data of name and its accuracy */
	private Map<NameSurferEntryExt, Double> accuracy = new HashMap<>();

	public PopUpWindow(NameSurferGraphExt graphics) {
		/** Initialize NameSurferGraphExt instance variable */
		this.graphics = graphics;
		data = graphics.getAllGraphs();
		forecast();
		/** Create frame and canvas */
		frame = new JFrame();
		northPanel = new JPanel();
		panel = new TablePanel(data.size(), 4, HGAP, VGAP);

		/**
		 * Adds window listener, so the program knows when X button is pressed
		 */
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				isOpened = false;
			}
		});
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.add(p, SOUTH);
		frame.add(northPanel, NORTH);
		frame.add(panel);
		interFace();
		box.addActionListener(this);

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setResizable(true);
	}

	/**
	 * This method creates the regression model for prediction
	 */
	private void forecast() {
		for (NameSurferEntryExt e : data) {
			/**
			 * Because there is no information about names rank which are not in
			 * 1000 (0 rank) model can't be create on them, therefore it's only
			 * possible to predict names next rank if this name doesn't have 0
			 * rank in X_train
			 */
			boolean contains = false;

			ArrayList<Double> X_train = new ArrayList<>();
			ArrayList<Double> y_train = new ArrayList<>();
			ArrayList<Double> X_test = new ArrayList<>();
			ArrayList<Double> y_test = new ArrayList<>();

			/**
			 * Creates X_train and y_train ArrayLists
			 */
			for (int i = 0; i < 10; i++) {
				Double year = (double) (1900 + i * 10);
				X_train.add(year);
				Double rank = (double) e.getRank(i);
				if (rank == 0) {
					contains = true;
					rank = (double) MAX_RANK;
				}
				y_train.add(rank);
			}

			if (contains == false) {
				/**
				 * Creates X_test and y_test Note that they can have 0 as an
				 * rank
				 */
				X_test.add((double) 2000);
				if (e.getRank(10) != 0) {
					y_test.add((double) e.getRank(10));
				} else {
					y_test.add((double) MAX_RANK);
				}

				/** Calculates maximum error margin for test */
				double maxErrorMargin = 0.0;
				if (MAX_RANK - y_test.get(0) > y_test.get(0)) {
					maxErrorMargin = MAX_RANK - y_test.get(0);
				} else {
					maxErrorMargin = y_test.get(0);
				}

				/**
				 * Build the Polynomial model which has fewer error margin and
				 * its R^2 is not too low
				 */
				Regression polReg = new Regression(X_train, y_train, X_test, y_test);
				double max = maxErrorMargin;
				int pow = 0;
				for (int i = 2; i < 41; i++) {
					RegressionFunction func = polReg.polynomialModel(i, (Double) 1.0, (Double) 1000.0, null, null);
					if (func.getErrorMargin() < max && func.getRSquared() > 0.8) {
						max = func.getErrorMargin();
						pow = i;
					}
				}
				RegressionFunction func = polReg.polynomialModel(pow, (Double) 1.0, (Double) 1000.0, null, null);
				regData.put(e, func);
				accuracy.put(e, (100 - 100 * func.getErrorMargin() / maxErrorMargin));
			}
		}
	}

	/**
	 * Pre Condition - Frame and canvas are created
	 * 
	 * Post Condition - check boxes are drawn
	 */
	private void interFace() {
		box = new JCheckBox("Toggle the labels on mouse hover");
		box.setSelected(graphics.getSelected());
		northPanel.add(box);

		for (NameSurferEntryExt e : data) {

			boolean contains = false;

			for (int i = 0; i < NDECADES - 1; i++) {
				if (e.getRank(i) == 0) {
					contains = true;
				}
			}
			JCheckBox graph = new JCheckBox(GRAPHDISPLAYLABEL);

			JLabel name = new JLabel(
					e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1).toLowerCase());

			graph.setSelected(true);
			panel.add(name);
			panel.add(graph);
			if (contains == false) {
				JCheckBox forecast = new JCheckBox(GRAPHPREDICTIONLABEL);
				double percentage = accuracy.get(e);
				percentage = Math.floor(percentage * 10) / 10 - 1;
				JLabel probabillity = new JLabel(percentage + "% Accuracy");
				if (graphics.getPredicted().containsKey(e)) {
					forecast.setSelected(true);
				}

				panel.add(forecast);
				panel.add(probabillity);
				predict.put(forecast, e);
				forecast.addActionListener(this);
			} else {
				JLabel empty = new JLabel("");
				JLabel empty2 = new JLabel("");
				panel.add(empty);
				panel.add(empty2);
			}

			check.put(graph, e);

			graph.addActionListener(this);
		}
	}

	/**
	 * This method is responsible for toggling check boxes. If the top is
	 * toggled than graphics method is called and if graphs check box are
	 * toggled, it will disappear
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == box) {
			graphics.toggleTheLabels(!box.isSelected());
		} else {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			if (checkBox.getActionCommand().equals(GRAPHDISPLAYLABEL)) {
				if (checkBox.isSelected() == true) {
					data.add(check.get(e.getSource()));
				} else {
					data.remove(check.get(e.getSource()));
				}
				graphics.setAllGraphs(data);
			} else if (checkBox.getActionCommand().equals(GRAPHPREDICTIONLABEL)) {
				if (checkBox.isSelected()) {
					double pred = Regression.calcFunc(regData.get(predict.get(checkBox)).getCoef(), 2010);
					if (predict.get(checkBox).getName().equals("j")) {
						label.setVisible(true);
						p.add(label);
						frame.pack();
					}
					if (pred < 1) {
						pred = 1;
					} else if (pred > 1000) {
						pred = 0;
					} else {
						pred = Math.floor(pred);
					}
					graphics.setPredicted(predict.get(checkBox), (int) pred);

				} else {
					if (predict.get(checkBox).getName().equals("j")) {
						label.setVisible(false);
					}
					graphics.unPredict(predict.get(checkBox));
				}

			}

		}
	}

	/**
	 * Checks if the window is opened or not
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private JPanel p = new JPanel();
	private JLabel label = new JLabel("Only one J was born in 2000-2010");
}
