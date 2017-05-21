package thirdpartyutils;

import java.util.Comparator;

/**
 * A simple comparator that performs case-insensitive comparisons. 
 *
 */
public class IgnoreCaseStringComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		
		return ((String)o1).compareToIgnoreCase((String)o2);
	}

}
