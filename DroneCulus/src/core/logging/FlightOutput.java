package core.logging;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * View of FlightOutput
 *
 */
public class FlightOutput extends JFrame {
	
	private static final long 		serialVersionUID = 1L;
	private PrintStream 			printstream;
	
	private static final String		LOG_FOLDER_NAME = "log_files";
	
	public FlightOutput() {
		
		super("Flight Output");
		add( new JLabel(" Output" ), BorderLayout.NORTH );
		JTextArea ta = new JTextArea();
	    TextAreaOutputStream taos = new TextAreaOutputStream( ta , "INFO");
	    FileOutputStream file = null;
		try {
			if (!new File(LOG_FOLDER_NAME).exists()) {
				File dir = new File(LOG_FOLDER_NAME);
				dir.mkdir();
			}
			file = new FileOutputStream(LOG_FOLDER_NAME + "/FlightLog.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    setPrintstream(new TextAreaPrintStream( file, taos ));
	    add( new JScrollPane( ta )  );
	    pack();
	    
        setSize(600,400);
    	setVisible( false );        
    	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    
	}

	public PrintStream getPrintstream() {
		return printstream;
	}

	public void setPrintstream(PrintStream printstream) {
		this.printstream = printstream;
	}

}



