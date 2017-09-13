package com.qlik_websocket_client.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



public class WriteCSV {
	//static String FILE_HEADER = "Stock Id,Side,Company,Quantity";
	public static void writeCSV(StringBuilder headers,List<List<String>> serverResponseList) {
		// open file input stream
		BufferedWriter writer = null;
		FileWriter fwriter = null; 
		String FILE_HEADER = headers.toString().substring(1);
		try {
						System.out.println("Generating the CSV file...........");
						fwriter = new FileWriter("E:/Github3/Output.csv");
						writer = new BufferedWriter(fwriter);
						writer.write(FILE_HEADER.toString());
						writer.write("\n");
						for(List<String> qTextList : serverResponseList) {
								for(int j = 0; j < qTextList.size(); j++) {
									writer.write(qTextList.get(j).replaceAll(",","" ));
									if(j != qTextList.size() - 1) 	
											writer.write(",");
								}	
								writer.write("\n");
						}						
		} catch(Exception e) {
			   			e.printStackTrace();
		} finally {
				try {
					if(writer != null)
						writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public static String generateHTML(StringBuilder headers,List<List<String>> serverResponseList) {
		StringBuilder strHTML= new StringBuilder();
		
		strHTML.append("<table style='border:1px solid #ccc'><thead style='border:1px solid #ccc'><tr>");
		String arrStr[] = headers.toString().split(",");
		for(int i=1;i<arrStr.length;i++) {
			strHTML.append("<th style='border:1px solid #ccc'>");
			strHTML.append(arrStr[i]);
			strHTML.append("</th>");
		}	
		strHTML.append("</tr></thead>");
		strHTML.append("<tbody style='border:1px solid #ccc'>");
		for(List<String> qTextList : serverResponseList) {
			strHTML.append("<tr>");
			for(int j = 0; j < qTextList.size(); j++) {
				strHTML.append("<td style='border:1px solid #ccc'>");
				strHTML.append(qTextList.get(j));
				strHTML.append("</td>");
			}
			strHTML.append("</tr>");
		}						
		strHTML.append("</tbody></table>");
		
		return strHTML.toString();
	}

}