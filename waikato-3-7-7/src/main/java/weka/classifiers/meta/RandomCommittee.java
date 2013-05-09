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
 *    RandomCommittee.java
 *    Copyright (C) 2003 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.meta;

import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
import weka.classifiers.RandomizableParallelIteratedSingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.util.Random;

/**
 <!-- globalinfo-start -->
 * Class for building an ensemble of randomizable base classifiers. Each base classifiers is built using a different random number seed (but based one the same data). The final prediction is a straight average of the predictions generated by the individual base classifiers.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 * 
 * <pre> -I &lt;num&gt;
 *  Number of iterations.
 *  (default 10)</pre>
 * 
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.trees.RandomTree)</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.trees.RandomTree:
 * </pre>
 * 
 * <pre> -K &lt;number of attributes&gt;
 *  Number of attributes to randomly investigate
 *  (&lt;1 = int(log(#attributes)+1)).</pre>
 * 
 * <pre> -M &lt;minimum number of instances&gt;
 *  Set minimum number of instances per leaf.</pre>
 * 
 * <pre> -S &lt;num&gt;
 *  Seed for random number generator.
 *  (default 1)</pre>
 * 
 * <pre> -depth &lt;num&gt;
 *  The maximum depth of the tree, 0 for unlimited.
 *  (default 0)</pre>
 * 
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 <!-- options-end -->
 *
 * Options after -- are passed to the designated classifier.<p>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 8048 $
 */
public class RandomCommittee 
  extends RandomizableParallelIteratedSingleClassifierEnhancer
  implements WeightedInstancesHandler {
    
  /** for serialization */
  static final long serialVersionUID = -9204394360557300092L;
  
  /** training data */
  protected Instances m_data;
  
  /**
   * Constructor.
   */
  public RandomCommittee() {
    
    m_Classifier = new weka.classifiers.trees.RandomTree();
  }

  /**
   * String describing default classifier.
   * 
   * @return the default classifier classname
   */
  protected String defaultClassifierString() {
    
    return "weka.classifiers.trees.RandomTree";
  }

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
 
    return "Class for building an ensemble of randomizable base classifiers. Each "
      + "base classifiers is built using a different random number seed (but based "
      + "one the same data). The final prediction is a straight average of the "
      + "predictions generated by the individual base classifiers.";
  }

  /**
   * Builds the committee of randomizable classifiers.
   *
   * @param data the training data to be used for generating the
   * bagged classifier.
   * @exception Exception if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    m_data = new Instances(data);
    m_data.deleteWithMissingClass();
    super.buildClassifier(m_data);
    
    if (!(m_Classifier instanceof Randomizable)) {
      throw new IllegalArgumentException("Base learner must implement Randomizable!");
    }

    m_Classifiers = AbstractClassifier.makeCopies(m_Classifier, m_NumIterations);

    Random random = m_data.getRandomNumberGenerator(m_Seed);
    for (int j = 0; j < m_Classifiers.length; j++) {

      // Set the random number seed for the current classifier.
      ((Randomizable) m_Classifiers[j]).setSeed(random.nextInt());
      
      // Build the classifier.
//      m_Classifiers[j].buildClassifier(m_data);
    }
    
    buildClassifiers();
    
    // save memory
    m_data = null;
  }
  
  /**
   * Returns a training set for a particular iteration.
   * 
   * @param iteration the number of the iteration for the requested training set.
   * @return the training set for the supplied iteration number
   * @throws Exception if something goes wrong when generating a training set.
   */
  protected synchronized Instances getTrainingSet(int iteration) throws Exception {
    
    // we don't manipulate the training data in any way.
    return m_data;
  }

  /**
   * Calculates the class membership probabilities for the given test
   * instance.
   *
   * @param instance the instance to be classified
   * @return preedicted class probability distribution
   * @exception Exception if distribution can't be computed successfully 
   */
  public double[] distributionForInstance(Instance instance) throws Exception {

    double [] sums = new double [instance.numClasses()], newProbs; 
    
    for (int i = 0; i < m_NumIterations; i++) {
      if (instance.classAttribute().isNumeric() == true) {
	sums[0] += m_Classifiers[i].classifyInstance(instance);
      } else {
	newProbs = m_Classifiers[i].distributionForInstance(instance);
	for (int j = 0; j < newProbs.length; j++)
	  sums[j] += newProbs[j];
      }
    }
    if (instance.classAttribute().isNumeric() == true) {
      sums[0] /= (double)m_NumIterations;
      return sums;
    } else if (Utils.eq(Utils.sum(sums), 0)) {
      return sums;
    } else {
      Utils.normalize(sums);
      return sums;
    }
  }

  /**
   * Returns description of the committee.
   *
   * @return description of the committee as a string
   */
  public String toString() {
    
    if (m_Classifiers == null) {
      return "RandomCommittee: No model built yet.";
    }
    StringBuffer text = new StringBuffer();
    text.append("All the base classifiers: \n\n");
    for (int i = 0; i < m_Classifiers.length; i++)
      text.append(m_Classifiers[i].toString() + "\n\n");

    return text.toString();
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 8048 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main(String [] argv) {
    runClassifier(new RandomCommittee(), argv);
  }
}

