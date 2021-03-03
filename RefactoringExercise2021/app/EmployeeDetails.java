package app;
/* * 
 * This is a menu driven system that will allow users to define a data
 * structure representing a collection of records that can be displayed
 * both by means of a dialog that can be scrolled through and by means 
 * of a table to give an overall view of the collection contents.
 * */

//EXTRACTED METHODS ARE AT THE END

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class EmployeeDetails extends JFrame 
implements ActionListener, ItemListener, DocumentListener, WindowListener {
	
	//---------------DECLARATIONS----------------------------------------------------------

	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	
	// hold object start position in file
	private long currentByteStart = 0;
	
	//holds the object for random file
	private RandomFile application = new RandomFile();
	
	// display files in File Chooser only with extension .dat
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	
	// hold file name and path for current file in use
	private File file;
	
	// holds true or false if any changes are made for text fields
	private boolean change = false;
	
	// holds true or false if any changes are made for file content
	boolean changesMade = false;
	
	//declaring all the menu items
	private JMenuItem open, save, saveAs, create,
					  modify, delete, firstItem, 
					  lastItem, nextItem, prevItem,
					  searchById,searchBySurname,
					  listAll, closeApp;
	
	//declaring all the buttons
	private JButton first, previous, next,
					last, add, edit, deleteButton,
					displayAll, searchId, searchSurname,
					saveChange, cancelChange;
	
	//JComboBox declaration
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	
	//Text field declaration
	private JTextField idField, ppsField, 
					   surnameField, firstNameField,
					   salaryField;
	
	
	private static EmployeeDetails frame = new EmployeeDetails();
	
	// font for labels, text fields and combo boxes
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	
	// holds automatically generated file name
	String generatedFileName;
	
	// holds current Employee object
	Employee currentEmployee;
	
	//Text fields for SEARCHING 
	JTextField searchByIdField, searchBySurnameField;
	
	// gender combo box values
	String[] gender = { "", "M", "F" };
	
	// department combo box values
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	
	// full time combo box values
	String[] fullTime = { "", "Yes", "No" };
	
	//End of Declarations--------------------------------------------------------------------
	
	//initialize menu bar
	private JMenuBar menuBar() {
		return extractedMenuBar();
	}

	// initialize search panel
	private JPanel searchPanel() {
		return extractedSearchPanel();
	}
		
	// initialize navigation panel
	private JPanel navigPanel() {
		return extractedNavigPanel();
	}
	
	//Button panel start
	private JPanel buttonPanel() {
		return extractedButtonPanel();
	}
	
		// initialize main/details panel
	@SuppressWarnings({ "unchecked" })
	private JPanel detailsPanel() {
		
		return extractedDetailsPanel();
	}
		
	// display current Employee details
	public void displayRecords(Employee thisEmployee) {
		
		// if Employee is null or ID is 0 do nothing else display Employee details
		boolean isNullOrZero = (thisEmployee == null) || (thisEmployee.getEmployeeId() == 0);
		
		if (isNullOrZero) {
		return;	
		}
		
		else {	
			extractedDisplayRecords(thisEmployee);
		}
		change = false;
	}
	


	//storing value once to avoid calling method again and again 
	boolean toDisplay = isSomeoneToDisplay(); 
	
	// display Employee summary dialog
	private void displayEmployeeSummaryDialog() {
		if (toDisplay)
			new EmployeeSummaryDialog(getAllEmloyees());
	}

	// display search by ID dialog
	private void displaySearchByIdDialog() {
		if (toDisplay)
			new SearchByIdDialog(EmployeeDetails.this);
	}

	// display search by surname dialog
	private void displaySearchBySurnameDialog() {
		if (toDisplay)
			new SearchBySurnameDialog(EmployeeDetails.this);
	}

	// find byte start in file for first active record
	private void firstRecord() {
		if (toDisplay) {
			extractedFirstRecord();
		} 
	}
	
	
	private void previousRecord() {
		// if any active record in file look for first record
		if (toDisplay) {
			extractedPreviousRecord();
		}
	}
		
	private void nextRecord() {
		// if any active record in file look for first record
		if (toDisplay) {
			extractedNextRecord();
		}
	}
		
	private void lastRecord() {

		if (toDisplay) {
			extractedLastRecord();
		}
	}
	
	public void searchEmployeeById() {
		boolean found = false;

		try {
			// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) 
				extractedSearchEmployeeById(found);
		}
		
		catch (NumberFormatException e) {
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}
	
	
	// search Employee by surname
	public void searchEmployeeBySurname() {
		

		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) 
			extractedSearchEmployeeBySurname(found);

		searchBySurnameField.setText("");
	}
		
	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else 
		//look for last active record
		
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		
		else {
			lastRecord();// look for last active record
			// add 1 to last active records ID to get next ID
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	// get values from text fields and create Employee object
	private Employee getChangedDetails() {
		boolean fullTime = ((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes");
		Employee theEmployee;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		application.openWriteFile(file.getAbsolutePath());
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
									// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			
			boolean ConfirmDelete = returnVal == JOptionPane.YES_OPTION;
			
			if (ConfirmDelete) {
			
				extractedConfirmDelete();
			}
		} 
	}
	
	// create vector of vectors with all Employee details
	@SuppressWarnings({ "deprecation" })
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		
		do {
			empDetails = extractedSettingEmployee();

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}
	
	// activate field for editing
	private void editDetails() {
	
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);// enable text fields for editing
		} 
	}

	// ignore changes and set text field unenabled
	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	// check if any of records in file is active - ID is not 0
	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		
		// open file for reading
		application.openReadFile(file.getAbsolutePath());
		
		// check if any of records in file is active - ID is not 0
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();// close file for reading
		
		// if no records found clear all text fields and display message
		if (!someoneToDisplay) {
			extractedSetNull();
		}
		return someoneToDisplay;
	}
	
	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		// check for correct PPS format based on assignment description
		boolean correctlength = pps.length() == 8 || pps.length() == 9;
		
		if (correctlength) {
			if (extractedDigitChecks(pps)) {
				
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();// close file for reading
			} 
			else
				ppsExist = true;
		} 
		else
			ppsExist = true;

		return ppsExist;
	}
	
	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		//boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (isDat(fileName, length))
			return true;
		
		else
		return false;
	}
	
	// check if any changes text field where made
	private boolean checkForChanges() {
		boolean anyChanges = false;

		if (change) {
			saveChanges();// save changes
			anyChanges = true;
		}
		
		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		} 

		return anyChanges;
	}

	// check for input in text fields
	private boolean checkInput() {
		boolean valid=true;
		// if any of inputs are in wrong format, colour text field and display
		// message
		 valid = extractedCheckValid(valid);
		
		try {
			Double.parseDouble(salaryField.getText());
			// check if salary is greater than 0
			boolean greaterThanZero = Double.parseDouble(salaryField.getText()) < 0;
			
			if (greaterThanZero) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		} 
		
		catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			}
		} 
		
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
			// display message if any input or format is wrong
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		// set text field to white colour if text fields are editable
		if (ppsField.isEditable())
			setToWhite();

		return valid;
	}
	
		// set text field background color to white
	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}
	
	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");

		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save old file
		boolean offerToSave = file.length() != 0 || change;
		
		if (offerToSave) {
			extractedSaveFile();
		} 
		
		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		
		boolean open = returnVal == JFileChooser.APPROVE_OPTION;
		
		if (open) {
			extractedOpenFile(fc);
		}
	}
	
	

	// save file
	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			extractedOfferSaveChanges();
		} 
	}
	
	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			application.openWriteFile(file.getAbsolutePath());
			// get changes for current Employee
			currentEmployee = getChangedDetails();
			// write changes to file for corresponding Employee record
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();// close file for writing
			changesMade = false;// state that all changes has bee saved
		}
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");

		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));
		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			extractedSaveOldFile(fc);
		}
		changesMade = false;
	}
	
	// allow to save changes to file when exiting the application
	private void exitApp() {
		// if file is not empty allow to save changes
		if (file.length() != 0) {
			extractedAllowSaveChanges();
		} else {
			// delete generated file if user chooses not to save file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			
			System.exit(0);// exit application
		} 
	}
	
	
	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
	}

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {

		extractedActionListener(e);
	}
	
	

	// content pane for main dialog
	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());// add menu bar to frame
		
		// add search panel to frame
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		
		// add navigation panel to frame
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		
		// add button panel to frame
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
		
		// add details panel to frame
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}

	// main method
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}// end main

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
	
	//**********EXTRACTED METHODS*****************************
	
	public JMenuBar extractedMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}

	public JPanel extractedSearchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200, growx, pushx");
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(searchId = new JButton(new ImageIcon(
				new ImageIcon("imgres.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchId.addActionListener(this);
		searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200, growx, pushx");
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchSurname = new JButton(new ImageIcon(new ImageIcon("imgres.png").getImage()
						.getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText("Search Employee By Surname");

		return searchPanel;
	}
	
	public JPanel extractedNavigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("previous.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(this);
		previous.setToolTipText("Display next Record");

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(this);
		next.setToolTipText("Display previous Record");

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(this);
		last.setToolTipText("Display last Record");

		return navigPanel;
	}

	public JPanel extractedButtonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}
	
	public JPanel extractedDetailsPanel() {
		//initializing instances
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(idField = new JTextField(20), "growx, pushx, wrap");
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(ppsField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(surnameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(firstNameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(genderCombo = new JComboBox<String>(gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<String>(department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), "growx, pushx, wrap");

		buttonPanel.add(saveChange = new JButton("Save"));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		// loop through panel components and add listeners and format
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			extractedEmpComponents(empDetails, i);
		} // end for
		return empDetails;
	}
	public void extractedEmpComponents(JPanel empDetails, int i) {
		JTextField field;
		empDetails.getComponent(i).setFont(font1);
		
		if (empDetails.getComponent(i) instanceof JTextField) {
			field = (JTextField) empDetails.getComponent(i);
			field.setEditable(false);
			
			if (field == ppsField) {
				field.setDocument(new JTextFieldLimit(9));
				}
			else {
				field.setDocument(new JTextFieldLimit(20));
			field.getDocument().addDocumentListener(this);
			}
		}
		
		else if (empDetails.getComponent(i) instanceof JComboBox) {
			empDetails.getComponent(i).setBackground(Color.WHITE);
			empDetails.getComponent(i).setEnabled(false);
			((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
			((JComboBox<String>) empDetails.getComponent(i)).setRenderer
			(new DefaultListCellRenderer() {
				
				public void paint(Graphics g) { // set foreground to combo boxes
					setForeground(new Color(65, 65, 65));
					super.paint(g);
				}
			});
		} 
	}

	
	
	public void extractedFirstRecord() {
		application.openReadFile(file.getAbsolutePath());
		// get byte start in file for first record
		currentByteStart = application.getFirst();
		// assign current Employee to first record in file
		currentEmployee = application.readRecords(currentByteStart);
		application.closeReadFile();// close file for reading
		// if first record is inactive look for next record
		if (currentEmployee.getEmployeeId() == 0)
			nextRecord();// look for next record
	}
	
	public void extractedDisplayRecords(Employee thisEmployee) {
		
		//initializing in else as they are not used in 'if' above
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");
		
		while (!found && countGender < gender.length - 1) {
			if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
				found = true;
			else
				countGender++;
		}
		
		found = false;
		
		// find corresponding department combo box value to current employee
		while (!found && countDep < department.length - 1) {
			if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
				found = true;
			else
				countDep++;
		} 
		
		idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
		ppsField.setText(thisEmployee.getPps().trim());
		surnameField.setText(thisEmployee.getSurName().trim());
		firstNameField.setText(thisEmployee.getFirstName());
		genderCombo.setSelectedIndex(countGender);
		departmentCombo.setSelectedIndex(countDep);
		salaryField.setText(format.format(thisEmployee.getSalary()));
		
		// set corresponding full time combo box value to current employee
		if (thisEmployee.getFullTime() == true)
			fullTimeCombo.setSelectedIndex(1);
		else
			fullTimeCombo.setSelectedIndex(2);
}

	public void extractedPreviousRecord() {
		application.openReadFile(file.getAbsolutePath());
		
		// get byte start in file for previous record
		currentByteStart = application.getPrevious(currentByteStart);
		
		// assign current Employee to previous record in file
		currentEmployee = application.readRecords(currentByteStart);
		
		// loop to previous record until Employee is active - ID is not 0
		while (currentEmployee.getEmployeeId() == 0) {
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);
			// assign current Employee to previous record in file
			currentEmployee = application.readRecords(currentByteStart);
		} 
		application.closeReadFile();
	}
	
public void extractedNextRecord() {
		
		application.openReadFile(file.getAbsolutePath());
	
		currentByteStart = application.getNext(currentByteStart);
	
		currentEmployee = application.readRecords(currentByteStart);
	
		while (currentEmployee.getEmployeeId() == 0) {
		
			currentByteStart = application.getNext(currentByteStart);
			
			currentEmployee = application.readRecords(currentByteStart);
		} 
		application.closeReadFile();
	}


public void extractedLastRecord() {
	application.openReadFile(file.getAbsolutePath());

	// get byte start in file for last record
	currentByteStart = application.getLast();
	
	// assign current Employee to first record in file
	currentEmployee = application.readRecords(currentByteStart);
	application.closeReadFile();// close file for reading
	
	// if last record is inactive look for previous record
	if (currentEmployee.getEmployeeId() == 0)
		previousRecord();
}

public void extractedSearchEmployeeById(boolean found) {
	firstRecord();// look for first record
	int firstId = currentEmployee.getEmployeeId();
	
	// if ID to search is already displayed do nothing else loop
	if (searchByIdField.getText().trim().equals(idField.getText().trim()))
		found = true;
	
	else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
		found = true;
		displayRecords(currentEmployee);
	}
	
	else {
		found = extractedCheckAll(found, firstId);
	} 

	if (!found)
		JOptionPane.showMessageDialog(null, "Employee not found!");
}

public boolean extractedCheckAll(boolean found, int firstId) {
	
		nextRecord();// look for next record
		// loop until Employee found or until all Employees haven been checked
		while (firstId != currentEmployee.getEmployeeId()) {
			
			// if found break from loop and display Employee details
			// else look for next record
			if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
				found = true;
				displayRecords(currentEmployee);
				break;
			}
			else
				nextRecord();
		} 
	return found;
}

public void extractedSearchEmployeeBySurname(boolean found) {
	firstRecord();// look for first record
	String firstSurname = currentEmployee.getSurName().trim();
	// if ID to search is already displayed do nothing else loop through
	// records
	boolean SearchSurname = searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim());
	boolean MatchesCurrentEmployee = searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurName().trim());

	if (SearchSurname)
		found = true;
	else {
		if (MatchesCurrentEmployee) {
			found = true;
			displayRecords(currentEmployee);
		} 
		else {
			found = extractedLoopAll(found, firstSurname, MatchesCurrentEmployee); 
		}
	}
		// if Employee not found display message
	if (!found)
		JOptionPane.showMessageDialog(null, "Employee not found!");
}

public boolean extractedLoopAll(boolean found, String firstSurname, boolean MatchesCurrentEmployee) {
	nextRecord();
	// loop until Employee found or until all Employees have been checked
	while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurName().trim())) {
		// if found break from loop and display Employee details
		// else look for next record
		if (MatchesCurrentEmployee) {
			found = true;
			displayRecords(currentEmployee);
			break;
		} 
		else
			nextRecord();// look for next record
	}
	return found;
}

public void extractedConfirmDelete() {
	application.openWriteFile(file.getAbsolutePath());

	application.deleteRecords(currentByteStart);
	application.closeWriteFile();// close file for writing

	if (isSomeoneToDisplay()) {
		nextRecord();// look for next record
		displayRecords(currentEmployee);
	}
}

public Vector<Object> extractedSettingEmployee() {
	Vector<Object> empDetails;
	empDetails = new Vector<Object>();
	empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
	empDetails.addElement(currentEmployee.getPps());
	empDetails.addElement(currentEmployee.getSurName());
	empDetails.addElement(currentEmployee.getFirstName());
	empDetails.addElement(new Character(currentEmployee.getGender()));
	empDetails.addElement(currentEmployee.getDepartment());
	empDetails.addElement(new Double(currentEmployee.getSalary()));
	empDetails.addElement(new Boolean(currentEmployee.getFullTime()));
	return empDetails;
}

public void extractedSetNull() {
	currentEmployee = null;
	idField.setText("");
	ppsField.setText("");
	surnameField.setText("");
	firstNameField.setText("");
	salaryField.setText("");
	genderCombo.setSelectedIndex(0);
	departmentCombo.setSelectedIndex(0);
	fullTimeCombo.setSelectedIndex(0);
	JOptionPane.showMessageDialog(null, "No Employees registered!");
}

public boolean extractedDigitChecks(String pps) {
	return Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
			&& Character.isDigit(pps.charAt(2))	&& Character.isDigit(pps.charAt(3)) 
			&& Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5)) 
			&& Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
			&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)));
}

private boolean isDat(File fileName, int length) {
	return fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
			&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't';
}

public boolean extractedCheckValid(boolean valid) {
	
	if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
		ppsField.setBackground(new Color(255, 150, 150));
		valid = false;
	}
	
	if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
		ppsField.setBackground(new Color(255, 150, 150));
		valid = false;
	} 
	if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
		surnameField.setBackground(new Color(255, 150, 150));
		valid = false;
	} 
	if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
		firstNameField.setBackground(new Color(255, 150, 150));
		valid = false;
	}
	if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
		genderCombo.setBackground(new Color(255, 150, 150));
		valid = false;
	}
	if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
		departmentCombo.setBackground(new Color(255, 150, 150));
		valid = false;
	}
	return valid;
}

private void extractedOpenFile(final JFileChooser fc) {
	File newFile;
	newFile = fc.getSelectedFile();
	// if old file wasn't saved and its name is generated file name,
	// delete this file
	if (file.getName().equals(generatedFileName))
		file.delete();// delete file
	file = newFile;// assign opened file to file
	// open file for reading
	application.openReadFile(file.getAbsolutePath());
	firstRecord();// look for first record
	displayRecords(currentEmployee);
	application.closeReadFile();// close file for reading
}

public void extractedSaveFile() {
	int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
			JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
	// if user wants to save file, save it
	if (returnVal == JOptionPane.YES_OPTION) {
		saveFile();
	}
}

public void extractedOfferSaveChanges() {
	// if changes has been made to text field offer user to save these changes
	if (change) {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// save changes if user choose this option
		if (returnVal == JOptionPane.YES_OPTION) {
			// save changes if ID field is not empty
			if (!idField.getText().equals("")) {
				application.openWriteFile(file.getAbsolutePath());
				currentEmployee = getChangedDetails();			
				application.changeRecords(currentEmployee, currentByteStart);
				application.closeWriteFile();// close file for writing
			}
		} 
	} 
	displayRecords(currentEmployee);
	setEnabled(false);
}

private void extractedSaveOldFile(final JFileChooser fc) {
	File newFile;
	newFile = fc.getSelectedFile();
	// check for file name
	if (!checkFileName(newFile)) {
		// add .dat extension if it was not there
		newFile = new File(newFile.getAbsolutePath() + ".dat");
		// create new file
		application.createFile(newFile.getAbsolutePath());
	} // end id
	else
		// create new file
		application.createFile(newFile.getAbsolutePath());

	try {// try to copy old file to new file
		Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		// if old file name was generated file name, delete it
		if (file.getName().equals(generatedFileName))
			file.delete();// delete file
		file = newFile;// assign new file to file
	} // end try
	catch (IOException e) {
	} // end catch
}

private void extractedAllowSaveChanges() {
	if (changesMade) {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user chooses to save file, save file
		if (returnVal == JOptionPane.YES_OPTION) {
			saveFile();// save file
			// delete generated file if user saved details to other file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			System.exit(0);// exit application
		} // end if
			// else exit application
		else if (returnVal == JOptionPane.NO_OPTION) {
			// delete generated file if user chooses not to save file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			System.exit(0);// exit application
		} // end else if
	} // end if
	else {
		// delete generated file if user chooses not to save file
		if (file.getName().equals(generatedFileName))
			file.delete();// delete file
		System.exit(0);// exit application
	} // end else
		// else exit application
}

public void extractedActionListener(ActionEvent e) {
	boolean commonCheck = checkInput() && !checkForChanges();
	Object source = e.getSource();
	
	if (source == closeApp) {
		if (commonCheck)
			exitApp();
	} 
	
	else if (source == open) {
		if (commonCheck)
			openFile();
	}
	
	else if (source == save) {
		if (commonCheck)
			saveFile();
		change = false;
	}
	else if (source == saveAs) {
		if (commonCheck)
			saveFileAs();
		change = false;
	}
	else if (source == searchById) {
		if (commonCheck)
			displaySearchByIdDialog();
	}
	else if (source == searchBySurname) {
		if (commonCheck)
			displaySearchBySurnameDialog();
	}
	else if (source == searchId || source == searchByIdField)
		searchEmployeeById();
	
	else if (source == searchSurname || source == searchBySurnameField)
		searchEmployeeBySurname();
	
	else if (source == saveChange) {
		if (commonCheck) {
			//saveChanges();
		}
	}
	
	else if (source == cancelChange)
		cancelChange();
	
	else if (source == firstItem || source == first) {
		if (commonCheck) {
			firstRecord();
			displayRecords(currentEmployee);
		}
	} 
	else if (source == prevItem || source == previous) {
		if (commonCheck) {
			previousRecord();
			displayRecords(currentEmployee);
		}
	}
	else if (source == nextItem || source == next) {
		if (commonCheck) {
			nextRecord();
			displayRecords(currentEmployee);
		}
	}
	else if (source == lastItem || source == last) {
		if (commonCheck) {
			lastRecord();
			displayRecords(currentEmployee);
		}
	} else if (source == listAll || source == displayAll) {
		if (commonCheck)
			if (isSomeoneToDisplay())
				displayEmployeeSummaryDialog();
	} else if (source == create || source == add) {
		if (commonCheck)
			new AddRecordDialog(EmployeeDetails.this);
	} else if (source == modify || source == edit) {
		if (commonCheck)
			editDetails();
	} else if (source == delete || source == deleteButton) {
		if (commonCheck)
			deleteRecord();
	} else if (source == searchBySurname) {
		if (commonCheck)
			new SearchBySurnameDialog(EmployeeDetails.this);
	}
}

//*****************End of Extracts**************************************************

	
}// end class EmployeeDetails
