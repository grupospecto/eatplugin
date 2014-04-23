package br.com.specto.plugin.tracer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import br.com.specto.plugin.tracer.Activator;

public class ResourceLoader {
	public static Properties getProperties(String resourceName){		
		Properties properties = new Properties();
		URL url = Activator.getDefault().getBundle().getEntry(resourceName);
		InputStream stream;
		try {
			stream = url.openStream();
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return properties;
	}
}
