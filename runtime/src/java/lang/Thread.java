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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A thread is a thread of execution in a program. The Java Virtual Machine allows an application to have multiple threads of execution running concurrently.
 * Every thread has a priority. Threads with higher priority are executed in preference to threads with lower priority.
 * There are two ways to create a new thread of execution. One is to declare a class to be a subclass of Thread. This subclass should override the run method of class Thread. An instance of the subclass can then be allocated and started. For example, a thread that computes primes larger than a stated value could be written as follows:
 */
public class Thread implements java.lang.Runnable{
    /**
     * The maximum priority that a thread can have.
     * See Also:Constant Field Values
     */
    public static final int MAX_PRIORITY=10;

    /**
     * The minimum priority that a thread can have.
     * See Also:Constant Field Values
     */
    public static final int MIN_PRIORITY=1;

    /**
     * The default priority that is assigned to a thread.
     * See Also:Constant Field Values
     */
    public static final int NORM_PRIORITY=5;

    private static long currentId;
    private Runnable target;
    private volatile boolean alive;
    private String name;
    private long entrypoint;
    private static volatile int activeThreads;
    private volatile boolean interrupted;
    private long id;
    private volatile boolean started;
    private long nativeContext;

    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
    private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private static final List<Thread> threads = new ArrayList<>();
    
    /**
     * Allocates a new Thread object.
     * Threads created this way must have overridden their run() method to actually do anything.
     */
    public Thread(){
        this(null, null);
    }

    /**
     * Main thread entrypoint (Takes main function pointer)
     */
    public Thread(long entrypoint) {
        this(null, "main");
        this.entrypoint = entrypoint;
    }

    /**
     * Allocates a new Thread object with a specific target object whose run method is called.
     * target - the object whose run method is called.
     */
    public Thread(java.lang.Runnable target){
         this(target, null);
    }

    /**
     * Allocates a new Thread object with the given target and name.
     * target - the object whose run method is called.name - the name of the new thread.
     */
    public Thread(java.lang.Runnable target, java.lang.String name){
         this.target = target;
         this.name = name;
         synchronized (Thread.class) {
             id = currentId++;
         }
         if (this.name == null)
             this.name = "Unnamed " + id;
    }

    /**
     * Allocates a new Thread object with the given name. Threads created this way must have overridden their run() method to actually do anything.
     * name - the name of the new thread.
     */
    public Thread(java.lang.String name){
         this.name = name;
    }

    /**
     * Returns the current number of active threads in the virtual machine.
     */
    public static int activeCount() {
        return activeThreads;
    }

    public long getId() {
        return id;
    }

    /**
     * Returns a reference to the currently executing Thread object.
     */
    public native static java.lang.Thread currentThread();

    /**
     * Returns this thread's name. Note that in CLDC the name of the thread can only be set when creating the thread.
     */
    public final java.lang.String getName(){
        return name; 
    }

    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this thread's priority.
     */
    public final int getPriority(){
        return 0;
    }

    /**
     * Interrupts this thread. In an implementation conforming to the CLDC Specification, this operation is not required to cancel or clean up any pending I/O operations that the thread may be waiting for.
     */
    public native void interrupt();

    /**
     * Tests if this thread is alive. A thread is alive if it has been started and has not yet died.
     */
    public final boolean isAlive(){
        return alive;
    }

    public static boolean interrupted() {
        Thread current = currentThread();
        boolean interrupted = current.interrupted;
        current.interrupted = false;
        return interrupted;
    }

    public final boolean isInterrupted() {
        return interrupted;
    }

    public final boolean isDaemon(){
        return false;
    }

    public final void setDaemon(boolean on) {
//        throw new UnsupportedOperationException();
    }

    /**
     * Waits for this thread to die.
     */
    public final synchronized void join() throws java.lang.InterruptedException {
        while (alive)
            wait();
    }

    public final synchronized void join(final long millis) throws java.lang.InterruptedException {
        if (millis == 0) {
            join();
            return;
        }
        long delay = millis;
        long start = System.currentTimeMillis();
        do {
            wait(delay);
        } while (alive && (delay = millis - (System.currentTimeMillis() - start)) > 0);
    }

    public final void join(long millis, int nanos) throws java.lang.InterruptedException {
        join(millis);
    }

    /**
     * If this thread was constructed using a separate Runnable run object, then that Runnable object's run method is called; otherwise, this method does nothing and returns.
     * Subclasses of Thread should override this method.
     */
    public void run(){
        if(target != null) {
            target.run();
        }
    }

    /**
     * Changes the priority of this thread.
     */
    public final void setPriority(int newPriority){
    }

    /**
     * Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of milliseconds. The thread does not lose ownership of any monitors.
     */
    public static void sleep(long millis) throws java.lang.InterruptedException {
        sleep(millis, 0);
    }

    public static void sleep(long millis, int nanos)
        throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                "nanosecond timeout value out of range");
        }

        sleepImpl(millis, nanos);
    }

    private static native void sleepImpl(long millis, int nanos);

    /**
     * Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread.
     * The result is that two threads are running concurrently: the current thread (which returns from the call to the start method) and the other thread (which executes its run method).
     */
    public native void start();

    public final void stop() {}

    public final void suspend() {}

    public final void resume() {}
    
    /**
     * Returns a string representation of this thread, including the thread's name and priority.
     */
    public java.lang.String toString(){
        return "Thread " + name; 
    }

    public native StackTraceElement[] getStackTrace();

    /**
     * Causes the currently executing thread object to temporarily pause and allow other threads to execute.
     */
    public static void yield(){
        try {
            sleep(1); 
        } catch(InterruptedException i) {}
    }

    @Override
    protected native void finalize() throws Throwable;

    public void setContextClassLoader(ClassLoader cl) {
    }

    public ClassLoader getContextClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        uncaughtExceptionHandler = handler;
    }

    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        defaultUncaughtExceptionHandler = handler;
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler () {
        return uncaughtExceptionHandler;
    }

    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler () {
        return defaultUncaughtExceptionHandler;
    }

    public interface UncaughtExceptionHandler {

        void uncaughtException(Thread t, Throwable e);
    }

    public State getState() {
        return !started ? State.NEW : alive ? State.RUNNABLE : State.TERMINATED;
    }

    public final ThreadGroup getThreadGroup() {
        return null;
    }
    
    public enum State {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED
    }
}
