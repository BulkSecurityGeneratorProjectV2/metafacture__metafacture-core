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

import org.metafacture.commons.ResourceUtil;
import org.metafacture.metamorph.api.MorphBuildException;
import org.metafacture.metamorph.api.MorphExecutionException;
import org.metafacture.metamorph.api.helpers.AbstractSimpleStatelessFunction;

import java.io.FileNotFoundException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A function which executes a JavaScript function.
 *
 * @author Markus Michael Geipel
 */
public final class Script extends AbstractSimpleStatelessFunction {

    private Invocable invocable;
    private String invoke;

    /**
     * Creates an instance of {@link Script}.
     */
    public Script() {
    }

    /**
     * Flags whether to invoke the script.
     *
     * @param invoke true if script should be invoked
     */
    public void setInvoke(final String invoke) {
        this.invoke = invoke;
    }

    /**
     * Loads a JavaScript file.
     *
     * @param file the filename
     */
    public void setFile(final String file) {
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            // TODO: The script file should be loaded relatively to the base URI
            engine.eval(ResourceUtil.getReader(file));
        }
        catch (final ScriptException e) {
            throw new MorphBuildException("Error in script", e);
        }
        catch (final FileNotFoundException e) {
            throw new MorphBuildException("Error loading script '" + file + "'", e);
        }
        invocable = (Invocable) engine;
    }

    @Override
    public String process(final String value) {
        final Object obj;
        try {
            obj = invocable.invokeFunction(invoke, value);
            return obj.toString();
        }
        catch (final ScriptException e) {
            throw new MorphExecutionException(
                    "Error in script while evaluating 'process' method", e);
        }
        catch (final NoSuchMethodException e) {
            throw new MorphExecutionException("'process' method is missing in script", e);
        }
    }

}
