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

import prerna.algorithm.impl.LinearInterpolation;


/**
 * SysIRRLinInterp is used to estimate the IRR (mu) for a given discount rate using Linear Interpolation.
 */
public class SysIRRLinInterp extends LinearInterpolation{

	double numMaintenanceSavings, serMainPerc, dataExposeCost, totalYrs, infRate,discRate;
	double N, B;
	String printString = "";

	/**
	 * Sets the parameters used in the equation.
	 * @param numMaintenanceSavings	
	 * @param serMainPerc
	 * @param dataExposeCost
	 * @param totalYrs
	 * @param infRate
	 * @param discRate
	 */
	public void setCalcParams(double numMaintenanceSavings,double serMainPerc,double dataExposeCost,double totalYrs,double infRate,double discRate) {
		this.numMaintenanceSavings=numMaintenanceSavings;
		this.serMainPerc=serMainPerc;
		this.dataExposeCost = dataExposeCost;
		this.totalYrs=totalYrs;
		this.infRate=infRate;
		this.discRate=discRate;
	}
	/**
	 * Sets the Budget and Number of years given
	 * @param B	Double	Budget
	 * @param N Double	Number of years to transition.
	 */
	public void setBAndN(double B,double N) {
		this.B = B;
		this.N = N;
	}
	
	/**
	 * Gets the calculation string that details the calculation performed for debugging purposes.
	 * @return printString
	 */
	public String getPrintString() {
		return printString;
	}
	

	/**
	 * Calculate the residual value for a given root estimate, possibleDiscRate.
	 * @param possibleDiscRate double	root estimate	
	 * @return Double	residual value for the calculation
	 */
	@Override
	public Double calcY(double possibleDiscRate) {		
		double v = (1+infRate)/(1+possibleDiscRate);
		double vFactor = totalYrs-N;
		if(v!=1)
			vFactor = Math.pow(v, N+1)*(1-Math.pow(v, totalYrs-N))/(1-v);
		double sustainSavings = vFactor*(numMaintenanceSavings - serMainPerc*dataExposeCost);
		double mu = (1+infRate)/(1+discRate);
		double investment = B*N;
		if(mu!=1)
			investment = B*(1-Math.pow(mu, N))/(1-mu);
		double yVal = sustainSavings - investment;
		printString += "\nv: "+v+" v^(N+1)*(1-v^(Q-N))/(1-v) "+vFactor+"\nmu: "+mu+" investment "+investment;
		return yVal;
	}
}
