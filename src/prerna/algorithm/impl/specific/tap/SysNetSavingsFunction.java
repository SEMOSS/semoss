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

import prerna.algorithm.impl.LinearInterpolation;

/**
 * This class is used to calculate the number of years to decommission systems based on the budget.
 */
public class SysNetSavingsFunction extends UnivariateSvcOptFunction{
	
	LinearInterpolation linInt;
	double numMaintenanceSavings;
	double serMainPerc;
	double dataExposeCost;

	/**
	 * Given a budget, calculate the years in Savings.
	 * Gets the lists of potential yearly savings and yearly budgets and can write to the app console for each iteration.
	 * @param a 		Budget used in the service optimizer.
	 * @return double	Cumulative Net Savings */
	@Override
	public double value(double a) {
		count++;
		updateProgressBar("Iteration " + count);
		linInt.setB(a);
		linInt.execute();
		double n = linInt.retVal;
		double savings = calculateRet(a,n);
		writeToAppConsole(a,n,savings);
		return savings;
	}
	public double calculateRet(double budget, double n)
	{
		return (totalYrs-n)*(numMaintenanceSavings - serMainPerc*dataExposeCost)-budget*n;
		//return (totalYrs-n)*(numMaintenanceSavings)-budget*n;

	}
	public double calculateRetForVariableTotal(double budget, double n,double totalNumYears)
	{
		return (totalNumYears-n)*(numMaintenanceSavings - serMainPerc*dataExposeCost)-budget*n;
		//return (totalNumYears-n)*(numMaintenanceSavings)-budget*n;

	}
	public double calculateSavingsForVariableTotal(double budget, double n,double totalNumYears)
	{
		return (totalNumYears-n)*(numMaintenanceSavings - serMainPerc*dataExposeCost);
		//return (totalNumYears-n)*(numMaintenanceSavings)-budget*n;

	}
	public ArrayList<Double> createCumulativeSavings(double budget, double n)
	{
		ArrayList<Double> cumSavingsList = new ArrayList<Double>();
		int index = 0;
		while(index<totalYrs)
		{
			if(index+1<n)
				cumSavingsList.add(0.0);
			else if(index<n)
				cumSavingsList.add(calculateSavingsForVariableTotal(budget,n,Math.ceil(n)));
			else
				cumSavingsList.add(calculateSavingsForVariableTotal(budget,n,index+1));
				//cumSavingsList.add(calculateSavingsForVariableTotal(budget,n,index+1.0));
			index++;
		}
		return cumSavingsList;
	}
	
	public ArrayList<Double> createBreakEven(double budget, double n)
	{
		ArrayList<Double> breakEvenList = new ArrayList<Double>();
		int index = 0;
		while(index<totalYrs)
		{
			if(index<n)
				breakEvenList.add(-1*budget*index);
			else if(index<n+1)
				breakEvenList.add(calculateRetForVariableTotal(budget,n,Math.ceil(n)));
			else
				breakEvenList.add(calculateRetForVariableTotal(budget,n,index+1.0));
			index++;
		}
		return breakEvenList;
	}
	public double calculateYear(double budget)
	{
		linInt.setB(budget);
		linInt.execute();
		return linInt.retVal;
	}
	public void setSavingsVariables(double numMaintenanceSavings,double serMainPerc,double dataExposeCost)
	{
		this.numMaintenanceSavings = numMaintenanceSavings;
		this.serMainPerc = serMainPerc;
		this.dataExposeCost = dataExposeCost;
	}
	public void writeToAppConsole(double budget, double n, double savings)
	{
		consoleArea.setText(consoleArea.getText()+"\nPerforming optimization iteration "+count);
		consoleArea.setText(consoleArea.getText()+"\nFor Budget B: "+budget+" the minimum N is "+n+" and the savings are "+savings);
	}
	
	//data Expose is in LOE
	public void createLinearInterpolation(double initProc, double secondYearProc, double secondYear, double dataExposeCost, double minYears, double maxYears)
	{
		 linInt = new LinearInterpolation();
		 linInt.setValues(initProc, secondYearProc, secondYear, dataExposeCost, minYears, maxYears);
	}

}
