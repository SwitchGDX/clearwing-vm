/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Codename One through http://www.codenameone.com/ if you
 * need additional information or have any questions.
 */

package java.lang;

import java.nio.NativeUtils;

/**
 * The Integer class wraps a value of the primitive type int in an object. An object of type Integer contains a single field whose type is int.
 * In addition, this class provides several methods for converting an int to a String and a String to an int, as well as other constants and methods useful when dealing with an int.
 * Since: JDK1.0, CLDC 1.0
 */
public final class Integer extends Number implements Comparable<Integer> {

	public static final Class<Integer> TYPE = (Class<Integer>)NativeUtils.getPrimitive("I");

	public static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * The largest value of type int. The constant value of this field is 2147483647.
	 * See Also:Constant Field Values
	 */
	public static final int MAX_VALUE = 2147483647;

	/**
	 * The smallest value of type int. The constant value of this field is -2147483648.
	 * See Also:Constant Field Values
	 */
	public static final int MIN_VALUE = -2147483648;

	public static final int SIZE = 32;

	private int value;

	/**
	 * Constructs a newly allocated Integer object that represents the primitive int argument.
	 * value - the value to be represented by the Integer.
	 */
	public Integer (int value) {
		this.value = value;
	}

	public Integer (String s) {
		this.value = valueOf(s);
	}

	/**
	 * Returns the value of this Integer as a byte.
	 */
	public byte byteValue () {
		return (byte)value;
	}

	/**
	 * Returns the value of this Integer as a double.
	 */
	public double doubleValue () {
		return value;
	}

	/**
	 * Compares this object to the specified object. The result is true if and only if the argument is not null and is an Integer object that contains the same int value as this object.
	 */
	public boolean equals (java.lang.Object obj) {
		return obj != null && obj.getClass() == getClass() && ((Integer)obj).value == value;
	}

	/**
	 * Returns the value of this Integer as a float.
	 */
	public float floatValue () {
		return value;
	}

	/**
	 * Returns a hashcode for this Integer.
	 */
	public int hashCode () {
		return value;
	}

	/**
	 * Returns the value of this Integer as an int.
	 */
	public int intValue () {
		return value;
	}

	/**
	 * Returns the value of this Integer as a long.
	 */
	public long longValue () {
		return value;
	}

	public static int rotateLeft(int i, int distance) {
		return i << distance | i >>> -distance;
	}

	public static int rotateRight(int i, int distance) {
		return i >>> distance | i << -distance;
	}

	public static int reverse(int i) {
		i = (i & 1431655765) << 1 | i >>> 1 & 1431655765;
		i = (i & 858993459) << 2 | i >>> 2 & 858993459;
		i = (i & 252645135) << 4 | i >>> 4 & 252645135;
		return reverseBytes(i);
	}

	public static int reverseBytes (int i) {
		return ((i >>> 24)) | ((i >> 8) & 0xFF00) | ((i << 8) & 0xFF0000) | ((i << 24));
	}

	/**
	 * Compares two {@code int} values numerically treating the values
	 * as unsigned.
	 *
	 * @param  x the first {@code int} to compare
	 * @param  y the second {@code int} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less
	 *         than {@code 0} if {@code x < y} as unsigned values; and
	 *         a value greater than {@code 0} if {@code x > y} as
	 *         unsigned values
	 * @since 1.8
	 */
	public static int compareUnsigned(int x, int y) {
		return compare(x + MIN_VALUE, y + MIN_VALUE);
	}

	/**
	 * Converts the argument to a {@code long} by an unsigned
	 * conversion.  In an unsigned conversion to a {@code long}, the
	 * high-order 32 bits of the {@code long} are zero and the
	 * low-order 32 bits are equal to the bits of the integer
	 * argument.
	 *
	 * Consequently, zero and positive {@code int} values are mapped
	 * to a numerically equal {@code long} value and negative {@code
	 * int} values are mapped to a {@code long} value equal to the
	 * input plus 2<sup>32</sup>.
	 *
	 * @param  x the value to convert to an unsigned {@code long}
	 * @return the argument converted to {@code long} by an unsigned
	 *         conversion
	 * @since 1.8
	 */
	public static long toUnsignedLong(int x) {
		return ((long) x) & 0xffffffffL;
	}

	/**
	 * Returns the unsigned quotient of dividing the first argument by
	 * the second where each argument and the result is interpreted as
	 * an unsigned value.
	 *
	 * <p>Note that in two's complement arithmetic, the three other
	 * basic arithmetic operations of add, subtract, and multiply are
	 * bit-wise identical if the two operands are regarded as both
	 * being signed or both being unsigned.  Therefore separate {@code
	 * addUnsigned}, etc. methods are not provided.
	 *
	 * @param dividend the value to be divided
	 * @param divisor the value doing the dividing
	 * @return the unsigned quotient of the first argument divided by
	 * the second argument
	 * @see #remainderUnsigned
	 * @since 1.8
	 */
	public static int divideUnsigned(int dividend, int divisor) {
		// In lieu of tricky code, for now just use long arithmetic.
		return (int)(toUnsignedLong(dividend) / toUnsignedLong(divisor));
	}

	/**
	 * Returns the unsigned remainder from dividing the first argument
	 * by the second where each argument and the result is interpreted
	 * as an unsigned value.
	 *
	 * @param dividend the value to be divided
	 * @param divisor the value doing the dividing
	 * @return the unsigned remainder of the first argument divided by
	 * the second argument
	 * @see #divideUnsigned
	 * @since 1.8
	 */
	public static int remainderUnsigned(int dividend, int divisor) {
		// In lieu of tricky code, for now just use long arithmetic.
		return (int)(toUnsignedLong(dividend) % toUnsignedLong(divisor));
	}
	
	/**
	 * Returns the number of one-bits in the two's complement binary
	 * representation of the specified {@code int} value.  This function is
	 * sometimes referred to as the <i>population count</i>.
	 *
	 * @param i the value whose bits are to be counted
	 * @return the number of one-bits in the two's complement binary
	 *     representation of the specified {@code int} value.
	 * @since 1.5
	 */
	public static int bitCount(int i) {
		// HD, Figure 5-2
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		i = (i + (i >>> 4)) & 0x0f0f0f0f;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		return i & 0x3f;
	}

	public static int lowestOneBit(int i) {
		return i & -i;
	}

	public static int highestOneBit(int i) {
		return i & MIN_VALUE >>> numberOfLeadingZeros(i);
	}

	/**
	 * Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits, except that the first character may be an ASCII minus sign '-' ('
	 * u002d') to indicate a negative value. The resulting integer value is returned, exactly as if the argument and the radix 10 were given as arguments to the
	 * method.
	 */
	public static int parseInt (java.lang.String s) throws java.lang.NumberFormatException {
		return parseInt(s, 10);
	}

	/**
	 * Parses the string argument as a signed integer in the radix specified by the second argument. The characters in the string must all be digits of the specified radix (as determined by whether
	 * returns a nonnegative value), except that the first character may be an ASCII minus sign '-' ('
	 * u002d') to indicate a negative value. The resulting integer value is returned.
	 * An exception of type NumberFormatException is thrown if any of the following situations occurs: The first argument is null or is a string of length zero. The radix is either smaller than Character.MIN_RADIX or larger than Character.MAX_RADIX. Any character of the string is not a digit of the specified radix, except that the first character may be a minus sign '-' ('u002d') provided that the string is longer than length 1. The integer value represented by the string is not a value of type int.
	 * Examples:
	 * parseInt("0", 10) returns 0 parseInt("473", 10) returns 473 parseInt("-0", 10) returns 0 parseInt("-FF", 16) returns -255 parseInt("1100110", 2) returns 102 parseInt("2147483647", 10) returns 2147483647 parseInt("-2147483648", 10) returns -2147483648 parseInt("2147483648", 10) throws a NumberFormatException parseInt("99", 8) throws a NumberFormatException parseInt("Kona", 10) throws a NumberFormatException parseInt("Kona", 27) returns 411787
	 */
	public static int parseInt (String string, int radix) throws NumberFormatException {
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			throw new NumberFormatException("Invalid radix: " + radix);
		}
		if (string == null) {
			throw invalidInt(string);
		}
		int length = string.length(), i = 0;
		if (length == 0) {
			throw invalidInt(string);
		}
		boolean negative = string.charAt(i) == '-';
		if (negative && ++i == length) {
			throw invalidInt(string);
		}
		boolean positive = string.charAt(i) == '+';
		if (positive && ++i == length) {
			throw invalidInt(string);
		}

		return parse(string, i, radix, negative);
	}

	private static int parse (String string, int offset, int radix, boolean negative) throws NumberFormatException {
		int max = Integer.MIN_VALUE / radix;
		int result = 0, length = string.length();
		while (offset < length) {
			int digit = Character.digit(string.charAt(offset++), radix);
			if (digit == -1) {
				throw invalidInt(string);
			}
			if (max > result) {
				throw invalidInt(string);
			}
			int next = result * radix - digit;
			if (next > result) {
				throw invalidInt(string);
			}
			result = next;
		}
		if (!negative) {
			result = -result;
			if (result < 0) {
				throw invalidInt(string);
			}
		}
		return result;
	}
	
	public static Integer getInteger(String nm) {
		return getInteger(nm, (Integer)null);
	}

	public static Integer getInteger(String nm, int val) {
		Integer result = getInteger(nm, (Integer)null);
		return result == null ? val : result;
	}

	public static Integer getInteger(String nm, Integer val) {
		String v = null;

		try {
			v = System.getProperty(nm);
		} catch (NullPointerException | IllegalArgumentException var4) {
		}

		if (v != null) {
			try {
				return parseInt(v);
			} catch (NumberFormatException var5) {
			}
		}

		return val;
	}

	private static NumberFormatException invalidInt (String s) {
		throw new NumberFormatException("Invalid int: \"" + s + "\"");
	}

	/**
	 * Returns a hash code for a {@code int} value; compatible with
	 * {@code Integer.hashCode()}.
	 *
	 * @param value the value to hash
	 * @since 1.8
	 *
	 * @return a hash code value for a {@code int} value.
	 */
	public static int hashCode(int value) {
		return value;
	}

	/**
	 * Returns the value of this Integer as a short.
	 */
	public short shortValue () {
		return (short)value;
	}

	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base
	 * 2.
	 * The unsigned integer value is the argument plus 232if the argument is negative; otherwise it is equal to the argument. This value is converted to a string of ASCII digits in binary (base2) with no extra leading 0s. If the unsigned magnitude is zero, it is represented by a single zero character '0' ('u0030'); otherwise, the first character of the representation of the unsigned magnitude will not be the zero character. The characters '0' ('u0030') and '1' ('u0031') are used as binary digits.
	 */
	public static java.lang.String toBinaryString (int i) {
		return intToBinaryString(i);
	}

	private static String intToBinaryString (int i) {
		int bufLen = 32;  // Max number of binary digits in an int
		char[] buf = new char[bufLen];
		int cursor = bufLen;

		do {
			buf[--cursor] = DIGITS[i & 1];
		} while ((i >>>= 1) != 0);

		return new String(buf, cursor, bufLen - cursor);
//		return new String(cursor, bufLen - cursor, buf);
	}

	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base
	 * 16.
	 * The unsigned integer value is the argument plus 232 if the argument is negative; otherwise, it is equal to the argument. This value is converted to a string of ASCII digits in hexadecimal (base16) with no extra leading 0s. If the unsigned magnitude is zero, it is represented by a single zero character '0' ('u0030'); otherwise, the first character of the representation of the unsigned magnitude will not be the zero character. The following characters are used as hexadecimal digits:
	 * 0123456789abcdef These are the characters '
	 * u0030' through '
	 * u0039' and 'u\0039' through '
	 * u0066'.
	 */
	public static java.lang.String toHexString (int i) {
		return intToHexString(i, 0);
	}

	public static String intToHexString (int i, int minWidth) {
		int bufLen = 8;  // Max number of hex digits in an int
		char[] buf = new char[bufLen];
		int cursor = bufLen;

		char[] digits = DIGITS;
		do {
			buf[--cursor] = digits[i & 0xf];
		} while ((i >>>= 4) != 0 || (bufLen - cursor < minWidth));

		return new String(buf, cursor, bufLen - cursor);
	}

	public static String intToOctalString (int i) {
		int bufLen = 11;  // Max number of octal digits in an int
		char[] buf = new char[bufLen];
		int cursor = bufLen;

		do {
			buf[--cursor] = DIGITS[i & 7];
		} while ((i >>>= 3) != 0);

		return new String(buf, cursor, bufLen - cursor);
	}

	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base 8.
	 * The unsigned integer value is the argument plus 232 if the argument is negative; otherwise, it is equal to the argument. This value is converted to a string of ASCII digits in octal (base8) with no extra leading 0s.
	 * If the unsigned magnitude is zero, it is represented by a single zero character '0' ('u0030'); otherwise, the first character of the representation of the unsigned magnitude will not be the zero character. The octal digits are:
	 * 01234567 These are the characters '
	 * u0030' through '
	 * u0037'.
	 */
	public static java.lang.String toOctalString (int i) {
		return intToOctalString(i);
	}

	/**
	 * Returns a String object representing this Integer's value. The value is converted to signed decimal representation and returned as a string, exactly as if the integer value were given as an argument to the
	 * method.
	 */
	public java.lang.String toString () {
		return toString(value);
	}

	/**
	 * Returns a new String object representing the specified integer. The argument is converted to signed decimal representation and returned as a string, exactly as if the argument and radix 10 were given as arguments to the
	 * method.
	 */
	public static java.lang.String toString (int i) {
		return toString(i, 10);
	}

	/**
	 * Creates a string representation of the first argument in the radix specified by the second argument.
	 * If the radix is smaller than Character.MIN_RADIX or larger than Character.MAX_RADIX, then the radix 10 is used instead.
	 * If the first argument is negative, the first element of the result is the ASCII minus character '-' ('u002d'). If the first argument is not negative, no sign character appears in the result.
	 * The remaining characters of the result represent the magnitude of the first argument. If the magnitude is zero, it is represented by a single zero character '0' ('u0030'); otherwise, the first character of the representation of the magnitude will not be the zero character. The following ASCII characters are used as digits:
	 * 0123456789abcdefghijklmnopqrstuvwxyz These are '
	 * u0030' through '
	 * u0039' and '
	 * u0061' through '
	 * u007a'. If the radix is N, then the first N of these characters are used as radix-N digits in the order shown. Thus, the digits for hexadecimal (radix 16) are 0123456789abcdef.
	 */
	public native static java.lang.String toString (int i, int radix);

	/**
	 * Returns a new Integer object initialized to the value of the specified String. The argument is interpreted as representing a signed decimal integer, exactly as if the argument were given to the
	 * method. The result is an Integer object that represents the integer value specified by the string.
	 * In other words, this method returns an Integer object equal to the value of:
	 * new Integer(Integer.parseInt(s))
	 */
	public static java.lang.Integer valueOf (java.lang.String s) throws java.lang.NumberFormatException {
		return new Integer(parseInt(s));
	}

	/**
	 * Returns a new Integer object initialized to the value of the specified String. The first argument is interpreted as representing a signed integer in the radix specified by the second argument, exactly as if the arguments were given to the
	 * method. The result is an Integer object that represents the integer value specified by the string.
	 * In other words, this method returns an Integer object equal to the value of:
	 * new Integer(Integer.parseInt(s, radix))
	 */
	public static java.lang.Integer valueOf (java.lang.String s, int radix) throws java.lang.NumberFormatException {
		return new Integer(parseInt(s, radix));
	}

	/**
	 * Returns the object instance of i
	 *
	 * @param i the primitive
	 * @return object instance
	 */
	public static Integer valueOf (int i) {
		return new Integer(i);
	}

	/**
	 * Returns the value of the {@code signum} function for the specified
	 * integer.
	 *
	 * @param i the integer value to check.
	 * @return -1 if {@code i} is negative, 1 if {@code i} is positive, 0 if
	 * {@code i} is zero.
	 * @since 1.5
	 */
	public static int signum (int i) {
		return (i >> 31) | (-i >>> 31); // Hacker's delight 2-7
	}

	public static int compare (int f1, int f2) {

		if (f1 > f2)
			return 1;
		else if (f1 < f2)
			return -1;
		return 0;

	}

	public int compareTo (Integer another) {
		return value < another.value ? -1 : value > another.value ? 1 : 0;
	}

	public static int numberOfLeadingZeros (int i) {
		if (i == 0) {
			return 32;
		}
		int n = 0;
		if (i >>> 16 != 0) {
			i >>>= 16;
			n |= 16;
		}
		if (i >>> 8 != 0) {
			i >>>= 8;
			n |= 8;
		}
		if (i >>> 4 != 0) {
			i >>>= 4;
			n |= 4;
		}
		if (i >>> 2 != 0) {
			i >>>= 2;
			n |= 2;
		}
		if (i >>> 1 != 0) {
			i >>>= 1;
			n |= 1;
		}
		return 32 - n - 1;
	}

	public static int numberOfTrailingZeros (int i) {
		int y;
		if (i == 0)
			return 32;
		int n = 31;
		y = i << 16;
		if (y != 0) {
			n = n - 16;
			i = y;
		}
		y = i << 8;
		if (y != 0) {
			n = n - 8;
			i = y;
		}
		y = i << 4;
		if (y != 0) {
			n = n - 4;
			i = y;
		}
		y = i << 2;
		if (y != 0) {
			n = n - 2;
			i = y;
		}
		return n - ((i << 1) >>> 31);
	}

	/**
	 * Adds two integers together as per the + operator.
	 *
	 * @param a the first operand
	 * @param b the second operand
	 * @return the sum of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static int sum(int a, int b) {
		return a + b;
	}

	/**
	 * Returns the greater of two {@code int} values
	 * as if by calling {@link Math#max(int, int) Math.max}.
	 *
	 * @param a the first operand
	 * @param b the second operand
	 * @return the greater of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static int max(int a, int b) {
		return Math.max(a, b);
	}

	/**
	 * Returns the smaller of two {@code int} values
	 * as if by calling {@link Math#min(int, int) Math.min}.
	 *
	 * @param a the first operand
	 * @param b the second operand
	 * @return the smaller of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static int min(int a, int b) {
		return Math.min(a, b);
	}
}
