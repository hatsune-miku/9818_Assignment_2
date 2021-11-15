package lifeGUI;

import java.awt.geom.Point2D;

import bigGrid.BigGrid.Boundary;

public class BirdsEyeView implements ViewStrategy {
	
	
	// Invariant: cachedBoundary == null
	//         or     worldLeft-worldRight >= 10.0
	//            and worldTop-worldBottom >= 10.0
	//            and viewWidth >= 1.0
	//            and viewHeight >= 1.0
	//            and v2wStretch = max( (worldRight - worldLeft) / viewWidth,
	//                                  (worldTop - worldBottom) / viewHeight)
	//            and w2vStretch = min( viewWidth / (worldRight - worldLeft),
	//                                  viewHeigth / (worldTop - worldBottom) )
	private double worldLeft ;
	private double worldRight ;
	private double worldBottom ;
	private double worldTop ;
	private double viewWidth ;
	private double viewHeight ;
	private double v2wStretch ;
	private double w2vStretch ;
	private Boundary cachedBoundary = null ;
	
	private void updateTransform( Boundary bounds, double width, double height ) {
		if( bounds != cachedBoundary || width != viewWidth || height != viewHeight ) {
			cachedBoundary = bounds ;
			if( width < 1) viewWidth = 1 ;
			else viewWidth = width ;
			if( height < 1 ) viewHeight = 1 ;
			else viewHeight = height ;
			// Ensure at least 2 cells beyond the boundary are visible.
			worldLeft = bounds.left() - 2.5 ;
			worldRight = bounds.right() + 2.5 ;
			worldBottom = bounds.bottom() - 2.5 ;
			worldTop = bounds.top() + 2.5 ;
			// Ensure that at least 10 grid squares can be seen horizontally
			double needed = 10 - (worldRight - worldLeft) ;
			if( needed > 0.0 ) {
				worldLeft = worldLeft - needed/2 ;
				worldRight = worldRight + needed/2 ;
			}
			// Ensure that at least 10 grid squares can be seen vertically
			needed = 10 - (worldTop - worldBottom) ;
			if( needed > 0.0 ) {
				worldBottom = worldBottom - needed/2 ;
				worldTop = worldTop + needed/2 ;
			}
			double hStretch = (worldRight - worldLeft) / viewWidth ;
			double vStretch = (worldTop - worldBottom) / viewHeight ;
			v2wStretch = Math.max(hStretch, vStretch) ;
			w2vStretch = 1.0 / v2wStretch ;
			System.out.println("worldLeft " + worldLeft ) ;
			System.out.println("worldRight " + worldRight ) ;
			System.out.println("worldBottom " + worldBottom ) ;
			System.out.println("worldTop " + worldTop ) ;
			System.out.println("viewWidth " + viewWidth ) ;
			System.out.println("viewHeight " + viewHeight ) ;
			System.out.println("v2wStretch " + v2wStretch ) ;
			System.out.println("w2vStretch " + w2vStretch ) ;
		}
	}
    
    public Point2D worldToView( double width, double height,
    		                    Boundary bounds,
    		                    double xw, double yw )
    {
    	updateTransform( bounds, width, height ) ;
        double x1 = xw - worldLeft ;
        double y1 = worldTop - yw ;
        double xv = x1 * w2vStretch ;
        double yv = y1 * w2vStretch ;
        return new Point2D.Double( xv, yv ) ; }

    
    public Point2D viewToWorld(
    		double width, double height,
            Boundary bounds,
            double xv, double yv )
    {
    	updateTransform( bounds, width, height ) ;
        double x1 = xv * v2wStretch ;
    	double y1 = yv * v2wStretch ;
    	double xw = x1 + worldLeft ;
    	double yw = worldTop - y1 ;
    	System.out.println( "View to world ("+xv+", "+yv+") -> ("+x1+", "+y1+") -> ("+xw+", "+yw+")" ) ;
        return new Point2D.Double( xw, yw ) ; }
}