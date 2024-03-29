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
 * TitleUpdatedEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.scripting.event;

import weka.gui.scripting.ScriptingPanel;

import java.util.EventObject;

/**
 * Event that gets send in case a scripting panel updates the title.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8048 $
 */
public class TitleUpdatedEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 4971569742479666535L;

  /**
   * Initializes the event.
   * 
   * @param source	the scripting panel that triggered the event
   */
  public TitleUpdatedEvent(ScriptingPanel source) {
    super(source);
  }
  
  /**
   * Returns the scripting panel that triggered the event. Use the
   * <code>getTitle()</code> method for accessing the new title.
   * 
   * @return		the panel
   */
  public ScriptingPanel getPanel() {
    return (ScriptingPanel) getSource();
  }
}
