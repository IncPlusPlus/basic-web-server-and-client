package io.github.incplusplus.socketcomms.server;

import io.github.incplusplus.socketcomms.server.enums.RequestMethod;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Objects;

import static io.github.incplusplus.socketcomms.server.StupidSimpleLogger.log;

public class Server
{
	private ServerSocket socket;
	
	void start(int port)
	{
		class ServerStartTask implements Runnable
		{
			int port;
			
			ServerStartTask(int p) { port = p; }
			
			public void run()
			{
				while (true)
				{
					try
					{
						socket = new ServerSocket(port);
						System.out.println("Ready and waiting!");
						new ClientHandler(socket.accept()).start();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						System.out.println(
								"FATAL ERROR. AN ERROR ESCAPED OUT INTO THE MAIN SERVER'S THREAD.RUN() METHOD");
					}
					finally
					{
						System.out.println("Server shutting down");
						try
						{
							socket.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
							System.out.println("FATAL ERROR. THE SERVER ENCOUNTERED AN ERROR DURING SHUTDOWN");
						}
					}
				}
			}
		}
		Thread t = new Thread(new ServerStartTask(port));
		t.setDaemon(true);
		t.start();
	}
	
	public void stop()
	{
		System.out.println("Server shutting down");
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("FATAL ERROR. THE SERVER ENCOUNTERED AN ERROR DURING SHUTDOWN");
		}
	}
	
	private class ClientHandler extends Thread
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
				/* TODO:
				 * So now the server and client work in tandem.
				 * Thing is, I don't know how to properly block without being vulnerable to DOS attacks.
				 * Because of this, I searched for alternatives to blocking and found the do{}while()
				 * loop solution that's in RequestUtils.getInputStreamAsString. Because of this,
				 * ClientHandler's run() method will proceed before the client has had the chance to finish talking.
				 *
				 * This is something that could be greatly improved and make this project stable
				 * enough for everyday usage.
				 */
				InputStream inputStream = connectionSocket.getInputStream();
				BufferedReader inFromClient = new BufferedReader(
						new StringReader(RequestUtils.getInputStreamAsString(inputStream)));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				
				List<String> headerLines = RequestUtils.getHeaderLines(inFromClient);
				String body = RequestUtils.getBody(inFromClient);
				log("RECEIVED: ");
				headerLines.forEach(StupidSimpleLogger::log);
				log("\nBody:");
				log(body);
				
				operateOnRequest(connectionSocket, outToClient, headerLines, body);
				log("Done servicing client!");
				inFromClient.close();
				outToClient.close();
				connectionSocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("FATAL ERROR. AN ERROR ESCAPED OUT INTO THE CLIENT HANDLER'S THREAD.RUN() METHOD");
			}
		}
	}
	
	private void performGet(Socket connectionSocket, DataOutputStream outToClient, String URI) throws IOException
	{
		//Test endpoint
		if (URI.equals("/"))
		{
			outToClient.writeBytes("HTTP/1.1 200 OK" + RequestUtils.END_OF_HEADER);
			connectionSocket.close();
			return;
		}
		try
		{
			log("Getting resource with URI: " + URI);
			InputStream requestedResource = getClass().getResourceAsStream(URI);
			if (Objects.isNull(requestedResource)) {throw new NoSuchFileException(URI);}
			outToClient.writeBytes("HTTP/1.1 200 OK" + RequestUtils.END_OF_HEADER);
			requestedResource.transferTo(outToClient);
		}
		//Send a 404 if the file doesn't exist
		catch (NoSuchFileException | NullPointerException e)
		{
			outToClient.writeBytes("HTTP/1.1 404 Not Found" + RequestUtils.END_OF_HEADER);
			connectionSocket.close();
			e.printStackTrace();
		}
		//If something else happens, don't bother
		catch (IOException e)
		{
			outToClient.writeBytes("HTTP/1.1 500 Internal Server Error" + RequestUtils.END_OF_HEADER);
			connectionSocket.close();
			e.printStackTrace();
		}
		finally
		{
			connectionSocket.close();
		}
	}
	
	private void operateOnRequest(Socket connectionSocket, DataOutputStream outToClient,
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
