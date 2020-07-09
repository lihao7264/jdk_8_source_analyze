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

package java.util.concurrent.atomic;
import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import sun.misc.Unsafe;

/**
 * An object reference that may be updated atomically. See the {@link
 * java.util.concurrent.atomic} package specification for description
 * of the properties of atomic variables.
 * @since 1.5
 * @author Doug Lea
 * @param <V> The type of object referred to by this reference
 *
 * AtomicReference是对应普通的对象引用。也就是它可以保证你在修改对象引用时的线程安全性。
 * AtmicReference的ABA问题的解决方法：
 *  a、
 *
 * ABA问题：线程判断被修改对象是否可以正确写入的条件是对象的当前值和期望是否一致。这个逻辑从一般意义上来说是正确的。
 *       但有可能出现一个小小的例外，就是当你获得对象当前数据后，在准备修改为新值前，对象的值被其他线程连续修改了2次，
 *       而经过这2次修改后，对象的值又恢复为旧值。这样，当前线程就无法正确判断这个对象究竟是否被修改过
 *
 */
public class AtomicReference<V> implements java.io.Serializable {
    private static final long serialVersionUID = -1848883965231344442L;

    //unsafe类,提供cas操作的功能
    private static final Unsafe unsafe = Unsafe.getUnsafe();

    //value变量的偏移地址,这个偏移地址在static块里初始化
    private static final long valueOffset;

    //静态代码块：类装载的时候初始化偏移地址。
    static {
        try {
            //通过unsafe变量获取valueOffset的偏移量
            //利用Unsafe类的内部方法进行对“value”获取其内存的偏移地址的，利用static的特性在类加载到内存中就获取到valueOffset的值.
            valueOffset = unsafe.objectFieldOffset
                (AtomicReference.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    //实际传入需要原子操作的那个类实例
    private volatile V value;

    /**
     * Creates a new AtomicReference with the given initial value.
     * 使用给定的初始值创建一个新的AtomicReference，即设置当前AtomicReference的值
     * @param initialValue the initial value
     */
    public AtomicReference(V initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicReference with null initial value.
     * 默认的构造方法
     */
    public AtomicReference() {
    }

    /**
     * Gets the current value.
     * 直接返回变量value
     * @return the current value
     */
    public final V get() {
        return value;
    }

    /**
     * Sets to the given value.
     *  非原子操作-慎用
     *  通过参数newValue将变量value进行值的更新
     * @param newValue the new value
     */
    public final void set(V newValue) {
        value = newValue;
    }

    /**
     * Eventually sets to the given value.
     * 通过 unsafe 实现原子操作
     * 通过unsafe变量最终设置为给定的值。
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(V newValue) {
        unsafe.putOrderedObject(this, valueOffset, newValue);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * 通过 unsafe 实现原子操作
     * CAS方法，利用判断旧值符合预期值并且更新新的值
     * @param expect the expected value 希望值：拿当前值value和希望值去比较，如果相等返回true并更新值为update值
     * @param update the new value  需要更新的值
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     *
     * 调用Unsafe的cas操作,传入对象、expect值、偏移地址、需要更新的值即可,如果更新成功,返回true,如果失败,返回false
     * 注意：
     *    a、有个坑：对于String变量来说,必须是对象相同才视为相同,而不是字符串的内容相同就可以相同
     */
    public final boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * <p><a href="package-summary.html#weakCompareAndSet">May fail
     * spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful
     */
    public final boolean weakCompareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    /**
     * Atomically sets to the given value and returns the old value.
     * 先获取旧值再更新新值，还是利用unsafe的内部方法来进行操作。
     * @param newValue the new value
     * @return the previous value
     */
    @SuppressWarnings("unchecked")
    public final V getAndSet(V newValue) {
        return (V)unsafe.getAndSetObject(this, valueOffset, newValue);
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the previous value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * 获取旧值，并且cas更新
     * @param updateFunction a side-effect-free function
     * @return the previous value
     * @since 1.8
     */
    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        V prev, next;
        do {
            prev = get();
            next = updateFunction.apply(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * 获取新值，并且cas更新
     * @param updateFunction a side-effect-free function
     * @return the updated value
     * @since 1.8
     */
    public final V updateAndGet(UnaryOperator<V> updateFunction) {
        V prev, next;
        do {
            // 获取当前值
            prev = get();
            next = updateFunction.apply(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the previous value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     * 获取旧值，并且cas更新
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the previous value
     * @since 1.8
     */
    public final V getAndAccumulate(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev, next;
        do {
            prev = get();
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the updated value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     * 获取新值，并且cas更新
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the updated value
     * @since 1.8
     */
    public final V accumulateAndGet(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev, next;
        do {
            prev = get();
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return String.valueOf(get());
    }

}
