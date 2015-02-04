package core.base;

import java.io.File; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import core.control.Control;

public class Main {
	
	/**
	 * load native libraries based on Windows-Version
	 */
	static {		
		try {
			if (System.getProperty("sun.arch.data.model").equals("32")) {
				
				System.loadLibrary("xboxcontroller");
				System.loadLibrary("JRiftLibrary");
				System.loadLibrary("opencv_java248");
				System.loadLibrary("Leap32");
				System.loadLibrary("LeapJava32");
//				loadJarDll("/resources/native32bit/xboxcontroller.dll");
//				loadJarDll("/resources/native32bit/JRiftLibrary.dll");
//				loadJarDll("/resources/native32bit/opencv_java248.dll");
			}
			else {
				System.loadLibrary("xboxcontroller64");
				System.loadLibrary("JRiftLibrary64");
				System.loadLibrary("opencv_java248x64");
				System.loadLibrary("Leap");
				System.loadLibrary("LeapJava");
//				loadJarDll("/resources/native64bit/xboxcontroller64.dll");
//				loadJarDll("/resources/native64bit/JRiftLibrary64.dll");				
//				loadJarDll("/resources/native64bit/opencv_java248.dll");
			}
			
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	public static void main(String[] args) {

		try {
			Control.start();
		} catch (Exception e) {		
			
			JOptionPane.showMessageDialog(new JFrame(),
			    e.getMessage(),
			    "Error",
			    JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
			System.exit(0);
		}	

	}
	
	/**
	 * Test for storing dll's in packages
	 */
	public static void loadJarDll(String name) throws IOException {
	    InputStream in = Main.class.getResourceAsStream(name);
	    byte[] buffer = new byte[1024];
	    int read = -1;
	    File temp = File.createTempFile(name, "");
	    FileOutputStream fos = new FileOutputStream(temp);

	    while((read = in.read(buffer)) != -1) {
	        fos.write(buffer, 0, read);
	    }
	    fos.close();
	    in.close();

	    System.load(temp.getAbsolutePath());
	}

}
