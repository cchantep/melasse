package melasse;

import java.util.GregorianCalendar;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;

import javax.swing.JPanel;

/**
 * Analog clock component.
 */
public class AnalogClock extends JPanel {
    // --- Properties ---

    /**
     * Seconds thickness
     * [2]
     */
    private int minThickness = 2;

    /**
     */
    private Image backgroundImage = null;

    /**
     */
    private Calendar calendar = null;

    /**
     * [RED]
     */
    private Color hourColor = Color.RED;

    /**
     * [BLUE]
     */
    private Color minuteColor = Color.BLUE;

    /**
     * [BLACK]
     */
    private Color secondColor = Color.BLACK;

    /**
     */
    private int[] x = new int[2];

    /**
     */
    private int[] y = new int[2];

    /**
     */
    private Timer timer = null;

    /**
     * You could set the TimeZone for the clock here. 
     * I used the default time zone from the user so that every 
     * time the program runs on different computers the correct 
     * time is displayed
     */
    private TimeZone timeZone = null;

    /**
     * Property change support
     */
    private PropertyChangeSupport pcs = null;

    // --- Constructors ---

    /**
     * No-arg constructor
     */
    public AnalogClock() {
	this.timer = new Timer();
	this.timeZone = TimeZone.getDefault();
	this.calendar = GregorianCalendar.getInstance(timeZone);

	this.setPreferredSize(new Dimension(210, 210));
	this.setMinimumSize(new Dimension(210, 210));

	this.pcs = new PropertyChangeSupport(this);

	//schedules the clock timer task to scan for every 1000ms=1sec
	timer.schedule(new TickTimerTask(), 0, 1000);
    } // end of <init>

    // --- Properties accessors ---

    /**
     * Sets |hours|.
     *
     * @param hours New hours
     */
    public void setHours(int hours) {
	int old = getHours();

	calendar.set(Calendar.HOUR_OF_DAY, hours);

	this.pcs.firePropertyChange("hours", old, hours);
    } // end of setHours

    /**
     * Returns hours.
     */
    public int getHours() {
	return calendar.get(Calendar.HOUR_OF_DAY);
    } // end of getHours

    /**
     * Sets |minutes|.
     *
     * @param minutes New minutes
     */
    public void setMinutes(int minutes) {
	int old = getMinutes();

	calendar.set(Calendar.MINUTE, minutes);

	this.pcs.firePropertyChange("minutes", old, minutes);
    } // end of setMinutes

    /**
     * Returns minutes.
     */
    public int getMinutes() {
	return calendar.get(Calendar.MINUTE);
    } // end of getMinute

    /**
     * Sets |seconds|.
     *
     * @param seconds New seconds
     */
    public void setSeconds(int seconds) {
	int old = getSeconds();

	calendar.set(Calendar.SECOND, seconds);

	this.pcs.firePropertyChange("seconds", old, seconds);
    } // end of setSeconds

    /**
     * Returns seconds.
     */
    public int getSeconds() {
	return calendar.get(Calendar.SECOND);
    } // end of getSeconds

    /**
     * Sets minimum |thickness| (for seconds).
     *
     * @param thickness Minimum thickness
     */
    public void setMinimumThickness(int thickness) {
	this.minThickness = thickness;
    } // end of setMinimumThickness

    /**
     * Returns minimum thickness.
     */
    public int getMinimumThickness() {
	return this.minThickness;
    } // end of getMinimumThickness

    /**
     * Sets background |image|.
     *
     * @param image New background image.
     */
    public void setBackgroundImage(Image image) {
	this.backgroundImage = image;
    } // end of setBackgroundImage

    /**
     * Returns background image.
     */
    public Image getBackgroundImage() {
	return this.backgroundImage;
    } // end of getBackgroundImage

    /**
     * Sets hour |color|.
     *
     * @param color New hour color.
     */
    public void setHourColor(Color color) {
	this.hourColor = color;
    } // end of setHourColor

    /**
     * Returns hour color.
     */
    public Color getHourColor() {
	return this.hourColor;
    } // end of getHourColor

    /**
     * Sets minute |color|.
     *
     * @param color New minute color.
     */
    public void setMinuteColor(Color color) {
	this.minuteColor = color;
    } // end of setMinuteColor

    /**
     * Returns minute color.
     */
    public Color getMinuteColor() {
	return this.minuteColor;
    } // end of getMinuteColor

    /**
     * Sets second |color|.
     *
     * @param color New second color.
     */
    public void setSecondColor(Color color) {
	this.secondColor = color;
    } // end of setSecondColor

    /**
     * Returns second color.
     */
    public Color getSecondColor() {
	return this.secondColor;
    } // end of getSecondColor

    // --- Bean support ---

    /**
     * Adds property change |listener|.
     *
     * @param listener Listener ed to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.addPropertyChangeListener(listener);
    } // end of addPropertyChangeListener

    /**
     * Removes property change |listener|.
     *
     * @param listener Listener ed to be removeed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.removePropertyChangeListener(listener);
    } // end of removePropertyChangeListener

    // --- Painting ---

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics g) {
	if (this.backgroundImage != null) {
	    g.drawImage(this.backgroundImage, 0, 0, this);
	} else {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	} // end of else

	drawCardinals((Graphics2D) g);

	drawHands((Graphics2D) g);
    } // end of paint

    /**
     * Calculte endpoints of the clock hand.
     */
    private void clockMinutes(int startRadius,
			      int endRadius,
			      double theta) {
	
	theta -= Math.PI/2;

	x[0] = (int) (getWidth() / 2 + startRadius * Math.cos(theta));
	y[0] = (int) (getHeight() / 2 + startRadius * Math.sin(theta));

	x[1] = (int) (getWidth() / 2 + endRadius * Math.cos(theta));
	y[1] = (int) (getHeight() / 2 + endRadius * Math.sin(theta));
    } // end of clockMinutes

    /** 
     * Sets stroke sets the thickness of the cardinals and hands.
     */
    private void drawCardinals(Graphics2D g) {
	g.setStroke(new BasicStroke(9));
	g.setColor(Color.black);
	
	for (double theta = 0; theta < Math.PI*2; theta += Math.PI / 6) {
	    clockMinutes(100, 100, theta);
	    /*
	     * Draws a sequence of connected lines defined by arrays of x and
	     * y coordinates. Each pair of (x, y) coordinates defines a point.
	     * The figure is not closed if the first point
	     * differs from the last point.
	     */
	//g.drawPolyline(x, y, 2);
	} // end of for
    } // end of drawCardinals

    /**
     * The Hand of the clocks instance method.
     */
    private void drawHands(Graphics2D g) {
	double h = 2 * Math.PI * (calendar.get(Calendar.HOUR));
	double m = 2 * Math.PI * (calendar.get(Calendar.MINUTE));
	double s = 2 * Math.PI * (calendar.get(Calendar.SECOND));

	// hours
	g.setStroke(new BasicStroke(minThickness+4));

	clockMinutes(0, 55, h / 12 + m / (60 * 12));
	g.setColor(this.hourColor);
	g.drawPolyline(x, y, 2);

	// minutes
	g.setStroke(new BasicStroke(minThickness+2));

	clockMinutes(0, 70, m / 60 + s / (60 * 60));
	g.setColor(this.minuteColor);
	g.drawPolyline(x, y, 2);

	// seconds
	g.setStroke(new BasicStroke(minThickness));

	clockMinutes(0, 70, s / 60);
	g.setColor(this.secondColor);
	g.drawPolyline(x, y, 2);

	// axis
	g.fillOval(getWidth() / 2 - 8, getHeight() / 2 - 8, 16, 16);
    } // end of drawHands

    /**
     * Updates/refreshes the clock every second.
     */
    private class TickTimerTask extends TimerTask {

	/**
	 * {@inheritDoc}
	 */
	public void run() {
	    int oldh = calendar.get(Calendar.HOUR_OF_DAY);
	    int oldm = calendar.get(Calendar.MINUTE);
	    int olds = calendar.get(Calendar.SECOND);

	    calendar.add(Calendar.SECOND, 1);

	    int newh = calendar.get(Calendar.HOUR_OF_DAY);
	    int newm = calendar.get(Calendar.MINUTE);
	    int news = calendar.get(Calendar.SECOND);

	    pcs.firePropertyChange("hours", oldh, newh);
	    pcs.firePropertyChange("minutes", oldm, newm);
	    pcs.firePropertyChange("seconds", olds, news);

	    repaint();
	} // end of run
    } // end of class TickTimerTask
} // end of class Clock
