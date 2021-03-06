/*
 * @version 0.0 15.04.2008
 * @author Tobse F
 */
package lib.mylib;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/** A basic Object on the Screen, which can be moved and drawn */
public class SObject implements Drawable, Updateable {
	protected int x, y;
	protected Image image = null;
	protected boolean isVisible = true;

	public SObject() {}

	public SObject(Image image) {
		this.image = image;
	}

	/**
	 * Sets an image which is drawn with draw
	 * 
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	public SObject(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SObject(Image image, int x, int y) {
		this(x, y);
		this.image = image;
	}

	/**
	 * Sets the X-Position on the Screen
	 * 
	 * @param x -in pixels
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the Y-Position on the Screen
	 * 
	 * @param y -in pixels
	 */
	public void setY(int y) {
		this.y = y;
	}

	public void setPos(int[] pos) {
		setPos(pos[0], pos[1]);
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Graphics g) {
		if (image != null) {
			g.drawImage(image, x, y);
		}
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	@Override
	public void update(int delta) {}

	/**
	 * @return ifTheCompomentShouldBeDrawn
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Sets wether the component should be drawn (true). It's depends on the draw()
	 * implementation wether it would be considered.
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}