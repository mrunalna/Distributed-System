package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerProvider.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    private static Uri mUri;
    final String authority = "edu.buffalo.cse.cse486586.groupmessenger1.provider";
    final String scheme = "content";
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        final EditText editText = (EditText) findViewById(R.id.editText1);
        String received = editText.getText().toString();

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {

            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }




        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

          final Button button = (Button) findViewById(R.id.button4);
          button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  String message = editText.getText().toString();
                  tv.append(message + "\t\n");
                  editText.setText("");
                  System.out.println("My Port "+myPort);

                  new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,message,myPort);
              }
          });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    public class ServerTask extends AsyncTask<ServerSocket, String, Void> {


        @Override
        protected Void doInBackground(ServerSocket... serverSockets) {

            ServerSocket serverSocket = serverSockets[0];
            Log.d(TAG, "In server");
            System.out.println("started server");
            String message_toRead;
            Socket socket;
            try {
                while (true) {
                    socket = serverSocket.accept();
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    message_toRead = (String)inputStream.readObject();
                    publishProgress(message_toRead);
                    System.out.println("message read "+message_toRead);
                    }
                    //socket.close();


                } catch (StreamCorruptedException ex) {
                ex.printStackTrace();
            }   catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;


        }

        protected void onProgressUpdate(String... strings) {

            String receivedString = strings[0].trim();
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.append(receivedString + "\t\n");


            boolean flag = false;
            String final_key;
            String final_value;
            String trim_value = receivedString.toString().trim();
            char whiteSpace[] = trim_value.toCharArray();
            for(int j=0;j<whiteSpace.length;j++){
                if(whiteSpace[j] == ' '){
                    flag = true;
                    break;
                }
            }
            if(flag == true){

                System.out.println("is true activity");
                String valuesInit[] = receivedString.toString().split(" ");
                final_key = valuesInit[0].split("=")[1];
                final_value = valuesInit[1].split("=")[1];
                System.out.println("final Key "+final_value);
                System.out.println("final value "+final_key);

            }
            else{
                final_key = receivedString.toString();
                final_value ="";
            }

            //  System.out.println("Content resolver chk " +getContentResolver().toString());

            ContentValues values = new ContentValues();
            values.put(GroupMessengerProvider.KEY, final_key);
            values.put(GroupMessengerProvider.VALUE, final_value);
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            mUri= uriBuilder.scheme(scheme).build();

            resolver = getContentResolver();
            // Uri uri = getContentResolver().insert(mUri,values);
            resolver.insert(mUri,values);
            return;
        }
    }

        public class ClientTask extends AsyncTask<String, Void, Void>{


            @Override
            protected Void doInBackground(String... strings) {

                System.out.println("In client");

                String remotePort = REMOTE_PORT0;
                String message;
                Socket socket1;
                Socket socket2;
                Socket socket3;
                Socket socket4;
                try {
                if(strings[1].equals(REMOTE_PORT0))
                {
                    remotePort = REMOTE_PORT1;
                    socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket1);
                    System.out.println("Message from socket1 sent");
                    System.out.println("Client Port "+socket1.getPort());
                    remotePort = REMOTE_PORT2;
                    socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket2);
                    System.out.println("Message from socket2 sent");
                    remotePort = REMOTE_PORT3;
                    socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket3);
                    System.out.println("Message from socket3 sent");
                    remotePort = REMOTE_PORT4;
                    socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket4);
                    System.out.println("Message from socket4 sent");



                }
                else if(strings[1].equals(REMOTE_PORT1)){
                    remotePort = REMOTE_PORT0;
                    socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket1);
                    System.out.println("Message from socket1 sent");
                    System.out.println("Client Port "+socket1.getPort());
                    remotePort = REMOTE_PORT2;
                    socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket2);
                    System.out.println("Message from socket2 sent");
                    remotePort = REMOTE_PORT3;
                    socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket3);
                    System.out.println("Message from socket3 sent");
                    remotePort = REMOTE_PORT4;
                    socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket4);
                    System.out.println("Message from socket4 sent");


                } else if(strings[1].equals(REMOTE_PORT2)){
                    remotePort = REMOTE_PORT0;
                    socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket1);
                    System.out.println("Message from socket1 sent");
                    System.out.println("Client Port "+socket1.getPort());
                    remotePort = REMOTE_PORT1;
                    socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket2);
                    System.out.println("Message from socket2 sent");
                    remotePort = REMOTE_PORT3;
                    socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket3);
                    System.out.println("Message from socket3 sent");
                    remotePort = REMOTE_PORT4;
                    socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket4);
                    System.out.println("Message from socket4 sent");

                }
                else if(strings[1].equals(REMOTE_PORT3)){

                    remotePort = REMOTE_PORT0;
                    socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket1);
                    System.out.println("Message from socket1 sent");
                    System.out.println("Client Port "+socket1.getPort());
                    remotePort = REMOTE_PORT1;
                    socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket2);
                    System.out.println("Message from socket2 sent");
                    remotePort = REMOTE_PORT2;
                    socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket3);
                    System.out.println("Message from socket3 sent");
                    remotePort = REMOTE_PORT4;
                    socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket4);
                    System.out.println("Message from socket4 sent");

                }
                else{

                    remotePort = REMOTE_PORT0;
                    socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket1);
                    System.out.println("Message from socket1 sent");
                    System.out.println("Client Port "+socket1.getPort());
                    remotePort = REMOTE_PORT1;
                    socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket2);
                    System.out.println("Message from socket2 sent");
                    remotePort = REMOTE_PORT2;
                    socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket3);
                    System.out.println("Message from socket3 sent");
                    remotePort = REMOTE_PORT3;
                    socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));
                    sendMessage(strings[0],socket4);
                    System.out.println("Message from socket3 sent");


                }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public void sendMessage(String msg , Socket socket){

               try {
                    ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                    // message = strings[0];
                    System.out.println("message " + msg);
                    Log.d(TAG, "Sending message started");
                    outStream.writeObject(msg);
                    outStream.flush();


                    //content provider code!

                   boolean flag = false;
                   String final_key;
                   String final_value;
                   String trim_value = msg.toString().trim();
                   char whiteSpace[] = trim_value.toCharArray();
                   for(int j=0;j<whiteSpace.length;j++){
                       if(whiteSpace[j] == ' '){
                           flag = true;
                           break;
                       }
                   }
                   if(flag == true){

                       System.out.println("is true activity");
                       String valuesInit[] = msg.toString().split(" ");
                       final_key = valuesInit[0].split("=")[1];
                       final_value = valuesInit[1].split("=")[1];
                       System.out.println("final Key "+final_value);
                       System.out.println("final value "+final_key);

                   }
                   else{
                       final_key = msg.toString();
                       final_value ="";
                   }

                   //  System.out.println("Content resolver chk " +getContentResolver().toString());

                   ContentValues values = new ContentValues();
                   values.put(GroupMessengerProvider.KEY, final_key);
                   values.put(GroupMessengerProvider.VALUE, final_value);
                   Uri.Builder uriBuilder = new Uri.Builder();
                   uriBuilder.authority(authority);
                   mUri= uriBuilder.scheme(scheme).build();

                   resolver = getContentResolver();
                   // Uri uri = getContentResolver().insert(mUri,values);
                   resolver.insert(mUri,values);


                } catch (IOException e) {
                   e.printStackTrace();
               }


            }
        }

}


        /*InputStreamReader reader = new InputStreamReader(socket.getInputStream());
        BufferedReader reader1 = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        while ((message_toRead = reader1.readLine()) != null) {
        System.out.println("Message " + message_toRead);

        buffer.append(message_toRead);
        publishProgress(message_toRead);
*/