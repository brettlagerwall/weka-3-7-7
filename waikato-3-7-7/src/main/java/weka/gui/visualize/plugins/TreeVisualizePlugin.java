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
 * TreeVisualizePlugin.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.visualize.plugins;

import javax.swing.JMenuItem;

/**
 * Interface implemented by classes loaded dynamically to
 * visualize classifier results in the explorer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8048 $
 */
public interface TreeVisualizePlugin {

  /**
   * Get a JMenu or JMenuItem which contain action listeners
   * that perform the visualization of the tree in GraphViz's dotty format.  
   * Exceptions thrown because of changes in Weka since compilation need to 
   * be caught by the implementer.
   *
   * @see NoClassDefFoundError
   * @see IncompatibleClassChangeError
   *
   * @param dotty 	the tree in dotty format
   * @param name	the name of the item (in the Explorer's history list)
   * @return menuitem 	for opening visualization(s), or null
   *         		to indicate no visualization is applicable for the input
   */
  public JMenuItem getVisualizeMenuItem(String dotty, String name);

  /**
   * Get the minimum version of Weka, inclusive, the class
   * is designed to work with.  eg: <code>3.5.0</code>
   * 
   * @return		the minimum version
   */
  public String getMinVersion();

  /**
   * Get the maximum version of Weka, exclusive, the class
   * is designed to work with.  eg: <code>3.6.0</code>
   * 
   * @return		the maximum version
   */
  public String getMaxVersion();

  /**
   * Get the specific version of Weka the class is designed for.
   * eg: <code>3.5.1</code>
   * 
   * @return		the version the plugin was designed for
   */
  public String getDesignVersion();
}



