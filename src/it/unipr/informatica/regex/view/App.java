package it.unipr.informatica.regex.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


public class App implements Runnable {
	
	private JFrame frame;
	private JPanel containerPanel;
	private JPanel statusBar;
	private JLabel status;
	private JTabbedPane tabs;
	private JMenuBar menuBar;
	private ReplacePanel replacePanel;
	private NFAPanel nfaPanel;
	private DFAPanel dfaPanel;
	private MatchPanel matchPanel;
	private ButtonPanel buttonPanel;
	
	public App() {
		buttonPanel = new ButtonPanel(this);
		matchPanel = new MatchPanel(this);
		replacePanel = new ReplacePanel();
		nfaPanel = new NFAPanel(this);
		dfaPanel = new DFAPanel(this);
	}
	
	@Override
	public void run() {
		// creating objects
		initComponents();
		
		// create menu
		initMenu();
		frame.setJMenuBar(menuBar);
		
		// setting window 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Regex");
		frame.setMinimumSize(new Dimension(500, 400));
		// open the windows in the center 
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(containerPanel);

		// setting main layout
		setLayout();
		
		// setting panel's components
		setPanelsComponents();
		
		// make the window visible
		frame.setVisible(true);
	}
	
	public void setStatus(String str) {
		status.setText(str);
	}
	
	private void setLayout() {
		// set panel layout
		containerPanel.setLayout(new BorderLayout());
		
		// set tabs
		tabs.addTab("Match", matchPanel);
		tabs.addTab("Replace", replacePanel);
		tabs.addTab("NFA", nfaPanel);
		tabs.addTab("DFA", dfaPanel);
		tabs.setTabPlacement(JTabbedPane.TOP);
		
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusBar, BorderLayout.SOUTH);
	
		statusBar.setPreferredSize(new Dimension(frame.getWidth(), 20));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setBorder(new EmptyBorder(7, 7, 7, 0));
		statusBar.add(status);
	}
	
	private void setPanelsComponents() {
		containerPanel.add(tabs, BorderLayout.CENTER);
		containerPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void initComponents() {
		containerPanel = new JPanel();
		tabs = new JTabbedPane();
		frame = new JFrame();
		statusBar = new JPanel();
		status = new JLabel("ready");
	}
	
	private void initMenu() {
		menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu converterMenu = new JMenu("Converter");
		JMenu helpMenu = new JMenu("Help");
		// file menu item
		JMenuItem openFileMenu = new JMenuItem("Open file");
		JMenuItem clearTextMenu = new JMenuItem("Clear search text");
		JMenuItem exitMenuMenu = new JMenuItem("Exit");
		// converter menu item
		JMenuItem parseRegexMenu = new JMenuItem("Parse regular expression");
		JMenuItem searchMenu = new JMenuItem("Search pattern");
		// edit menu item
		JMenuItem showNFAStateMenu = new JMenuItem("Build NFA");
		JMenuItem showDFAStateMenu = new JMenuItem("Build DFA");
		// help menu item
		JMenuItem aboutMenu = new JMenuItem("About");
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(converterMenu);
		menuBar.add(helpMenu);
		fileMenu.add(openFileMenu);
		fileMenu.add(clearTextMenu);
		fileMenu.add(exitMenuMenu);
		editMenu.add(showDFAStateMenu);
		editMenu.add(showNFAStateMenu);
		converterMenu.add(parseRegexMenu);
		converterMenu.add(searchMenu);
		
		helpMenu.add(aboutMenu);
		
		// adding listeners
		
		// open file lister
		openFileMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file", "txt", "cpp", "c", "html", "css", "sh",
			    															 "plist", "h", "py", "java", "js", "sql", "log", "tex");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       File file = chooser.getSelectedFile();
			       matchPanel.editSearchTextArea(file);
			    }
			}
		});
		
		// clear lister
		clearTextMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelTextArea();
			}
		});
		
		// exit listener
		exitMenuMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		// parse regex listener
		parseRegexMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parseRegex();
				status.setText("Regular expression parsed");
			}
		});
		
		// search pattern listener
		searchMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		
		// show NFA listener
		showNFAStateMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showNFA();
			}
		});
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// clear the nfa file
				try {
					// clear the file
					System.setOut(new PrintStream(new OutputStream() { public void write(int b) throws IOException {} }));
					
					// set nfa.log for the output of the console
					System.setOut(new PrintStream(new FileOutputStream("nfa.log")));
				} catch(Exception e1) {
					
				}
				System.out.println(" ");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
					
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
			}
		});
		
		showNFAStateMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showNFA();
			}
		});
		
		showDFAStateMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDFA();
			}
		});
	}
	
	public void search() {
		
	}
	
	public void cancelTextArea() {
		matchPanel.clearSearchTextArea();
	}
	
	public void parseRegex() {
		matchPanel.parseRegex();
	}
	
	public void showNFA() {
		File file = new File("nfa.log");
		nfaPanel.showNFA(file);
	}
	
	public void showDFA() {
		File file = new File("dfa.log");
		dfaPanel.showDFA(file);
	}
}
