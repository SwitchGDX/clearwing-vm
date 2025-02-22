/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.util;


import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@code Map} is a data structure consisting of a set of keys and values
 * in which each key is mapped to a single value.  The class of the objects
 * used as keys is declared when the {@code Map} is declared, as is the 
 * class of the corresponding values.
 * <p>
 * A {@code Map} provides helper methods to iterate through all of the
 * keys contained in it, as well as various methods to access and update 
 * the key/value pairs.  
 */
public interface Map<K,V> {

    /**
     * {@code Map.Entry} is a key/value mapping contained in a {@code Map}.
     */
    public static interface Entry<K,V> {
        /**
         * Compares the specified object to this {@code Map.Entry} and returns if they
         * are equal. To be equal, the object must be an instance of {@code Map.Entry} and have the
         * same key and value.
         * 
         * @param object
         *            the {@code Object} to compare with this {@code Object}.
         * @return {@code true} if the specified {@code Object} is equal to this
         *         {@code Map.Entry}, {@code false} otherwise.
         * @see #hashCode()
         */
        public boolean equals(Object object);

        /**
         * Returns the key.
         * 
         * @return the key
         */
        public K getKey();

        /**
         * Returns the value.
         * 
         * @return the value
         */
        public V getValue();

        /**
         * Returns an integer hash code for the receiver. {@code Object} which are
         * equal return the same value for this method.
         * 
         * @return the receiver's hash code.
         * @see #equals(Object)
         */
        public int hashCode();

        /**
         * Sets the value of this entry to the specified value, replacing any
         * existing value.
         * 
         * @param object
         *            the new value to set.
         * @return object the replaced value of this entry.
         */
        public V setValue(V object);
    };

    /**
     * Removes all elements from this {@code Map}, leaving it empty.
     * 
     * @throws UnsupportedOperationException
     *                if removing elements from this {@code Map} is not supported.
     * @see #isEmpty()
     * @see #size()
     */
    public void clear();

    /**
     * Returns whether this {@code Map} contains the specified key.
     * 
     * @param key
     *            the key to search for.
     * @return {@code true} if this map contains the specified key,
     *         {@code false} otherwise.
     */
    public boolean containsKey(Object key);

    /**
     * Returns whether this {@code Map} contains the specified value.
     * 
     * @param value
     *            the value to search for.
     * @return {@code true} if this map contains the specified value,
     *         {@code false} otherwise.
     */
    public boolean containsValue(Object value);

    /**
     * Returns a {@code Set} containing all of the mappings in this {@code Map}. Each mapping is
     * an instance of {@link Map.Entry}. As the {@code Set} is backed by this {@code Map},
     * changes in one will be reflected in the other.
     * 
     * @return a set of the mappings
     */
    public Set<Map.Entry<K,V>> entrySet();

    /**
     * Compares the argument to the receiver, and returns {@code true} if the
     * specified object is a {@code Map} and both {@code Map}s contain the same mappings.
     * 
     * @param object
     *            the {@code Object} to compare with this {@code Object}.
     * @return boolean {@code true} if the {@code Object} is the same as this {@code Object}
     *         {@code false} if it is different from this {@code Object}.
     * @see #hashCode()
     * @see #entrySet()
     */
    public boolean equals(Object object);

    /**
     * Returns the value of the mapping with the specified key.
     * 
     * @param key
     *            the key.
     * @return the value of the mapping with the specified key, or {@code null}
     *         if no mapping for the specified key is found.
     */
    public V get(Object key);

    /**
     * Returns an integer hash code for the receiver. {@code Object}s which are equal
     * return the same value for this method.
     * 
     * @return the receiver's hash.
     * @see #equals(Object)
     */
    public int hashCode();

    /**
     * Returns whether this map is empty.
     * 
     * @return {@code true} if this map has no elements, {@code false}
     *         otherwise.
     * @see #size()
     */
    public boolean isEmpty();

    /**
     * Returns a set of the keys contained in this {@code Map}. The {@code Set} is backed by
     * this {@code Map} so changes to one are reflected by the other. The {@code Set} does not
     * support adding.
     * 
     * @return a set of the keys.
     */
    public Set<K> keySet();

    /**
     * Maps the specified key to the specified value.
     * 
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @return the value of any previous mapping with the specified key or
     *         {@code null} if there was no mapping.
     * @throws UnsupportedOperationException
     *                if adding to this {@code Map} is not supported.
     * @throws ClassCastException
     *                if the class of the key or value is inappropriate for
     *                this {@code Map}.
     * @throws IllegalArgumentException
     *                if the key or value cannot be added to this {@code Map}.
     * @throws NullPointerException
     *                if the key or value is {@code null} and this {@code Map} does
     *                not support {@code null} keys or values.
     */
    public V put(K key, V value);

    /**
     * Copies every mapping in the specified {@code Map} to this {@code Map}.
     * 
     * @param map
     *            the {@code Map} to copy mappings from.
     * @throws UnsupportedOperationException
     *                if adding to this {@code Map} is not supported.
     * @throws ClassCastException
     *                if the class of a key or a value of the specified {@code Map} is
     *                inappropriate for this {@code Map}.
     * @throws IllegalArgumentException
     *                if a key or value cannot be added to this {@code Map}.
     * @throws NullPointerException
     *                if a key or value is {@code null} and this {@code Map} does not
     *                support {@code null} keys or values.
     */
    public void putAll(Map<? extends K,? extends V> map);

    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * This is equivalent to
     *  <pre> {@code
     * if (!map.containsKey(key))
     *   return map.put(key, value);
     * else
     *   return map.get(key);
     * }</pre>
     *
     * except that the action is performed atomically.
     *
     * @implNote This implementation intentionally re-abstracts the
     * inappropriate default provided in {@code Map}.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with the key,
     *         if the implementation supports null values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified key or value is null,
     *         and this map does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     */
    default V putIfAbsent (K key, V value) {
        if (!containsKey(key) || get(key) == null) {
            put(key, value);
            return null;
        }
        return get(key);
    }

    /**
     * Removes a mapping with the specified key from this {@code Map}.
     * 
     * @param key
     *            the key of the mapping to remove.
     * @return the value of the removed mapping or {@code null} if no mapping
     *         for the specified key was found.
     * @throws UnsupportedOperationException
     *                if removing from this {@code Map} is not supported.
     */
    public V remove(Object key);

    default boolean remove(Object key, Object value) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, value) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    default V replace (K key, V value) {
        if (containsKey(key))
            return put(key, value);
        else
            return null;
    }

    default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    default V getOrDefault(Object key, V defaultValue) {
        V v;
        return (v = this.get(key)) == null && !this.containsKey(key) ? defaultValue : v;
    }

    default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);

        K k;
        V v;
        for(Iterator var2 = this.entrySet().iterator(); var2.hasNext(); action.accept(k, v)) {
            Entry<K, V> entry = (Entry)var2.next();

            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException var7) {
                IllegalStateException ise = var7;
                throw new ConcurrentModificationException(ise);
            }
        }

    }

    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        Iterator var2 = this.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<K, V> entry = (Entry)var2.next();

            K k;
            V v;
            IllegalStateException ise;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException var8) {
                ise = var8;
                throw new ConcurrentModificationException(ise);
            }

            v = function.apply(k, v);

            try {
                entry.setValue(v);
            } catch (IllegalStateException var7) {
                ise = var7;
                throw new ConcurrentModificationException(ise);
            }
        }

    }

    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        V newValue;
        if ((v = this.get(key)) == null && (newValue = mappingFunction.apply(key)) != null) {
            this.put(key, newValue);
            return newValue;
        } else {
            return v;
        }
    }

    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = this.get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                this.put(key, newValue);
                return newValue;
            } else {
                this.remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue == null && !this.containsKey(key)) {
                return null;
            } else {
                this.remove(key);
                return null;
            }
        } else {
            this.put(key, newValue);
            return newValue;
        }
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = this.get(key);
        V newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.put(key, newValue);
        }

        return newValue;
    }


    /**
     * Returns the number of mappings in this {@code Map}.
     * 
     * @return the number of mappings in this {@code Map}.
     */
    public int size();

    /**
     * Returns a {@code Collection} of the values contained in this {@code Map}. The {@code Collection}
     * is backed by this {@code Map} so changes to one are reflected by the other. The
     * {@code Collection} supports {@link Collection#remove}, {@link Collection#removeAll}, 
     * {@link Collection#retainAll}, and {@link Collection#clear} operations,
     * and it does not support {@link Collection#add} or {@link Collection#addAll} operations.
     * <p>
     * This method returns a {@code Collection} which is the subclass of
     * {@link AbstractCollection}. The {@link AbstractCollection#iterator} method of this subclass returns a
     * "wrapper object" over the iterator of this {@code Map}'s {@link #entrySet()}. The {@link AbstractCollection#size} method
     * wraps this {@code Map}'s {@link #size} method and the {@link AbstractCollection#contains} method wraps this {@code Map}'s
     * {@link #containsValue} method.
     * <p>
     * The collection is created when this method is called at first time and
     * returned in response to all subsequent calls. This method may return
     * different Collection when multiple calls to this method, since it has no
     * synchronization performed.
     * 
     * @return a collection of the values contained in this map.
     */
    public Collection<V> values();
}
