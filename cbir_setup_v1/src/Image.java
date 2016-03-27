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
//import java.io.FileWriter;

public class Image
{
    public static final short COLOURS = 256;

    private int id;
    private String name;
    // dimentions
    private int wd;
    private int ht;
    private int sz;
    
    /* IMAGE FEATURES */
    // normalized colour histogram 
    private int[] uCH;
    private double[] nCH;
    // covariance matrix
    private double[][] cvm;
    // homogeneity
    private double uHom;
    private double uCon;
    private double nHom;
    private double nCon;
    // colour moments
    // channel means
    private double uRmean;
    private double uGmean;
    private double uBmean;
    private double nRmean;
    private double nGmean;
    private double nBmean;
    // standard deviations
    private double uRsd;
    private double uGsd;
    private double uBsd;
    private double nRsd;
    private double nGsd;
    private double nBsd;
    // channel skewness
    private double uRskew;
    private double uGskew;
    private double uBskew;
    private double nRskew;
    private double nGskew;
    private double nBskew;

    /**
     * Constructor takes a bitmap object and executes 
     * all analysis required to build normalized histogram.
     * @param bm the bitmap object to analyze
     * @param filename the bitmap's filename from which to extract the id
     */
    public Image(Bitmap bm, String filename){
        setID(filename);
        setDimensions(bm);
		initCH();
        initCVM();
        analyze(bm);
    }

    /**
     * Default constructor. Programmer complements this using void init(Bitmap, String).
     */
/*    
    public Image(){}
*/
    /**
     * Method that assists/follows the default constructor.
     * @param bm the bitmap object to analyze
     * @param id the id to assign to the image
     */
/*    
    public void init(Bitmap bm, String filename){
        setID(filename);
        setDimensions(bm);
        initCH();
        initCVM();
        analyze(bm);
    }
*/
/*
    // get methods
    public int getID(){ return this.id; }
    public int getHeight(){ return this.ht; }
    public int getWidth(){ return this.wd; }
    public int getSize(){ return this.sz; }
    public double[] getCH(){ return this.CH; }
    public double[][] getCVM(){ return this.cvm; }
    public double getHomogeneity(){ return this.hom; }
    public double getContrast(){ return this.con; }
    public double getRedMean(){ return this.rAv; }
    public double getGreenMean(){ return this.gAv; }
    public double getBlueMean(){ return this.bAv; }
    public double getRedStdDev(){ return this.rSD; }
    public double getGreenStdDev(){ return this.gSD; }
    public double getBlueStdDev(){ return this.bSD; }
    public double getRedSkewness(){ return this.rSkew; }
    public double getGreenSkewness(){ return this.gSkew; }
    public double getBlueSkewness(){ return this.bSkew; }
*/
	/**
	 * 1. Create the colour histogram
	 * @param bm The bitmap object to analyse.
	 */
	private void makeColourHistogram(Bitmap bm){
		// make the histogram
		for(int x=0; x<wd; ++x){
			for(int y=0; y<ht; ++y){
				try{
					int m = calcMValue(bm.getR(x,y),bm.getG(x,y),bm.getB(x,y));
					this.uCH[m]++;
				}
				catch(Exception e){
					Pr.ln(e.getMessage());
				}
			}
		}
		// normalize it!
		for(int c=0; c<COLOURS; ++c){
			this.nCH[c] = (double)this.uCH[c] / (double)this.sz;
		}
	}
    /**
     * Calculates an M value given an RGB triplet.
     * @param r The red component of the RGB triplet.
     * @param g The green component of the RGB triplet.
     * @param b The blue component of the RGB triplet.
     */
    private static int calcMValue(int r, int g, int b){
        r /= 32;
        g /= 32;
        b /= 64;
        return (int)(r + g*8 + b*64);
    }
	
	/**
	 * 2. Create colour moments
	 * @param bm The bitmap object to analyse.
	 */
	private void makeColourMoments(Bitmap bm){
	    // central moments
        calculateChannelMeans(bm);
        calculateStdDevAndSkewness(bm);	
        
        normalizeMeans();
        normalizeStdDevs();
        normalizeSkewness();

//        normalizeRed();
//        normalizeGreen();
//        normalizeBlue();

        return;
	}
	/**
	 * 2a. Calculate the average of each colour channel.
	 * @param bm The bitmap object to analyse.
	 */
	private void calculateChannelMeans(Bitmap bm){
        // declare & init var's channel accumulations
        double rAcc=0.0, gAcc=0.0, bAcc=0.0;
		
        // iterate thru pixels to 
        for(int x=0; x<wd; ++x){
            for(int y=0; y<ht; ++y){
                // extract channels and accumulate
                try{
                    rAcc += (double)bm.getR(x,y);
                    gAcc += (double)bm.getG(x,y);
                    bAcc += (double)bm.getB(x,y);
                }catch(Exception e){
                    Pr.ln("Coordinate error! Aborting...");
                    System.exit(-1);
                }
            }   // ENDFOR inner
        }   // ENDFOR outer
        // average channels
        this.uRmean = rAcc / (double)sz;
        this.uGmean = gAcc / (double)sz;
        this.uBmean = bAcc / (double)sz;

        return;
	}
	/**
	 * 2b&c. Calculate the Standard Deviations and Skewnesses.
	 * @param bm The bitmap object to analyse.
	 */
	private void calculateStdDevAndSkewness(Bitmap bm){
		// cumulative values for Standard Deviation
		double rSDAcc=0.0, gSDAcc=0.0, bSDAcc=0.0;
		// cumulative values for Skewness
		double rSkew=0.0, gSkew=0.0, bSkew=0.0;
		for(int x=0; x<wd; ++x){
            for(int y=0; y<ht; ++y){
                // extract channels and accumulate
                try{
                    // TODO establish whether or not the following
                    // operations should be on initial or 
                    // normalized means
                    double pixOffR = (double)bm.getR(x,y)-this.uRmean;
                    double pixOffG = (double)bm.getG(x,y)-this.uGmean;
                    double pixOffB = (double)bm.getB(x,y)-this.uBmean;
					// accumulate StdDev values
					rSDAcc += Math.pow(pixOffR,2.0f);
					gSDAcc += Math.pow(pixOffG,2.0f);
					bSDAcc += Math.pow(pixOffB,2.0f);
					// accumulate Skewness values
					rSkew += Math.pow(pixOffR,3.0f);
					gSkew += Math.pow(pixOffG,3.0f);
					bSkew += Math.pow(pixOffB,3.0f);
                }catch(Exception e){
                    Pr.ln("Coordinate error! Aborting...");
                    System.exit(-1);
                }
			}
		}
		// assign Standard Deviation values
		this.uRsd = Math.pow(((1.0d/(double)this.sz)*rSDAcc), (1.0d/2.0d));
		this.uGsd = Math.pow(((1.0d/(double)this.sz)*gSDAcc), (1.0d/2.0d));
		this.uBsd = Math.pow(((1.0d/(double)this.sz)*rSDAcc), (1.0d/2.0d));
		// assign Colour Skewness values
		this.uRskew = Math.pow(((1.0d/(double)this.sz)*rSDAcc), (1.0d/3.0d));
		this.uGskew = Math.pow(((1.0d/(double)this.sz)*gSDAcc), (1.0d/3.0d));
		this.uBskew = Math.pow(((1.0d/(double)this.sz)*rSDAcc), (1.0d/3.0d));
	
		return;
 	}

    // normlize by moments
    private void normalizeMeans(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uRmean, 2.0d);
        denom += Math.pow(this.uGmean, 2.0d);
        denom += Math.pow(this.uBmean, 2.0d);
        denom = Math.sqrt(denom);
        // calculate normalized values
        this.nRmean = this.uRmean / denom;
        this.nGmean = this.uGmean / denom;
        this.nBmean = this.uBmean / denom;

        return; 
    }
    private void normalizeStdDevs(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uRsd, 2.0d);
        denom += Math.pow(this.uGsd, 2.0d);
        denom += Math.pow(this.uBsd, 2.0d);
        denom = Math.sqrt(denom);
        // calculate normalized values
        this.nRsd = this.uRsd / denom;
        this.nGsd = this.uGsd / denom;
        this.nBsd = this.uBsd / denom;

        return;
    }
    private void normalizeSkewness(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uRskew, 2.0d);
        denom += Math.pow(this.uGskew, 2.0d);
        denom += Math.pow(this.uBskew, 2.0d);
        denom = Math.sqrt(denom);
        // calculate normalized values
        this.nRskew = this.uRskew / denom;
        this.nGskew = this.uGskew / denom;
        this.nBskew = this.uBskew / denom;

        return;
    }

    // TODO normalize by channels
    private void normalizeRed(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uRmean, 2.0d);
        denom += Math.pow(this.uRsd, 2.0d);
        denom += Math.pow(this.uRskew, 2.0d);
        // calculate normalized values
        this.nRmean = this.uRmean / denom;
        this.nRsd = this.uRsd / denom;
        this.nRskew = this.uRskew / denom;

        return;
    }
    private void normalizeGreen(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uGmean, 2.0d);
        denom += Math.pow(this.uGsd, 2.0d);
        denom += Math.pow(this.uGskew, 2.0d);
        // calculate normalized values
        this.nGmean = this.uGmean / denom;
        this.nGsd = this.uGsd / denom;
        this.nGskew = this.uGskew / denom;

        return;
    }
    private void normalizeBlue(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(this.uBmean, 2.0d);
        denom += Math.pow(this.uBsd, 2.0d);
        denom += Math.pow(this.uBskew, 2.0d);
        // calculate normalized values
        this.nBmean = this.uBmean / denom;
        this.nBsd = this.uBsd / denom;
        this.nBskew = this.uBskew / denom;

        return;
    }


	/**
	 * 3. Create texture values.
	 * @param bm The bitmap object to analyse.
	 */
	private void makeTextures(Bitmap bm){
		makeCovarianceMatrix(bm);
        calculateHomogeneity();    		
		calculateContrast();
        normalizeTextures();
        return;
	}
	/**
	 * 3a. Create Covariance Matrix
	 * @param bm The bitmap object to analyse.
	 */
	private void makeCovarianceMatrix(Bitmap bm){
        // iterate thru pixels
        for(int x=0; x<(wd-1); ++x){
            for(int y=0; y<ht; ++y){
                // declare and init individual pixel channels
		        double r=0.0, g=0.0, b=0.0;
                // extract channels
                try{
					// get right pixel channel extracts
                    double rLf = (double)bm.getR(x,y);
                    double gLf = (double)bm.getG(x,y);
                    double bLf = (double)bm.getB(x,y);
					// get left pixel channel extracts
                    double rRt = (double)bm.getR((x+1),y);
                    double gRt = (double)bm.getG((x+1),y);
                    double bRt = (double)bm.getB((x+1),y);
					// get gray levels
					int grayLf = (int)calcGrayLevel((int)rLf,(int)gLf,(int)bLf);
					int grayRt = (int)calcGrayLevel((int)rRt,(int)gRt,(int)bRt);
					// increment cvm index
					this.cvm[grayLf][grayRt]++;
                }catch(Exception e){
                    Pr.ln("Coordinate error! Aborting...");
                    System.exit(-1);
                }
            }   // ENDFOR inner
        }   // ENDFOR outer
        return;
	}
    /**
     * Calculates a gray-level value given an RGB triplet.
     * @param r The red component of the RGB triplet.
     * @param g The green component of the RGB triplet.
     * @param b The blue component of the RGB triplet.
     */
    private double calcGrayLevel(int r, int g, int b){
        return (0.299f*(double)r + 0.587f*(double)g + 0.114f*(double)b);
    }
    /**
     * 3b. Calculates the image's homogeniety.
     */
    private void calculateHomogeneity(){
        int h=0;
        for(int i=0; i<COLOURS; ++i)
            for(int j=0; j<COLOURS; ++j)
                h += Math.pow(this.cvm[i][j], 2.0d);
        this.uHom = h;
        return;
    }
    /**
     * 3c. Calculates the image's contrast.
     */
    private void calculateContrast(){
        int c=0;
        for(int i=0; i<COLOURS; ++i)
            for(int j=0; j<COLOURS; ++j)
                c += (Math.pow((i-j), 2.0d) * this.cvm[i][j]);
        this.uCon = c;
        return;
    }
    /**
     * 3d. Normalize the texture values.
     */
    private void normalizeTextures(){
        double denom = 0.0d;
        // evaluate denominator
        denom += Math.pow(uCon, 2.0);
        denom += Math.pow(uHom, 2.0);
        denom = Math.sqrt(denom);
        // calculate normalized values
        this.nCon = this.uCon / denom;
        this.nHom = this.uHom / denom;

        return;
    }

    /**
     * Main analysis method that calls all others.
     * @param bm The bitmap object to assess.
     */
    private void analyze(Bitmap bm){
		makeColourHistogram(bm);
		makeColourMoments(bm);
		makeTextures(bm);
        return;
    }
	
	/* initialisation methods */
	
    /**
     * Sets the image ID & name given an image filename.
     * @param filename The filename of the image used.
     */
    @SuppressWarnings("illegal escape character")
    private void setID(String filename){
        String[] parts = filename.split("\\.");
        String num = parts[0];
        this.name = num;
        try{
            this.id = Integer.parseInt(num);
        }catch(NumberFormatException e){
            this.id = 0;
        }
        return;
    }
    /**
     * Assigns the image's dimensions in pixels.
     * @param bm The bitmap object used to extract the dimensions.
     */
    private void setDimensions(Bitmap bm){
        this.wd = bm.getWidth();
        this.ht = bm.getHeight();
        this.sz = this.wd * this.ht;
        return;
    }
    /**
     * Initialises the Colour Histogram attribute.
     */
    private void initCH(){
        this.uCH = new int[COLOURS];
        this.nCH = new double[COLOURS];
        for(int i=0; i<COLOURS; ++i){
            this.uCH[i] = 0;
            this.nCH[i] = 0.0d;
        }
        return;
    }
    /**
     * Initialises the Covariance Matrix attribute.
     */
    private void initCVM(){
        this.cvm = new double[COLOURS][COLOURS];
        for(int x=0; x<COLOURS; ++x)
            for(int y=0; y<COLOURS; ++y)
                this.cvm[x][y] = 0.0d;
        return;
    }
	
	/* print utilities */
	
    /**
     * The most important print utility, and for that matter the only
     * one that should be used outside of testing. If loading data 
     * directly from MySQL using "load data infile...", the delimiter 
     * should be ", ". If loading data by running MySQL in batch mode,
     * the delimiter should be "\t".
     * @param delim The chosen delimiter, either ", " or "\t".
     */
    public String insertString(String delim){
        NumberFormat form = new DecimalFormat("0.000000000000");
        String str = (this.name + delim);
        for(int i=0; i<COLOURS; ++i)
            str += (form.format(this.nCH[i]) + delim);
        str += (form.format(this.nRmean) + delim);
        str += (form.format(this.nGmean) + delim);
        str += (form.format(this.nBmean) + delim);
        str += (form.format(this.nRsd) + delim);
        str += (form.format(this.nGsd) + delim);
        str += (form.format(this.nBsd) + delim);
        str += (form.format(this.nRskew) + delim);
        str += (form.format(this.nGskew) + delim);
        str += (form.format(this.nBskew) + delim);
        str += (form.format(this.nCon) + delim);
        str += (form.format(this.nHom));

        return str;
    }
    
	/**
     * Prints histogram to command line (for testing).
     */
/*    
    public void printCH(){
        for(int i=0; i<COLOURS; i++)
            System.out.println("colour " + i + " has " + this.CH[i] + " pixels");
    }
*/	
    /**
     * Writes histogram to a specified CSV file.
     * @param filename The name of the CSV file.
     */
/*    
    public void writeChToCsv(String filename){
        FileWriter out;
        try{
            out = new FileWriter(filename);
            for(double c : this.CH){
                out.append(c + ",");
            }
            out.flush();
            out.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
*/


    // print colour moments
/*
    public void printMeans(){
        Pr.ln("*** Channel Means ***");
        Pr.ln("Initial values...");
        Pr.ln("R: " + this.uRmean);
        Pr.ln("G: " + this.uGmean);
        Pr.ln("B: " + this.uBmean);
        Pr.ln("Normalized values...");
        Pr.ln("R: " + this.nRmean);
        Pr.ln("G: " + this.nGmean);
        Pr.ln("B: " + this.nBmean);
        Pr.ln("*********************\n");
    }

    public void printStdDevs(){
        Pr.ln("*** Standard Deviations ***");
        Pr.ln("Initial values...");
        Pr.ln("R: " + this.uRsd);
        Pr.ln("G: " + this.uGsd);
        Pr.ln("B: " + this.uBsd);
        Pr.ln("Normalized values...");
        Pr.ln("R: " + this.nRsd);
        Pr.ln("G: " + this.nGsd);
        Pr.ln("B: " + this.nBsd);
        Pr.ln("***************************\n");
    }

    public void printSkewness(){
        Pr.ln("**** Skewness ****");
        Pr.ln("Initial values...");
        Pr.ln("R: " + this.uRskew);
        Pr.ln("G: " + this.uGskew);
        Pr.ln("B: " + this.uBskew);
        Pr.ln("Normalized values...");
        Pr.ln("R: " + this.nRskew);
        Pr.ln("G: " + this.nGskew);
        Pr.ln("B: " + this.nBskew);
        Pr.ln("******************\n");
    }

    // print texture values

    public void printTextures(){
        Pr.ln("***** Textures *****");
        Pr.ln("Initial values...");
        Pr.ln("contrast:    " + this.uCon);
        Pr.ln("homogeniety: " + this.uHom);
        Pr.ln("Normalized values...");
        Pr.ln("contrast:    " + this.nCon);
        Pr.ln("homogeniety: " + this.nHom);
        Pr.ln("********************\n");
    }
*/    
}

