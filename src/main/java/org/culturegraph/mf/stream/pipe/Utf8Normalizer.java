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

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.culturegraph.mf.framework.DefaultObjectPipe;
import org.culturegraph.mf.framework.ObjectReceiver;
import org.culturegraph.mf.framework.annotations.Description;
import org.culturegraph.mf.framework.annotations.In;
import org.culturegraph.mf.framework.annotations.Out;

/**
 * Normalises diacritics in UTF-8 encoded strings.
 * 
 * @author Christoph Böhme
 *
 */
@Description("Normalizes diacritics UTF-8 encoded strings.")
@In(String.class)
@Out(String.class)
public final class Utf8Normalizer extends
		DefaultObjectPipe<String, ObjectReceiver<String>> {

	@Override
	public void process(final String str) {
		getReceiver().process(Normalizer.normalize(str, Form.NFC));
	}
	
}
