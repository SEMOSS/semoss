/*******************************************************************************
 * Copyright 2014 SEMOSS.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package prerna.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A class that provides scrolling capabilities to a long menu dropdown or
 * popup menu.  A number of items can optionally be frozen at the top and/or
 * bottom of the menu.
 * <P>
 * <B>Implementation note:</B>  The default number of items to display
 * at a time is 15, and the default scrolling interval is 125 milliseconds.
 * <P>
 *
 * @version 1.5.0 04/05/12
 * @author Darryl
 */
public class MenuScroller {

  //private JMenu menu;
  private JPopupMenu menu;
  private Component[] menuItems;
  private MenuScrollItem upItem;
  private MenuScrollItem downItem;
  private final MenuScrollListener menuListener = new MenuScrollListener();
  private int scrollCount;
  private int interval;
  private int topFixedCount;
  private int bottomFixedCount;
  private int firstIndex = 0;
  private int keepVisibleIndex = -1;

  /**
   * Registers a menu to be scrolled with the default number of items to
   * display at a time and the default scrolling interval.
   * 
   * @param menu the menu
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JMenu menu) {
    return new MenuScroller(menu);
  }

  /**
   * Registers a popup menu to be scrolled with the default number of items to
   * display at a time and the default scrolling interval.
   * 
   * @param menu the popup menu
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JPopupMenu menu) {
    return new MenuScroller(menu);
  }

  /**
   * Registers a menu to be scrolled with the default number of items to
   * display at a time and the specified scrolling interval.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to display at a time
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JMenu menu, int scrollCount) {
    return new MenuScroller(menu, scrollCount);
  }

  /**
   * Registers a popup menu to be scrolled with the default number of items to
   * display at a time and the specified scrolling interval.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to display at a time
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount) {
    return new MenuScroller(menu, scrollCount);
  }

  /**
   * Registers a menu to be scrolled, with the specified number of items to
   * display at a time and the specified scrolling interval.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to be displayed at a time
   * @param interval the scroll interval, in milliseconds
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JMenu menu, int scrollCount, int interval) {
    return new MenuScroller(menu, scrollCount, interval);
  }

  /**
   * Registers a popup menu to be scrolled, with the specified number of items to
   * display at a time and the specified scrolling interval.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to be displayed at a time
   * @param interval the scroll interval, in milliseconds
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount, int interval) {
    return new MenuScroller(menu, scrollCount, interval);
  }

  /**
   * Registers a menu to be scrolled, with the specified number of items
   * to display in the scrolling region, the specified scrolling interval,
   * and the specified numbers of items fixed at the top and bottom of the
   * menu.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to display in the scrolling portion
   * @param interval the scroll interval, in milliseconds
   * @param topFixedCount the number of items to fix at the top.  May be 0.
   * @param bottomFixedCount the number of items to fix at the bottom. May be 0
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JMenu menu, int scrollCount, int interval,
          int topFixedCount, int bottomFixedCount) {
    return new MenuScroller(menu, scrollCount, interval,
            topFixedCount, bottomFixedCount);
  }

  /**
   * Registers a popup menu to be scrolled, with the specified number of items
   * to display in the scrolling region, the specified scrolling interval,
   * and the specified numbers of items fixed at the top and bottom of the
   * popup menu.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to display in the scrolling portion
   * @param interval the scroll interval, in milliseconds
   * @param topFixedCount the number of items to fix at the top.  May be 0
   * @param bottomFixedCount the number of items to fix at the bottom.  May be 0
  
  
   * @return the MenuScroller */
  public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount, int interval,
          int topFixedCount, int bottomFixedCount) {
    return new MenuScroller(menu, scrollCount, interval,
            topFixedCount, bottomFixedCount);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a menu with the
   * default number of items to display at a time, and default scrolling
   * interval.
   * 
   * @param menu the menu
   */
  public MenuScroller(JMenu menu) {
    this(menu, 15);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a popup menu with the
   * default number of items to display at a time, and default scrolling
   * interval.
   * 
   * @param menu the popup menu
   */
  public MenuScroller(JPopupMenu menu) {
    this(menu, 15);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a menu with the
   * specified number of items to display at a time, and default scrolling
   * interval.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to display at a time
  
   */
  public MenuScroller(JMenu menu, int scrollCount) {
    this(menu, scrollCount, 150);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a popup menu with the
   * specified number of items to display at a time, and default scrolling
   * interval.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to display at a time
  
   */
  public MenuScroller(JPopupMenu menu, int scrollCount) {
    this(menu, scrollCount, 150);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a menu with the
   * specified number of items to display at a time, and specified scrolling
   * interval.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to display at a time
   * @param interval the scroll interval, in milliseconds
  
   */
  public MenuScroller(JMenu menu, int scrollCount, int interval) {
    this(menu, scrollCount, interval, 0, 0);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a popup menu with the
   * specified number of items to display at a time, and specified scrolling
   * interval.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to display at a time
   * @param interval the scroll interval, in milliseconds
  
   */
  public MenuScroller(JPopupMenu menu, int scrollCount, int interval) {
    this(menu, scrollCount, interval, 0, 0);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a menu with the
   * specified number of items to display in the scrolling region, the
   * specified scrolling interval, and the specified numbers of items fixed at
   * the top and bottom of the menu.
   * 
   * @param menu the menu
   * @param scrollCount the number of items to display in the scrolling portion
   * @param interval the scroll interval, in milliseconds
   * @param topFixedCount the number of items to fix at the top.  May be 0
   * @param bottomFixedCount the number of items to fix at the bottom.  May be 0
  
   */
  public MenuScroller(JMenu menu, int scrollCount, int interval,
          int topFixedCount, int bottomFixedCount) {
    this(menu.getPopupMenu(), scrollCount, interval, topFixedCount, bottomFixedCount);
  }

  /**
   * Constructs a <code>MenuScroller</code> that scrolls a popup menu with the
   * specified number of items to display in the scrolling region, the
   * specified scrolling interval, and the specified numbers of items fixed at
   * the top and bottom of the popup menu.
   * 
   * @param menu the popup menu
   * @param scrollCount the number of items to display in the scrolling portion
   * @param interval the scroll interval, in milliseconds
   * @param topFixedCount the number of items to fix at the top.  May be 0
   * @param bottomFixedCount the number of items to fix at the bottom.  May be 0
  
   */
  public MenuScroller(JPopupMenu menu, int scrollCount, int interval,
          int topFixedCount, int bottomFixedCount) {
    if (scrollCount <= 0 || interval <= 0) {
      throw new IllegalArgumentException("scrollCount and interval must be greater than 0");
    }
    if (topFixedCount < 0 || bottomFixedCount < 0) {
      throw new IllegalArgumentException("topFixedCount and bottomFixedCount cannot be negative");
    }

    upItem = new MenuScrollItem(MenuIcon.UP, -1);
    downItem = new MenuScrollItem(MenuIcon.DOWN, +1);
    setScrollCount(scrollCount);
    setInterval(interval);
    setTopFixedCount(topFixedCount);
    setBottomFixedCount(bottomFixedCount);

    this.menu = menu;
    menu.addPopupMenuListener(menuListener);
  }

  /**
   * Returns the scroll interval in milliseconds
   * 
  
   * @return the scroll interval in milliseconds */
  public int getInterval() {
    return interval;
  }

  /**
   * Sets the scroll interval in milliseconds
   * 
   * @param interval the scroll interval in milliseconds
  
   */
  public void setInterval(int interval) {
    if (interval <= 0) {
      throw new IllegalArgumentException("interval must be greater than 0");
    }
    upItem.setInterval(interval);
    downItem.setInterval(interval);
    this.interval = interval;
  }

  /**
   * Returns the number of items in the scrolling portion of the menu.
   *
  
   * @return the number of items to display at a time */
  public int getscrollCount() {
    return scrollCount;
  }

  /**
   * Sets the number of items in the scrolling portion of the menu.
   * 
   * @param scrollCount the number of items to display at a time
  
   */
  public void setScrollCount(int scrollCount) {
    if (scrollCount <= 0) {
      throw new IllegalArgumentException("scrollCount must be greater than 0");
    }
    this.scrollCount = scrollCount;
    MenuSelectionManager.defaultManager().clearSelectedPath();
  }

  /**
   * Returns the number of items fixed at the top of the menu or popup menu.
   * 
  
   * @return the number of items */
  public int getTopFixedCount() {
    return topFixedCount;
  }

  /**
   * Sets the number of items to fix at the top of the menu or popup menu.
   * 
   * @param topFixedCount the number of items
   */
  public void setTopFixedCount(int topFixedCount) {
    if (firstIndex <= topFixedCount) {
      firstIndex = topFixedCount;
    } else {
      firstIndex += (topFixedCount - this.topFixedCount);
    }
    this.topFixedCount = topFixedCount;
  }

  /**
   * Returns the number of items fixed at the bottom of the menu or popup menu.
   * 
  
   * @return the number of items */
  public int getBottomFixedCount() {
    return bottomFixedCount;
  }

  /**
   * Sets the number of items to fix at the bottom of the menu or popup menu.
   * 
   * @param bottomFixedCount the number of items
   */
  public void setBottomFixedCount(int bottomFixedCount) {
    this.bottomFixedCount = bottomFixedCount;
  }

  /**
   * Scrolls the specified item into view each time the menu is opened.  Call this method with
   * <code>null</code> to restore the default behavior, which is to show the menu as it last
   * appeared.
   *
   * @param item the item to keep visible
   * @see #keepVisible(int)
   */
  public void keepVisible(JMenuItem item) {
    if (item == null) {
      keepVisibleIndex = -1;
    } else {
      int index = menu.getComponentIndex(item);
      keepVisibleIndex = index;
    }
  }

  /**
   * Scrolls the item at the specified index into view each time the menu is opened.  Call this
   * method with <code>-1</code> to restore the default behavior, which is to show the menu as
   * it last appeared.
   *
   * @param index the index of the item to keep visible
   * @see #keepVisible(javax.swing.JMenuItem)
   */
  public void keepVisible(int index) {
    keepVisibleIndex = index;
  }

  /**
   * Removes this MenuScroller from the associated menu and restores the
   * default behavior of the menu.
   */
  public void dispose() {
    if (menu != null) {
      menu.removePopupMenuListener(menuListener);
      menu = null;
    }
  }

  /**
   * Ensures that the <code>dispose</code> method of this MenuScroller is
   * called when there are no more refrences to it.
   * 
  
   * @see MenuScroller#dispose()
   */
  @Override
  public void finalize() throws Throwable {
    dispose();
  }

  /**
   * Method refreshMenu.
   */
  private void refreshMenu() {
    if (menuItems != null && menuItems.length > 0) {
      firstIndex = Math.max(topFixedCount, firstIndex);
      firstIndex = Math.min(menuItems.length - bottomFixedCount - scrollCount, firstIndex);

      upItem.setEnabled(firstIndex > topFixedCount);
      downItem.setEnabled(firstIndex + scrollCount < menuItems.length - bottomFixedCount);

      menu.removeAll();
      for (int i = 0; i < topFixedCount; i++) {
        menu.add(menuItems[i]);
      }
      if (topFixedCount > 0) {
        menu.addSeparator();
      }

      menu.add(upItem);
      for (int i = firstIndex; i < scrollCount + firstIndex; i++) {
        menu.add(menuItems[i]);
      }
      menu.add(downItem);

      if (bottomFixedCount > 0) {
        menu.addSeparator();
      }
      for (int i = menuItems.length - bottomFixedCount; i < menuItems.length; i++) {
        menu.add(menuItems[i]);
      }

      JComponent parent = (JComponent) upItem.getParent();
      parent.revalidate();
      parent.repaint();
    }
  }

  /**
   */
  private class MenuScrollListener implements PopupMenuListener {

    /**
     * Method popupMenuWillBecomeVisible.
     * @param e PopupMenuEvent
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      setMenuItems();
    }

    /**
     * Method popupMenuWillBecomeInvisible.
     * @param e PopupMenuEvent
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      restoreMenuItems();
    }

    /**
     * Method popupMenuCanceled.
     * @param e PopupMenuEvent
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
      restoreMenuItems();
    }

    /**
     * Method setMenuItems.
     */
    private void setMenuItems() {
      menuItems = menu.getComponents();
      if (keepVisibleIndex >= topFixedCount
              && keepVisibleIndex <= menuItems.length - bottomFixedCount
              && (keepVisibleIndex > firstIndex + scrollCount
              || keepVisibleIndex < firstIndex)) {
        firstIndex = Math.min(firstIndex, keepVisibleIndex);
        firstIndex = Math.max(firstIndex, keepVisibleIndex - scrollCount + 1);
      }
      if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
        refreshMenu();
      }
    }

    /**
     * Method restoreMenuItems.
     */
    private void restoreMenuItems() {
      menu.removeAll();
      for (Component component : menuItems) {
        menu.add(component);
      }
    }
  }

  /**
   */
  private class MenuScrollTimer extends Timer {

    /**
     * Constructor for MenuScrollTimer.
     * @param increment int
     * @param interval int
     */
    public MenuScrollTimer(final int increment, int interval) {
      super(interval, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          firstIndex += increment;
          refreshMenu();
        }
      });
    }
  }

  /**
   */
  private class MenuScrollItem extends JMenuItem
          implements ChangeListener {

    private MenuScrollTimer timer;

    /**
     * Constructor for MenuScrollItem.
     * @param icon MenuIcon
     * @param increment int
     */
    public MenuScrollItem(MenuIcon icon, int increment) {
      setIcon(icon);
      setDisabledIcon(icon);
      timer = new MenuScrollTimer(increment, interval);
      addChangeListener(this);
    }

    /**
     * Method setInterval.
     * @param interval int
     */
    public void setInterval(int interval) {
      timer.setDelay(interval);
    }

    /**
     * Method stateChanged.
     * @param e ChangeEvent
     */
    @Override
    public void stateChanged(ChangeEvent e) {
      if (isArmed() && !timer.isRunning()) {
        timer.start();
      }
      if (!isArmed() && timer.isRunning()) {
        timer.stop();
      }
    }
  }

  /**
   */
  private static enum MenuIcon implements Icon {

    UP(9, 1, 9),
    DOWN(1, 9, 1);
    final int[] xPoints = {1, 5, 9};
    final int[] yPoints;

    /**
     * Constructor for MenuIcon.
     * @param yPoints int[]
     */
    MenuIcon(int... yPoints) {
      this.yPoints = yPoints;
    }

    /**
     * Method paintIcon.
     * @param c Component
     * @param g Graphics
     * @param x int
     * @param y int
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Dimension size = c.getSize();
      Graphics g2 = g.create(size.width / 2 - 5, size.height / 2 - 5, 10, 10);
      g2.setColor(Color.GRAY);
      g2.drawPolygon(xPoints, yPoints, 3);
      if (c.isEnabled()) {
        g2.setColor(Color.BLACK);
        g2.fillPolygon(xPoints, yPoints, 3);
      }
      g2.dispose();
    }

    /**
     * Method getIconWidth.
    
     * @return int */
    @Override
    public int getIconWidth() {
      return 0;
    }

    /**
     * Method getIconHeight.
    
     * @return int */
    @Override
    public int getIconHeight() {
      return 10;
    }
  }
}
