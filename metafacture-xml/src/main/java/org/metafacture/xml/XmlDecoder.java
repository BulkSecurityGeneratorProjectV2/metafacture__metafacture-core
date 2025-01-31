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

package org.metafacture.xml;

import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.XmlReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultObjectPipe;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * Reads an XML file and passes the XML events to a receiver.
 *
 * @author Christoph Böhme
 *
 */
@Description("Reads an XML file and passes the XML events to a receiver.")
@In(Reader.class)
@Out(XmlReceiver.class)
@FluxCommand("decode-xml")
public final class XmlDecoder extends DefaultObjectPipe<Reader, XmlReceiver> {

    private static final String SAX_PROPERTY_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    private final XMLReader saxReader;

    /**
     * Constructs an XmlDecoder by obtaining a new instance of an
     * {@link org.xml.sax.XMLReader}.
     */
    public XmlDecoder() {
        try {
            saxReader = XMLReaderFactory.createXMLReader();
        }
        catch (final SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void process(final Reader reader) {
        try {
            saxReader.parse(new InputSource(reader));
        }
        catch (final IOException e) {
            throw new MetafactureException(e);
        }
        catch (final SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    protected void onSetReceiver() {
        saxReader.setContentHandler(getReceiver());
        saxReader.setDTDHandler(getReceiver());
        saxReader.setEntityResolver(getReceiver());
        saxReader.setErrorHandler(getReceiver());
        try {
            saxReader.setProperty(SAX_PROPERTY_LEXICAL_HANDLER, getReceiver());
        }
        catch (final SAXNotRecognizedException e) {
            throw new MetafactureException(e);
        }
        catch (final SAXNotSupportedException e) {
            throw new MetafactureException(e);
        }
    }

}
