echo "file;sizePI;sizeI;linesPI;linesI">isize.csv
cat linux_files.lst | while read i; do 
	echo "$i";
	echo  "linux-2.6.33.3/$i;"\
		`cat "linux-2.6.33.3/$i.pi"|wc -c`";"\
		`cat "linux-2.6.33.3/$i.i"|wc -c`";"\
		`cat "linux-2.6.33.3/$i.pi" | grep -v ^$ | grep "^#" -v|wc -l`";"\
		`cat "linux-2.6.33.3/$i.i" | grep -v ^$ | grep "^#" -v|wc -l`>>isize.csv; 
done

