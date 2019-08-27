package me.lovegao.gdao.test;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {
	@Test
	public void numberCheck() {
		String n1 = "1.1";
		System.out.println(NumberUtils.isNumber(n1));
	}
}
