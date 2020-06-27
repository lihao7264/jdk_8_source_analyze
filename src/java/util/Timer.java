/*
 * Copyright (c) 1999, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java.util;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A facility for threads to schedule tasks for future execution in a
 * background thread.  Tasks may be scheduled for one-time execution, or for
 * repeated execution at regular intervals.
 *
 * <p>Corresponding to each <tt>Timer</tt> object is a single background
 * thread that is used to execute all of the timer's tasks, sequentially.
 * Timer tasks should complete quickly.  If a timer task takes excessive time
 * to complete, it "hogs" the timer's task execution thread.  This can, in
 * turn, delay the execution of subsequent tasks, which may "bunch up" and
 * execute in rapid succession when (and if) the offending task finally
 * completes.
 *
 * <p>After the last live reference to a <tt>Timer</tt> object goes away
 * <i>and</i> all outstanding tasks have completed execution, the timer's task
 * execution thread terminates gracefully (and becomes subject to garbage
 * collection).  However, this can take arbitrarily long to occur.  By
 * default, the task execution thread does not run as a <i>daemon thread</i>,
 * so it is capable of keeping an application from terminating.  If a caller
 * wants to terminate a timer's task execution thread rapidly, the caller
 * should invoke the timer's <tt>cancel</tt> method.
 *
 * <p>If the timer's task execution thread terminates unexpectedly, for
 * example, because its <tt>stop</tt> method is invoked, any further
 * attempt to schedule a task on the timer will result in an
 * <tt>IllegalStateException</tt>, as if the timer's <tt>cancel</tt>
 * method had been invoked.
 *
 * <p>This class is thread-safe: multiple threads can share a single
 * <tt>Timer</tt> object without the need for external synchronization.
 *
 * <p>This class does <i>not</i> offer real-time guarantees: it schedules
 * tasks using the <tt>Object.wait(long)</tt> method.
 *
 * <p>Java 5.0 introduced the {@code java.util.concurrent} package and
 * one of the concurrency utilities therein is the {@link
 * java.util.concurrent.ScheduledThreadPoolExecutor
 * ScheduledThreadPoolExecutor} which is a thread pool for repeatedly
 * executing tasks at a given rate or delay.  It is effectively a more
 * versatile replacement for the {@code Timer}/{@code TimerTask}
 * combination, as it allows multiple service threads, accepts various
 * time units, and doesn't require subclassing {@code TimerTask} (just
 * implement {@code Runnable}).  Configuring {@code
 * ScheduledThreadPoolExecutor} with one thread makes it equivalent to
 * {@code Timer}.
 *
 * <p>Implementation note: This class scales to large numbers of concurrently
 * scheduled tasks (thousands should present no problem).  Internally,
 * it uses a binary heap to represent its task queue, so the cost to schedule
 * a task is O(log n), where n is the number of concurrently scheduled tasks.
 *
 * <p>Implementation note: All constructors start a timer thread.
 *
 * Timer要点：
 * a、Timer中的schedule功能提供了一个添加任务定时执行的功能，它支持多线程。
 *    这个功能的实现概括来讲就是用一个优先队列，建立任务(Task)，
 *    把任务按实行时间加到优先队列里。睡若干秒直到第一个任务。
 * b、Timer用一个TaskQueue，就是一个优先队列，
 *    可以把任务(Task) 按实行时间加到优先队列里。
 *    执行的时候，只有一个线程可以进行操作。
 *    提到了一个例子，就是如果一个任务间隔10s，
 *    但是执行花20s，还是得等执行完以后，新的任务才能添加。
 *    （这时加入队列的时间会为负数，也就是说立即实行）
 * c、运用java多线程里的wait和notify。
 *
 *
 * 可以设置一个后台线程，有计划的执行一次或者重复的按规律执行。
 * 每一个Timer是一个后台线程，按顺序执行所有的timer的任务。
 * 所以Timer不要执行耗时的操作，否则会造成任务堆积。
 *
 * Timer类是线程安全的
 *
 * 多线程任务调度，请使用：java.util.concurrent.ScheduledThreadPoolExecutor，
 * 可以完全取代Timer和TimerTask
 * @author  Josh Bloch
 * @see     TimerTask
 * @see     Object#wait(long)
 * @since   1.3
 */

public class Timer {
    /**
     * The timer task queue.  This data structure is shared with the timer
     * thread.  The timer produces tasks, via its various schedule calls,
     * and the timer thread consumes, executing timer tasks as appropriate,
     * and removing them from the queue when they're obsolete.
     *
     * 计时器任务队列。
     * 该数据结构与计时器线程共享。
     * 计时器通过其各种调度调用产生任务，
     * 并且计时器线程消耗，适当地执行计时器任务，并在过时时将其从队列中删除。
     *
     * 有优先级的时间任务队列，按照执行时间先后排序。内部用平衡二叉堆实现
     * 任务队列
     */
    private final TaskQueue queue = new TaskQueue();

    /**
     * The timer thread.
     * 定时器的线程
     */
    private final TimerThread thread = new TimerThread(queue);

    /**
     * This object causes the timer's task execution thread to exit
     * gracefully when there are no live references to the Timer object and no
     * tasks in the timer queue.  It is used in preference to a finalizer on
     * Timer as such a finalizer would be susceptible to a subclass's
     * finalizer forgetting to call it.
     *
     * 当没有实时引用Timer对象且计时器队列中没有任务时，此对象会使计时器的任务执行线程正常退出。 它优先于Timer上的终结器使用，因为这样的终结器将易于子类的终结器忘记调用它。
     */
    private final Object threadReaper = new Object() {
        protected void finalize() throws Throwable {
            synchronized(queue) {
                thread.newTasksMayBeScheduled = false;
                queue.notify(); // In case queue is empty.
            }
        }
    };

    /**
     * This ID is used to generate thread names.
     *
     * 用于生成线程名字的id
     */
    private final static AtomicInteger nextSerialNumber = new AtomicInteger(0);

    // 线程名字的id
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    /**
     * Creates a new timer.  The associated thread does <i>not</i>
     * {@linkplain Thread#setDaemon run as a daemon}.
     *
     *  无参
     */
    public Timer() {
        this("Timer-" + serialNumber());
    }

    /**
     * Creates a new timer whose associated thread may be specified to
     * {@linkplain Thread#setDaemon run as a daemon}.
     * A daemon thread is called for if the timer will be used to
     * schedule repeating "maintenance activities", which must be
     * performed as long as the application is running, but should not
     * prolong the lifetime of the application.
     *
     * 是否为后台进程(守护进程)
     * @param isDaemon true if the associated thread should run as a daemon.
     *
     */
    public Timer(boolean isDaemon) {
        this("Timer-" + serialNumber(), isDaemon);
    }

    /**
     * Creates a new timer whose associated thread has the specified name.
     * The associated thread does <i>not</i>
     * {@linkplain Thread#setDaemon run as a daemon}.
     *
     * 用指定名字创建线程
     *
     * @param name the name of the associated thread
     * @throws NullPointerException if {@code name} is null
     * @since 1.5
     */
    public Timer(String name) {
        thread.setName(name);
        thread.start();
    }

    /**
     * Creates a new timer whose associated thread has the specified name,
     * and may be specified to
     * {@linkplain Thread#setDaemon run as a daemon}.
     *
     * @param name the name of the associated thread
     * @param isDaemon true if the associated thread should run as a daemon
     * @throws NullPointerException if {@code name} is null
     * @since 1.5
     */
    public Timer(String name, boolean isDaemon) {
        thread.setName(name);
        thread.setDaemon(isDaemon);
        // 启动线程
        thread.start();
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     * 指定任务和延迟时间
     *
     * @param task  task to be scheduled.  定时的任务
     * @param delay delay in milliseconds before task is to be executed.
     *              在执行任务之前的延迟（以毫秒为单位）。
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *         <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} is null
     *
     * schedule的下次执行时间按照当前时间来算的，
     * 而scheduleAtFixedRate的下次执行时间按照最后一次计算出的执行时间来算的，
     * scheduleAtFixedRate更加注重频率，schedule更加注重间隔时间
     */
    public void schedule(TimerTask task, long delay) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        sched(task, System.currentTimeMillis()+delay, 0);
    }

    /**
     * Schedules the specified task for execution at the specified time.  If
     * the time is in the past, the task is scheduled for immediate execution.
     *
     * 指定任务和执行时时间
     * @param task task to be scheduled. 定时任务
     * @param time time at which task is to be executed. 任务执行的时间
     * @throws IllegalArgumentException if <tt>time.getTime()</tt> is negative.
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} or {@code time} is null
     */
    public void schedule(TimerTask task, Date time) {
        sched(task, time.getTime(), 0);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning after the specified delay.  Subsequent executions take place
     * at approximately regular intervals separated by the specified period.
     *
     * <p>In fixed-delay execution, each execution is scheduled relative to
     * the actual execution time of the previous execution.  If an execution
     * is delayed for any reason (such as garbage collection or other
     * background activity), subsequent executions will be delayed as well.
     * In the long run, the frequency of execution will generally be slightly
     * lower than the reciprocal of the specified period (assuming the system
     * clock underlying <tt>Object.wait(long)</tt> is accurate).
     *
     * <p>Fixed-delay execution is appropriate for recurring activities
     * that require "smoothness."  In other words, it is appropriate for
     * activities where it is more important to keep the frequency accurate
     * in the short run than in the long run.  This includes most animation
     * tasks, such as blinking a cursor at regular intervals.  It also includes
     * tasks wherein regular activity is performed in response to human
     * input, such as automatically repeating a character as long as a key
     * is held down.
     *
     * 指定任务，第一次执行延迟，以后每次执行周期 ms
     *
     * @param task   task to be scheduled.  定时任务
     * @param delay  delay in milliseconds before task is to be executed.
     *               在执行任务之前的延迟（以毫秒为单位）。
     * @param period time in milliseconds between successive task executions.
     *               连续执行任务之间的时间（以毫秒为单位）。（也就是执行周期）
     * @throws IllegalArgumentException if {@code delay < 0}, or
     *         {@code delay + System.currentTimeMillis() < 0}, or
     *         {@code period <= 0}
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} is null
     */
    public void schedule(TimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis()+delay, -period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>,
     * beginning at the specified time. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
     *
     * <p>In fixed-delay execution, each execution is scheduled relative to
     * the actual execution time of the previous execution.  If an execution
     * is delayed for any reason (such as garbage collection or other
     * background activity), subsequent executions will be delayed as well.
     * In the long run, the frequency of execution will generally be slightly
     * lower than the reciprocal of the specified period (assuming the system
     * clock underlying <tt>Object.wait(long)</tt> is accurate).  As a
     * consequence of the above, if the scheduled first time is in the past,
     * it is scheduled for immediate execution.
     *
     * <p>Fixed-delay execution is appropriate for recurring activities
     * that require "smoothness."  In other words, it is appropriate for
     * activities where it is more important to keep the frequency accurate
     * in the short run than in the long run.  This includes most animation
     * tasks, such as blinking a cursor at regular intervals.  It also includes
     * tasks wherein regular activity is performed in response to human
     * input, such as automatically repeating a character as long as a key
     * is held down.
     *
     * 指定任务，第一次执行时间，以后每次执行周期 ms
     *
     * @param task   task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if {@code firstTime.getTime() < 0}, or
     *         {@code period <= 0}
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} or {@code firstTime} is null
     */
    public void schedule(TimerTask task, Date firstTime, long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, firstTime.getTime(), -period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning after the specified delay.  Subsequent executions take place
     * at approximately regular intervals, separated by the specified period.
     *
     * <p>In fixed-rate execution, each execution is scheduled relative to the
     * scheduled execution time of the initial execution.  If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), two or more executions will occur in rapid succession to
     * "catch up."  In the long run, the frequency of execution will be
     * exactly the reciprocal of the specified period (assuming the system
     * clock underlying <tt>Object.wait(long)</tt> is accurate).
     *
     * <p>Fixed-rate execution is appropriate for recurring activities that
     * are sensitive to <i>absolute</i> time, such as ringing a chime every
     * hour on the hour, or running scheduled maintenance every day at a
     * particular time.  It is also appropriate for recurring activities
     * where the total time to perform a fixed number of executions is
     * important, such as a countdown timer that ticks once every second for
     * ten seconds.  Finally, fixed-rate execution is appropriate for
     * scheduling multiple repeating timer tasks that must remain synchronized
     * with respect to one another.
     *
     * 指定任务，第一次执行延迟，以后每次执行周期 ms
     *
     * @param task   task to be scheduled.
     * @param delay  delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if {@code delay < 0}, or
     *         {@code delay + System.currentTimeMillis() < 0}, or
     *         {@code period <= 0}
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} is null
     *
     * schedule的下次执行时间按照当前时间来算的，
     * 而scheduleAtFixedRate的下次执行时间按照最后一次计算出的执行时间来算的，
     * scheduleAtFixedRate更加注重频率，schedule更加注重间隔时间
     */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis()+delay, period);
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>,
     * beginning at the specified time. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
     *
     * <p>In fixed-rate execution, each execution is scheduled relative to the
     * scheduled execution time of the initial execution.  If an execution is
     * delayed for any reason (such as garbage collection or other background
     * activity), two or more executions will occur in rapid succession to
     * "catch up."  In the long run, the frequency of execution will be
     * exactly the reciprocal of the specified period (assuming the system
     * clock underlying <tt>Object.wait(long)</tt> is accurate).  As a
     * consequence of the above, if the scheduled first time is in the past,
     * then any "missed" executions will be scheduled for immediate "catch up"
     * execution.
     *
     * <p>Fixed-rate execution is appropriate for recurring activities that
     * are sensitive to <i>absolute</i> time, such as ringing a chime every
     * hour on the hour, or running scheduled maintenance every day at a
     * particular time.  It is also appropriate for recurring activities
     * where the total time to perform a fixed number of executions is
     * important, such as a countdown timer that ticks once every second for
     * ten seconds.  Finally, fixed-rate execution is appropriate for
     * scheduling multiple repeating timer tasks that must remain synchronized
     * with respect to one another.
     *
     * 指定任务，第一次执行时间，以后每次执行周期 ms
     *
     * @param task   task to be scheduled.
     * @param firstTime First time at which task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @throws IllegalArgumentException if {@code firstTime.getTime() < 0} or
     *         {@code period <= 0}
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} or {@code firstTime} is null
     */
    public void scheduleAtFixedRate(TimerTask task, Date firstTime,
                                    long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, firstTime.getTime(), period);
    }

    /**
     * Schedule the specified timer task for execution at the specified
     * time with the specified period, in milliseconds.  If period is
     * positive, the task is scheduled for repeated execution; if period is
     * zero, the task is scheduled for one-time execution. Time is specified
     * in Date.getTime() format.  This method checks timer state, task state,
     * and initial execution time, but not period.
     *
     * 任务加入队列
     *
     * @throws IllegalArgumentException if <tt>time</tt> is negative.
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException if {@code task} is null
     */
    private void sched(TimerTask task, long time, long period) {
        if (time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        // Constrain value of period sufficiently to prevent numeric
        // overflow while still being effectively infinitely large.
        // 有效限制周期的值，以防止数值溢出，同时仍然有效地无限大。
        if (Math.abs(period) > (Long.MAX_VALUE >> 1))
            period >>= 1;

        // 加锁队列
        synchronized(queue) {
            if (!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            // 加锁任务
            synchronized(task.lock) {
                // 初始化状态，任务还没有被执行，则加入到队列中
                if (task.state != TimerTask.VIRGIN)
                    throw new IllegalStateException(
                        "Task already scheduled or cancelled");
                // 任务下一次执行时间
                task.nextExecutionTime = time;
                // 周期
                task.period = period;
                // 任务已经计划执行(如果不是一个重复执行的任务，它就还没有被执行过)
                task.state = TimerTask.SCHEDULED;
            }

            // 将新任务添加到优先级队列
            queue.add(task);
            // 获取堆顶任务，如果就是当前任务的话，则通知优先级队列
            if (queue.getMin() == task)
                queue.notify();
        }
    }

    /**
     * Terminates this timer, discarding any currently scheduled tasks.
     * Does not interfere with a currently executing task (if it exists).
     * Once a timer has been terminated, its execution thread terminates
     * gracefully, and no more tasks may be scheduled on it.
     *
     * <p>Note that calling this method from within the run method of a
     * timer task that was invoked by this timer absolutely guarantees that
     * the ongoing task execution is the last task execution that will ever
     * be performed by this timer.
     *
     * <p>This method may be called repeatedly; the second and subsequent
     * calls have no effect.
     *
     * 中断定时器，不会影响正在执行的任务
     */
    public void cancel() {
        synchronized(queue) {
            thread.newTasksMayBeScheduled = false;
            // 清空队列
            queue.clear();
            queue.notify();  // In case queue was already empty.
        }
    }

    /**
     * Removes all cancelled tasks from this timer's task queue.  <i>Calling
     * this method has no effect on the behavior of the timer</i>, but
     * eliminates the references to the cancelled tasks from the queue.
     * If there are no external references to these tasks, they become
     * eligible for garbage collection.
     *
     * <p>Most programs will have no need to call this method.
     * It is designed for use by the rare application that cancels a large
     * number of tasks.  Calling this method trades time for space: the
     * runtime of the method may be proportional to n + c log n, where n
     * is the number of tasks in the queue and c is the number of cancelled
     * tasks.
     *
     * <p>Note that it is permissible to call this method from within a
     * a task scheduled on this timer.
     *
     * 移除所有队列中取消的任务
     *
     * @return the number of tasks removed from the queue.
     * @since 1.5
     */
     public int purge() {
         int result = 0;

         synchronized(queue) {
             for (int i = queue.size(); i > 0; i--) {
                 if (queue.get(i).state == TimerTask.CANCELLED) {
                     queue.quickRemove(i);
                     result++;
                 }
             }

             if (result != 0)
                 queue.heapify();
         }

         return result;
     }
}

/**
 * This "helper class" implements the timer's task execution thread, which
 * waits for tasks on the timer queue, executions them when they fire,
 * reschedules repeating tasks, and removes cancelled tasks and spent
 * non-repeating tasks from the queue.
 *
 * 此"帮助程序类"实现了计时器的任务执行线程，
 * 该线程等待计时器队列中的任务，在它们触发时执行它们，
 * 重新计划重复的任务，并从队列中删除已取消的任务和已花费的非重复任务。
 *
 * 这个类实现了定时器任务的执行线程，在任务队列上等待，
 * 并且执行任务，重新调整重复执行的任务，
 * 移除取消的任务和已经执行的非重复性任务。继承Thread类
 *
 */
class TimerThread extends Thread {
    /**
     * This flag is set to false by the reaper to inform us that there
     * are no more live references to our Timer object.  Once this flag
     * is true and there are no more tasks in our queue, there is no
     * work left for us to do, so we terminate gracefully.  Note that
     * this field is protected by queue's monitor!
     *
     * 收割者将此标志设置为false，以通知我们不再有对Timer对象的实时引用。
     * 一旦此标志为true，并且队列中没有其它任务，便没有工作要做，
     * 因此我们可以正常终止。
     * 请注意，此字段受队列的监视器保护！
     *
     * 当定时器没有有效的引用的时候，设置为false
     *
     * 一个有无任务的flag。
     */
    boolean newTasksMayBeScheduled = true;

    /**
     * Our Timer's queue.  We store this reference in preference to
     * a reference to the Timer so the reference graph remains acyclic.
     * Otherwise, the Timer would never be garbage-collected and this
     * thread would never go away.
     *
     * 定时器的队列，不引用Timer对象，是因为会造成循环引用，
     * Timer无法被回收，线程也会一直存在下去
     *
     * 优先队列
     */
    private TaskQueue queue;

    // 传入任务队列的构造函数
    TimerThread(TaskQueue queue) {
        this.queue = queue;
    }

    // 线程具体的执行
    public void run() {
        try {
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            synchronized(queue) {
                newTasksMayBeScheduled = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }

    /**
     * The main timer loop.  (See class comment.)
     * 主计时器循环。
     */
    private void mainLoop() {
        while (true) {
            try {
                TimerTask task;
                boolean taskFired;
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    // 等待队列变为非空
                    while (queue.isEmpty() && newTasksMayBeScheduled)
                        queue.wait();
                    // 队列为空，将永远保留； 死
                    if (queue.isEmpty())
                        break; // Queue is empty and will forever remain; die

                    // 队列非空； 看一眼，做正确的事
                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    // 1、在队列中获取要执行的任务
                    task = queue.getMin();
                    synchronized(task.lock) {
                        // 如果任务已取消的任务，在队列中去除
                        if (task.state == TimerTask.CANCELLED) {
                            queue.removeMin();
                            continue;  // No action required, poll queue again
                        }
                        // 获取当前时间
                        currentTime = System.currentTimeMillis();
                        // 下一次任务执行的时间
                        executionTime = task.nextExecutionTime;
                        // 如果下一次任务执行的时间小于等于当前时间
                        if (taskFired = (executionTime<=currentTime)) {
                            // 如果任务是非重复执行的任务，在队列中去除
                            if (task.period == 0) { // Non-repeating, remove
                                queue.removeMin();
                                // 将该非重复性任务状态设置为已经被执行了（或者正在执行），并且没有被取消
                                task.state = TimerTask.EXECUTED;
                            } else { // Repeating task, reschedule
                                // 重复任务进行重新安排
                                queue.rescheduleMin(
                                  task.period<0 ? currentTime   - task.period
                                                : executionTime + task.period);
                            }
                        }
                    }
                    // 任务尚未触发； 等待
                    if (!taskFired) // Task hasn't yet fired; wait
                        // 队列等待下一个任务执行时间到当前时间的时间
                        queue.wait(executionTime - currentTime);
                }
                // 执行任务
                if (taskFired)  // Task fired; run it, holding no locks
                    task.run();
            } catch(InterruptedException e) {
            }
        }
    }
}

/**
 * This class represents a timer task queue: a priority queue of TimerTasks,
 * ordered on nextExecutionTime.  Each Timer object has one of these, which it
 * shares with its TimerThread.  Internally this class uses a heap, which
 * offers log(n) performance for the add, removeMin and rescheduleMin
 * operations, and constant time performance for the getMin operation.
 */
class TaskQueue {
    /**
     * Priority queue represented as a balanced binary heap: the two children
     * of queue[n] are queue[2*n] and queue[2*n+1].  The priority queue is
     * ordered on the nextExecutionTime field: The TimerTask with the lowest
     * nextExecutionTime is in queue[1] (assuming the queue is nonempty).  For
     * each node n in the heap, and each descendant of n, d,
     * n.nextExecutionTime <= d.nextExecutionTime.
     *
     * 优先级队列表示为平衡的二进制堆：queue [n]的两个子级是queue [2 * n]和queue [2 * n + 1]。
     * 优先级队列在nextExecutionTime字段上排序：
     * 具有最低nextExecutionTime的TimerTask在queue [1]中（假定队列为非空）。
     * 对于堆中的每个节点n，以及n、d的每个后代，
     * n.nextExecutionTime <= d.nextExecutionTime。
     *
     * 队列数组，按照TimerTask的nextExecutionTime进行优先级比较
     */
    private TimerTask[] queue = new TimerTask[128];

    /**
     * The number of tasks in the priority queue.  (The tasks are stored in
     * queue[1] up to queue[size]).
     *
     * 优先级队列中的任务数。(任务存储在queue[1]到queue[size]中）。
     *
     */
    private int size = 0;

    /**
     * Returns the number of tasks currently on the queue.
     * 返回当前在队列中的任务数。
     */
    int size() {
        return size;
    }

    /**
     * Adds a new task to the priority queue.
     * 将新任务添加到优先级队列。
     */
    void add(TimerTask task) {
        // Grow backing store if necessary
        // 当队列满，则扩容2倍
        if (size + 1 == queue.length)
            queue = Arrays.copyOf(queue, 2*queue.length);
        // 将任务增加在队列末尾
        queue[++size] = task;
        fixUp(size);
    }

    /**
     * Return the "head task" of the priority queue.  (The head task is an
     * task with the lowest nextExecutionTime.)
     *
     * 返回优先级队列的"头任务"。(头任务是具有最低nextExecutionTime的任务。)
     * 获取堆顶元素
     */
    TimerTask getMin() {
        return queue[1];
    }

    /**
     * Return the ith task in the priority queue, where i ranges from 1 (the
     * head task, which is returned by getMin) to the number of tasks on the
     * queue, inclusive.
     * 返回指定下标的元素
     */
    TimerTask get(int i) {
        return queue[i];
    }

    /**
     * Remove the head task from the priority queue.
     * 移除堆顶元素
     */
    void removeMin() {
        queue[1] = queue[size];
        queue[size--] = null;  // Drop extra reference to prevent memory leak
        fixDown(1);
    }

    /**
     * Removes the ith element from queue without regard for maintaining
     * the heap invariant.  Recall that queue is one-based, so
     * 1 <= i <= size.
     * 移除第i个元素
     */
    void quickRemove(int i) {
        assert i <= size;
        queue[i] = queue[size];
        // 删除多余的ref以防止内存泄漏
        queue[size--] = null;  // Drop extra ref to prevent memory leak
    }

    /**
     * Sets the nextExecutionTime associated with the head task to the
     * specified value, and adjusts priority queue accordingly.
     * 重新设置顶部元素下次执行时间
     */
    void rescheduleMin(long newTime) {
        queue[1].nextExecutionTime = newTime;
        //下沉操作
        fixDown(1);
    }

    /**
     * Returns true if the priority queue contains no elements.
     *
     * 判断空
     */
    boolean isEmpty() {
        return size==0;
    }

    /**
     * Removes all elements from the priority queue.
     * 清空队列
     */
    void clear() {
        // Null out task references to prevent memory leak
        for (int i=1; i<=size; i++)
            queue[i] = null;

        size = 0;
    }

    /**
     * Establishes the heap invariant (described above) assuming the heap
     * satisfies the invariant except possibly for the leaf-node indexed by k
     * (which may have a nextExecutionTime less than its parent's).
     *
     * This method functions by "promoting" queue[k] up the hierarchy
     * (by swapping it with its parent) repeatedly until queue[k]'s
     * nextExecutionTime is greater than or equal to that of its parent.
     *
     * addTask 方法：把新的task加入arr最后，
     *              然后以size为参数call这个fixUp方法，
     *              这样新加入的task按时间排到相应的位置。
     * 添加一个元素后，堆的自我调整，进行上浮操作
     *
     */
    private void fixUp(int k) {
        while (k > 1) {
            // 除以2的值, 8>>1=4  9>>1=4
            int j = k >> 1;
            if (queue[j].nextExecutionTime <= queue[k].nextExecutionTime)
                break;
            // j、k调换位置
            TimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }

    /**
     * Establishes the heap invariant (described above) in the subtree
     * rooted at k, which is assumed to satisfy the heap invariant except
     * possibly for node k itself (which may have a nextExecutionTime greater
     * than its children's).
     *
     * This method functions by "demoting" queue[k] down the hierarchy
     * (by swapping it with its smaller child) repeatedly until queue[k]'s
     * nextExecutionTime is less than or equal to those of its children.
     * 下沉调整
     */
    private void fixDown(int k) {
        int j;
        while ((j = k << 1) <= size && j > 0) {
            if (j < size &&
                queue[j].nextExecutionTime > queue[j+1].nextExecutionTime)
                j++; // j indexes smallest kid
            if (queue[k].nextExecutionTime <= queue[j].nextExecutionTime)
                break;
            TimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }

    /**
     * Establishes the heap invariant (described above) in the entire tree,
     * assuming nothing about the order of the elements prior to the call.
     *
     * 初始化堆
     */
    void heapify() {
        for (int i = size/2; i >= 1; i--)
            fixDown(i);
    }
}
