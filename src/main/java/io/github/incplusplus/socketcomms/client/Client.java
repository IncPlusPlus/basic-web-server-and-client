package io.github.incplusplus.socketcomms.client;

import io.github.incplusplus.socketcomms.server.enums.RequestMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.socketcomms.client.MiscUtils.sendRequest;

public class Client
{
	public static void main(String[] args) throws IOException
	{
		String URI = "/HelloWorld.html";
		String host = "localhost";
		int port = 1234;
		System.out.println(sendRequest(RequestMethod.GET,URI,host,port));
//		Socket connectionSocket = new Socket("localhost", 1234);
//
//		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
//		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//
//		Scanner s = new Scanner(System.in);
//		System.out.print("Input lowercase sentence: ");
//		String message = s.nextLine();
//
//		outToServer.writeBytes(message + "\r\n");
//
//		String modifiedMessage = inFromServer.readLine();
//		System.out.println("FROM SERVER: " + modifiedMessage);
//
//		s.close();
//		outToServer.close();
//		connectionSocket.close();
	}
}
