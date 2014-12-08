package it.unipr.informatica.regex;
import it.unipr.informatica.regex.view.App;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Main {
	public static void main(String args[]) {
		// for OSX
		// setting the native menu
		try {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Regex");
			System.setProperty("com.apple.macos.useScreenMenuBar", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		} catch(SecurityException e) {
			// do nothing
		}
		
		// setting system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		} catch(UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		App app = new App();
		app.run();
	}
}
