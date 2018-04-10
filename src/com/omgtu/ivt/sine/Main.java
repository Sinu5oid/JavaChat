package com.omgtu.ivt.sine;

import com.omgtu.ivt.sine.client.Client;
import com.omgtu.ivt.sine.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        System.out.print("Specify the mode: (C)lient / (S)erver >> ");
        String mode = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                mode = reader.readLine();
            } catch (IOException e) {
                System.out.println("IO Exception. Closing app...");
                e.printStackTrace();
                System.exit(-1);
            }
            if (mode == null)   System.exit(-1);;
            switch (mode.toLowerCase().charAt(0)) {
                case 'c':
                    new Client();
                    System.exit(0);
                    break;
                case 's':
                    new Server();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Wrong mode. Please be accurate while entering the mode.");
            }
        }
    }
}
