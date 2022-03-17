import java.util.ArrayList;

/**
 * Regression Function saves the data about the regression(coefficients, R^2 and
 * error margin)
 * 
 * @author DarsalBrdzeni
 *
 */
public class RegressionFunction {
	/** Instance Variables */
	private ArrayList<Double> coef = new ArrayList<>();
	private double errorMargin;
	private double rSquared;

	/**
	 * Gets all the data
	 * @param coef
	 * @param errorMargin
	 * @param rSquared
	 */
	public RegressionFunction(ArrayList<Double> coef, double errorMargin, double rSquared) {
		this.coef = coef;
		this.errorMargin = errorMargin;
		this.rSquared = rSquared;
	}

	/**
	 * Getter for coefficients
	 * @return
	 */
	public ArrayList<Double> getCoef() {
		return coef;
	}

	
	/**
	 * Getter for R^2
	 * @return
	 */
	public double getRSquared() {
		return rSquared;
	}

	/**
	 * Getter for error margin
	 * @return
	 */
	public double getErrorMargin() {
		return errorMargin;
	}
}
