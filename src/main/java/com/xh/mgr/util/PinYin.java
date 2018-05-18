/**
 * 
 */
package com.xh.mgr.util;

/**
 * @author norman
 * 
 */
public class PinYin {

	private String sentence;
	private String spell;
	private String initials;

	public PinYin(String sentence,
			String spell,String initials){
		this.sentence=sentence;
		this.spell=spell;
		this.initials=initials;
	}
	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}
}
