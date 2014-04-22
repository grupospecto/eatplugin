package br.kreuch.plugin.tracer.factory;

import java.util.Map;

import br.kreuch.plugin.tracer.util.ResourceLoader;

public class I18nConstantsFactory {
	public final static String CLASS_NOT_FOUND_IN_EA="CLASS_NOT_FOUND_IN_EA";
	public final static String SELECT_A_FILE="SELECT_A_FILE";
	public final static String USE_CASES="USE_CASES";
	public final static String INFORMATION="INFORMATION";
	
	private static Map<String, String> I18N_CONSTANTS;
	@SuppressWarnings("unused")
	private static Map<String, String> getProperties(){
		if (I18N_CONSTANTS == null){
			I18N_CONSTANTS = ResourceLoader.getProperties("src/resources/i18n.properties");
		}
		
		return I18N_CONSTANTS;
	}
	
	public static String getI18nConstantFor(String key){
		return I18N_CONSTANTS.get(key);
	}
}
