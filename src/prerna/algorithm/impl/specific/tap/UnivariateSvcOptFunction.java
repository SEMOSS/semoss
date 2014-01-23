/*******************************************************************************
 * Copyright 2013 SEMOSS.ORG
 * 
 * This file is part of SEMOSS.
 * 
 * SEMOSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SEMOSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SEMOSS.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package prerna.algorithm.impl.specific.tap;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.apache.commons.math3.analysis.UnivariateFunction;

import prerna.ui.components.specific.tap.OptimizationOrganizer;

/**
 * Interface representing a univariate real function that is implemented for TAP service optimization functions.
 */
public abstract class UnivariateSvcOptFunction implements UnivariateFunction{

	public ServiceOptimizer lin;
	
	double cnstC, cnstK;
	public int totalYrs;
	double sdlcSusPerc;//% of sdlc needed to sustain a service
	double icdSusCost, icdMt, serMain;
	public double[] learningConstants;
	public Object[][] ORIGicdSerMatrix;
	int count = 0;
	double hourlyRate;
	public Hashtable ORIGserCostHash = new Hashtable();
	public ArrayList<String> ORIGicdLabels = new ArrayList<String>();
	public ArrayList<String> ORIGserLabels = new ArrayList<String>();
	public double attRate, hireRate, infRate, disRate;
	JTextArea consoleArea;
	boolean write = true;
	JProgressBar progressBar;
	
	/**
	 * Sets variables used in the optimization. 
	 * @param 	numberOfYears		Total number of years service is used.
	 * @param 	hourlyRate			Hourly rate of service.
	 * @param 	icdMt				ICD maintenance costs over a single year. 
	 * @param 	serMain				% of SDLC total required to maintain service.
	 * @param 	attRate				Attrition rate (how many employees leave) over a year.
	 * @param 	hireRate			Hire rate over the year.
	 * @param 	infRate				Inflation rate over the year.
	 * @param 	disRate				Discount rate over the year.
	 * @param 	secondProYear		Second pro year - year in which more information is known.
	 * @param 	initProc			How much information you have initially.
	 * @param 	secondProc			How much information you have at second pro year.
	 */
	public void setVariables(int numberOfYears, double hourlyRate, double icdMt, double serMain, double attRate, double hireRate, double infRate, double disRate, int secondProYear, double initProc, double secondProc){
		lin = new ServiceOptimizer(icdMt,serMain);
		this.icdMt= icdMt;
		this.serMain= serMain;
		this.attRate = attRate;
		this.hourlyRate = hourlyRate;
		this.hireRate = hireRate;
		this.infRate = infRate;
		this.disRate = disRate;
		totalYrs = numberOfYears;
		
		createLearningYearlyConstants(numberOfYears, secondProYear, initProc, secondProc);
	}
	
	/**
	 * Sets data in the service optimizer.
	 * @param optOrg 	Optimization Organizer is used to efficiently run TAP-specific optimizations.
	 */
	public void setData(OptimizationOrganizer optOrg){
		lin.setData(optOrg);
	}
	
	/**
	 * value
	 * @param arg0 double
	// TODO: Return empty object instead of null
	 * @return double */
	public double value(double arg0) {
		return 0;
	}
	
	/**
	 * Sets the console area.
	 * @param JTextArea		Console area.
	 */
	public void setConsoleArea (JTextArea consoleArea)
	{
		this.consoleArea=consoleArea;
	}

	/**
	 * Sets properties of the progress bar.
	 * @param bar	Original bar that updates are made to.
	 */
	public void setProgressBar (JProgressBar bar)
	{
		this.progressBar=bar;
		this.progressBar.setVisible(true);
		this.progressBar.setIndeterminate(true);
		this.progressBar.setStringPainted(true);
	}
	
	/**
	 * Sets the value of the progress string on the progress bar.
	 * @param text	Text to be set.
	 */
	public void updateProgressBar (String text)
	{

		this.progressBar.setString(text);
	}
	
	/**
	 * Sets the write boolean.
	 * @param write	Boolean that is either true or false depending on optimization.
	 */
	public void setWriteBoolean (boolean write)
	{
		this.write = write;
	}
	
	/**
	 * 	Writes information to a JTextArea console.
	 * @param	objList		List of potential yearly savings for current iteration.
	 * @param 	budgetList	List of yearly budgets for current iteration.
	 * @param 	objFct		Objective function used in optimization.
	 */
	public void writeToAppConsole(ArrayList objList, ArrayList budgetList, double objFct)
	{
		consoleArea.setText(consoleArea.getText()+"\nPerforming yearly linear optimization iteration "+count);
		String budgetText = parseYearText(budgetList,"Year-by-year Budgets for current iteration:");
		String objText = parseYearText(objList,"Year-by-year savings potential for current iteration:");
		consoleArea.setText(consoleArea.getText()+"\n"+budgetText);
		consoleArea.setText(consoleArea.getText()+"\n"+objText);
		consoleArea.setText(consoleArea.getText()+"\nTotal obj function: "+objFct);
	}
	
	/**
	 * Solve differential equations in order to obtain the learning curve yearly constants for a service.
	 * @param 	numberOfYears	Number of years service is used.
	 * @param 	secondProYear	Second pro year - year in which more information is known.
	 * @param 	initProC		How much information you have initially.
	 * @param 	secondProC		How much information you have at second pro year.
	
	 * @return 	An array of doubles containing the learning curve constants. */
	public double[] createLearningYearlyConstants(int numberOfYears, int secondProYear, double initProC, double secondProC)
	{
		//learning curve differential equation f(t) = Pmax-C*e^(-k*t)
		//after you solve for differential equation to get constants
		//here are the equations for the constants
		
		cnstC = 1.0-initProC;
		cnstK = (1.0/secondProYear)*Math.log((1.0-initProC)/(1.0-secondProC));
		int yearToCalc = Math.max(totalYrs, 10);
		learningConstants = new double[yearToCalc];
		double[] origLearningConstants = new double[yearToCalc];
		for (int i = 0; i<origLearningConstants.length;i++)
		{
			origLearningConstants[i]=1.0+(cnstC/cnstK)*Math.exp(-(i+1.0)*cnstK)*(1.0-Math.exp(cnstK));
		}
		for (int i = 0; i<learningConstants.length;i++)
		{
			//account for turnover
			double retConstant=0.0;
			//ensure number of iterations does not pass
			for (int j=0;j<i;j++)
			{
				learningConstants[i]=learningConstants[i]+origLearningConstants[j]*hireRate*Math.pow(1-attRate, j);
			}
			learningConstants[i]=learningConstants[i]+origLearningConstants[i]*Math.pow((1-attRate),i);
		}
		return learningConstants;
		
	}
	
	/**
	 * Loops through a list and parses through each element to return the year text.
	 * @param 	list		List.
	 * @param 	retString	String to be returned.
	
	 * @return 	retString	Year text. */
	public String parseYearText(ArrayList list, String retString)
	{
		for (int i=0;i<list.size();i++)
		{
			int year = i+1;
			retString = retString+"Year "+ year+"-"+list.get(i)+", ";
		}
		return retString;
	}
}
