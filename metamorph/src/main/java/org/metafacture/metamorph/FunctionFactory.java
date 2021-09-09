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

package org.metafacture.metamorph;

import org.metafacture.commons.ResourceUtil;
import org.metafacture.commons.reflection.ObjectFactory;
import org.metafacture.framework.MetafactureException;
import org.metafacture.metamorph.api.Function;

import java.io.IOException;

/**
 * Creates the functions available in Metamorph.
 *
 * @author Markus Michael Geipel
 *
 */
final class FunctionFactory extends ObjectFactory<Function> {

    FunctionFactory() {
        try {
            loadClassesFromMap(ResourceUtil.loadProperties("morph-functions.properties"), Function.class);
        }
        catch (final IOException e) {
            throw new MetafactureException("Failed to load functions list", e);
        }
    }

}
