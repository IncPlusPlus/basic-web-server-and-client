package io.github.incplusplus.socketcomms.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestUtils
{
	final static String OK_HEADER_LINE = "HTTP/1.1 200 OK";
	final static String HEADER_CRLF = "\r\n";
	public final static String END_OF_HEADER = HEADER_CRLF + HEADER_CRLF;
	final static String CONNECTION_CLOSE = "Connection: close";
	static String exampleResponseHeader =
			OK_HEADER_LINE
					+ HEADER_CRLF
					+ "Date: " + getDateTimeString()
					+ HEADER_CRLF
					+ CONNECTION_CLOSE
					+ END_OF_HEADER;
	private final static String HEADER_FIRST_LINE_REGEX = "(?:GET|PUT|POST|DELETE) (.*?) \\S+";
	
	static List<String> getHeaderLines(BufferedReader inFromClient) throws IOException
	{
		List<String> headerLines = new ArrayList<>();
		String currentLine;
		while ((currentLine = inFromClient.readLine()) != null)
		{
			//if we've reached the 2nd of the newlines. This must be the end of the header section
			if (currentLine.isEmpty())
			{
				break;
			}
			headerLines.add(currentLine);
		}
		return headerLines;
	}
	
	static String getBody(BufferedReader inFromClient) throws IOException
	{
		List<String> lines = new ArrayList<>();
		String currentLine;
		StringBuilder body = new StringBuilder();
		while ((currentLine = inFromClient.readLine()) != null)
		{
			//if we've reached the 2nd of the newlines. This must be the end of the header section
			if (currentLine.isEmpty())
			{
				break;
			}
			lines.add(currentLine);
		}
		for (int i = 0; i < lines.size(); i++)
		{
			body.append(lines.get(i));
			//If there is still another line below this one
			if (i < lines.size() - 1)
			{
				//Although this may replace newlines which were originally \r\n. I don't think it's a huge deal
				body.append("\n");
			}
		}
		return body.toString();
	}
	
	public static String getInputStreamAsString(InputStream is) throws IOException
	{
		int c;
		StringBuilder raw = new StringBuilder();
		do
		{
			c = is.read();
			raw.append((char) c);
		} while (is.available() > 0);
		return raw.toString();
	}
	
	static void validateFirstHeaderLine(List<String> headerLines)
	{
		assert headerLines.get(0).matches(HEADER_FIRST_LINE_REGEX);
	}
	
	static String getDateTimeString()
	{
		return DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O")
				.format(ZonedDateTime.now());
	}
}
