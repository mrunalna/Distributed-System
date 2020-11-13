package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    private static final String TAG= GroupMessengerProvider.class.getSimpleName();
    static  String VALUE = "value";
    static String KEY = "key";
    private static HashMap<String, String> keyValuePairs;
    private static HashMap<String,String> count_vals;
    private static HashMap<String, String> countKey;
    private static String coloumns[] = {"key","value"};
    static MatrixCursor cursor = new MatrixCursor(coloumns);
    static int i=0;
    public static int x=0;
    static {
        keyValuePairs = new HashMap<String, String>(); //this will store key-value pairs
        count_vals = new HashMap<String, String>(); //this will store count and message
        countKey = new HashMap<String, String>(); //this will store count and key
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */

      //  String msg="";
        String chkMsg="";
        System.out.println("values in insert "+values.toString());
        String filename="";
        String fileContents="";
        boolean flag = false;
        String keyValue = "";
        String final_key= "";
        String final_value;
        String trim_value = values.toString().trim();
        char whiteSpace[] = trim_value.toCharArray();


           for(int j=0;j<whiteSpace.length;j++){
            if(whiteSpace[j] == ' '){
                flag = true;
                break;
            }
        }

        if(flag == true){

            System.out.println("is true in provider 1st");

            String valuesInit[] = values.toString().split(" ");
            if(!(valuesInit[0].equals("value="))) {
                System.out.println("init values 1" + valuesInit[1] + " " + valuesInit[0]);
                System.out.println("init values 0 " + valuesInit[0]);
                final_key = valuesInit[0].split("=")[1];
            }
            final_value = valuesInit[1].split("=")[1];
            System.out.println("final Key "+final_value);
            System.out.println("final value "+final_key);


        }
        else{
            final_value = values.toString();
            final_key ="";
            System.out.println("checking else for init ######");
        }



        String count_name = Integer.toString(i);
        String count = Integer.toString(i);

        String oldValue="";
        String oldKey="";
        String cKey="";

        //code for duplicate values check

        if(keyValuePairs.containsKey(final_value)){
            System.out.println("is duplicate KEY 1st # "+final_value);
           // System.out.println("is duplicate value keypair "+keyValuePairs.get(final_value).toString());
            for(Map.Entry entry : keyValuePairs.entrySet()){
                if(entry.getKey().equals(final_value)){
                    oldValue = entry.getValue().toString();
                    oldKey = entry.getKey().toString();
                    System.out.println("old value: "+oldValue +"NEW VALUE#" + trim_value);
                    System.out.println("old key "+oldKey);
                    break;
                }
            }
              if(countKey.containsValue(oldKey)){

                  for(Map.Entry map: countKey.entrySet()){
                      if(map.getValue().equals(oldKey)){
                          cKey = map.getKey().toString();
                          countKey.remove(cKey);
                          countKey.put(cKey, final_value);
                          break;

                      }
                  }
              }

            for(Map.Entry map : count_vals.entrySet()){
                if(map.getKey().equals(cKey)){
                    System.out.println("yayyyaayyy got the msg!!! " +cKey +" "+ map.getValue().toString());
                    count_vals.remove(cKey);
                    count_vals.put(cKey,trim_value);
                    System.out.println("trim value: "+trim_value);
                    getContext().deleteFile(cKey);
                    filename = cKey;
                   // i --;

                    break;
                }
            }


            keyValuePairs.remove(final_value);
            keyValuePairs.put(final_value,final_key);
            System.out.println("new value in value "+final_key + "key "+final_value);

        System.out.println("i count when keypair dup "+i);
        }
        else {
          ////  System.out.println("is not duplicate value count "+count_vals.get(count).toString());
            count_vals.put(count, values.toString());
            keyValuePairs.put(final_value, final_key);
            countKey.put(count,final_value);
            System.out.println("final value "+final_key);
            filename= count;
            i++;

          //  fileContents = final_value + " " + final_key;
        }



        //Added values to a cursor and kept records of key value map and cursor
        MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
            rowBuilder.add(count);
            rowBuilder.add(values.toString());


      //  cursor.newRow().add(new String[]{count, values.toString()});
       // cursor.newRow().add(values.toString());

      //  System.out.println("#Insert cursor count "+cursor.getCount());
       // filename = count;
        fileContents =values.toString();

        System.out.println("context!!! "+getContext());
        try {
            FileOutputStream outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            System.out.println("#File creation success "+filename);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("insert", values.toString());
        System.out.println("#rows of countval "+(count_vals.size()-1));
        System.out.println("#count "+count);
        System.out.println("#gain after incr # "+i);

        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */

        String matrixValues ="";

 //       System.out.println(cursor.getCount()+ " cursor count");
       MatrixCursor matrixCursor = new MatrixCursor(coloumns);
        MatrixCursor.RowBuilder builder = matrixCursor.newRow();
        boolean flag = false;


       System.out.println("b4 if");
       String valuesV="";
         if(!(isInteger(selection))){

             for (Map.Entry map :keyValuePairs.entrySet()){
                 if(map.getKey().equals(selection)){
                     System.out.println("Key value pair are: " +map.getKey().toString() +" "+ map.getValue());


                     builder.add(map.getKey().toString());
                     builder.add(map.getValue().toString());
                     break;
                 }
             }


         }
         else{
             for (Map.Entry entry : count_vals.entrySet()){

                 if(entry.getKey().equals(selection)){

                     System.out.println("Key value pairs of count vals: " +entry.getKey().toString() +" "+ entry.getValue());
                     String subString[] = entry.getValue().toString().split(" ");
                     String keysV = subString[1].split("=")[1];

                     System.out.println("Keys V ="+keysV);
                     if(!(subString[0].equals("value=")))
                      valuesV = subString[0].split("=")[1];

                     System.out.println("ValuesV "+valuesV);

                    builder.add(selection);
                    builder.add(keysV.toString());//+valuesV.toString());
                     break;
                 }
             }
         }
        Log.v("query", selection);

        System.out.println("matrixCursor count "+matrixCursor.getCount());



        return matrixCursor;
    }

    public static boolean isInteger(String s){
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e){
            return false;
        }catch(NullPointerException e){
            return false;
        }

        return true;

    }

  /*  public HashMap<String,String> replaceDuplicate(HashMap<String,String>map,String key,String Newvalue){

        String value="";
        String newKey = "";

        for(Map.Entry entry: map.entrySet()){

            if(entry.getKey().equals(key)){
                map.remove(key);
                map.put(key,Newvalue);
                //return map;
                break;

            }
        }

        return map;

        }

        public boolean isDuplictae(HashMap<String,String>map,String key){



               for(Map.Entry entry: map.entrySet()){
                   if(entry.getKey().equals(key)){
                       System.out.println("is Duplicate "+key);
                      return true;
                   }

               }

               return false;

        }
*/





    }





