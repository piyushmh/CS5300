package com.cs5300.proj2;


public final class Constants {

	public static double REJECT_MIN=0.97416;
	public static  double REJECT_LIMIT=0.98416;
	public static final int TOTAL_NODES = 685230;
	public static final int TOTAL_BLOCKS = 68;
	public static final double TERMINATION_RESIDUAL = 0.001;
	public static final int RESIDUAL_OFFSET = 10000000;
	public static final double INIT_PR = 1/(double)TOTAL_NODES;
	
	public static final int BLOCK_SIZE = 10000;
	
	public static final String PR_DELIMITER = "PR";
	
	public static final String BE_DELIMITER = "BE";

	public static final String BC_DELIMITER = "BC";
	
	public static final String TUPLE_DELIMITER=" ";
	
	public static final String OUT_NODE_LIST_DELIMITER=",";
	
	public static final String OUT_EDGE = "OUT";
	
	public static final String IN_EDGE = "IN";
	
	public static final double DAMPING_FACTOR = 0.85;
	

}
