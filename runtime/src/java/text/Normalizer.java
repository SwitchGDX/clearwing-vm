/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.text;

import java.io.UnsupportedOperationException;

/**
 * Provides normalization functions according to
 * <a href="http://www.unicode.org/unicode/reports/tr15/tr15-23.html">Unicode Standard Annex #15:
 * Unicode Normalization Forms</a>. Normalization can decompose and compose
 * characters for equivalency checking.
 *
 * @since 1.6
 */
public final class Normalizer {
    /**
     * The normalization forms supported by the Normalizer. These are specified in
     * <a href="http://www.unicode.org/unicode/reports/tr15/tr15-23.html">Unicode Standard
     * Annex #15</a>.
     */
    public static enum Form {
        /**
         * Normalization Form D - Canonical Decomposition.
         */
        NFD,

        /**
         * Normalization Form C - Canonical Decomposition, followed by Canonical Composition.
         */
        NFC,

        /**
         * Normalization Form KD - Compatibility Decomposition.
         */
        NFKD,

        /**
         * Normalization Form KC - Compatibility Decomposition, followed by Canonical Composition.
         */
        NFKC;
    }

    /**
     * Check whether the given character sequence <code>src</code> is normalized
     * according to the normalization method <code>form</code>.
     *
     * @param src character sequence to check
     * @param form normalization form to check against
     * @return true if normalized according to <code>form</code>
     */
    public static boolean isNormalized(CharSequence src, Form form) {
        throw new UnsupportedOperationException();
    }

    /**
     * Normalize the character sequence <code>src</code> according to the
     * normalization method <code>form</code>.
     *
     * @param src character sequence to read for normalization
     * @param form normalization form
     * @return string normalized according to <code>form</code>
     */
    public static String normalize(CharSequence src, Form form) {
        throw new UnsupportedOperationException();
    }

    private Normalizer() {}
}
