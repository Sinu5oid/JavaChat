package com.omgtu.ivt.sine.client;

import com.omgtu.ivt.sine.ProjectConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public Client(){
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the server IP to connect\tIP must be like xxx.xxx.xxx.xxx >> ");
        try {
            ip = consoleIn.readLine();
        } catch (IOException e) {
            System.out.println("IO Exception. Closing app...");
            e.printStackTrace();
            System.exit(-1);
        }
        if (ip == null) System.exit(-1);

        try {
            clientSocket = new Socket(ip, ProjectConsts.PORT);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.print("Enter your nickname >> ");
            String nickName = consoleIn.readLine();
            out.println(nickName);

            MessageReceiver receiver = new MessageReceiver();
            receiver.setDaemon(true);
            receiver.start();

            System.out.println("You are connected to "+ ip +"\tType \"exit()\" to terminate session. Have fun :)");
            String str = "";
            while (!str.equals("exit()")) {
                str = consoleIn.readLine();
                out.println(str);
            }

            receiver.setStop();
        } catch (IOException e) {
            System.out.println("IO Exception. Closing app...");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            closeAllConnections();
        }
    }

    private void closeAllConnections(){
        try {
            in.close();
            System.out.println("System.in stream closed");
            out.close();
            System.out.println("Socket out stream closed");
            clientSocket.close();
            System.out.println("Socket closed");
        } catch (Exception e) {
            System.out.println("Connections was not closed. Closing app...");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private class MessageReceiver extends Thread implements Runnable{
        @Override
        public void run() {
            isStopped = false;
            try {
                while (!isStopped) {
                    String buffer = in.readLine();
                    System.out.println(buffer);
                }
            } catch (IOException e) {
                System.out.println("IO Exception. Closing app...");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        public void setStop() {
            isStopped = true;
        }
        private boolean isStopped;
    }

    private BufferedReader consoleIn;
    private BufferedReader in;
    private PrintWriter out = null;
    private String ip = null;
    private Socket clientSocket;

}

