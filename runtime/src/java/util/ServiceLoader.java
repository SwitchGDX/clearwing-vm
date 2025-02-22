/*
 * Copyright (c) 2005, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * A facility to load implementations of a service.
 *
 * <p> A <i>service</i> is a well-known interface or class for which zero, one,
 * or many service providers exist. A <i>service provider</i> (or just
 * <i>provider</i>) is a class that implements or subclasses the well-known
 * interface or class. A {@code ServiceLoader} is an object that locates and
 * loads service providers deployed in the run time environment at a time of an
 * application's choosing. Application code refers only to the service, not to
 * service providers, and is assumed to be capable of differentiating between
 * multiple service providers as well as handling the possibility that no service
 * providers are located.
 *
 * <h3> Obtaining a service loader </h3>
 *
 * <p> An application obtains a service loader for a given service by invoking
 * one of the static {@code load} methods of ServiceLoader. If the application
 * is a module, then its module declaration must have a <i>uses</i> directive
 * that specifies the service; this helps to locate providers and ensure they
 * will execute reliably. In addition, if the service is not in the application
 * module, then the module declaration must have a <i>requires</i> directive
 * that specifies the module which exports the service.
 *
 * <p> A service loader can be used to locate and instantiate providers of the
 * service by means of the {@link #iterator() iterator} method. {@code ServiceLoader}
 * also defines the {@link #stream() stream} method to obtain a stream of providers
 * that can be inspected and filtered without instantiating them.
 *
 * <p> As an example, suppose the service is {@code com.example.CodecFactory}, an
 * interface that defines methods for producing encoders and decoders:
 *
 * <pre>{@code
 *     package com.example;
 *     public interface CodecFactory {
 *         Encoder getEncoder(String encodingName);
 *         Decoder getDecoder(String encodingName);
 *     }
 * }</pre>
 *
 * <p> The following code obtains a service loader for the {@code CodecFactory}
 * service, then uses its iterator (created automatically by the enhanced-for
 * loop) to yield instances of the service providers that are located:
 *
 * <pre>{@code
 *     ServiceLoader<CodecFactory> loader = ServiceLoader.load(CodecFactory.class);
 *     for (CodecFactory factory : loader) {
 *         Encoder enc = factory.getEncoder("PNG");
 *         if (enc != null)
 *             ... use enc to encode a PNG file
 *             break;
 *         }
 * }</pre>
 *
 * <p> If this code resides in a module, then in order to refer to the
 * {@code com.example.CodecFactory} interface, the module declaration would
 * require the module which exports the interface. The module declaration would
 * also specify use of {@code com.example.CodecFactory}:
 * <pre>{@code
 *     requires com.example.codec.core;
 *     uses com.example.CodecFactory;
 * }</pre>
 *
 * <p> Sometimes an application may wish to inspect a service provider before
 * instantiating it, in order to determine if an instance of that service
 * provider would be useful. For example, a service provider for {@code
 * CodecFactory} that is capable of producing a "PNG" encoder may be annotated
 * with {@code @PNG}. The following code uses service loader's {@code stream}
 * method to yield instances of {@code Provider<CodecFactory>} in contrast to
 * how the iterator yields instances of {@code CodecFactory}:
 * <pre>{@code
 *     ServiceLoader<CodecFactory> loader = ServiceLoader.load(CodecFactory.class);
 *     Set<CodecFactory> pngFactories = loader
 *            .stream()                                              // Note a below
 *            .filter(p -> p.type().isAnnotationPresent(PNG.class))  // Note b
 *            .map(Provider::get)                                    // Note c
 *            .collect(Collectors.toSet());
 * }</pre>
 * <ol type="a">
 *   <li> A stream of {@code Provider<CodecFactory>} objects </li>
 *   <li> {@code p.type()} yields a {@code Class<CodecFactory>} </li>
 *   <li> {@code get()} yields an instance of {@code CodecFactory} </li>
 * </ol>
 *
 * <h3> Designing services </h3>
 *
 * <p> A service is a single type, usually an interface or abstract class. A
 * concrete class can be used, but this is not recommended. The type may have
 * any accessibility. The methods of a service are highly domain-specific, so
 * this API specification cannot give concrete advice about their form or
 * function. However, there are two general guidelines:
 * <ol>
 *   <li><p> A service should declare as many methods as needed to allow service
 *   providers to communicate their domain-specific properties and other
 *   quality-of-implementation factors. An application which obtains a service
 *   loader for the service may then invoke these methods on each instance of
 *   a service provider, in order to choose the best provider for the
 *   application. </p></li>
 *   <li><p> A service should express whether its service providers are intended
 *   to be direct implementations of the service or to be an indirection
 *   mechanism such as a "proxy" or a "factory". Service providers tend to be
 *   indirection mechanisms when domain-specific objects are relatively
 *   expensive to instantiate; in this case, the service should be designed
 *   so that service providers are abstractions which create the "real"
 *   implementation on demand. For example, the {@code CodecFactory} service
 *   expresses through its name that its service providers are factories
 *   for codecs, rather than codecs themselves, because it may be expensive
 *   or complicated to produce certain codecs. </p></li>
 * </ol>
 *
 * <h3> <a id="developing-service-providers">Developing service providers</a> </h3>
 *
 * <p> A service provider is a single type, usually a concrete class. An
 * interface or abstract class is permitted because it may declare a static
 * provider method, discussed later. The type must be public and must not be
 * an inner class.
 *
 * <p> A service provider and its supporting code may be developed in a module,
 * which is then deployed on the application module path or in a modular
 * image. Alternatively, a service provider and its supporting code may be
 * packaged as a JAR file and deployed on the application class path. The
 * advantage of developing a service provider in a module is that the provider
 * can be fully encapsulated to hide all details of its implementation.
 *
 * <p> An application that obtains a service loader for a given service is
 * indifferent to whether providers of the service are deployed in modules or
 * packaged as JAR files. The application instantiates service providers via
 * the service loader's iterator, or via {@link Provider Provider} objects in
 * the service loader's stream, without knowledge of the service providers'
 * locations.
 *
 * <h3> Deploying service providers as modules </h3>
 *
 * <p> A service provider that is developed in a module must be specified in a
 * <i>provides</i> directive in the module declaration. The provides directive
 * specifies both the service and the service provider; this helps to locate the
 * provider when another module, with a <i>uses</i> directive for the service,
 * obtains a service loader for the service. It is strongly recommended that the
 * module does not export the package containing the service provider. There is
 * no support for a module specifying, in a <i>provides</i> directive, a service
 * provider in another module.

 * <p> A service provider that is developed in a module has no control over when
 * it is instantiated, since that occurs at the behest of the application, but it
 * does have control over how it is instantiated:
 *
 * <ul>
 *
 *   <li> If the service provider declares a provider method, then the service
 *   loader invokes that method to obtain an instance of the service provider. A
 *   provider method is a public static method named "provider" with no formal
 *   parameters and a return type that is assignable to the service's interface
 *   or class.
 *   <p> In this case, the service provider itself need not be assignable to the
 *   service's interface or class. </li>
 *
 *   <li> If the service provider does not declare a provider method, then the
 *   service provider is instantiated directly, via its provider constructor. A
 *   provider constructor is a public constructor with no formal parameters.
 *   <p> In this case, the service provider must be assignable to the service's
 *   interface or class </li>
 *
 * </ul>
 *
 * <p> A service provider that is deployed as an
 * {@linkplain java.lang.module.ModuleDescriptor#isAutomatic automatic module} on
 * the application module path must have a provider constructor. There is no
 * support for a provider method in this case.
 *
 * <p> As an example, suppose a module specifies the following directives:
 * <pre>{@code
 *     provides com.example.CodecFactory with com.example.impl.StandardCodecs;
 *     provides com.example.CodecFactory with com.example.impl.ExtendedCodecsFactory;
 * }</pre>
 *
 * <p> where
 *
 * <ul>
 *   <li> {@code com.example.CodecFactory} is the two-method service from
 *   earlier. </li>
 *
 *   <li> {@code com.example.impl.StandardCodecs} is a public class that implements
 *   {@code CodecFactory} and has a public no-args constructor. </li>
 *
 *   <li> {@code com.example.impl.ExtendedCodecsFactory} is a public class that
 *   does not implement CodecFactory, but it declares a public static no-args
 *   method named "provider" with a return type of {@code CodecFactory}. </li>
 * </ul>
 *
 * <p> A service loader will instantiate {@code StandardCodecs} via its
 * constructor, and will instantiate {@code ExtendedCodecsFactory} by invoking
 * its {@code provider} method. The requirement that the provider constructor or
 * provider method is public helps to document the intent that the class (that is,
 * the service provider) will be instantiated by an entity (that is, a service
 * loader) which is outside the class's package.
 *
 * <h3> Deploying service providers on the class path </h3>
 *
 * A service provider that is packaged as a JAR file for the class path is
 * identified by placing a <i>provider-configuration file</i> in the resource
 * directory {@code META-INF/services}. The name of the provider-configuration
 * file is the fully qualified binary name of the service. The provider-configuration
 * file contains a list of fully qualified binary names of service providers, one
 * per line.
 *
 * <p> For example, suppose the service provider
 * {@code com.example.impl.StandardCodecs} is packaged in a JAR file for the
 * class path. The JAR file will contain a provider-configuration file named:
 *
 * <blockquote>{@code
 *     META-INF/services/com.example.CodecFactory
 * }</blockquote>
 *
 * that contains the line:
 *
 * <blockquote>{@code
 *     com.example.impl.StandardCodecs # Standard codecs
 * }</blockquote>
 *
 * <p><a id="format">The provider-configuration file must be encoded in UTF-8. </a>
 * Space and tab characters surrounding each service provider's name, as well as
 * blank lines, are ignored. The comment character is {@code '#'}
 * ({@code '&#92;u0023'} <span style="font-size:smaller;">NUMBER SIGN</span>);
 * on each line all characters following the first comment character are ignored.
 * If a service provider class name is listed more than once in a
 * provider-configuration file then the duplicate is ignored. If a service
 * provider class is named in more than one configuration file then the duplicate
 * is ignored.
 *
 * <p> A service provider that is mentioned in a provider-configuration file may
 * be located in the same JAR file as the provider-configuration file or in a
 * different JAR file. The service provider must be visible from the class loader
 * that is initially queried to locate the provider-configuration file; this is
 * not necessarily the class loader which ultimately locates the
 * provider-configuration file.
 *
 * <h3> Timing of provider discovery </h3>
 *
 * <p> Service providers are loaded and instantiated lazily, that is, on demand.
 * A service loader maintains a cache of the providers that have been loaded so
 * far. Each invocation of the {@code iterator} method returns an {@code Iterator}
 * that first yields all of the elements cached from previous iteration, in
 * instantiation order, and then lazily locates and instantiates any remaining
 * providers, adding each one to the cache in turn. Similarly, each invocation
 * of the stream method returns a {@code Stream} that first processes all
 * providers loaded by previous stream operations, in load order, and then lazily
 * locates any remaining providers. Caches are cleared via the {@link #reload
 * reload} method.
 *
 * <h3> <a id="errors">Errors</a> </h3>
 *
 * <p> When using the service loader's {@code iterator}, the {@link
 * Iterator#hasNext() hasNext} and {@link Iterator#next() next} methods will
 * fail with {@link ServiceConfigurationError} if an error occurs locating,
 * loading or instantiating a service provider. When processing the service
 * loader's stream then {@code ServiceConfigurationError} may be thrown by any
 * method that causes a service provider to be located or loaded.
 *
 * <p> When loading or instantiating a service provider in a module, {@code
 * ServiceConfigurationError} can be thrown for the following reasons:
 *
 * <ul>
 *
 *   <li> The service provider cannot be loaded. </li>
 *
 *   <li> The service provider does not declare a provider method, and either
 *   it is not assignable to the service's interface/class or does not have a
 *   provider constructor. </li>
 *
 *   <li> The service provider declares a public static no-args method named
 *   "provider" with a return type that is not assignable to the service's
 *   interface or class. </li>
 *
 *   <li> The service provider class file has more than one public static
 *   no-args method named "{@code provider}". </li>
 *
 *   <li> The service provider declares a provider method and it fails by
 *   returning {@code null} or throwing an exception. </li>
 *
 *   <li> The service provider does not declare a provider method, and its
 *   provider constructor fails by throwing an exception. </li>
 *
 * </ul>
 *
 * <p> When reading a provider-configuration file, or loading or instantiating
 * a provider class named in a provider-configuration file, then {@code
 * ServiceConfigurationError} can be thrown for the following reasons:
 *
 * <ul>
 *
 *   <li> The format of the provider-configuration file violates the <a
 *   href="ServiceLoader.html#format">format</a> specified above; </li>
 *
 *   <li> An {@link IOException IOException} occurs while reading the
 *   provider-configuration file; </li>
 *
 *   <li> A service provider cannot be loaded; </li>
 *
 *   <li> A service provider is not assignable to the service's interface or
 *   class, or does not define a provider constructor, or cannot be
 *   instantiated. </li>
 *
 * </ul>
 *
 * <h3> Security </h3>
 *
 * <p> Service loaders always execute in the security context of the caller
 * of the iterator or stream methods and may also be restricted by the security
 * context of the caller that created the service loader.
 * Trusted system code should typically invoke the methods in this class, and
 * the methods of the iterators which they return, from within a privileged
 * security context.
 *
 * <h3> Concurrency </h3>
 *
 * <p> Instances of this class are not safe for use by multiple concurrent
 * threads.
 *
 * <h3> Null handling </h3>
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method in this class will cause a {@link NullPointerException} to be thrown.
 *
 * @param  <S>
 *         The type of the service to be loaded by this loader
 *
 * @author Mark Reinhold
 * @since 1.6
 * @revised 9
 * @spec JPMS
 */

public final class ServiceLoader<S>
        implements Iterable<S>
{
    // The class or interface representing the service being loaded
    private final Class<S> service;

    // The class of the service type
    private final String serviceName;

    // The lazy-lookup iterator for iterator operations
    private Iterator<Provider<S>> lookupIterator1;
    private final List<S> instantiatedProviders = new ArrayList<>();

    // The lazy-lookup iterator for stream operations
    private Iterator<Provider<S>> lookupIterator2;
    private final List<Provider<S>> loadedProviders = new ArrayList<>();
    private boolean loadedAllProviders; // true when all providers loaded

    // Incremented when reload is called
    private int reloadCount;
 

    /**
     * Represents a service provider located by {@code ServiceLoader}.
     *
     * <p> When using a loader's {@link ServiceLoader#stream() stream()} method
     * then the elements are of type {@code Provider}. This allows processing
     * to select or filter on the provider class without instantiating the
     * provider. </p>
     *
     * @param  <S> The service type
     * @since 9
     * @spec JPMS
     */
    public static interface Provider<S> extends Supplier<S> {
        /**
         * Returns the provider type. There is no guarantee that this type is
         * accessible or that it has a public no-args constructor. The {@link
         * #get() get()} method should be used to obtain the provider instance.
         *
         * <p> When a module declares that the provider class is created by a
         * provider factory then this method returns the return type of its
         * public static "{@code provider()}" method.
         *
         * @return The provider type
         */
        Class<? extends S> type();

        /**
         * Returns an instance of the provider.
         *
         * @return An instance of the provider.
         *
         * @throws ServiceConfigurationError
         *         If the service provider cannot be instantiated, or in the
         *         case of a provider factory, the public static
         *         "{@code provider()}" method returns {@code null} or throws
         *         an error or exception. The {@code ServiceConfigurationError}
         *         will carry an appropriate cause where possible.
         */
        @Override S get();
    }

    /**
     * Initializes a new instance of this class for locating service providers
     * via a class loader.
     *
     * @throws ServiceConfigurationError
     *         If {@code svc} is not accessible to {@code caller} or the caller
     *         module does not use the service type.
     */
    private ServiceLoader(Class<S> svc, ClassLoader cl) {
        Objects.requireNonNull(svc);
        
        this.service = svc;
        this.serviceName = svc.getName();
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                cause);
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
            throws ServiceConfigurationError
    {
        fail(service, u + ":" + line + ": " + msg);
    }

    /**
     * Returns {@code true} if the provider is in an explicit module
     */
    private boolean inExplicitModule(Class<?> clazz) {
//        Module module = clazz.getModule();
//        return module.isNamed() && !module.getDescriptor().isAutomatic();
        return false;
    }

    /**
     * Returns the public static "provider" method if found.
     *
     * @throws ServiceConfigurationError if there is an error finding the
     *         provider method or there is more than one public static
     *         provider method
     */
    private Method findStaticProviderMethod(Class<?> clazz) {
        Method[] methods = null;
        try {
            methods = clazz.getDeclaredMethods();
        } catch (Throwable x) {
            fail(service, "Unable to get public provider() method", x);
        }
        if (methods.length == 0) {
            // does not declare a public provider method
            return null;
        }

        // locate the static methods, can be at most one
        Method result = null;
        for (Method method : methods) {
            int mods = method.getModifiers();
            assert Modifier.isPublic(mods);
            if (Modifier.isStatic(mods)) {
                if (result != null) {
                    fail(service, clazz + " declares more than one"
                            + " public static provider() method");
                }
                result = method;
            }
        }
        if (result != null) {
            result.setAccessible(true);
            return null;
        }
        return result;
    }

    /**
     * Returns the public no-arg constructor of a class.
     *
     * @throws ServiceConfigurationError if the class does not have
     *         public no-arg constructor
     */
    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> ctor = null;
        try {
            ctor = clazz.getConstructor();
            if (inExplicitModule(clazz))
                ctor.setAccessible(true);
        } catch (Throwable x) {
            String cn = clazz.getName();
            fail(service, cn + " Unable to get public no-arg constructor", x);
        }
        return ctor;
    }

    /**
     * A Provider implementation that supports invoking, with reduced
     * permissions, the static factory to obtain the provider or the
     * provider's no-arg constructor.
     */
    private static class ProviderImpl<S> implements Provider<S> {
        final Class<S> service;
        final Class<? extends S> type;
        final Method factoryMethod;  // factory method or null
        final Constructor<? extends S> ctor; // public no-args constructor or null

        ProviderImpl(Class<S> service,
                     Class<? extends S> type,
                     Method factoryMethod) {
            this.service = service;
            this.type = type;
            this.factoryMethod = factoryMethod;
            this.ctor = null;
        }

        ProviderImpl(Class<S> service,
                     Class<? extends S> type,
                     Constructor<? extends S> ctor) {
            this.service = service;
            this.type = type;
            this.factoryMethod = null;
            this.ctor = ctor;
        }

        @Override
        public Class<? extends S> type() {
            return type;
        }

        @Override
        public S get() {
            if (factoryMethod != null) {
                return invokeFactoryMethod();
            } else {
                return newInstance();
            }
        }

        /**
         * Invokes the provider's "provider" method to instantiate a provider.
         * When running with a security manager then the method runs with
         * permissions that are restricted by the security context of whatever
         * created this loader.
         */
        private S invokeFactoryMethod() {
            Object result = null;
            Throwable exc = null;
            try {
                result = factoryMethod.invoke(null);
            } catch (Exception pae) {
                exc = pae.getCause();
            }
            if (exc != null) {
                if (exc instanceof InvocationTargetException)
                    exc = exc.getCause();
                fail(service, factoryMethod + " failed", exc);
            }
            if (result == null) {
                fail(service, factoryMethod + " returned null");
            }
            @SuppressWarnings("unchecked")
            S p = (S) result;
            return p;
        }

        /**
         * Invokes Constructor::newInstance to instantiate a provider. When running
         * with a security manager then the constructor runs with permissions that
         * are restricted by the security context of whatever created this loader.
         */
        private S newInstance() {
            S p = null;
            Throwable exc = null;
            try {
                p = (S)ctor.newInstance();
            } catch (Throwable x) {
                exc = x;
            }
            if (exc != null) {
                if (exc instanceof InvocationTargetException)
                    exc = exc.getCause();
                String cn = ctor.getDeclaringClass().getName();
                fail(service,
                        "Provider " + cn + " could not be instantiated", exc);
            }
            return p;
        }

        // For now, equals/hashCode uses the access control context to ensure
        // that two Providers created with different contexts are not equal
        // when running with a security manager.

        @Override
        public int hashCode() {
            return Objects.hash(service, type);
        }

        @Override
        public boolean equals(Object ob) {
            if (!(ob instanceof ProviderImpl))
                return false;
            @SuppressWarnings("unchecked")
            ProviderImpl<?> that = (ProviderImpl<?>)ob;
            return this.service == that.service
                    && this.type == that.type;
        }
    }

    /**
     * Implements lazy service provider lookup of service providers that
     * are provided by modules defined to a class loader or to modules in
     * layers with a module defined to the class loader.
     */
    private final class ModuleServicesLookupIterator<T>
            implements Iterator<Provider<T>>
    {
        Provider<T> nextProvider;
        ServiceConfigurationError nextError;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Provider<T> next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Provider<T> provider = nextProvider;
            if (provider != null) {
                nextProvider = null;
                return provider;
            } else {
                ServiceConfigurationError e = nextError;
                assert e != null;
                nextError = null;
                throw e;
            }
        }
    }

    /**
     * Returns a new lookup iterator.
     */
    private Iterator<Provider<S>> newLookupIterator() {
        Iterator<Provider<S>> first = new ModuleServicesLookupIterator<>();
        return new Iterator<Provider<S>>() {
            @Override
            public boolean hasNext() {
                return (first.hasNext());
            }
            @Override
            public Provider<S> next() {
                if (first.hasNext()) {
                    return first.next();
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    /**
     * Returns an iterator to lazily load and instantiate the available
     * providers of this loader's service.
     *
     * <p> To achieve laziness the actual work of locating and instantiating
     * providers is done by the iterator itself. Its {@link Iterator#hasNext
     * hasNext} and {@link Iterator#next next} methods can therefore throw a
     * {@link ServiceConfigurationError} for any of the reasons specified in
     * the <a href="#errors">Errors</a> section above. To write robust code it
     * is only necessary to catch {@code ServiceConfigurationError} when using
     * the iterator. If an error is thrown then subsequent invocations of the
     * iterator will make a best effort to locate and instantiate the next
     * available provider, but in general such recovery cannot be guaranteed.
     *
     * <p> Caching: The iterator returned by this method first yields all of
     * the elements of the provider cache, in the order that they were loaded.
     * It then lazily loads and instantiates any remaining service providers,
     * adding each one to the cache in turn. If this loader's provider caches are
     * cleared by invoking the {@link #reload() reload} method then existing
     * iterators for this service loader should be discarded.
     * The {@code  hasNext} and {@code next} methods of the iterator throw {@link
     * java.util.ConcurrentModificationException ConcurrentModificationException}
     * if used after the provider cache has been cleared.
     *
     * <p> The iterator returned by this method does not support removal.
     * Invoking its {@link java.util.Iterator#remove() remove} method will
     * cause an {@link UnsupportedOperationException} to be thrown.
     *
     * @apiNote Throwing an error in these cases may seem extreme.  The rationale
     * for this behavior is that a malformed provider-configuration file, like a
     * malformed class file, indicates a serious problem with the way the Java
     * virtual machine is configured or is being used.  As such it is preferable
     * to throw an error rather than try to recover or, even worse, fail silently.
     *
     * @return  An iterator that lazily loads providers for this loader's
     *          service
     *
     * @revised 9
     * @spec JPMS
     */
    public Iterator<S> iterator() {

        // create lookup iterator if needed
        if (lookupIterator1 == null) {
            lookupIterator1 = newLookupIterator();
        }

        return new Iterator<S>() {

            // record reload count
            final int expectedReloadCount = ServiceLoader.this.reloadCount;

            // index into the cached providers list
            int index;

            /**
             * Throws ConcurrentModificationException if the list of cached
             * providers has been cleared by reload.
             */
            private void checkReloadCount() {
                if (ServiceLoader.this.reloadCount != expectedReloadCount)
                    throw new ConcurrentModificationException();
            }

            @Override
            public boolean hasNext() {
                checkReloadCount();
                if (index < instantiatedProviders.size())
                    return true;
                return lookupIterator1.hasNext();
            }

            @Override
            public S next() {
                checkReloadCount();
                S next;
                if (index < instantiatedProviders.size()) {
                    next = instantiatedProviders.get(index);
                } else {
                    next = lookupIterator1.next().get();
                    instantiatedProviders.add(next);
                }
                index++;
                return next;
            }

        };
    }

    private class ProviderSpliterator<T> implements Spliterator<Provider<T>> {
        final int expectedReloadCount = ServiceLoader.this.reloadCount;
        final Iterator<Provider<T>> iterator;
        int index;

        ProviderSpliterator(Iterator<Provider<T>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Spliterator<Provider<T>> trySplit() {
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean tryAdvance(Consumer<? super Provider<T>> action) {
            if (ServiceLoader.this.reloadCount != expectedReloadCount)
                throw new ConcurrentModificationException();
            Provider<T> next = null;
            if (index < loadedProviders.size()) {
                next = (Provider<T>) loadedProviders.get(index++);
            } else if (iterator.hasNext()) {
                next = iterator.next();
            } else {
                loadedAllProviders = true;
            }
            if (next != null) {
                action.accept(next);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int characteristics() {
            // not IMMUTABLE as structural interference possible
            // not NOTNULL so that the characteristics are a subset of the
            // characteristics when all Providers have been located.
            return Spliterator.ORDERED;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Creates a new service loader for the given service. The service loader
     * uses the given class loader as the starting point to locate service
     * providers for the service. The service loader's {@link #iterator()
     * iterator} and {@link #stream() stream} locate providers in both named
     * and unnamed modules, as follows:
     *
     * <ul>
     *   <li> <p> Step 1: Locate providers in named modules. </p>
     *
     *   <p> Service providers are located in all named modules of the class
     *   loader or to any class loader reachable via parent delegation. </p>
     *
     *   <p> In addition, if the class loader is not the bootstrap or {@linkplain
     *   ClassLoader#getPlatformClassLoader() platform class loader}, then service
     *   providers may be located in the named modules of other class loaders.
     *   Specifically, if the class loader, or any class loader reachable via
     *   parent delegation, has a module in a {@linkplain ModuleLayer module
     *   layer}, then service providers in all modules in the module layer are
     *   located.  </p>
     *
     *   <p> For example, suppose there is a module layer where each module is
     *   in its own class loader (see {@link ModuleLayer#defineModulesWithManyLoaders
     *   defineModulesWithManyLoaders}). If this {@code ServiceLoader.load} method
     *   is invoked to locate providers using any of the class loaders created for
     *   the module layer, then it will locate all of the providers in the module
     *   layer, irrespective of their defining class loader. </p>
     *
     *   <p> Ordering: The service loader will first locate any service providers
     *   in modules defined to the class loader, then its parent class loader,
     *   its parent parent, and so on to the bootstrap class loader. If a class
     *   loader has modules in a module layer then all providers in that module
     *   layer are located (irrespective of their class loader) before the
     *   providers in the parent class loader are located. The ordering of
     *   modules in same class loader, or the ordering of modules in a module
     *   layer, is not defined. </p>
     *
     *   <p> If a module declares more than one provider then the providers
     *   are located in the order that its module descriptor {@linkplain
     *   java.lang.module.ModuleDescriptor.Provides#providers() lists the
     *   providers}. Providers added dynamically by instrumentation agents (see
     *   {@link java.lang.instrument.Instrumentation#redefineModule redefineModule})
     *   are always located after providers declared by the module. </p> </li>
     *
     *   <li> <p> Step 2: Locate providers in unnamed modules. </p>
     *
     *   <p> Service providers in unnamed modules are located if their class names
     *   are listed in provider-configuration files located by the class loader's
     *   {@link ClassLoader#getResources(String) getResources} method. </p>
     *
     *   <p> The ordering is based on the order that the class loader's {@code
     *   getResources} method finds the service configuration files and within
     *   that, the order that the class names are listed in the file. </p>
     *
     *   <p> In a provider-configuration file, any mention of a service provider
     *   that is deployed in a named module is ignored. This is to avoid
     *   duplicates that would otherwise arise when a named module has both a
     *   <i>provides</i> directive and a provider-configuration file that mention
     *   the same service provider. </p>
     *
     *   <p> The provider class must be visible to the class loader. </p> </li>
     *
     * </ul>
     *
     * @apiNote If the class path of the class loader includes remote network
     * URLs then those URLs may be dereferenced in the process of searching for
     * provider-configuration files.
     *
     * <p> This activity is normal, although it may cause puzzling entries to be
     * created in web-server logs.  If a web server is not configured correctly,
     * however, then this activity may cause the provider-loading algorithm to fail
     * spuriously.
     *
     * <p> A web server should return an HTTP 404 (Not Found) response when a
     * requested resource does not exist.  Sometimes, however, web servers are
     * erroneously configured to return an HTTP 200 (OK) response along with a
     * helpful HTML error page in such cases.  This will cause a {@link
     * ServiceConfigurationError} to be thrown when this class attempts to parse
     * the HTML page as a provider-configuration file.  The best solution to this
     * problem is to fix the misconfigured web server to return the correct
     * response code (HTTP 404) along with the HTML error page.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @param  loader
     *         The class loader to be used to load provider-configuration files
     *         and provider classes, or {@code null} if the system class
     *         loader (or, failing that, the bootstrap class loader) is to be
     *         used
     *
     * @return A new service loader
     *
     * @throws ServiceConfigurationError
     *         if the service type is not accessible to the caller or the
     *         caller is in an explicit module and its module descriptor does
     *         not declare that it uses {@code service}
     *
     * @revised 9
     * @spec JPMS
     */
    public static <S> ServiceLoader<S> load(Class<S> service,
                                            ClassLoader loader)
    {
        return new ServiceLoader<>(service, loader);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * current thread's {@linkplain java.lang.Thread#getContextClassLoader
     * context class loader}.
     *
     * <p> An invocation of this convenience method of the form
     * <pre>{@code
     *     ServiceLoader.load(service)
     * }</pre>
     *
     * is equivalent to
     *
     * <pre>{@code
     *     ServiceLoader.load(service, Thread.currentThread().getContextClassLoader())
     * }</pre>
     *
     * @apiNote Service loader objects obtained with this method should not be
     * cached VM-wide. For example, different applications in the same VM may
     * have different thread context class loaders. A lookup by one application
     * may locate a service provider that is only visible via its thread
     * context class loader and so is not suitable to be located by the other
     * application. Memory leaks can also arise. A thread local may be suited
     * to some applications.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     *
     * @throws ServiceConfigurationError
     *         if the service type is not accessible to the caller or the
     *         caller is in an explicit module and its module descriptor does
     *         not declare that it uses {@code service}
     *
     * @revised 9
     * @spec JPMS
     */
    public static <S> ServiceLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return new ServiceLoader<>(service, cl);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * {@linkplain ClassLoader#getPlatformClassLoader() platform class loader}.
     *
     * <p> This convenience method is equivalent to: </p>
     *
     * <pre>{@code
     *     ServiceLoader.load(service, ClassLoader.getPlatformClassLoader())
     * }</pre>
     *
     * <p> This method is intended for use when only installed providers are
     * desired.  The resulting service will only find and load providers that
     * have been installed into the current Java virtual machine; providers on
     * the application's module path or class path will be ignored.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     *
     * @throws ServiceConfigurationError
     *         if the service type is not accessible to the caller or the
     *         caller is in an explicit module and its module descriptor does
     *         not declare that it uses {@code service}
     *
     * @revised 9
     * @spec JPMS
     */
    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        return new ServiceLoader<>(service, cl);
    }

    /**
     * Creates a new service loader for the given service type to load service
     * providers from modules in the given module layer and its ancestors. It
     * does not locate providers in unnamed modules. The ordering that the service
     * loader's {@link #iterator() iterator} and {@link #stream() stream} locate
     * providers and yield elements is as follows:
     *
     * <ul>
     *   <li><p> Providers are located in a module layer before locating providers
     *   in parent layers. Traversal of parent layers is depth-first with each
     *   layer visited at most once. For example, suppose L0 is the boot layer, L1
     *   and L2 are modules layers with L0 as their parent. Now suppose that L3 is
     *   created with L1 and L2 as the parents (in that order). Using a service
     *   loader to locate providers with L3 as the context will locate providers
     *   in the following order: L3, L1, L0, L2. </p></li>
     *
     *   <li><p> If a module declares more than one provider then the providers
     *   are located in the order that its module descriptor
     *   {@linkplain java.lang.module.ModuleDescriptor.Provides#providers()
     *   lists the providers}. Providers added dynamically by instrumentation
     *   agents are always located after providers declared by the module. </p></li>
     *
     *   <li><p> The ordering of modules in a module layer is not defined. </p></li>
     * </ul>
     *
     * @apiNote Unlike the other load methods defined here, the service type
     * is the second parameter. The reason for this is to avoid source
     * compatibility issues for code that uses {@code load(S, null)}.
     *
     * @param  <S> the class of the service type
     *
     * @param  layer
     *         The module layer
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     *
     * @throws ServiceConfigurationError
     *         if the service type is not accessible to the caller or the
     *         caller is in an explicit module and its module descriptor does
     *         not declare that it uses {@code service}
     *
     * @since 9
     * @spec JPMS
     */
//    public static <S> ServiceLoader<S> load(ModuleLayer layer, Class<S> service) {
//        return new ServiceLoader<>(layer, service);
//    }

    /**
     * Load the first available service provider of this loader's service. This
     * convenience method is equivalent to invoking the {@link #iterator()
     * iterator()} method and obtaining the first element. It therefore
     * returns the first element from the provider cache if possible, it
     * otherwise attempts to load and instantiate the first provider.
     *
     * <p> The following example loads the first available service provider. If
     * no service providers are located then it uses a default implementation.
     * <pre>{@code
     *    CodecFactory factory = ServiceLoader.load(CodecFactory.class)
     *                                        .findFirst()
     *                                        .orElse(DEFAULT_CODECSET_FACTORY);
     * }</pre>
     * @return The first service provider or empty {@code Optional} if no
     *         service providers are located
     *
     * @throws ServiceConfigurationError
     *         If a provider class cannot be loaded for any of the reasons
     *         specified in the <a href="#errors">Errors</a> section above.
     *
     * @since 9
     * @spec JPMS
     */
    public Optional<S> findFirst() {
        Iterator<S> iterator = iterator();
        if (iterator.hasNext()) {
            return Optional.of(iterator.next());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Clear this loader's provider cache so that all providers will be
     * reloaded.
     *
     * <p> After invoking this method, subsequent invocations of the {@link
     * #iterator() iterator} or {@link #stream() stream} methods will lazily
     * locate providers (and instantiate in the case of {@code iterator})
     * from scratch, just as is done by a newly-created service loader.
     *
     * <p> This method is intended for use in situations in which new service
     * providers can be installed into a running Java virtual machine.
     */
    public void reload() {
        lookupIterator1 = null;
        instantiatedProviders.clear();

        lookupIterator2 = null;
        loadedProviders.clear();
        loadedAllProviders = false;

        // increment count to allow CME be thrown
        reloadCount++;
    }

    /**
     * Returns a string describing this service.
     *
     * @return  A descriptive string
     */
    public String toString() {
        return "java.util.ServiceLoader[" + service.getName() + "]";
    }

}