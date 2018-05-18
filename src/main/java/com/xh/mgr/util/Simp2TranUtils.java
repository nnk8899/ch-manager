package com.xh.mgr.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Simp2TranUtils {
	
	public static boolean UI_SIMP2TRAN_ENABLED = "zh_HK".equalsIgnoreCase(System.getProperty("robot.locale"))
			|| "zh_TW".equalsIgnoreCase(System.getProperty("robot.locale"));
	
	public static String simp2tran(String sentence)  {
		if (!UI_SIMP2TRAN_ENABLED) return sentence;
		return simp2tran0(sentence);
	}
	public static String simp2tran0(String sentence)  {
		if (sentence == null) return null;
		int pHead = 0;
		int pTail = maxWordLength;
		StringBuilder ret = new StringBuilder();
		while(pHead < sentence.length()){
			final char ck = sentence.charAt(pHead);
//			if(! set.contains(ck)){
//				ret.append(ck);
//				pTail = (++ pHead) + maxWordLength;
//				continue;
//			}
			if(! check.containsKey(ck)){
				ret.append(ck);
				pTail = (++ pHead) + maxWordLength;
				continue;
			}
			final String cn = check.get(ck);
			if(! cn.equals(EMPTY)){
				ret.append(cn);
				pTail = (++ pHead) + maxWordLength;
				continue;
			}
			
			if(pTail > sentence.length()){
				pTail = sentence.length();
			}
			int l = pTail - pHead;
			boolean fMatched = false;
			for(;l > 0; l --){
				String test = sentence.substring(pHead, pHead + l);
				if(map.containsKey(test)){
					ret.append(map.get(test));
					fMatched = true;
					break;
				}
			}
			if(fMatched){
				pHead += l;
			}else{
				ret.append(sentence.charAt(pHead));
				pHead ++;
			}
			pTail = pHead + maxWordLength;
		}
		return ret.toString();
	}
	
	
	private static String EMPTY = "";
	
	private static Map<String, String> map;

	private static Map<Character, String> check;
	
	private static final char SPLITER = ',';

	private static int maxWordLength;

	static {
		long time = System.currentTimeMillis();
		InputStream stream = Simp2TranUtils.class.getResourceAsStream("big2cn.txt");
		try {
			map = new HashMap<String, String>();
			//		set = new HashSet<Character>();
			check = new HashMap<Character, String>();
			maxWordLength = 0;
			BufferedReader br = null;
			if(stream != null){
				br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
				for(String in = br.readLine(); in != null; in = br.readLine()){
					in = in.trim();
					if(in.startsWith("//") || in.length() == 0){
						continue;
					}
					final int psp = in.indexOf(SPLITER);
					if(psp > 0 && ! in.contains("?")){
						String cn = in.substring(0, psp);
						String big = in.substring(psp + 1);
						if(big.length() > maxWordLength){
							maxWordLength = big.length();
						}
						//				set.add(big.charAt(0));
						
						if(!map.containsKey(big))
							map.put(big, cn);
						check.put(big.charAt(0), cn);
					}
				}
			}
			if(br != null){
				br.close();
			}
			for(Entry<String, String> entry: map.entrySet()){
				final String big = entry.getKey();
				if(big.length() > 1){
					check.put(big.charAt(0), EMPTY);
				}
			}
			time = System.currentTimeMillis() - time;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try{stream.close();}catch(Exception e){}
		}
	}

}
