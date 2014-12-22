/*******************************************************************************
 * Copyright 2014 SEMOSS.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package prerna.algorithm.impl.specific.tap;

/**
 * This class is used to calculate the number of years to decommission systems based on the budget.
 * It is used in TAP Systems optimization.
 */
public class SysNetSavingsFunction extends UnivariateSysOptFunction{
	
	/**
	 * Given a specific budget, calculates the savings up until .
	 * Gets the list of potential yearly savings and yearly budgets.
	 * @param a 	Budget used in the service optimizer.
	 * @return 		Profit. */
	public double calculateRet(double budget, double n)
	{
		calculateInvestment(budget,n);
		//if it takes the full time, there is no savings, just return the investment?
		if(totalYrs <= n)
			return -1*investment;
		//make the savings inflation/discount factor if applicable
		double savings =totalYrs-n;
		if(inflDiscFactor!=1)
			savings = Math.pow(inflDiscFactor,n+1) * (1-Math.pow(inflDiscFactor,totalYrs-n) ) / (1-inflDiscFactor);
		//multiply the savings for all years
		savings = savings * (numMaintenanceSavings - serMainPerc*dataExposeCost);
		savings = savings - investment;
		return savings;
	}
	
	public void writeToAppConsole(double budget, double n, double savings)
	{
		consoleArea.setText(consoleArea.getText()+"\nPerforming optimization iteration "+count);
		consoleArea.setText(consoleArea.getText()+"\nFor Budget B: "+budget+" the minimum N is "+n+" and the savings are "+savings);
	}
	
//	public double calculateYears(double budget)
//	{
//		workPerformedArray = new ArrayList<Double>();
//		double workNeededAdj = 0.0;
//		while(workNeededAdj<dataExposeCost)
//		{
//			//year 1, q=1 in index 0.
//			double workPerformedInYearq = calculateWorkPerformedInYearq(budget, workPerformedArray.size()+1);
//			workNeededAdj+=workPerformedInYearq;
//			workPerformedArray.add(workPerformedInYearq);
//		}
//		double workPerformedInLastYear = workPerformedArray.get(workPerformedArray.size()-1);
//		double fraction = (dataExposeCost - (workNeededAdj - workPerformedInLastYear))/workPerformedInLastYear;
//		return workPerformedArray.size()-1+fraction;
//	}
//	public double calculateWorkPerformedInYearq(double budget, int q)
//	{
//		double P1q = calculateP1q(q);
//		double workPerformedInYearq = budget * P1q;
//		return workPerformedInYearq;
//	}
//	public double calculateP1q(double q)
//	{
//		double Pq = calculatePq(q);
//		double hireSum = 0.0;
//		for(int i=1;i<=q-1;i++)
//		{
//			hireSum+=Math.pow(1-attRate,i-1)*calculatePq(i);
//		}
//		double P1q = Pq*Math.pow(1-attRate,q-1)+hireRate*hireSum;
//		return P1q;
//	}
//	public double calculateP1qFraction(double q)
//	{
//		double Pq = calculatePq(q);
//		double hireSum = 0.0;
//		for(int i=1;i<=q;i++)
//		{
//			hireSum+=Math.pow(1-attRate,i-1)*calculatePq(i);
//		}
//		double P1q = Pq*Math.pow(1-attRate,q)+hireRate*hireSum;
//		return P1q;
//	}
//
//	public double calculatePq(double q)
//	{
//		return 1+sigma*Math.exp(-1*q*k);
//	}
//	
//	public double calculateBudgetForOneYear()
//	{
//		return dataExposeCost/calculatePq(1);
//	}
//	public void calculateInvestment(double budget, double n)
//	{
//		double P1InflationSum = 0.0;
//		for(int q=1;q<=n;q++)
//		{
//			double P1Inflation = 1.0;
//			if(inflDiscFactor!=1)
//				P1Inflation = Math.pow(inflDiscFactor, q-1);
//			//P1Inflation *= calculateP1q(q);
//			P1InflationSum += P1Inflation;
//		}
//		double extraYear = 1.0;
//		if(inflDiscFactor!=1)
//			extraYear = Math.pow(inflDiscFactor,n);
//		P1InflationSum+=extraYear*(n-Math.floor(n));
//		investment = budget * P1InflationSum;
//	}
//
//	public double calculateRetForVariableTotal(double budget, double n,double totalNumYears)
//	{
//		calculateInvestment(budget,n);
//		//if it takes the full time, there is no savings, just return the investment?
//		if(totalNumYears == n)
//			return -1*investment;
//		//make the savings inflation/discount factor if applicable
//		double savings =totalNumYears-n;
//		if(inflDiscFactor!=1)
//			savings = Math.pow(inflDiscFactor,n+1) * (1-Math.pow(inflDiscFactor,totalNumYears-n) ) / (1-inflDiscFactor);
//		//multiply the savings for all years
//		savings = savings * (numMaintenanceSavings - serMainPerc*dataExposeCost);
//		savings = savings - investment;
//		return savings;
//	}
//	public double calculateSavingsForVariableTotal(double budget, double n,double totalNumYears)
//	{
//		calculateInvestment(budget,n);
//		//if it takes the full time, there is no savings, just return the investment?
//		if(totalNumYears == n)
//			return -1*investment;
//		//make the savings inflation/discount factor if applicable
//		double savings =totalNumYears-n;
//		if(inflDiscFactor!=1)
//			savings = Math.pow(inflDiscFactor,n+1) * (1-Math.pow(inflDiscFactor,totalNumYears-n) ) / (1-inflDiscFactor);
//		//multiply the savings for all years
//		savings = savings * (numMaintenanceSavings - serMainPerc*dataExposeCost);
//		return savings;
//	}
//	
//	public ArrayList<Double> createSustainmentCosts(double budget, double n)
//	{
//		ArrayList<Double> sustainmentList = new ArrayList<Double>();
//		int index = 0;
//		while(index<totalYrs)
//		{
//			if(index+1<n)
//				sustainmentList.add(0.0);
//			else if(index<n)
//			{
//				double factor=1.0;
//				if(inflDiscFactor!=1)
//					factor= Math.pow(inflDiscFactor,index+1);
//				//double sustainment = factor*(numMaintenanceSavings - serMainPerc*investment)*(n-(index+1));
//				double sustainment = factor*(serMainPerc*dataExposeCost)*((index+1)-n);
//				sustainmentList.add(sustainment);
//			}
//			else
//			{
//				double factor=1.0;
//				if(inflDiscFactor!=1)
//					factor= Math.pow(inflDiscFactor,index+1);
//				//double sustainment = factor*(numMaintenanceSavings - serMainPerc*investment);
//				double sustainment = factor*(serMainPerc*dataExposeCost);
//				sustainmentList.add(sustainment);
//			}
//			index++;
//		}
//		return sustainmentList;
//	}
//	public ArrayList<Double> createInstallCosts(double budget, double n)
//	{
//		ArrayList<Double> installList = new ArrayList<Double>();
//		int index = 0;
//		while(index<totalYrs)
//		{
//			if(index+1<n)//might need to say zero if null pointer, this should probably be adjusted to investment cost at each year.
//			{
//				double factor = 1.0;
//				if(inflDiscFactor!=1)
//					factor = Math.pow(inflDiscFactor,index);
//				double buildCost = factor*budget;
//				installList.add(buildCost);
//			}
//			else if(index<n)
//			{
//				double factor = 1.0;
//				if(inflDiscFactor!=1)
//					factor = Math.pow(inflDiscFactor,index);
//				double buildCost = factor*budget*(n-(index));
//				installList.add(buildCost);
//			}
//			else
//				installList.add(0.0);
//			index++;
//		}
//		return installList;
//	}
//	
//	public ArrayList<Double> createCumulativeSavings(double budget, double n)
//	{
//		ArrayList<Double> cumSavingsList = new ArrayList<Double>();
//		int index = 0;
//		while(index<totalYrs)
//		{
//			if(index+1<n||(index==0&&n==1))
//				cumSavingsList.add(0.0);
//			else if(index<n)
//				cumSavingsList.add(calculateSavingsForVariableTotal(budget,n,Math.ceil(n)));
//			else
//				cumSavingsList.add(calculateSavingsForVariableTotal(budget,n,index+1));
//			index++;
//		}
//		return cumSavingsList;
//	}
//	
//	public ArrayList<Double> createBreakEven(double budget, double n)
//	{
//		ArrayList<Double> breakEvenList = new ArrayList<Double>();
//		double buildCost=0.0;
//		int index = 0;
//		while(index<totalYrs)
//		{
//			if(index+1<n||(index==0&&n==1))
//			{
//				double factor = 1.0;
//				if(inflDiscFactor!=1)
//					factor = Math.pow(inflDiscFactor,index);
//				buildCost += -1*factor*budget;
//				breakEvenList.add(buildCost);
//			}
//			else if(index<n)
//			{
//				double factor = 1.0;
//				if(inflDiscFactor!=1)
//					factor = Math.pow(inflDiscFactor,index);
//				buildCost += -1*factor*budget*(n-(index));
//				breakEvenList.add(buildCost+calculateSavingsForVariableTotal(budget,n,Math.ceil(n)));
//			}
//			else
//				breakEvenList.add(buildCost+calculateSavingsForVariableTotal(budget,n,index+1.0));
//			//breakEvenList.add(buildCost+calculateRetForVariableTotal(budget,n,index+1.0));
//			index++;
//		}
//		return breakEvenList;
//	}
//



}
