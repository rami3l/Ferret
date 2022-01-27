
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.acl.LastOwnerException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;

import javax.swing.*; 
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI extends JFrame{

	private String fileNameAndPath;
	JLabel fileLocation = new JLabel("File location: None Selected");
	String[] asnCode = {"EAS","CDX","CHB","CHS","JPT","KHV","CHD"};
	String[] eurCode = {"EUR","CEU","GBR","FIN","IBS","TSI"};
	String[] afrCode = {"AFR","ACB","ASW","ESN","GWD","LWK","MSL","YRI"};
	String[] amrCode = {"AMR","CLM","MXL","PEL","PUR"};
	String[] sanCode = {"SAS","BEB","GIH","ITU","PJL","STU"};
	String[] allracesString = {"ALL"};
	static JFrame SNPFerret = new JFrame("Ferret v2.1.1");
	JLabel afrLabel = new JLabel("Africans");
	JLabel eurLabel = new JLabel("Europeans");
	JLabel asnLabel = new JLabel("East Asians");
	JLabel amrLabel = new JLabel("Americans");
	JLabel sanLabel = new JLabel("South Asians");
	JLabel allracesLabel = new JLabel("All Populations");

	JLabel chrLabel = new JLabel(": Chromosome:");
	JLabel selectChrRegionLabel = new JLabel("Input Locus ");
	JLabel startLabel = new JLabel("Start:");
	JLabel endLabel = new JLabel("End:");
	String[] chrOptions = {" ", "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22"};
	private JComboBox<String> chrList = new JComboBox<String>(chrOptions);
	private JTextField startPosTextField = new JTextField(8);
	private JTextField endPosTextField = new JTextField(8);

	JCheckBox[] afrsub = new JCheckBox[8];
	JCheckBox[] eursub = new JCheckBox[6];
	JCheckBox[] amrsub = new JCheckBox[5];
	JCheckBox[] sansub = new JCheckBox[6];
	JCheckBox[] asnsub = new JCheckBox[7];
	JCheckBox[] allracessub = new JCheckBox[1];

	private JTextField snpTextField = new JTextField(8);
	private JTextField snpWindowField = new JTextField(8);
	private JTextField geneNameField = new JTextField(8);
	JCheckBox snpWindowCheckBox = new JCheckBox("Include surrounding variant(s) in a window of ");
	JLabel snpWindowBP = new JLabel("bp");
	JLabel geneNameLabel = new JLabel("Input gene(s):");
	JLabel snpTextLabel = new JLabel("Input variant ID(s):");
	JPanel snpWindowPanel = new JPanel();
	JPanel snpSelectPanel = new JPanel();
	JPanel snpInputPanel = new JPanel();
	JPanel snpOptionsPanel = new JPanel();
	JPanel snpESPOptionPanel = new JPanel();
	JLabel snpOR = new JLabel("OR");
	JButton snpFileBrowseButton = new JButton("Browse");
	JButton snpFileClearButton = new JButton("Clear");
	JLabel snpFileLocation = new JLabel("No file selected");

	JCheckBox snpESPCheckBox = new JCheckBox("Output frequencies from the Exome Sequencing Project");
	JCheckBox geneESPCheckBox = new JCheckBox("Output frequencies from the Exome Sequencing Project");
	JCheckBox chrESPCheckBox = new JCheckBox("Output frequencies from the Exome Sequencing Project");

	String snpFileNameAndPath;

	JPanel chromosomeSelectPanel = new JPanel();
	JPanel chromosomeInputPanel = new JPanel();
	JPanel chromosomeESPOptionPanel = new JPanel();
	JPanel geneSelectPanel = new JPanel();
	JPanel geneInputPanel = new JPanel();
	JPanel geneSelectOptionsPanel = new JPanel();
	JPanel geneESPOptionPanel = new JPanel();
	JTabbedPane inputSelect = new JTabbedPane();

	JLabel geneOR = new JLabel("OR");

	String geneFileNameAndPath;
	JLabel geneFileLocation = new JLabel("No file selected");
	JLabel geneInputType = new JLabel("Input gene as: ");
	JButton geneFileBrowseButton = new JButton("Browse");
	JButton geneFileClearButton = new JButton("Clear");

	JRadioButton geneNameRadioButton = new JRadioButton(new String("Name"));
	JRadioButton geneIDRadioButton = new JRadioButton(new String("ID"));
	ButtonGroup geneInputButtonGroup = new ButtonGroup();

	JRadioButton geneNCBIRadioButton = new JRadioButton(new String("NCBI"));
	JRadioButton geneV37RadioButton = new JRadioButton(new String("v37"));
	ButtonGroup geneSourceButtonGroup = new ButtonGroup();

	JRadioButton snpNCBIRadioButton = new JRadioButton(new String("NCBI"));
	JRadioButton snpV37RadioButton = new JRadioButton(new String("v37"));
	ButtonGroup snpSourceButtonGroup = new ButtonGroup();

	JPanel bigPanel = new JPanel();
	JPanel kgPopulationPanel = new JPanel();
	JPanel goPanel = new JPanel();
	JPanel afrPanel = new JPanel();
	JPanel eurPanel = new JPanel();
	JPanel asnPanel = new JPanel();
	JPanel amrPanel = new JPanel();
	JPanel sanPanel = new JPanel();
	JPanel allracesPanel = new JPanel();
	JButton goButton = new JButton("Run Ferret, Run!");
	JButton browseButton = new JButton("Browse");
	JFileChooser openFileChooser = new JFileChooser();
	JScrollPane scrollBigPanel = new JScrollPane(bigPanel);

	static JFrame progressWindow = new JFrame("Working...");
	static JProgressBar progressBar = new JProgressBar(0,100);
	JLabel progressText = new JLabel("Initializing...");
	static Integer variantCounterResult;

	// update window
	JFrame updateFrame = new JFrame("Update");
	JPanel updatePanel = new JPanel();
	JPanel updateBarHolder = new JPanel();
	JPanel updateButtonHolder = new JPanel();
	JProgressBar updateProgressBar = new JProgressBar();
	JLabel updateLabel = new JLabel("Checking for update...");
	JLabel updateDetailLabel = new JLabel("");
	JButton updateOK = new JButton("OK");

	final JFileChooser saveFileChooser = new JFileChooser();

	JPanel fileChoosePanel = new JPanel();

	JMenuBar menuBar = new JMenuBar();
	JMenu ferretMenu = new JMenu("Ferret");
	JMenu helpMenu = new JMenu("Help");
	JMenuItem settingsMenuItem = new JMenuItem("Settings");
	JMenuItem updateMenuItem = new JMenuItem("Check for updates");
	JMenuItem exitMenuItem = new JMenuItem("Quit");
	JMenuItem aboutMenuItem = new JMenuItem("About Ferret");
	JMenuItem faqMenuItem = new JMenuItem("FAQ");
	JMenuItem contactMenuItem = new JMenuItem("Contact");

	URL questionMarkURL = getClass().getResource("questionMark25.png");
	ImageIcon questionMark = new ImageIcon(questionMarkURL);
	JLabel questionMarkSNPInput = new JLabel(questionMark);
	JLabel questionMarkGeneInput = new JLabel(questionMark);
	JLabel questionMarkLocusInput = new JLabel(questionMark);
	JLabel questionMarkGeneFileInput = new JLabel(questionMark);
	JLabel questionMarkSNPFileInput = new JLabel(questionMark);
	JLabel questionMarkMAFThreshold = new JLabel(questionMark);
	JLabel questionMarkESPMAF = new JLabel(questionMark);

	//Settings pane:
	JFrame settingsFrame = new JFrame("Settings");
	JPanel settingsPanel = new JPanel();
	JTextField vcfURLText = new JTextField();
	JTextField fileNomenclatureText = new JTextField();
	JSlider mafSlider = new JSlider(0, 5000, 0);
	JRadioButton phase3Button = new JRadioButton("Phase 3 (2,504 individuals) [default]");
	JRadioButton phase1Button = new JRadioButton("Phase 1 (1,092 individuals)");
	ButtonGroup vcfRadioButtons = new ButtonGroup();
	JRadioButton allFilesButton = new JRadioButton("Allele Frequencies (.frq) + Plink/HaploView (.map/.ped/.info) [default]");
	JRadioButton freqFileButton = new JRadioButton("Allele Frequencies (.frq) only");
	JRadioButton vcfFileButton = new JRadioButton("VCF file only");
	ButtonGroup fileOutputButtons = new ButtonGroup();
	JRadioButton version19Button = new JRadioButton("hg19/GRCh37 [default]");
	JRadioButton version38Button = new JRadioButton("hg38/GRCh38 [only available for Phase 3 data]");
	ButtonGroup hgVersionButtons = new ButtonGroup();
	JLabel vcfVersionLabel = new JLabel("1000 Genomes Version");
	JLabel vcfURLLabel = new JLabel("OR specify the VCF URL");
	JLabel vcfNomenclatureLabel = new JLabel("AND file nomenclature for chr $");
	JLabel MAFOptionLabel = new JLabel("Minor Allele Frequency (MAF)");
	JLabel MAFThresholdLabel = new JLabel("MAF Threshold: ");
	JLabel hgVersionLabel = new JLabel("Human Genome Version");
	JLabel filesLabel = new JLabel("Output Files");
	JButton settingsOK = new JButton("OK");
	JButton settingsCancel = new JButton("Cancel");
	JPanel mafPanel = new JPanel();
	JPanel mafESPPanel = new JPanel();
	JPanel vcfVersionPanel = new JPanel();
	JPanel settingsButtonPanel = new JPanel();
	JCheckBox ESPMAF = new JCheckBox("Apply MAF threshold to the Exome Sequencing Project");

	//About window
	JFrame aboutFrame = new JFrame("About");
	JPanel aboutPanel = new JPanel();
	JLabel ferretVersionLabel = new JLabel("Ferret v2.1.1");
	JLabel ferretDateLabel = new JLabel("February 2016");
	JTextArea ferretCitation = new JTextArea("Citation: Limou, S., Taverner, A., Nelson, G., "
			+ "Winkler, C.A. Ferret: a user-friendly Java tool to extract data from the 1000 Genomes "
			+ "Project. Presented at the annual meeting of the American Society of Human Genetics "
			+ "(ASHG), 2015, Baltimore, MD, USA.", 4, 50);

	//Contact window
	JFrame contactFrame = new JFrame("Contact");
	JPanel contactPanel = new JPanel();
	JLabel contactPeopleLabel = new JLabel("Sophie Limou and Andrew M. Taverner");
	JTextArea contactEmailLabel = new JTextArea("ferret@nih.gov");

	public class LinkLabel extends JTextField implements MouseListener, FocusListener, ActionListener {
		private URI target;

		public Color standardColor = new Color(0,0,255);
		public Color hoverColor = new Color(255,0,0);
		public Color activeColor = new Color(128,0,128);
		public Color transparent = new Color(0,0,0,0);
		public Color backgroundColor;

		private Border activeBorder;
		private Border hoverBorder;
		private Border standardBorder;

		public LinkLabel(URI target, String text){
			super(text);
			this.target = target;
		}

		public void setBackgroundColor(Color bgColor){
			this.backgroundColor = bgColor;
		}

		public void init() {
			this.addMouseListener(this);
			this.addFocusListener(this);
			this.addActionListener(this);
			setToolTipText(target.toString());

			activeBorder = new MatteBorder(0,0,1,0,activeColor);
			hoverBorder = new MatteBorder(0,0,1,0,hoverColor);
			standardBorder = new MatteBorder(0,0,1,0,transparent);

			setEditable(false);
			setForeground(standardColor);
			setBorder(standardBorder);
			setBackground(backgroundColor);
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		public void browse() {
			setForeground(activeColor);
			setBorder(activeBorder);
			try {
				Desktop.getDesktop().browse(target);
			} catch(Exception e) {
				e.printStackTrace();
			}
			setForeground(standardColor);
			setBorder(standardBorder);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			browse();	
		}

		@Override
		public void focusGained(FocusEvent e) {
			setForeground(hoverColor);
			setBorder(hoverBorder);
		}

		@Override
		public void focusLost(FocusEvent e) {
			setForeground(standardColor);
			setBorder(standardBorder);
		}

		public void mouseClicked(MouseEvent e) {
			browse();
		}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {
			setForeground(hoverColor);
			setBorder(hoverBorder);
		}

		public void mouseExited(MouseEvent e) {
			setForeground(standardColor);
			setBorder(standardBorder);
		}

	}

	public static class GhostText implements FocusListener, DocumentListener, PropertyChangeListener {
		private final JTextField textfield;
		private boolean isEmpty;
		private Color ghostColor;
		private Color foregroundColor;
		private final String ghostText;

		protected GhostText(final JTextField textfield, String ghostText) {
			super();
			this.textfield = textfield;
			this.ghostText = ghostText;
			this.ghostColor = Color.LIGHT_GRAY;
			textfield.addFocusListener(this);
			registerListeners();
			updateState();
			if (!this.textfield.hasFocus()) {
				focusLost(null);
			}
		}

		public void delete() {
			unregisterListeners();
			textfield.removeFocusListener(this);
		}

		private void registerListeners() {
			textfield.getDocument().addDocumentListener(this);
			textfield.addPropertyChangeListener("foreground", this);
		}

		private void unregisterListeners() {
			textfield.getDocument().removeDocumentListener(this);
			textfield.removePropertyChangeListener("foreground", this);
		}

		public Color getGhostColor() {
			return ghostColor;
		}

		public void setGhostColor(Color ghostColor) {
			this.ghostColor = ghostColor;
		}

		private void updateState() {
			isEmpty = textfield.getText().length() == 0;
			foregroundColor = textfield.getForeground();
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (isEmpty) {
				unregisterListeners();
				try {
					textfield.setText("");
					textfield.setForeground(foregroundColor);
				} finally {
					registerListeners();
				}
			}

		}

		@Override
		public void focusLost(FocusEvent e) {
			if (isEmpty) {
				unregisterListeners();
				try {
					textfield.setText(ghostText);
					textfield.setForeground(ghostColor);
				} finally {
					registerListeners();
				}
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateState();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateState();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateState();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateState();
		}

	}

	public enum version1KG {
		ZERO, ONE, THREE
	}
	public enum fileOutput {
		ALL, FRQ, VCF
	}

	public GUI() {
		LinkLabel ferretWebLabelAbout = null;
		try{
			ferretWebLabelAbout = new LinkLabel(new URI("http://limousophie35.github.io/Ferret/"),"http://limousophie35.github.io/Ferret/");
		}catch (URISyntaxException e){
			e.printStackTrace();
		}

		NumberFormat mafFormat = NumberFormat.getNumberInstance();
		mafFormat.setMaximumFractionDigits(4);
		final JFormattedTextField mafText = new JFormattedTextField(mafFormat);

		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(20000);

		final version1KG[] currVersion = {version1KG.THREE};
		final fileOutput[] currFileOut = {fileOutput.ALL};
		final Boolean[] defaultHG = {true};
		final double[] mafThreshold = {0.0};
		final Boolean[] ESPMAFBoolean = {false};
		final Boolean[] checkedForUpdate = {false};

		ferretMenu.add(settingsMenuItem);
		ferretMenu.add(updateMenuItem);
		ferretMenu.add(exitMenuItem);
		helpMenu.add(aboutMenuItem);
		helpMenu.add(faqMenuItem);
		helpMenu.add(contactMenuItem);
		menuBar.add(ferretMenu);
		menuBar.add(helpMenu);

		// Update window stuff
		updateFrame.setResizable(true);
		updateFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		updateFrame.getContentPane().add(updatePanel);
		updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.Y_AXIS));
		updatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		updatePanel.add(updateLabel);
		//updatePanel.add(updateDetailLabel);
		updatePanel.add(Box.createRigidArea(new Dimension(500, 0)));
		updateLabel.setAlignmentX(CENTER_ALIGNMENT);
		updateProgressBar.setIndeterminate(true);
		//updateBarHolder.add(updateDetailLabel);
		updateDetailLabel.setAlignmentX(CENTER_ALIGNMENT);
		updateBarHolder.add(updateProgressBar);
		updatePanel.add(updateBarHolder);
		updatePanel.add(updateButtonHolder);
		updateButtonHolder.setLayout(new BoxLayout(updateButtonHolder, BoxLayout.X_AXIS));
		updateButtonHolder.add(updateOK);
		updateOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFrame.dispose();
			}
		});
		updateFrame.pack();

		// About window stuff
		aboutFrame.getContentPane().add(aboutPanel);
		aboutFrame.setResizable(true);
		aboutFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
		aboutPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		aboutPanel.add(ferretVersionLabel);
		aboutPanel.add(ferretDateLabel);
		aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));
		ferretWebLabelAbout.setBackgroundColor(aboutPanel.getBackground());
		ferretWebLabelAbout.init();
		ferretWebLabelAbout.setAlignmentX(LEFT_ALIGNMENT);
		ferretWebLabelAbout.setMaximumSize(ferretWebLabelAbout.getPreferredSize());
		aboutPanel.add(ferretWebLabelAbout);
		aboutPanel.add(Box.createRigidArea(new Dimension(0, 12)));
		ferretCitation.setAlignmentX(LEFT_ALIGNMENT);
		ferretCitation.setLineWrap(true);
		ferretCitation.setWrapStyleWord(true);
		ferretCitation.setBackground(aboutPanel.getBackground());
		ferretCitation.setMaximumSize(ferretCitation.getPreferredSize());
		aboutPanel.add(ferretCitation);
		aboutFrame.pack();

		// Contact window stuff
		contactFrame.getContentPane().add(contactPanel);
		contactFrame.setResizable(true);
		contactFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
		contactPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contactPeopleLabel.setAlignmentX(CENTER_ALIGNMENT);
		contactPanel.add(contactPeopleLabel);
		contactEmailLabel.setAlignmentX(CENTER_ALIGNMENT);
		contactEmailLabel.setBackground(contactPanel.getBackground());
		/*
		ferretCitation.setAlignmentX(LEFT_ALIGNMENT);
		ferretCitation.setLineWrap(true);
		ferretCitation.setWrapStyleWord(true);
		ferretCitation.setBackground(aboutPanel.getBackground());
		ferretCitation.setMaximumSize(ferretCitation.getPreferredSize());
		aboutPanel.add(ferretCitation);
		 */
		contactPanel.add(contactEmailLabel);
		contactFrame.pack();

		// Settings window stuff
		settingsFrame.getContentPane().add(settingsPanel);
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		settingsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		settingsFrame.setResizable(true);
		settingsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		vcfVersionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		settingsPanel.add(vcfVersionLabel);
		vcfRadioButtons.add(phase3Button);
		vcfRadioButtons.add(phase1Button);
		settingsPanel.add(phase3Button);
		settingsPanel.add(phase1Button);
		phase3Button.setSelected(true);
		phase3Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				version38Button.setEnabled(true);
			}
		});
		phase1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				version19Button.setSelected(true);
				version38Button.setEnabled(false);
			}
		});
		/*settingsPanel.add(vcfURLLabel);
		vcfURLText.setAlignmentX(LEFT_ALIGNMENT);
		fileNomenclatureText.setAlignmentX(LEFT_ALIGNMENT);
		GhostText vcfURLExample = new GhostText(vcfURLText, "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/");
		settingsPanel.add(vcfURLText);

		vcfURLText.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent arg0) {
				if (vcfURLText.getText().equals("http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/") && 
						fileNomenclatureText.getText().equals("ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz")){
					phase3Button.setEnabled(true);
					phase1Button.setEnabled(true);
					phase3Button.setSelected(true);
					version19Button.setEnabled(true);
					version38Button.setEnabled(true);
					version19Button.setSelected(true);
					vcfFileButton.setEnabled(true);
				}else{
					phase3Button.setEnabled(false);
					phase3Button.setSelected(true);
					phase1Button.setEnabled(false);
					version19Button.setSelected(true);
					version19Button.setEnabled(false);
					version38Button.setEnabled(false);
					if (vcfFileButton.isSelected()){
						freqFileButton.setSelected(true);
					}
					vcfFileButton.setEnabled(false);
				}
			}

			public void focusGained(FocusEvent arg0) {
			}
		});
		settingsPanel.add(vcfNomenclatureLabel);

		GhostText vcfNomenclatureExample = new GhostText(fileNomenclatureText, "ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz");
		settingsPanel.add(fileNomenclatureText);
		fileNomenclatureText.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent arg0) {
				if (vcfURLText.getText().equals("http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/") && 
						fileNomenclatureText.getText().equals("ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz")){
					phase3Button.setEnabled(true);
					phase1Button.setEnabled(true);
					phase3Button.setSelected(true);
					version19Button.setEnabled(true);
					version38Button.setEnabled(true);
					version19Button.setSelected(true);
					vcfFileButton.setEnabled(true);
				}else{
					phase3Button.setEnabled(false);
					phase3Button.setSelected(true);
					phase1Button.setEnabled(false);
					version19Button.setSelected(true);
					version19Button.setEnabled(false);
					version38Button.setEnabled(false);
					if (vcfFileButton.isSelected()){
						freqFileButton.setSelected(true);
					}
					vcfFileButton.setEnabled(false);
				}
			}

			public void focusGained(FocusEvent arg0) {
			}
		});
		 */
		settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mafPanel.setAlignmentX(LEFT_ALIGNMENT);
		settingsPanel.add(MAFOptionLabel);
		MAFOptionLabel.setFont(new Font("SansSerif",Font.BOLD,16));
		settingsPanel.add(mafPanel);
		mafPanel.setLayout(new BoxLayout(mafPanel, BoxLayout.X_AXIS));
		mafPanel.add(MAFThresholdLabel);
		mafText.setColumns(5);
		mafText.setMaximumSize(mafText.getPreferredSize());
		mafText.setValue(new Double(0));
		mafPanel.add(mafText);

		mafText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				double localMAFThreshold = ((Number)mafText.getValue()).doubleValue();
				if (localMAFThreshold > 0.5){
					localMAFThreshold = 0.5;
					mafText.setValue(localMAFThreshold);
				}else if(localMAFThreshold < 0.0){
					localMAFThreshold = 0.0;
					mafText.setValue(localMAFThreshold);
				}
				mafSlider.setValue((int)(localMAFThreshold*10000));
			}
		});

		mafSlider.setMajorTickSpacing(1000);
		mafSlider.setPaintTicks(true);
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("0.0"));
		labelTable.put(new Integer(5000),new JLabel("0.5"));
		mafSlider.setLabelTable(labelTable);
		mafSlider.setValue(0);
		mafSlider.setPaintLabels(true);
		mafPanel.add(mafSlider);
		mafSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double localMAFThreshold = mafSlider.getValue();
				mafText.setValue(localMAFThreshold/10000);
			}
		});
		mafPanel.add(questionMarkMAFThreshold);
		questionMarkMAFThreshold.setToolTipText("<html>The MAF threshold is applied to the selected 1000 Genomes populations<br>"
				+ "<u>Example:</u> For a MAF threshold of 0.05 (i.e. 5%), Ferret will only output variants with <br> a frequency >= 5% in the "
				+ "selected populations.</html>");
		mafPanel.add(Box.createHorizontalGlue());
		mafESPPanel.setLayout(new BoxLayout(mafESPPanel, BoxLayout.X_AXIS));
		mafESPPanel.setAlignmentX(LEFT_ALIGNMENT);
		mafESPPanel.add(ESPMAF);
		mafESPPanel.add(questionMarkESPMAF);
		settingsPanel.add(mafESPPanel);
		questionMarkESPMAF.setToolTipText("<html> If checked, the MAF threshold is also applied to the Exome Sequencing Project populations."
				+ "<br><u>Example:</u> For a MAF threshold of 0.05 (i.e. 5%), Ferret will only output variants with a frequency >= 5% <br> "
				+ "in either the selected 1000 Genomes populations, or the European American population from the <br>"
				+ "Exome Sequencing Project, or the African American population from the Exome Sequencing Project. </html>");
		settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		settingsPanel.add(filesLabel);
		filesLabel.setFont(new Font("SansSerif",Font.BOLD,16));
		fileOutputButtons.add(allFilesButton);
		fileOutputButtons.add(freqFileButton);
		fileOutputButtons.add(vcfFileButton);
		settingsPanel.add(allFilesButton);
		allFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!version38Button.isSelected()){
					ESPMAF.setEnabled(true);
				}				
			}
		});
		settingsPanel.add(freqFileButton);
		freqFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!version38Button.isSelected()){
					ESPMAF.setEnabled(true);
				}
			}
		});
		settingsPanel.add(vcfFileButton);
		vcfFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (ESPMAF.isSelected()){
					ESPMAF.setSelected(false);
				}
				ESPMAF.setEnabled(false);
			}
		});
		allFilesButton.setSelected(true);
		settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		settingsPanel.add(hgVersionLabel);
		hgVersionLabel.setFont(new Font("SansSerif",Font.BOLD,16));
		hgVersionButtons.add(version19Button);
		hgVersionButtons.add(version38Button);
		settingsPanel.add(version19Button);
		version19Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				phase1Button.setEnabled(true);
				if (!vcfFileButton.isSelected()){
					ESPMAF.setEnabled(true);
				}
			}
		});
		settingsPanel.add(version38Button);
		version38Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				phase3Button.setSelected(true);
				phase1Button.setEnabled(false);
				if (ESPMAF.isSelected()){
					ESPMAF.setSelected(false);
				}
				ESPMAF.setEnabled(false);
			}
		});
		version19Button.setSelected(true);
		settingsButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
		settingsButtonPanel.setLayout(new BoxLayout(settingsButtonPanel, BoxLayout.X_AXIS));
		settingsPanel.add(settingsButtonPanel);
		settingsButtonPanel.add(Box.createHorizontalGlue());
		settingsButtonPanel.add(settingsCancel);
		settingsCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				phase3Button.setSelected(currVersion[0] == version1KG.THREE);
				phase3Button.setEnabled(true);
				phase1Button.setEnabled(defaultHG[0]);
				phase1Button.setSelected(currVersion[0] == version1KG.ONE);
				mafText.setValue(new Double(mafThreshold[0]));
				mafSlider.setValue((int)(mafThreshold[0]*10000));
				if (currFileOut[0] == fileOutput.VCF || !defaultHG[0]){
					ESPMAF.setEnabled(false);
				} else {
					ESPMAF.setEnabled(true);
				}
				ESPMAF.setSelected(ESPMAFBoolean[0]);
				allFilesButton.setSelected(currFileOut[0] == fileOutput.ALL);
				freqFileButton.setSelected(currFileOut[0] == fileOutput.FRQ);
				vcfFileButton.setSelected(currFileOut[0] == fileOutput.VCF);
				vcfFileButton.setEnabled(true);
				version19Button.setEnabled(true);
				version19Button.setSelected(defaultHG[0]);
				version38Button.setSelected(!defaultHG[0]);
				version38Button.setEnabled(currVersion[0] == version1KG.THREE);
				settingsFrame.dispose();
			}
		});
		settingsButtonPanel.add(settingsOK);

		settingsOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (phase3Button.isSelected()){
					currVersion[0] = version1KG.THREE;
					setPhase3();
				}else if (phase1Button.isSelected()){
					currVersion[0] = version1KG.ONE;
					setPhase1();
				}else{
					currVersion[0] = version1KG.ZERO;
				}
				mafThreshold[0] = ((Number)mafText.getValue()).doubleValue();
				if (allFilesButton.isSelected()){
					currFileOut[0] = fileOutput.ALL;
				}else if(freqFileButton.isSelected()){
					currFileOut[0] = fileOutput.FRQ;
				}else if(vcfFileButton.isSelected()){
					currFileOut[0] = fileOutput.VCF;
				}
				defaultHG[0] = version19Button.isSelected();
				ESPMAFBoolean[0] = ESPMAF.isSelected();
				// Requesting either GRCh38 or VCF only prevents ESP button from working
				if ((version38Button.isSelected() || vcfFileButton.isSelected()) && (snpESPCheckBox.isSelected() || geneESPCheckBox.isSelected() || chrESPCheckBox.isSelected())){
					snpESPCheckBox.setSelected(false);
					geneESPCheckBox.setSelected(false);
					chrESPCheckBox.setSelected(false);
				}
				if (version38Button.isSelected()){
					questionMarkLocusInput.setToolTipText("<html>Input hg38 human genome version coordinates in bp. <br><u> Example for CCR5:</u> Chromosome: 3 Start: 46370142 End: 46376206</html>");
				}
				if (version19Button.isSelected()){
					questionMarkLocusInput.setToolTipText("<html>Input hg19 human genome version coordinates in bp. <br><u> Example for CCR5:</u> Chromosome: 3 Start: 46411633 End: 46417697</html>");
				}
				snpESPCheckBox.setEnabled(version19Button.isSelected() & !vcfFileButton.isSelected());
				geneESPCheckBox.setEnabled(version19Button.isSelected() & !vcfFileButton.isSelected());
				chrESPCheckBox.setEnabled(version19Button.isSelected() & !vcfFileButton.isSelected());
				settingsFrame.dispose();
			}
		});
		settingsFrame.pack();

		// Progress window stuff
		progressWindow.setResizable(true);
		progressWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JPanel progressPanel = new JPanel();
		progressWindow.getContentPane().add(progressPanel);
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		progressPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		progressPanel.add(Box.createVerticalGlue());
		progressPanel.add(progressText);
		progressText.setAlignmentX(Container.CENTER_ALIGNMENT);
		progressPanel.add(Box.createRigidArea(new Dimension(0,10)));
		progressPanel.add(progressBar);
		progressPanel.add(Box.createVerticalGlue());
		//progressWindow.add(progressPanel);
		progressWindow.pack();

		SNPFerret.setJMenuBar(menuBar);
		SNPFerret.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SNPFerret.setResizable(true); 
		//		SNPFerret.getContentPane().add(bigPanel);
		SNPFerret.getContentPane().add(scrollBigPanel);
		bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));

		// menubar action listeners
		settingsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.setLocationRelativeTo(SNPFerret);
				settingsFrame.setVisible(true);
			}
		});
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		updateMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateFrame.setLocationRelativeTo(SNPFerret);
				updateFrame.setVisible(true);

				if (!checkedForUpdate[0]){
					checkedForUpdate[0] = true;
					final checkForUpdate updateWorker = new checkForUpdate();
					updateWorker.addPropertyChangeListener(new PropertyChangeListener() {

						public void propertyChange(PropertyChangeEvent arg0) {
							if (arg0.getPropertyName().equals("state")){
								if ((StateValue) arg0.getNewValue() == StateValue.DONE){


									String updateReason = updateWorker.updateStatus();
									Boolean urgentUpdate = updateWorker.urgentUpdate();
									Boolean needUpdate = updateWorker.needUpdate();

									if (urgentUpdate || needUpdate){
										updateLabel.setText(updateReason);
										updateBarHolder.remove(updateProgressBar);
										LinkLabel ferretUpdate = null;
										try {
											ferretUpdate = new LinkLabel(new URI("http://limousophie35.github.io/Ferret/"),"http://limousophie35.github.io/Ferret/");
										} catch (URISyntaxException e) {
											e.printStackTrace();
										}
										JLabel updateFerretLabel = new JLabel("Please update Ferret at:");
										updateBarHolder.add(updateFerretLabel);
										updateBarHolder.repaint();
										updateFerretLabel.setText("");
										updateFerretLabel.setText("Please update Ferret at:");
										ferretUpdate.setBackgroundColor(updatePanel.getBackground());
										ferretUpdate.init();
										ferretUpdate.setAlignmentX(LEFT_ALIGNMENT);
										ferretUpdate.setMaximumSize(ferretUpdate.getPreferredSize());
										updateBarHolder.add(ferretUpdate);
									}else{
										updateLabel.setText("");
										updateBarHolder.remove(updateProgressBar);
										updateBarHolder.add(new JLabel(updateReason));
									}
								}
							}

						}

					});
					updateWorker.execute();
				}
			}
		});
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aboutFrame.setLocationRelativeTo(SNPFerret);
				aboutFrame.setVisible(true);
			}
		});
		faqMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://limousophie35.github.io/Ferret/#faq"));
				} catch(Exception e) {
					//e.printStackTrace();
				}
			}
		});
		contactMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contactFrame.setLocationRelativeTo(SNPFerret);
				contactFrame.setVisible(true);
			}
		});

		bigPanel.add(inputSelect);

		// Method select tabs
		inputSelect.addTab("Locus", chromosomeSelectPanel);
		inputSelect.addTab("Gene", geneSelectPanel);
		inputSelect.addTab("Variant", snpSelectPanel);

		// Source selection panel for SNP
		snpSourceButtonGroup.add(snpNCBIRadioButton);
		snpSourceButtonGroup.add(snpV37RadioButton);
		snpNCBIRadioButton.setSelected(true);

		// SNP input panel
		snpInputPanel.setLayout(new BoxLayout(snpInputPanel, BoxLayout.X_AXIS));
		snpInputPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		snpInputPanel.add(snpTextLabel);
		snpInputPanel.add(questionMarkSNPInput);
		questionMarkSNPInput.setToolTipText("<html>Input the rs number without the letters 'rs'<br><u>Example:</u> 73885319 for rs73885319<br><br>"
				+ "To input multiple variants at once, enter a list of variant IDs separated by a comma, or input a file.<br><u>Example:</u> 73885319, 2395029 for rs73885319 and rs2395029</html>");
		snpInputPanel.add(snpTextField);
		snpTextField.setMaximumSize(snpTextField.getPreferredSize());
		snpInputPanel.add(Box.createRigidArea(new Dimension(15,0)));
		snpInputPanel.add(snpOR);
		snpInputPanel.add(Box.createRigidArea(new Dimension(15,0)));
		snpInputPanel.add(snpFileBrowseButton);
		snpFileBrowseButton.setPreferredSize(new Dimension(100,30));
		snpFileBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = openFileChooser.showOpenDialog(GUI.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File snpFile = openFileChooser.getSelectedFile();
					snpFileNameAndPath = snpFile.getAbsolutePath();
					String snpDisplayFileName;
					if(snpFileNameAndPath.length() > 35){
						snpDisplayFileName = ".." + snpFileNameAndPath.substring(snpFileNameAndPath.lastIndexOf('/'));
					}else{
						snpDisplayFileName = snpFileNameAndPath;
					}
					snpFileLocation.setText("File Location: " + snpDisplayFileName);
					snpFileClearButton.setEnabled(true);
					snpTextField.setEnabled(false);
					snpTextField.setText("");
				}
			}
		});
		snpInputPanel.add(snpFileLocation);
		snpInputPanel.add(snpFileClearButton);
		snpInputPanel.add(questionMarkSNPFileInput);
		questionMarkSNPFileInput.setToolTipText("<html>You can load a file in any of the following formats: <br> - a comma-delimited .csv file (example: variant.csv containing 73885319, 2395029) <br>"
				+ " - a tab-delimited .tab or .tsv file (example: variant.tab containing 73885319 &nbsp&nbsp&nbsp&nbsp 2395029) <br>"
				+ " - a space-delimited .txt file (example: variant.txt containing 73885319 2395029)"
				+ "<br><br> A carriage return can also be used as a delimiter for all above file types.</html>");
		snpFileClearButton.setEnabled(false);
		snpFileClearButton.setPreferredSize(new Dimension(100,30));
		snpFileClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				snpFileNameAndPath = null;
				snpFileLocation.setText("No file selected");
				snpFileClearButton.setEnabled(false);
				snpTextField.setEnabled(true);
			}
		});
		//snpInputPanel.setMaximumSize(snpInputPanel.getPreferredSize());

		snpWindowPanel.setLayout(new BoxLayout(snpWindowPanel, BoxLayout.X_AXIS));
		snpWindowPanel.add(snpWindowCheckBox);
		snpWindowCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (snpWindowCheckBox.isSelected()){
					snpWindowField.setEnabled(true);
				} else {
					snpWindowField.setEnabled(false);
					snpWindowField.setText("");
				}
			}
		});
		snpWindowPanel.add(snpWindowField);
		snpWindowField.setEnabled(false);
		snpWindowPanel.add(snpWindowBP);
		snpWindowField.setMaximumSize(snpWindowField.getPreferredSize());
		snpWindowPanel.setMaximumSize(snpWindowPanel.getPreferredSize());

		snpESPOptionPanel.setLayout(new BoxLayout(snpESPOptionPanel, BoxLayout.X_AXIS));
		snpESPOptionPanel.add(snpESPCheckBox);
		snpESPOptionPanel.setMaximumSize(snpESPOptionPanel.getPreferredSize());

		/* Currently out of commission
		snpOptionsPanel.setLayout(new BoxLayout(snpOptionsPanel, BoxLayout.X_AXIS));
		snpOptionsPanel.add(snpNCBIRadioButton);
		snpOptionsPanel.add(snpV37RadioButton);
		snpOptionsPanel.setMaximumSize(snpOptionsPanel.getPreferredSize());
		 */

		// SNP selection method
		snpSelectPanel.setLayout(new BoxLayout(snpSelectPanel, BoxLayout.Y_AXIS));
		snpSelectPanel.add(snpInputPanel);
		snpSelectPanel.add(snpWindowPanel);
		//snpSelectPanel.add(snpOptionsPanel);
		snpSelectPanel.add(snpESPOptionPanel);

		// Gene selection method ----------------------------------------------------------
		// Create the button groups
		geneSourceButtonGroup.add(geneNCBIRadioButton);
		geneSourceButtonGroup.add(geneV37RadioButton);
		geneNCBIRadioButton.setSelected(true);
		geneInputButtonGroup.add(geneNameRadioButton);
		geneInputButtonGroup.add(geneIDRadioButton);

		// Create the gene input panel
		geneInputPanel.setLayout(new BoxLayout(geneInputPanel, BoxLayout.X_AXIS));
		geneInputPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		geneInputPanel.add(geneNameLabel);
		geneInputPanel.add(questionMarkGeneInput);
		questionMarkGeneInput.setToolTipText("<html>Input a gene name or a gene ID, and check the corresponding box (Name vs ID) <br>"
				+ "<u>Example:</u> CCR5 for gene name or 1234 for gene ID <br><br>"
				+ "To input multiple genes at once, enter a list of genes separated by a comma or input a file. <br>"
				+ "<u>Example:</u> CCR5, HCP5 for gene name input or 1234, 10866 for gene ID input.</html>");
		geneInputPanel.add(geneNameField);
		geneNameField.setMaximumSize(geneNameField.getPreferredSize());
		geneInputPanel.add(Box.createRigidArea(new Dimension(15,0)));
		geneInputPanel.add(geneOR);
		geneInputPanel.add(Box.createRigidArea(new Dimension(15,0)));
		geneInputPanel.add(geneFileBrowseButton);

		geneFileBrowseButton.setPreferredSize(new Dimension(100,30));
		geneFileBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = openFileChooser.showOpenDialog(GUI.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File geneFile = openFileChooser.getSelectedFile();
					geneFileNameAndPath = geneFile.getAbsolutePath();
					String geneDisplayFileName;
					if(geneFileNameAndPath.length() > 35){
						geneDisplayFileName = ".." + geneFileNameAndPath.substring(geneFileNameAndPath.lastIndexOf('/'));
					}else{
						geneDisplayFileName = geneFileNameAndPath;
					}
					geneFileLocation.setText("File Location: " + geneDisplayFileName);
					geneFileClearButton.setEnabled(true);
					geneNameField.setEnabled(false);
					geneNameField.setText("");
				}
			}		
		});

		geneInputPanel.add(geneFileLocation);
		geneFileClearButton.setPreferredSize(new Dimension(100,30));
		geneInputPanel.add(geneFileClearButton);
		geneInputPanel.add(questionMarkGeneFileInput);
		questionMarkGeneFileInput.setToolTipText("<html>You can load a file in any of the following formats for either gene names or gene IDs: <br> "
				+ " - a comma-delimited .csv file (example: gene.csv containing CCR5, HCP5) <br>"
				+ " - a tab-delimited .tab or .tsv file (example: gene.tab containing CCR5 &nbsp&nbsp&nbsp&nbsp HCP5) <br>"
				+ " - a space-delimited .txt file (example: gene.txt containing CCR5 HCP5)"
				+ "<br><br> A carriage return can also be used as a delimiter for all above file types.</html>");
		geneFileClearButton.setEnabled(false);
		geneFileClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				geneFileNameAndPath = null;
				geneFileLocation.setText("No file selected");
				geneFileClearButton.setEnabled(false);
				geneNameField.setEnabled(true);
			}
		});

		// Create the NCBI/Frozen look-up panel
		geneSelectOptionsPanel.setLayout(new BoxLayout(geneSelectOptionsPanel, BoxLayout.X_AXIS));
		geneSelectOptionsPanel.add(geneInputType);
		geneSelectOptionsPanel.add(geneNameRadioButton);
		geneSelectOptionsPanel.add(geneIDRadioButton);
		geneNameRadioButton.setSelected(true);

		geneESPOptionPanel.setLayout(new BoxLayout(geneESPOptionPanel,BoxLayout.X_AXIS));
		geneESPOptionPanel.add(geneESPCheckBox);

		/* Currently out of commission
		geneSelectOptionsPanel.add(Box.createRigidArea(new Dimension(30,0)));
		geneSelectOptionsPanel.add(geneNCBIRadioButton);
		geneSelectOptionsPanel.add(geneV37RadioButton);
		 */

		// Finally add the panels to the main panel
		geneSelectPanel.add(Box.createVerticalGlue());
		geneSelectPanel.setLayout(new BoxLayout(geneSelectPanel, BoxLayout.Y_AXIS));
		geneSelectPanel.add(geneInputPanel);
		geneSelectPanel.add(geneSelectOptionsPanel);
		geneSelectPanel.add(geneESPOptionPanel);
		geneSelectPanel.add(Box.createVerticalGlue());
		// end gene selection method ----------------------------------------------------------

		// Chromosome region selection method
		chromosomeInputPanel.setLayout(new BoxLayout(chromosomeInputPanel, BoxLayout.X_AXIS));
		chromosomeInputPanel.add(selectChrRegionLabel);
		chromosomeInputPanel.add(questionMarkLocusInput);
		questionMarkLocusInput.setToolTipText("<html>Input hg19 human genome version coordinates in bp. <br><u> Example for CCR5:</u> Chromosome: 3 Start: 46411633 End: 46417697</html>");
		chromosomeInputPanel.add(chrLabel);
		chromosomeInputPanel.add(chrList);
		chromosomeInputPanel.add(startLabel);
		chromosomeInputPanel.add(startPosTextField);
		startPosTextField.setMaximumSize(startPosTextField.getPreferredSize());
		chromosomeInputPanel.add(endLabel);
		chromosomeInputPanel.add(endPosTextField);
		endPosTextField.setMaximumSize(endPosTextField.getPreferredSize());

		chromosomeESPOptionPanel.add(chrESPCheckBox);

		chromosomeSelectPanel.setLayout(new BoxLayout(chromosomeSelectPanel, BoxLayout.Y_AXIS));
		chromosomeSelectPanel.add(Box.createVerticalGlue());
		chromosomeSelectPanel.setBorder(BorderFactory.createEmptyBorder(0,80,0,80));
		chromosomeSelectPanel.add(chromosomeInputPanel);
		chromosomeSelectPanel.add(chromosomeESPOptionPanel);
		chromosomeSelectPanel.add(Box.createVerticalGlue());
		//chromosomeSelectPanel.setMaximumSize(chromosomeSelectPanel.getPreferredSize());
		// end chromosome selection panel (now called Locus) ----------------------------------

		inputSelect.setMaximumSize(inputSelect.getPreferredSize());

		bigPanel.add(kgPopulationPanel);

		kgPopulationPanel.setLayout(new GridLayout(2,3));
		kgPopulationPanel.setBorder(BorderFactory.createEmptyBorder(0,20,5,20));
		kgPopulationPanel.add(allracesPanel);
		allracesPanel.setLayout(new GridLayout(9,1));
		allracessub[0] = new JCheckBox("ALL All Populations (n=2,504)");

		allracesLabel.setFont(new Font("Serif", Font.BOLD, 20));
		allracesPanel.add(allracesLabel);
		allracesPanel.add(allracessub[0]);

		allracessub[0].addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				setAfrican(0, !allracessub[0].isSelected());
				setAsian(0, !allracessub[0].isSelected());
				setEuropean(0, !allracessub[0].isSelected());
				setAmerican(0, !allracessub[0].isSelected());
				setSouthAsian(0, !allracessub[0].isSelected());
			}
		});

		kgPopulationPanel.add(afrPanel);
		afrPanel.setLayout(new GridLayout(9,1));
		afrsub[0] = new JCheckBox("AFR All Africans (n=661)");
		afrsub[1] = new JCheckBox("ACB African Caribbean (n=96)");
		afrsub[2] = new JCheckBox("ASW African American (n=61)");
		afrsub[3] = new JCheckBox("ESN Esan (n=99)");
		afrsub[4] = new JCheckBox("GWD Gambian (n=113)");
		afrsub[5] = new JCheckBox("LWK Luhya (n=99)");
		afrsub[6] = new JCheckBox("MSL Mende (n=85)");
		afrsub[7] = new JCheckBox("YRI Yoruba (n=108)");
		afrLabel.setFont(new Font("Serif", Font.BOLD, 20));
		afrPanel.add(afrLabel);
		for(int i = 0; i < afrsub.length; i++) {
			afrPanel.add(afrsub[i]);
			if(afrsub[i].getText().contains("n=0")){
				afrsub[i].setEnabled(false);
			}
		}
		afrsub[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setAfrican(1, !afrsub[0].isSelected());
			}
		});

		kgPopulationPanel.add(amrPanel);
		amrPanel.setLayout(new GridLayout(9,1));
		amrsub[0]= new JCheckBox("AMR All Americans (n=347)");
		amrsub[1]= new JCheckBox("CLM Colombian (n=94)");
		amrsub[2]= new JCheckBox("MXL Mexican American (n=64)");
		amrsub[3]= new JCheckBox("PEL Peruvian (n=85)");
		amrsub[4]= new JCheckBox("PUR Puerto Rican (n=104)");
		amrLabel.setFont(new Font("Serif", Font.BOLD, 20));
		amrPanel.add(amrLabel);
		for(int i = 0; i < amrsub.length; i++) {
			amrPanel.add(amrsub[i]);
			if(amrsub[i].getText().contains("n=0")){
				amrsub[i].setEnabled(false);
			}
		}
		amrsub[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setAmerican(1, !amrsub[0].isSelected());
			}
		});

		kgPopulationPanel.add(asnPanel);
		asnPanel.setLayout(new GridLayout(9,1));
		asnsub[0] = new JCheckBox("EAS All East Asians (n=504)");
		asnsub[1] = new JCheckBox("CDX Dai Chinese (n=93)");
		asnsub[2] = new JCheckBox("CHB Han Chinese (n=103)");
		asnsub[3] = new JCheckBox("CHS Southern Han Chinese (n=105)");
		asnsub[4] = new JCheckBox("JPT Japanese (n=104)");
		asnsub[5] = new JCheckBox("KHV Kinh Vietnamese (n=99)");
		asnsub[6] = new JCheckBox("CHD Denver Chinese (n=0)");
		asnLabel.setFont(new Font("Serif", Font.BOLD, 20));
		asnPanel.add(asnLabel);
		for(int i = 0; i < asnsub.length; i++) {
			asnPanel.add(asnsub[i]);
			if(asnsub[i].getText().contains("n=0")){
				asnsub[i].setEnabled(false);
			}
		}
		asnsub[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setAsian(1, !asnsub[0].isSelected());
			}
		});

		kgPopulationPanel.add(eurPanel);
		eurPanel.setLayout(new GridLayout(9,1));
		eursub[0]= new JCheckBox("EUR All Europeans (n=503)");
		eursub[1] = new JCheckBox("CEU CEPH (n=99)");
		eursub[2] = new JCheckBox("GBR British (n=91)");
		eursub[3] = new JCheckBox("FIN Finnish (n=99)");
		eursub[4] = new JCheckBox("IBS Spanish (n=107)");
		eursub[5]= new JCheckBox("TSI Tuscan (n=107)");
		eurLabel.setFont(new Font("Serif", Font.BOLD, 20));
		eurPanel.add(eurLabel);
		for(int i = 0; i < eursub.length; i++) {
			eurPanel.add(eursub[i]);
			if(eursub[i].getText().contains("n=0")){
				eursub[i].setEnabled(false);
			}
		}
		eursub[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setEuropean(1, !eursub[0].isSelected());
			}
		});

		kgPopulationPanel.add(sanPanel);
		sanPanel.setLayout(new GridLayout(9,1));

		sansub[0] = new JCheckBox("SAS All South Asians (n=489)");
		sansub[1] = new JCheckBox("BEB Bengali (n=86)");
		sansub[2] = new JCheckBox("GIH Gujarati Indian (n=103)");
		sansub[3] = new JCheckBox("ITU Indian Telugu (n=102)");
		sansub[4] = new JCheckBox("PJL Punjabi (n=96)");
		sansub[5] = new JCheckBox("STU Sri Lankan Tamil (n=102)");
		sanLabel.setFont(new Font("Serif", Font.BOLD, 20));
		sanPanel.add(sanLabel);
		for(int i = 0; i < sansub.length; i++) {
			sanPanel.add(sansub[i]);
			if(sansub[i].getText().contains("n=0")){
				sansub[i].setEnabled(false);
			}
		}
		sansub[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setSouthAsian(1, !sansub[0].isSelected());
			}
		});

		bigPanel.add(fileChoosePanel);
		fileChoosePanel.add(browseButton);
		browseButton.setPreferredSize(new Dimension(100,30));
		fileChoosePanel.add(fileLocation);
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				saveFileChooser.setDialogTitle("Save As");
				int returnVal = saveFileChooser.showSaveDialog(GUI.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = saveFileChooser.getSelectedFile();
					fileNameAndPath = file.getAbsolutePath();
					fileLocation.setText("File Location: " + fileNameAndPath);
				}			
			}
		});

		bigPanel.add(goPanel);

		goPanel.add(goButton);
		goButton.setPreferredSize(new Dimension(300, 60));

		goPanel.setBackground(Color.gray);

		bigPanel.add(Box.createVerticalGlue());
		SNPFerret.pack();
		SNPFerret.setVisible(true);

		goButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) { // Position

				final long startTime = System.nanoTime();
				ArrayList<CharSequence> populations = new ArrayList<CharSequence>();
				for(int i = 0; i < afrsub.length; i++){
					if(afrsub[i].isSelected()){
						populations.add(afrCode[i]);
					}
				}
				for(int i = 0; i < eursub.length; i++){
					if(eursub[i].isSelected()){
						populations.add(eurCode[i]);
					}
				}
				for(int i = 0; i < sansub.length; i++){
					if(sansub[i].isSelected()){
						populations.add(sanCode[i]);
					}
				}
				for(int i = 0; i < asnsub.length; i++){
					if(asnsub[i].isSelected()){
						populations.add(asnCode[i]);
					}
				}
				for(int i = 0; i < amrsub.length; i++){
					if(amrsub[i].isSelected()){
						populations.add(amrCode[i]);
					}
				}
				if(allracessub[0].isSelected()){
					populations.add("ALL");
				}
				boolean popSelected = !populations.isEmpty(), fileLocSelected = (fileNameAndPath != null);

				if(inputSelect.getSelectedIndex() == 0){
					// Chr position input method
					boolean getESP = chrESPCheckBox.isSelected();
					String chrSelected = (String)chrList.getSelectedItem();
					String startPosition = startPosTextField.getText();
					String endPosition = endPosTextField.getText();

					boolean isChrSelected, startSelected, endSelected, startEndValid = true, withinRange = true;
					int chrEndBound = 0;

					isChrSelected = !chrSelected.equals(" ");

					// Checks to see if number and integer
					if((startSelected = !startPosition.isEmpty())){
						for(int i = 0; i < startPosition.length(); i++){
							if(!Character.isDigit(startPosition.charAt(i))){
								startSelected = false;
							}
						}
					}
					if((endSelected = !endPosition.isEmpty())){
						for(int i = 0; i < endPosition.length(); i++){
							if(!Character.isDigit(endPosition.charAt(i))){
								endSelected = false;
							}
						}
					}

					if(startSelected && endSelected){
						int tempEndPos, tempStartPos;
						try{
							if(startSelected && endSelected){
								startEndValid = Integer.parseInt(endPosition) >= Integer.parseInt(startPosition);
							}
							tempEndPos = Integer.parseInt(endPosition);
							tempStartPos = Integer.parseInt(startPosition);
						}catch(NumberFormatException e){
							tempEndPos = 250000000; tempStartPos = 0;
						}
						if (defaultHG[0]){
							switch (chrSelected){
							case "X": if(tempEndPos > 155270560 || tempStartPos < 1){withinRange = false; chrEndBound = 155270560;} break;
							case "1": if(tempEndPos > 249250621 || tempStartPos < 1){withinRange = false; chrEndBound = 249250621;} break;
							case "2": if(tempEndPos > 243199373 || tempStartPos < 1){withinRange = false; chrEndBound = 243199373;} break;
							case "3": if(tempEndPos > 198022430 || tempStartPos < 1){withinRange = false; chrEndBound = 198022430;} break;
							case "4": if(tempEndPos > 191154276 || tempStartPos < 1){withinRange = false; chrEndBound = 191154276;} break;
							case "5": if(tempEndPos > 180915260 || tempStartPos < 1){withinRange = false; chrEndBound = 180915260;} break;
							case "6": if(tempEndPos > 171115067 || tempStartPos < 1){withinRange = false; chrEndBound = 171115067;} break;
							case "7": if(tempEndPos > 159138663 || tempStartPos < 1){withinRange = false; chrEndBound = 159138663;} break;
							case "8": if(tempEndPos > 146364022 || tempStartPos < 1){withinRange = false; chrEndBound = 146364022;} break;
							case "9": if(tempEndPos > 141213431 || tempStartPos < 1){withinRange = false; chrEndBound = 141213431;} break;
							case "10": if(tempEndPos > 135534747 || tempStartPos < 1){withinRange = false; chrEndBound = 135534747;} break;
							case "11": if(tempEndPos > 135006516 || tempStartPos < 1){withinRange = false; chrEndBound = 135006516;} break;
							case "12": if(tempEndPos > 133851895 || tempStartPos < 1){withinRange = false; chrEndBound = 133851895;} break;
							case "13": if(tempEndPos > 115169878 || tempStartPos < 1){withinRange = false; chrEndBound = 115169878;} break;
							case "14": if(tempEndPos > 107349540 || tempStartPos < 1){withinRange = false; chrEndBound = 107349540;} break;
							case "15": if(tempEndPos > 102531392 || tempStartPos < 1){withinRange = false; chrEndBound = 102531392;} break;
							case "16": if(tempEndPos > 90354753 || tempStartPos < 1){withinRange = false; chrEndBound = 90354753;} break;
							case "17": if(tempEndPos > 81195210 || tempStartPos < 1){withinRange = false; chrEndBound = 81195210;} break;
							case "18": if(tempEndPos > 78077248 || tempStartPos < 1){withinRange = false; chrEndBound = 78077248;} break;
							case "19": if(tempEndPos > 59128983 || tempStartPos < 1){withinRange = false; chrEndBound = 59128983;} break;
							case "20": if(tempEndPos > 63025520 || tempStartPos < 1){withinRange = false; chrEndBound = 63025520;} break;
							case "21": if(tempEndPos > 48129895 || tempStartPos < 1){withinRange = false; chrEndBound = 48129895;} break;
							case "22": if(tempEndPos > 51304566 || tempStartPos < 1){withinRange = false; chrEndBound = 51304566;} break;
							}
						} else {
							switch (chrSelected){
							case "X": if(tempEndPos > 156040895 || tempStartPos < 1){withinRange = false; chrEndBound = 156040895;} break;
							case "1": if(tempEndPos > 248956422 || tempStartPos < 1){withinRange = false; chrEndBound = 248956422;} break;
							case "2": if(tempEndPos > 242193529 || tempStartPos < 1){withinRange = false; chrEndBound = 242193529;} break;
							case "3": if(tempEndPos > 198295559 || tempStartPos < 1){withinRange = false; chrEndBound = 198295559;} break;
							case "4": if(tempEndPos > 190214555 || tempStartPos < 1){withinRange = false; chrEndBound = 190214555;} break;
							case "5": if(tempEndPos > 181538259 || tempStartPos < 1){withinRange = false; chrEndBound = 181538259;} break;
							case "6": if(tempEndPos > 170805979 || tempStartPos < 1){withinRange = false; chrEndBound = 170805979;} break;
							case "7": if(tempEndPos > 159345973 || tempStartPos < 1){withinRange = false; chrEndBound = 159345973;} break;
							case "8": if(tempEndPos > 145138636 || tempStartPos < 1){withinRange = false; chrEndBound = 145138636;} break;
							case "9": if(tempEndPos > 138394717 || tempStartPos < 1){withinRange = false; chrEndBound = 138394717;} break;
							case "10": if(tempEndPos > 133797422 || tempStartPos < 1){withinRange = false; chrEndBound = 133797422;} break;
							case "11": if(tempEndPos > 135086622 || tempStartPos < 1){withinRange = false; chrEndBound = 135086622;} break;
							case "12": if(tempEndPos > 133275309 || tempStartPos < 1){withinRange = false; chrEndBound = 133275309;} break;
							case "13": if(tempEndPos > 114364328 || tempStartPos < 1){withinRange = false; chrEndBound = 114364328;} break;
							case "14": if(tempEndPos > 107043718 || tempStartPos < 1){withinRange = false; chrEndBound = 107043718;} break;
							case "15": if(tempEndPos > 101991189 || tempStartPos < 1){withinRange = false; chrEndBound = 101991189;} break;
							case "16": if(tempEndPos > 90338345 || tempStartPos < 1){withinRange = false; chrEndBound = 90338345;} break;
							case "17": if(tempEndPos > 83257441 || tempStartPos < 1){withinRange = false; chrEndBound = 83257441;} break;
							case "18": if(tempEndPos > 80373285 || tempStartPos < 1){withinRange = false; chrEndBound = 80373285;} break;
							case "19": if(tempEndPos > 58617616 || tempStartPos < 1){withinRange = false; chrEndBound = 58617616;} break;
							case "20": if(tempEndPos > 64444167 || tempStartPos < 1){withinRange = false; chrEndBound = 64444167;} break;
							case "21": if(tempEndPos > 46709983 || tempStartPos < 1){withinRange = false; chrEndBound = 46709983;} break;
							case "22": if(tempEndPos > 50818468 || tempStartPos < 1){withinRange = false; chrEndBound = 50818468;} break;
							}
						}
					}

					boolean espError = false;
					if(getESP && isChrSelected && popSelected && startSelected && endSelected && startEndValid && withinRange && fileLocSelected){
						Process proc;
						try{
							if(System.getProperty("os.name").contains("Windows")){
								File f = new File("C:\\Program Files\\Java\\");
								if(f.exists()){
									FilenameFilter jdkFilter = new FilenameFilter() {
										@Override
										public boolean accept(File dir, String name) {
											return name.contains("jdk");
										}
									};
									File[] jdkList = f.listFiles(jdkFilter);
									CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
									File jarFile = new File(codeSource.getLocation().toURI().getPath());
									String jarDir = jarFile.getParentFile().getPath();

									if(jdkList.length == 0){
										//System.out.println("One installed");
										proc = Runtime.getRuntime().exec("jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										/*BufferedReader stError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
										String line = null;
										while((line = stError.readLine()) != null){
											System.out.println(line);
										}*/
										proc.waitFor();
									} else {
										//System.out.println(jdkList.length + " installed");
										Arrays.sort(jdkList);
										System.out.println(jdkList[0]);
										proc = Runtime.getRuntime().exec(jdkList[0].toString() + "\\bin\\jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										/*BufferedReader stError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
										String line = null;
										while((line = stError.readLine()) != null){
											System.out.println(line);
										}*/
										proc.waitFor();
									}
								}
							}else{
								CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
								File jarFile = new File(codeSource.getLocation().toURI().getPath());
								String jarDir = jarFile.getParentFile().getPath();
								proc = Runtime.getRuntime().exec(new String[] {"bash", "-c", "jar -xf '" + jarDir + "/Ferret_v2.1.1.jar' evsClient0_15.jar"});
								proc.waitFor();
							}
						}catch(IOException e){}
						catch(InterruptedException e){}
						catch (URISyntaxException e){}

						File evs = new File("evsClient0_15.jar");
						if(!evs.exists()){
							int choice = JOptionPane.showOptionDialog(SNPFerret,
									"Ferret encountered a problem with Exome Sequencing Project\n"
											+ "Please check to make sure you have JDK installed (See FAQ)\n"
											+ "Do you want to run Ferret anyway?",
											"Exome Sequencing Project Error",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.PLAIN_MESSAGE,
											null, 
											new Object[] {"Yes", "No"}, 
											null);
							if(choice == JOptionPane.YES_OPTION){
								getESP = false;
							} else {
								espError = true;
							}
						}
					}

					if(isChrSelected && popSelected && startSelected && endSelected && startEndValid && withinRange && fileLocSelected && !espError){

						inputRegion[] queries = {new inputRegion(chrSelected, Integer.parseInt(startPosition), Integer.parseInt(endPosition))};

						// if not get esp, string is none, else if get only ref, then string is ref, else string is both
						// this should be combined with the one single call to Ferret later
						// HERE
						final Integer[] variants = {0};
						String output = null;

						switch (currFileOut[0]){
						case ALL:
							output = "all";
							break;
						case FRQ:
							output = "freq";
							break;
						case VCF:
							output = "vcf";
							break;
						}

						String webAddress = null;
						if (currVersion[0] == version1KG.ONE){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20110521/ALL.chr$.phase1_release_v3.20101123.snps_indels_svs.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & !defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/supporting/GRCh38_positions/ALL.chr$.phase3_shapeit2_mvncall_integrated_v3plus_nounphased.rsID.genotypes.GRCh38_dbSNP_no_SVs.vcf.gz";
						}
						final FerretData currFerretWorker = new FerretData(queries, populations, fileNameAndPath, getESP, progressText, webAddress, mafThreshold[0], ESPMAFBoolean[0] , output);

						currFerretWorker.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								switch (evt.getPropertyName()){
								case "progress":
									progressBar.setValue((Integer) evt.getNewValue());
								case "state":
									try{
										switch ((StateValue) evt.getNewValue()){
										case DONE:
											progressWindow.setVisible(false);
											try{
												variants[0] = currFerretWorker.get();
											} catch (ExecutionException e){
												e.printStackTrace();
												variants[0] = -1;
											} catch (InterruptedException e){
												e.printStackTrace();
												variants[0] = -1;
											}

											new File("evsClient0_15.jar").delete();
											Object[] options ={"Yes","No"};
											int choice;
											System.out.println("Total Time: " + (System.nanoTime() - startTime));
											if(variants[0] == 1){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Files have been downloaded\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null);
											} else if(variants[0] == -3){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"After applying the MAF threshold, no variants were found"
														+ "\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else if(variants[0] == 0) {
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"No variants were found in this region\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else {
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Ferret has encountered a problem downloading data. \n"
																+ "Please try again later or consult the FAQ. \nDo you want to close Ferret?",
																"Close Ferret?",
																JOptionPane.YES_NO_OPTION,
																JOptionPane.PLAIN_MESSAGE,
																null, 
																options, 
																null); 
											}
											if(choice == JOptionPane.YES_OPTION ){
												SNPFerret.dispose();
												System.exit(0);
											}else{
												enableComponents(SNPFerret, true);
												if (currFileOut[0] == fileOutput.VCF){
													snpESPCheckBox.setEnabled(false);
													geneESPCheckBox.setEnabled(false);
													chrESPCheckBox.setEnabled(false);
												}
												for(int i = 0; i < asnsub.length; i++) {
													asnPanel.add(asnsub[i]);
													if(asnsub[i].getText().contains("n=0")){
														asnsub[i].setEnabled(false);
													}
												}
												progressText.setText("Initializing...");
												progressBar.setValue(0);
												checkBoxReset();
											}
											break;
										case STARTED:
										case PENDING:
											Dimension windowSize = SNPFerret.getSize();
											progressWindow.setSize(new Dimension((int)(windowSize.width*.5),(int)(windowSize.height*.2)));
											progressWindow.setLocationRelativeTo(SNPFerret);
											progressWindow.setVisible(true);
											enableComponents(SNPFerret, false);
										}
									}catch(ClassCastException e){}
								}

							}
						});
						currFerretWorker.execute();

					} else {
						StringBuffer errorMessage = new StringBuffer("Correct the following errors:");
						if(!isChrSelected){
							errorMessage.append("\n Select a chromosome");
						}
						if(!popSelected){
							errorMessage.append("\n Select one or more populations");
						}
						if(!startSelected){
							errorMessage.append("\n Enter a valid, integer starting position");
						}
						if(!endSelected){
							errorMessage.append("\n Enter a valid, integer ending position");
						}
						if(!startEndValid){
							errorMessage.append("\n Starting position must be less than ending position");
						}
						if(!withinRange){
							errorMessage.append("\n Invalid chromosome positions. Valid positions for chr" + chrSelected + " are from 1 to " + chrEndBound);
						}
						if(!fileLocSelected){
							errorMessage.append("\n Select a destination for the files to be saved");
						}
						if(espError){
							errorMessage.append("\n JDK error. Consult the FAQ for help with exome sequencing project errors.");
						}
						JOptionPane.showMessageDialog(SNPFerret, errorMessage,"Error",JOptionPane.OK_OPTION);
					}

				} else if(inputSelect.getSelectedIndex() == 1){	// Gene starts after this line ------------------------------------------------------------------
					boolean getESP = geneESPCheckBox.isSelected();
					String geneString = geneNameField.getText();
					String[] geneListArray = null;
					boolean geneListInputted = geneString.length() > 0;
					boolean geneFileImported = geneFileNameAndPath != null;
					boolean geneFileError = false;
					boolean geneFileExtensionError = false;
					boolean invalidCharacter = false;
					boolean geneNameInputted = geneNameRadioButton.isSelected();
					boolean fromNCBI = geneNCBIRadioButton.isSelected();

					String invalidRegex;
					if(geneNameInputted){
						invalidRegex = ".*[^a-zA-Z0-9\\-].*"; // This is everything except letters and numbers, including underscore
					} else {
						invalidRegex = ".*\\D.*"; // This is everything except numbers
					}

					if(geneFileImported){
						if(geneFileNameAndPath.length() <= 4){
							geneFileError = true;
						} else {
							String fileType = geneFileNameAndPath.substring(geneFileNameAndPath.length()-4);
							String delimiter = null;
							switch (fileType) {
							case ".csv":
								delimiter = ",";
								break;
							case ".tab":
							case ".tsv":
								delimiter = "\\t";
								break;
							case ".txt":
								delimiter = " ";
								break;
							default:
								geneFileExtensionError = true;
								break;
							}
							ArrayList<String> geneListArrayList = new ArrayList<String>();

							if(delimiter != null){
								try (
										BufferedReader geneFileRead = new BufferedReader(new FileReader(geneFileNameAndPath));
										){
									String geneStringToParse;
									while((geneStringToParse = geneFileRead.readLine()) != null){
										String[] text = geneStringToParse.split(delimiter);
										for(int i = 0; i < text.length; i++){
											text[i] = text[i].replace(" ", "").toUpperCase(new Locale("all")); // remove spaces
											if(text[i].matches(invalidRegex)){ // identify invalid characters
												invalidCharacter = true;
												break;
											}
											if(text[i].length() > 0){
												geneListArrayList.add(text[i]);
											}
										}
									}
									geneListArray = geneListArrayList.toArray(new String[geneListArrayList.size()]);
								} catch(IOException e){
									//e.printStackTrace();
									geneFileError = true;
								} catch(NullPointerException e){
									//File is empty 
									geneFileError = true;
								}
							}
						}

					} else if(geneListInputted){
						geneString = geneString.toUpperCase(new Locale("all"));
						String geneList = geneString.replace(" ", "");
						invalidCharacter = geneList.replace(",","").matches(invalidRegex);
						if(geneList.endsWith(",")){
							geneList = geneList.substring(0, geneList.length()-1);
						}
						geneListArray = geneList.split(",");
					}

					boolean espError = false;
					if(getESP && (geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError)) && !invalidCharacter && popSelected && fileLocSelected){
						Process proc;
						try{
							if(System.getProperty("os.name").contains("Windows")){
								File f = new File("C:\\Program Files\\Java\\");
								if(f.exists()){

									FilenameFilter jdkFilter = new FilenameFilter() {
										@Override
										public boolean accept(File dir, String name) {
											return name.contains("jdk");
										}
									};

									File[] jdkList = f.listFiles(jdkFilter);
									CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
									File jarFile = new File(codeSource.getLocation().toURI().getPath());
									String jarDir = jarFile.getParentFile().getPath();

									if(jdkList.length == 0){
										proc = Runtime.getRuntime().exec("jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										proc.waitFor();
									} else {
										Arrays.sort(jdkList);
										proc = Runtime.getRuntime().exec(jdkList[0].toString() + "\\bin\\jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										proc.waitFor();
									}
								}
							}else{
								CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
								File jarFile = new File(codeSource.getLocation().toURI().getPath());
								String jarDir = jarFile.getParentFile().getPath();
								proc = Runtime.getRuntime().exec(new String[] {"bash", "-c", "jar -xf '" + jarDir + "/Ferret_v2.1.1.jar' evsClient0_15.jar"});
								proc.waitFor();
							}
						}catch(IOException e){}
						catch(InterruptedException e){}
						catch(URISyntaxException e){}

						File evs = new File("evsClient0_15.jar");
						if(!evs.exists()){
							int choice = JOptionPane.showOptionDialog(SNPFerret,
									"Ferret encountered a problem with Exome Sequencing Project\n"
											+ "Please check to make sure you have JDK installed (See FAQ)\n"
											+ "Do you want to run Ferret anyway?",
											"Exome Sequencing Project Error",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.PLAIN_MESSAGE,
											null, 
											new Object[] {"Yes", "No"}, 
											null);
							if(choice == JOptionPane.YES_OPTION){
								getESP = false;
							} else {
								espError = true;
							}
						}
					}

					if((geneListInputted || (geneFileImported && !geneFileError && !geneFileExtensionError)) && !invalidCharacter && popSelected && fileLocSelected && !espError){

						// this should be combined with the one single call to Ferret later
						final Integer[] variants = {0};
						String output = null;

						switch (currFileOut[0]){
						case ALL:
							output = "all";
							break;
						case FRQ:
							output = "freq";
							break;
						case VCF:
							output = "vcf";
							break;
						}

						String webAddress = null;


						if (currVersion[0] == version1KG.ONE){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20110521/ALL.chr$.phase1_release_v3.20101123.snps_indels_svs.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & !defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/supporting/GRCh38_positions/ALL.chr$.phase3_shapeit2_mvncall_integrated_v3plus_nounphased.rsID.genotypes.GRCh38_dbSNP_no_SVs.vcf.gz";
						}

						String geneQueryType;
						if(geneNameInputted){
							geneQueryType = "geneName";
						} else{
							geneQueryType = "geneID";
						}

						final FerretData currFerretWorker = new FerretData(geneQueryType, geneListArray, populations, fileNameAndPath, getESP, progressText, webAddress, mafThreshold[0], ESPMAFBoolean[0] , output,defaultHG[0]);

						currFerretWorker.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								switch (evt.getPropertyName()){
								case "progress":
									progressBar.setValue((Integer) evt.getNewValue());
								case "state":
									try{
										switch ((StateValue) evt.getNewValue()){
										case DONE:
											progressWindow.setVisible(false);
											try{
												variants[0] = currFerretWorker.get();
											} catch (ExecutionException e){
												e.printStackTrace();
												variants[0] = -1;
											} catch (InterruptedException e){
												e.printStackTrace();
												variants[0] = -1;
											}

											new File("evsClient0_15.jar").delete();
											Object[] options ={"Yes","No"};
											int choice;
											System.out.println("Total Time: " + (System.nanoTime() - startTime));
											if(variants[0] == 1){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Files have been downloaded\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null);
											} else if(variants[0] == -3){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"After applying the MAF threshold, no variants were found"
														+ "\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else if(variants[0] == 0) {
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"No variants were found in this region\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else if(variants[0] == -1){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Ferret has encountered a problem downloading data. \n"
																+ "Please try again later or consult the FAQ. \nDo you want to close Ferret?",
																"Close Ferret?",
																JOptionPane.YES_NO_OPTION,
																JOptionPane.PLAIN_MESSAGE,
																null, 
																options, 
																null); 
											} else {
												choice = JOptionPane.NO_OPTION;
											}
											if(choice == JOptionPane.YES_OPTION ){
												SNPFerret.dispose();
												System.exit(0);
											}else{
												enableComponents(SNPFerret, true);
												if (currFileOut[0] == fileOutput.VCF){
													snpESPCheckBox.setEnabled(false);
													geneESPCheckBox.setEnabled(false);
													chrESPCheckBox.setEnabled(false);
												}
												for(int i = 0; i < asnsub.length; i++) {
													asnPanel.add(asnsub[i]);
													if(asnsub[i].getText().contains("n=0")){
														asnsub[i].setEnabled(false);
													}
												}
												progressText.setText("Initializing...");
												progressBar.setValue(0);
												checkBoxReset();
											}
											break;
										case STARTED:
										case PENDING:
											Dimension windowSize = SNPFerret.getSize();
											progressWindow.setSize(new Dimension((int)(windowSize.width*.5),(int)(windowSize.height*.2)));
											progressWindow.setLocationRelativeTo(SNPFerret);
											progressWindow.setVisible(true);
											enableComponents(SNPFerret, false);
										}
									}catch(ClassCastException e){}
								}

							}
						});
						currFerretWorker.execute();

					} else {
						StringBuffer errorMessage = new StringBuffer("Correct the following errors:");
						if(!geneListInputted && !geneFileImported){
							errorMessage.append("\n Enter a gene name/ID or select a file");
						}
						if(geneFileImported && geneFileError){
							errorMessage.append("\n There was a problem reading the Gene file. Please check the FAQ.");
						}
						if(geneFileImported && geneFileExtensionError){
							errorMessage.append("\n Invalid file extension. Ferret supports tsv, csv, tab, and txt files.");
						}
						if((geneListInputted || geneFileImported) && invalidCharacter){
							errorMessage.append("\n Invalid character entered");
						}
						if(!fileLocSelected){
							errorMessage.append("\n Select a destination for the files to be saved");
						}
						if(!popSelected){
							errorMessage.append("\n Select one or more populations");
						}
						if(espError){
							errorMessage.append("\n JDK error. Consult the FAQ for help with Exome Sequencing Project errors.");
						}
						JOptionPane.showMessageDialog(SNPFerret, errorMessage,"Error",JOptionPane.OK_OPTION);
					}
				} else { // SNP starts here ---------------------------------------------------------------------------------

					boolean getESP = snpESPCheckBox.isSelected();
					String snpString = snpTextField.getText();
					boolean snpListInputted = snpString.length() > 0;
					boolean snpFileImported = snpFileNameAndPath != null;
					boolean snpFileError = false;
					boolean snpFileExtensionError = false;
					boolean invalidCharacter = false;
					boolean fromNCBI = snpNCBIRadioButton.isSelected();
					String invalidRegex = ".*\\D.*"; // This is everything except numbers
					ArrayList<String> snpListArray = new ArrayList<String>();
					String snpWindowSize = snpWindowField.getText();
					boolean snpWindowSelected = snpWindowCheckBox.isSelected();
					boolean validWindowSizeEntered = true; // must be both not empty and an int

					if(snpWindowSelected){
						if(snpWindowSize.length() == 0){
							validWindowSizeEntered = false; // must have something there
						} else { // test for non ints
							for(int i = 0; i < snpWindowSize.length(); i++){
								if(!Character.isDigit(snpWindowSize.charAt(i))){
									validWindowSizeEntered = false;
								}
							}
						}
					}else{ //if no window specified, it's always fine
						snpWindowSize = "0";
					}

					if(snpFileImported){
						if(snpFileNameAndPath.length() <= 4){
							snpFileError = true;
						} else {
							String fileType = snpFileNameAndPath.substring(snpFileNameAndPath.length()-4);
							String delimiter = null;
							switch (fileType) {
							case ".csv":
								delimiter = ",";
								break;
							case ".tab":
							case ".tsv":
								delimiter = "\\t";
								break;
							case ".txt":
								delimiter = " ";
								break;
							default:
								snpFileExtensionError = true;
								break;
							}
							if(delimiter != null){
								try (
										BufferedReader snpFileRead = new BufferedReader(new FileReader(snpFileNameAndPath));
										){
									String snpStringToParse;
									while((snpStringToParse = snpFileRead.readLine()) != null){
										String[] text = snpStringToParse.split(delimiter);
										for(int i = 0; i < text.length; i++){
											text[i] = text[i].replace(" ", ""); // remove spaces
											if(text[i].matches(invalidRegex)){ // identify invalid characters
												invalidCharacter = true; // probably can just throw error here, might be easier/more straight forward. But then errors wouldn't be 'accumulated' to the end
												break;
											}
											if(text[i].length() > 0){
												snpListArray.add(text[i]);
											}
										}
									}
								} catch(IOException e) {
									//e.printStackTrace();
									snpFileError = true;
								} catch(NullPointerException e){
									snpFileError = true;
								}
							}
						}

					} else if(snpListInputted){

						while(snpString.endsWith(",") || snpString.endsWith(" ")){ // maybe this should be added for gene input too
							snpString = snpString.substring(0, snpString.length()-1);
						}
						String[] text = snpString.split(",");
						for(int i = 0; i < text.length; i++){
							text[i] = text[i].replace(" ", "");// remove spaces
							if(text[i].matches(invalidRegex)){
								invalidCharacter = true;
								break;
							}
						}
						snpListArray = new ArrayList<String>(Arrays.asList(text));
					}

					boolean espError = false;
					if(getESP && (snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError)) && !invalidCharacter && validWindowSizeEntered && popSelected && fileLocSelected){
						Process proc;
						try{
							if(System.getProperty("os.name").contains("Windows")){
								File f = new File("C:\\Program Files\\Java\\");
								if(f.exists()){

									FilenameFilter jdkFilter = new FilenameFilter() {
										@Override
										public boolean accept(File dir, String name) {
											return name.contains("jdk");
										}
									};

									File[] jdkList = f.listFiles(jdkFilter);
									CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
									File jarFile = new File(codeSource.getLocation().toURI().getPath());
									String jarDir = jarFile.getParentFile().getPath();

									if(jdkList.length == 0){
										proc = Runtime.getRuntime().exec("jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										proc.waitFor();
									} else {
										Arrays.sort(jdkList);
										proc = Runtime.getRuntime().exec(jdkList[0].toString() + "\\bin\\jar -xf \"" + jarDir + "\\Ferret_v2.1.1.jar\" evsClient0_15.jar");
										proc.waitFor();
									}
								}
							}else{
								CodeSource codeSource = Ferret.class.getProtectionDomain().getCodeSource();
								File jarFile = new File(codeSource.getLocation().toURI().getPath());
								String jarDir = jarFile.getParentFile().getPath();
								proc = Runtime.getRuntime().exec(new String[] {"bash", "-c", "jar -xf '" + jarDir + "/Ferret_v2.1.1.jar' evsClient0_15.jar"});
								proc.waitFor();
							}
						}catch(IOException e){}
						catch(InterruptedException e){}
						catch(URISyntaxException e){}

						File evs = new File("evsClient0_15.jar");
						if(!evs.exists()){
							int choice = JOptionPane.showOptionDialog(SNPFerret,
									"Ferret encountered a problem with Exome Sequencing Project\n "
											+ "Please check to make sure you have JDK installed (See FAQ)\n"
											+ "Do you want to run Ferret anyway?",
											"Exome Sequencing Project Error",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.PLAIN_MESSAGE,
											null, 
											new Object[] {"Yes", "No"}, 
											null);
							if(choice == JOptionPane.YES_OPTION){
								getESP = false;
							} else {
								espError = true;
							}
						}
					}

					if((snpListInputted || (snpFileImported && !snpFileError && !snpFileExtensionError)) && !invalidCharacter && validWindowSizeEntered && popSelected && fileLocSelected && !espError){

						final Integer[] variants = {0};
						String output = null;

						switch (currFileOut[0]){
						case ALL:
							output = "all";
							break;
						case FRQ:
							output = "freq";
							break;
						case VCF:
							output = "vcf";
							break;
						}

						String webAddress = null;

						if (currVersion[0] == version1KG.ONE){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20110521/ALL.chr$.phase1_release_v3.20101123.snps_indels_svs.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr$.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz";
						} else if (currVersion[0] == version1KG.THREE & !defaultHG[0]){
							webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/supporting/GRCh38_positions/ALL.chr$.phase3_shapeit2_mvncall_integrated_v3plus_nounphased.rsID.genotypes.GRCh38_dbSNP_no_SVs.vcf.gz";
						}


						final FerretData currFerretWorker = new FerretData("SNP", snpListArray, populations, fileNameAndPath, getESP, progressText, webAddress, mafThreshold[0], 
								ESPMAFBoolean[0] , output, defaultHG[0],snpWindowSelected,Integer.parseInt(snpWindowSize));

						currFerretWorker.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								switch (evt.getPropertyName()){
								case "progress":
									progressBar.setValue((Integer) evt.getNewValue());
								case "state":
									try{
										switch ((StateValue) evt.getNewValue()){
										case DONE:
											progressWindow.setVisible(false);
											try{
												variants[0] = currFerretWorker.get();
											} catch (ExecutionException e){
												e.printStackTrace();
												variants[0] = -1;
											} catch (InterruptedException e){
												e.printStackTrace();
												variants[0] = -1;
											}

											new File("evsClient0_15.jar").delete();
											Object[] options ={"Yes","No"};
											int choice;
											System.out.println("Total Time: " + (System.nanoTime() - startTime));
											if(variants[0] == 1){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Files have been downloaded\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null);
											}else if(variants[0] == -3){
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"After applying the MAF threshold, no variants were found"
														+ "\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else if(variants[0] == 0) {
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"No variants were found in this region\nDo you want to close Ferret?",
														"Close Ferret?",
														JOptionPane.YES_NO_OPTION,
														JOptionPane.PLAIN_MESSAGE,
														null, 
														options, 
														null); 
											} else if(variants[0] == -1) {
												choice = JOptionPane.showOptionDialog(SNPFerret,
														"Ferret has encountered a problem downloading data. \n"
																+ "Please try again later or consult the FAQ. \nDo you want to close Ferret?",
																"Close Ferret?",
																JOptionPane.YES_NO_OPTION,
																JOptionPane.PLAIN_MESSAGE,
																null, 
																options, 
																null); 
											} else { //Only comes here if no SNPs found and user does not want to quit or 
												//not all SNPs found and user doesn't wish to continue with partial query
												choice = JOptionPane.NO_OPTION;
											}
											if(choice == JOptionPane.YES_OPTION ){
												SNPFerret.dispose();
												System.exit(0);
											}else{
												enableComponents(SNPFerret, true);
												if (currFileOut[0] == fileOutput.VCF){
													snpESPCheckBox.setEnabled(false);
													geneESPCheckBox.setEnabled(false);
													chrESPCheckBox.setEnabled(false);
												}
												for(int i = 0; i < asnsub.length; i++) {
													asnPanel.add(asnsub[i]);
													if(asnsub[i].getText().contains("n=0")){
														asnsub[i].setEnabled(false);
													}
												}
												progressText.setText("Initializing...");
												progressBar.setValue(0);
												checkBoxReset();
											}
											break;
										case STARTED:
										case PENDING:
											Dimension windowSize = SNPFerret.getSize();
											progressWindow.setSize(new Dimension((int)(windowSize.width*.5),(int)(windowSize.height*.2)));
											progressWindow.setLocationRelativeTo(SNPFerret);
											progressWindow.setVisible(true);
											enableComponents(SNPFerret, false);
										}
									}catch(ClassCastException e){}
								}

							}
						});
						currFerretWorker.execute();

					} else {

						StringBuffer errorMessage = new StringBuffer("Correct the following errors:");
						if(!snpListInputted && !snpFileImported){
							errorMessage.append("\n Enter a variant number or select a file");
						}
						if(snpFileImported && snpFileError){
							errorMessage.append("\n There was a problem reading the variant file. Please check the FAQ.");
						}
						if(snpFileImported && snpFileExtensionError){
							errorMessage.append("\n Invalid file extension. Ferret supports tsv, csv, tab, and txt files.");
						}
						if((snpListInputted || snpFileImported) && invalidCharacter){
							errorMessage.append("\n Invalid character entered");
						}
						if(!fileLocSelected){
							errorMessage.append("\n Select a destination for the files to be saved");
						}
						if(!popSelected){
							errorMessage.append("\n Select one or more populations");
						}
						if(!validWindowSizeEntered){
							errorMessage.append("\n You must enter an integer window size if you wish to retrieve regions around variants");
						}
						if(espError){
							errorMessage.append("\n JDK error. Consult the FAQ for help with Exome Sequencing Project errors.");
						}
						JOptionPane.showMessageDialog(SNPFerret, errorMessage,"Error",JOptionPane.OK_OPTION);

					}

				}
			}
		});

		//colors
	}

	private void checkBoxReset(){
		if (allracessub[0].isSelected()){
			setAfrican(0, false);
			setEuropean(0, false);
			setAmerican(0, false);
			setSouthAsian(0, false);
			setAsian(0, false);
		}else{
			if (afrsub[0].isSelected()){
				setAfrican(1, false);
			}
			if (eursub[0].isSelected()){
				setEuropean(1, false);
			}
			if (amrsub[0].isSelected()){
				setAmerican(1, false);
			}
			if (sansub[0].isSelected()){
				setSouthAsian(1, false);
			}
			if (asnsub[0].isSelected()){
				setAsian(1, false);
			}
		}
	}

	private void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container)component, enable);
			}
		}
	}

	private void setAfrican(int start, boolean state){
		for(int i=start; i<afrsub.length; i++){
			if(!afrsub[i].getText().contains("n=0)")){
				afrsub[i].setEnabled(state);
			}
			afrsub[i].setSelected(false);
			afrsub[i].updateUI();
		}
	}

	private void setAsian(int start, boolean state){
		for(int i=start; i<asnsub.length; i++){
			if(!asnsub[i].getText().contains("n=0)")){
				asnsub[i].setEnabled(state);
			}
			asnsub[i].setSelected(false);
			asnPanel.updateUI();
		}
	}

	private void setEuropean(int start, boolean state){
		for(int i=start; i<eursub.length; i++){
			if(!eursub[i].getText().contains("n=0)")){
				eursub[i].setEnabled(state);
			}
			eursub[i].setSelected(false);
			eursub[i].updateUI();
		}
	}

	private void setAmerican(int start, boolean state){
		for(int i=start; i<amrsub.length; i++){
			if(!amrsub[i].getText().contains("n=0)")){
				amrsub[i].setEnabled(state);
			}
			amrsub[i].setSelected(false);
			amrPanel.updateUI();
		}
	}

	private void setSouthAsian(int start, boolean state){
		for(int i=start; i<sansub.length; i++){
			if(!sansub[i].getText().contains("n=0)")){
				sansub[i].setEnabled(state);
			}
			sansub[i].setSelected(false);
			sansub[i].updateUI();
		}
	}

	private void setPhase1(){

		allracessub[0].setText("ALL All Populations (n=1,092)");

		sansub[0].setText("SAS All South Asians (n=0)");
		sansub[1].setText("BEB Bengali (n=0)");
		sansub[2].setText("GIH Gujarati Indian (n=0)");
		sansub[3].setText("ITU Indian Telugu (n=0)");
		sansub[4].setText("PJL Punjabi (n=0)");
		sansub[5].setText("STU Sri Lankan Tamil (n=0)");
		if(!allracessub[0].isSelected() && !sansub[0].isSelected()){
			for (int i = 0; i < sansub.length;i++){
				if(sansub[i].getText().contains("n=0)")){
					sansub[i].setEnabled(false);
					if (sansub[i].isSelected()){
						sansub[i].setSelected(false);
					}
				} else {
					sansub[i].setEnabled(true);
				}
			}
		}

		eursub[0].setText("EUR All Europeans (n=379)");
		eursub[1].setText("CEU CEPH (n=85)");
		eursub[2].setText("GBR British (n=89)");
		eursub[3].setText("FIN Finnish (n=93)");
		eursub[4].setText("IBS Spanish (n=14)");
		eursub[5].setText("TSI Tuscan (n=98)");
		if(!allracessub[0].isSelected() && !eursub[0].isSelected()){
			for (int i = 0; i < eursub.length;i++){
				if(eursub[i].getText().contains("n=0)")){
					eursub[i].setEnabled(false);
					if (eursub[i].isSelected()){
						eursub[i].setSelected(false);
					}
				} else {
					eursub[i].setEnabled(true);
				}
			}
		}

		asnsub[0].setText("EAS All East Asians (n=286)");
		asnsub[1].setText("CDX Dai Chinese (n=0)");
		asnsub[2].setText("CHB Han Chinese (n=97)");
		asnsub[3].setText("CHS Southern Han Chinese (n=100)");
		asnsub[4].setText("JPT Japanese (n=89)");
		asnsub[5].setText("KHV Kinh Vietnamese (n=0)");
		asnsub[6].setText("CHD Denver Chinese (n=0)");
		if(!allracessub[0].isSelected() && !asnsub[0].isSelected()){
			for (int i = 0; i < asnsub.length;i++){
				if(asnsub[i].getText().contains("n=0)")){
					asnsub[i].setEnabled(false);
					if (asnsub[i].isSelected()){
						asnsub[i].setSelected(false);
					}
				} else {
					asnsub[i].setEnabled(true);
				}
			}
		}

		amrsub[0].setText("AMR All Americans (n=181)");
		amrsub[1].setText("CLM Colombian (n=60)");
		amrsub[2].setText("MXL Mexican American (n=66)");
		amrsub[3].setText("PEL Peruvian (n=0)");
		amrsub[4].setText("PUR Puerto Rican (n=55)");
		if(!allracessub[0].isSelected() && !amrsub[0].isSelected()){
			for (int i = 0; i < amrsub.length;i++){
				if(amrsub[i].getText().contains("n=0)")){
					amrsub[i].setEnabled(false);
					if (amrsub[i].isSelected()){
						amrsub[i].setSelected(false);
					}
				} else {
					amrsub[i].setEnabled(true);
				}
			}
		}

		afrsub[0].setText("AFR All Africans (n=246)");
		afrsub[1].setText("ACB African Caribbean (n=0)");
		afrsub[2].setText("ASW African American (n=61)");
		afrsub[3].setText("ESN Esan (n=0)");
		afrsub[4].setText("GWD Gambian (n=0)");
		afrsub[5].setText("LWK Luhya (n=97)");
		afrsub[6].setText("MSL Mende (n=0)");
		afrsub[7].setText("YRI Yoruba (n=88)");
		if(!allracessub[0].isSelected() && !afrsub[0].isSelected()){
			for (int i = 0; i < afrsub.length;i++){
				if(afrsub[i].getText().contains("n=0)")){
					afrsub[i].setEnabled(false);
					if (afrsub[i].isSelected()){
						afrsub[i].setSelected(false);
					}
				} else {
					afrsub[i].setEnabled(true);
				}
			}
		}
	}

	private void setPhase3(){
		allracessub[0].setText("ALL All Populations (n=2,504)");

		sansub[0].setText("SAS All South Asians (n=489)");
		sansub[1].setText("BEB Bengali (n=86)");
		sansub[2].setText("GIH Gujarati Indian (n=103)");
		sansub[3].setText("ITU Indian Telugu (n=102)");
		sansub[4].setText("PJL Punjabi (n=96)");
		sansub[5].setText("STU Sri Lankan Tamil (n=102)");
		if(!allracessub[0].isSelected() && !sansub[0].isSelected()){
			for (int i = 0; i < sansub.length;i++){
				if(sansub[i].getText().contains("n=0)")){
					sansub[i].setEnabled(false);
					if (sansub[i].isSelected()){
						sansub[i].setSelected(false);
					}
				} else {
					sansub[i].setEnabled(true);
				}
			}
		}

		eursub[0].setText("EUR All Europeans (n=503)");
		eursub[1].setText("CEU CEPH (n=99)");
		eursub[2].setText("GBR British (n=91)");
		eursub[3].setText("FIN Finnish (n=99)");
		eursub[4].setText("IBS Spanish (n=107)");
		eursub[5].setText("TSI Tuscan (n=107)");
		if(!allracessub[0].isSelected() && !eursub[0].isSelected()){
			for (int i = 0; i < eursub.length;i++){
				if(eursub[i].getText().contains("n=0)")){
					eursub[i].setEnabled(false);
					if (eursub[i].isSelected()){
						eursub[i].setSelected(false);
					}
				} else {
					eursub[i].setEnabled(true);
				}
			}
		}

		asnsub[0].setText("EAS All East Asians (n=504)");
		asnsub[1].setText("CDX Dai Chinese (n=93)");
		asnsub[2].setText("CHB Han Chinese (n=103)");
		asnsub[3].setText("CHS Southern Han Chinese (n=105)");
		asnsub[4].setText("JPT Japanese (n=104)");
		asnsub[5].setText("KHV Kinh Vietnamese (n=99)");
		asnsub[6].setText("CHD Denver Chinese (n=0)");
		if(!allracessub[0].isSelected() && !asnsub[0].isSelected()){
			for (int i = 0; i < asnsub.length;i++){
				if(asnsub[i].getText().contains("n=0)")){
					asnsub[i].setEnabled(false);
					if (asnsub[i].isSelected()){
						asnsub[i].setSelected(false);
					}
				} else {
					asnsub[i].setEnabled(true);
				}
			}
		}

		amrsub[0].setText("AMR All Americans (n=347)");
		amrsub[1].setText("CLM Colombian (n=94)");
		amrsub[2].setText("MXL Mexican American (n=64)");
		amrsub[3].setText("PEL Peruvian (n=85)");
		amrsub[4].setText("PUR Puerto Rican (n=104)");
		if(!allracessub[0].isSelected() && !amrsub[0].isSelected()){
			for (int i = 0; i < amrsub.length;i++){
				if(amrsub[i].getText().contains("n=0)")){
					amrsub[i].setEnabled(false);
					if (amrsub[i].isSelected()){
						amrsub[i].setSelected(false);
					}
				} else {
					amrsub[i].setEnabled(true);
				}
			}
		}

		afrsub[0].setText("AFR All Africans (n=661)");
		afrsub[1].setText("ACB African Caribbean (n=96)");
		afrsub[2].setText("ASW African American (n=61)");
		afrsub[3].setText("ESN Esan (n=99)");
		afrsub[4].setText("GWD Gambian (n=113)");
		afrsub[5].setText("LWK Luhya (n=99)");
		afrsub[6].setText("MSL Mende (n=85)");
		afrsub[7].setText("YRI Yoruba (n=108)");
		if(!allracessub[0].isSelected() && !afrsub[0].isSelected()){
			for (int i = 0; i < afrsub.length;i++){
				if(afrsub[i].getText().contains("n=0)")){
					afrsub[i].setEnabled(false);
					if (afrsub[i].isSelected()){
						afrsub[i].setSelected(false);
					}
				} else {
					afrsub[i].setEnabled(true);
				}
			}
		}
	}

	private static inputRegion[] getQueryFromSNPFrozenDb(String[] snpListArray){ // this is not used
		if(snpListArray.length == 0){
			return null;
		}
		StringBuffer snpList = new StringBuffer();
		for(int i = 0; i < snpListArray.length-1; i++){
			snpList.append("=" + snpListArray[i] + ",");
		}
		snpList.append("=" + snpListArray[snpListArray.length - 1]);
		// need to put in check if only one SNP given and it's a bad one
		try{
			URL frozenSNPDbURL = new URL("http://lgdfm3.ncifcrf.gov/bic/SNPs/recordlisturl.php?RS=" + snpList);
			BufferedReader br = new BufferedReader(new InputStreamReader(frozenSNPDbURL.openStream()));
			String currentString;
			while((currentString = br.readLine()) != null && !currentString.contains("<table cellpadding=\"0\" cellspacing=\"0\" class=\"browse_records\">")){
			}
			while((currentString = br.readLine()) != null){
			}
		} catch (MalformedURLException e){
			//e.printStackTrace();

		} catch (IOException e){
			//e.printStackTrace();

		}
		return null;
	}

	public class checkForUpdate extends SwingWorker<Boolean,Object>{
		Boolean needUpdate = null, urgentUpdate = null;
		String updateMessage = null;

		public Boolean needUpdate(){
			return needUpdate;
		}

		public Boolean urgentUpdate(){
			return urgentUpdate;
		}

		public String updateStatus(){
			return updateMessage;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			try{

				URL urlLocation = new URL("https://webspace.princeton.edu/users/taverner/updateFerret.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(urlLocation.openStream()));
				String currentString;
				while((currentString = br.readLine()) != null){
					String[] updateInformation = currentString.split(":");
					if(Integer.parseInt(updateInformation[0]) <= 3){ // 2 is the internal version number of this version of Ferret
						if(updateInformation[1].equals("urgentUpdate")){
							updateMessage = "Urgent update. Ferret may not be functional until you update: " + updateInformation[2];
							needUpdate = true;
							urgentUpdate = true;
						} else if(updateInformation[1].equals("recommendedUpdate")) {
							updateMessage = "Recommended update: " + updateInformation[2];
							needUpdate = true;
							urgentUpdate = false;
						} else if(updateInformation[1].equals("noUpdate")){
							updateMessage = "Ferret is up to date";
							needUpdate = false;
							urgentUpdate = false;
						}
					}
				}
				if (needUpdate == null || urgentUpdate == null){
					needUpdate = true;
					urgentUpdate = true;
					updateMessage = "Unable to contact update server. Try again later or update Ferret.";
				}
			} catch(IOException e){
				needUpdate = true;
				urgentUpdate = true;
				updateMessage = "Unable to contact update server. Try again later or update Ferret.";
			}			
			return needUpdate;
		}

	}
}