/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    SuperGreedy.java
 *    Copyright (C) 2004 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.attributeSelection;

import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Arrays;

// TODO Add information here. See SuperGreedy for inspiration.
/** 
 *
 * @author Brett Lagerwall
 */

public class RestrictedForwardSelection  extends ASSearch
	implements OptionHandler {


	/** An inner class for storing a ranked attribute */
	protected class RankedAttribute implements Serializable,
		Comparable<RankedAttribute> {

		/** For serialization. */
		static final long serialVersionUID = -4865832784228787646L;

		private int index;
		private double merit;

		public RankedAttribute(int index, double merit) {
			this.index = index;
			this.merit = merit;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public double getMerit() {
			return merit;
		}

		public void setMerit(double merit) {
			this.merit = merit;
		}

		public int compareTo(RankedAttribute o) {
			// Sorts from highest merit to lowest merit.
			if (merit < o.merit) {
				return 1;
			} else {
				// For the equal case, it does not matter what is returned.
				return -1;
			}
		}

	}
 
	/** For serialization. */
	static final long serialVersionUID = -4336827949282668955L;

	/** Does the data have a class. */
	protected boolean hasClass;
 
	/** Holds the class index. */
	protected int classIndex;
 
	/** Number of attributes in the data. */
	protected int numAttribs;

	/** The number of attributes to select. Defaults to 1. */
	protected int numToSelect = 1;

	/** The Attribute Selection Evaluation method. */
	protected ASEvaluation asEval;

	/** The data. */
	protected Instances instances;


	/**
	 * Constructor
	 */
	public RestrictedForwardSelection () {
		resetOptions();
	}

// TODO Add global information for this search. This is seen in the GUI.
	/**
	 * Returns a string describing this search method.
	 * @return A description of the search suitable for
	 * displaying in the explorer/experimenter gui.
	 */
	public String globalInfo() {
		/*return "SuperGreedy :\n\nPerforms a greedy forward selection search "
			+ "through the attributes. This forward selection must start from "
			+ "an empty initial attribute set. The algorithm first performs an "
			+ "evaluation of each individual attribute. The attributes are "
			+ "then sorted according to their merit (basically their "
			+ "predictive ability for the specified evaluator). The top N "
			+ "attributes are then returned as the attribute set -- where N is "
			+ "the user-specified number to select.\n";*/
		return "";
  }


	/**
	 * Specify the number of attributes to select.
	 * @param n The number of attributes to retain.
	 */
	public void setNumToSelect(int n) {
		numToSelect = n;
	}


	/**
	 * Gets the number of attributes to be retained.
	 * @return The number of attributes to retain.
	 */
	public int getNumToSelect() {
		return numToSelect;
	}


	/**
	 * Returns an enumeration describing the available options.
	 * @return An enumeration of all the available options.
	 **/
	public Enumeration listOptions () {
		Vector newVector = new Vector(5);

		newVector.addElement(new Option
			("\tSpecify number of attributes to select", "N", 1,
			"-N <num to select>"));

		return newVector.elements();
	}


	/**
	 * Parses a given list of options. <p/>
	 *
	 <!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -N &lt;num to select&gt;
	 *  Specify number of attributes to select</pre>
	 * 
	 <!-- options-end -->
	 *
	 * @param options The list of options as an array of strings.
	 * @throws Exception If an option is not supported.
	 */
	public void setOptions(String[] options) throws Exception {
		String optionString;
		resetOptions();

		optionString = Utils.getOption('N', options);
		if (optionString.length() != 0) {
			setNumToSelect(Integer.parseInt(optionString));
		}
	}


	/**
	 * Gets the current settings.
	 * @return an array of strings suitable for passing to setOptions()
	 */
	public String[] getOptions () {
		String[] options = new String[9];
		int current = 0;

		options[current++] = "-N";
		options[current++] = "" + getNumToSelect();

		while (current < options.length) {
			options[current++] = "";
		}
		return  options;
	}


// TODO Write a toString() method  for RestrictedForwardSelection.
	/**
	 * Returns a description of the search.
	 * @return A description of the search as a String.
	 */
	public String toString() {
		/*StringBuffer strBuf = new StringBuffer();
		strBuf.append("Super greedy search heuristic.\n\n");
		strBuf.append("Number of attributes to select (-N): " + numToSelect +
			"\n");

		if (rankedAtts != null) {
			strBuf.append("Ranked Attributes\n");
			strBuf.append("-----------------\n");
			strBuf.append("Index\tMerit\n");

			for (int i = 0; i < numAttribs; i++) {
				if (rankedAtts[i] != null &&
						rankedAtts[i].getIndex() != classIndex) {
					strBuf.append(rankedAtts[i].getIndex() + "\t" +
						rankedAtts[i].getMerit() + "\n");
				}
			}
		}
		return strBuf.toString();*/
		return "";
	}


	protected void initialise(ASEvaluation ASEval, Instances data)
		throws Exception {

		if (data != null) {
			// Fresh run -- so reset options.
			resetOptions();
			instances = data;
		}

		asEval = ASEval;

		numAttribs = instances.numAttributes();

		if (numToSelect >= numAttribs) {
			throw new Exception("Trying to select " + numToSelect +
				" attributes when there are only " + (numAttribs - 1) +
				" attributes.");
		}

		if (numToSelect <= 0) {
			throw new Exception("Must select 1 or more attributes.");
		}

		if (!(asEval instanceof SubsetEvaluator)) {
			throw new Exception(asEval.getClass().getName() + " is not a " 
				+ "Subset evaluator!");
		}

		if (asEval instanceof UnsupervisedSubsetEvaluator) {
			hasClass = false;
			classIndex = -1;
		} else {
			hasClass = true;
			classIndex = instances.classIndex();
		}
	}

	/**
	 * Searches the attribute subset space using restricted forward selection.
	 *
	 * @param ASEval The attribute evaluator to guide the search.
	 * @param data The training instances.
	 * @return An array (not necessarily ordered) of selected attribute indexes.
	 * @throws Exception If the search can't be completed.
	 */
	public int[] search (ASEvaluation ASEval, Instances data) throws Exception {

		initialise(ASEval, data);

		SubsetEvaluator asEvaluator = (SubsetEvaluator)asEval;
		BitSet bestGroup = new BitSet(numAttribs);

		RankedAttribute[] oldRanking = new RankedAttribute[numAttribs - 1];
		int position = 0;
		for (int i = 0; i < numAttribs; i++) {
			if (i != classIndex) {
				bestGroup.set(i);
				oldRanking[position] = new RankedAttribute(i,
					asEvaluator.evaluateSubset(bestGroup));
				position++;
				bestGroup.clear(i);
			}
		}
		Arrays.sort(oldRanking);
		bestGroup.set(oldRanking[0].getIndex());
System.out.println("\n\nLoop: 0");
System.out.println("Added attribute " + (oldRanking[0].getIndex() + 1) + " to the best set.");

		for (int i = 1; i < numToSelect; i++) {
System.out.println("\nLoop: " + i);

			RankedAttribute[] newRanking =
				new RankedAttribute[(int)Math.round
				((double)(numAttribs - 1) / (i + 1))];
System.out.println("Looking for " + newRanking.length + " attributes.");

			position = 0;
			for (int j = 0; j < oldRanking.length; j++) {
				if (position >= newRanking.length) {
System.out.println("Found sufficient attributes.");
					// Filled up the new array, so break.
					break;
				}

				if (bestGroup.get(oldRanking[j].getIndex())) {
					// Attribute has already been selected, so continue.
					continue;
				} else {
					bestGroup.set(oldRanking[j].getIndex());
					newRanking[position] = new RankedAttribute(
						oldRanking[j].getIndex(),
						asEvaluator.evaluateSubset(bestGroup));
					position++;
					bestGroup.clear(oldRanking[j].getIndex());
				}
			}

			if (position != newRanking.length) {
				// Weren't enough attributes to fill up the new array.

				// If one or more attributes were evaluated in the last round,
				// then the winner can be added to the best group.
System.out.println("Not enough attributes to fill up the array. Needed " + newRanking.length + ", but got " + position);
				if (position != 0) {
					Arrays.sort(newRanking, 0, position - 1);
					bestGroup.set(newRanking[0].getIndex());
System.out.println("Can still add one more attribute to the best set.");
System.out.println("Added attribute " + (newRanking[0].getIndex() + 1) + " to the best set.");
System.out.println("Exiting early.");
					break;
				}
			} else {
				Arrays.sort(newRanking);
				bestGroup.set(newRanking[0].getIndex());
System.out.println("Added attribute " + (newRanking[0].getIndex() + 1) + " to the best set.");
				oldRanking = newRanking;
			}
		}

System.out.println("\n");
		return attributeList(bestGroup);
	}


	/**
	 * Converts a BitSet into a list of attribute indexes.
	 * @param group The BitSet to convert.
	 * @return An array of attribute indexes.
	 **/
	protected int[] attributeList(BitSet group) {
		int count = 0;

		// Count how many were selected.
		for (int i = 0; i < numAttribs; i++) {
			if (group.get(i)) {
				count++;
			}
		}

		int[] list = new int[count];
		count = 0;

		for (int i = 0; i < numAttribs; i++) {
			if (group.get(i)) {
				list[count] = i;
				count++;
			}
		}

		return list;
	}


	/**
	 * Resets options
	 */
	protected void resetOptions() {
		asEval = null;
		instances = null;
	}

}
