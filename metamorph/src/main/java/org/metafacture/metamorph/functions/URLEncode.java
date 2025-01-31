/*
 * Copyright 2013, 2014 Deutsche Nationalbibliothek
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.metafacture.metamorph.functions;

import org.metafacture.metamorph.api.MorphExecutionException;
import org.metafacture.metamorph.api.helpers.AbstractSimpleStatelessFunction;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * URL encodes the received value.
 *
 * @author Markus Michael Geipel
 *
 */
public final class URLEncode extends AbstractSimpleStatelessFunction {

    /**
     * Creates an instance of {@link URLEncode}.
     */
    public URLEncode() {
    }

    @Override
    public String process(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new MorphExecutionException("urlencode: unsupported encoding UTF-8", e);
        }
    }

}
