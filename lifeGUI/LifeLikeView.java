package lifeGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import bigGrid.BigGrid;
import bigGrid.BigGrid.Boundary;
import life.LifeLike;

public class LifeLikeView extends JPanel
{
	
	private static final long serialVersionUID = 237882347038933995L;
	private LifeLike game ;
	private ViewStrategy worldToViewMap;
	
	LifeLikeView( LifeLike game, ViewStrategy worldToViewMap ) {
		this.game = game ;
		this.worldToViewMap = worldToViewMap ;
		this.addMouseListener( new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				double vx = e.getX() ;
				double vy = e.getY() ;
				System.out.println( "Click at "+vx+" "+vy) ;
				int width = getWidth(), height = getHeight() ;
				BigGrid.Boundary bounds = game.getBoundary() ;
				Point2D w = LifeLikeView.this.worldToViewMap.viewToWorld(
								width, height,bounds, vx, vy ) ;
				int x = (int) Math.round( w.getX() ) ;
				int y = (int) Math.round( w.getY() ) ;
				System.out.println( "Toggling "+x+" "+y) ;
				game.toggle(x,y) ;
				LifeLikeView.this.repaint() ;
			}
		});
	}
	
    @Override protected void paintComponent( Graphics g ) {
    	super.paintComponent(g) ;
    	int width = getWidth(), height = getHeight() ;
    	
    	BigGrid.Boundary bounds = game.getBoundary() ;
    	
    	Point2D wTL = viewToWorld( width, height, bounds, 0.0, 0.0 ) ;
    	Point2D wTR = viewToWorld( width, height, bounds, width, 0.0 ) ;
    	Point2D wBL = viewToWorld( width, height, bounds, 0.0, height ) ;
    	Point2D wBR = viewToWorld( width, height, bounds, width, height ) ;
    	int worldLeft = (int) Math.floor( Math.min(wTL.getX(), wBL.getX() ) ) ;
    	int worldRight = (int) Math.floor( Math.max(wTR.getX(), wBR.getX() ) ) ;
    	int worldBottom = (int) Math.floor( Math.min(wBL.getY(), wBR.getY() ) );
    	int worldTop = (int) Math.floor( Math.max(wTL.getY(), wTR.getY() ) ) ;
    	
    	
    	Point2D p0, p1, p2, p3 ;
        /* Fill in the grid */ {
        	for( int x=worldLeft; x <=worldRight ; ++x ) {
        		for( int y=worldBottom; y <= worldTop ; ++y ) {

                    p0 = worldToView( width, height, bounds, x+0.5, y-0.5 ) ;
                    p1 = worldToView( width, height, bounds, x+0.5, y+0.5 ) ;
                    p2 = worldToView( width, height, bounds, x-0.5, y-0.5 ) ;
                    p3 = worldToView( width, height, bounds, x-0.5, y+0.5 ) ;
                    g.setColor( game.getColor(x,y) ) ;
                    paintFilledRectangle( g, p0, p1, p2, p3 ) ; } } }
            
	        /* Draw vertical lines */ {
	            for( int x=worldLeft; x <=worldRight+1 ; ++x ) {
	                p0 = worldToView( width, height, bounds, x-0.5, worldTop) ;
	                p1 = worldToView( width, height, bounds, x-0.5, worldBottom ) ;
		            g.setColor(x==0 || x==1? Color.red : Color.gray);
	                g.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY()) ; } }
	                
	        /* Draw horizontal lines */ {
	            for( int y=worldBottom ; y <= worldTop+1 ; ++y ) {
	                p0 = worldToView( width, height, bounds, worldLeft, y-0.5 ) ;
	                p1 = worldToView( width, height, bounds, worldRight, y-0.5 ) ;
	                g.setColor(y==0 || y==1 ? Color.red : Color.gray);
	                g.drawLine((int)p0.getX(), (int)p0.getY(), (int)p1.getX(), (int)p1.getY()) ; } }

    }
    
    private void paintFilledRectangle( Graphics g,
            Point2D tl, Point2D tr, Point2D bl, Point2D br ) {
		// Draw a rectangle.
		int [] xs = new int[]{ (int)tl.getX(), (int)tr.getX(), (int)br.getX(), (int)bl.getX() } ;
		int [] ys = new int[]{ (int)tl.getY(), (int)tr.getY(), (int)br.getY(), (int)bl.getY() } ;
		g.fillPolygon(xs, ys, 4); }

    
    private Point2D worldToView( double width, double height, Boundary bounds, double x, double y ) {
        return worldToViewMap.worldToView( width, height, bounds, x, y )  ; }
    
    private Point2D viewToWorld( double width, double height, Boundary bounds, double x, double y ) {
        return worldToViewMap.viewToWorld( width, height, bounds, x, y )  ; }
}
