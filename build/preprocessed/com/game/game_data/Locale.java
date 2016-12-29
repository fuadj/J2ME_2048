package com.game.game_data;

import java.io.IOException;
import java.util.Hashtable;
import java.io.*;

public class Locale {
    private static final String L_FILE_EN = "/locale/locale_en.txt";
    private static final String L_FILE_AM = "/locale/locale_am.txt";
    
    private static final String ENCODING_ENG = "US-ASCII";
    private static final String ENCODING_AMH = "UTF-8";

	public static final String KEY_LANGUAGE = "language";

	private static final String LANG_EN = "English";
	private static final String LANG_AM = "አማርኛ";

	private static String[] supported_languages = null;
    
    private Hashtable map;		// holds our key-value pairs
    
    private static char[] p_start = {'('};
    private static char[] p_end = {')'};
    private static char[] equal = {'='};
		
	public Locale() throws IOException {
		String file;
		String encoding;

		UserData userData = UserData.getUserData();
		String current_language = (String)userData.getValueForKey(KEY_LANGUAGE);

		if (current_language.equalsIgnoreCase(LANG_EN)) {
			file = L_FILE_EN;
			encoding = ENCODING_ENG;
		} else if (current_language.equalsIgnoreCase(LANG_AM)) {
			file = L_FILE_AM;
			encoding = ENCODING_AMH;
		} else {
			throw new IOException("Unsupported Language: " + current_language);
		}

		userData.close();

		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(file);
			InputStreamReader isr = new InputStreamReader(in, encoding);
			GDBufferedReader reader = new GDBufferedReader(isr);

			map = new Hashtable();

			while (true) {
				char read = reader.readUntilDelimiter(p_start);
				if (read == GDBufferedReader.EOF) break;

				reader.readUntilDelimiter(equal);
				String key = reader.getStringInBuffer().trim().toUpperCase();

				reader.readUntilDelimiter(p_end);
				String value = reader.getStringInBuffer().trim();

				map.put(key, value);
			}

			isr.close();
		} catch (IOException ie) {
			throw ie;
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException ie) {}
		}
	}

	public String getString(String key) {
		String value = (String)map.get(key.toUpperCase());
		return value;
	}

	public static String[] getSupportedLanguages() {
		if (supported_languages == null) {
			supported_languages = new String[2];

			supported_languages[0] = LANG_EN;
			supported_languages[1] = LANG_AM;
		}

		return supported_languages;
	}

}
