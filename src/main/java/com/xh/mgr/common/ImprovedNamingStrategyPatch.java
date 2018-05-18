package com.xh.mgr.common;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class ImprovedNamingStrategyPatch extends ImprovedNamingStrategy {

	public String columnName(String columnName) {
		char fc = columnName.charAt(0);
		char lc = columnName.charAt(columnName.length() - 1);
		String string = super.columnName(columnName);
		if (fc == '[' && lc == ']' || fc == '`' && lc == '`' || fc == '"'
				&& lc == '"') {
			return string.substring(0, 1).concat(
					columnName.substring(1, columnName.length() - 1)).concat(
					string.substring(string.length() - 1));
		}
		return string;
	}

}
