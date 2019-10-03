package io.github.incplusplus.socketcomms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
	public static void main(String[] args) throws IOException
	{
		Socket connectionSocket = new Socket("localhost", 1234);
		
		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		Scanner s = new Scanner(System.in);
		System.out.print("Input lowercase sentence: ");
		String message = s.nextLine();
		
		outToServer.writeBytes(message + "\r\n");
		
		String modifiedMessage = inFromServer.readLine();
		System.out.println("FROM SERVER: " + modifiedMessage);
		
		s.close();
		outToServer.close();
		connectionSocket.close();
	}
}
