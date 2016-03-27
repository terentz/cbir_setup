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

public class Setup 
{
    public static void main(String[] args)
    {
        int numParams = 7;
        String infile = "config";
        String outfile = "run.sh";
        String[] params = new String[numParams];
        try{
            Scanner fin = new Scanner(new FileInputStream(infile));
            PrintWriter fout = new PrintWriter(new FileOutputStream(outfile));
            for(int i=0; i<7; ++i)
                params[i] = paramValue(fin.nextLine(), infile, i+1);
            fout.println("#!/bin/csh");
            fout.println("javac *.java");
            fout.println("ln -s /home/student/csiiilib/ims imsdb");
            fout.println("ls -L imsdb > " + params[1]);
            fout.println("java AnalyseImages " + Integer.parseInt(params[0]));
            fout.println("java MakeDDL");
            fout.println("echo DDL generated..");
            fout.println("mysql -h " + params[4] + " --user=" + params[5] + " --password=" + params[6] + " < " + params[2]);
            fout.println("echo \"Table created..\"");
            fout.println("mysql -h " + params[4] + " --user=" + params[5] + " --password=" + params[6] + " < " + params[3]);
            fout.println("echo \"Database populated.\"");
            fout.flush();
            fout.close();
            Process proc = Runtime.getRuntime().exec("chmod 701 run.sh");
            System.exit(0);
        }
        catch(FileNotFoundException e){
            System.out.println("Unable to read file \"" + infile + "\"!");
            System.out.println("Aborting...");
            System.exit(0);
        }
        catch(IOException e){
            System.out.println("Unable to create file \"" + outfile + "\"!");
            System.out.println("Aborting...");
            System.exit(0);
        }
        catch(NumberFormatException e){
            System.out.println("First parameter in file \"" + infile + "\" must be an integer, either 1 or 2.");
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

