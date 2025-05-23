/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.labs64.utils.swid.support;

import com.labs64.utils.swid.exception.SwidException;
import jakarta.xml.bind.*;
import org.apache.commons.lang3.StringUtils;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Support class provides convenient methods for working with JAXB.
 */
public final class JAXBUtils {

    public static <T> T readObject(final String resource, final Class<T> expectedType) {
        return readObjectFromInputStream(JAXBUtils.class.getClassLoader().getResourceAsStream(resource), expectedType);
    }

    public static <T> T readObjectFromString(final String content, final Class<T> expectedType) {
        return readObjectFromInputStream(new ByteArrayInputStream(content.getBytes()), expectedType);
    }

    public static <T> T readObjectFromInputStream(final InputStream inputStream, final Class<T> expectedType) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(expectedType);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<T> element = unmarshaller.unmarshal(new StreamSource(inputStream), expectedType);
            return element.getValue();
        } catch (final JAXBException e) {
            throw new SwidException("Cannot process resource.", e);
        }
    }

    /**
     * Write XML item to the given destination.
     * 
     * @param entity
     *            XML item
     * @param destination
     *            destination to write to. Supported destinations: {@link java.io.OutputStream}, {@link java.io.File},
     *            {@link java.io.Writer}
     * @param comment
     *            optional comment which will be added at the begining of the generated XML
     * @throws IllegalArgumentException
     * @throws SwidException
     * @param <T>
     *            JAXB item
     */
    public static <T> void writeObject(final T entity, final Object destination, final String comment) {
        try {
            JAXBContext jaxbContext;
            if (entity instanceof JAXBElement) {
                jaxbContext = JAXBContext.newInstance(((JAXBElement) entity).getValue().getClass());
            } else {
                jaxbContext = JAXBContext.newInstance(entity.getClass());
            }

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            if (StringUtils.isNotBlank(comment)) {
                marshaller.setProperty("org.glassfish.jaxb.xmlHeaders", comment);
            }

            if (destination instanceof java.io.OutputStream) {
                marshaller.marshal(entity, (OutputStream) destination);
            } else if (destination instanceof java.io.File) {
                marshaller.marshal(entity, (java.io.File) destination);
            } else if (destination instanceof java.io.Writer) {
                marshaller.marshal(entity, (java.io.Writer) destination);
            } else {
                throw new IllegalArgumentException("Unsupported destination.");
            }
        } catch (final JAXBException e) {
            throw new SwidException("Cannot write object.", e);
        }
    }

    /**
     * Write XML item to the string.
     * 
     * @param entity
     *            XML item
     * @throws IllegalArgumentException
     * @throws SwidException
     * @param <T>
     *            JAXB item
     */
    public static <T> String writeObjectToString(final T entity) {
        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        writeObject(entity, destination, null);
        return destination.toString();
    }

    /**
     * Convert {@link Date} to {@link XMLGregorianCalendar}.
     * 
     * @param date
     *            XML item
     */
    public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(final Date date) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            XMLGregorianCalendar calXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND),
                    cal.get(Calendar.MILLISECOND),
                    0);
            return calXml;
        } catch (DatatypeConfigurationException e) {
            throw new SwidException("Cannot convert date", e);
        }
    }
}
