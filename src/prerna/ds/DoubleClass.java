package prerna.ds;

public class DoubleClass implements ISEMOSSNode {

	double value;
	String type = null;
	Object rawValue;
	
	public DoubleClass(double d, Object r, String t) {
		value = d;
		rawValue = r;
		type = t;
	}

	public DoubleClass(String string, boolean serialized){
		if(!serialized){
			this.value = Double.parseDouble(string);
		}
		else{
			fromString(string);
		}
	}
	
	public DoubleClass(double d, String t) {
		value = d;
		type = t;
	}
	
	@Override
	public String toString(){
		String className = "";
		Class<?> enclosingClass = getClass().getEnclosingClass();
		if (enclosingClass != null) {
			className = enclosingClass.getName();
		} else {
			className = getClass().getName();
		}
		String ret = className + "###" + 
				"innerString==="+this.value +
				"&&&type==="+this.type +
				"&&&rawValue==="+this.rawValue;
		return ret;
	}
	
	private void fromString(String serializedString){
//		System.err.println(serializedString);
		String[] mainParts = serializedString.split("&{3}");
		for(int idx = 0; idx < mainParts.length; idx++){
			String element = mainParts[idx];
			String[] parts = element.split("={3}");
			String name = parts[0];
			String value = parts[1];
			if(!value.equals("null")){
				if(name.equals("innerString")){
					this.value = Double.parseDouble(value);
				}
				else if(name.equals("type")){
					this.type = value;
				}
				else if(name.equals("rawValue")){
					this.rawValue = value;
				}
			}
		}
	}
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return value+"";
	}

	@Override
	public boolean isLeft(ITreeKeyEvaluatable object) {
		return this.value > ((DoubleClass)object).value;
	}

	@Override
	public boolean isEqual(ITreeKeyEvaluatable object) {
		return this.value == ((DoubleClass)object).value;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Object getRawValue() {
		return this.rawValue;
	}

	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public VALUE_TYPE getValueType() {
		return ISEMOSSNode.VALUE_TYPE.ORDINAL;
	}


}
