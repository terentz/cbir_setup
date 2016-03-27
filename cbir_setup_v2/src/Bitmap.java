/* This program is designed by Xiaohang (Alex) Ma
   x4ma@cs.latrobe.edu.au
   Converted to Java by: Zhen He
   z.he@latrobe.edu.au
*/

import java.util.*;
import java.awt.Color; 
import java.io.*;

public class Bitmap 
{
    
    private class RGBQUAD { // rgbq 
	public int  rgbBlue; 
	public int  rgbGreen; 
	public int  rgbRed; 
	public int  rgbReserved; 
    };
    
    private class  BITMAPFILEHEADER{  // bmfh 
		public  byte []  bfType; 
		public	byte []  bfSize; 
		public	byte []  bfReserved1; 
		public	byte []  bfReserved2;
		public	byte []  bfOffBits; 

		public BITMAPFILEHEADER() {
			bfType = new byte[2]; 
			bfSize = new byte[4]; 
			bfReserved1 = new byte[2]; 
			bfReserved2 = new byte[2];
			bfOffBits = new byte[4]; 
		}
    };
    
    private class BITMAPINFOHEADER { // bmih 
		int  biSize; 
		int  biWidth; 
		int  biHeight; 
		int  biPlanes; 
		int  biBitCount; 
		int  biCompression; 
		int  biSizeImage; 
		int  biXPelsPerMeter; 
		int  biYPelsPerMeter; 
		int  biClrUsed; 
		int  biClrImportant; 
    }; 
    
	// attributes
    private int [] BmpData;		//Bmp data
    private int bytPadding;		//byte padding
    private  int ImageSize;		//Size of Image
    private int Err;
    
    private BITMAPFILEHEADER BmpFileHeader ;         //Bmp file header
    private BITMAPINFOHEADER BmpInfoHeader;	     //Bmp info header
    private RGBQUAD [] BmpRGBQuad;		     //Bmp color platter
    

    // functions used by the internal implementation
    private  int RGB(int R, int G, int B) {
		return (((R*256)+G)*256+B);
    }



    public Bitmap() {
	Err = -1;
	 BmpFileHeader = new  BITMAPFILEHEADER();        
	 BmpInfoHeader = new BITMAPINFOHEADER();
    }
    public Bitmap(String FileName) throws Exception {
	Err=-1;
	BmpFileHeader = new  BITMAPFILEHEADER();        
	BmpInfoHeader = new BITMAPINFOHEADER();
	loadBmpFile(FileName);

    }

    //Load the specified bmp file
    public void loadBmpFile(String FileName) throws Exception {       
		File inFile = new File (FileName);

		FileInputStream inStream = new FileInputStream (inFile);
		DataInputStream input = new DataInputStream (inStream);
		input.read(BmpFileHeader.bfType);
		input.read(BmpFileHeader.bfSize);
		input.read(BmpFileHeader.bfReserved1);
		input.read(BmpFileHeader.bfReserved2);
		input.read(BmpFileHeader.bfOffBits);
		if ((char)BmpFileHeader.bfType[0] != 'B' && (char)BmpFileHeader.bfType[1] != 'M')
			throw new  Exception("Not a BMP file");
		BmpInfoHeader.biSize =  intelInt(input.readInt());
		BmpInfoHeader.biWidth =  intelInt(input.readInt());
		BmpInfoHeader.biHeight = intelInt(input.readInt());
		BmpInfoHeader.biPlanes = intelShort(input.readUnsignedShort());
		BmpInfoHeader.biBitCount =  intelShort(input.readUnsignedShort());
		BmpInfoHeader.biCompression =  intelInt(input.readInt()); 
		BmpInfoHeader.biSizeImage =  intelInt(input.readInt());
		BmpInfoHeader.biXPelsPerMeter =  intelInt(input.readInt());
		BmpInfoHeader.biYPelsPerMeter =  intelInt(input.readInt());
		BmpInfoHeader.biClrUsed =    intelInt(input.readInt());
		BmpInfoHeader.biClrImportant =    intelInt(input.readInt());
		if (BmpInfoHeader.biCompression != 0 )
			throw new Exception("BMP is compressed");

		switch (BmpInfoHeader.biBitCount) {
			case 1:
				BmpRGBQuad = new RGBQUAD[1];
				break;
			case 4:
				BmpRGBQuad = new RGBQUAD[15];
				break;
			case 8:
				BmpRGBQuad = new RGBQUAD[255];
				break;
		}
		//If it's less than 24bit, then get the colour data
		if (BmpInfoHeader.biBitCount < 24) {
			//Fill the colour table
			BmpRGBQuad[0].rgbBlue = input.read();
			BmpRGBQuad[0].rgbGreen = input.read();
			BmpRGBQuad[0].rgbRed = input.read();
			BmpRGBQuad[0].rgbReserved = input.read();
		}
		bytPadding = 32 - ((BmpInfoHeader.biWidth * BmpInfoHeader.biBitCount) %32);
		if (bytPadding == 32) 
			bytPadding = 0;
		bytPadding = bytPadding / 8;
		
		if (BmpInfoHeader.biBitCount == 24)
			ImageSize = (BmpInfoHeader.biWidth * 3 + bytPadding) * BmpInfoHeader.biHeight;
		else
			ImageSize = (BmpInfoHeader.biWidth + bytPadding) * BmpInfoHeader.biHeight;

		BmpData = new int[ImageSize];
		for (int i = 0; i < ImageSize; i++) {
			BmpData[i] = input.read();
		}

		input.read();
		input.close();
		inStream.close();

    }

    /**
	 * Returns the colour of the pixel defined at (x,y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the pixel colour in 24 bit format
	 */
    public int getPixelColor(int X, int Y) throws Exception {

	int lngPixelnum;
	boolean blnFirstHalf;
	int bytNum;
	int bytBitNum;
	char bytBitVal;

	if(X>BmpInfoHeader.biWidth-1 || Y >BmpInfoHeader.biHeight-1)
	    throw new  Exception("Invalid data");
	
	lngPixelnum = (BmpInfoHeader.biHeight - Y - 1) * (bytPadding + BmpInfoHeader.biWidth * 3) + X * 3;

	//Find the colour of a given pixel within an array
	//of data of given bit depth (Bits Per Pixel)
    
	//If it's a 24bit bitmap produce the colour from the 3 bits
	if (BmpInfoHeader.biBitCount == 24)
	    return RGB(BmpData[lngPixelnum + 2], BmpData[lngPixelnum + 1], BmpData[lngPixelnum]);
	//If it's 8bit, look up the colour in the table
	else 
	{
	    if (BmpInfoHeader.biBitCount == 8)
	    {
			return RGB(BmpRGBQuad[BmpData[lngPixelnum]].rgbRed, BmpRGBQuad[BmpData[lngPixelnum]].rgbGreen, BmpRGBQuad[BmpData[lngPixelnum]].rgbBlue);
		}
	    //If it's 4bit, split the byte and look up the colour in the table
	    else
	    {
			if (BmpInfoHeader.biBitCount == 4)
			{
				//Find out which half of the byte we're using
				blnFirstHalf = false;
				if (lngPixelnum % 2 == 0) blnFirstHalf = true;
				//Extract the number from that half of the byte
					if (blnFirstHalf == true)  {
						bytNum = BmpData[lngPixelnum]>>4;
					  }
					else {
						bytNum = BmpData[lngPixelnum] & 0xf;
					}
					//Return the colour from the colour table
					return RGB(BmpRGBQuad[bytNum].rgbRed, BmpRGBQuad[bytNum].rgbGreen, BmpRGBQuad[bytNum].rgbBlue);
				}
				else
				{
					//If it's 1bit, split the byte into bits and look up the colour table
					if (BmpInfoHeader.biBitCount == 1)
					{
						//Find which bit to use
						bytBitNum = lngPixelnum % 8;
						//Determine if the bit is set or not
						bytBitVal = 0;
						bytBitNum=1 << bytBitNum;
						if ((bytBitNum & BmpData[lngPixelnum / 8]) == 1) bytBitVal = 1;
						//Return the colour from the colour table
						return RGB(BmpRGBQuad[bytBitVal].rgbRed, BmpRGBQuad[bytBitVal].rgbGreen, BmpRGBQuad[bytBitVal].rgbBlue);
					}
				}
			}
		}
		return 0;
    }
    
    
    //Get the red channel value for the pixel  at (x,y)
	/**
	 * Returns the red component of the pixel at (x,y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the 8-bit value of the red component
	 */
    public int getR(int X,  int Y) throws Exception {
		int temp;
		temp=getPixelColor(X,Y);
		return (temp>>16)&0xFF;
    }
   
	/**
	 * Returns the green component of the pixel at (x,y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the 8-bit value of the green component
	 */
    public int getG(int X,  int Y) throws Exception {
		int temp;
		temp=getPixelColor(X,Y);
		return (temp>>8)&0xFF;
    }

	/**
	 * Returns the blue component of the pixel at (x,y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the 8-bit value of the blue component
	 */
    public int getB( int X,  int Y) throws Exception {
		int temp;
		temp=getPixelColor(X,Y);
		return temp&0xFF ;
    }

	 /**
	  * Return the width of the bitmap in pixels.
	  * @return the width of the bitmap in pixels
	  */
    public int getWidth() {
		return BmpInfoHeader.biWidth;
    }

	 /**
	  * Return the height of the bitmap in pixels.
	  * @return the height of the bitmap in pixels
	  */
    public int getHeight() {
		return BmpInfoHeader.biHeight;
    }

	/**
	 * Returns the number of bits used per pixel.
	 * @return the number of bits used per pixel
	 */
    public  int  getBitCount() {
		return BmpInfoHeader.biBitCount;
    }

    // function used by the internal implementation
    private static int intelShort(int i)
    {
        return ((i >> 8) & 0xff) + ((i << 8) & 0xff00);
    }


    // function used by the internal implementation
    private static int intelInt(int i)
    {
        return ((i & 0xff) << 24) + ((i & 0xff00) << 8) +
            ((i & 0xff0000) >> 8) + ((i >> 24) & 0xff);
    }
   
};
