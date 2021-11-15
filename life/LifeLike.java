package life;

import java.awt.Color ;
import java.util.Set;

import bigGrid.BigGrid.Boundary ;

public interface LifeLike {
	/** Reset this Life-like game to its initial state.
	 * Typically this is all blank. */
	void reset() ;
	
	/** Change the color of one cell to a new color.
	 * Typically this will be 
	 * @param x
	 * @param y
	 */
	void toggle( int x, int y) ;
	void step() ;

	void setColor( int x, int y, Color color) ;
	Color getColor(int x, int y) ;

	Boundary getBoundary() ;

	Set<Color> getPalette();
	Color getDefaultColor() ;

}
