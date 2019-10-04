package io.github.incplusplus.socketcomms.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerDriver
{
	final static int port = 1234;
	
	public static void main(String[] args) throws IOException
	{
		Scanner in = new Scanner(System.in);
		//Set up my custom logging implementation
		StupidSimpleLogger.enable();
		
		Server myDefaultServer = new Server();
		myDefaultServer.start(port);
		System.out.println("Server started on port " + port + ".");
		System.out.println("Hit enter to stop the server.");
		/*
		 * Wait for newline from user.
		 * This call will block the main thread
		 * until the user hits enter in the console.
		 * This is because the server runs on a daemon thread.
		 * This feels like a cleaner way than having a while(true){}
		 * on the main thread.
		 */
		in.nextLine();
		System.out.println("Server stopped.");
	}
}
