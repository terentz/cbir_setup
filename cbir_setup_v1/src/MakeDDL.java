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

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class MakeDDL
{
    // environment constants
    public static final String host = "latcs7";
    public static final String usrnm = "terentz";
    public static final String pwd = "6@phomet";
    public static final String dbName = "terentz";

    // program constants
    public static final String filename = "createImageTable.txt";
    public static final String tableName = "image";
    public static final String datatype = "DOUBLE ZEROFILL NOT NULL";
    public static final String login = "mysql -h " + host + " -u " + usrnm + " -p " + pwd;


    public static void main(String[]args)
    {
        PrintWriter out = null;
        try{
            out = new PrintWriter(new FileOutputStream(filename));
        }
        catch(FileNotFoundException e){
            System.out.println("File creation failure! Aborting..");
            System.exit(-1);
        }
        
        out.println("USE " + dbName + ";");
        out.println("DROP TABLE IF EXISTS " + tableName + ";");
        out.println("CREATE TABLE IF NOT EXISTS " + tableName);
        out.println("(");
        out.println("\tid\tINT(4) NOT NULL PRIMARY KEY,");
        for(int bin=0; bin<256; ++bin)
            out.printf("\thist%03d\t%s,\n", bin, datatype);
        out.printf("\tr_av\t%s,\n", datatype);
        out.printf("\tg_av\t%s,\n", datatype);
        out.printf("\tb_av\t%s,\n", datatype);
        out.printf("\tr_sd\t%s,\n", datatype);
        out.printf("\tg_sd\t%s,\n", datatype);
        out.printf("\tb_sd\t%s,\n", datatype);
        out.printf("\tr_sk\t%s,\n", datatype);
        out.printf("\tg_sk\t%s,\n", datatype);
        out.printf("\tb_sk\t%s,\n", datatype);
        out.printf("\tcon\t%s,\n", datatype);
        out.printf("\thom\t%s\n", datatype);
        out.println(") TYPE=InnoDB;");
        out.close();
    }
}

