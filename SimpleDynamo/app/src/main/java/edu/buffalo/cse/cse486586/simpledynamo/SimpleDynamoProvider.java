package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDynamoProvider extends ContentProvider {
	static final String TAG = SimpleDynamoProvider.class.getSimpleName();
	static ArrayList<String> joiningPorts = new ArrayList<String>();
	static ArrayList <String> hashPorts = new ArrayList<String>();
	static HashMap<String, String> hashMap = new HashMap<String, String>();
	//static HashMap<String, String> hashMapFinal = new HashMap<String, String>();
	static ConcurrentHashMap<String, String> hashMapFinal = new ConcurrentHashMap<String, String>();
	String coloumns[] = {"key", "value"};
	static  int TIMEOUT = 1000;
	static int count =0;
	static ConcurrentHashMap avd0Map = new ConcurrentHashMap();
	static ConcurrentHashMap avd1Map = new ConcurrentHashMap();
	static ConcurrentHashMap avd2Map = new ConcurrentHashMap();
	static ConcurrentHashMap avd3Map = new ConcurrentHashMap();
	static ConcurrentHashMap avd4Map = new ConcurrentHashMap();
	static String createPort="";

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub

		if(selection.equals("@")){

			//hashMapFinal.clear();
			for(Map.Entry entry: hashMapFinal.entrySet()){
				hashMapFinal.remove(entry.getKey());
				delReplica(entry.getKey().toString().trim());
				System.out.println("del @ logic printing hashmap "+hashMapFinal.toString());
				getContext().deleteFile(selection);
			}
		}else if(selection.equals("*")){
			System.out.println("Del called *");
			delAll();

		}else{

			System.out.println("Selection for del: "+selection);
			hashMapFinal.remove(selection);
			delReplica(selection);
			getContext().deleteFile(selection);
		}
		return 0;
	}

	public void delReplica(String selection){
		try {

			Socket sockets[] = new Socket[10];
			Socket socket;
			System.out.println("Create port: "+createPort);
			if (createPort.equals("5554")) {
				ArrayList<String> delPorts = new ArrayList<String>();
				//delPorts.add("5554");
				delPorts.add("5558");
				delPorts.add("5560");
				delPorts.add("5556");
				avd1Map.clear();
				avd4Map.clear();
				System.out.println("Cleared avds 1 and 4 "+avd1Map.toString()+" avd4 "+avd4Map.toString());
				for (int i=0; i<delPorts.size(); i++) {
					try {

						sockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(delPorts.get(i).trim()) * 2));
						socket = sockets[i];
						socket.setSoTimeout(100);
						ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
						ostream.writeObject("del5554:" + selection);
						ostream.flush();
					}catch (SocketTimeoutException e){
						System.out.println("Socket timed out "+delPorts.get(i));
					}catch (EOFException e){
						System.out.println("EOF Exception: "+delPorts.get(i));
					}
				}
			}
			else if (createPort.equals("5556")) {
				ArrayList<String> delPorts = new ArrayList<String>();
				delPorts.add("5554");
				delPorts.add("5558");
				delPorts.add("5562");
				avd3Map.clear();
				avd4Map.clear();
				System.out.println("Cleared avds 3 and 4 "+avd3Map.toString()+" avd4 "+avd4Map.toString());
				for (int i = 0; i < delPorts.size(); i++) {
					try {
						sockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(delPorts.get(i).trim()) * 2));
						socket = sockets[i];
						socket.setSoTimeout(100);
						ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
						ostream.writeObject("del5556:" + selection);
						ostream.flush();
					}catch (SocketTimeoutException e){
						System.out.println("Socket timed out "+delPorts.get(i));
					}catch (EOFException e){
						System.out.println("EOF Exception: "+delPorts.get(i));
					}

				}
			}
			else if (createPort.equals("5558")) {
				ArrayList<String> delPorts = new ArrayList<String>();
				//delPorts.add("5554");
				delPorts.add("5560");
				delPorts.add("5562");
				delPorts.add("5554");
				avd0Map.clear();
				avd1Map.clear();
				System.out.println("Cleared avds 0 and 1 "+avd0Map.toString()+" avd1 "+avd1Map.toString());
				for (int i = 0; i < delPorts.size(); i++) {
					try {
						sockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(delPorts.get(i).trim()) * 2));
						socket = sockets[i];
						socket.setSoTimeout(100);
						ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
						ostream.writeObject("del5558:" + selection);
						ostream.flush();
					}catch (SocketTimeoutException e){
						System.out.println("Socket timed out "+delPorts.get(i));
					}catch (EOFException e){
						System.out.println("EOF Exception: "+delPorts.get(i));
					}
				}
			}
			else if (createPort.equals("5560")) {
				ArrayList<String> delPorts = new ArrayList<String>();
				//delPorts.add("5554");
				delPorts.add("5562");
				delPorts.add("5556");
				delPorts.add("5558");
				avd0Map.clear();
				avd2Map.clear();
				System.out.println("Cleared avds 0 and 2 "+avd0Map.toString()+" avd2 "+avd2Map.toString());
				for (int i = 0; i < delPorts.size(); i++) {
					try {
						sockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(delPorts.get(i).trim()) * 2));
						socket = sockets[i];
						socket.setSoTimeout(100);
						ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
						ostream.writeObject("del5560:" + selection);
						ostream.flush();
					}catch (SocketTimeoutException e){
						System.out.println("Socket timed out "+delPorts.get(i));
					}catch (EOFException e){
						System.out.println("EOF Exception: "+delPorts.get(i));
					}
				}
			}
			else if (createPort.equals("5562")) {
				ArrayList<String> delPorts = new ArrayList<String>();
				//delPorts.add("5554");
				delPorts.add("5556");
				delPorts.add("5554");
				delPorts.add("5560");
				avd2Map.clear();
				avd3Map.clear();
				System.out.println("Cleared avds 2 and 3 "+avd2Map.toString()+" avd3 "+avd3Map.toString());
				for (int i = 0; i < delPorts.size(); i++) {
					try {
						sockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(delPorts.get(i).trim()) * 2));
						socket = sockets[i];
						socket.setSoTimeout(100);
						ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
						ostream.writeObject("del5562:" + selection);
						ostream.flush();
					}catch (SocketTimeoutException e){
						System.out.println("Socket timed out "+delPorts.get(i));
					}catch (EOFException e){
						System.out.println("EOF Exception: "+delPorts.get(i));
					}
				}
			}
		}
//		} catch (SocketTimeoutException e){
//			System.out.println("Forcefully stopped");
//		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public void delAll(){

		Socket socket[] = new Socket[5];
		Socket socket_;
		ObjectOutputStream os;
		ObjectInputStream istream;
		ArrayList<String> mapList = new ArrayList<String>();
	//	String str = getJoiningPorts();

		try {

			for (int i = 0; i < joiningPorts.size(); i++) {
				if(!joiningPorts.get(i).trim().equals("")){

					socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(joiningPorts.get(i).trim())*2));
					socket_ = socket[i];
					socket_.setSoTimeout(100);
					os = new ObjectOutputStream(socket_.getOutputStream());
					os.writeObject("delAll");
					// os.writeObject("QueryRequest");
					os.flush();
//                    istream = new ObjectInputStream(socket_.getInputStream());
//                    String str1 = (String) istream.readObject();
//                    mapList.add(str1);

				}
			}
		} catch (SocketTimeoutException e){
			System.out.println("Socket timeout exception in delAll");
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub

		String key = (String) values.get("key");
		String value = (String) values.get("value");

		// System.out.println("Message read: "+msgRead);
		//  String split1[]= msgRead.split(" ");
		String keyHashed1 = null;
		if(!(key.contains("fromServ") &&  value.contains("fromServ"))) {
			try {
				keyHashed1 = genHash(key);
				System.out.println("Key and its hash: "+key+" "+keyHashed1);
				String port1[] = keyStoreLogic(key,value);
				ArrayList <String> listOfSend = new ArrayList<String>();
				for(int i=0;i<port1.length;i++){
					listOfSend.add(port1[i]);
				}
				if ( !(port1==null) && !port1.equals("none")) {
					String msgRead = key + "," + value;
					//sendToStore(msgRead, port1);
					//new ClientStore().sendToStore(msgRead,port1);
					new ClientStore().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msgRead,listOfSend.toString());
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}


		else if(key.contains("fromServ") && value.contains("fromServ"))  {
			String keys[]= key.split(":");
			key = keys[0];
			String vals[]= value.split(":");



			value = vals[0];
			System.out.println("###KEY value in insert: "+key+" "+value);
			if(vals.length==4){
				System.out.println("#####It is backup and Length is 4: "+vals[2]);
				if(vals[2].trim().equals("5554")){
					System.out.println("Backup of AVD0");
					if(avd0Map.contains(key.trim())){
						avd0Map.remove(key.trim());
						avd0Map.put(key.trim(),value.trim());
					}else {
						avd0Map.put(key.trim(), value.trim());
					}
				}else if(vals[2].trim().equals("5556")){
					System.out.println("Backup of AVD1");
					if(avd1Map.contains(key.trim())){
						avd1Map.remove(key.trim());
						avd1Map.put(key.trim(),value.trim());
					}else {
						avd1Map.put(key.trim(), value.trim());
					}
				}else if(vals[2].trim().equals("5558")){
					System.out.println("Backup of AVD2");
					if(avd2Map.contains(key.trim())){
						avd2Map.remove(key.trim());
						avd2Map.put(key.trim(),value.trim());
					}else {
						avd2Map.put(key.trim(), value.trim());
					}
				}else if(vals[2].trim().equals("5560")){
					System.out.println("Backup of AVD3");
					if(avd3Map.contains(key.trim())){
						avd3Map.remove(key.trim());
						avd3Map.put(key.trim(),value.trim());
					}else {
						avd3Map.put(key.trim(), value.trim());
					}
				}else if(vals[2].trim().equals("5562")){
					System.out.println("Backup of AVD4");
					if(avd4Map.contains(key.trim())){
						avd4Map.remove(key.trim());
						avd4Map.put(key.trim(),value.trim());
					}else {
						avd4Map.put(key.trim(), value.trim());
					}
				}
			}
			System.out.println("Printing map avd0Map "+avd0Map.toString());
			System.out.println("Printing map avd1Map "+avd1Map.toString());
			System.out.println("Printing map avd2Map "+avd2Map.toString());
			System.out.println("Printing map avd3Map "+avd3Map.toString());
			System.out.println("Printing map avd4Map "+avd4Map.toString());



			String filename = key.trim();
			System.out.println("In insert of content provider");

			if(hashMapFinal.contains(key.trim())){
				hashMapFinal.remove(key.trim());
				hashMapFinal.put(key.trim(),value.trim());
				getContext().deleteFile(key.trim());
				try {
					FileOutputStream fileOutputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
					fileOutputStream.write(value.toString().getBytes());
					System.out.println("file created with the filename " + key);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				hashMapFinal.put(key.trim(), value.trim());
				try {
					FileOutputStream fileOutputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
					fileOutputStream.write(value.toString().getBytes());
					System.out.println("file created with the filename " + key);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
		Log.v("insert", values.toString());

		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub

		TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String portstr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		final String myPort = String.valueOf(Integer.parseInt(portstr));
		createPort = myPort;

		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub


		if (selection.equals("*")) {
			MatrixCursor cursor = new MatrixCursor(coloumns);

//			String str = getJoiningPorts();
//			str = str.replace("[", "");
//			str = str.replace("]", "");
//			String split[] = str.split(",");
			String split[] = new String[joiningPorts.size()];
			for(int i=0;i<split.length;i++){
				split[i]= joiningPorts.get(i);
			}
			ArrayList<String> mapList = requestingInfo (split);
			if (mapList.isEmpty()) {
				for (Map.Entry entry : hashMapFinal.entrySet()) {
					String row[] = {entry.getKey().toString().trim(), entry.getValue().toString().trim()};
				//	System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
					cursor.addRow(row);
				}
				return cursor;

			} else {
				HashMap<String, String> allHash = mapListLogic(mapList);

				System.out.println("Got * query");


				for (Map.Entry entry : allHash.entrySet()) {

					String row[] = {entry.getKey().toString().trim(), entry.getValue().toString().trim()};
					cursor.addRow(row);

				}

				return cursor;
			}

		} else if (selection.equals("@")) {
			MatrixCursor cursor = new MatrixCursor(coloumns);

			for (Map.Entry entry : hashMapFinal.entrySet()) {
				String row[] = {entry.getKey().toString().trim(), entry.getValue().toString().trim()};
				System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
				cursor.addRow(row);
			}
			return cursor;

		} else {
			MatrixCursor cursor = new MatrixCursor(coloumns);


			String [] split = new String[joiningPorts.size()];
			for (int i = 0; i < joiningPorts.size(); i++) {
				split[i] = joiningPorts.get(i);
				System.out.println("Split!! " + split[i]);
			}

			ArrayList<String> mapList = requestingInfo(split);


			if (mapList.isEmpty()) {
				String val = "";
				String key2="";
				for (Map.Entry entry : hashMapFinal.entrySet()) {
					 key2 = entry.getKey().toString();

					if (key2.trim().equals(selection)) {
						val = hashMapFinal.get(key2);
						break;

					}

				}
				String row[] = {key2.trim(), val.trim()};
				//System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
				System.out.println("Key value for single pair: " + key2 + " " + val);
				cursor.addRow(row);
				return cursor;
			} else {
				HashMap<String, String> allHash = mapListLogic(mapList);

				System.out.println("Single pair query");

				String key3="";
				String val1 = "";
				for (Map.Entry entry : allHash.entrySet()) {
					 key3 = entry.getKey().toString();

					if (key3.trim().equals(selection)) {
						val1 = allHash.get(key3.trim());
                         break;
					}

					// String row[] = {entry.getKey().toString(), entry.getValue().toString()};


				}
				if(val1.trim().equals("")){

					System.out.println("Value not received");
					ArrayList<String> mapList1 = requestingInfo(split); //split
					HashMap<String, String> allHash1 = mapListLogic(mapList1);
					for (Map.Entry entry : allHash1.entrySet()) {
						key3 = entry.getKey().toString().trim();

						if (key3.trim().equals(selection)) {
							val1 = allHash.get(key3.trim());
							break;
						}

						// String row[] = {entry.getKey().toString(), entry.getValue().toString()};


					}
				}
				System.out.println("Returning for selection: "+selection+" value: "+val1);
				String row[] = {key3.trim(), val1.trim()};
				cursor.addRow(row);

				return cursor;
			}
			//return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}


	public HashMap<String, String> mapListLogic(ArrayList<String> mapLis){
		HashMap<String, String> hMap = new HashMap<String,String>();
		for(int j=0;j<mapLis.size();j++){
			String str = mapLis.get(j);
			System.out.println(str);
			str =str.replace("{", "");
			str =str.replace("}", "");
			System.out.println(str);
			String []abc = str.split(",");
			String [] lst1 = new String[abc.length];
			for(int k=0; k<abc.length;k++){
				System.out.println("ABC: "+abc[k]);
				lst1[k]= abc[k].trim();
				System.out.println("lst1: "+lst1[k]);
			}
			System.out.println(abc.toString());
			for(int i=0;i<abc.length;i++) {
				System.out.println("abc[i] "+abc[i]);
				if(!(abc[i].trim().equals(""))) {
					String split[] = abc[i].split("=");
					hMap.put(split[0].trim(), split[1].trim());
				}
			}
		}

		return hMap;
	}

	public String serverSocketCreate(ServerSocket serverSocket){

		String msgRead ="";
		try {
			Socket socket = serverSocket.accept();
			//  InputStream ip = socket.getInputStream();
			ObjectInputStream oip = new ObjectInputStream(socket.getInputStream());
			msgRead = (String) oip.readObject();
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Message read from server: "+msgRead);
			if(msgRead.contains("JRF")){
				System.out.println("!!!! "+msgRead);
				joinLogic(msgRead);
				msgRead="";
			}else if(msgRead.equals("ping")){
				outputStream.writeObject("ack");
				outputStream.flush();
				msgRead="";
			}
			else if(msgRead.contains("final")){
				System.out.println("In final just before sending to content provider");
				String split1[] = msgRead.split(":");
				if(split1[1].equals("backup")){
					msgRead = split1[0]+":bckup:"+split1[2].trim();
				}
				else {
					msgRead = split1[0];
				}


			}else if(msgRead.contains("getPorts")){
				msgRead = joiningPorts.toString();
				outputStream.writeObject(msgRead);
				outputStream.flush();
				msgRead ="";
			}else if(msgRead.contains("getHashPorts")) {
				msgRead = hashPorts.toString();
				outputStream.writeObject(msgRead);
				outputStream.flush();
				msgRead = "";
			}else if(msgRead.contains("getHashMap")) {
				msgRead = hashMap.toString();
				outputStream.writeObject(msgRead);
				outputStream.flush();
				msgRead = "";
			}else if(msgRead.contains("QueryRequest")){

				HashMap<String, String> maps = new HashMap<String, String>();
				maps.putAll(hashMapFinal);
				msgRead = maps.toString();
				outputStream.writeObject(msgRead);
				outputStream.flush();
				msgRead ="";
			}else if(msgRead.contains("delAll")){
				// hashMapFinal.clear();
				for(Map.Entry entry: hashMapFinal.entrySet()){
					hashMapFinal.remove(entry.getKey());
					if(avd0Map.contains(entry.getKey())){
						avd0Map.remove(entry.getKey());
					}
					if(avd1Map.contains(entry.getKey())){
						avd1Map.remove(entry.getKey());
					}
					if(avd2Map.contains(entry.getKey())){
						avd2Map.remove(entry.getKey());
					}
					if(avd3Map.contains(entry.getKey())){
						avd3Map.remove(entry.getKey());
					}
					if(avd4Map.contains(entry.getKey())){
						avd4Map.remove(entry.getKey());
					}

				}
			}else if(msgRead.contains("A0:")){
				System.out.println("AVD0 asking backup!!");
				String split[]= msgRead.split(":");
				if(split.length==3){
					String msg= avd0Map.toString()+":avdMaps:"+avd1Map.toString();
					System.out.println("Sending avd0 and avd1 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}else if (split.length==2){

					String msg = avd4Map.toString();
					System.out.println("Sending avd4 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";
				}

			}else if(msgRead.contains("A1:")){
				System.out.println("AVD1 asking backup!!");
				String split[]= msgRead.split(":");
				if(split.length==3){
					String msg= avd1Map.toString()+":avdMaps:"+avd4Map.toString();
					System.out.println("AVD1 "+avd1Map.toString());
					System.out.println("AVD4 "+avd4Map.toString());
					System.out.println("Sending avd1 and avd4 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}else if (split.length==2){

					String msg = avd3Map.toString();
					System.out.println("Sending avd3 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";
				}

			}else if(msgRead.contains("A2:")){
				System.out.println("AVD2 asking backup!!");
				String split[]= msgRead.split(":");
				if(split.length==3){
					String msg= avd2Map.toString()+":avdMaps:"+avd0Map.toString();
					System.out.println("Sending avd2 and avd0 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}else if (split.length==2){

					String msg = avd1Map.toString();
					System.out.println("Sending avd1 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";
				}

			}else if(msgRead.contains("A3:")){
				System.out.println("AVD3 asking backup!!");
				String split[]= msgRead.split(":");
				if(split.length==3){
					String msg= avd3Map.toString()+":avdMaps:"+avd2Map.toString();
					System.out.println("Sending avd3 and avd2 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}else if (split.length==2){
					String msg = avd0Map.toString();
					System.out.println("Sending avd0 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}

			}else if(msgRead.contains("A4:")){
				System.out.println("AVD4 asking backup!!");
				String split[]= msgRead.split(":");
				if(split.length==3){
					String msg= avd4Map.toString()+":avdMaps:"+avd3Map.toString();
					System.out.println("Sending avd4 and avd3 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}else if (split.length==2){
					String msg = avd2Map.toString();
					System.out.println("Sending avd2 "+msg);
					outputStream.writeObject(msg);
					outputStream.flush();
					msgRead="";

				}

			}else if(msgRead.contains("del5554")){

				String msg[]= msgRead.split(":");
				String selection = msg[1].trim();
				System.out.println("Clearing data that can give backup: avd2 and avd1");
				if(!(avd2Map.isEmpty())){
					avd2Map.clear();
				}
				if(!(avd1Map.isEmpty())){
					avd1Map.clear();
				}
				if(avd0Map.contains(selection)) {
					System.out.println("Deleting selection from avd0 "+selection);
					avd0Map.remove(selection);
				}
				msgRead="";

			}else if(msgRead.contains("del5556")){

				String msg[]= msgRead.split(":");
				String selection = msg[1].trim();

				System.out.println("Clearing data that can give backup avd0 and avd4");
				if(!(avd0Map.isEmpty())) {
					avd0Map.clear();
				}
				if(!(avd4Map.isEmpty())) {
					avd4Map.clear();
				}
				if(avd1Map.contains(selection)) {
					System.out.println("Deleting selection from avd1 " + selection);
					avd1Map.remove(selection);
				}

				msgRead="";

			}else if(msgRead.contains("del5558")){

				String msg[]= msgRead.split(":");
				String selection = msg[1].trim();

				System.out.println("Clearing data that can give backup avd3 and avd0");
				if(!(avd0Map.isEmpty())) {
					avd0Map.clear();
				}
				if(!(avd3Map.isEmpty())) {
					avd3Map.clear();
				}

                if(avd2Map.contains(selection)) {
					avd2Map.remove(selection);
					System.out.println("Deleting selection from avd2 " + selection);
				}
				msgRead="";

			}else if(msgRead.contains("del5560")){

				String msg[]= msgRead.split(":");
				String selection = msg[1].trim();
				System.out.println("Clearing data that can give backup avd4 and avd2");
				if(!(avd4Map.isEmpty())) {
					avd4Map.clear();
				}
				if(!(avd2Map.isEmpty())) {
					avd2Map.clear();
				}
				if(avd3Map.contains(selection)) {
					avd3Map.remove(selection);
					System.out.println("Deleting selection from avd3 " + selection);
				}
				msgRead="";

			}else if(msgRead.contains("del5562")){

				String msg[]= msgRead.split(":");
				String selection = msg[1].trim();

				System.out.println("Clearing data that can give backup avd1 and avd3");
				if(!(avd1Map.isEmpty())) {
					avd1Map.clear();
				}
				if(!(avd3Map.isEmpty())) {
					avd3Map.clear();
				}

				if(avd4Map.contains(selection)) {
					avd4Map.remove(selection);
					System.out.println("Deleting selection from avd4 " + selection);
				}
				msgRead="";

			}
			else{

                     /* if(msgRead.contains(",")){
                          System.out.println("Message read: "+msgRead);
                          String split[]= msgRead.split(",");
                          String keyHashed = genHash(split[0]);
                        String port=  keyStoreLogic(keyHashed);
                        if(!port.equals("none")){
                            sendToStore(msgRead,port);
                        }


                        msgRead ="";
                      }else{*/
				System.out.println("Message read: "+msgRead);
				String split1[]= msgRead.split(" ");
				//  String keyHashed1 = genHash(split1[0]);
				String [] port1=  keyStoreLogic(split1[0],split1[1]);
				if(!port1.equals("none")){
					sendToStore(msgRead,port1);
				}

				msgRead ="";

				// }
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
//             } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
		return msgRead;

	}

	public void joinLogic(String msg){

        System.out.println("^^^^#@Message in joing logic: "+msg);

		if (hashPorts.isEmpty() && hashMap.isEmpty()) {
		try {


				String[] ports = {"5554", "5556", "5558", "5560", "5562"};
				for (int i = 0; i < ports.length; i++) {
					String hasPort = genHash(ports[i]);
					hashPorts.add(hasPort.trim());
					hashMap.put(hasPort.trim(), ports[i].trim());
					joiningPorts.add(ports[i].trim());

				}

			} catch(NoSuchAlgorithmException e){
				e.printStackTrace();

			}
		}
		    syncLogic(msg);

	}

	public void syncLogic(String msg) {

		String split[] = msg.split(":");
		String newOrCrashedPort = split[1];
		System.out.println("New or crashed port: " + newOrCrashedPort);
		Socket socket[] = new Socket[10];
		if(!(newOrCrashedPort.trim().equals("final"))){

		String actualPort = Integer.toString((Integer.parseInt(newOrCrashedPort) / 2));
		System.out.println("Actual port: " + actualPort);
		String avd0 = Integer.toString((Integer.parseInt("5554") * 2));
		String avd1 = Integer.toString((Integer.parseInt("5556") * 2));
		String avd2 = Integer.toString((Integer.parseInt("5558") * 2));
		String avd3 = Integer.toString((Integer.parseInt("5560") * 2));
		String avd4 = Integer.toString((Integer.parseInt("5562") * 2));

		try {
			if (actualPort.trim().equals("5554")) {
				socket[0] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd2));
				socket[0].setSoTimeout(100);
				ObjectOutputStream os = new ObjectOutputStream(socket[0].getOutputStream());
				os.writeObject("A0:0:1");
				os.flush();
				ObjectInputStream inputStream = new ObjectInputStream(socket[0].getInputStream());
				String msg1 = (String) inputStream.readObject();
				socket[1] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd1));
				socket[1].setSoTimeout(100);
				ObjectOutputStream os1 = new ObjectOutputStream(socket[1].getOutputStream());
				os1.writeObject("A0:4");
				os1.flush();
				ObjectInputStream inputStream1 = new ObjectInputStream(socket[1].getInputStream());
				String msg2 = (String) inputStream1.readObject();
				if (!(msg1.trim().equals("")) && !(msg2.trim().equals(""))) {
					String theMsgFromAvds = msg1 + ":Part:" + msg2;
					System.out.println("#######Recovering: " + theMsgFromAvds + " 5554");
					storingInFinalMap(theMsgFromAvds + ":A014");
				}

			} else if (actualPort.trim().equals("5556")) {
				socket[2] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd0));
				socket[2].setSoTimeout(100);
				ObjectOutputStream os = new ObjectOutputStream(socket[2].getOutputStream());
				os.writeObject("A1:1:4");
				os.flush();
				ObjectInputStream inputStream = new ObjectInputStream(socket[2].getInputStream());
				String msg1 = (String) inputStream.readObject();
				socket[3] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd4));
				socket[3].setSoTimeout(100);
				ObjectOutputStream os1 = new ObjectOutputStream(socket[3].getOutputStream());
				os1.writeObject("A1:3");
				os1.flush();
				ObjectInputStream inputStream1 = new ObjectInputStream(socket[3].getInputStream());
				String msg2 = (String) inputStream1.readObject();
				if (!(msg1.trim().equals("")) && !(msg2.trim().equals(""))) {
					String theMsgFromAvds = msg1 + ":Part:" + msg2;
					System.out.println("#######Recovering: " + theMsgFromAvds + " 5556");
					storingInFinalMap(theMsgFromAvds + ":A143");
				}

			} else if (actualPort.trim().equals("5558")) {
				socket[4] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd3));
				socket[4].setSoTimeout(100);
				ObjectOutputStream os = new ObjectOutputStream(socket[4].getOutputStream());
				os.writeObject("A2:2:0");
				os.flush();
				ObjectInputStream inputStream = new ObjectInputStream(socket[4].getInputStream());
				String msg1 = (String) inputStream.readObject();
				socket[5] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd0));
				socket[5].setSoTimeout(100);
				ObjectOutputStream os1 = new ObjectOutputStream(socket[5].getOutputStream());
				os1.writeObject("A2:1");
				os1.flush();
				ObjectInputStream inputStream1 = new ObjectInputStream(socket[5].getInputStream());
				String msg2 = (String) inputStream1.readObject();
				if (!(msg1.trim().equals("")) && !(msg2.trim().equals(""))) {
					String theMsgFromAvds = msg1 + ":Part:" + msg2;
					System.out.println("#######Recovering: " + theMsgFromAvds + " 5558");
					storingInFinalMap(theMsgFromAvds + ":A201");
				}

			} else if (actualPort.trim().equals("5560")) {
				socket[6] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd4));
				socket[6].setSoTimeout(100);
				ObjectOutputStream os = new ObjectOutputStream(socket[6].getOutputStream());
				os.writeObject("A3:3:2");
				os.flush();
				ObjectInputStream inputStream = new ObjectInputStream(socket[6].getInputStream());
				String msg1 = (String) inputStream.readObject();
				socket[7] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd2));
				socket[7].setSoTimeout(100);
				ObjectOutputStream os1 = new ObjectOutputStream(socket[7].getOutputStream());
				os1.writeObject("A3:0");
				os1.flush();
				ObjectInputStream inputStream1 = new ObjectInputStream(socket[7].getInputStream());
				String msg2 = (String) inputStream1.readObject();
				if (!(msg1.trim().equals("")) && !(msg2.trim().equals(""))) {
					String theMsgFromAvds = msg1 + ":Part:" + msg2;
					System.out.println("#######Recovering: " + theMsgFromAvds + ":5560");
					storingInFinalMap(theMsgFromAvds + ":A320");
				}


			} else if (actualPort.trim().equals("5562")) {
				socket[8] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd1));
				socket[8].setSoTimeout(100);
				ObjectOutputStream os = new ObjectOutputStream(socket[8].getOutputStream());
				os.writeObject("A4:4:3");
				os.flush();
				ObjectInputStream inputStream = new ObjectInputStream(socket[8].getInputStream());
				String msg1 = (String) inputStream.readObject();
				socket[9] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(avd3));
				socket[9].setSoTimeout(100);
				ObjectOutputStream os1 = new ObjectOutputStream(socket[9].getOutputStream());
				os1.writeObject("A4:2");
				os1.flush();
				ObjectInputStream inputStream1 = new ObjectInputStream(socket[9].getInputStream());
				String msg2 = (String) inputStream1.readObject();
				if (!(msg1.trim().equals("")) && !(msg2.trim().equals(""))) {
					String theMsgFromAvds = msg1 + ":Part:" + msg2;
					System.out.println("#######Recovering: " + theMsgFromAvds + ":5562");
					storingInFinalMap(theMsgFromAvds + ":A432");
				}


			}

		} catch (SocketTimeoutException e) {
			System.out.println("New or Creashed Node");
			Log.v(TAG, "Crashed or new Node");
			return;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			System.out.println("New or Creashed Node end of file exception");
			Log.v(TAG, "Crashed or new Node end of file exception");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	}
	public void storingInFinalMap(String msg) {

		String split[] = msg.split(":Part:"); // {}:avdMap:{}:Part:{}:A123

		String oneAvd[] = split[1].split(":");
		String oneAvdMap = oneAvd[0];
		oneAvdMap = oneAvdMap.replace("{", "");
		oneAvdMap = oneAvdMap.replace("}", "");  //after Part map

		String twoAvd[] = split[0].split(":avdMaps:");
		String twoAvdMap1 = twoAvd[0]; // before avdMaps map
		twoAvdMap1 = twoAvdMap1.replace("{", "");
		twoAvdMap1 = twoAvdMap1.replace("}", "");
		String twoAvdMap2 = twoAvd[1]; // after avdMaps map
		twoAvdMap2 = twoAvdMap2.replace("{", "");
		twoAvdMap2 = twoAvdMap2.replace("}", "");


		System.out.println("The three maps are: oneAvdMap: {" + oneAvdMap + "} Two AVD MAP first: {" + twoAvdMap1 + "} Two avdMap part2 {" + twoAvdMap2 + "}");


		if (!(oneAvdMap.trim().equals("") && twoAvdMap1.trim().equals("") && twoAvdMap2.trim().equals(""))) {

			//	HashMap<String, String> backupHash = new HashMap<String, String>();

			String oneAvdSplit[] = oneAvdMap.split(","); // we are taking each individual values in this array and then inserting in respective maps
			String twoAvd1[] = twoAvdMap1.split(",");
			String twoAvd2[] = twoAvdMap2.split(",");

			String identification = oneAvd[1];

            if(!(twoAvdMap1.trim().equals(""))){
			for (int i = 0; i < twoAvd1.length; i++) {
				String individualSplit[] = twoAvd1[i].trim().split("=");
				hashMapFinal.put(individualSplit[0].trim(), individualSplit[1].trim());

				if (identification.trim().equals("A014")) {
					avd0Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A143")) {
					avd1Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A201")) {
					avd2Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A320")) {
					avd3Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A432")) {
					avd4Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				}
			}
		}
			if(!(twoAvdMap2.trim().equals(""))){
			for (int i = 0; i < twoAvd2.length; i++) {
				String individualSplit[] = twoAvd2[i].trim().split("=");
				hashMapFinal.put(individualSplit[0].trim(), individualSplit[1].trim());

				if (identification.trim().equals("A014")) {
					avd1Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A143")) {
					avd4Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A201")) {
					avd0Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A320")) {
					avd2Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				} else if (identification.trim().equals("A432")) {
					avd3Map.put(individualSplit[0].trim(), individualSplit[1].trim());
				}
			}
		}
			if (!(oneAvdMap.trim().equals(""))){
				for (int i = 0; i < oneAvdSplit.length; i++) {
					String individualSplit[] = oneAvdSplit[i].trim().split("=");

					hashMapFinal.put(individualSplit[0].trim(), individualSplit[1].trim());

					if (identification.trim().equals("A014")) {
						avd4Map.put(individualSplit[0].trim(), individualSplit[1].trim());
					} else if (identification.trim().equals("A143")) {
						avd3Map.put(individualSplit[0].trim(), individualSplit[1].trim());
					} else if (identification.trim().equals("A201")) {
						avd1Map.put(individualSplit[0].trim(), individualSplit[1].trim());
					} else if (identification.trim().equals("A320")) {
						avd0Map.put(individualSplit[0].trim(), individualSplit[1].trim());
					} else if (identification.trim().equals("A432")) {
						avd2Map.put(individualSplit[0].trim(), individualSplit[1].trim());
					}
				}
		}

		}




//		String part1[] = split[0].split(":avdMaps:");
//		String mapStr = split[1];
//		System.out.println("Mapstr and part1[0] and split[1] "+" part1[0] "+part1[0]+" part1[1] "+part1[1]+ " split[0] "+split[0]+ " split[1] "+split[1]);
//
//		String twoMaps = part1[0];
//		twoMaps = twoMaps.replace("{", "");
//		twoMaps = twoMaps.replace("}", "");
//
//		String twoMapsPart2 = part1[1];
//		twoMapsPart2 = twoMapsPart2.replace("{", "");
//		twoMapsPart2 = twoMapsPart2.replace("}", "");
//		if(!(twoMaps.trim().equals("") && twoMapsPart2.trim().equals(""))){
//		String Str[] = mapStr.split(":A");
//		Str[0] = Str[0].replace("{", "");
//		Str[0] = Str[0].replace("}", "");
//		String individualValue[] = Str[0].split(",");
//
//		for (int i = 0; i < individualValue.length; i++) {
//			String theKeyValuePairs[] = individualValue[i].split("=");
//			String key = theKeyValuePairs[0].trim();
//			String value = theKeyValuePairs[1].trim();
//			//if (!hashMapFinal.contains(key.trim())) {
//				hashMapFinal.put(key.trim(), value.trim());
//				if (Str[1].trim().contains("A014")) {
//					System.out.println("@@##$$Storing");
//					avd4Map.put(key.trim(), value.trim());
//				} else if (Str[1].trim().contains("A143")) {
//					System.out.println("@@##$$Storing");
//					avd3Map.put(key.trim(), value.trim());
//				} else if (Str[1].trim().contains("A201")) {
//					System.out.println("@@##$$Storing");
//					avd1Map.put(key.trim(), value.trim());
//				} else if (Str[1].trim().contains("A320")) {
//					System.out.println("@@##$$Storing");
//					avd0Map.put(key.trim(), value.trim());
//				} else if (Str[1].trim().contains("A432")) {
//					System.out.println("@@##$$Storing");
//					avd2Map.put(key.trim(), value.trim());
//				}
//		//	}
//
//		}
//
//
//		String twoMapSplit[] = twoMaps.split(",");
//		for (int i = 0; i < twoMapSplit.length; i++) {
//			String theKeyValuePairs[] = twoMapSplit[i].split("=");
//			String key = theKeyValuePairs[0].trim();
//			String value = theKeyValuePairs[1].trim();
//		//	if (!hashMapFinal.contains(key.trim())) {
//				hashMapFinal.put(key.trim(), value.trim());
//			//}
//			if (Str[1].trim().contains("A014")) {
//				avd0Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A143")) {
//				avd1Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A201")) {
//				avd2Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A320")) {
//				avd3Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A432")) {
//				avd4Map.put(key.trim(), value.trim());
//			}
//
//		}
//
//
//		String twoMapPart2Split[] = twoMapsPart2.split(",");
//		for (int i = 0; i < twoMapPart2Split.length; i++) {
//			String theKeyValuePairs[] = twoMapPart2Split[i].split("=");
//			String key = theKeyValuePairs[0].trim();
//			String value = theKeyValuePairs[1].trim();
//			//if (!hashMapFinal.contains(key.trim())) {
//				hashMapFinal.put(key.trim(), value.trim());
//		//	}
//			if (Str[1].trim().contains("A014")) {
//				avd1Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A143")) {
//				avd4Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A201")) {
//				avd0Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A320")) {
//				avd2Map.put(key.trim(), value.trim());
//			} else if (Str[1].trim().contains("A432")) {
//				avd3Map.put(key.trim(), value.trim());
//			}
//
//		}
//	}
	}

	public void sendToStore(String msg,String port[]){

		String orignalPort= "";
		for(int i=0;i<port.length;i++) {
			try {
				System.out.println("Sending final message to store to Port: " + port);
				String prt[]=   port[i].split(":");
				if(prt[1].trim().equals("orignal")){
					orignalPort = prt[0].trim();
					String str1 = Integer.toString((Integer.parseInt(prt[0].trim()) * 2));
					System.out.println("Sending port: "+port[i]+ " str1 "+str1);
					Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(str1.trim()));
					ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
					System.out.println("final msg: " + msg);
					os.writeObject(msg + ":final");
					os.flush();
				}else if(prt[1].equals("backup")){
					port[i] = Integer.toString((Integer.parseInt(prt[0].trim()) * 2));
					Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port[i].trim()));
					ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
					System.out.println("final msg: " + msg);
					System.out.println("Backup of orignal port is: "+orignalPort);
					os.writeObject(msg + ":backup:"+orignalPort+":final");
					os.flush();
				}

//				Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port[i]));
//				ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
//				System.out.println("final msg: " + msg);
//				os.writeObject(msg + ":final");
//				os.flush();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public String[] keyStoreLogic(String key,String values){
		String hashKey = null;
		try {
			hashKey = genHash(key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println("## Hash key received is: "+hashKey);
		String [] port =new String[3];
		ArrayList<String> decOrderlist = new ArrayList<String>();


		HashMap <String, String> mappingPorts = new HashMap<String, String>();


			for(Map.Entry entry: hashMap.entrySet()){
				mappingPorts.put(entry.getKey().toString().trim(),entry.getValue().toString().trim());
			}

		ArrayList <Integer> compute = new ArrayList<Integer>();
		HashMap <Integer,String> hMap = new HashMap<Integer, String>();

		// decOrderlist=finalPorts;
		for(int i=0; i< hashPorts.size();i++){
			decOrderlist.add(hashPorts.get(i).trim());
		}
		decOrderlist.add(hashKey.trim());
		Collections.sort(decOrderlist);
		int index = decOrderlist.indexOf(hashKey.trim());
		System.out.println("@@hash port: "+hashMap.toString());
		System.out.println("@@Index: "+index);
		System.out.println("@@Hashkey: "+hashKey);
		System.out.println("@@Dec list: "+decOrderlist.toString());
	//	System.out.println("@@FInalPorts list: "+finalPorts.toString());

		if(index == decOrderlist.size()-1){
			//   port = mappingPorts.get(decOrderlist.get(decOrderlist.size()-2).trim());

			port[0]= hashMap.get(decOrderlist.get(0).trim())+":orignal";
			port[1]= hashMap.get(decOrderlist.get(1).trim())+":backup";
			port[2] = hashMap.get(decOrderlist.get(2).trim())+":backup";

//			for(int k=0;k< port.length;k++){
//				port[k]= hashMap.get(decOrderlist.get(k).trim());
//				System.out.println("If index is last Ports are: "+port[k]);
//			}
			//port = mappingPorts.get(decOrderlist.get(0).trim());

			//  insertFun(key,values);
			System.out.println(" If index in  last then Ports are: "+port[0]+" "+port[1]+" "+port[2]);
			return port;
			//return "none";
		}else if(index == decOrderlist.size()-2){

			port[0] = hashMap.get(decOrderlist.get(index+1).trim())+":orignal";
			port[1] = hashMap.get(decOrderlist.get(0).trim())+":backup";
			port[2] = hashMap.get(decOrderlist.get(1).trim())+":backup";
			System.out.println(" If index in second last then Ports are: "+port[0]+" "+port[1]+" "+port[2]);
			return port;


		}else if(index == decOrderlist.size()-3){

			port[0] = hashMap.get(decOrderlist.get(index+1).trim())+":orignal";
			port[1] = hashMap.get(decOrderlist.get(index+2).trim())+":backup";
			port[2] = hashMap.get(decOrderlist.get(0).trim())+":backup";
			System.out.println(" If index in third last then Ports are: "+port[0]+" "+port[1]+" "+port[2]);
			return port;

		}

		else{


			int x=index+1;

			port[0] = hashMap.get(decOrderlist.get(x).trim())+":orignal";
			port[1] = hashMap.get(decOrderlist.get(x+1).trim())+":backup";
			port[2] = hashMap.get(decOrderlist.get(x+2).trim())+":backup";

			System.out.println(" If index in any in middle mostly 1st or 2nd then Ports are: "+port[0]+" "+port[1]+" "+port[2]);
			return port;
		}


	}
	public HashMap<String,String> getMap(String lst){
		HashMap<String, String> hMap = new HashMap<String,String>();
		lst =lst.replace("{", "");
		lst =lst.replace("}", "");
		String []abc = lst.split(",");
		for(int i=0;i<abc.length;i++) {
			System.out.println("Received is abc[i]: "+abc[i]);
			if(!(abc[i].trim().equals(""))) {
				String split[] = abc[i].trim().split("=");
				hMap.put(split[0].trim(), split[1].trim());
			}
		}

		return hMap;

	}
	public void clientTask(String msg){


		Socket socket_;
		ObjectOutputStream os;
		try {
			//  for (int i = 0; i < joiningPorts.size(); i++) {
			socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
			// socket_ = socket[i];
			os = new ObjectOutputStream(socket_.getOutputStream());
			os.writeObject(msg);
			os.flush();
			// }
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}


	}
	public void clientJoinRequest(String msg) {

		String msgs[] = msg.split(":");
		String ports = msgs[1];
//        if (count == 0 && !(ports.equals("11108"))) {
//            System.out.println("FIRST TIME and not AVD0");
//            firstReq(msg);
//            count++;
//        } else{
		try {
			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(ports));
			socket.setSoTimeout(TIMEOUT);
			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
			ops.writeObject(msg);

			ops.flush();

		} catch (SocketTimeoutException e) {
			Log.d(TAG, "Socket timeout exception, failed socket: ");
			firstReq(msg);


		}catch (EOFException e){
			firstReq(msg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		//}
	}

	public void firstReq(String msg){
		String msgs[] = msg.split(":");
		String ports = msgs[1];

		try {
			System.out.println("first Joining req "+msg);
			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(ports));
			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());

			ops.writeObject(msg);
			ops.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public ArrayList<String> requestingInfo(String [] arr) {

		Socket socket[] = new Socket[arr.length];
		int sock=0;
		Socket socket_;
		ObjectOutputStream os;
		ObjectInputStream istream;
		ArrayList<String> mapList = new ArrayList<String>();
		for (int i = 0; i < arr.length; i++) {
			try {

				//for (int i = 0; i < arr.length; i++) {
				if (!arr[i].trim().equals("")) {

					socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(arr[i].trim()) * 2));
					socket_ = socket[i];
					sock = socket_.getPort();
					socket_.setSoTimeout(100);
					os = new ObjectOutputStream(socket_.getOutputStream());
					os.writeObject("QueryRequest");
					// os.writeObject("QueryRequest");
					os.flush();
					istream = new ObjectInputStream(socket_.getInputStream());
					String str = (String) istream.readObject();
					mapList.add(str);

				}
				//}
			} catch (SocketTimeoutException e) {
				System.out.println("Socket timed out " + sock);
				Log.v(TAG, "Socket timed out: " + sock);

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (EOFException e) {
				System.out.println("EOF exception. Port forefully stopped " + sock);
				Log.v(TAG, "EOF Exception");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mapList;
	}

	public String getJoiningPorts(){
		String list="";
		int sock=0;
		try {
			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
			sock = socket.getPort();
			socket.setSoTimeout(100);
			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
			ops.writeObject("getPorts");
			ops.flush();
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			list= (String) inputStream.readObject();

		}catch (SocketTimeoutException e){
			System.out.println("Socket timed out: "+sock);
		}
		catch (EOFException e){
			return list;
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return list;
	}
	public void getHashPorts(){
//		String list="";
//		try {
//			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
//			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
//			ops.writeObject("getHashPorts");
//			ops.flush();
//			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
//			list= (String) inputStream.readObject();
//
//		}catch (EOFException e){
//			return list;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();

//		}
        try {

			String[] ports = {"5554", "5556", "5558", "5560", "5562"};
			for (int i = 0; i < ports.length; i++) {
				String hasPort = genHash(ports[i]);
				hashPorts.add(hasPort.trim());
				hashMap.put(hasPort.trim(), ports[i].trim());

			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		//return list;
	}
	public String getHashMaps(){
		String list="";
		try {
			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
			ops.writeObject("getHashMap");
			ops.flush();
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			list= (String) inputStream.readObject();

		}catch (EOFException e){
			return list;
		}catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return list;
	}
	public boolean checkPingAck(String msg){

		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
			ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
			ops.writeObject("ping");
			ops.flush();
			ObjectInputStream ist = new ObjectInputStream(socket.getInputStream());
			String ack = (String)ist.readObject();
			if(ack.equals("ack")){
				return true;
			}

		}catch (EOFException e){
			return false;
		}catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// socket.setSoTimeout(TIMEOUT);

		return false;
	}
	private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }




	public class ClientStore extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... strings) {
			sendToStore(strings[0],strings[1]);
			return null;
		}

		public void sendToStore(String msg,String portList){
			portList = portList.replace("[","");
			portList = portList.replace("]","");
			String port[]= portList.split(",");
			System.out.println("$$$Message from send to store!! "+msg);

			String orignalPort= "";
			for(int i=0;i<port.length;i++) {
				try {
					System.out.println("Sending final message to store to Port: " + port[i]);
					String prt[]=   port[i].trim().split(":");
					System.out.println("Port split: "+prt[0] +" "+prt[1]);
					if(prt[1].trim().equals("orignal")){
						orignalPort = prt[0].trim();
						String str1 = Integer.toString((Integer.parseInt(prt[0].trim()) * 2));
						System.out.println("Sending port: "+port[i]+ " str1 "+str1);
						Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(str1.trim()));
						ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
						System.out.println("final msg: " + msg);
						os.writeObject(msg + ":final");
						os.flush();
					}else if(prt[1].trim().equals("backup")){
						port[i] = Integer.toString((Integer.parseInt(prt[0].trim()) * 2));
						System.out.println("Backup port sending: "+port[i]);
						Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port[i].trim()));
						ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
						System.out.println("final msg: " + msg);
						System.out.println("Backup of orignal port is: "+orignalPort);
						os.writeObject(msg + ":backup:"+orignalPort+":final");
						os.flush();
					}

//				Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port[i]));
//				ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
//				System.out.println("final msg: " + msg);
//				os.writeObject(msg + ":final");
//				os.flush();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
