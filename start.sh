#!/bin/sh
	while true
	do
		clear
		java -Xms1000M -Xmx1800M -jar R_Parser.jar
		echo "Waiting 24 hours..."‬
		sleep 1d
		for i in 5 4 3 2 1
		do
			echo -ne "."
			sleep 1
		done
		clear
	done
