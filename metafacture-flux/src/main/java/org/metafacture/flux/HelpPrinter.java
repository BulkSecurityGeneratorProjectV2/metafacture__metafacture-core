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

package org.metafacture.flux;

import org.metafacture.commons.ResourceUtil;
import org.metafacture.commons.reflection.ConfigurableClass;
import org.metafacture.commons.reflection.ObjectFactory;
import org.metafacture.flux.parser.FluxProgramm;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.annotations.ReturnsAvailableArguments;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Prints Flux help for a given {@link ObjectFactory}
 *
 * @author Markus Michael Geipel
 */
public final class HelpPrinter {

    private HelpPrinter() {
        // no instances
    }

    /**
     * Prints Flux help to the standard output.
     *
     * @param args unused
     *
     * @see #print
     */
    public static void main(final String[] args) {
        FluxProgramm.printHelp(System.out);
    }

    /**
     * Prints Flux help for a given ObjectFactory. Excerpts setters and their
     * arguments, {@code @in} annotations and {@code @out} annotations.
     *
     * @param factory the ObjectFactory
     * @param out     the PrintStream to print to
     */
    public static void print(final ObjectFactory<?> factory,
            final PrintStream out) {
        out.println("Welcome to Metafacture");
        out.println("======================");
        out.println();
        out.println(getVersionInfo());

        out.println("\nUsage:\tflux FLOW_FILE [VARNAME=VALUE ...]\n");
        out.println("Available flux commands:\n");

        final List<String> keyWords = new ArrayList<String>();
        keyWords.addAll(factory.keySet());
        Collections.sort(keyWords);
        for (final String name : keyWords) {
            describe(name, factory, out);
        }
    }

    private static String getVersionInfo() {
        try {
            return ResourceUtil.loadProperties("build.properties").toString();
        }
        catch (final IOException e) {
            throw new MetafactureException("Failed to load build infos", e);
        }
    }

    private static <T> void describe(final String name, final ObjectFactory<T> factory, final PrintStream out) { // checkstyle-disable-line ExecutableStatementCount
        final ConfigurableClass<? extends T> configurableClass = factory.get(name);
        final Class<? extends T> moduleClass = configurableClass.getPlainClass();
        final Description desc = moduleClass.getAnnotation(Description.class);

        out.println(name);
        name.chars().forEach(c -> out.print("-"));
        out.println();

        if (desc != null) {
            out.println("- description:\t" + desc.value());
        }
        final Collection<String> arguments = getAvailableArguments(moduleClass);
        if (!arguments.isEmpty()) {
            out.println("- arguments:\t" + arguments);
        }

        final Map<String, Method> attributes = configurableClass.getSetters();

        if (!attributes.isEmpty()) {
            out.print("- options:\t");
            final StringBuilder builder = new StringBuilder();
            for (final Entry<String, Method> entry : attributes.entrySet()) {
                final Method method = entry.getValue();
                final Class<?> type = configurableClass.getSetterType(method);

                if (method.isAnnotationPresent(Deprecated.class)) {
                    builder.append("[deprecated] ");
                }

                if (type.isEnum()) {
                    builder.append(entry.getKey())
                            .append(" ")
                            .append(Arrays.asList(type.getEnumConstants()))
                            .append(", ");
                }
                else {
                    builder.append(entry.getKey())
                            .append(" (")
                            .append(type.getSimpleName())
                            .append("), ");
                }

            }
            out.println(builder.substring(0, builder.length() - 2));
        }

        String inString = "<unknown>";
        String outString = "";
        final In inClass = moduleClass.getAnnotation(In.class);
        if (inClass != null) {
            inString = inClass.value().getSimpleName();
        }
        final Out outClass = moduleClass.getAnnotation(Out.class);
        if (outClass != null) {
            outString = outClass.value().getSimpleName();
        }
        out.println("- signature:\t" + inString + " -> " + outString);
        out.println("- java class:\t" + moduleClass.getCanonicalName());
        out.println();
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> getAvailableArguments(final Class<?> moduleClass) {
        for (final Method method : moduleClass.getMethods()) {
            if (method.getAnnotation(ReturnsAvailableArguments.class) != null) {
                try {
                    return (Collection<String>) method.invoke(moduleClass);
                }
                catch (final IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                    // silently ignore
                }
            }
        }
        return Collections.emptyList();
    }

}
