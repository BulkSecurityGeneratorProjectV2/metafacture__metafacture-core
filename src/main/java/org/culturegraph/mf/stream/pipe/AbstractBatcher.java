/*
 *  Copyright 2013 Deutsche Nationalbibliothek
 *
 *  Licensed under the Apache License, Version 2.0 the "License";
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.culturegraph.mf.stream.pipe;

import org.culturegraph.mf.framework.StreamReceiver;

/**
 * Base class for pipes that perform an action every N records.
 * 
 * @author Markus Geipel
 * 
 */
public abstract class AbstractBatcher 
		extends WrappingStreamPipe<StreamReceiver> {

	public static final long DEFAULT_BATCH_SIZE = 1000;
	
	private long batchSize = DEFAULT_BATCH_SIZE;
	private long recordCount;
	private long batchCount;

	public final void setBatchSize(final int batchSize) {
		this.batchSize = batchSize;
	}
	
	public final long getBatchSize() {
		return batchSize;
	}
	
	public final long getBatchCount() {
		return batchCount;
	}
	
	public final long getRecordCount() {
		return recordCount;
	}

	@Override
	public final void startRecord(final String identifier) {
		getInternalReceiver().startRecord(identifier);
	}

	@Override
	public final void endRecord() {
		getInternalReceiver().endRecord();

		++recordCount;
		recordCount %= batchSize;
		if (0 == recordCount) {
			++batchCount;
			onBatchComplete();
		}
	}

	@Override
	public final void startEntity(final String name) {
		getInternalReceiver().startEntity(name);
	}

	@Override
	public final void endEntity() {
		getInternalReceiver().endEntity();
	}

	@Override
	public final void literal(final String name, final String value) {
		getInternalReceiver().literal(name, value);
	}
	
	@Override
	protected void onResetStream() {
		recordCount = 0;
		batchCount = 0;
	}

	protected abstract void onBatchComplete();

}
