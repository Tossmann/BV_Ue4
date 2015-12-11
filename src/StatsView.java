// BV Ue04 WS2015/16 Vorgabe Hilfsklasse StatsView
//
// Copyright (C) 2014 by Klaus Jung

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class StatsView extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String[] names = { "Minimum:", "Maximum:", "Average:", "Median:" , "Varianz:", "Entropy:" }; // TODO: enter proper names
	private static final int rows = names.length;
	private static final int border = 2;
	private static final int columns = 2;
	private static final int graySteps = 256;
	public int width = 0;
	public int height = 0;

	private JLabel[] infoLabel = new JLabel[rows];
	private JLabel[] valueLabel = new JLabel[rows];

	private int[] histogram = null;

	public StatsView() {
		super(new GridLayout(rows, columns, border, border));
		TitledBorder titBorder = BorderFactory.createTitledBorder("Statistics");
		titBorder.setTitleColor(Color.GRAY);
		setBorder(titBorder);
		for(int i = 0; i < rows; i++) {
			infoLabel[i] = new JLabel(names[i]);
			valueLabel[i] = new JLabel("-----");
			add(infoLabel[i]);
			add(valueLabel[i]);
		}
	}

	private void setValue(int column, int value) {
		valueLabel[column].setText("" + value);
	}

	private void setValue(int column, double value) {
		valueLabel[column].setText(String.format(Locale.US, "%.2f", value));
	}

	public boolean setHistogram(int[] histogram) {
		if(histogram == null || histogram.length != graySteps) {
			return false;
		}
		this.histogram = histogram;

		update();
		return true;
	}

	public void setDimension(int width, int heigth){
		this.width = width;
		this.height = heigth;
	}

	public boolean update() {
		if(histogram == null) {
			return false;
		}
		// TODO: calculate and display statistic values
		setValue(0, calculateMin());
		setValue(1, calculateMax());
		double ave = calculateAve();
		setValue(2, ave);
		setValue(3, calculateMed());
		setValue(4, calculateVar(ave));
		setValue(5, calculateEnt());
		return true;
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

	public double calculateAve(){
		double ave = 0;

		for(int i = 0; i <histogram.length;i++){
			ave += i*histogram[i];
		}

		return ave / (width*height);
	}

	public int calculateMed(){
		int position = 0;
		int counter = 0;

		while(position < (width*height)/2){
			position += histogram[counter];
			counter++;
		}
		return counter-1;
	}

	public double calculateVar(double ave){
		double var = 0;
		double pixelAmount = width*height;

		for(int i = 0; i < histogram.length;i++){
			var += (histogram[i]/pixelAmount)*Math.pow((i-ave),2);
		}
		return var;
	}

	public double calculateEnt(){
		double[] probability = new double[countEntropie()];
		double entropy = 0.0;
		int counter = 0;

		for(int i = 0; i < histogram.length;i++){
			if(histogram[i] > 0){
				probability[counter] = getProbability(i);
				entropy += (probability[counter])*((double)((histogram[i])/(double)(width*height)));
				counter++;
			}
		}
		return entropy;
	}

	public int countEntropie(){
		int counter = 0;

		for(int i = 0; i <histogram.length;i++){
			if(histogram[i] > 0)
				counter++;
		}
		return counter;
	}

	public double getProbability(int index){
		return -((Math.log10(((double)histogram[index])/(width*height)))/Math.log10(2.0));
	}
}
