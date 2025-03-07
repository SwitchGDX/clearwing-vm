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
package java.nio.charset;

import java.io.UnsupportedOperationException;

/**
 * Added this for Kotlin
 * @author shannah
 */
public class Charset implements Comparable<Charset> {

    private final String name;

    public Charset(String name) {
        this.name = name;
    }

    protected Charset(String canonicalName, String[] aliases) {
        name = canonicalName;
    }
    
    public int compareTo(Charset another) {
        return name.compareTo(another.name);
    }
    
    public String displayName() {
        return name;
    }
    
    public static Charset forName(String name) {
        switch (name) {
            case "UTF-8": return StandardCharsets.UTF_8;
            case "ISO-8859-1": return StandardCharsets.ISO_8859_1;
            case "UTF-16": return StandardCharsets.UTF_16;
            case "UTF-16LE": return StandardCharsets.UTF_16LE;
            case "UTF-16BE": return StandardCharsets.UTF_16BE;
            case "US-ASCII": return StandardCharsets.US_ASCII;
            default: throw new UnsupportedCharsetException(name);
        }
    }

    public static Charset defaultCharset() {
        return StandardCharsets.UTF_8;
    }

    public CharsetDecoder newDecoder() {
        throw new UnsupportedOperationException();
    }

    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException();
    }
}
