package io.github.incplusplus.socketcomms.server;

import java.io.IOException;

public class ServerDriver
{
	public static void main(String[] args) throws IOException
	{
		//Set up my custom logging implementation
		StupidSimpleLogger.enable();
		
		Server myDefaultServer = new Server();
		myDefaultServer.start(1234);
		System.out.println("Server stopped.");
	}
}
