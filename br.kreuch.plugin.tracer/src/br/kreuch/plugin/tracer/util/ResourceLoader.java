package br.kreuch.plugin.tracer.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class ResourceLoader {
	public static Map<String, String> getProperties(String fileName){
		BufferedReader br = null;

		try {
			String line;
			br = new BufferedReader(new FileReader(fileName));

			Map<String, String> result = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				String key = line.split("=")[0];
				String value = line.split("=")[1];

				JOptionPane.showMessageDialog(null, key + " = " + value);
				result.put(key, value);
			}
			
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
