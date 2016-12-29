package com.game.game_data;

import java.util.*;
import java.io.*;
import javax.microedition.rms.*;

public class UserData {
	private static final String DATA = "USERDATA";
	private static final int RECORD_ID = 1;

	private static UserData instance = null;

	private Hashtable map;
	private RecordStore store;
		
	private UserData() {
		store = null;
		map = new Hashtable();

		try {
			store = RecordStore.openRecordStore(DATA, true);
			if (store.getNumRecords() == 0) return;
			
			ByteArrayInputStream bins = new ByteArrayInputStream(store.getRecord(RECORD_ID));
			DataInputStream dins = new DataInputStream(bins);

			try {
				while (true) {
					String key = dins.readUTF();
					String value = dins.readUTF();

					map.put(key, value);
				}
			} catch (EOFException eof) {

			}

			dins.close();
			bins.close();
		} catch (Exception e) {

		} finally {
		}
	}

	private boolean isStoreEmpty() {
		if (store != null) {
			try {
				return store.getNumRecords() == 0;
			} catch (RecordStoreNotOpenException rsno) { return true; }
		}
		return true;
	}

	private void openRecordStore(){
		try {
			store = RecordStore.openRecordStore(DATA, true);
		} catch (RecordStoreException rse) {

		}
	}

	/**
	 * gets the value associated with a given key
	 * @param key
	 * @return an object for the given key
	 * @throws IOException if error occurs
	 */
	public String getValueForKey(String key){
		Object value = map.get(key);
		if (value != null) return (String)value;
		return "";
	}

	/**
	 * associates the key with the given object
	 * and saves it the "database".
	 * @param key
	 * @param value
	 * @throws IOException 
	 */
	public void setValueForKey(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Should be called to commit changes to disk.
	 * @return whether flushing was completed
	 */
	public boolean flush() {
		if (store != null) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);

				Enumeration keys = map.keys();

				while (keys.hasMoreElements()) {
					String key = (String)keys.nextElement();
					String value = (String)map.get(key);

					dout.writeUTF(key);
					dout.writeUTF(value);
				}

				dout.flush();
				byte[] inByteForm = bout.toByteArray();

				try {
					store.setRecord(RECORD_ID, inByteForm, 0, inByteForm.length);
				} catch (InvalidRecordIDException ired) {
					store.addRecord(inByteForm, 0, inByteForm.length);
				}

				dout.close();
				bout.close();
			} catch(IOException ie) {
				return false;
			} catch (RecordStoreException rse) {
				return false;
			}

			return true;
		}

		return false;
	}

	/**
	 * Closes any open resources allocated by the instance.
	 */
	public void close() {
		try {
			if (store != null) store.closeRecordStore();
		} catch (RecordStoreException rse) { } 
	}

	/**
	 * Can check if the user has no previous user data and now is the first time.
	 * @return True if it is first time
	 */
	public static boolean isFirstTime() {
		UserData userData = getUserData();

		return userData.isStoreEmpty();
	}

	/**
	 * returns the only instance of the UserData
	 * @return 
	 */
	public static UserData getUserData() {
		if (instance == null) 
			instance = new UserData();
		instance.openRecordStore();
		return instance;
	}

}
