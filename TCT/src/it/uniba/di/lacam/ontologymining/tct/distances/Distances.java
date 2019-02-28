package it.uniba.di.lacam.ontologymining.tct.distances;

public enum Distances {
	simpleDistance1,
	simpleDistance2,
	entropicSimpleDistance1,
	entropicSimpleDistance2,
	sqrtDistance1,
	sqrtDistance2;
	
	
	public static Distances value(String s){
	if (s.compareToIgnoreCase("simpleDistance1")==0)
		return simpleDistance1;
	if (s.compareToIgnoreCase("simpleDistance2")==0)
		return simpleDistance2;
	if (s.compareToIgnoreCase("entropicDistance1")==0)
		return entropicSimpleDistance1;
	if (s.compareToIgnoreCase("entropicDistance2")==0)
		return entropicSimpleDistance2;
	if (s.compareToIgnoreCase("sqrtDistance1")==0)
		return sqrtDistance1;
	if (s.compareToIgnoreCase("sqrtDistance2")==0)
		return sqrtDistance2;
	
	return null;
	
	}

}
