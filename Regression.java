import java.util.ArrayList;
import Jama.Matrix;

/**
 * This class is for building polynomial regression model
 * 
 * @author DarsalBrdzeni
 */
public class Regression {
	/** Instance Variables */
	private ArrayList<Double> X_train = new ArrayList<>();
	private ArrayList<Double> y_train = new ArrayList<>();
	private ArrayList<Double> X_test = new ArrayList<>();
	private ArrayList<Double> y_test = new ArrayList<>();

	/**
	 * Gets every essential data for the model
	 * 
	 * @param X_train
	 *            Data of Doubles' with the model must be created (Independent
	 *            Variable)
	 * @param y_train
	 *            Data of Doubles' with the model must be created (Dependent
	 *            Variable)
	 * @param X_test
	 *            Data of Doubles' for testing (Independent Variable)
	 * @param y_test
	 *            Data of Doubles' for testing (Dependent Variable)
	 */
	public Regression(ArrayList<Double> X_train, ArrayList<Double> y_train, ArrayList<Double> X_test,
			ArrayList<Double> y_test) {
		this.X_train = X_train;
		this.X_test = X_test;
		this.y_train = y_train;
		this.y_test = y_test;
	}

	/**
	 * Summing up everything in the ArrayList
	 * 
	 * @param x
	 *            ArrayList of Doubles'
	 * @param pow
	 *            in which power do you want to sum them
	 * @return sum
	 */
	private double sum(ArrayList<Double> x, int pow) {
		double sum = 0.0;
		for (double i : x) {
			sum += Math.pow(i, pow);
		}
		return sum;
	}

	/**
	 * Calculates R^2 which shows how close the points are to the model Value is
	 * between 0-1 and closer to 1 is better
	 * 
	 * @param y_pred
	 *            ArrayList of the data which comes from using X_train data on
	 *            the model
	 * @return R^2
	 */
	private double calcRSquared(ArrayList<Double> y_pred) {
		double SSR = 0.0;
		double SST = 0.0;
		double mean = sum(y_train, 1) / y_train.size();
		double rSquared = 0.0;

		for (int i = 0; i < y_train.size(); i++) {
			SSR += Math.pow(y_train.get(i) - y_pred.get(i), 2);
		}

		for (double i : y_train) {
			SST += Math.pow(i - mean, 2);
		}

		rSquared = 1 - SSR / SST;

		return rSquared;
	}

	/**
	 * Finds the difference between two doubles
	 * 
	 * @param test
	 *            first double
	 * @param now
	 *            second double
	 * @return difference
	 */
	private double errorMarginCalc(double test, double now) {
		double e = 0.0;
		if (test - now > 0) {
			e = test - now;
		} else {
			e = now - test;
		}
		return e;
	}

	/**
	 * It calculates polynomial function's value for specific X value
	 * 
	 * @param coef
	 *            coefficients of the function
	 * @param x
	 *            Independent Variable
	 * @return Function's value
	 */
	public static double calcFunc(ArrayList<Double> coef, double x) {
		double sum = 0.0;
		for (int i = 0; i < coef.size(); i++) {
			sum += coef.get(i) * Math.pow(x, i);
		}
		return sum;
	}

	/**
	 * Creates X_train matrix for least square method
	 * 
	 * @param i
	 *            power of the polynomial model
	 * @return Matrix
	 */
	private Matrix xMatrix(int i) {
		double[][] matrix = new double[i + 1][i + 1];
		matrix[0][0] = X_train.size();
		for (int j = 0; j < matrix.length; j++) {
			for (int k = 0; k < matrix[j].length; k++) {
				if (!(j == 0 && k == 0)) {
					matrix[j][k] = sum(X_train, j + k);
				}
			}
		}

		Matrix m = new Matrix(matrix);
		return m;
	}

	/**
	 * Creates y_train matrix for least square method
	 * 
	 * @param i
	 *            power of the polynomial model
	 * @return Matrix
	 */
	private Matrix yMatrix(int i) {
		double[] y = new double[i + 1];
		for (int j = 0; j < y.length; j++) {
			ArrayList<Double> arrayList = new ArrayList<>();
			for (int k = 0; k < X_train.size(); k++) {
				arrayList.add(Math.pow(X_train.get(k), j) * y_train.get(k));
			}
			y[j] = sum(arrayList, 1);
		}
		Matrix yM = new Matrix(y, i + 1);
		return yM;
	}

	/**
	 * Builds polynomial regression model
	 * 
	 * @param i
	 *            power of the polynomial model
	 * @param bounds
	 *            Varargs that shows if the model has lowest and highest points
	 *            and if it so, user can also input to what they want to change
	 *            those points that exceed the graph.
	 * 
	 *            For Instance: polynomialModel(power, lowest, highest,
	 *            lowChange, highChange); if lowChange and highChange are null
	 *            than values that exceed the bounds will become the bounds
	 * @return RegressionFunction 
	 */
	public RegressionFunction polynomialModel(int i, Double... bounds) {
		ArrayList<Double> y_pred = new ArrayList<>();
		ArrayList<Double> coef = new ArrayList<>();

		Matrix xM = xMatrix(i);

		Matrix yM = yMatrix(i);

		Matrix answer = xM.solve(yM);

		for (int j = 0; j < i + 1; j++) {
			coef.add(answer.get(j, 0));
		}

		for (double x : X_train) {
			y_pred.add(calcFunc(coef, x));
		}

		double next = calcFunc(coef, X_test.get(0));

		if (bounds.length > 0) {
			Double low = bounds[0];

			Double lowChange = bounds.length > 2 ? bounds[2] : null;

			Double high = bounds.length > 1 ? bounds[1] : null;
			Double highChange = bounds.length > 3 ? bounds[3] : null;

			if (next < low) {
				if (lowChange == null) {
					next = low;
				} else {
					next = lowChange;
				}

			} else if (next > high) {
				if (highChange == null) {
					next = high;
				} else {
					next = highChange;
				}
			}
		}
		double mean = sum(y_test, 1) / y_test.size();
		
		double nowMargin = errorMarginCalc(mean, next);
		double rSquared = calcRSquared(y_pred);
		RegressionFunction function = new RegressionFunction(coef, nowMargin, rSquared);
		return function;
	}

}
