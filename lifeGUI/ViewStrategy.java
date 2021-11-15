package lifeGUI;

import java.awt.geom.Point2D;

import bigGrid.BigGrid.Boundary;

public interface ViewStrategy {
	
	Point2D worldToView( double width, double height,
            			 Boundary bounds,
            			 double xw, double yw ) ;
	
	Point2D viewToWorld(
    		double width, double height,
            Boundary bounds,
            double xv, double yv ) ;
}
