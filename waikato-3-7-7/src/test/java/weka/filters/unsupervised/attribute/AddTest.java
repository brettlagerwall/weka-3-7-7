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
 * Copyright (C) 2002 University of Waikato 
 */

package weka.filters.unsupervised.attribute;

import weka.filters.AbstractFilterTest;
import weka.filters.Filter;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests Add. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.AddTest
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision: 8050 $
 */
public class AddTest extends AbstractFilterTest {
  
  public AddTest(String name) { super(name);  }

  /** Creates a default Add */
  public Filter getFilter() {
    return new Add();
  }

  /** Creates a specialized Add */
  public Filter getFilter(int pos) {
    Add af = new Add();
    af.setAttributeIndex("" + (pos + 1));
    return af;
  }

  public void testAddFirst() {
    m_Filter = getFilter(0);
    testBuffered();
  }

  public void testAddLast() {
    m_Filter = getFilter(m_Instances.numAttributes() - 1);
    testBuffered();
  }

  public void testAddNominal() {
    m_Filter = getFilter();
    ((Add)m_Filter).setNominalLabels("hello,there,bob");
    testBuffered();
  }

  public static Test suite() {
    return new TestSuite(AddTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }

}
