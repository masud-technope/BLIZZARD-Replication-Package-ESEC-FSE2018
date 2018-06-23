/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.processing;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;

public abstract class JobManager implements Runnable {

    /* queue of jobs to execute */
    protected IJob[] awaitingJobs = new IJob[10];

    protected int jobStart = 0;

    protected int jobEnd = -1;

    protected boolean executing = false;

    /* background processing */
    protected Thread processingThread;

    protected Job progressJob;

    /* counter indicating whether job execution is enabled or not, disabled if <= 0
	    it cannot go beyond 1 */
    private int enableCount = 1;

    public static boolean VERBOSE = false;

    /* flag indicating that the activation has completed */
    public boolean activated = false;

    private int awaitingClients = 0;

    /**
	 * Invoked exactly once, in background, before starting processing any job
	 */
    public void activateProcessing() {
        this.activated = true;
    }

    /**
	 * Answer the amount of awaiting jobs.
	 */
    public synchronized int awaitingJobsCount() {
        // pretend busy in case concurrent job attempts performing before activated
        return this.activated ? this.jobEnd - this.jobStart + 1 : 1;
    }

    /**
	 * Answers the first job in the queue, or null if there is no job available
	 * Until the job has completed, the job manager will keep answering the same job.
	 */
    public synchronized IJob currentJob() {
        if (this.enableCount > 0 && this.jobStart <= this.jobEnd)
            return this.awaitingJobs[this.jobStart];
        return null;
    }

    public synchronized void disable() {
        this.enableCount--;
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("DISABLING background indexing");
    }

    /**
	 * Remove the index from cache for a given project.
	 * Passing null as a job family discards them all.
	 */
    public void discardJobs(String jobFamily) {
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("DISCARD   background job family - " + jobFamily);
        try {
            IJob currentJob;
            // cancel current job if it belongs to the given family
            synchronized (this) {
                currentJob = currentJob();
                disable();
            }
            if (currentJob != null && (jobFamily == null || currentJob.belongsTo(jobFamily))) {
                currentJob.cancel();
                // wait until current active job has finished
                while (this.processingThread != null && this.executing) {
                    try {
                        if (VERBOSE)
                            Util.verbose(//$NON-NLS-1$
                            "-> waiting end of current background job - " + //$NON-NLS-1$
                            currentJob);
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            }
            // flush and compact awaiting jobs
            int loc = -1;
            synchronized (this) {
                for (int i = this.jobStart; i <= this.jobEnd; i++) {
                    currentJob = this.awaitingJobs[i];
                    if (// sanity check
                    currentJob != // sanity check
                    null) {
                        this.awaitingJobs[i] = null;
                        if (// copy down, compacting
                        !(jobFamily == null || currentJob.belongsTo(jobFamily))) {
                            this.awaitingJobs[++loc] = currentJob;
                        } else {
                            if (VERBOSE)
                                Util.verbose("-> discarding background job  - " + currentJob);
                            currentJob.cancel();
                        }
                    }
                }
                this.jobStart = 0;
                this.jobEnd = loc;
            }
        } finally {
            enable();
        }
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("DISCARD   DONE with background job family - " + jobFamily);
    }

    public synchronized void enable() {
        this.enableCount++;
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("ENABLING  background indexing");
        // wake up the background thread if it is waiting (context must be synchronized)
        notifyAll();
    }

    protected synchronized boolean isJobWaiting(IJob request) {
        for (// don't check job at jobStart, as it may have already started
        int i = this.jobEnd; // don't check job at jobStart, as it may have already started
        i > this.jobStart; // don't check job at jobStart, as it may have already started
        i--) if (request.equals(this.awaitingJobs[i]))
            return true;
        return false;
    }

    /**
	 * Advance to the next available job, once the current one has been completed.
	 * Note: clients awaiting until the job count is zero are still waiting at this point.
	 */
    protected synchronized void moveToNextJob() {
        if (this.jobStart <= this.jobEnd) {
            this.awaitingJobs[this.jobStart++] = null;
            if (this.jobStart > this.jobEnd) {
                this.jobStart = 0;
                this.jobEnd = -1;
            }
        }
    }

    /**
	 * When idle, give chance to do something
	 */
    protected void notifyIdle(long idlingTime) {
    // do nothing
    }

    /**
	 * This API is allowing to run one job in concurrence with background processing.
	 * Indeed since other jobs are performed in background, resource sharing might be
	 * an issue.Therefore, this functionality allows a given job to be run without
	 * colliding with background ones.
	 * Note: multiple thread might attempt to perform concurrent jobs at the same time,
	 *            and should synchronize (it is deliberately left to clients to decide whether
	 *            concurrent jobs might interfere or not. In general, multiple read jobs are ok).
	 *
	 * Waiting policy can be:
	 * 		IJobConstants.ForceImmediateSearch
	 * 		IJobConstants.CancelIfNotReadyToSearch
	 * 		IJobConstants.WaitUntilReadyToSearch
	 *
	 */
    public boolean performConcurrentJob(IJob searchJob, int waitingPolicy, IProgressMonitor monitor) {
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("STARTING  concurrent job - " + searchJob);
        searchJob.ensureReadyToRun();
        boolean status = IJob.FAILED;
        try {
            SubMonitor subMonitor = SubMonitor.convert(monitor);
            if (awaitingJobsCount() > 0) {
                switch(waitingPolicy) {
                    case IJob.ForceImmediate:
                        if (VERBOSE)
                            Util.verbose("-> NOT READY - forcing immediate - " + searchJob);
                        try {
                            // pause indexing
                            disable();
                            status = searchJob.execute(subMonitor);
                        } finally {
                            enable();
                        }
                        if (VERBOSE)
                            Util.verbose(//$NON-NLS-1$
                            "FINISHED  concurrent job - " + //$NON-NLS-1$
                            searchJob);
                        return status;
                    case IJob.CancelIfNotReady:
                        if (VERBOSE)
                            Util.verbose(//$NON-NLS-1$
                            "-> NOT READY - cancelling - " + //$NON-NLS-1$
                            searchJob);
                        if (VERBOSE)
                            Util.verbose(//$NON-NLS-1$
                            "CANCELED concurrent job - " + //$NON-NLS-1$
                            searchJob);
                        throw new OperationCanceledException();
                    case IJob.WaitUntilReady:
                        int totalWork = 1000;
                        SubMonitor subProgress = subMonitor.setWorkRemaining(10).split(8).setWorkRemaining(totalWork);
                        // use local variable to avoid potential NPE (see bug 20435 NPE when searching java method
                        // and bug 42760 NullPointerException in JobManager when searching)
                        Thread t = this.processingThread;
                        int originalPriority = t == null ? -1 : t.getPriority();
                        try {
                            if (t != null)
                                t.setPriority(Thread.currentThread().getPriority());
                            synchronized (this) {
                                this.awaitingClients++;
                            }
                            IJob previousJob = null;
                            int awaitingJobsCount;
                            int lastJobsCount = totalWork;
                            float lastWorked = 0;
                            float totalWorked = 0;
                            while ((awaitingJobsCount = awaitingJobsCount()) > 0) {
                                if (subProgress.isCanceled() || this.processingThread == null)
                                    throw new OperationCanceledException();
                                IJob currentJob = currentJob();
                                // currentJob can be null when jobs have been added to the queue but job manager is not enabled
                                if (currentJob != null && currentJob != previousJob) {
                                    if (VERBOSE)
                                        Util.verbose("-> NOT READY - waiting until ready - " + searchJob);
                                    if (subProgress != null) {
                                        String indexing = Messages.bind(Messages.jobmanager_filesToIndex, currentJob.getJobFamily(), Integer.toString(awaitingJobsCount));
                                        subProgress.subTask(indexing);
                                        // ratio of the amount of work relative to the total work
                                        float ratio = awaitingJobsCount < totalWork ? 1 : ((float) totalWork) / awaitingJobsCount;
                                        if (lastJobsCount > awaitingJobsCount) {
                                            totalWorked += (lastJobsCount - awaitingJobsCount) * ratio;
                                        } else {
                                            // more jobs were added, just increment by the ratio
                                            totalWorked += ratio;
                                        }
                                        if (totalWorked - lastWorked >= 1) {
                                            subProgress.worked((int) (totalWorked - lastWorked));
                                            lastWorked = totalWorked;
                                        }
                                        lastJobsCount = awaitingJobsCount;
                                    }
                                    previousJob = currentJob;
                                }
                                try {
                                    if (VERBOSE)
                                        //$NON-NLS-1$
                                        Util.verbose(//$NON-NLS-1$
                                        "-> GOING TO SLEEP - " + searchJob);
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                }
                            }
                        } finally {
                            synchronized (this) {
                                this.awaitingClients--;
                            }
                            if (t != null && originalPriority > -1 && t.isAlive())
                                t.setPriority(originalPriority);
                        }
                }
            }
            status = searchJob.execute(subMonitor);
        } finally {
            SubMonitor.done(monitor);
            if (VERBOSE)
                //$NON-NLS-1$
                Util.verbose(//$NON-NLS-1$
                "FINISHED  concurrent job - " + searchJob);
        }
        return status;
    }

    public abstract String processName();

    public synchronized void request(IJob job) {
        job.ensureReadyToRun();
        // append the job to the list of ones to process later on
        int size = this.awaitingJobs.length;
        if (// when growing, relocate jobs starting at position 0
        ++this.jobEnd == size) {
            // jobEnd now equals the number of jobs
            this.jobEnd -= this.jobStart;
            if (this.jobEnd < 50 && this.jobEnd < this.jobStart) {
                // plenty of free space in the queue so shift the remaining jobs to the beginning instead of growing it
                System.arraycopy(this.awaitingJobs, this.jobStart, this.awaitingJobs, 0, this.jobEnd);
                for (int i = this.jobStart; i < size; i++) this.awaitingJobs[i] = null;
            } else {
                System.arraycopy(this.awaitingJobs, this.jobStart, this.awaitingJobs = new IJob[size * 2], 0, this.jobEnd);
            }
            this.jobStart = 0;
        }
        this.awaitingJobs[this.jobEnd] = job;
        if (VERBOSE) {
            //$NON-NLS-1$
            Util.verbose("REQUEST   background job - " + job);
            //$NON-NLS-1$
            Util.verbose("AWAITING JOBS count: " + awaitingJobsCount());
        }
        // wake up the background thread if it is waiting
        notifyAll();
    }

    /**
	 * Flush current state
	 */
    public synchronized void reset() {
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("Reset");
        if (this.processingThread != null) {
            // discard all jobs
            discardJobs(null);
        } else {
            /* initiate background processing */
            this.processingThread = new Thread(this, processName());
            this.processingThread.setDaemon(true);
            // less prioritary by default, priority is raised if clients are actively waiting on it
            this.processingThread.setPriority(Thread.NORM_PRIORITY - 1);
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=296343
            // set the context loader to avoid leaking the current context loader
            this.processingThread.setContextClassLoader(this.getClass().getClassLoader());
            this.processingThread.start();
        }
    }

    /**
	 * Infinite loop performing resource indexing
	 */
    public void run() {
        long idlingStart = -1;
        activateProcessing();
        try {
            class ProgressJob extends Job {

                 ProgressJob(String name) {
                    super(name);
                }

                protected IStatus run(IProgressMonitor monitor) {
                    IJob job = currentJob();
                    while (!monitor.isCanceled() && job != null) {
                        String taskName = new StringBuffer(Messages.jobmanager_indexing).append(Messages.bind(Messages.jobmanager_filesToIndex, job.getJobFamily(), Integer.toString(awaitingJobsCount()))).toString();
                        monitor.subTask(taskName);
                        setName(taskName);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                        job = currentJob();
                    }
                    return Status.OK_STATUS;
                }
            }
            this.progressJob = null;
            while (this.processingThread != null) {
                try {
                    IJob job;
                    synchronized (this) {
                        // handle shutdown case when notifyAll came before the wait but after the while loop was entered
                        if (this.processingThread == null)
                            continue;
                        // must check for new job inside this sync block to avoid timing hole
                        if ((job = currentJob()) == null) {
                            if (this.progressJob != null) {
                                this.progressJob.cancel();
                                this.progressJob = null;
                            }
                            if (idlingStart < 0)
                                idlingStart = System.currentTimeMillis();
                            else
                                notifyIdle(System.currentTimeMillis() - idlingStart);
                            // wait until a new job is posted (or reenabled:38901)
                            this.wait();
                        } else {
                            idlingStart = -1;
                        }
                    }
                    if (job == null) {
                        notifyIdle(System.currentTimeMillis() - idlingStart);
                        // just woke up, delay before processing any new jobs, allow some time for the active thread to finish
                        Thread.sleep(500);
                        continue;
                    }
                    if (VERBOSE) {
                        Util.verbose(//$NON-NLS-1$
                        awaitingJobsCount() + //$NON-NLS-1$
                        " awaiting jobs");
                        Util.verbose("STARTING background job - " + job);
                    }
                    try {
                        this.executing = true;
                        if (this.progressJob == null) {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            this.progressJob = new ProgressJob(Messages.bind(Messages.jobmanager_indexing, "", ""));
                            this.progressJob.setPriority(Job.LONG);
                            this.progressJob.setSystem(true);
                            this.progressJob.schedule();
                        }
                        /*boolean status = */
                        job.execute(null);
                    //if (status == FAILED) request(job);
                    } finally {
                        this.executing = false;
                        if (VERBOSE)
                            Util.verbose("FINISHED background job - " + job);
                        moveToNextJob();
                        if (this.awaitingClients == 0)
                            Thread.sleep(50);
                    }
                } catch (InterruptedException // background indexing was interrupted
                e) {
                }
            }
        } catch (RuntimeException e) {
            if (this.processingThread != null) {
                Util.log(e, "Background Indexer Crash Recovery");
                discardJobs(null);
                this.processingThread = null;
                reset();
            }
            throw e;
        } catch (Error e) {
            if (this.processingThread != null && !(e instanceof ThreadDeath)) {
                Util.log(e, "Background Indexer Crash Recovery");
                discardJobs(null);
                this.processingThread = null;
                reset();
            }
            throw e;
        }
    }

    /**
	 * Stop background processing, and wait until the current job is completed before returning
	 */
    public void shutdown() {
        if (VERBOSE)
            //$NON-NLS-1$
            Util.verbose("Shutdown");
        disable();
        // will wait until current executing job has completed
        discardJobs(null);
        Thread thread = this.processingThread;
        try {
            if (// see http://bugs.eclipse.org/bugs/show_bug.cgi?id=31858
            thread != null) {
                synchronized (this) {
                    // mark the job manager as shutting down so that the thread will stop by itself
                    this.processingThread = null;
                    // ensure its awake so it can be shutdown
                    notifyAll();
                }
                // in case processing thread is handling a job
                thread.join();
            }
            Job job = this.progressJob;
            if (job != null) {
                job.cancel();
                job.join();
            }
        } catch (InterruptedException e) {
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(10);
        //$NON-NLS-1$
        buffer.append("Enable count:").append(this.enableCount).append('\n');
        int numJobs = this.jobEnd - this.jobStart + 1;
        //$NON-NLS-1$
        buffer.append("Jobs in queue:").append(numJobs).append('\n');
        for (int i = 0; i < numJobs && i < 15; i++) {
            //$NON-NLS-1$ //$NON-NLS-2$
            buffer.append(i).append(" - job[" + i + "]: ").append(this.awaitingJobs[this.jobStart + i]).append('\n');
        }
        return buffer.toString();
    }
}
