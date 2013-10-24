/**
 * 
 */
package com.lars_albrecht.mdb.main.core.models;

/**
 * @author lalbrecht
 * 
 *         A class for multiple different values. Use it for e.g. multiple
 *         result values.
 * 
 */
public class Tuple<X, Y> {

	private X	x;
	private Y	y;

	public Tuple() {
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Tuple(final X x, final Y y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public final X getX() {
		return this.x;
	}

	/**
	 * @return the y
	 */
	public final Y getY() {
		return this.y;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public final void setX(final X x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public final void setY(final Y y) {
		this.y = y;
	}

}