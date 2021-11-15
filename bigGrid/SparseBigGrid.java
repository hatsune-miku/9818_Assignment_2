package bigGrid;

import java.util.Map;
import java.util.HashMap;


public class SparseBigGrid<T> implements BigGrid<T> {
	
	public static record Coordinate(int x, int y) { }
	
	private Map<Coordinate, T> map = new HashMap<Coordinate, T>() ;
	
	private T defaultValue;
	
	/* Representation relation:
	 *   For every x, y in int, 
	 *      if the value int this grid at (x,y) is the defaultValue
	 *        then (x,y) is in the set of keys of map
	 *             (! map.containsKey( new Coordinate(x,y) )
	 *      otherwise
	 *           the map contains (x,y) at a key and the value
	 *           at (x,y) is map.get( newCoordintate(x,y) )
	 */
	
	public SparseBigGrid(T defaultValue) {
		this.defaultValue = defaultValue ;
	}

	@Override
	public void setDefaultValue(T v) {
		map = new HashMap< >() ;
		defaultValue = v ;
	}

	@Override
	public void setCellValue(int x, int y, T v) {
		Coordinate coordinate = new Coordinate(x, y) ;
		if(  map.containsKey( coordinate ) ) {
			if( v.equals( defaultValue ) ) {
				map.remove( coordinate ) ;
			} else {
				map.remove( coordinate ) ;
				map.put( coordinate, v ) ;
			}
		} else {
			if( v.equals( defaultValue ) ) {
				// New and old values are the same 
			} else {
				map.put(coordinate, v) ;
			}
		}
	}

	@Override
	public T getCellValue(int x, int y) {
		Coordinate coordinate = new Coordinate(x, y) ;
		if(  map.containsKey( coordinate ) ) 
			return map.get( coordinate ) ;
		else 
			return defaultValue ;
	}

	@Override
	public Boundary getBoundary() {
		if( map.isEmpty() ) {
    		return new Boundary(0, 0, 0 , 0); }
		else {
			int t = Integer.MIN_VALUE ;
			int b = Integer.MAX_VALUE ;
			int r = Integer.MIN_VALUE ;
			int l = Integer.MAX_VALUE ;
			for(Coordinate c : map.keySet()  ) {
				if( c.y() > t ) t = c.y() ;
				if( c.y() < b ) b = c.y() ;
				if( c.x() > r ) r = c.x() ;
				if( c.x() < l ) l = c.x() ; 
			}
			return new Boundary(t, r, b, l) ;
		}
	}
}
