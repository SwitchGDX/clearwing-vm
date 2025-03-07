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
 * The Byte class is the standard wrapper for byte values.
 * Since: JDK1.1, CLDC 1.0
 */
public final class Byte extends Number implements Comparable<Byte> {
    
    public static final Class<Byte> TYPE = (Class<Byte>)NativeUtils.getPrimitive("B");
    public static final int SIZE = 8;
    
    /**
     * The maximum value a Byte can have.
     * See Also:Constant Field Values
     */
    public static final byte MAX_VALUE = 127;

    /**
     * The minimum value a Byte can have.
     * See Also:Constant Field Values
     */
    public static final byte MIN_VALUE = -128;

    private byte value;
    
    /**
     * Constructs a Byte object initialized to the specified byte value.
     * value - the initial value of the Byte
     */
    public Byte(byte value){
         this.value = value;
    }

    /**
     * Returns the value of this Byte as a byte.
     */
    public byte byteValue(){
        return value; 
    }

    /**
     * Compares this object to the specified object.
     */
    public boolean equals(java.lang.Object obj){
        return obj != null && obj.getClass() == getClass() && ((Byte)obj).value == value;
    }

    /**
     * Returns a hashcode for this Byte.
     */
    public int hashCode(){
        return intValue();
    }
    
    public static int hashCode(byte value) {
        return value;
    }

    /**
     * Assuming the specified String represents a byte, returns that byte's value. Throws an exception if the String cannot be parsed as a byte. The radix is assumed to be 10.
     */
    public static byte parseByte(java.lang.String s) throws java.lang.NumberFormatException{
        return parseByte(s, 10);
    }

    /**
     * Assuming the specified String represents a byte, returns that byte's value. Throws an exception if the String cannot be parsed as a byte.
     */
    public static byte parseByte(java.lang.String s, int radix) throws java.lang.NumberFormatException{
        int intValue = Integer.parseInt(s, radix);
        byte result = (byte) intValue;
        if (result == intValue) {
            return result;
        }
        throw new NumberFormatException("Value out of range for byte: \"" + s + "\"");
    }

    /**
     * Returns a String object representing this Byte's value.
     */
    public java.lang.String toString(){
        return Integer.toString(value);
    }

    public static String toString(byte b) {
        return Integer.toString(b);
    }

    /**
     * Returns the object instance of i
     * @param i the primitive
     * @return object instance
     */
    public static Byte valueOf(byte i) {
        return new Byte(i);
    }

    public static Byte valueOf(String s) {
        return null;
    }

    /**
     * Converts the argument to an {@code int} by an unsigned
     * conversion.  In an unsigned conversion to an {@code int}, the
     * high-order 24 bits of the {@code int} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code int} value and negative {@code
     * byte} values are mapped to an {@code int} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code int}
     * @return the argument converted to {@code int} by an unsigned
     *         conversion
     * @since 1.8
     */
    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public static int compare(byte f1, byte f2) {
        return f1 - f2;
    }

    public static int compareUnsigned(byte x, byte y) {
        return toUnsignedInt(x) - toUnsignedInt(y);
    }

    public static long toUnsignedLong(byte x) {
        return (long)x & 255L;
    }

    public int compareTo(Byte another) {
        return value < another.value ? -1 : value > another.value ? 1 : 0;
    }
}
