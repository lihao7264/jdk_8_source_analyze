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
 * A {@code Future} represents the result of an asynchronous
 * computation.  Methods are provided to check if the computation is
 * complete, to wait for its completion, and to retrieve the result of
 * the computation.  The result can only be retrieved using method
 * {@code get} when the computation has completed, blocking if
 * necessary until it is ready.  Cancellation is performed by the
 * {@code cancel} method.  Additional methods are provided to
 * determine if the task completed normally or was cancelled. Once a
 * computation has completed, the computation cannot be cancelled.
 * If you would like to use a {@code Future} for the sake
 * of cancellability but not provide a usable result, you can
 * declare types of the form {@code Future<?>} and
 * return {@code null} as a result of the underlying task.
 *
 * <p>
 * <b>Sample Usage</b> (Note that the following classes are all
 * made-up.)
 * <pre> {@code
 * interface ArchiveSearcher { String search(String target); }
 * class App {
 *   ExecutorService executor = ...
 *   ArchiveSearcher searcher = ...
 *   void showSearch(final String target)
 *       throws InterruptedException {
 *     Future<String> future
 *       = executor.submit(new Callable<String>() {
 *         public String call() {
 *             return searcher.search(target);
 *         }});
 *     displayOtherThings(); // do other things while searching
 *     try {
 *       displayText(future.get()); // use future
 *     } catch (ExecutionException ex) { cleanup(); return; }
 *   }
 * }}</pre>
 *
 * The {@link FutureTask} class is an implementation of {@code Future} that
 * implements {@code Runnable}, and so may be executed by an {@code Executor}.
 * For example, the above construction with {@code submit} could be replaced by:
 *  <pre> {@code
 * FutureTask<String> future =
 *   new FutureTask<String>(new Callable<String>() {
 *     public String call() {
 *       return searcher.search(target);
 *   }});
 * executor.execute(future);}</pre>
 *
 * <p>Memory consistency effects: Actions taken by the asynchronous computation
 * <a href="package-summary.html#MemoryVisibility"> <i>happen-before</i></a>
 * actions following the corresponding {@code Future.get()} in another thread.
 * {@code Future}表示异步计算的结果。
 * 它提供了一些方法来检查计算是否完成、等待其完成以及检索计算结果。
 * 计算完成后，只能使用方法{@code get}来检索结果，必要时将阻塞直到准备好为止。
 * 取消是通过{@code cancel}方法执行的。
 * 提供了其它方法来确定任务是正常完成还是被取消。
 * 一旦计算完成，就不能取消计算。
 * 如果出于可取消性的目的而使用{@code Future}而不提供可用结果，
 * 则可以声明{@code Future <？>}形式的类型，并返回{@code null}作为 基本任务。
 *
 * @see FutureTask
 * @see Executor
 * @since 1.5
 * @author Doug Lea
 * @param <V> The result type returned by this Future's {@code get} method
 */
public interface Future<V> {

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     *
     * 取消任务的执行
     *
     * 尝试取消执行此任务。
     * 如果任务已经完成，已经被取消或由于某些其他原因而无法取消，则此尝试将失败。
     * 如果成功，并且在调用{@code cancel}时此任务尚未开始，则此任务永远不会运行。
     * 如果任务已经开始，则{@code mayInterruptIfRunning}参数确定是否应中断执行该任务的线程以尝试停止该任务。
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     * task should be interrupted; otherwise, in-progress tasks are allowed
     * to complete
     * mayInterruptIfRunning{@code true}如果执行此任务的线程应被中断;
     * 否则，正在进行的任务将被允许完成。
     *
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     *
     * {@code false}如果无法取消任务，通常是因为它已经正常完成了; 否则{@code true}
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     * 如果此任务在正常完成之前被取消，则返回{@code true}。
     *
     * @return {@code true} if this task was cancelled before it completed
     * {@code true}，如果此任务在完成之前被取消
     */
    boolean isCancelled();

    /**
     * Returns {@code true} if this task completed.
     * 如果此任务完成，则返回{@code true}。
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * 完成可能是由于正常终止|异常或取消引起的，
     * 在所有这些情况下，此方法都将返回{@code true}。
     *
     * @return {@code true} if this task completed
     * {@code true}（如果此任务已完成）
     */
    boolean isDone();

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     * 等待必要的计算完成，然后检索其结果。
     *
     * @return the computed result    计算结果
     * @throws CancellationException if the computation was cancelled
     * 如果计算被取消，则抛出 CancellationException
     * @throws ExecutionException if the computation threw an
     * exception
     * 如果计算引发异常，则抛出 ExecutionException
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * 如果当前线程在等待时被中断，则抛出InterruptedException
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * 必要时最多等待给定时间以完成计算，然后检索其结果（如果有）。
     * @param timeout the maximum time to wait   等待的最长时间
     * @param unit the time unit of the timeout argument   超时参数的时间单位
     * @return the computed result  计算结果
     * @throws CancellationException if the computation was cancelled
     * 如果计算被取消，则抛出 CancellationException
     * @throws ExecutionException if the computation threw an
     * exception
     * 如果计算引发异常，则抛出 ExecutionException
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * 如果当前线程在等待时被中断，则抛出InterruptedException
     * @throws TimeoutException if the wait timed out
     * 如果等待超时，则抛出 TimeoutException
     */
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
