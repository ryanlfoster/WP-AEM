/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.batchjob.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.threads.ModifiableThreadPoolConfig;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;

import com.aditya.gmwp.aem.services.batchjob.BatchJobExecuter;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Component(metatype = false)
@Service(value = BatchJobExecuter.class)
public class BatchJobExecutorImpl implements BatchJobExecuter {

	@Reference
    private ThreadPoolManager threadPoolManager;
	
	private ThreadPool threadPool;
	
	/**
     * Gets automatically invoked when service is started.
     */
    @Activate
    protected final void activate() {
        final ModifiableThreadPoolConfig config = new ModifiableThreadPoolConfig();
        config.setMinPoolSize(1);
        config.setMaxPoolSize(1);
        this.threadPool = this.threadPoolManager.create(config);
    }

    /**
     * Gets automatically invoked when service is deactivated.
     */
    @Deactivate
    protected final void deactivate() {
        this.threadPoolManager.release(this.threadPool);
    }

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.batchjob.BatchJobExecuter#execute(java.util.List)
	 */
	@Override
	public void execute(List<Runnable> jobs) {
		for (final Runnable runnable : jobs) {
            this.threadPool.execute(runnable);
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.batchjob.BatchJobExecuter#execute(java.lang.Runnable[])
	 */
	@Override
	public void execute(Runnable... jobs) {
		execute(Arrays.asList(jobs));
	}
}
