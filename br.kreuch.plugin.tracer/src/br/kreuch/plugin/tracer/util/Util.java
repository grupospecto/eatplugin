package br.kreuch.plugin.tracer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Util {
	public static Long getKeyByValue(Map<Long, String> map, String value) {
		Iterator<Long> it = map.keySet().iterator();
		while (it.hasNext()){
			long i = it.next();
			if (value.equals(map.get(i))){
				return i;
			}
		}
		
		return null;
	}
	
	public static String getPackageNameForFilePath(String filePath){
//		final String TAG = "src/";
//		String result = filePath.substring(filePath.indexOf(TAG) + TAG.length());
//		result = result.substring(0, result.indexOf('.')); //removes file extension, generaly '.java'
//		return result.replace('/', '.'); //change folder notation, from slashes to dots
		
		final String TAG = "src";
		
		return filePath.substring(filePath.indexOf(TAG) + TAG.length()).replace('/', '\\');
	}
	
	public static String getListAsString(List<Long> ids){
		return getListAsString(ids, "");
	}
	
	public static String getListAsString(List<Long> ids, String prefix){
		String result = "";
		
		for (Long id : ids){
			result += ", " + prefix + id;
		}
		
		return result.substring(1); //removes first comma
	}
	
	public static List<Long> getStringAsList(String useCaseList){
		/* <UseCases> UC-548, UC-560 </UseCases> */
		
		useCaseList = useCaseList.substring(useCaseList.indexOf("<UseCases>")+10);
		useCaseList = useCaseList.substring(0, useCaseList.indexOf("</UseCases>"));
		useCaseList = useCaseList.trim();
		useCaseList = useCaseList.replace("UC-", "");
		useCaseList = useCaseList.replace(" ", "");
		String[] values = useCaseList.split(",");

		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < values.length; i++){
			result.add(Long.parseLong(values[i]));
		}
		
		return result;
	}
}
