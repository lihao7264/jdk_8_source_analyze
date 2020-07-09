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

/**
 * An {@code AtomicStampedReference} maintains an object reference
 * along with an integer "stamp", that can be updated atomically.
 *
 * <p>Implementation note: This implementation maintains stamped
 * references by creating internal objects representing "boxed"
 * [reference, integer] pairs.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <V> The type of object referred to by this reference
 * 参考地址：https://segmentfault.com/a/1190000020601800
 * 情况一：
 *   如果元素值和版本号都没有变化，并且和新的也相同，返回true。
 *
 * 情况二：
 *   如果元素值和版本号都没有变化，并且和新的不完全相同，就构造一个新的Pair对象并执行CAS更新pair。
 *
 * 解决ABA问题的方法：
 *   a、使用版本号控制
 *   b、不重复使用节点（Pair）的引用，每次都新建一个新的Pair来作为CAS比较的对象，而不是复用旧的
 *   c、外部传入元素值及版本号，而不是节点（Pair）的引用
 */
public class AtomicStampedReference<V> {

    /**
     * 将值和版本号封装为一个Pair，比较就是比较这个Pair
     * @param <T>
     */
    private static class Pair<T> {
        final T reference;
        //时间戳
        final int stamp;
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }

        //创建一个Pair对象
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }

    // 多个线程同时修改这个pair要可见。比如：一直自加到100
    private volatile Pair<V> pair;

    /**
     * Creates a new {@code AtomicStampedReference} with the given
     * initial values.
     * 构造AtomicStampedReference时候把要多线程修改的值封装成pair
     *
     * @param initialRef the initial reference
     * @param initialStamp the initial stamp
     */
    public AtomicStampedReference(V initialRef, int initialStamp) {
        pair = Pair.of(initialRef, initialStamp);
    }

    /**
     * Returns the current value of the reference.
     *
     * 获取准备通过AtomicStampedReference来改变的值。
     *
     * @return the current value of the reference
     */
    public V getReference() {
        return pair.reference;
    }

    /**
     * Returns the current value of the stamp.
     *
     * 获取准备通过AtomicStampedReference来改变的值的版本号。
     *
     * @return the current value of the stamp
     */
    public int getStamp() {
        return pair.stamp;
    }

    /**
     * Returns the current values of both the reference and the stamp.
     * Typical usage is {@code int[1] holder; ref = v.get(holder); }.
     *
     * @param stampHolder an array of size of at least one.  On return,
     * {@code stampholder[0]} will hold the value of the stamp.
     * @return the current value of the reference
     */
    public V get(int[] stampHolder) {
        Pair<V> pair = this.pair;
        stampHolder[0] = pair.stamp;
        return pair.reference;
    }

    /**
     * Atomically sets the value of both the reference and stamp
     * to the given update values if the
     * current reference is {@code ==} to the expected reference
     * and the current stamp is equal to the expected stamp.
     *
     * <p><a href="package-summary.html#weakCompareAndSet">May fail
     * spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedStamp the expected value of the stamp
     * @param newStamp the new value for the stamp
     * @return {@code true} if successful
     */
    public boolean weakCompareAndSet(V   expectedReference,
                                     V   newReference,
                                     int expectedStamp,
                                     int newStamp) {
        return compareAndSet(expectedReference, newReference,
                             expectedStamp, newStamp);
    }

    /**
     * Atomically sets the value of both the reference and stamp
     * to the given update values if the
     * current reference is {@code ==} to the expected reference
     * and the current stamp is equal to the expected stamp.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedStamp the expected value of the stamp
     * @param newStamp the new value for the stamp
     * @return {@code true} if successful
     *
     */
    public boolean compareAndSet(V   expectedReference,
                                 V   newReference,
                                 int expectedStamp,
                                 int newStamp) {
        // 获取当前的（元素值，版本号）对
        Pair<V> current = pair;
        return
            // 引用没变
            expectedReference == current.reference &&
            // 版本号没变
            expectedStamp == current.stamp &&

            // 新引用等于旧引用
            ((newReference == current.reference &&
              // 新版本号等于旧版本号
              newStamp == current.stamp) ||  //使用短路||
             // 构造新的Pair对象并CAS更新
             casPair(current, Pair.of(newReference, newStamp)));
    }

    /**
     * Unconditionally sets the value of both the reference and stamp.
     *
     * @param newReference the new value for the reference
     * @param newStamp the new value for the stamp
     */
    public void set(V newReference, int newStamp) {
        Pair<V> current = pair;
        if (newReference != current.reference || newStamp != current.stamp)
            this.pair = Pair.of(newReference, newStamp);
    }

    /**
     * Atomically sets the value of the stamp to the given update value
     * if the current reference is {@code ==} to the expected
     * reference.  Any given invocation of this operation may fail
     * (return {@code false}) spuriously, but repeated invocation
     * when the current value holds the expected value and no other
     * thread is also attempting to set the value will eventually
     * succeed.
     *
     * @param expectedReference the expected value of the reference
     * @param newStamp the new value for the stamp
     * @return {@code true} if successful
     */
    public boolean attemptStamp(V expectedReference, int newStamp) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            (newStamp == current.stamp ||
             casPair(current, Pair.of(expectedReference, newStamp)));
    }

    // Unsafe mechanics

    private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();

    //声明一个Pair类型的变量并使用Unsfae获取其偏移量，存储到pairOffset中
    private static final long pairOffset =
        objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);

    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        // 调用Unsafe的compareAndSwapObject()方法CAS更新pair的引用为新引用
        return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
    }

    static long objectFieldOffset(sun.misc.Unsafe UNSAFE,
                                  String field, Class<?> klazz) {
        try {
            return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            // Convert Exception to corresponding Error
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }
}
