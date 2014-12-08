package it.unipr.informatica.regex.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ButtonPanel extends JPanel {
	private JButton searchButton;
	private JButton cancelButton;
	private App app;
	
	public ButtonPanel(App a) {
		this.app = a;
		// init buttons
		searchButton = new JButton("Search");
		cancelButton = new JButton("Cancel");
		
		// add listeners
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.search();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.cancelTextArea();;
			}
		});
		
		// set layout
		setLayout(new FlowLayout());
		add(searchButton);
		add(cancelButton);
	}
}
