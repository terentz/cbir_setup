
public class Analyse {
	public static final String BMP_LOCATION = "res/";
	
	public static void main(String[] args) {
		String[] files = { "001.bmp", "002.bmp", "003.bmp" };
		Image image = new Image(BMP_LOCATION + files[1]);
		Pr.ln("(255,255,255): " + image.calcMValue(255, 255, 255));
		Pr.ln("(200,200,200): " + image.calcMValue(200,200,200));
		Pr.ln("(100,100,100): " + image.calcMValue(100,100,100));
		Pr.ln("(70,70,70): " + image.calcMValue(70,70,70));
		Pr.ln("(40,40,40): " + image.calcMValue(40,40,40));
		Pr.ln("(20,20,20): " + image.calcMValue(20,20,20));
		
		image.calcNormCH();
		image.testRawCH();
		image.testNormCH();
		//image.calcMValue(r, g, b)
		// test raw CH init
		//image.testRawCH();
		// test norm CH init
		//image.testNormCH();
		
		//image.genNormCH();
		// test raw CH init
		//image.testNormCH();
				
		
		//Pr.ln(image.fileInfoToString());
		//Pr.ln(image.rawCHPrepForCsv());
	//	image.printRawToCsv();
		
		
		
	}
}
