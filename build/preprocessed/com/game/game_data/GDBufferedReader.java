package com.game.game_data;

import java.io.*;

public class GDBufferedReader {
	public static final char EOF = (char ) -1;

	private static char[] buffer = new char[400];		// to hold temp data
	private int length;		// length of data read sofar

	private InputStreamReader inputStreamReader;

	public GDBufferedReader(InputStreamReader isr) {
		inputStreamReader = isr;
		length = 0;
	}

	public char readUntilDelimiter(char delimiters[]) throws IOException {
		length = 0;

		char val;
		while ((val = (char)inputStreamReader.read()) != EOF) {
			for (int i = 0; i < delimiters.length; i++) {		// check to see if it is one of the delimiting characters
				if (delimiters[i] == val) {
					return delimiters[i];
				}
			}

			buffer[length++] = val;

			if (length == buffer.length) break;		// our buffer is full!, oops
		}

		return val;
	}

	public String getStringInBuffer() {
		return new String(buffer, 0, length);
	}
}