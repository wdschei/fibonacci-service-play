/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * Fibonacci Service
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2014 - 2017 Coffeehouse Consultants
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package com.thescheideggers.fibonacciservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.http.HttpEntity;
import play.mvc.Controller;
import play.mvc.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.util.MimeTypeUtils.*;

/**
 * REST Web Service.</p>
 * <a href="http://vichargrave.com/restful-web-service-development-with-netbeans-and-tomcat-tutorial/">
 * Vic Hargrave described it as
 * </a>:
 * <p/>
 * <b>HTTP Service Requests</b>
 * <p/>
 * RESTful web services are implemented using one or more of the following four HTTP request types depending on the design of the system.
 * These services loosely map to the so-called CRUD operations: Create, Retrieve, Update and Delete.
 * <ul>
 * <li><b>PUT - </b>
 * Adds a resource on the server.
 * The resource is contained in the body of the PUT request.
 * PUT is analogous to an SQL insert statement.
 * </li>
 * <li><b>GET – </b>
 * Retrieves a resource from the server.
 * The resource is specified with a URL and may include a <i>?</i> to delineate the request from the request parameters.
 * GET is analogous to an SQL select statement.
 * </li>
 * <li><b>POST - </b>
 * Updates a resource on the server.
 * The resource is contained in the body of the POST request.
 * POST is analogous to an SQL update statement.
 * </li>
 * <li><b>DELETE – </b>
 * Deletes a resource on the server.
 * The resource is specified in the URL only.
 * DELETE is analogous to an SQL delete command.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:William.Scheidegger@GMail.com?subject=FibonacciService%20JavaDoc">William Scheidegger</a>
 */
public class FibonacciController extends Controller {

    private final AtomicLong counter = new AtomicLong();

    public Result getFibonacciString(String length) {
        int status;
        final FibonacciResponseString response = new FibonacciResponseString(counter.incrementAndGet());
        try {
            response.setValue(FibonacciImpl.getFibonacciString(Integer.valueOf(length)));
            status = OK;
        } catch (NumberFormatException ex) {
            response.setValue(String.format("Length was unparseable. [%s]", length));
            status = BAD_REQUEST;
        } catch (IllegalArgumentException ex) {
            response.setValue(ex.getMessage());
            status = BAD_REQUEST;
        }
        return fromFibonacciResponse(response, status);
    }

    public Result getFibonacciArray(String length) {
        int status;
        final FibonacciResponseArray response = new FibonacciResponseArray(counter.incrementAndGet());
        try {
            response.setValue(FibonacciImpl.getFibonacci(Integer.valueOf(length)));
            status = OK;
        } catch (NumberFormatException ex) {
            response.setError(String.format("Length was unparseable. [%s]", length));
            status = BAD_REQUEST;
        } catch (IllegalArgumentException ex) {
            response.setError(ex.getMessage());
            status = BAD_REQUEST;
        }
        return fromFibonacciResponse(response, status);
    }

    private Result fromFibonacciResponse(FibonacciResponse fibonacciResponse, int status) {
        HttpEntity httpEntity;
        switch (status) {
            case OK:
                String acceptType = request().getHeader(ACCEPT);
                switch (acceptType) {
                    case APPLICATION_JSON_VALUE:
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            httpEntity = HttpEntity.fromString(
                                    mapper.writeValueAsString(fibonacciResponse),
                                    "utf-8")
                                    .as(acceptType);
                        } catch (JsonProcessingException e) {
                            httpEntity = HttpEntity.fromString(
                                    "Failed to write JSON",
                                    "utf-8")
                                    .as("text/plain");
                            status = INTERNAL_SERVER_ERROR;
                        }
                        break;
                    case TEXT_HTML_VALUE:
                        Object value = fibonacciResponse.getValue();
                        String body;
                        if (value instanceof String) {
                            body = String.format("<html><body>%s</body></html>", (String) value);
                        } else if (value instanceof long[]) {
                            body = String.format("<html><body>%s</body></html>", Arrays.toString((long[]) value));
                        } else {
                            body = "Failed to write HTML";
                            status = INTERNAL_SERVER_ERROR;
                        }
                        httpEntity = HttpEntity.fromString(
                                body,
                                "utf-8")
                                .as(acceptType);
                        break;
                    case APPLICATION_XML_VALUE:
                    case TEXT_XML_VALUE:
                        try {
                            JAXBContext jaxbContext = JAXBContext.newInstance(fibonacciResponse.getClass());
                            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                            StringWriter sw = new StringWriter();
                            jaxbMarshaller.marshal(fibonacciResponse, sw);
                            httpEntity = HttpEntity.fromString(
                                    sw.toString(),
                                    "utf-8")
                                    .as(acceptType);
                        } catch (JAXBException e) {
                            httpEntity = HttpEntity.fromString(
                                    "Failed to write XML",
                                    "utf-8")
                                    .as("text/plain");
                            status = INTERNAL_SERVER_ERROR;
                        }
                        break;
                    default:
                        httpEntity = HttpEntity.fromString(
                                "Unsupported Media Type",
                                "utf-8")
                                .as("text/plain");
                        status = UNSUPPORTED_MEDIA_TYPE;
                }
                break;
            case UNSUPPORTED_MEDIA_TYPE:
                httpEntity = HttpEntity.fromString(
                        "Unsupported Media Type",
                        "utf-8")
                        .as("text/plain");
                break;
            default:
                httpEntity = HttpEntity.fromString(
                        fibonacciResponse.getValue().toString(),
                        "utf-8")
                        .as("text/plain");
        }
        return new Result(status, httpEntity);
    }
}
