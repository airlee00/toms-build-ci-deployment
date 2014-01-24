package com.toms.ftp.test;

public class Test {

	private static String remoteFileSep = "/";
	   protected static String resolveFile(String file) {
	        return file.replace(System.getProperty("file.separator").charAt(0),
	                            remoteFileSep.charAt(0));
	    }
	   
	public static void main(String[] args) {
		String file="d:\\dddd\\aaa.txt";
		System.out.println(resolveFile(file));
	}
}
