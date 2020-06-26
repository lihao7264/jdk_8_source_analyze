/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;

/**
 * A task that returns a result and may throw an exception.
 * Implementors define a single method with no arguments called
 * {@code call}.
 *
 * <p>The {@code Callable} interface is similar to {@link
 * java.lang.Runnable}, in that both are designed for classes whose
 * instances are potentially executed by another thread.  A
 * {@code Runnable}, however, does not return a result and cannot
 * throw a checked exception.
 *
 * <p>The {@link Executors} class contains utility methods to
 * convert from other common forms to {@code Callable} classes.
 *
 * 一个返回结果并可能引发异常的任务。
 * 实现者定义了一个没有参数的单一方法，称为{@code call}。
 *
 * {@code Callable} 接口与{@link java.lang.Runnable}相似，
 * 因为两者都是针对其实例可能由另一个线程执行的类设计的。
 * 但是，{@code Runnable}不会返回结果，也不能引发已检查的异常。
 *
 * {@link Executors}类包含实用程序方法，
 * 可以从其它常见形式转换为{@code Callable}类。
 *
 * @see Executor
 * @since 1.5
 * @author Doug Lea
 * @param <V> the result type of method {@code call}   方法{@code call}的结果类型
 */
@FunctionalInterface
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     * 计算结果，如果无法执行，则抛出异常。
     *
     * @return computed result  计算结果
     * @throws Exception if unable to compute a result   如果无法计算结果则异常
     */
    V call() throws Exception;
}
