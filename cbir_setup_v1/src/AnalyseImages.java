/*
 * Class name:      LoadImages
 *
 * Author:          Tristan Rentz
 * Date created:    Sunday, 08 May 2011, 12:05
 * Last modified: Wednesday, 11 May 2011, 22:59
 *
 * Description:     This class opens a text file in which is stored a list of bitmap filenames,
 *                  as well as a file to which to write image features to be loaded into a MySQL
 *                  database.  It reads an image filename, loads the specified bitmap, analyses
 *                  the bitmap, extracting its features, then writes the features to the data file.
 *                  It then discards the analysis file and moves on to the next filename in the 
 *                  original text file.
 *
 * Usage:           java LoadImages <mode>
 *                  
 *                  where..
 *
 *                  "mode"  is an integer representing and specifying the mode in which
 *                          to run the program, where
 *                                  
 *                          1 = write to file for internal MySQL execution
 *                              (using "load data infile..." command.
 *                  
 *                          2 = write to file for running MySQL in batch mode.
 *
 */

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.NumberFormatException;

public class AnalyseImages
{
    public static final String path1 = "/home/student/terentz/public_html/imsdb/";
    public static final String path2 = "./imsdb/";

    public static void main(String[] args)
    {
        // check for correct number of execution arguments
        if(args.length!=1){
            Pr.ln("Insufficient argument list! Aborting...");
            System.exit(0);
        }

        int mode = 0;
        try{
            mode = Integer.parseInt(args[0]);
            if(!(mode == 1 || mode == 2))
                throw new NumberFormatException();
        }
        catch(NumberFormatException e){
            Pr.ln("Invalid mode specified!");
            Pr.ln("Second execution argument should be either \"1\" or \"2\", where");
            Pr.ln("  1 = write to file for internal MySQL execution");
            Pr.ln("  2 = write to file for running MySQL in batch mode");
            Pr.ln("Aborting...");
            System.exit(0);
        }

        try{
            Scanner confIn = new Scanner(new FileInputStream("config"));
            String[] params = new String[7];
            for(int i=0; i<7; ++i)
                params[i] = paramValue(confIn.nextLine());
            confIn.close();
            
            // do the work!
            switch(mode){
                //            case 1: executeModeOne();
                //                break;
                case 2: executeModeTwo(params);
                        break;
            }
        }
        catch(FileNotFoundException e){
            Pr.ln("Config file not found! Aborting...");
            System.exit(0);
        }
        // end it all..
        System.exit(0);
    }

    private static String paramValue(String line){
        String[] parts = line.split("\\=");
        if(parts.length != 2){
            Pr.ln("Config parameter without value! Check config file. Aborting...");
            System.exit(0);
        }
        return parts[1].trim();
    }


    /*
       private static void executeModeOne(){
       Scanner configIn = new Scanner(new FileInputStream("config");
// configure the following variables
String tableName = "image";
String loadTableFile = "insertDataInternal.txt";

long startTime = System.currentTimeMillis();
long finishTimeInMS;
int finishTimeInSecs;
// write the fkn file!!
try{
PrintWriter writeInsert = new PrintWriter(new FileOutputStream(loadTableFile));
Scanner imageListIn = new Scanner(new File(imageListFile));
Pr.ln("Reading " + imageListFile + "..");
    // loop thru list items
    while(imageListIn.hasNextLine()){
    String value = "";
    try{
    // set objects
    String fileName = removeStar(imageListIn.nextLine().trim());
    Bitmap bm = new Bitmap(path1 + fileName);
    Image image = new Image(bm, fileName);
    // create the line of data
    value = image.insertString("\t");
    // write it and flush
    writeInsert.println(value);
    Pr.ln(fileName + " done.");
    writeInsert.flush();
    }
    catch(Exception e){
    Pr.ln("Problem creating Bitmap or Image object!");
    }
    }
    writeInsert.close();
    }
    catch(FileNotFoundException e){
    Pr.ln("Cannot open/locate " + imageListFile + "! Aborting..");
    System.exit(0);
    }
    catch(IOException e){
    Pr.ln("Problem opening " + loadTableFile + "! Aborting...");
    System.exit(0);
    }
    finishTimeInMS = System.currentTimeMillis() - startTime;
    finishTimeInSecs = (int)(finishTimeInMS/1000);
    Pr.ln(loadTableFile + " took " + finishTimeInSecs + " seconds to load.");

    }
    */

    private static void executeModeTwo(String[] params){
        String tableName = "image";
        String loadTableFile = params[3];
        String imageListFile = params[1];

        long startTime = System.currentTimeMillis();
        long finishTimeInMS;
        int finishTimeInSecs;
        // write the fkn file!!
        try{
            PrintWriter writeInsert = new PrintWriter(new FileOutputStream(loadTableFile));
            Scanner imageListIn = new Scanner(new File(imageListFile));
            Pr.ln("Reading " + imageListFile + "..");
            String useStatement = "USE terentz;\n";
            writeInsert.print(useStatement);
            String insertHeader = "INSERT INTO " + tableName + "\nVALUES\n(";
            String insertTail = ");";
            // loop thru list items
            while(imageListIn.hasNextLine()){
                String value = "";
                try{
                    // set objects
                    String fileName = removeStar(imageListIn.nextLine().trim());
                    Bitmap bm = new Bitmap(path1 + fileName);
                    Image image = new Image(bm, fileName);
                    // create the insert statement
                    value = insertHeader + image.insertString(", ") + insertTail;
//                    if(imageListIn.hasNextLine())
//                        value = addBraces(value);
//                    else
//                        value = addBracesEnd(value);
                    // write it and flush
                    writeInsert.println(value);
                    Pr.ln(fileName + " done.");
                    writeInsert.flush();
                }
                catch(Exception e){
                    Pr.ln("Problem creating Bitmap or Image object!");
                }
            }
            writeInsert.close();
        }
        catch(FileNotFoundException e){
            Pr.ln("Cannot open/locate " + imageListFile + "! Aborting..");
            System.exit(0);
        }
        catch(IOException e){
            Pr.ln("Problem opening " + loadTableFile + "! Aborting...");
            System.exit(0);
        }
        finishTimeInMS = System.currentTimeMillis() - startTime;
        finishTimeInSecs = (int)(finishTimeInMS/1000);
        Pr.ln("Analysis completed in " + finishTimeInSecs + " seconds.");

    }

    private static String removeStar(String str){
        if(str.charAt(str.length()-1) == '*')
            return str.substring(0,str.length()-1);
        return str;
    }
/*
    private static String addBraces(String str){
        return "(" + str + "),";
    }
    private static String addBracesEnd(String str){
        return "(" + str + "));";
    }
*/
}

