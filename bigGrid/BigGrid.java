/**
 * 
 */
package bigGrid;

/** BigGrid. Represent grid's of almost unbounded size.
 * 
 * Abstract value: A mutable 2-dimensional grid (array) of values indexed by integer
 * (int) values on both the x and y axes, together with a default value.
 * 
 * The grid extends in all directions.
 * 
 *
 * @author Theo
 *
 */
public interface BigGrid<T> {
	
	public static record Boundary( int top, int right, int bottom, int left) { }
	
	/** Set every cell to the value.
	 * @require true 
	 * @ensure the value of every grid cell after is v.
	 */
	public void setDefaultValue( T v ) ;
	
	/** Set one cell to a value.
	 * @require true
	 * @ensure the value cell (x, y) after is v
	 *     and the value of every other cell remains the same (value at (x0, y0)
	 *     after = the value at (x0, y0) before for every x0, y0 such that
	 *     x0 !=x or y0 != y) . 
	 * @param x
	 * @param y
	 * @param v 
	 */
	public void setCellValue( int x, int y, T v ) ;
	
	/**
	 * @require true
	 * @ensure result the value at cell (x,y) .
	 * @param x
	 * @param y
	 */
	public T getCellValue( int x, int y ) ;
	
	/** Returns the boundary of a rectangle that contains all nondefault values.
	 * @requires true
	 * @ensure result is such that if
	 *            x > result.right  or  x < result.left  or y > result.top  or y < result.bottom
	 *        then the value at (x,y) is the the default
	 *        and if there are no nondefault values then top == bottom == right == left == 0.
	 *        and result is a smallest such box.
	 * @return
	 */
	public Boundary getBoundary() ;

}
