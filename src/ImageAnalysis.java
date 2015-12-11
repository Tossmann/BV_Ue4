// BV Ue04 WS2015/16 Vorgabe
//
// Copyright (C) 2015 by Klaus Jung

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ImageAnalysis extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String author = "Trautmann, Heinig";		// TODO: type in your name here
	private static final String initialFilename = "mountains.png";
	private static final File openPath = new File(".");
	private static final int border = 10;
	private static final int maxWidth = 910; 
	private static final int maxHeight = 910; 
	private static final int graySteps = 256;

	private static int width;
	private static int height;

	private static JFrame frame;

	private ImageView imgView;						// image view
	private HistoView histoView = new HistoView();	// histogram view
	private StatsView statsView = new StatsView();	// statistics values view
	private JSlider brightnessSlider;				// brightness Slider

	// TODO: add an array to hold the histogram of the loaded image
	private int[] histogram = new int[graySteps];
	// TODO: add an array that holds the ARGB-Pixels of the originally loaded image
	private int[] origARGB;
	private int[] dstARGB;
	// TODO: add a contrast slider
	private JSlider contrastSlider;			//contrast Slider

	private JLabel statusLine;				// to print some status text

	/**
	 * Constructor. Constructs the layout of the GUI components and loads the initial image.
	 */
	public ImageAnalysis() {
		super(new BorderLayout(border, border));

		// load the default image
		File input = new File(initialFilename);

		if(!input.canRead()) input = openFile(); // file not found, choose another image

		imgView = new ImageView(input);
		imgView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// TODO: set the histogram array of histView and statsView
		histoView.setHistogram(new int[256]);
		// TODO: initialize the original ARGB-Pixel array from the loaded image
		height = imgView.getImgHeight();
		width = imgView.getImgWidth();
		origARGB = new int[width*height];
		dstARGB = new int[width*height];
		dstARGB = imgView.getPixels();
		origARGB = dstARGB.clone();


		// load image button
		JButton load = new JButton("Open Image");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File input = openFile();
				if(input != null) {
					imgView.loadImage(input);
					imgView.setMaxSize(new Dimension(maxWidth, maxHeight));

					height = imgView.getImgHeight();
					width = imgView.getImgWidth();
					// TODO: initialize the original ARGB-Pixel array from the newly loaded image
					origARGB = new int[width*height];
					dstARGB = new int[width*height];
					dstARGB = (int[])imgView.getPixels();
					origARGB = dstARGB.clone();
					frame.pack();
					processImage();
				}
			}        	
		});

		JButton reset = new JButton("Reset Slider");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				brightnessSlider.setValue(0);
				contrastSlider.setValue(100);
				// TODO: reset contrast slider
				processImage();
			}        	
		});

		JButton autoContrast = new JButton("Auto Contrast");
		autoContrast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoContrast();
			}        	
		});

		// some status text
		statusLine = new JLabel(" ");

		// top view controls
		JPanel topControls = new JPanel(new GridBagLayout());
		topControls.add(load);
		topControls.add(reset);
		topControls.add(autoContrast);

		// center view
		JPanel centerControls = new JPanel();
		JPanel rightControls = new JPanel();
		rightControls.setLayout(new BoxLayout(rightControls, BoxLayout.Y_AXIS));
		centerControls.add(imgView);
		rightControls.add(histoView);
		rightControls.add(statsView);
		centerControls.add(rightControls);

		// bottom view controls
		JPanel botControls = new JPanel();
		botControls.setLayout(new BoxLayout(botControls, BoxLayout.Y_AXIS));

		// brightness slider
		brightnessSlider = new JSlider(-graySteps, graySteps, 0);
		int brightness = brightnessSlider.getValue();
		String brightnessText = "Brightness: " +brightness;
		TitledBorder titBorder = BorderFactory.createTitledBorder(brightnessText);
		titBorder.setTitleColor(Color.GRAY);
		brightnessSlider.setBorder(titBorder);
		brightnessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int brightness = brightnessSlider.getValue();
				String brightnessText = "Brightness: " +brightness;
				TitledBorder titBorder = BorderFactory.createTitledBorder(brightnessText);
				brightnessSlider.setBorder(titBorder);
				processImage();
			}       	
		});

		// TODO: setup contrast slider
		contrastSlider = new JSlider(0, 1000, 100);
		double contrast = contrastSlider.getValue()*1.0/100;
		String contrastText = "Contrast: " +contrast;
		TitledBorder titBorderContrast = BorderFactory.createTitledBorder(contrastText);
		titBorder.setTitleColor(Color.BLACK);
		contrastSlider.setBorder(titBorderContrast);
		contrastSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double contrast = contrastSlider.getValue()*1.0/100;
				String contrastText = "Contrast: " +contrast;
				TitledBorder titBorderContrast = BorderFactory.createTitledBorder(contrastText);
				contrastSlider.setBorder(titBorderContrast);
				processImage();
			}        	
		});

		botControls.add(brightnessSlider);
		statusLine.setAlignmentX(Component.CENTER_ALIGNMENT);
		botControls.add(statusLine);
		botControls.add(contrastSlider);

		// add to main panel
		add(topControls, BorderLayout.NORTH);
		add(centerControls, BorderLayout.CENTER);
		add(botControls, BorderLayout.SOUTH);

		// add border to main panel
		setBorder(BorderFactory.createEmptyBorder(border,border,border,border));

		// perform the initial rotation
		processImage();
	}


	/**
	 * Set up and show the main frame.
	 */
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame("Image Analysis - " + author);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JComponent contentPane = new ImageAnalysis();
		contentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(contentPane);

		// display the window
		frame.pack();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	/**
	 * Main method. 
	 * @param args - ignored. No arguments are used by this application.
	 */
	public static void main(String[] args) {
		// schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}


	/**
	 * Open file dialog used to select a new image.
	 * @return The selected file object or null on cancel.
	 */
	private File openFile() {
		// file open dialog
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(openPath);
		int ret = chooser.showOpenDialog(this);
		if(ret == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
		return null;		
	}



	/**
	 * Update image with new brightness and contrast values.
	 * Update histogram, histogram view and statistics view.
	 */
	protected void processImage() {
		Arrays.fill(histogram, 0);
		long startTime = System.currentTimeMillis();

		// TODO: add your processing code here
		imgView.setPixels(origARGB.clone());

		brightnessChanged();
		contrastChanged();	

		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int pos = y*width+x;
				int grayValue = (dstARGB[pos] & 0xFF);
				histogram[grayValue]++;
			}
		}	

		histoView.setHistogram(histogram);
		statsView.setDimension(width, height);
		statsView.setHistogram(histogram);

		imgView.applyChanges();
		histoView.update();
		statsView.update();

		// show processing time
		long time = System.currentTimeMillis() - startTime;
		statusLine.setText("Processing time = " + time + " ms.");
	}

	private void contrastChanged() {	
		double contrastFactor = contrastSlider.getValue()*1.0/100;
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int pos = y*width+x;
				int grayValue = putInRange((int)(contrastFactor*((dstARGB[pos] & 0xFF)-128)+128));
				dstARGB[pos] = (0xFF<<24) | (grayValue<<16) | (grayValue<<8) | grayValue;
			}
		}
	}

	private void brightnessChanged() {	

		int value = brightnessSlider.getValue();

		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int pos = y*width+x;
				int grayValue = putInRange((dstARGB[pos] & 0xFF)+ value);
				dstARGB[pos] = (0xFF<<24) | (grayValue<<16) | (grayValue<<8) | grayValue;
			}
		}
	}

	private int putInRange(int colorValue) {
		if(colorValue<0)colorValue = 0;
		if(colorValue>255)colorValue = 255;
		return colorValue;
	} 

	private void autoContrast(){

		int amountColorValues = calculateMax() - calculateMin();
		int newMid = amountColorValues/2 +calculateMin();
		double contrastChange = (256.0/amountColorValues*100);

		brightnessSlider.setValue(128-newMid); //histogramm in die Mitte schieben
		contrastSlider.setValue((int)contrastChange);

		processImage();
	}

	public int calculateMin(){
		int minVal = 0;
		int counter = 0;

		while(histogram[counter] < 1){
			minVal = counter+1;
			counter++;
		}
		return minVal;
	}

	public int calculateMax(){
		int maxVal = 255;
		int counter = 255;

		while(histogram[counter] < 1){
			maxVal = counter-1;
			counter--;
		}
		return maxVal;
	}
}

