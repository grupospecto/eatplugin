package br.com.specto.plugin.tracer.factory;

import java.util.Properties;

import br.com.specto.plugin.tracer.util.ResourceLoader;

public class I18nConstantsFactory {
	public final static String CLASS_NOT_FOUND_IN_EA="CLASS_NOT_FOUND_IN_EA";
	public final static String SELECT_A_FILE="SELECT_A_FILE";
	public final static String USE_CASES="USE_CASES";
	public final static String INFORMATION="INFORMATION";
	
	private static Properties I18N_CONSTANTS;

	private static Properties getProperties(){
		if (I18N_CONSTANTS == null){
			I18N_CONSTANTS = ResourceLoader.getProperties("resources/i18n.properties");
		}
		
		return I18N_CONSTANTS;
	}
	
	public static String getI18nConstantFor(String key){
		String result = (String) I18nConstantsFactory.getProperties().get(key);
		
		if (result == null){
			result = "No value for key '" + key + "'";
		}
		
		return result;
	}
}
