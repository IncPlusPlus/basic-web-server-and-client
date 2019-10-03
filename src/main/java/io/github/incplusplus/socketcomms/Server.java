package io.github.incplusplus.socketcomms;

import io.github.incplusplus.socketcomms.enums.RequestMethod;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static io.github.incplusplus.socketcomms.StupidSimpleLogger.log;

public class Server
{
	private ServerSocket socket;
	
	void start(int port) throws IOException
	{
		socket = new ServerSocket(port);
		System.out.println("Ready and waiting!");
		while (true)
		{
			new ClientHandler(socket.accept()).start();
		}
	}
	
	public void stop() throws IOException
	{
		socket.close();
	}
	
	private static class ClientHandler extends Thread
	{
		private Socket connectionSocket;
		
		ClientHandler(Socket currentConnection)
		{
			this.connectionSocket = currentConnection;
		}
		
		public void run()
		{
			try
			{
				InputStream inputStream = connectionSocket.getInputStream();
				BufferedReader inFromClient = new BufferedReader(
						new StringReader(RequestUtils.getRawRequest(inputStream)));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				
				List<String> headerLines = RequestUtils.getHeaderLines(inFromClient);
				String body = RequestUtils.getBody(inFromClient);
				log("RECEIVED: ");
				headerLines.forEach(StupidSimpleLogger::log);
				log("\nBody:");
				log(body);
				
				operateOnRequest(connectionSocket, outToClient, headerLines, body);
				log("done sending!!!!!!!!!");
				connectionSocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("FATAL ERROR. AN ERROR ESCAPED OUT INTO THE THREAD.RUN() METHOD");
			}
		}
	}
	
	private static void performGet(Socket connectionSocket, DataOutputStream outToClient, String URI) throws IOException
	{
		//Test endpoint
		if (URI.equals("/"))
		{
			outToClient.writeBytes("HTTP/1.1 200 OK" + RequestUtils.END_OF_HEADER);
			connectionSocket.close();
			return;
		}
		File desiredFile = new File(URI);
		//Don't use a file that's bigger than 2GB please!
		try
		{
			outToClient.write(Files.readAllBytes(desiredFile.toPath()));
		}
		//Send a 404 if the file doesn't exist
		catch (NoSuchFileException e)
		{
			outToClient.writeBytes("HTTP/1.1 404 Not Found" + RequestUtils.END_OF_HEADER);
			connectionSocket.close();
			e.printStackTrace();
		}
		//If something else happens, don't bother
		catch (IOException e)
		{
			outToClient.writeBytes("HTTP/1.1 500 Internal Server Error");
			connectionSocket.close();
			e.printStackTrace();
		}
		finally
		{
			connectionSocket.close();
		}
	}
	
	private static void operateOnRequest(Socket connectionSocket, DataOutputStream outToClient,
	                                     List<String> headerLines,
	                                     String body) throws IOException
	{
		RequestUtils.validateFirstHeaderLine(headerLines);
		String[] firstLineElements = headerLines.get(0).split("\\s");
		RequestMethod verb = RequestMethod.valueOf(firstLineElements[0]);
		String URI = firstLineElements[1];
		//we don't care about the http version for this quick implementation
		//therefore we won't record it
		switch (verb)
		{
			case GET:
				performGet(connectionSocket, outToClient, URI);
				break;
			default:
				outToClient.writeBytes("HTTP/1.1 501 Not Implemented");
				connectionSocket.close();
		}
	}
}
