package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    static String [] mPorts = {REMOTE_PORT0, REMOTE_PORT1,REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};
    // static PriorityQueue <Double> priorityQueue = new PriorityQueue<Double>(100,Collections.reverseOrder());
    PriorityQueue <Messages> messageQueue = new PriorityQueue<Messages>(100, new MessagesComparator());
    PriorityQueue <Messages> holdBackQueue = new PriorityQueue<Messages>(100, new MessagesComparator());
    static int sequence=0;
    static int identificationPort;
    static int seq =0;
    static int uniId =0;
    static int cnt1=-1;
    static  int TIMEOUT = 20000;
    int counter0=0,counter1=0,counter2=0,counter3=0,counter4=0;



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
        //  String receivedMessage = editText.getText().toString();


        //Telephony manager code

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portstr = tel.getLine1Number().substring(tel.getLine1Number().length()-4);
        final String myPort = String.valueOf(Integer.parseInt(portstr)*2);

        //here we initialize all the devices count to zero.

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));


       // for (int h =0; h<1000; h++){}


        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,serverSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        final Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          String message = editText.getText().toString() +"\n";
                                          System.out.println("Read from text box: "+ message);
                                          tv.append(message + "\t\n");
                                          editText.setText("");
                                          System.out.println("myPort value!! "+myPort);
                                          identificationPort = Integer.parseInt(myPort);
                                          new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message,myPort);

                                      }
                                  }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    public class ServerTask extends AsyncTask<ServerSocket , String,Void>{

        @Override
        protected Void doInBackground(ServerSocket... serverSockets) {

            ServerSocket serverSocket = serverSockets[0];
            System.out.println("server socket "+ serverSocket);


            Log.d(TAG, "in server");

            System.out.println("in server!");

            Messages msgs,tempMsg;
            String message_to_read = "";
            Socket socket;
            double proposedPriority=0;
            int s =0;
            int cnt=0;
            BufferedReader br;
            OutputStream op;
            PrintWriter writer;

            //ObjectInputStream istream,in;
            //ObjectOutputStream ostream;
            Gson gson = new Gson();
            ArrayList <Messages> listOfMessages = new ArrayList<Messages>();


            while (true){

                try {

                    socket = serverSocket.accept();
                    socket.setSoTimeout(TIMEOUT);
                    System.out.println("in server again!");
                    InputStream ip = socket.getInputStream();
                    InputStreamReader ir = new InputStreamReader(ip);
                    br = new BufferedReader(ir);
                    message_to_read = br.readLine();
                    System.out.println("Message to read: "+message_to_read);
                    /*istream = new ObjectInputStream(socket.getInputStream());
                    message_to_read = (String) istream.readObject();*/

                    //     while((message_to_read!=null) &&(!(message_to_read.equals("")))){
                    msgs = gson.fromJson(message_to_read, Messages.class);
                    int myPort2 = msgs.getPort();
                    String portOut = Integer.toString(myPort2);
                    int uId = msgs.getUniqueId();


                    System.out.println("In server myPort: "+ myPort2);
                    System.out.println("Count value: "+cnt);
                    if(myPort2 == 11108){

                        proposedPriority = (1.0+(cnt*0.1));

                    }else if(myPort2 == 11112){
                        proposedPriority = (2.0+(cnt*0.1));

                    }else if(myPort2 == 11116){
                        proposedPriority = (3.0+(cnt*0.1));

                    }else if(myPort2 ==11120){
                        proposedPriority = (4.0+(cnt*0.1));

                    }else if(myPort2 ==11124){
                        proposedPriority = (5.0+(cnt*0.1));

                    }

                    if(!(msgs.isDeliverable())) { //setting proposed priority

                        msgs.setProposedPriority(proposedPriority);
                        msgs.setDeliverable(false);

                        messageQueue.add(msgs);
                        cnt++;

                        System.out.println("Proposed priority by server: " + proposedPriority + " " + msgs.getSquenceNo() + "." + msgs.getUniqueId() + " time: "+ System.currentTimeMillis());
                        op = socket.getOutputStream();
                        writer = new PrintWriter(op);
                        String sendPriority = gson.toJson(msgs);
                        writer.println(sendPriority);
                        writer.flush();
                        br.close();

                        //ostream = new ObjectOutputStream(socket.getOutputStream());
                        //  ostream.writeObject(sendPriority);
                        // ostream.flush();
                        //    cnt++;
                        // System.out.println("Value of count: "+cnt);
                    }


                    else {

                        System.out.println("Message after priority setting is: " + msgs.toString());
                        System.out.println("Messages priorities: " + msgs.getProposedPriority() + " Port: " + msgs.getPort());


                        Messages messages = fifioCheck(msgs);
                        if (messages.reply == true) {
                            Iterator<Messages> messagesIterator2 = messageQueue.iterator();
                            while (messagesIterator2.hasNext()) {

                                tempMsg = messagesIterator2.next();
                                System.out.println("message queue msgs: " + tempMsg.toString());
                                System.out.println("temp msg value: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId() + " messageQueue peek: " + messageQueue.peek().getSquenceNo() + "." + messageQueue.peek().getUniqueId() + " message queue priorities: " + messageQueue.peek().getProposedPriority()
                                        + " tempMsgPort: " + messageQueue.peek().getPort() + " msgs seq: " + msgs.getSquenceNo() + "." + msgs.getUniqueId() + " msgs port: " + msgs.getPort() + " msgs proposed prio: " + msgs.getProposedPriority() + " msgs final prio " + msgs.getFinalPriority());

                                if (/*tempMsg.getProposedPriority() == msgs.getProposedPriority() &&*/ (tempMsg.getSquenceNo() == msgs.getSquenceNo()) && (tempMsg.getUniqueId() == msgs.getUniqueId()) /*&& tempMsg.getPort() ==msgs.getPort()*/) {
                                    System.out.println("Message is deliverable!with final prio: " + msgs.getFinalPriority() + " Msg UID: " + msgs.getSquenceNo() + "." + msgs.getUniqueId());
                                    System.out.println("#Message is deliverable!with final prio: " + tempMsg.getFinalPriority() + " Msg UID: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId());
                                    //while(tempMsg.getProposedPriority() ==-1 && tempMsg.getSquenceNo() == msgs.getSquenceNo())

                                    messageQueue.remove(tempMsg);
                                    messageQueue.add(msgs);
                                    //  holdBackQueue.add(messageQueue.peek());
                                    System.out.println("msgQueue size: " + messageQueue.size());
                                    System.out.println("Message queue msgs in second if removed:: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId() + " removed prop priorities:" + tempMsg.getProposedPriority() + " removed final priority: " + tempMsg.getFinalPriority() + " added: "
                                            + msgs.getSquenceNo() + "." + msgs.getUniqueId() + " added prop priorities:" + msgs.getProposedPriority() + " added final priority: " + msgs.getFinalPriority());
                                    System.out.println("Message queue msgs in second if msg queue peek: " + messageQueue.peek().toString());
                                    break;

                                }

                            }


                        } else {

                            Messages chkAgain = fifioCheck(holdBackQueue.peek());
                            if (chkAgain.reply) {

                                System.out.println("In hold back queue chk again if: ");

                                Iterator<Messages> messagesIterator2 = messageQueue.iterator();
                                while (messagesIterator2.hasNext()) {

                                    tempMsg = messagesIterator2.next();
                                    System.out.println("message queue msgs: " + tempMsg.toString());
                                    System.out.println("temp msg value: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId() + " messageQueue peek: " + messageQueue.peek().getSquenceNo() + "." + messageQueue.peek().getUniqueId() + " message queue priorities: " + messageQueue.peek().getProposedPriority()
                                            + " final prio msg queue: " + messageQueue.peek().getFinalPriority() + " msgs seq: " + msgs.getSquenceNo() + "." + msgs.getUniqueId() + " msgs port: " + msgs.getPort() + " msgs proposed prio: " + msgs.getProposedPriority() + " msgs final prio " + msgs.getFinalPriority());

                                    // if((tempMsg.getSquenceNo() == msgs.getSquenceNo()) && (tempMsg.getUniqueId() == msgs.getUniqueId())){
                                    if (/*tempMsg.getProposedPriority() == msgs.getProposedPriority() &&*/ (tempMsg.getSquenceNo() == msgs.getSquenceNo()) && (tempMsg.getUniqueId() == msgs.getUniqueId()) /*&& tempMsg.getPort() ==msgs.getPort()*/) {
                                        System.out.println("Message is deliverable!with final prio: " + msgs.getFinalPriority() + " Msg UID: " + msgs.getSquenceNo() + "." + msgs.getUniqueId());
                                        System.out.println("#Message is deliverable!with final prio: " + tempMsg.getFinalPriority() + " Msg UID: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId());
                                        //while(tempMsg.getProposedPriority() ==-1 && tempMsg.getSquenceNo() == msgs.getSquenceNo())

                                        messageQueue.remove(tempMsg);
                                        messageQueue.add(msgs);
                                        holdBackQueue.poll();
                                        System.out.println("msgQueue size: " + messageQueue.size());
                                        System.out.println("Message queue msgs in second if removed:: " + tempMsg.getSquenceNo() + "." + tempMsg.getUniqueId() + " removed prop priorities:" + tempMsg.getProposedPriority() + " removed final priority: " + tempMsg.getFinalPriority() + " added: "
                                                + msgs.getSquenceNo() + "." + msgs.getUniqueId() + " added prop priorities:" + msgs.getProposedPriority() + " added final priority: " + msgs.getFinalPriority());
                                        System.out.println("Message queue msgs in second if msg queue peek: " + messageQueue.peek().toString());
                                        break;

                                    }

                                }

                            } else {
                                System.out.println("Not yet ready:");
                            }

                        }



                    }
                    //once final priority decided, send it to save it

                    if(messageQueue.peek() != null && messageQueue.peek().isDeliverable()){

                        System.out.println("Message queue peek deliverable: "+messageQueue.peek().getSquenceNo()+"."+messageQueue.peek().getUniqueId()+" Final Prio: "+ messageQueue.peek().getFinalPriority());
                        Messages msgToSend = messageQueue.poll();
                        Iterator <Messages> msgItr2 = messageQueue.iterator();

                        publishProgress(msgToSend.getMessage()+":"+msgToSend.getFinalPriority());


                    }

                    //  socket.close();
                    //  istream.close();


                } catch (SocketTimeoutException e){

                    Log.d(TAG, "Socket timeout exception, failed socket: " );
                }

                catch (IOException e) {
                    System.out.println("EOF exception");
                    break;
                    //e.printStackTrace();
                }



            }

            return null;
        }

        protected void onProgressUpdate(String... strings) {
            String received = strings[0];
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.append(received + "\t\n");

            int l= ++cnt1;


            if(!received.trim().equals("")){
                String keyValue[] = received.split(":");
                String value = keyValue[0];
                //  String key = keyValue[1];

                String key = Integer.toString(l);


                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put("key", key);
                values.put("value", value);
                Uri.Builder builder = new Uri.Builder();
                builder.authority("edu.buffalo.cse.cse486586.groupmessenger2.provider");
                builder.scheme("content");
                Uri uri = builder.build();
                System.out.println("Inserting the final priority in content provider");
                resolver.insert(uri, values);
                System.out.println("Message received in publish progress "+received +" key: "+key);

            }
            // System.out.println();
            return;


        }

        public Messages fifioCheck(Messages msgs){
            System.out.println("Checking FIFO!!");

            if(msgs.getSquenceNo() ==1 && (msgs.getInPort() == counter0 +1)){
                msgs.setReply(true);
                counter0++;
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + "Now the counter is updated to: "+counter0);
            } else if(msgs.getSquenceNo() ==2 && (msgs.getInPort() == counter1 +1)){
                msgs.setReply(true);
                counter1++;
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + "Now the counter is updated to: "+counter1);
            }else if(msgs.getSquenceNo() ==3 && (msgs.getInPort() == counter2 +1)){
                msgs.setReply(true);
                counter2++;
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + "Now the counter is updated to: "+counter2);
            }else if(msgs.getSquenceNo() ==4 && (msgs.getInPort() == counter3 +1)){
                msgs.setReply(true);
                counter3++;
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + "Now the counter is updated to: "+counter3);
            }else if(msgs.getSquenceNo() ==5 && (msgs.getInPort() == counter4 +1)){
                msgs.setReply(true);
                counter4++;
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + "Now the counter is updated to: "+counter4);
            }
            else{
                holdBackQueue.add(msgs);
                System.out.println("The msg was from avd seq: "+ msgs.getSquenceNo() + " Message needs to wait: ");
            }
            return msgs;
        }
    }

    public class ClientTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {

            Log.d(TAG, "In client");


            Socket sockets[] = new Socket[5];
            String msgReceived = "";
            ArrayList <Messages> msgList = new ArrayList<Messages>();
            Socket socket;
            //  ObjectInputStream inputStream;
            // ObjectOutputStream outputStream;
            List <Messages> tempMessage = new ArrayList<Messages>();
            System.out.println("!!!!!!!!!!!!!!!!!!");
            seq++;
            System.out.println("sequence check: "+seq);
            msgReceived = strings[0];
            Messages message= null;
            Messages messages1= null;
            BufferedReader in;
            OutputStream os;
            PrintWriter writer;

            int x=0;
            int j=0;
            int v=0;
            int x1=0;
            double finalPriority=0;
            String messages;
            Gson gson = new Gson();


            if(identificationPort == Integer.parseInt(REMOTE_PORT0)){
                sequence = 1;
            }
            else if(identificationPort == Integer.parseInt(REMOTE_PORT1)){
                sequence=2;
            }
            else if(identificationPort == Integer.parseInt(REMOTE_PORT2)){
                sequence=3;
            }
            else if(identificationPort == Integer.parseInt(REMOTE_PORT3)){
                sequence=4;
            }
            else if(identificationPort == Integer.parseInt(REMOTE_PORT4)){
                sequence=5;
            }



            while(j<5){ //5
                try {   //sending receiving logic
                    System.out.println("In for!!");
                    System.out.println("value of uniId " + uniId);
                    sockets[j] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(mPorts[j]));
                    socket = sockets[j];


                    System.out.println("Is the socket reachable?? " + socket.getInetAddress().isReachable(500));

                    x1 = Integer.parseInt(mPorts[j]);
                    socket.setSoTimeout(TIMEOUT);


                    message = new Messages(msgReceived, sequence, uniId, x1, seq);
                    System.out.println("Message read at client: " + msgReceived);
                    System.out.println("@@Sending message with unique ID from client: " + message.getUniqueId());
                    messages = gson.toJson(message);

                    System.out.println("messages string " + messages);

                    System.out.println(sockets[j] + " here!!!");


                    writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println(messages + "\n");


                    // uncomment here!!
                    /*outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(messages);
                    outputStream.flush();

                    inputStream = new ObjectInputStream(socket.getInputStream());
                    String inputMsg = (String) inputStream.readObject();
                    */
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String inputMsg = in.readLine();
                    if ((inputMsg!=null) && (!(inputMsg.equals("")))  ){
                        Messages msg = gson.fromJson(inputMsg, Messages.class);
                        System.out.println("Received proposed priority at client: " + msg.getProposedPriority() + " ID is " + msg.getSquenceNo() + "." + msg.getUniqueId());


                        if (j == 0)
                            msgList.add(msg);
                        else {

                            System.out.println("Size of msgList: " + msgList.size());
                            for (Messages msgL : msgList) {

                                if (msg.getProposedPriority() > msgL.getProposedPriority()) {
                                    msgList.remove(msgL);
                                    msgList.add(msg);
                                    if (j == 4) {
                                        finalPriority = msg.getProposedPriority();
                                        System.out.println("@@Fina; Prio: " + finalPriority);
                                        msg.setFinalPriority(finalPriority);
                                        msg.setDeliverable(true);
                                        msg.setProposedPriority(msg.getProposedPriority());
                                        msgList.remove(msgL);
                                        msgList.add(msg);
                                        break;
                                    }
                                } else {
                                    if (j == 4) {
                                        Messages msgSend = msgL;
                                        msgSend.setFinalPriority(msgL.getFinalPriority());
                                        msgSend.setDeliverable(true);
                                        msgSend.setProposedPriority(msgL.getProposedPriority());
                                        break;
                                    }
                                }

                            }


                            //    socket.close();
                        }
                    }

                    j++;
                    System.out.println("j: "+j);


                }catch (SocketTimeoutException e){
                    j++;

                    Log.d(TAG, "Socket timeout exception, failed socket: "+ x1 );
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }



            uniId++;


            // once final priority is decided, we can send it:
            for(Messages messg:msgList ) {
                if (messg.isDeliverable()) { //msgList.get(4).isDeliverable()
                    System.out.println("Message is now deliverable! " + " Final Priority: " + messg.getFinalPriority() + " Message: " + messg.getMessage());

                    for (int f = 0; f < 5; f++) {  //f<5
                        try {
                            sockets[f] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(mPorts[f]));
                            // sockets[f].setSoTimeout(TIMEOUT);
                        //    messg.setPort(Integer.parseInt(mPorts[f]));
                            String finalMessage = gson.toJson(messg);
                            PrintWriter  writer1 = new PrintWriter(sockets[f].getOutputStream(),true);
                            writer1.println(finalMessage+ "\n");



                            // ObjectOutputStream ostream3 = new ObjectOutputStream(sockets[f].getOutputStream());
                            //  Messages messg = new Messages(msgList.get(4));
                            //finalPriority = messg.getFinalPriority();

                            //msg2 = messg;
                            //messg.setFinalPriority(finalPriority);
                            //msg2.setDeliverable(true);
                            //msg2.setPort(Integer.parseInt(mPorts[f]));

                            //  ostream3.writeObject(finalMessage);
                            // ostream3.flush();
                            // sockets[f].close();

                        } catch (UnknownHostException e) {
                            Log.d(TAG, "Unknown host exception: ");

                        } catch (SocketTimeoutException e) {

                            Log.d(TAG, "Socket timeout exception while  sending finL priority, failed socket: " + mPorts[f]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }




            return null;
        }
    }


    public class MessagesComparator implements Comparator<Messages>{

        @Override
        public int compare(Messages lhs, Messages rhs) {

            if(lhs.getFinalPriority()> rhs.getFinalPriority()){
                return -1;
            }
            else if(lhs.getFinalPriority() < rhs.getFinalPriority()){
                return 1;
            }
           /* if(lhs.getProposedPriority()> rhs.getProposedPriority()){
                return -1;
            }
            else if(lhs.getProposedPriority() < rhs.getProposedPriority()){
                return 1;
            }
            return secondCompare(lhs, rhs);*/

              return 0;
        }
        public int secondCompare(Messages lhs, Messages rhs){

            if(lhs.getProposedPriority()> rhs.getProposedPriority()){
                return -1;
            }
            else if(lhs.getProposedPriority() < rhs.getProposedPriority()){
                return 1;
            }
            return 0;
        }
    }

    public class SendingComparator implements Comparator<Messages>{

        @Override
        public int compare(Messages lhs, Messages rhs) {

            if(lhs.getFinalPriority()> rhs.getFinalPriority()){
                return 1;
            }
            else if(lhs.getFinalPriority() < rhs.getFinalPriority()){
                return -1;
            }
            return 0;
        }
    }


}

