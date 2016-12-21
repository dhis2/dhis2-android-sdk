/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.job;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public final class JobExecutor {
    private static final String TAG = JobExecutor.class.getSimpleName();
    private static final int MAX_RUNNING_JOBS = 64;

    private static JobExecutor mJobExecutor;

    private Queue<Job> mPendingJobs;
    private Queue<Job> mRunningJobs;
    private Map<Integer, Job> mPendingJobIds;
    private Map<Integer, Job> mRunningJobIds;

    private JobExecutor() {
        mPendingJobs = new LinkedList<>();
        mRunningJobs = new LinkedList<>();

        mPendingJobIds = new HashMap<>();
        mRunningJobIds = new HashMap<>();
    }

    public static JobExecutor getInstance() {
        if (mJobExecutor == null) {
            mJobExecutor = new JobExecutor();
        }

        return mJobExecutor;
    }

    private static <T> void run(AsyncTask<Void, Void, T> task) {
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public static <T> Job enqueueJob(Job<T> job) {
        isNull(job, "Job object must not be null");

        JobExecutor executor = getInstance();
        if (!executor.isJobEnqueued(job.getJobId())) {
            executor.enqueuePendingJob(job);
            executor.executeNextJob();
        }
        return job;
    }

    public static boolean isJobRunning(int jobId) {
        JobExecutor executor = getInstance();
        return executor.isJobEnqueued(jobId);
    }

    private void executeNextJob() {
        if (mRunningJobIds.size() < MAX_RUNNING_JOBS &&
                mPendingJobs.size() > 0) {
            Job job = mPendingJobs.peek();
            dequeuePendingJob(job);
            onStartJob(job);
        }
    }

    <T> void onStartJob(Job<T> job) {
        bindJob(job);
        run(job);
    }

    void onFinishJob(Job job) {
        unbindJob(job);
        executeNextJob();
    }

    <T> void bindJob(Job<T> job) {
        job.onBind(this);
        enqueueRunningJob(job);
    }

    <T> void unbindJob(Job<T> job) {
        job.onUnbind();
        dequeueRunningJob(job);
    }

    private void enqueuePendingJob(Job job) {
        mPendingJobIds.put(job.getJobId(), job);
        mPendingJobs.add(job);
    }

    private void dequeuePendingJob(Job job) {
        mPendingJobIds.remove(job.getJobId());
        mPendingJobs.remove(job);
    }

    private void enqueueRunningJob(Job job) {
        mRunningJobIds.put(job.getJobId(), job);
        mRunningJobs.add(job);
    }

    public void dequeueRunningJob(Job job) {
        mRunningJobIds.remove(job.getJobId());
        mRunningJobs.remove(job);
    }

    private boolean isJobEnqueued(int jobId) {
        return mPendingJobIds.get(jobId) != null ||
                mRunningJobIds.get(jobId) != null;
    }
}