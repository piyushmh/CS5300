package com.cs5300.proj2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.metrics.spi.NoEmitMetricsContext;

import com.cs5300.proj2.BlockPageRank.ProjectCounters;


/**
 * Run reducer for every block, Receives all messages mapped to a single blockID
 * @author kt466
 *
 */
public class BlockPageRankReducer extends Reducer<Text, Text, Text, Text> {




	private HashMap<String, Double> newPR = new HashMap<String, Double>();

	private HashMap<String, Node> nodeIdToNodeMap = new HashMap<String, Node>();	
	private HashMap<String, ArrayList<String>> BE = new HashMap<String, ArrayList<String>>();
	private HashMap<String, Double> BC = new HashMap<String, Double>();


	private double dampingFactor = 0.85;
	private double randomJumpFactor = (1 - dampingFactor) / Constants.TOTAL_BLOCKS;
	private int maxIterations = 5;
	private Double threshold = 0.001; 

	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {


		//this is because the same reducer instance can be used for multiple keys
		newPR.clear();
		nodeIdToNodeMap.clear();
		BE.clear();
		BC.clear();


		Iterator<Text> iter = values.iterator();

		while( iter.hasNext()){ //read all values one by one

			Text input = iter.next();
			String [] valuearray = input.toString().split("\\s+");

			if( "PR".equals(valuearray[0].trim())){

				Node node = new Node();
				node.setNodeID(valuearray[1].trim());
				node.setPageRank(Double.parseDouble(valuearray[2].trim()));

				newPR.put(node.getNodeID(), node.getPageRank());

				if( valuearray.length == 4){
					node.setEdgeList(valuearray[3].trim());
					node.setDegrees(valuearray[3].trim().split(",").length);
				}

				nodeIdToNodeMap.put(node.getNodeID(), node);

				//add code to add the max value

			}else if ("BE".equals(valuearray[0].trim())){

				if( ! BE.containsKey(valuearray[2].trim())){
					BE.put(valuearray[2].trim(), new ArrayList<String>());
				}

				BE.get(valuearray[2].trim()).add(valuearray[1].trim());

			}else if ("BC".equals(valuearray[0].trim())){

				if ( ! BC.containsKey(valuearray[2].trim())){
					BC.put(valuearray[2].trim(), 0.0);
				}

				double tempVal =  BC.get(valuearray[2].trim()) + Double.parseDouble(valuearray[3].trim()) ; 
				BC.put(valuearray[2].trim(), tempVal);
			}

		}

		double residualerroriteration  = 0.0;
		for( int i = 0 ; i < maxIterations ; i++){

			residualerroriteration = iterateBlockOnce();

			if( residualerroriteration < threshold)
				break;
		}


		double residualerror = 0.0;
		for( String node : this.nodeIdToNodeMap.keySet()){

			residualerror+= 
					Math.abs((newPR.get(node) - this.nodeIdToNodeMap.get(node).getPageRank())) 
					/ newPR.get(node); 
		}


		long residualLong = (long)residualerror * Constants.RESIDUAL_OFFSET;
		context.getCounter(ProjectCounters.RESIDUAL_ERROR).increment(residualLong);

		for( String node : this.nodeIdToNodeMap.keySet()){
			String output = newPR.get(node) + " " + this.nodeIdToNodeMap.get(node)
					.getDegrees() + " " + this.nodeIdToNodeMap.get(node).getEdgeList();
			 
			Text outputkey = new Text(node);
			Text outputtext = new Text(output);
			context.write(outputkey, outputtext);
		}

		cleanup(context);

	}


	private double iterateBlockOnce(){

		Set<String> nodeset = this.nodeIdToNodeMap.keySet();
		double residualError = 0.0;


		for (String node : nodeset) {

			double oldpagerank = this.nodeIdToNodeMap.get(node).getPageRank();
			double newpagerank = 0.0;

			if( BE.containsKey(node)){
				ArrayList<String> edgelist = BE.get(node);

				for (String edge: edgelist) {
					newpagerank += ( newPR.get(edge) / this.nodeIdToNodeMap.get(edge).getDegrees());
				}
			}


			if( BC.containsKey(node)){
				newpagerank += BC.get(node);
			}

			newpagerank = (newpagerank* dampingFactor) + randomJumpFactor;

			newPR.put(node, newpagerank);
			residualError += Math.abs(newpagerank - oldpagerank) / newpagerank;
		}

		residualError = residualError / nodeset.size();
		return residualError;
	}
}
























