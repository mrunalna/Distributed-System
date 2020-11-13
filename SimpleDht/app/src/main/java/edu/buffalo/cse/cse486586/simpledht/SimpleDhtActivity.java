package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SimpleDhtActivity extends Activity {

    static final int SERVER_PORT = 10000;
    static ArrayList<String> jPorts = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);

        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        final EditText editText = (EditText) findViewById(R.id.editText1);


        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portstr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf(Integer.parseInt(portstr) * 2);


        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }
           // if(!myPort.equals("11108")){
                String msg = "JRF:" + myPort;
                jPorts.add(myPort);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg,myPort);
//          try {
//            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
//            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
//            os.writeObject("Join request from: " + myPort);
//            os.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
   // }

        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));

        final Button sendButton = (Button) findViewById(R.id.button4);
        sendButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String message = editText.getText().toString() +"\n";
                                              System.out.println("Send button pressed message is: "+message);
                                              System.out.println("myPort string: "+myPort);
                                             // tv.append(message + "\t\n");
                                              editText.setText("");
                                              new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message,myPort);
                                          }
                                      }

        );

        final Button gDump = (Button) findViewById(R.id.button2);
        gDump.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String message = "*";
                                         System.out.println("GDUMP!!");
                                         new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message, myPort);
                                     }
                                 }

        );

      //  final Button lDump = (Button) findViewById(R.id.button1);
        final Button lDump = (Button) findViewById(R.id.button1);
        lDump.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String message = "@";
                                         System.out.println("LDUMP!!");
                                         new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message, myPort);
                                     }
                                 }

        );

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

    public class ServerTask extends AsyncTask<ServerSocket , String,Void>{

        @Override
        protected Void doInBackground(ServerSocket... serverSockets) {

            while (true){
                ServerSocket serverSocket = serverSockets[0];
                SimpleDhtProvider provider = new SimpleDhtProvider();
                String msg= provider.serverSocketCreate(serverSocket);
                System.out.println("Msg at server: "+msg);
                publishProgress(msg);


            }
        }

        protected void onProgressUpdate(String... strings) {

            String received = strings[0];
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.append(received+"\t\n");
            ContentResolver resolver = getContentResolver();
            if(!received.equals("")){
                String rec[] = received.split(",");
                String key = rec[0];
                String value = rec[1];
                System.out.println("Key value pair is: "+key+","+value);
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
            return;
        }




    }

    public class ClientTask extends AsyncTask<String,Void,Void>{



        @Override
        protected Void doInBackground(String... strings) {

            String msgRec = strings[0];

            System.out.println("Strings[1] "+strings[1]);

            SimpleDhtProvider simpleDhtProvider = new SimpleDhtProvider();
            if(msgRec.contains("JRF")){
                String []splits = msgRec.split(":");
                String por= splits[1];
                System.out.println("Join req received");
                System.out.println("Jports size: "+jPorts.size());
                System.out.println("por: "+por);

                if(por.equals("11108")) {

                    simpleDhtProvider.clientJoinRequest(msgRec);
                }
                else{
                   boolean val= simpleDhtProvider.checkPingAck(msgRec);
                   if(val){
                       simpleDhtProvider.clientJoinRequest(msgRec);

                   }
                   else{
                       simpleDhtProvider.firstReq(msgRec);
                   }
                }


//                if((count==0) && !(por.equals("11108"))){
//                    System.out.println("Does not contain avd0 "+count);
//                    simpleDhtProvider.firstReq(msgRec);
//                    count++;
//                }else{
//                    System.out.println("Does cointain avd0 "+count);
//                    simpleDhtProvider.clientJoinRequest(msgRec);
//                    count++;
//                }

            }else if(msgRec.contains("*")){

                ContentResolver resolver = getContentResolver();
                Uri.Builder builder = new Uri.Builder();
                builder.authority("edu.buffalo.cse.cse486586.simpledht.provider");
                builder.scheme("content");
                Uri uri = builder.build();
                Cursor curosr =resolver.query(uri,null,"*",null,null);
                 while(curosr.moveToNext()){
                     System.out.println("key:value: "+curosr.getString(0)+" : "+curosr.getString(1));

                 }



            }else if(msgRec.equals("@")){

                ContentResolver resolver = getContentResolver();
                Uri.Builder builder = new Uri.Builder();
                builder.authority("edu.buffalo.cse.cse486586.simpledht.provider");
                builder.scheme("content");
                Uri uri = builder.build();
                Cursor curosr =resolver.query(uri,null,"@",null,null);
                while(curosr.moveToNext()){
                    System.out.println("key:value: "+curosr.getString(0)+" : "+curosr.getString(1));

                }

            }

            else {
               // jPorts.add(strings[1]);
                System.out.println("Str[1] " + strings[1]);
                simpleDhtProvider.clientTask(msgRec);
            }



            return null;
        }
    }

}
