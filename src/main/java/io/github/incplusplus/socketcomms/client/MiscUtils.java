package io.github.incplusplus.socketcomms.client;

import io.github.incplusplus.socketcomms.server.enums.RequestMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static io.github.incplusplus.socketcomms.server.RequestUtils.END_OF_HEADER;
import static io.github.incplusplus.socketcomms.server.RequestUtils.getInputStreamAsString;

public class MiscUtils
{
	/**
	 * Extremely rudimentary send method
	 *
	 * @param verb the method to use for the request (GET, POST, etc.)
	 * @param URI  the URI to include in the request
	 * @param host the IP address or hostname of the server
	 * @param port the port of the server
	 * @return the raw content of the server's response
	 * @throws IOException if anything happens to conveniently go wrong
	 */
	public static String sendRequest(RequestMethod verb, String URI, String host, int port) throws IOException
	{
		Socket connectionSocket = new Socket(host, port);
		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		
		outToServer.writeBytes(verb.name() + " " + URI + " HTTP/1.1");
		outToServer.writeBytes(END_OF_HEADER);
		outToServer.close();
		
		String response = getInputStreamAsString(connectionSocket.getInputStream());
		connectionSocket.close();
		return response;
	}
	
	/**
	 * Extremely rudimentary send method
	 *
	 * @param verb the method to use for the request (GET, POST, etc.)
	 * @param URI  the URI to include in the request
	 * @param host the IP address or hostname of the server
	 * @param port the port of the server
	 * @param body the content of the body of the request
	 * @return the raw content of the server's response
	 * @throws IOException if anything happens to conveniently go wrong
	 */
	public static String sendRequest(RequestMethod verb, String URI, String host, int port,
	                                 String body) throws IOException
	{
		Socket connectionSocket = new Socket(host, port);
		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		
		outToServer.writeBytes(verb.name() + " " + URI + " HTTP/1.1");
		outToServer.writeBytes(END_OF_HEADER);
		outToServer.writeBytes(body);
		outToServer.close();
		
		String response = getInputStreamAsString(connectionSocket.getInputStream());
		connectionSocket.close();
		return response;
	}
}
