/**
 * 
 */
package bigGrid;

import java.util.ArrayList;

/**
 * @author theo
 *
 */
public class DenseBigGrid<T> implements BigGrid<T> {
	
	/** Representation relation.
	 * Let g be the grid represented by this object.
	 * 
	 * Concrete object Invariant: northEast != null
	 *         and northWest != null
	 *         and southEast != null
	 *         and southWest != null
	 *         and for all c in 0 to northEast.size(), northEast.get(c) != null
	 *         and for all c in 0 to northWest.size(), northWest.get(c) != null
	 *         and for all c in 0 to southEast.size(), southEast.get(c) != null
	 *         and for all c in 0 to southWest.size(), southWest.get(c) != null
	 * Rep relation:
	 *     for all x, y in int
	 *         if x >= 0 && y >= 0 then
	 *             northEast.size() > x 
	 *             and northEast.get(x).size() > y
	 *             and northEast.get(x).get(y).equals( g(x,y) )
	 *          or
	 *             g(x,y) == defaultValue and northEast.size() <= x 
	 *          or
	 *             g(x,y) == defaultValue and northEast.size() > x
	 *             and northEast.get(x).size() <= y
	 *         else if x < 0 and y >= 0 then
	 *             northWest.size() > (-1)-x 
	 *             and northWest.get((-1)-x).size() > y
	 *             and northWest.get((-1)-x).get(y).equals( g(x,y) )
	 *          or
	 *             g(x,y) == defaultValue and northWest.size() <= (-1)-x 
	 *          or
	 *             g(x,y) == defaultValue and northWest.size() > (-1)-x
	 *             and northWest.get((-1)-x).size() <= y
	 *        else if x >= 0 and y < 0 then
	 *             southEast.size() > x 
	 *             and southEast.get(x).size() > (-1)-y
	 *             and southEast.get(x).get((-1)-y).equals( g(x,y) )
	 *          or
	 *             g(x,y) == defaultValue and southEast.size() <= x 
	 *          or
	 *             g(x,y) == defaultValue and southEast.size() > x
	 *             and southEast.get(x).size() <= (-1)-y
	 *        else // x < 0 and y < 0 
	 *             southWest.size() > (-1)-x 
	 *             and southWest.get((-1)-x).size() > (-1)-y
	 *             and southWest.get((-1)-x).get((-1)-y).equals( g(x,y) )
	 *          or
	 *             g(x,y) == defaultValue and southEast.size() <= (-1)-x 
	 *          or
	 *             g(x,y) == defaultValue and southEast.size() > (-1)-x
	 *             and southEast.get((-1)-x).size() <= (-1)-y
	 *     		
	 *        
	 * 
	 */
	
	private ArrayList<ArrayList<T>> northEast = new ArrayList<>() ;
	private ArrayList<ArrayList<T>> northWest = new ArrayList<>() ;
	private ArrayList<ArrayList<T>> southEast = new ArrayList<>() ;
	private ArrayList<ArrayList<T>> southWest = new ArrayList<>() ;
	private T defaultValue ;

	public DenseBigGrid(T defaultValue) {
		this.defaultValue = defaultValue ;
	}

	@Override
	public void setDefaultValue(T v) {
		northEast = new ArrayList<>() ;
		northWest = new ArrayList<>() ;
		southEast = new ArrayList<>() ;
		southWest = new ArrayList<>() ;
		this.defaultValue = v ;
	}

	@Override
	public void setCellValue(int x, int y, T v) {
		ArrayList<ArrayList<T>> listOfColumns ;
		int row ;
		int col ;
		if( x >= 0 && y >= 0 ) {
			listOfColumns = northEast ;
			row = y ;
			col = x ;
		} else if( x < 0 && y >= 0 ) {
			listOfColumns = northWest ;
			row = y ;
			col = (-1)-x ;
		} else if( x >= 0 && y < 0 ) {
			listOfColumns = southEast ;
			row = (-1)-y ;
			col = x ;
		} else {
			listOfColumns = southWest ;
			row = (-1)-y ;
			col = (-1)-x ;
		}
		if( v == defaultValue && getCellValue(x, y) == defaultValue ) {
			// There is nothing to be done.
		} else {
			// Either v is not default or it is default, but there is
			// already an item at the location. Either way, we do the
			// same thing.
			
			// Add new columns until there is a column for this entry
			while( col >= listOfColumns.size() ) {
				listOfColumns.add( new ArrayList<T>() ) ; }
			ArrayList<T> column = listOfColumns.get( col ) ;
			// Add new rows to the column until there is a place for
			// the value
			while( row >= column.size() ) {
				column.add( defaultValue ) ;
			}
			// Now we have a place to put the value.
			column.set( row, v) ;
			
			// The remaining part is optional. It saves space
			// but will take time.  However it will save time
			// in getBoundary.
			
			// Trim the column until it is empty or the last item is
			// not default
			int lastIndex = column.size() - 1 ;
			while( lastIndex >= 0 && column.get( lastIndex) == defaultValue) {
				column.remove( lastIndex ) ;
				lastIndex -= 1 ;
			}
			// Trim the column list to remove empty columns at the end
			lastIndex = listOfColumns.size() - 1 ;
			while( lastIndex >= 0 && listOfColumns.get(lastIndex).size()==0 ) {
				listOfColumns.remove( lastIndex ) ;
				lastIndex -= 1 ;
			}
		}

	}

	@Override
	public T getCellValue(int x, int y) {
		ArrayList<ArrayList<T>> listOfColumns ;
		int row ;
		int col ;
		if( x >= 0 && y >= 0 ) {
			listOfColumns = northEast ;
			row = y ;
			col = x ;
		} else if( x < 0 && y >= 0 ) {
			listOfColumns = northWest ;
			row = y ;
			col = (-1)-x ;
		} else if( x >= 0 && y < 0 ) {
			listOfColumns = southEast ;
			row = (-1)-y ;
			col = x ;
		} else {
			listOfColumns = southWest ;
			row = (-1)-y ;
			col = (-1)-x ;
		}
		if( col >= listOfColumns.size() )
			return defaultValue ;
		else {
			ArrayList<T> column = listOfColumns.get( col ) ;
			if( row >= column.size() ) 
				return defaultValue ;
			else
				return column.get( row ) ;
		}
	}

	@Override
	public Boundary getBoundary() {

		int topNorthEast = biggestVertical(northEast)  ;
		int topNorthWest = biggestVertical(northWest) ;
		int top = Math.max(topNorthEast, topNorthWest) ;
		
		int bottomSouthEast = (-1) - biggestVertical( southEast ) ;
		int bottomSouthWest = (-1) - biggestVertical( southWest ) ;
		int bottom = Math.min(bottomSouthEast, bottomSouthWest) ;
		

		
		int rightNorthEast = biggestHorizontal( northEast ) ;
		int rightSouthEast = biggestHorizontal( southEast ) ;
		int right = Math.max( rightNorthEast, rightSouthEast ) ;
		
		int leftNorthEast = (-1) - biggestHorizontal( northWest ) ;
		int leftSouthEast = (-1) - biggestHorizontal( southWest ) ;
		int left = Math.min( leftNorthEast, leftSouthEast ) ;
		
		if( top == -1 && bottom == 0 ) {
			// In this case the whole grid is all the default value.
			return new Boundary(0, 0, 0, 0) ;
		} else {
			return new Boundary(top, right, bottom, left) ;
		}
	}
	
	// Return the column number of the farthest column that has something in it.
	int biggestHorizontal( ArrayList<ArrayList<T>> listOfColumns) {
		int col = listOfColumns.size() - 1 ;
		while( col >= 0  ) {
			ArrayList<T> column = listOfColumns.get( col ) ;
			int row = column.size() - 1 ;
			while( row >= 0) {
				if( column.get(row) != defaultValue ) {
					return col ; }
				row = row - 1 ; }
			col = col - 1 ;
		}
		return -1 ;
	}
	
	// Return the row number of the biggest row that is not default
	int biggestVertical( ArrayList<ArrayList<T>> listOfColumns) {
		int col = listOfColumns.size() - 1 ;
		int bestSoFar = -1 ;
		while( col >= 0  ) {
			ArrayList<T> column = listOfColumns.get( col ) ;
			int row = column.size() - 1 ;
			while( row >= 0 && row > bestSoFar ) {
				if( column.get( row ) != defaultValue )
					bestSoFar = row ;
				row = row - 1 ; }
			col = col - 1 ; }
		return bestSoFar ;
	}

}
