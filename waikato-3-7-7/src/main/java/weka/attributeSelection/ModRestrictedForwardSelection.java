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

 *    ModRestrictedForwardSelection.java
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

// TODO Need to rewrite this info.
/** 
 <!-- globalinfo-start -->
 * ModRestrictedForwardSelection :<br/>
 * <br/>
 * Performs a greedy forward selection search through the attributes. This
forward selection must start from an empty initial attribute set. The algorithm
first performs an evaluation of each individual attribute. The attributes are
then sorted according to their merit (basically their predictive ability for the
specified evaluator). The top attribute is then selected. This process is then
repeated, but this time only using the previous best M/2 attributes from the
sorted list. A winner is selected and added to the best group. The process is
then repeated, but only using the best M/3 attributes from the second sorted
list. Then a third winner is selected. All of this continues until m winners
have been selected and these form the filtered subset of attributes.
 * <br/>
 * For more information see:<br/>
 * <br/>
 * Deng, Kan, and Andrew W. Moore. On Greediness of Feature Selection
Algorithms. Carnegie Mellon University, The Robotics Institute, 1998.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;book{deng1998greediness,
 * 		title={On Greediness of Feature Selection Algorithms},
 * 		author={Deng, Kan and Moore, Andrew W},
 * 		year={1998},
 * 		publisher={Citeseer}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -N &lt;num to select&gt;
 *  Specify number of attributes to select</pre>
 * 
 <!-- options-end -->
 *
 * @author Brett Lagerwall
 */

public class ModRestrictedForwardSelection extends ASSearch
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
	static final long serialVersionUID = -2845254575621234145L;

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
	public ModRestrictedForwardSelection () {
		resetOptions();
	}

// TODO Need to rewrite the global info.
	/**
	 * Returns a string describing this search method.
	 * @return A description of the search suitable for
	 * displaying in the explorer/experimenter gui.
	 */
	public String globalInfo() {
		return "ModRestrictedForwardSelection:\n\nPerforms a greedy forward " 				+ "selection search through the attributes. This forward selection "
			+ "must start from an empty initial attribute set. The algorithm "
			+ "first performs an evaluation of each individual attribute. The "
			+ "attributes are then sorted according to their merit (basically "
			+ "their predictive ability for the specified evaluator). The top "
			+ "attribute is then selected. This process is then repeated, but "
			+ "this time only using the previous best M/2 attributes from the "
			+ "sorted list. A winner is selected and added to the best group. "
			+ "The process is then repeated, but only using the best M/3 "
			+ "attributes from the second sorted list. Then a third winner is "
			+ "selected. All of this continues until m winners have been "
			+ "selected and these form the filtered subset of attributes.\n";
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


//TODO Need to make a new toString() method.
	/**
	 * Returns a description of the search.
	 * @return A description of the search as a String.
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Restricted forward selection search heuristic.\n\n");
		strBuf.append("Number of attributes to select (-N): " + numToSelect +
			"\n");
		return strBuf.toString();
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


	protected void fillRemainingSlots(BitSet bestGroup,
		RankedAttribute[] oldRanking, int slotsRemaining) {

		for (int i = 1; i < 1 + slotsRemaining; i++) {
			bestGroup.set(oldRanking[i].getIndex());
System.out.println("Selected attribute " + (oldRanking[i].getIndex() + 1));
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
	public int[] search(ASEvaluation ASEval, Instances data) throws Exception {

		initialise(ASEval, data);

		double divFactor = Math.pow(numAttribs - 1, 1.0 / numToSelect);
System.out.println("\ndivFactor: " + divFactor);
System.out.println("\nLoop 1:");
		double listSize = numAttribs - 1;
System.out.println("listSize " + listSize);
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
System.out.println("Selected attribute " + (oldRanking[0].getIndex() + 1));

		for (int i = 1; i < numToSelect; i++) {

System.out.println("\nLoop " + (i + 1));
			listSize /= divFactor;
System.out.println("listSize " + listSize);
			int actualListSize = (int)Math.ceil(listSize);
System.out.println("actualListSize " + actualListSize);
System.out.println("Slot to fill " + (numToSelect - i));
			if (actualListSize <= numToSelect - i) {
System.out.println("List size too small. Filling all remaining slots.");
				fillRemainingSlots(bestGroup, oldRanking, numToSelect - i);
System.out.println("Exiting outer loop.");
				break;
			}
			RankedAttribute[] newRanking = new RankedAttribute[actualListSize];

			position = 0;
			// Attribute at index 0 will have been used in previous iteration.
System.out.println("Creating new array.");
			for (int j = 1; j < oldRanking.length; j++) {
				
				if (position >= newRanking.length) {
					// Filled up the new array, so break.
System.out.println("New array is full.");
					break;
				} else {
					bestGroup.set(oldRanking[j].getIndex());
					newRanking[position] = new RankedAttribute(
						oldRanking[j].getIndex(),
						asEvaluator.evaluateSubset(bestGroup));
					position++;
					bestGroup.clear(oldRanking[j].getIndex());
				}

			}

			Arrays.sort(newRanking);
			bestGroup.set(newRanking[0].getIndex());
System.out.println("Selected attribute " + (newRanking[0].getIndex() + 1));
			oldRanking = newRanking;
		}

System.out.println("Finished\n");
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
