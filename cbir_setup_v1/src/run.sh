#!/bin/csh
javac *.java
ln -s /home/student/csiiilib/ims imsdb
ls -L imsdb > imageList.txt
java AnalyseImages 2
java MakeDDL
echo DDL generated..
mysql -h latcs7 --user=terentz --password=6@phomet < createImageTable.txt
echo "Table created.."
mysql -h latcs7 --user=terentz --password=6@phomet < insertDataExternal.txt
echo "Database populated."
