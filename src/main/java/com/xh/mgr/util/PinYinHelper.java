/**
 * 
 */
package com.xh.mgr.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author norman 中文拼音处理程序，将中文词组、字符（包括多音字）转化为拼音
 */
public class PinYinHelper {

	private Map<Character, LinkedList<String>> charMap;

	private Map<String, String> multiMap;

	private static PinYinHelper instance = new PinYinHelper();

	public PinYinHelper() {
		charMap = new LinkedHashMap<Character, LinkedList<String>>();
		multiMap = new HashMap<String, String>();
		try {
			reload();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PinYinHelper getInstance() {
		return instance;
	}

	public synchronized void reload() throws Exception {
		InputStream f1 = PinYinHelper.class.getResourceAsStream("pinyin.txt");
		InputStream f2 = PinYinHelper.class.getResourceAsStream("duoYinZi_ZuCi.txt");
		if (f1 == null)
			throw new IllegalArgumentException("/pinyin.txt not found.");
		if (f2 == null)
			throw new IllegalArgumentException("/duoYinZi_ZuCi.txt not found.");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(f1, "utf-8"));
			String input = "";
			LinkedList<String> spell;
			while ((input = br.readLine()) != null) {
				char ch = input.charAt(0);
				String s = input.substring(2, input.length() - 1);
				if (!charMap.containsKey(ch)) {
					spell = new LinkedList<String>();
					spell.add(s);
					charMap.put(ch, spell);
				} else {
					spell = charMap.get(ch);
					if (!spell.contains(s))
						spell.add(s);
				}
			}
			br.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(f2, "utf-8"));
			String s;
			String ch;
			int t = 0;
			while ((s = in.readLine()) != null) {
				ch = s.substring(0, 1);
				String[] arr = s.substring(4).split("/");
				for (String temp : arr) {
					t = temp.indexOf("]");
					if (t > 0) {
						String py = ch + temp.substring(1, t);
						String word = temp.substring(t + 1).trim();
						if (word.length() > 0)
							multiMap.put(py, word);
					}
				}
			}
			in.close();// 关闭输入流;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f1.close();
			f2.close();
		}
	}

	public String[] gePinYin(String str) {
		int len = str.length();
		String py = "";
		int i;
		List<String> pinyin = new LinkedList<String>();

		for (i = 0; i < len; i++) {
			Character ch = str.charAt(i);
			if (charMap.get(ch) == null) {
				py += ch;
			} else {
				if (py.length() > 0) {
					pinyin.add(py);
					py = "";
				}
				LinkedList<String> L = charMap.get(ch);
				if (L.size() == 1) {
					pinyin.add(L.get(0));
				} else if (L.size() > 1) {
					int j;
					for (j = 0; j < L.size(); j++) {
						String s1 = L.get(j);
						String multiWords = multiMap.get(ch + s1);
						if (multiWords != null) {
							// 在多音字组词表中找匹配的组词
							if (((i + 2) <= str.length() && multiWords.contains(str.substring(i, i + 2)))
									|| ((i >= 1) && multiWords.contains(str.substring(i - 1, i + 1)))) {
								pinyin.add(s1);
								break;
							}
						}
					}
					if (j == L.size()) {
						pinyin.add(L.get(0));
					}
				}
			}

		}
		if (i == len && py.length() > 0) {
			pinyin.add(py);
		}
		return pinyin.toArray(new String[0]);
	}

	public String[] converterToSpell(char c) {
		LinkedList<String> list = charMap.get(c);
		if (list == null)
			return null;
		return list.toArray(new String[0]);
	}

	public PinYin toPinYin(String str) {
		StringBuilder b1 = new StringBuilder();
		StringBuilder b2 = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			Character ch = str.charAt(i);
			LinkedList<String> L = charMap.get(ch);
			if (L == null) {
				b1.append(ch);
				continue;
			}
			if (L.size() > 1) {
				int j;
				for (j = 0; j < L.size(); j++) {
					String s1 = L.get(j);
					String multiWords = multiMap.get(ch + s1);
					if (multiWords != null) {// 在多音字组词表中找匹配的组词
						if (((i + 2) <= str.length() && multiWords.contains(str.substring(i, i + 2)))
								|| ((i >= 1) && multiWords.contains(str.substring(i - 1, i + 1)))) {
							b1.append(s1);
							b1.append(" ");
							b2.append(s1.charAt(0));
							b2.append(" ");
							break;
						}
					}
				}
				if (j == L.size()) {
					b1.append(L.get(0));
					b1.append(" ");
					b2.append(L.get(0).charAt(0));
					b2.append(" ");
				}
			} else {
				b1.append(L.get(0));
				b1.append(" ");
				b2.append(L.get(0).charAt(0));
				b2.append(" ");
			}
		}
		return new PinYin(str, b1.toString().trim(), b2.toString().trim());
	}

	public String[] parse(String word) {
		List<String> ret = new ArrayList<String>();
		char prevMc = word.charAt(0);
		String miniword = "" + prevMc;
		for (int i = 1; i < word.length(); i++) {
			char mc = word.charAt(i);
			if (mc == 'n' && !smSet.contains(prevMc)
					&& (prevMc == 'a' || prevMc == 'o' || prevMc == 'e' || prevMc == 'i')) {
				// do nothing
			} else if (smSet.contains(mc) && !smSet.contains(prevMc) && i != word.length() - 1) {
				if (miniword.length() > 0) {
					ret.add(miniword);
					miniword = "";
				}
			} else if (miniword.endsWith("an") || miniword.endsWith("on") || miniword.endsWith("en")
					|| miniword.endsWith("in")) {
				if (i < word.length() - 1) {
					if (mc != 'g') {
						ret.add(miniword);
						miniword = "";
					}
				}
			} else if (miniword.endsWith("ang") || miniword.endsWith("ong") || miniword.endsWith("eng")
					|| miniword.endsWith("ing")) {
				ret.add(miniword);
				miniword = "";
			} else if (smSet.contains(prevMc) && smSet.contains(mc)) {
				ret.add(miniword);
				miniword = "";
			}
			prevMc = mc;
			miniword += mc;
		}
		if (miniword.length() > 0)
			ret.add(miniword);
		List<String> _ret = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (String s : ret) {
			if (s.length() > 1) {
				if (sb.length() > 0) {
					_ret.add(sb.toString());
					sb.setLength(0);
				}
				_ret.add(s);
			} else
				sb.append(s);
		}
		if (sb.length() > 0)
			_ret.add(sb.toString());
		return _ret.toArray(new String[_ret.size()]);
	}

	private static final Set<Character> smSet = new HashSet<Character>();

	static {
		String[] smArr = "b p m f d t n l g k h j q x r z c s y w".split(" ");
		for (String sm : smArr)
			smSet.add(sm.charAt(0));
	}

	public static void main(String[] args) {
		String input = "你好";
		PinYinHelper py = new PinYinHelper();
		String[] pinyin = py.gePinYin(input);
		String pinyinStr =  "";
		for(String p:pinyin){
			pinyinStr +=p;
		}
		System.out.println(pinyinStr);
	}
}
