package app;/*
 * 
 * This is a dialog for searching Employees by their surname.
 * 
 * */

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class SearchBySurnameDialog extends JDialog implements ActionListener{
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	
	// constructor for search by surname dialog
	public SearchBySurnameDialog(EmployeeDetails parent) {
		extractedSearchBySurname(parent);
	}

	// initialize search container
	public Container searchPane() {
		return extractedSearchPane();
	}

	// action listener for save and cancel button
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == search){
			this.parent.searchBySurnameField.setText(searchField.getText());
		
			// search Employee by surname
			this.parent.searchEmployeeBySurname();
			dispose();
		}
		
		// else dispose dialog
		else if(e.getSource() == cancel)
			dispose();
	}
	
	//***********EXTRACTED METHODS*************************
	
	private void extractedSearchBySurname(EmployeeDetails parent) {
		setTitle("Search by Surname");
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane());
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}
	
	private Container extractedSearchPane() {
		JPanel searchPanel = new JPanel(new GridLayout(3,1));
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JLabel searchLabel;

		searchPanel.add(new JLabel("Search by Surname"));
	
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(searchLabel = new JLabel("Enter Surname:"));
		searchLabel.setFont(this.parent.font1);
		textPanel.add(searchField = new JTextField(20));
		searchField.setFont(this.parent.font1);
		searchField.setDocument(new JTextFieldLimit(20));

		buttonPanel.add(search = new JButton("Search"));
		search.addActionListener(this);
		search.requestFocus();
		
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);
		
		searchPanel.add(textPanel);
		searchPanel.add(buttonPanel);

		return searchPanel;
	}
	//*********END OF EXTRACTED METHODS*******************
}// end class SearchBySurnameDialog
