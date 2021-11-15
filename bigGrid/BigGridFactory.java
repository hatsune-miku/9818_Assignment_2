package bigGrid;

public class BigGridFactory<T> {

	public BigGrid<T> make( T defaultValue ) {
		return new DenseBigGrid<T>( defaultValue ) ;
	}
	
	public BigGrid<T> makeDense( T defaultValue ) {
		return new DenseBigGrid<T>( defaultValue ) ;
	}
	
	public BigGrid<T> makeSparse( T defaultValue ) {
		return new SparseBigGrid<T>( defaultValue ) ;
	}

}
