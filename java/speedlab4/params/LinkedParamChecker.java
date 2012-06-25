package speedlab4.params;

/*
 * Provides functionality for two linked params to know
 * when to update their values and what to update them to,
 * in order to keep both their values legal.
 */
public interface LinkedParamChecker {

	/*
	 * Return true if both p1 and p2 are allowable values.
	 * Otherwise return false, indicating that one of them
	 * needs to be changed.
	 */
	public boolean checkParams(ParamLinkedDouble p1, ParamLinkedDouble p2);
	
	/*
	 * Precondition: checkParams(paramToFix, itsPair) returns false.
	 * Returns the value that paramToFix should be changed to
	 * in order for checkParams(paramToFix, itsPair) to return true.
	 */
	public double changeParamTo(ParamLinkedDouble paramToFix);
}
