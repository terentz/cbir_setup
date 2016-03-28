/*
 * Class name:    Config
 *
 * Author:        Tristan Rentz
 * Date created:  Wednesday, 11 May 2011, 19:02
 * Last modified: Wednesday, 11 May 2011, 23:02
 *
 * Description:   
 *
 */

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class Setup {
    
    public static final int NUM_PARAMS = 13;
    public static final String CONFIG_FILE = "config";
    public static final String SETUP_SCRIPT = "run.sh";

    public static void main(String[] args) {
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
        
        // Declare streams..
        Scanner fin = null;
        PrintWriter fout = null;
        
        try {
            // Open file stream and read config file...
            fin = new Scanner(new FileInputStream(CONFIG_FILE));
            for(int i=0; i<NUM_PARAMS; ++i)
                params[i] = paramValue(fin.nextLine(), CONFIG_FILE, i+1);
            
            // Populate the param's
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
            
            // Create 'run.sh'...
            fout = new PrintWriter(new FileOutputStream(setScr));
            fout.println("#!/bin/bash");
            fout.println("javac *.java");
            fout.println("ls -L " + linkSrc + " > " + imgList);
            fout.println("ln -s " + linkSrc + " " + linkDst);
            fout.println("chmod 555 " + linkDst);
            fout.println("sudo chown www-data:www-data " + linkDst);
            fout.println("java AnalyseImages " + Integer.parseInt(exeMode));
            fout.println("java MakeDDL");
            fout.println("echo DDL generated..");
            //System.exit(1);
            fout.println("mysql -h " + dbHost + " -u " + dbUser + " -p" + dbPwd + " " + dbName + " < " + dbCreate);
            fout.println("echo \"Table created..\"");
            fout.println("mysql -h " + dbHost + " -u " + dbUser + " -p" + dbPwd + " " + dbName + " < " + dbInsert);
            fout.println("echo \"Database populated.\"");
            fout.flush();
            fout.close();
            Process proc = Runtime.getRuntime().exec("chmod 701 " + setScr);
            System.exit(1);
        }
        catch(FileNotFoundException e){
            System.out.println("Unable to read file \"" + CONFIG_FILE + "\"!");
            System.out.println("Aborting...");
            System.exit(0);
        }
        catch(IOException e){
            System.out.println("Unable to create file \"" + setScr + "\"!");
            System.out.println("Aborting...");
            System.exit(0);
        }
        catch(NumberFormatException e){
            System.out.println("First parameter in file \"" + CONFIG_FILE + "\" must be an integer, either 1 or 2.");
            System.out.println("Aborting...");
            System.exit(0);
        }
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

