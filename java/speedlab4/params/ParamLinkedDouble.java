package speedlab4.params;

public class ParamLinkedDouble extends ParamDouble {
	   
	public ParamLinkedDouble pair = null;
	public LinkedParamChecker checker = null;
	public boolean linked = false;
	public boolean pushover = false;

    public ParamLinkedDouble(String name, Double value, double min, double max,String description, boolean reqRestart){
    	super(name, value, min, max, description, reqRestart);
    }

    public ParamLinkedDouble(String name, Double value, double min, double max) {
        super(name, value, min, max);
    }

    public ParamLinkedDouble(String name, Double value, double min) {
        this(name, value, min, 1d);
    }

    public ParamLinkedDouble(String name, Double value) {
        this(name, value, 0d, 1d);
    }

    /*
     * Calls doLinkedDouble on the Paramable object param
     */
    public <N, E, T extends Paramable<E, N>> E visit(T param, N arg) {
        return param.doLinkedDouble(this, arg);
    }
    
	/*
	 * Link this param to 'pair' and set its paramChecker to 'c'
	 */
    public void setLinked(ParamLinkedDouble pair, LinkedParamChecker c){
    	this.pair = pair;
    	this.checker = c;
    	linked = true;
    }
    
    /*
     * Static methods to link params into groups.
     * Should be invoked in constructor of SimModel after params
     * have been instantiated.
     */
	
	/*
	 * Link two params to require their sum to equal 'sum'.
	 * When a param is changed by the user, the param's pair will
	 * adjust its value to ensure the sum is correct.
	 */
	public static void linkSum(ParamLinkedDouble p1, ParamLinkedDouble p2, final double sum){
		
		LinkedParamChecker lc = new LinkedParamChecker(){
			double desiredSum = sum;
			public boolean checkParams(ParamLinkedDouble parm1, ParamLinkedDouble parm2){
				double total = parm1.value + parm2.value;
				if (total == desiredSum)
					return true;
				else
					return false;
			}
			public double changeParamTo(ParamLinkedDouble p){
				double newVal = desiredSum - p.pair.value;
				if (newVal < 0)
					return 0;
				else
					return newVal;
			}
		};
		link(p1, p2, lc);
	}
	
	/*
	 * Link two params to require that the dependent param is always greater
	 * than or equal to the independent param
	 */
	public static void linkGreaterOrEqual(ParamLinkedDouble independent, ParamLinkedDouble dependent){
		
		LinkedParamChecker lc = new LinkedParamChecker(){
			public boolean checkParams(ParamLinkedDouble parm1, ParamLinkedDouble parm2){
				if (parm1.pushover){
					if (parm1.value < parm2.value)
						return false;
					return true;
				}
				else{
					if (parm2.value < parm1.value)
						return false;
					return true;
				}
			}
			public double changeParamTo(ParamLinkedDouble p){
				// if param is a pushover, change it to its pair's value
				if (p.pushover)					
					return p.pair.value;
				// otherwise don't change it
				return p.value;
			}
		};
		dependent.pushover = true;
		link(independent, dependent, lc);
	}
	
	/*
	 * Link two params with requirements specified by the given LinkedParamChecker
	 */
	public static void link(ParamLinkedDouble p1, ParamLinkedDouble p2, LinkedParamChecker c){
		p1.setLinked(p2, c);
		p2.setLinked(p1, c);
	}

}
