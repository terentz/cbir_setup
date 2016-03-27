/*
 * Class name:    Image
 *
 * Author:        Tristan Rentz
 * Date created:  Monday, 18 April 2011, 21:14
 * Last modified: Wednesday, 11 May 2011, 11:55
 *
 * Description:   
 *
 */

//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.io.UnsupportedEncodingException;
//import java.io.IOException;
//import java.io.File;
import java.lang.Math;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Image
{	
	
    
    
    
    /* ANALYSIS */
    
    protected void analyse() {  
    	// TODO kill these off...
	    calculateColourHistogram();
		//calculateColourMoments();
		//calculateTextures();
    }
    public void printNormToCsv() {
    	String content = strId();
    	// TODO incrementally uncomment these...
    	content += normCHPrepForCsv();
    	//content += normCMPrepForCsv();
    	//content += normTexturePrepForCsv();
    	FileWriter writer = null;
    	try {
    		writer = new FileWriter(new File(strId() + ".csv"));
    		writer.write(content);
    		writer.flush();
    		writer.close();
    	} catch (IOException e) {
    		Pr.ln(e.getMessage());
    	}
    }
    public void printRawToCsv() {
    	String content = strId();
    	content += rawCHPrepForCsv();
    	FileWriter writer = null;
    	try {
    		writer = new FileWriter(new File(strId() + ".csv"));
    		writer.write(content);
    		writer.flush();
    		writer.close();
    	} catch (IOException e) {
    		Pr.ln(e.getMessage());
    	}
    }

    
    
    // 1. Colour Histogram Processing and Display
    private void initRawCH() {
    	this.rawColourHistogram = new int[COLOURS];
		for (short bin=0; bin<CHANNELS; bin++) {
			rawCH(bin, 0);
    	}
    }
    private void incrRawBin(short bin) { 
    	this.rawColourHistogram[bin]++;
    }
    public void testRawCH() {
		String chStr = "";
		for (int bin=0; bin<COLOURS; bin++) {
			chStr += (bin==0 ? 
					this.rawColourHistogram[bin] : 
					", " + this.rawColourHistogram[bin]);
		}
		Pr.ln(chStr);
	}

    private void initNormCH() { 
    	this.normalisedColourHistogram = new double[COLOURS];
    	for(short bin=0; bin<COLOURS; bin++) {
    		normCH(bin, 0.0);
    	}
    }
    public void testNormCH() {
    	String out = "";
    	for(short bin=0; bin<COLOURS; bin++) {
    		out += (bin==0 ? normCH(bin) : ", " + normCH(bin));
    	}
    	Pr.ln(out);
    }

    /**
     * Image.calcMValue() v1 - this is the version suggested
     * by Justin and Paul.
     * 
     * @param r The red component of the current pixel.
     * @param g The green component of the current pixel.
     * @param b The blue component of the current pixel.
     * @return An m-value.
     */
    /*
    public int calcMValue(int r, int g, int b){
        r /= 32;
        g /= 32;
        b /= 64;
        return (int)(r + g*8 + b*64);
    }
    */
    /**
     * Image.calcMValue() v2 - based on the actual formula and 
     * it's description as provided in the lecture slides. 
     * Needs further experimentation.
     * 
     * @param r The red component of the current pixel.
     * @param g The green component of the current pixel.
     * @param b The blue component of the current pixel.
     * @return An m-value.
     */
    /*
    public int calcMValue(int r, int g, int b) {
    	return r + (COLOURS * g) + (COLOURS * COLOURS * b);
    }
    */
    
    public int calcMValue(double r, double g, double b){
        r /= 32.0;
        g /= 32.0;
        b /= 64.0;
        return (int)(r + g*8 + b*64);
    }
    /*
    public int calcMValue(int r, int g, int b){
        r *= 1;
        g *= COLOURS;
        b *= COLOURS * COLOURS;
        return (int)(r+g+b);
    }
    */
    public void calcNormCH() {
    	for(int x=0; x<wd(); x++) {
    		for(int y=0; y<ht(); y++) {
    			
    			int rVal, gVal, bVal, mVal;
    			try {
    				// get the channel values at (x,y)
	    			rVal = bm().getR(x,y);
	    			gVal = bm().getG(x,y);
	    			bVal = bm().getB(x,y);
	    			
	    			// calculate the M value
	    			mVal = calcMValue(rVal, gVal, bVal);
	    			
	    			// increment the relevant bin
	    			//incrRawBin((short)mVal);
	    			// test line
	    			Pr.ln("(" + x + "," + y + ") | (" + rVal + "," + gVal + "," + bVal + ") | mValInt = " + calcMValue(rVal, gVal, bVal));


	    			
    			} catch (Exception e) {
    				Pr.ln(e.getMessage());
    			}
    		}
    	}
    	
    	// now transfer to normalised colour histogram
    	for(short bin=0; bin<COLOURS; bin++) {
    		normCH(bin, (double)rawCH(bin)/sz());
    	}
    }
    protected void calculateColourHistogram() {
    	int size = sz();
    	for(short bin=0; bin<COLOURS; bin++) {
    		int mVal = calcMValue(this.rawColourHistogram[bin],
    								this.rawColourHistogram[bin],
    								this.rawColourHistogram[bin]);
    		Pr.ln("mVal = " + mVal);
    		this.normalisedColourHistogram[(int)mVal]++;
    	}
    }
    public String colourHistogramsToString() {
    	return ""; // TODO complete this function.
    }
    public String rawCHPrepForCsv() { 
    	String out = "";
    	for (int ch=0; ch<3; ch++) {
	    	for (int bin=0; bin<COLOURS; bin++) {
	    		out += ", " + rawCH((short)bin, (short)ch);
	    	}
	    	out += "\n";
    	}
	    return out;
    }
    public String normCHPrepForCsv() { 
    	String out = "";
    	for (int bin=0; bin<COLOURS; bin++) {
    		out += ", " + normCH((short)bin);
    	}
    	return out;
    }

    
    
        
    /* CONTRUCTORS */
    
    public Image(Bitmap bm, String filename) {
    }
    public Image(String filename) {
    	try{
    		this.bitmap = new Bitmap(filename);
    	} catch (Exception e) { Pr.ln(e.getMessage()); }
    	setFileInfo(bm(), filename);
    	initRawCH();
    	initNormCH();
    	
    	//initArrays();
    	//analyse();
    }
  
    /* FILE INFO */
    
    protected void setFileInfo(Bitmap bm, String filename) {
    	bm(bm);
    	name(filename);
    	strId(filename.split("\\.")[0].split("/")[1]);
    	intId(Integer.parseInt(stringId));
    	wd(this.bitmap.getWidth());
    	ht(this.bitmap.getHeight());
    	sz(wd()*ht());
    }
    public String fileInfoToString() {
    	return "Filename: " + name() +
    			"\nString ID: " + strId() +
    			"\nInt ID: " + intId() +
    			"\nWidth: " + wd() +
    			"\nHeight: " + ht() +
    			"\nSize: " + sz();
    }
    
    
    /* INIT ARRAYS */
    
    protected void initArrays() {
    	initRawCH();
    	initNormCH();
    }

    
    /* ATTRIBUTES */

	// num bins in Colour Histogram
    public static final short COLOURS = 256;
    public static final short CHANNELS = 3;
    // file info
    private Bitmap bitmap;
    private String filename;
    private int intId;
    private String stringId;
    // dimensions
    private int width;
    private int height;
    private int size;
    
    // IMAGE FEATURES 
    // 1. Colour Histograms 
    private int[] rawColourHistogram;
    private double[] normalisedColourHistogram;

    
    /* ATTRIBUTE GET's AND SET's */
    // Resource and Header Info 
    private Bitmap bm() { return this.bitmap; }
    private void bm(Bitmap bmp) { this.bitmap = bmp; }
    private String name() { return this.filename; }
    private void name(String name) { this.filename = name; }
    private String strId() { return this.stringId; }
    private void strId(String id) { this.stringId = id; }
    private int intId() { return this.intId; }
    private void intId(int id) { this.intId = id; }
    private int wd() { return this.width; }
    private void wd(int value) { this.width = value; }
    private int ht() { return this.height; }
    private void ht(int value) { this.height = value; }
    private int sz() { return this.size; }
    private void sz(int value) { this.size = value; }
    // Feature 1. Colour Histograms
    private int[] rawCH() { return this.rawColourHistogram; }
    private int rawCH(short bin) { return this.rawColourHistogram[bin]; }
    private boolean rawCH(short bin, int value) { 
    	if ( bin > -1 && bin < COLOURS) {
    		this.rawColourHistogram[bin] = value; 
    		return true;
    	} else return false;
    }
    private double[] normCH() { return this.normalisedColourHistogram; }
    private double normCH(short cell) { return this.normalisedColourHistogram[cell]; }
    private boolean normCH(short cell, double value) { 
    	if ( cell > -1 && cell < 256 ) {
    		this.normalisedColourHistogram[cell] = value; 
    		return true;
    	} else return false;
    }


}

