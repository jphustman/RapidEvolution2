package com.ibm.iwt;

import javax.swing.JDialog;
import javax.swing.JFrame;
import com.ibm.iwt.util.IWTUtilities;
import java.awt.BorderLayout;
import com.ibm.iwt.window.IWindowTitleBar;
import com.ibm.iwt.window.IContentPane;
import javax.swing.border.AbstractBorder;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import javax.swing.JRootPane;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.Border;
import java.awt.Image;
import com.ibm.iwt.window.IBorderComponent;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.Robot;
import java.awt.Frame;
import java.awt.Point;
import com.ibm.iwt.event.WindowChangeEvent;
import java.awt.image.BufferedImage;
import java.awt.event.MouseMotionListener;
import com.ibm.iwt.event.WindowChangeListener;
import java.awt.event.WindowListener;
import rapid_evolution.ui.SkinManager;
import com.brunchboy.util.swing.relativelayout.AttributeConstraint;
import com.brunchboy.util.swing.relativelayout.AttributeType;
import com.brunchboy.util.swing.relativelayout.DependencyManager;
import com.brunchboy.util.swing.relativelayout.RelativeLayout;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class IDialog extends JDialog implements WindowChangeListener, WindowListener, MouseListener, MouseMotionListener {
  public IDialog(JFrame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }
  public IDialog(JDialog owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  protected IWindowTitleBar titleBar;
  /** the instance of the icontent pane inside the frame */
  protected IContentPane iContentPane;

  private static int RESTORE_WIDTH = 0;
  private static int RESTORE_HEIGHT = 0;
  private static int RESTORE_X = 0;
  private static int RESTORE_Y = 0;

  /** constant to turn on transparency when frame is initialized */
  protected int TRANSPARENT_STARTUP = -1;
  /** constant to turn off transparency */
  protected int TRANSPARENT_NONE = 0;
  /** contant to turn on transparency */
  protected int TRANSPARENT_ON = 0;

  private int direction = WindowChangeEvent.RESIZE_NONE;
  private int X = 0;
  private int Y = 0;
  private ResizeThread r;
  private boolean isDrawingTransparent = false;
  private IBorderComponent pressedComponent = null;

  /** the screenshot that is used to draw transparency */
  protected BufferedImage screenShot = null;
  /** the robot instance that captures screen shots */
  protected Robot robot;
  /** the rectangle that defines the screen dimensions */
  protected Rectangle rect;
  /** the frame's transparency status - when off it will speed performance */
  protected int transparentState = TRANSPARENT_STARTUP;

  /**
   * A convenience static method that returns the current application size in pixels.
   * Subclasses of the IBorderComponent may find this method useful.
   * @return the size of the frame in pixels
   */
  public static Dimension getApplicationSize()
  {
          return new Dimension(RESTORE_WIDTH, RESTORE_HEIGHT);
  }

  /**
   * Sets the frame visibility and also, if transparency is turned on, captures
   * the screen shot.
   * @param isVisible the visibility of the frame
   */
  private boolean first_visible = true;
  public void setVisible(boolean isVisible)
  {
      if ((isVisible && first_visible) && !this.isVisible()) {
          RESTORE_WIDTH = getWidth();
          RESTORE_HEIGHT = getHeight();
          first_visible = false;
      }
          if (isVisible && transparentState != TRANSPARENT_NONE)
          {
                  if (transparentState == TRANSPARENT_STARTUP)
                          transparentState = TRANSPARENT_NONE;
                  captureScreenShot();
          }
          super.setVisible(isVisible);
  }

  /**
   * Sets the size of the frame.
   * @param width the width in pixels
   * @param height the height in pixels
   */
  public void setSize(int width, int height)
  {
          super.setSize(width, height);
          repaint();
  }

  /**
   * Sets the size of the frame.
   * @param size the size of the frame in pixels
   */
  public void setSize(Dimension size)
  {
          setSize((int)size.getWidth(), (int)size.getHeight());
  }


  /**
   * Sets the bounds of the frame.
   * @param r the rectangle that defines the bounds
   */
  public void setBounds(Rectangle r)
  {
          setBounds(r.x, r.y, r.width, r.height);
  }

  /**
   * Sets the location of the frame.
   * @param x the x position
   * @param y the y position
   */
  public void setLocation(int x, int y)
  {
          super.setLocation(x, y);
          RESTORE_X = x;
          RESTORE_Y = y;
  }

  /**
   * Sets the title of the frame and passes the title on to the current title bar.
   * @param title the title of the frame
   */
  public void setTitle(String title)
  {
          super.setTitle(title);
          getTitleBar().setTitle(title);
  }

  /**
   * Sets the icon image of the frame and passes the icon on to the current title bar.
   * @param i the icon of the frame
   */
  public void setIconImage(Image i)
  {
          getTitleBar().setLogo(new ImageIcon(i));
  }


  private void initialize()
  {
    setUndecorated(true);
    getRootPane().setWindowDecorationStyle(JRootPane.NONE);
          addWindowListener(this);
          BorderLayout layout = new BorderLayout();
//          RelativeLayout layout = new RelativeLayout();
          getContentPane().setLayout(layout);
//          getContentPane().add(getTitleBar(), "title_bar");
//          layout.addConstraint("title_bar", AttributeType.TOP,  new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.TOP, 0));
//          layout.addConstraint("title_bar", AttributeType.HORIZONTAL_CENTER,  new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));
//          getContentPane().add(getIContentPane(), "content_pane");
//          layout.addConstraint("content_pane", AttributeType.TOP,  new AttributeConstraint("title_bar", AttributeType.BOTTOM, 0));
//          layout.addConstraint("content_pane", AttributeType.HORIZONTAL_CENTER,  new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));
          getContentPane().add(getTitleBar(), BorderLayout.NORTH);
          getContentPane().add(getIContentPane(), BorderLayout.CENTER);
          this.setSize(IWTUtilities.getScreenSize(this));
          RESTORE_WIDTH = getWidth();
          RESTORE_HEIGHT = getHeight();
          r = new ResizeThread(this, 0, 0, getWidth(), getHeight());
          getRootPane().addMouseListener(this);
          getRootPane().addMouseMotionListener(this);
  }

  /**
   * Returns the instance of the icontent pane that is being used by the frame.
   * <p><b>Important Note:</b> This method should be used to add components to the
   * interior of the frame, not <code>getContentPane()</code>.  Using <code>
   * getContentPane()</code> will cause unpredictable and most likely incorrect
   * behavior.  By default, the icontent pane instance has a BorderLayout layout and
   * a default Windows 2000 border.
   * @return the current icontent pane
   */
  public IContentPane getIContentPane()
  {
          if (iContentPane == null)
          {
                  iContentPane = new IContentPane();
                  iContentPane.setBorder(new DefaultBorder());
                  iContentPane.setLayout(new BorderLayout());
                  iContentPane.addWindowChangeListener(this);
          }
          return iContentPane;
  }

  /**
   * Sets the current icontent pane of the frame.
   * @param contentPane the new icontent pane
   */
  public void setIContentPane(IContentPane contentPane)
  {
          getContentPane().add(contentPane, BorderLayout.CENTER);
          iContentPane = contentPane;
          iContentPane.addWindowChangeListener(this);
  }

  public void setResizable(boolean value) {
    super.setResizable(value);
    getTitleBar().setMaximize(value);
  }

  /**
   * Returns the current title bar being used by the frame.
   * @return the current title bar
   */
  public IWindowTitleBar getTitleBar()
  {
          if (titleBar == null)
          {
                  titleBar = new IWindowTitleBar();
                  titleBar.addWindowChangeListener(this);
          }
          return titleBar;
  }

  /**
   * Sets the title bar on the frame.
   * @param windowTitleBar the new title bar
   */
  public void setTitleBar(IWindowTitleBar windowTitleBar)
  {
          getContentPane().remove(titleBar);
          titleBar = windowTitleBar;
          titleBar.addWindowChangeListener(this);
          getContentPane().add(windowTitleBar, BorderLayout.NORTH);
          repaint();
  }

  private void captureScreenShot()
  {
          try
          {
                  robot = new Robot();
                  rect = new Rectangle(0, 0, IWTUtilities.getScreenWidth(), IWTUtilities.getScreenHeight());
                  screenShot = robot.createScreenCapture(rect);
          }
          catch (java.awt.AWTException ex) {}
  }

  /**
   * Sets a region on the specified component transparent to the pixels behind the
   * frame.  To improve performance, the region that is being drawn transparent
   * should be small, as painting the transparent regions is a relatively slow
   * process compared to other repaints.  Also, calls to this function should
   * be called from the caller's <code>paint()</code> so that the transparent
   * region repaints itself properly as well.
   * @param c the component that will have the transparent region
   * @param g the Graphics instance
   * @param x the x coordinate of the rectangular transparent region
   * @param y the y coordinate of the rectangular transparent region
   * @param w the width of the rectangular transparent region
   * @param h the height of the rectangular transparent region
   */
  public void setTransparent(JComponent c, Graphics g, int x, int y, int w, int h)
  {
          transparentState = TRANSPARENT_ON;
          Point p = new Point(x,y);
          SwingUtilities.convertPointToScreen(p,c);
          if (p.x+w > screenShot.getWidth())
                  w = screenShot.getWidth() - p.x;
          if (p.y+h > screenShot.getHeight())
                  h = screenShot.getHeight() - p.y;
          BufferedImage i = screenShot.getSubimage(Math.max(0,p.x), Math.max(0,p.y), w, h);
          ImageIcon icon = new ImageIcon(i);
          Point p2 = new Point(Math.max(0, p.x), Math.max(0, p.y));
          SwingUtilities.convertPointFromScreen(p2, c);
          icon.paintIcon(c, g, p2.x, p2.y);
  }

  /**
   * Sets the solid background color of the title bar.
   * @param background the background color
   */
  public void setTitleBarBackground(Color background)
  {
          getTitleBar().setBackground(background);
  }

  /**
   * Gets the background color of the title bar.
   * @return the background color
   */
  public Color getTitleBarBackground()
  {
          return getTitleBar().getBackground();
  }

  /**
   * Sets the height of the title bar.
   * @param height the height in pixels
   */
  public void setTitleBarHeight(int height)
  {
          getTitleBar().setPreferredSize(new Dimension(1500, height));
  }

  /**
   * Gets the height of the title bar.
   * @return the height in pixels
   */
  public int getTitleBarHeight()
  {
          return (int)getTitleBar().getPreferredSize().getHeight();
  }

  /**
   * Sets the foreground and the background colors of the title bar buttons.
   * @param foreground the foreground color
   * @param background the background color
   */
  public void setTitleBarButtonColors(Color foreground, Color background)
  {
          getTitleBar().setWindowButtonColors(background, foreground);
  }

  /**
   * Gets the background color of the title bar buttons.
   * @return the background color - note that since every title bar button can
   * have a different color, this will be the background color of the first title bar
   * button
   */
  public Color getTitleBarButtonBackground()
  {
          return getTitleBar().getWindowButtonBackground();
  }

  /**
   * Gets the foreground color of the title bar buttons.
   * @return the foreground color - note that since every title bar button can
   * have a different color, this will be the foreground color of the first title bar
   * button
   */
  public Color getTitleBarButtonForeground()
  {
          return getTitleBar().getWindowButtonForeground();
  }

  /**
   * Sets the size of the title bar buttons.
   * @param size the size in pixels
   */
  public void setTitleBarButtonSize(Dimension size)
  {
          getTitleBar().setWindowButtonSize(size);
  }

  /**
   * Gets the size of the title bar buttons.
   * @return the size - note that since every title bar button can
   * have a different size, this will be the size of the first title bar
   * button
   */
  public Dimension getTitleBarButtonSize()
  {
          return getTitleBar().getWindowButtonSize();
  }

  /**
   * Sets the border of the current icontent pane.
   * @param b the new border
   */
  public void setIContentPaneBorder(Border b)
  {
          getIContentPane().setBorder(b);
  }

  /**
   * Gets the border of the current icontent pane.
   * @return the border of the icontent pane
   */
  public Border getIContentPaneBorder()
  {
          return getIContentPane().getBorder();
  }

  /**
   * Sets the border of the current title bar.
   * @param b the new border
   */
  public void setTitleBarBorder(Border b)
  {
          getTitleBar().setBorder(b);
  }

  /**
   * Gets the border of the current title bar.
   * @return the border of the title bar
   */
  public Border getTitleBarBorder()
  {
          return getTitleBar().getBorder();
  }

  /**
   * Sets the bounds of the frame.
   * @param x the x coordinate for the upper left corner of the frame
   * @param y the y coordinate for the upper left corner of the frame
   * @param width the width of the frame
   * @param height the height of the frame
   */
  public void setBounds(int x, int y, int width, int height)
  {
          super.setBounds(x, y, width, height);
          if (getRootPane() != null)
                  getContentPane().setBounds(0, 0, width, height);
          repaint();
  }

  /**
   * Repaints the frame and all of its children.
   */
  public void repaint()
  {
          super.repaint();
          if (getRootPane() != null)
          {
                  getContentPane().invalidate();
                  getContentPane().validate();
          }
  }

  /**
   * Closes the frame and calls <code>System.exit(0)</code>.
   * @param e the WindowChangeEvent from one of its children
   */
  public void windowClosed(WindowChangeEvent e)
  {
    setVisible(false);
  }

  /**
   * Computes the restore size of the frame based on the Windows standard of 2 restore
   * states - restore maximized and restore minimized.
   * @param e the WindowChangeEvent from one of its children.
   */
  public void windowMaximized(WindowChangeEvent e)
  {
      if (!this.isResizable()) return;
          // if the app is already maximized
          if (getWidth() == IWTUtilities.getScreenWidth() && getHeight() == IWTUtilities.getScreenHeight())
          {
                  // the app just opened and they don't have a restore size yet
                  if (RESTORE_WIDTH == IWTUtilities.getScreenWidth() && RESTORE_HEIGHT == IWTUtilities.getScreenHeight())
                          setSize(IWTUtilities.getRestoreSize());
                  // they've resized already
                  else
                          setSize(RESTORE_WIDTH, RESTORE_HEIGHT);
                  RESTORE_WIDTH = getWidth();
                  RESTORE_HEIGHT = getHeight();
                  super.setLocation(RESTORE_X, RESTORE_Y);
          }
          // it's not maximized
          else
          {
                  setSize(IWTUtilities.getScreenWidth(), IWTUtilities.getScreenHeight());
                  super.setLocation(0, 0);
          }
          repaint();
          setVisible(true);
  }

  /**
   * Minimizes the frame by minimizing it in the native OS's window management.
   * @param e the WindowChangeEvent from one of its children
   */
  public void windowMinimized(WindowChangeEvent e) {
    JFrame frame = SkinManager.instance.getFrame("main_frame");
    if (frame instanceof IFrame) {
      IFrame iframe = (IFrame)frame;
      iframe.windowMinimized(e);
    }
  }

  /**
   * Moves the frame.
   * @param e the WindowChangeEvent from one of its children
   */
  public void windowMoved(WindowChangeEvent e)
  {
      boolean veto = false;
	    int buffer = 15;
	    if (getLocation().x + e.getChangeX() > IWTUtilities.getScreenWidth() - buffer* 3) veto = true;
	    if (getLocation().x + e.getChangeX() + getWidth() < buffer * 5) veto = true;
	    if (getLocation().y + e.getChangeY() > IWTUtilities.getScreenHeight() - buffer) veto = true;
	    if (getLocation().y + e.getChangeY() + buffer < 0) veto = true;
	    if (veto) {
	        //if (debug) System.out.println("windowMoved(): move vetoed!");
	    } else {      
          super.setLocation(getLocation().x + e.getChangeX(), getLocation().y + e.getChangeY());
          // BUG FIX - Restore coordinates were getting reset to 0 on window maximizes
          RESTORE_X = (getLocation().x == 0) ? RESTORE_X : getLocation().x;
          RESTORE_Y = (getLocation().y == 0) ? RESTORE_Y : getLocation().y;
          repaint();
	    }
  }

  /**
   * Resizes the frame.
   * @param e the WindowChangeEvent from one of its children
   */
  public void windowResized(WindowChangeEvent e)
  {
    if (!isResizable()) return;
    setVisible(true);
          if (!r.isResizing())
          {
                  Thread t = new Thread(r);
                  t.start();
                  r.update(getLocation().x, getLocation().y, getWidth(), getHeight());
          }
          // they're still resizing
//          if (e.isDragging())
          {
                  switch (e.getDirection())
                  {
                          case WindowChangeEvent.RESIZE_EAST :
                                  {
                                          r.update(getLocation().x, getLocation().y, e.getPosX(), getHeight());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH :
                                  {
                                          r.update(
                                                          (int) getLocation().getX(),
                                                          (int) getLocation().getY() + e.getChangeY(),
                                                          getWidth(),
                                                          getHeight() - e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH_EAST :
                                  {
                                          r.update(
                                                          (int) getLocation().getX(),
                                                          (int) getLocation().getY() + e.getChangeY(),
                                                          e.getPosX(),
                                                          getHeight() - e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH_WEST :
                                  {
                                          r.update(
                                                          (int) getLocation().getX() + e.getChangeX(),
                                                          (int) getLocation().getY() + e.getChangeY(),
                                                          getWidth() - e.getChangeX(),
                                                          getHeight() - e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH :
                                  {
                                          r.update(getLocation().x, getLocation().y, getWidth(), e.getPosY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH_EAST :
                                  {
                                          r.update(getLocation().x, getLocation().y, e.getPosX(), e.getPosY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH_WEST :
                                  {
                                          r.update(
                                                          (int) getLocation().getX() + e.getChangeX(),
                                                          (int) getLocation().getY(),
                                                          getWidth() - e.getChangeX(),
                                                          e.getPosY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_WEST :
                                  {
                                          r.update(
                                                          (int) getLocation().getX() + e.getChangeX(),
                                                          (int) getLocation().getY(),
                                                          getWidth() - e.getChangeX(),
                                                          getHeight());
                                          break;
                                  }
                  }
          }
          // they're done dragging, paint the window

          if (!e.isDragging())
              r.stopResizing();          
          
          {

                  switch (e.getDirection())
                  {
                          case WindowChangeEvent.RESIZE_EAST :
                                  {
                                          setSize(e.getPosX(), getHeight());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH :
                                  {
                                          setSize(getWidth(), getHeight() - e.getChangeY());
                                          super.setLocation((int) getLocation().getX(), (int) getLocation().getY() + e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH_EAST :
                                  {
                                          setSize(e.getPosX(), getHeight() - e.getChangeY());
                                          super.setLocation((int) getLocation().getX(), (int) getLocation().getY() + e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_NORTH_WEST :
                                  {
                                          setSize(getWidth() - e.getChangeX(), getHeight() - e.getChangeY());
                                          super.setLocation((int) getLocation().getX() + e.getChangeX(),(int) getLocation().getY() + e.getChangeY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH :
                                  {
                                          setSize(getWidth(), e.getPosY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH_EAST :
                                  {
                                          setSize(e.getPosX(), e.getPosY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_SOUTH_WEST :
                                  {
                                          setSize(getWidth() - e.getChangeX(), e.getPosY());
                                          super.setLocation((int) getLocation().getX() + e.getChangeX(), (int) getLocation().getY());
                                          break;
                                  }
                          case WindowChangeEvent.RESIZE_WEST :
                                  {
                                          setSize(getWidth() - e.getChangeX(), getHeight());
                                          super.setLocation((int) getLocation().getX() + e.getChangeX(), (int) getLocation().getY());
                                          break;
                                  }
                  }
                  RESTORE_WIDTH = getWidth();
                  RESTORE_HEIGHT = getHeight();
                  RESTORE_X = getLocation().x;
                  RESTORE_Y = getLocation().y;
                  getTitleBar().setRestoreButtonState(true);
                  repaint();
          }
  }

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowOpened(WindowEvent e)
  {
  }

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowClosing(WindowEvent e)
  {
  }

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowClosed(WindowEvent e)
  {
  }

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowIconified(WindowEvent e)
  {
  }

  /**
   * Recaptures the screen shot if transparency is turned on.
   * @param e the WindowEvent
   */
  public void windowDeiconified(WindowEvent e)
  {
//		setVisible(false);
//		setVisible(true);
  }

  boolean isActive = false;

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowActivated(WindowEvent e)
  {
          if (!isActive)
          {
//			setVisible(false);
//			setVisible(true);
                  isActive = true;
          }
  }

  /**
   * Does nothing.
   * @param e the WindowEvent
   */
  public void windowDeactivated(WindowEvent e)
  {
  }

  /**
   * Captures mouse clicks from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseClicked(MouseEvent e)
  {
          getBorderComponent(e.getX(), e.getY()).mouseClicked(e);
  }

  /**
   * Captures mouse presses from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mousePressed(MouseEvent e)
  {
          pressedComponent = getBorderComponent(e.getX(), e.getY());
          if (pressedComponent.isTransparent(e.getX(), e.getY()))
          {
                  toBack();
          }
          else
                  pressedComponent.mousePressed(e);
  }

  /**
   * Captures mouse releases from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseReleased(MouseEvent e)
  {
          pressedComponent.mouseReleased(e);
  }

  /**
   * Captures mouse entries from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseEntered(MouseEvent e)
  {
          getBorderComponent(e.getX(), e.getY()).mouseEntered(e);
  }

  /**
   * Captures mouse exits from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseExited(MouseEvent e)
  {
          getBorderComponent(e.getX(), e.getY()).mouseExited(e);
  }

  /**
   * Captures mouse drags from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseDragged(MouseEvent e)
  {
          pressedComponent.mouseDragged(e);
  }

  /**
   * Captures mouse movements from the root pane and relays them to the
   * component at the event's coordinates.
   * @param e the MouseEvent from the root pane
   */
  public void mouseMoved(MouseEvent e)
  {
          getBorderComponent(e.getX(), e.getY()).mouseMoved(e);
  }

  /**
   * Returns the IBorderComponent at the given coordinates in the frame.
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the iborder component at the given mouse coordinates
   */
  protected IBorderComponent getBorderComponent(int x, int y)
  {
          if (y < getTitleBarHeight())
                  return getTitleBar();
          else
                  return getIContentPane();
  }

  /**
   * The ResizeThread takes charge of resizing and revalidating the frame.  Without
   * the resize thread, resizing of the frame results in flickering and unsmooth
   * resizing.  By using the resize thread, flickering is removed and the resizing
   * is much smoother.
   * @author MAbernethy
   */
  protected class ResizeThread implements Runnable
  {
          private int x;
          private int y;
          private int width;
          private int height;
          private boolean isResizing;
          private JDialog w;

          /**
           * Creates a ResizeThread
           * @param w the frame instance getting resized
           * @param x the current x coordinate
           * @param y the current y coordinate
           * @param width the width of the frame
           * @param height the height of the frame
           */
          public ResizeThread(JDialog w, int x, int y, int width, int height)
          {
                  this.w = w;
                  this.x = x;
                  this.y = y;
                  this.width = width;
                  this.height = height;
          }

          /**
           * Updates the size and location of the frame.
           * @param x the new x coordinate
           * @param y the new y coordinate
           * @param width the new width
           * @param height the new height
           */
          public void update(int x, int y, int width, int height)
          {

                  this.x = x;
                  this.y = y;
                  this.width = width;
                  this.height = height;
          }

          /**
           * Tells the thread to stop resizing the frame.
           */
          public void stopResizing()
          {
                  isResizing = false;
          }

          /**
           * Returns whether the thread is currently resizing the frame.
           * @return whether the thread is resizing
           */
          public boolean isResizing()
          {
                  return isResizing;
          }

          /**
           * Resizes the frame and revalidates the content pane.
           */
          public void run()
          {
                  isResizing = true;
                  while (isResizing)
                  {
                          w.setBounds(x, y, width, height);
                          w.getContentPane().invalidate();
                          w.getContentPane().validate();
                          try
                          {
                                  Thread.sleep(300);
                          }
                          catch (InterruptedException e){}
                  }
          }
  }

  /**
   * The default border that appears around the frame in Windows 2000.
   * @author MAbernethy
   */
  protected class DefaultBorder extends AbstractBorder
  {
          public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
          {
                  g.setColor(c.getBackground().brighter().brighter());
          g.drawLine(0, 0, 0, h-1);

          g.setColor(c.getBackground().brighter());
          g.drawLine(1, 1, 1, h-2);

          g.setColor(c.getBackground().darker().darker());
          g.drawLine(1, h-1, w-1, h-1);
          g.drawLine(w-1, 1, w-1, h-2);

          g.setColor(c.getBackground().darker());
          g.drawLine(2, h-2, w-2, h-2);
          g.drawLine(w-2, 2, w-2, h-3);
          }
	}
}
