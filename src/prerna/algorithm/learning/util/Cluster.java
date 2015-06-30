package prerna.algorithm.learning.util;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Cluster {

	private ICategoricalCluster categoricalCluster;
	private INumericalCluster numericalCluster;
	
	//TODO: take into consideration null values
	public Cluster(Map<String, Double> categoricalWeights, Map<String, Double> numericalWeights) {
		this.categoricalCluster = new CategoricalCluster(categoricalWeights);
		this.numericalCluster = new NumericalCluster(numericalWeights);
	}
	
	public void addToCluster(Object[] values, String[] names, boolean[] isNumeric) {
		for(int i = 0; i < values.length; i++){
			if(isNumeric[i]) {
				addToNumericalCluster(names[i], (Double)values[i]);
			} else {
				addToCategoricalCluster(names[i], (String)values[i]);
			}
		}
	}
	
	public void removeFromCluster(Object[] values, String[] names, boolean[] isNumeric) {
		for(int i = 0; i < values.length; i++){
			if(isNumeric[i]) {
				removeFromNumericalCluster(names[i], (Double)values[i]);
			} else {
				removeFromCategoricalCluster(names[i], (String)values[i]);
			}
		}
	}
	
	private void addToCategoricalCluster(String attributeName, String attributeInstance) {
		addToCategoricalCluster(attributeName, attributeInstance, 1.0);
	}
	
	private void addToCategoricalCluster(String attributeName, String attributeInstance, Double value) {
		categoricalCluster.addToCluster(attributeName, attributeInstance, value);
	}
	
	private void removeFromCategoricalCluster(String attributeName, String attributeInstance) {
		removeFromCategoricalCluster(attributeName, attributeInstance, 1.0);
	}
	
	private void removeFromCategoricalCluster(String attributeName, String attributeInstance, Double value) {
		categoricalCluster.removeFromCluster(attributeName, attributeInstance, value);
	}
	
	private void addToNumericalCluster(String attributeName, Double value) {
		numericalCluster.addToCluster(attributeName, value);
	}
	
	private void removeFromNumericalCluster(String attributeName, Double value) {
		numericalCluster.removeFromCluster(attributeName, value);
	}
	
	public void setDistanceMode(String[] levelNames, IClusterDistanceMode[] distanceMeasures, boolean[] isNumeric) {
		for(int i = 0; i < levelNames.length; i++) {
			if(isNumeric[i]) {
				setDistanceMode(levelNames[i], distanceMeasures[i]);
			}
		}
	}
	
	public void setDistanceMode(String[] levelNames, IClusterDistanceMode[] distanceMeasures) {
		for(int i = 0; i < levelNames.length; i++) {
			setDistanceMode(levelNames[i], distanceMeasures[i]);
		}
	}
	
	public void setDistanceMode(Map<String, IClusterDistanceMode.DistanceMeasure> distanceMeasures) {
		for(String attribute : distanceMeasures.keySet()) {
			IClusterDistanceMode distance = null;
			IClusterDistanceMode.DistanceMeasure measure = distanceMeasures.get(attribute);
			if(measure == IClusterDistanceMode.DistanceMeasure.MEAN) {
				distance = new MeanDistance();
			} 
			//TODO: add other classes once they are done being implemented
//			else if(measure == IClusterDistanceMode.DistanceMeasure.MAX) {
//				
//			}
			setDistanceMode(attribute, distance);
		}
	}
	
	public void setDistanceMode(String attributeName, IClusterDistanceMode distanceMeasure) {
		numericalCluster.setDistanceMode(attributeName, distanceMeasure);
	}
	
	/**
	 * Returns the similarity score for an instance with the this cluster
	 * @param instanceValues			The values for the instance for each column
	 * @param attributeNames			The names of associated for each value the instanceValues array, essentially the 'column name'
	 * @param isNumeric					The boolean representing if value of i in the array is numeric
	 * @return
	 */
	public double getSimilarityForInstance(Object[] instanceValues, String[] attributeNames, boolean[] isNumeric, int indexToSkip) {
		List<String> categoricalValues = new Vector<String>();
		List<String> categoricalValueNames = new Vector<String>();
		List<Double> numericalValues = new Vector<Double>();
		List<String> numericalValueNames = new Vector<String>();
		
		int i = 0;
		int size = isNumeric.length;
		for(; i < size; i++) {
			if(isNumeric[i]) {
				numericalValues.add((Double) instanceValues[i]);
				numericalValueNames.add((String) attributeNames[i]);
				if(i==indexToSkip) indexToSkip = numericalValueNames.size()-1;
			} else {
				categoricalValues.add((String) instanceValues[i]);
				categoricalValueNames.add((String) attributeNames[i]);
				if(i==indexToSkip) indexToSkip = categoricalValueNames.size()-1;
			}
		}
		
		double similarityValue = 0;
		
		
		if(!categoricalValues.isEmpty()) {
			if(isNumeric[indexToSkip]) {
				similarityValue += getSimilarityFromCategoricalValues(categoricalValues, categoricalValueNames);
			} else {
				similarityValue += getSimilarityFromCategoricalValues(categoricalValues, categoricalValueNames, indexToSkip);
			}
		}
		
		if(!numericalValues.isEmpty()) {
			if(isNumeric[indexToSkip]) {
				similarityValue += getSimilarityFromNumericalValues(numericalValues, numericalValueNames, indexToSkip);
			} else {
				similarityValue += getSimilarityFromNumericalValues(numericalValues, numericalValueNames);
			}
		}
		
		return similarityValue;
	}

	private double getSimilarityFromNumericalValues(List<Double> numericalValues, List<String> numericalValueNames) {
		return numericalCluster.getSimilarity(numericalValueNames, numericalValues);
	}

	private double getSimilarityFromNumericalValues(List<Double> numericalValues, List<String> numericalValueNames, int index) {
		return numericalCluster.getSimilarity(numericalValueNames, numericalValues, index);
	}
	
	private double getSimilarityFromCategoricalValues(List<String> categoricalValues, List<String> categoricalValueNames) {
		return categoricalCluster.getSimilarity(categoricalValues, categoricalValueNames);
	}
	
	private double getSimilarityFromCategoricalValues(List<String> categoricalValues, List<String> categoricalValueNames, int index) {
		return categoricalCluster.getSimilarity(categoricalValues, categoricalValueNames, index);
	}
}
