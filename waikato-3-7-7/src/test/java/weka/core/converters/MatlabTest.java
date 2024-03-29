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
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.core.converters;

import weka.core.TestInstances;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests MatlabLoader/MatlabSaver. Run from the command line with:<p/>
 * java weka.core.converters.MatlabTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8050 $
 */
public class MatlabTest 
  extends AbstractFileConverterTest {

  /**
   * Constructs the <code>MatlabTest</code>.
   *
   * @param name the name of the test class
   */
  public MatlabTest(String name) { 
    super(name);  
  }

  /**
   * returns the loader used in the tests.
   * 
   * @return the configured loader
   */
  public AbstractLoader getLoader() {
    return new MatlabLoader();
  }

  /**
   * returns the saver used in the tests.
   * 
   * @return the configured saver
   */
  public AbstractSaver getSaver() {
    return new MatlabSaver();
  }

  /**
   * returns the test data generator.
   * 
   * @return 	the configured test data generator
   */
  protected TestInstances getTestInstances() {
    return TestInstances.forCapabilities(new MatlabSaver().getCapabilities());
  }
  
  /**
   * Called by JUnit before each test method. This implementation creates
   * the default loader/saver to test and generates a test set of Instances.
   *
   * @throws Exception if an error occurs reading the example instances.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_MaxDiffValues  = 1E-6;
    m_MaxDiffWeights = 0.0;
    m_CheckHeader    = false;
  }

  /**
   * returns a test suite.
   * 
   * @return the test suite
   */
  public static Test suite() {
    return new TestSuite(MatlabTest.class);
  }

  /**
   * for running the test from commandline.
   * 
   * @param args the commandline arguments - ignored
   */
  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}
