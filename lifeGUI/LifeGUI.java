package lifeGUI;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.* ;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import life.AlphaCat;
import life.Gomoku;
import life.LifeLike;

public class LifeGUI extends JFrame {

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

		LifeGUI frame = new LifeGUI();


        //Centre the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

	private JTabbedPane tabbedPane;
	
    //Construct the frame
    public LifeGUI() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {

        	this.setTitle("Lifelike games");
        	JMenu fileMenu = new JMenu("File");
            JMenuItem exitMenuItem = new JMenuItem("Exit");
            exitMenuItem.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                	System.exit(0);
                }
            });
            JMenu helpMenu = new JMenu("Help");
            JMenuItem aboutMenuItem = new JMenuItem("About");
            aboutMenuItem.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                	JOptionPane.showMessageDialog(LifeGUI.this,
                			"Life World\n"
                			+"(c) Theodore Norvell\n"
                			+"Licence is granted to modify and redistribute\n"
                			+"this program in creative ways for fun and education.");
                }
            });
            JMenuBar menuBar = new JMenuBar();
            fileMenu.add(exitMenuItem);
            helpMenu.add(aboutMenuItem);
            menuBar.add(fileMenu);
            menuBar.add(helpMenu);
            this.setJMenuBar(menuBar);
            
            this.tabbedPane = new JTabbedPane() ;
            this.getContentPane().add( tabbedPane ) ;
            initializeOneTab(tabbedPane, "Life", new life.Life() );
            initializeOneTab(tabbedPane, "Life & Death", new life.LifeAndDeath() ) ;
            initializeOneTab(tabbedPane, "Gomoku", new Gomoku() );
            
        	
        	// The following lines help direct keyboard input to the controller
        	// of the currently selected tab.
        	ChangeListener cl = new ChangeListener() {
				@Override public void stateChanged(ChangeEvent e) {
					JComponent c = (JComponent) tabbedPane.getSelectedComponent() ;
					if( c != null ) c.requestFocusInWindow() ;
				} } ;
            this.tabbedPane.addChangeListener( cl) ;
			cl.stateChanged(null) ; // put the focus on the first tab.
        	
            setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** Initialize a tab on a tabbed pane to hold all
     * the GUI components associated with one Controller.
     * 
     * @param tabbedPane
     * @throws Exception
     */
    private void initializeOneTab(JTabbedPane tabbedPane, String name, LifeLike game ) throws Exception  {
    	JLabel statusBar = new JLabel();
    	JPanel buttonBar = new JPanel();
    	ViewStrategy viewStrategy = new BirdsEyeView() ;
    	JPanel view = new LifeLikeView(game, viewStrategy) ;
        JPanel panel = new JPanel() ;
       	panel.setLayout(new BorderLayout());
        
        // Here is GUI hierarchy:
        //  tabbedPane -- panel -- buttonBar
        //                      -- gamePanel
        //                      -- statusBar
        //             -- other panels ...
        
    	tabbedPane.addTab( name, panel) ;
        statusBar.setText("Ready.");
        panel.add(statusBar, BorderLayout.SOUTH);
        panel.add(buttonBar, BorderLayout.NORTH);
        panel.add(view, BorderLayout.CENTER);
        
        {
        	JButton button = new JButton( "Reset" ) ;
        	buttonBar.add( button ) ;
        	button.addActionListener( new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					game.reset() ;
					view.repaint() ; } } );
        }
        {
        	JButton button = new JButton( "Step" ) ;
        	buttonBar.add( button ) ;
        	button.addActionListener( new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					game.step() ; 
					view.repaint() ; } } );
        }
//        {
//        	JButton button = new JButton( "Go" ) ;
//        	buttonBar.add( button ) ;
//        	button.addActionListener( new ActionListener() {
//				@Override public void actionPerformed(ActionEvent e) {
//					/*TODO*/ } } );
//        }
//        {
//        	JButton button = new JButton( "Stop" ) ;
//        	buttonBar.add( button ) ;
//        	button.addActionListener( new ActionListener() {
//				@Override public void actionPerformed(ActionEvent e) {
//					/*TODO*/ } } );
//        }
        
        view.setVisible( true ); // Needed?
        
        //createButtonBar( buttonBar, controller ) ;
        //KeyListener keyListener = new Keys( controller ) ;
        //panel.addKeyListener( keyListener ) ;
        panel.setFocusable( true ) ;


        pack() ;
        this.setSize(new Dimension(800, 600));
        validate();
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                /* MainFrame.this.requestFocusInWindow(); */ }
        });
    }
}
