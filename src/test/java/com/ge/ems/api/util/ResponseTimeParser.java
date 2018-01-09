package com.ge.ems.api.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class ResponseTimeParser {

	public static void main(String [ ] args) {
		try{
		    PrintWriter writer = new PrintWriter("C:/temp/output.txt", "UTF-8");
		    Scanner fScn = new Scanner(new File("C:/temp/input.txt"));
		    String data;
		    while(fScn.hasNextLine()) {
		        data = fScn.nextLine();
		        if(data.contains("milliseconds"))
		        	writer.println(data);
		    }
		    writer.close();
		    fScn.close();
		} catch (Exception e) {
		   System.out.println(e.getMessage());
		}
	}
}
