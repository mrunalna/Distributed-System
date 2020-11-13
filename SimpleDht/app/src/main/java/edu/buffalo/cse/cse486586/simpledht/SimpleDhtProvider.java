package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class SimpleDhtProvider extends ContentProvider {

    static final String TAG = SimpleDhtProvider.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    static String [] mPorts = {REMOTE_PORT0, REMOTE_PORT1,REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};

    static ArrayList <String> joiningPorts = new ArrayList<String>();
    static ArrayList <String> hashPorts = new ArrayList<String>();
    static HashMap<String, String> hashMap = new HashMap<String, String>();
    static HashMap<String, String> hashMapFinal = new HashMap<String, String>();
    String coloumns[] = {"key", "value"};
    static  int TIMEOUT = 1000;
    static int count =0;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        if(selection.equals("@")){

            //hashMapFinal.clear();
            for(Map.Entry entry: hashMapFinal.entrySet()){
                hashMapFinal.remove(entry.getKey().toString().trim());
                System.out.println("del @ logic printing hashmap "+hashMapFinal.toString());
                getContext().deleteFile(entry.getKey().toString().trim());
            }
        }else if(selection.equals("*")){
            System.out.println("Del called *");
            delAll();

        }else{

            System.out.println("Selection for del: "+selection);
            hashMapFinal.remove(selection.trim());
            getContext().deleteFile(selection);
        }


        return 0;
    }

    public void delAll(){

        Socket socket[] = new Socket[5];
        Socket socket_;
        ObjectOutputStream os;
        ObjectInputStream istream;
        ArrayList<String> mapList = new ArrayList<String>();
        String str = getJoiningPorts();

        if(str.trim().equals("")){
            str = joiningPorts.toString();
        }
        str = str.replace("[", "");
        str = str.replace("]", "");
        String split[] = str.split(",");
        for(int i=0;i<split.length;i++){
            System.out.println("Split!! "+split[i]);
        }
        try {

            for (int i = 0; i < split.length; i++) {
                if(!split[i].trim().equals("")){

                    socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(split[i].trim())*2));
                    socket_ = socket[i];
                    os = new ObjectOutputStream(socket_.getOutputStream());
                    os.writeObject("delAll");
                    // os.writeObject("QueryRequest");
                    os.flush();
//                    istream = new ObjectInputStream(socket_.getInputStream());
//                    String str1 = (String) istream.readObject();
//                    mapList.add(str1);

                }
            }
        } catch (UnknownHostException e) {
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
        if(!(key.contains("fromServ") && key.contains("fromServ"))) {
            try {
                keyHashed1 = genHash(key);
                System.out.println("Key and its hash: "+key+" "+keyHashed1);
                String port1 = keyStoreLogic(key,value);
                if ( !(port1==null) && !port1.equals("none")) {
                    String msgRead = key + "," + value;
                    sendToStore(msgRead, port1);
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

            String filename = key;
            System.out.println("In insert of content provider");
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
        Log.v("insert", values.toString());

        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

     //   new SimpleDhtActivity.ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg,myPort);
        return false;
    }

    @Override
    public MatrixCursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub


        if (selection.equals("*")) {
            MatrixCursor cursor = new MatrixCursor(coloumns);

            String str = getJoiningPorts();
            str = str.replace("[", "");
            str = str.replace("]", "");
            String split[] = str.split(",");
            ArrayList<String> mapList = requestingInfo(split);
            if (mapList.isEmpty()) {
                for (Map.Entry entry : hashMapFinal.entrySet()) {
                    String row[] = {entry.getKey().toString(), entry.getValue().toString()};
                    System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
                    cursor.addRow(row);
                }
                return cursor;

            } else {
                HashMap<String, String> allHash = mapListLogic(mapList);

                System.out.println("Got * query");

                for (Map.Entry entry : allHash.entrySet()) {

                    String row[] = {entry.getKey().toString(), entry.getValue().toString()};
                    cursor.addRow(row);

                }

                return cursor;
            }

        } else if (selection.equals("@")) {
            MatrixCursor cursor = new MatrixCursor(coloumns);

            for (Map.Entry entry : hashMapFinal.entrySet()) {
                String row[] = {entry.getKey().toString(), entry.getValue().toString()};
                System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
                cursor.addRow(row);
            }
            return cursor;

        } else {
            MatrixCursor cursor = new MatrixCursor(coloumns);

            String str = getJoiningPorts();

            if(str.trim().equals("")){
                str = joiningPorts.toString();
            }
            str = str.replace("[", "");
            str = str.replace("]", "");
            String split[] = str.split(",");
            for(int i=0;i<split.length;i++){
                System.out.println("Split!! "+split[i]);
            }
            ArrayList<String> mapList = requestingInfo(split);


            if (mapList.isEmpty()) {
                for (Map.Entry entry : hashMapFinal.entrySet()) {
                    String key2 = entry.getKey().toString();
                    String val="";
                    if(key2.trim().equals(selection)){
                        val = hashMapFinal.get(key2);

                    }
                    String row[] = {key2, val};
                    //System.out.println("Key value for @: " + entry.getKey().toString() + " " + entry.getValue().toString());
                    System.out.println("Key value for single pair: " + key2 + " " + val);
                    cursor.addRow(row);
                }
                return cursor;
            }else{
                HashMap<String, String> allHash = mapListLogic(mapList);

                System.out.println("Single pair query");


                for (Map.Entry entry : allHash.entrySet()) {
                    String key3 = entry.getKey().toString();
                    String val1 ="";
                    if(key3.trim().equals(selection)){
                        val1 = allHash.get(key3);

                    }
                    String row[] = {key3, val1};
                    cursor.addRow(row);
                   // String row[] = {entry.getKey().toString(), entry.getValue().toString()};


                }

                return cursor;
            }
        }

//        else{
//            FileInputStream fileInputStream = null;
//            BufferedReader reader = null;
//            MatrixCursor matrixCursor=null;
//
//            try {
//                 matrixCursor = new MatrixCursor(coloumns);
//                fileInputStream = getContext().openFileInput(selection);
//                reader = new BufferedReader(new InputStreamReader(fileInputStream));
//                String value = reader.readLine();
//                String row[] = {selection, value};
//                matrixCursor = new MatrixCursor(coloumns);
//                matrixCursor.addRow(row);
//                Log.v(TAG , selection+ " "+ value);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Log.v("query", selection);
//            return matrixCursor;
//        }

         // return null;
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

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
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
                    msgRead = split1[0];


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

                    msgRead = hashMapFinal.toString();
                    outputStream.writeObject(msgRead);
                    outputStream.flush();
                    msgRead ="";
                }else if(msgRead.contains("delAll")){
                   // hashMapFinal.clear();
                    for(Map.Entry entry: hashMapFinal.entrySet()){
                        hashMapFinal.remove(entry.getKey().toString().trim());
                        getContext().deleteFile(entry.getKey().toString().trim());
                    }
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
                          String port1=  keyStoreLogic(split1[0],split1[1]);
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

    public void sendToStore(String msg,String port){

        try {
            System.out.println("Sending final message to store to Port: "+port);
            port = Integer.toString ((Integer.parseInt(port)*2));
            Socket socket_ = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
            ObjectOutputStream os = new ObjectOutputStream(socket_.getOutputStream());
            System.out.println("final msg: "+msg);
            os.writeObject(msg+":final");
            os.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void joinLogic(String msg){

        System.out.println("!!!! "+msg);
        String ports[] = msg.split(":");
        int oriPort = Integer.parseInt(ports[1]);
        int actualPort = oriPort/2;
       // joiningPorts.add(ports[1]);
        joiningPorts.add(Integer.toString(actualPort));
        try {
            ports[1] = Integer.toString(actualPort);
            String hashedPort = genHash(ports[1].trim());
            hashPorts.add(hashedPort);
            System.out.println("hashport: port = "+ hashedPort+":"+ports[1]);
            hashMap.put(hashedPort,ports[1]);
            Collections.sort(hashPorts);
            System.out.println("Hashed port is: "+hashedPort);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }




    public String keyStoreLogic(String key,String values){
        String hashKey = null;
        try {
            hashKey = genHash(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("## Hash key received is: "+hashKey);
        String port ="";
        int flag =0;
        ArrayList<String> decOrderlist = new ArrayList<String>();

        String str = getHashPorts();
        System.out.println("HashPorts: "+str);
        ArrayList<String> finalPorts = new ArrayList<String>();
        if(str.trim().equals("")){
            for (int i=0;i<hashPorts.size();i++){
                finalPorts.add(hashPorts.get(i).trim());
                Collections.sort(finalPorts);

            }

        }
        else{
            str = str.replace("[","");
            str = str.replace("]","");
            String split[]= str.split(",");
            for(int i=0;i<split.length;i++){
                finalPorts.add(split[i].trim());
                Collections.sort(finalPorts);
                //ascending order hashes list
            }
        }



//        for (int k=finalPorts.size()-1; k>=0;k--){
//            decOrderlist.add(finalPorts.get(k).trim());
//        }





        String hashList = getHashMaps();
        System.out.println("HashList: "+hashList);
        HashMap <String, String> mappingPorts = new HashMap<String, String>();

        if(hashList.trim().equals("")){
            for(Map.Entry entry: hashMap.entrySet()){
                mappingPorts.put(entry.getKey().toString().trim(),entry.getValue().toString().trim());
            }
        }
        else{
            HashMap<String, String> hash = new HashMap<String, String>();
            hash = getMap(hashList);
            for(Map.Entry entry: hash.entrySet()){
                mappingPorts.put(entry.getKey().toString().trim(),entry.getValue().toString().trim());
            }

        }

        ArrayList <Integer> compute = new ArrayList<Integer>();
        HashMap <Integer,String> hMap = new HashMap<Integer, String>();

        // decOrderlist=finalPorts;
        for(int i=0; i< finalPorts.size();i++){
            decOrderlist.add(finalPorts.get(i));
        }
        decOrderlist.add(hashKey);
        Collections.sort(decOrderlist);
        int index = decOrderlist.indexOf(hashKey);
        System.out.println("@@Mapping port: "+mappingPorts.toString());
        System.out.println("@@Index: "+index);
        System.out.println("@@Hashkey: "+hashKey);
        System.out.println("@@Dec list: "+decOrderlist.toString());
        System.out.println("@@FInalPorts list: "+finalPorts.toString());


        if(finalPorts.size()==1){
            port = mappingPorts.get(finalPorts.get(0).trim());
            System.out.println("POrt: "+port);

            return  port;
        }
        else if(index == decOrderlist.size()-1){
         //   port = mappingPorts.get(decOrderlist.get(decOrderlist.size()-2).trim());
            port = mappingPorts.get(decOrderlist.get(0).trim());
            System.out.println("POrt last: "+port);
          //  insertFun(key,values);
            return port;
            //return "none";
        }else{

            port =  mappingPorts.get(decOrderlist.get(index+1).trim());
            System.out.println("POrt: "+port);
            return port;
        }


//          for(int i=0; i<finalPorts.size();i++){
//
//              decOrderlist.add(hashKey);
//
//          }




//        for(int i=0; i<decOrderlist.size();i++){
//            int diff = hashKey.compareTo(decOrderlist.get(i));
//            compute.add(diff);
//            hMap.put(diff,decOrderlist.get(i));
//            System.out.println("Hash map diff mapping: "+hMap.toString());
//            System.out.println("Compute: "+compute.toString());
//


//            if(i== decOrderlist.size()-1){
//                int val= compute.get(0);
//
//
//                if(compute.size() ==1){
//                   val= compute.get(0);
//                    String hashDiffKey = hMap.get(val).trim();
//                    port = mappingPorts.get(hashDiffKey.trim());
//                    System.out.println("Diff key and ports are: "+val+" hashPort: "+hashDiffKey+" Port: "+port);
//                    return port;
//                } else if(val>0){
//
//                    System.out.println("Insetring directly in the same avd since diff is positive");
//                   // insertFun(key,values);
//                    val = compute.get(decOrderlist.size()-1);
//                    String hashDiffKey = hMap.get(val).trim();
//                    port = mappingPorts.get(hashDiffKey.trim());
//                    System.out.println("Diff key and ports are: "+val+" hashPort: "+hashDiffKey+" Port: "+port);
//                    return port;
//
//                }
//                else{
//
//                    for(int k=0;k<compute.size();k++){
//
//                        val = compute.get(k);
//                        if(val>0){
//                            val = compute.get(k-1);
//                            String hashDiffKey = hMap.get(val).trim();
//                            port = mappingPorts.get(hashDiffKey.trim());
//                            System.out.println("Diff key and ports are: "+val+" hashPort: "+hashDiffKey+" Port: "+port);
//                            return port;
//                        }
//                           if(k == compute.size()-1){
//                               val = compute.get(0);
//                               String hashDiffKey = hMap.get(val).trim();
//                               port = mappingPorts.get(hashDiffKey.trim());
//                               System.out.println("Diff key and ports are: "+val+" hashPort: "+hashDiffKey+" Port: "+port);
//                               return port;
//
//                        }
//
//                    }
//
//                }
//
//            }
//
//
//        }

//        for(int i=0;i< finalPorts.size();i++){
//            int value = hashKey.compareTo(finalPorts.get(i).trim());
//            compute.add(value);
//            Collections.sort(compute); //ascending
//            hMap.put(value, finalPorts.get(i).trim());
//            System.out.println("Compute: "+compute);
//            System.out.println("Diff val mapping: "+hMap);
//            System.out.println("Mapping ports: "+mappingPorts.toString());
//
//            if(i== finalPorts.size()-1){
//
//
//
//
//                int diff = compute.get(0);
//                if(diff>0 || diff ==0){
//
//                    System.out.println("Least diff is: "+diff);
//                    String hashKeyF = hMap.get(diff).trim();
//                    //port = hashMap.get(hashKeyF);
//                    port = mappingPorts.get(hashKeyF.trim());
//                    System.out.println("Final hashKey dcided "+hashKeyF);
//                    System.out.println("Port is: "+port);
//
//                    return port;
//
//                }else if(compute.size()==1){
//                    diff = compute.get(0);
//                    System.out.println("Least diff is: "+diff);
//                    String hashKeyF = hMap.get(diff);
//                    //  port = hashMap.get(hashKeyF);
//                    port = mappingPorts.get(hashKeyF.trim());
//                    System.out.println("Final hashKey dcided "+hashKeyF);
//                    System.out.println("Port is: "+port);
//
//                    return port;
//                }else{
//
//                    for(int l=0;l<compute.size();l++ ){
//
//                        if(compute.get(l)>0){
//                            diff = compute.get(l);
//                            System.out.println("Least diff is: "+diff);
//                            String hashKeyF = hMap.get(diff);
//                            //port = hashMap.get(hashKeyF);
//                            port = mappingPorts.get(hashKeyF.trim());
//                            System.out.println("Final hashKey dcided "+hashKeyF);
//                            System.out.println("Port is: "+port);
//
//                            return port;
//                        }
//
//                        if(l== compute.size()-1){
//                            System.out.println("In the last if!!");
//                            //diff = compute.get(0);
//                            diff = compute.get(l);
//                            System.out.println("Least diff is: "+diff);
//                            String hashKeyF = hMap.get(diff);
//                            // port = hashMap.get(hashKeyF);
//                            port = mappingPorts.get(hashKeyF.trim());
//                            System.out.println("Final hashKey dcided "+hashKeyF);
//                            System.out.println("Port is: "+port);
//
//                            return port;
//                        }
//                    }
//                }
//
//            }
//        }


        //return "none";
    }


    public void insertFun(String key, String value){
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put("key", key+":fromServ");
        values.put("value", value+":fromServ");
        Uri.Builder builder = new Uri.Builder();
        builder.authority("edu.buffalo.cse.cse486586.simpledht.provider");
        builder.scheme("content");
        Uri uri = builder.build();
        System.out.println("Inserting the final priority in content provider");
        resolver.insert(uri, values);

    }


//    public String keyStoreLogic(String hasKey){
//
//        int j=1;
//        String val="";
//        HashMap<String,String> mappingPorts = new HashMap<String, String>();
//
//        String port = "";
//        String str = getHashPorts();
//        System.out.println("HashPorts: "+str);
//        ArrayList <String> finalPorts = new ArrayList<String>();
//        if(str.trim().equals("")){
//            String split[] = new String[hashPorts.size()];
//            for(int i=0;i<hashPorts.size();i++){
//                split[i]= hashPorts.get(i);
//                finalPorts.add(split[i].trim());
//            }
//        }
//        else {
//            str = str.replace("[", "");
//            str = str.replace("]", "");
//            String split[] = str.split(",");
//            for(int i=0;i<split.length;i++){
//               // split[i]= hashPorts.get(i);
//                finalPorts.add(split[i].trim());
//            }
//
//        }
//        String hashList = getHashMaps();
//        System.out.println("Hashlist: "+hashList);
//        if(hashList.trim().equals("")){
//            for(Map.Entry entry : hashMap.entrySet()){
//                mappingPorts.put(entry.getKey().toString(),entry.getValue().toString());
//            }
//
//        }else{
//
//            HashMap<String,String> hash = getMap(hashList);
//            for(Map.Entry entry : hash.entrySet()){
//                mappingPorts.put(entry.getKey().toString(),entry.getValue().toString());
//            }
//        }
//        System.out.println("Mapping port: "+mappingPorts.toString());
//       // HashMap<String,String> hash = getMap(hashList);
//        ArrayList <Integer> compute = new ArrayList<Integer>();
//        HashMap <Integer,String> hMap = new HashMap<Integer, String>();
//
//
//        System.out.println("Hash key received: "+hasKey);
//
//        for(int i=0; i<finalPorts.size();i++){ //hashPorts.size()
//            int value = hasKey.compareTo(finalPorts.get(i));
//            System.out.println("Key store logic value: "+ value);
//            compute.add(value);
//            Collections.sort(compute);
//            hMap.put(value,finalPorts.get(i).trim() );
//            System.out.println("HMAP with diff: "+hMap.toString());
//            System.out.println("Compute: "+compute.toString());
//            //if(value > 1 || value == 0){
//          //  if(i== hashPorts.size()-1){
//
//            if(i== finalPorts.size()-1){
//                int diff = compute.get(0);
//                if(diff >0 || diff ==0 ){
//                    System.out.println("Least diff is: "+diff);
//                    String hashKeyF = hMap.get(diff).trim();
//                    //port = hashMap.get(hashKeyF);
//                    port = mappingPorts.get(hashKeyF.trim());
//                    System.out.println("Final hashKey dcided "+hashKeyF);
//                    System.out.println("Port is: "+port);
//
//                    return port;
//
//                }else if(compute.size()==1){
//
//                    diff = compute.get(0);
//                    System.out.println("Least diff is: "+diff);
//                    String hashKeyF = hMap.get(diff);
//                  //  port = hashMap.get(hashKeyF);
//                    port = mappingPorts.get(hashKeyF.trim());
//                    System.out.println("Final hashKey dcided "+hashKeyF);
//                    System.out.println("Port is: "+port);
//
//                    return port;
//
//                }
//                else{
//
//                    for(int l=0;l<compute.size();l++){
//
//                        if(compute.get(l)>0){
//                            diff = compute.get(l);
//                            System.out.println("Least diff is: "+diff);
//                            String hashKeyF = hMap.get(diff);
//                            //port = hashMap.get(hashKeyF);
//                            port = mappingPorts.get(hashKeyF.trim());
//                            System.out.println("Final hashKey dcided "+hashKeyF);
//                            System.out.println("Port is: "+port);
//
//                            return port;
//                        } if(l== compute.size()-1){
//                            System.out.println("In the last if!!");
//                            diff = compute.get(0);
//                            System.out.println("Least diff is: "+diff);
//                            String hashKeyF = hMap.get(diff);
//                            // port = hashMap.get(hashKeyF);
//                            port = mappingPorts.get(hashKeyF.trim());
//                            System.out.println("Final hashKey dcided "+hashKeyF);
//                            System.out.println("Port is: "+port);
//
//                            return port;
//
//
//                        }
//
//
//
//                    }
////                    while(compute.get(j)>0){
////                        diff = compute.get(j);
////                        System.out.println("Least diff is: "+diff);
////                        String hashKeyF = hMap.get(diff);
////                        //port = hashMap.get(hashKeyF);
////                        port = mappingPorts.get(hashKeyF.trim());
////                        System.out.println("Final hashKey dcided "+hashKeyF);
////                        System.out.println("Port is: "+port);
////
////                        return port;
////
////                    }
////                       j++;
////
////                      if(j== compute.size()){
////                          System.out.println("In the last if!!");
////                          diff = compute.get(0);
////                          System.out.println("Least diff is: "+diff);
////                          String hashKeyF = hMap.get(diff);
////                         // port = hashMap.get(hashKeyF);
////                          port = mappingPorts.get(hashKeyF.trim());
////                          System.out.println("Final hashKey dcided "+hashKeyF);
////                          System.out.println("Port is: "+port);
////
////                          return port;
////
////
////                      }
//
//
//
//                }
//
//                            }
//
//
//
//             /* if(value<0 || value ==0){
//                System.out.println("value became greater or 0");
//                val = hashPorts.get(i);
//                System.out.println("Hash in which to store is: "+ val);
//                port = hashMap.get(val);
//                System.out.println("Port is: "+port);
//                return port;
//
//            }*/
//        }
//
//        return "none";
//
//
//    }

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
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
                socket.setSoTimeout(TIMEOUT);
                ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
                ops.writeObject(msg);
              //  ops.flush();
                count++;
                //ops.writeObject("ping");
                ops.flush();
//                ObjectInputStream ist = new ObjectInputStream(socket.getInputStream());
//                String ack = (String)ist.readObject();
                //if(ack.equals("ack")){
                   // System.out.println("Received ack from avd0");
                    //socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
//                    ops = new ObjectOutputStream(socket.getOutputStream());
//                    ops.writeObject(msg);
//                    ops.flush();

//                //}
//                else{
//                       System.out.println("AVD0 not present!");
//                          firstReq(msg);
////                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(ports));
////                    ops = new ObjectOutputStream(socket.getOutputStream());
////                    ops.writeObject(msg);
////                    ops.flush();
//
//
//                }

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

        Socket socket[] = new Socket[5];
        Socket socket_;
        ObjectOutputStream os;
        ObjectInputStream istream;
        ArrayList<String> mapList = new ArrayList<String>();
        try {

            for (int i = 0; i < arr.length; i++) {
                if(!arr[i].trim().equals("")){

                socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), (Integer.parseInt(arr[i].trim())*2));
                socket_ = socket[i];
                os = new ObjectOutputStream(socket_.getOutputStream());
                os.writeObject("QueryRequest");
                // os.writeObject("QueryRequest");
                os.flush();
                istream = new ObjectInputStream(socket_.getInputStream());
                String str = (String) istream.readObject();
                mapList.add(str);

            }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mapList;
    }



    public String getJoiningPorts(){
        String list="";
        try {
            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
            ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
            ops.writeObject("getPorts");
            ops.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            list= (String) inputStream.readObject();

        }catch (EOFException e){
            return list;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }
    public String getHashPorts(){
        String list="";
        try {
            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
            ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
            ops.writeObject("getHashPorts");
            ops.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            list= (String) inputStream.readObject();

        }catch (EOFException e){
            return list;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
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

}
