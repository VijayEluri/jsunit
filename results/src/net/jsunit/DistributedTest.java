package net.jsunit;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sun.net.www.protocol.http.HttpURLConnection;

public class DistributedTest extends TestCase {
	public static final String PROPERTY_REMOTE_MACHINE_NAMES = "remoteMachineNames";
	public DistributedTest(String name) {
		super(name);
	}
	public void testCollectResults() {
		Iterator it = remoteMachineNames().iterator();
		while (it.hasNext()) {
			String next = (String) it.next();
			String result = submitRequestTo(next);
			try {
				Document document = new SAXBuilder().build(new StringReader(result));
				Element resultElement = document.getRootElement();
				if (!"result".equals(resultElement.getName()))
					fail("Unrecognized response from " + next + ": " + result);
				assertEquals(next + " failed", "success", resultElement.getText());
			} catch (Exception e) {
				e.printStackTrace();
				fail("Could not parse XML response from " + next + ": " + result);
			}
		}
	}
	private String submitRequestTo(String next) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL("http://" + next + "/jsunit/runner");
			connection = new HttpURLConnection(url, "", 0);
			InputStream in = connection.getInputStream();
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			in.close();
			return new String(buffer);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Could not submit request to " + next);
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
	private List remoteMachineNames() {
		String remoteMachineNamesCommaDelimitedList = JsUnitServer.instance().jsUnitProperties().getProperty(PROPERTY_REMOTE_MACHINE_NAMES);
		return Utility.listFromCommaDelimitedString(remoteMachineNamesCommaDelimitedList);
	}
}