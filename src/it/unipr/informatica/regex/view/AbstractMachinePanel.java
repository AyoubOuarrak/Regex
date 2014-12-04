package it.unipr.informatica.regex.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import it.unipr.informatica.regex.App;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class AbstractMachinePanel extends JPanel {
	private JButton showNFAbutton;
	private JTextArea textNFA;
	private JLabel labelNFA;
	private JScrollPane scrollPane;
	App app;
	
	public AbstractMachinePanel(App a) {
		this.app = a;
		File file = new File("nfa.log");
		textNFA = new JTextArea();
		scrollPane = new JScrollPane(textNFA);
		showNFAbutton = new JButton("Build NFA");
		labelNFA = new JLabel("NFA rappresentation");
		
		setLayout(new BorderLayout());
		scrollPane.setBorder(new EmptyBorder(8,8,8,8));
		
		//textNFA.setEditable(false);
		
		add(scrollPane, BorderLayout.CENTER);
		add(labelNFA, BorderLayout.NORTH);
		add(showNFAbutton, BorderLayout.SOUTH);
		
		showNFAbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showNFA(file);
			}
		});
		
	}
	
	public void showNFA(File fil) {
		try {
			FileReader reader = new FileReader(fil);
			BufferedReader buffer = new BufferedReader(reader);
			textNFA.read(buffer, null);
			buffer.close();
			
		} catch(Exception e1) {
			
		}
	}
}
