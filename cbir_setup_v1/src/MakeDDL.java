/*
 * Class name:    MakeDDL
 *
 * Author:        Tristan Rentz
 * Date created:  Wednesday, 27 April 2011, 03:03
 * Last modified: Wednesday, 11 May 2011, 10:08
 *
 * Description:   
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MakeDDL
{
    // program constants
    public static final String TABLE_NAME = "image";
    public static final String DATA_TYPE = "DOUBLE ZEROFILL NOT NULL";
    public static final String CONFIG_FILE = "config";
    public static final int NUM_PARAMS = 13;


    public static void main(String[]args)
    {
        // Declare param's...
        String[] params = new String[NUM_PARAMS];
        String exeMode  = null;
        String imgList  = null;
        String linkSrc  = null;
        String linkDst  = null;
        String dbCreate = null;
        String dbInsert = null;
        String dbHost   = null;
        String dbName   = null;
        String dbUser   = null;
        String dbPwd    = null;
        String dbTbl    = null;
        String setScr   = null;
        String resDir   = null;
        
        // Declare streams...
        Scanner fin = null;
        PrintWriter fout = null;
        
        try{
            // Open file input stream and read config file...
            fin = new Scanner(new FileInputStream(CONFIG_FILE));
            for(int i=0; i<NUM_PARAMS; ++i)
                params[i] = paramValue(fin.nextLine(), CONFIG_FILE, i+1);

            // Populate param's...
            exeMode  = params[0];
            imgList  = params[1];
            linkSrc  = params[2];
            linkDst  = params[3];
            dbCreate = params[4];
            dbInsert = params[5];
            dbHost   = params[6];
            dbName   = params[7];
            dbUser   = params[8];
            dbPwd    = params[9];
            dbTbl    = params[10];
            setScr   = params[11];
            resDir   = params[12];
        
        
            // Create file output stream...
            fout = new PrintWriter(new FileOutputStream(dbCreate));

            // Write the DDL file..
            fout.println("USE " + dbName + ";");
            fout.println("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            fout.println("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
            fout.println("(");
            fout.println("\tid\tINT(4) NOT NULL PRIMARY KEY,");
            for(int bin=0; bin<256; ++bin)
                fout.printf("\thist%03d\t%s,\n", bin, DATA_TYPE);
            fout.printf("\tr_av\t%s,\n", DATA_TYPE);
            fout.printf("\tg_av\t%s,\n", DATA_TYPE);
            fout.printf("\tb_av\t%s,\n", DATA_TYPE);
            fout.printf("\tr_sd\t%s,\n", DATA_TYPE);
            fout.printf("\tg_sd\t%s,\n", DATA_TYPE);
            fout.printf("\tb_sd\t%s,\n", DATA_TYPE);
            fout.printf("\tr_sk\t%s,\n", DATA_TYPE);
            fout.printf("\tg_sk\t%s,\n", DATA_TYPE);
            fout.printf("\tb_sk\t%s,\n", DATA_TYPE);
            fout.printf("\tcon\t%s,\n", DATA_TYPE);
            fout.printf("\thom\t%s\n", DATA_TYPE);
            fout.println(") ENGINE=InnoDB");
            fout.println("DEFAULT CHARACTER SET = utf8");
            fout.println("COLLATE = utf8_bin");
            fout.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Unable to read file \"" + CONFIG_FILE + "\"!");
            System.out.println("Aborting...");
            System.exit(0);
        }
//        catch(IOException e){
//            System.out.println("File creation failure! Aborting..");
//            System.exit(-1);
//        }
    }

    private static String paramValue(String line, String infile, int num){
        String[] parts = line.split("\\=");
        if(parts.length < 2){
            System.out.println("param no." + num + " in file \"" + infile + "\" not set!");
            System.out.println("Aborting...");
        }
        return parts[1].trim();
    }
}

