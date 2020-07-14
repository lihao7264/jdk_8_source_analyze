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
import java.util.function.LongBinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A package-local class holding common representation and mechanics
 * for classes supporting dynamic striping on 64bit values. The class
 * extends Number so that concrete subclasses must publicly do so.
 */
@SuppressWarnings("serial")
abstract class Striped64 extends Number {
    /*
     * This class maintains a lazily-initialized table of atomically
     * updated variables, plus an extra "base" field. The table size
     * is a power of two. Indexing uses masked per-thread hash codes.
     * Nearly all declarations in this class are package-private,
     * accessed directly by subclasses.
     *
     * Table entries are of class Cell; a variant of AtomicLong padded
     * (via @sun.misc.Contended) to reduce cache contention. Padding
     * is overkill for most Atomics because they are usually
     * irregularly scattered in memory and thus don't interfere much
     * with each other. But Atomic objects residing in arrays will
     * tend to be placed adjacent to each other, and so will most
     * often share cache lines (with a huge negative performance
     * impact) without this precaution.
     *
     * In part because Cells are relatively large, we avoid creating
     * them until they are needed.  When there is no contention, all
     * updates are made to the base field.  Upon first contention (a
     * failed CAS on base update), the table is initialized to size 2.
     * The table size is doubled upon further contention until
     * reaching the nearest power of two greater than or equal to the
     * number of CPUS. Table slots remain empty (null) until they are
     * needed.
     *
     * A single spinlock ("cellsBusy") is used for initializing and
     * resizing the table, as well as populating slots with new Cells.
     * There is no need for a blocking lock; when the lock is not
     * available, threads try other slots (or the base).  During these
     * retries, there is increased contention and reduced locality,
     * which is still better than alternatives.
     *
     * The Thread probe fields maintained via ThreadLocalRandom serve
     * as per-thread hash codes. We let them remain uninitialized as
     * zero (if they come in this way) until they contend at slot
     * 0. They are then initialized to values that typically do not
     * often conflict with others.  Contention and/or table collisions
     * are indicated by failed CASes when performing an update
     * operation. Upon a collision, if the table size is less than
     * the capacity, it is doubled in size unless some other thread
     * holds the lock. If a hashed slot is empty, and lock is
     * available, a new Cell is created. Otherwise, if the slot
     * exists, a CAS is tried.  Retries proceed by "double hashing",
     * using a secondary hash (Marsaglia XorShift) to try to find a
     * free slot.
     *
     * The table size is capped because, when there are more threads
     * than CPUs, supposing that each thread were bound to a CPU,
     * there would exist a perfect hash function mapping threads to
     * slots that eliminates collisions. When we reach capacity, we
     * search for this mapping by randomly varying the hash codes of
     * colliding threads.  Because search is random, and collisions
     * only become known via CAS failures, convergence can be slow,
     * and because threads are typically not bound to CPUS forever,
     * may not occur at all. However, despite these limitations,
     * observed contention rates are typically low in these cases.
     *
     * It is possible for a Cell to become unused when threads that
     * once hashed to it terminate, as well as in the case where
     * doubling the table causes no thread to hash to it under
     * expanded mask.  We do not try to detect or remove such cells,
     * under the assumption that for long-running instances, observed
     * contention levels will recur, so the cells will eventually be
     * needed again; and for short-lived ones, it does not matter.
     */

    /**
     * Padded variant of AtomicLong supporting only raw accesses plus CAS.
     *
     * JVM intrinsics note: It would be possible to use a release-only
     * form of CAS here, if it were provided.
     * Striped64中的内部类，使用@sun.misc.Contended注解，说明里面的值消除伪共享(一个64字节的缓存行只存储一个值)
     */
    @sun.misc.Contended static final class Cell {
        // 存储元素的值，使用volatile修饰保证可见性
        volatile long value;
        Cell(long x) { value = x; }
        // CAS更新value的值
        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        // Unsafe mechanics
        // Unsafe实例
        private static final sun.misc.Unsafe UNSAFE;
        // value字段的偏移量
        private static final long valueOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset
                    (ak.getDeclaredField("value"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /** Number of CPUS, to place bound on table size */
    // CPUS的数量，要限制在表大小上
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * Table of cells. When non-null, size is a power of 2.
     *  cells数组：存储各个段的值
     *  在非空情况下一定是2的幂长度
     */
    transient volatile Cell[] cells;

    /**
     * Base value, used mainly when there is no contention, but also as
     * a fallback during table initialization races. Updated via CAS.
     * 最初无竞争时使用的，也算一个特殊的段
     * 也同时充当table在初始化期间的竞争后备，通过CAS操作进行更新.
     */
    transient volatile long base;

    /**
     * Spinlock (locked via CAS) used when resizing and/or creating Cells.
     * 标记当前是否有线程在创建或扩容cells或者在创建Cell
     * 通过CAS更新该值，相当于是一个锁
     * 自旋锁 (通过CAS操作加锁)用于在对Cells做创建或者调整大小的时候进行加锁。
     */
    transient volatile int cellsBusy;

    /**
     * Package-private default constructor
     * 默认函数使用default修饰，也就是包内有效。
     */
    Striped64() {
    }

    /**
     * CASes the base field.
     * cas更新base字段
     */
    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
    }

    /**
     * CASes the cellsBusy field from 0 to 1 to acquire lock.
     * cas方式，将cellBusy字段从0设为1，以标示获取锁。
     */
    final boolean casCellsBusy() {
        return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
    }

    /**
     * Returns the probe value for the current thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     * 返回当前线程的标示。
     * 由于包限制，这段代码是从ThreadLocalRandom拷贝过来的
     */
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    /**
     * Pseudo-randomly advances and records the given probe value for the
     * given thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     *
     * 利用伪随机算法加强标识后，将为当前线程记录这个标识。
     * 由于包限制，这段代码是从ThreadLocalRandom拷贝过来的
     */
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    /**
     * Handles cases of updates involving initialization, resizing,
     * creating new Cells, and/or contention. See above for
     * explanation. This method suffers the usual non-modularity
     * problems of optimistic retry code, relying on rechecked sets of
     * reads.
     * 这个方法处理初始化、调整大小、创建新Cells和争用等情况。
     * 这个方法由于有比较乐观的重试机制，所以存在常见的非模块化问题，依赖于重试
     *
     * @param x the value   元素
     * @param fn the update function, or null for add (this convention
     * avoids the need for an extra field or function in LongAdder).   更新函数，如果是add可以为null，这个约定避免了longadder中定义额外的变量或者函数
     * @param wasUncontended false if CAS failed before call  如果CAS在调用之前失败了，这个值为false
     */
    final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended) {
        int h;
        // 获取当前线程的probe值，如果为0，则需要初始化该线程的probe值
        if ((h = getProbe()) == 0) {
            // 强制初始化
            ThreadLocalRandom.current(); // force initialization
            // 重新获取probe值
            h = getProbe();
            // 都未初始化，肯定还不存在竞争激烈
            wasUncontended = true;
        }
        // 是否发生碰撞
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            // cells已经初始化过
            if ((as = cells) != null && (n = as.length) > 0) {
                // 当前线程所在的Cell未初始化
                if ((a = as[(n - 1) & h]) == null) {
                    // 当前无其它线程在创建或扩容cells，也没有线程在创建Cell
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        // 新建一个Cell，值为当前需要增加的值
                        Cell r = new Cell(x);   // Optimistically create
                        // 再次检测cellsBusy，并尝试更新它为1
                        // 相当于当前线程加锁
                        if (cellsBusy == 0 && casCellsBusy()) {
                            // 是否创建成功
                            boolean created = false;
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                // 重新获取cells，并找到当前线程hash到cells数组中的位置
                                // 这里一定要重新获取cells，因为as并不在锁定范围内
                                // 有可能已经扩容了，这里要重新获取
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    // 把上面新建的Cell放在cells的j位置处
                                    rs[j] = r;
                                    // 创建成功
                                    created = true;
                                }
                            } finally {
                                // 相当于释放锁
                                cellsBusy = 0;
                            }
                            // 创建成功了就返回
                            // 值已经放在新建的Cell里面了
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    // 标记当前未出现冲突
                    collide = false;
                }
                // 当前线程所在的Cell不为空，且更新失败了
                // 这里简单地设为true，相当于简单地自旋一次
                // 通过下面的语句修改线程的probe再重新尝试
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                // 再次尝试CAS更新当前线程所在Cell的值，如果成功了就返回
                else if (a.cas(v = a.value, ((fn == null) ? v + x :
                                             fn.applyAsLong(v, x))))
                    break;
                // 如果cells数组的长度达到了CPU核心数或者cells扩容了
                // 设置collide为false并通过下面的语句修改线程的probe再重新尝试

                // cells != as表明cells数组已经被更新了
                //标记为最大状态或者说是过期状态
                else if (n >= NCPU || cells != as)
                    collide = false;            // At max size or stale
                // 上个else if都更新失败了，且上个条件不成立，说明出现冲突了
                else if (!collide)
                    collide = true;
                // 明确出现冲突了，尝试占有锁，并扩容
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        // 检查是否有其它线程已经扩容过了
                        if (cells == as) {      // Expand table unless stale
                            // 新数组为原数组的两倍
                            Cell[] rs = new Cell[n << 1];
                            // 把旧数组元素拷贝到新数组中
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            // 重新赋值cells为新数组
                            cells = rs;
                        }
                    } finally {
                        //释放锁
                        cellsBusy = 0;
                    }
                    // 已解决冲突
                    collide = false;
                    // 使用扩容后的新数组重新尝试
                    continue;                   // Retry with expanded table
                }
                // 更新失败或者达到了CPU核心数，重新生成probe，并重试
                h = advanceProbe(h);
            }
            // 未初始化过cells数组，尝试占有锁并初始化cells数组
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                // 是否初始化成功
                boolean init = false;
                try {                           // Initialize table
                    // 检测是否有其它线程初始化过
                    if (cells == as) {
                         // 新建一个大小为2的Cell数组
                        Cell[] rs = new Cell[2];
                        // 找到当前线程hash到数组中的位置并创建其对应的Cell
                        rs[h & 1] = new Cell(x);
                        // 赋值给cells数组
                        cells = rs;
                        // 初始化成功
                        init = true;
                    }
                } finally {
                    //释放锁
                    cellsBusy = 0;
                }

                // 初始化成功直接返回
                // 因为增加的值已经同时创建到Cell中了
                if (init)
                    break;
            }
            //此处表明Cells为空，并且初始化的时候获取锁失败，直接在base上进行CAS
            // 如果有其它线程在初始化cells数组中，就尝试更新base
            // 如果成功了就返回
            else if (casBase(v = base, ((fn == null) ? v + x :
                                        fn.applyAsLong(v, x))))
                break;                          // Fall back on using base
        }
    }

    /**
     * Same as longAccumulate, but injecting long/double conversions
     * in too many places to sensibly merge with long version, given
     * the low-overhead requirements of this class. So must instead be
     * maintained by copy/paste/adapt.
     */
    final void doubleAccumulate(double x, DoubleBinaryOperator fn,
                                boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        Cell r = new Cell(Double.doubleToRawLongBits(x));
                        if (cellsBusy == 0 && casCellsBusy()) {
                            boolean created = false;
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (a.cas(v = a.value,
                               ((fn == null) ?
                                Double.doubleToRawLongBits
                                (Double.longBitsToDouble(v) + x) :
                                Double.doubleToRawLongBits
                                (fn.applyAsDouble
                                 (Double.longBitsToDouble(v), x)))))
                    break;
                else if (n >= NCPU || cells != as)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == as) {      // Expand table unless stale
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = advanceProbe(h);
            }
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(Double.doubleToRawLongBits(x));
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            else if (casBase(v = base,
                             ((fn == null) ?
                              Double.doubleToRawLongBits
                              (Double.longBitsToDouble(v) + x) :
                              Double.doubleToRawLongBits
                              (fn.applyAsDouble
                               (Double.longBitsToDouble(v), x)))))
                break;                          // Fall back on using base
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long BASE;
    private static final long CELLSBUSY;
    private static final long PROBE;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> sk = Striped64.class;
            BASE = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("base"));
            CELLSBUSY = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("cellsBusy"));
            Class<?> tk = Thread.class;
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
