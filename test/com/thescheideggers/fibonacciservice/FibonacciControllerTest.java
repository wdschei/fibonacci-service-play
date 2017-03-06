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

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;
import org.w3c.dom.Document;
import play.Logger;
import play.libs.Json;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.MimeTypeUtils.*;
import static play.mvc.Http.HeaderNames.ACCEPT;
import static play.mvc.Http.HeaderNames.CONTENT_ENCODING;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

/**
 * This is the Known Answer Test (KAT) for the Fibonacci Service.
 *
 * @author <a href="mailto:William.Scheidegger@GMail.com?subject=FibonacciControllerTest%20JavaDoc">William Scheidegger</a>
 */
public class FibonacciControllerTest extends WithApplication {

    private static final String HTML_HEADER_STRING = "<html><body>";
    private static final String HTML_FOOTER_STRING = "</body></html>";
    private static final String KAT_05_STRING = "0 1 1 2 3";
    private static final String KAT_93_STRING = KAT_05_STRING + " 5 8 13 21 34 55 89 144 233 377 "
            + "610 987 1597 2584 4181 6765 10946 17711 28657 46368 "
            + "75025 121393 196418 317811 514229 832040 1346269 2178309 "
            + "3524578 5702887 9227465 14930352 24157817 39088169 63245986 "
            + "102334155 165580141 267914296 433494437 701408733 1134903170 "
            + "1836311903 2971215073 4807526976 7778742049 12586269025 "
            + "20365011074 32951280099 53316291173 86267571272 139583862445 "
            + "225851433717 365435296162 591286729879 956722026041 1548008755920 "
            + "2504730781961 4052739537881 6557470319842 10610209857723 "
            + "17167680177565 27777890035288 44945570212853 72723460248141 "
            + "117669030460994 190392490709135 308061521170129 498454011879264 "
            + "806515533049393 1304969544928657 2111485077978050 3416454622906707 "
            + "5527939700884757 8944394323791464 14472334024676221 23416728348467685 "
            + "37889062373143906 61305790721611591 99194853094755497 160500643816367088 "
            + "259695496911122585 420196140727489673 679891637638612258 1100087778366101931 "
            + "1779979416004714189 2880067194370816120 4660046610375530309 7540113804746346429";
    private static final String ERR_MSG_NEG = "Length was less than 1. [-1]";
    private static final String ERR_MSG_POS = "Length was greater than 93. [94]";
    private static final String ERR_MSG_NON = "Length was unparseable. [%s]";
    private static final long[] KAT_05_ARRAY = new long[]{0L, 1L, 1L, 2L, 3L};
    private static final long[] KAT_93_ARRAY = new long[]{
            0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L, 55L, 89L, 144L, 233L, 377L,
            610L, 987L, 1597L, 2584L, 4181L, 6765L, 10946L, 17711L, 28657L, 46368L,
            75025L, 121393L, 196418L, 317811L, 514229L, 832040L, 1346269L, 2178309L,
            3524578L, 5702887L, 9227465L, 14930352L, 24157817L, 39088169L, 63245986L,
            102334155L, 165580141L, 267914296L, 433494437L, 701408733L, 1134903170L,
            1836311903L, 2971215073L, 4807526976L, 7778742049L, 12586269025L,
            20365011074L, 32951280099L, 53316291173L, 86267571272L, 139583862445L,
            225851433717L, 365435296162L, 591286729879L, 956722026041L, 1548008755920L,
            2504730781961L, 4052739537881L, 6557470319842L, 10610209857723L,
            17167680177565L, 27777890035288L, 44945570212853L, 72723460248141L,
            117669030460994L, 190392490709135L, 308061521170129L, 498454011879264L,
            806515533049393L, 1304969544928657L, 2111485077978050L, 3416454622906707L,
            5527939700884757L, 8944394323791464L, 14472334024676221L, 23416728348467685L,
            37889062373143906L, 61305790721611591L, 99194853094755497L, 160500643816367088L,
            259695496911122585L, 420196140727489673L, 679891637638612258L, 1100087778366101931L,
            1779979416004714189L, 2880067194370816120L, 4660046610375530309L, 7540113804746346429L
    };


    public FibonacciControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with Zero args and expecting HTML.
     */
    @Test
    public void testGetFibonacciString_HTML_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(TEXT_HTML_VALUE, result.contentType().get());
        assertEquals(HTML_HEADER_STRING + KAT_93_STRING + HTML_FOOTER_STRING, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with Zero args and expecting XML.
     */
    @Test
    public void testGetFibonacciString_XML_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(KAT_93_STRING, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with Zero args and expecting JSON.
     */
    @Test
    public void testGetFibonacciString_JSON_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        assertEquals(KAT_93_STRING, json.get("value").asText());
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Positive arg and expecting HTML.
     */
    @Test
    public void testGetFibonacciString_HTML_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/5")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(TEXT_HTML_VALUE, result.contentType().get());
        assertEquals(HTML_HEADER_STRING + KAT_05_STRING + HTML_FOOTER_STRING, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Positive arg and expecting XML.
     */
    @Test
    public void testGetFibonacciString_XML_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/5")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(KAT_05_STRING, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Positive arg and expecting JSON.
     */
    @Test
    public void testGetFibonacciString_JSON_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/5")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        assertEquals(KAT_05_STRING, json.get("value").asText());
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Negative arg and expecting HTML.
     */
    @Test
    public void testGetFibonacciString_HTML_Negative() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/-1")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(ERR_MSG_NEG, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Negative arg and expecting XML.
     */
    @Test
    public void testGetFibonacciString_XML_Negative() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/-1")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(ERR_MSG_NEG, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a Negative arg and expecting JSON.
     */
    @Test
    public void testGetFibonacciString_JSON_Negative() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/-1")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(ERR_MSG_NEG, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a non-numeric arg and expecting HTML.
     */
    @Test
    public void testGetFibonacciString_HTML_NonNumeric() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/five")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(String.format(ERR_MSG_NON, "five"), getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a non-numeric arg and expecting XML.
     */
    @Test
    public void testGetFibonacciString_XML_NonNumeric() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/five")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(String.format(ERR_MSG_NON, "five"), getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciString method of class FibonacciController with a non-numeric arg and expecting JSON.
     */
    @Test
    public void testGetFibonacciString_JSON_NonNumeric() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacci/five")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(TEXT_PLAIN_VALUE, result.contentType().get());
        assertEquals(String.format(ERR_MSG_NON, "five"), getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with Zero args and expecting HTML.
     */
    @Test
    public void testGetFibonacciArray_HTML_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(TEXT_HTML_VALUE, result.contentType().get());
        assertEquals(HTML_HEADER_STRING + Arrays.toString(KAT_93_ARRAY) + HTML_FOOTER_STRING, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with Zero args and expecting XML.
     */
    @Test
    public void testGetFibonacciArray_XML_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        for (int i = 0; i < KAT_93_ARRAY.length; ++i) {
            assertEquals(KAT_93_ARRAY[i], (long)(Long.valueOf(XPath.selectText(String.format("/FibonacciResponse/value[%d]", i+1), dom))));
        }
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with Zero args and expecting JSON.
     */
    @Test
    public void testGetFibonacciArray_JSON_Zero() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        JsonNode value = json.get("value");
        for (int i = 0; i < KAT_93_ARRAY.length; ++i) {
            assertEquals(KAT_93_ARRAY[i], value.get(i).asLong());
        }
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with a Positive arg and expecting HTML.
     */
    @Test
    public void testGetFibonacciArray_HTML_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray/5")
                .header(ACCEPT, TEXT_HTML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(TEXT_HTML_VALUE, result.contentType().get());
        assertEquals(HTML_HEADER_STRING + Arrays.toString(KAT_05_ARRAY) + HTML_FOOTER_STRING, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with a Positive arg and expecting XML.
     */
    @Test
    public void testGetFibonacciArray_XML_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray/5")
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        for (int i = 0; i < KAT_05_ARRAY.length; ++i) {
            assertEquals(KAT_05_ARRAY[i], (long)(Long.valueOf(XPath.selectText(String.format("/FibonacciResponse/value[%d]", i+1), dom))));
        }
        Logger.trace("Finished.");
    }

    /**
     * Test of getFibonacciArray method of class FibonacciController with a Positive arg and expecting JSON.
     */
    @Test
    public void testGetFibonacciArray_JSON_Positive() throws Exception {
        Logger.trace("Starting...");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/fibonacciArray/5")
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        JsonNode value = json.get("value");
        for (int i = 0; i < KAT_05_ARRAY.length; ++i) {
            assertEquals(KAT_05_ARRAY[i], value.get(i).asLong());
        }
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciString method of class FibonacciController with a Positive arg and expecting XML.
     */
    @Test
    @Ignore
    public void testPutFibonacciString_XML_Positive() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(5);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacci")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(KAT_05_STRING, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciString method of class FibonacciController with a Positive arg and expecting JSON.
     */
    @Test
    @Ignore
    public void testPutFibonacciString_JSON_Positive() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(5);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacci")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        assertEquals(KAT_05_STRING, json.get("value").asText());
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciString method of class FibonacciController with a Negative arg and expecting XML.
     */
    @Test
    @Ignore
    public void testPutFibonacciString_XML_Negative() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(-1);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacci")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(ERR_MSG_NEG, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciString method of class FibonacciController with a Negative arg and expecting JSON.
     */
    @Test
    @Ignore
    public void testPutFibonacciString_JSON_Negative() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(-1);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacci")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        assertEquals(ERR_MSG_NEG, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciArray method of class FibonacciController with a Positive arg and expecting XML.
     */
    @Test
    @Ignore
    public void testPutFibonacciArray_XML_Positive() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(5);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacciArray")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(KAT_05_STRING, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciArray method of class FibonacciController with a Positive arg and expecting JSON.
     */
    @Test
    @Ignore
    public void testPutFibonacciArray_JSON_Positive() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(5);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacciArray")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        JsonNode json = getResultBodyAsJsonNode(result);
        for (int i = 0; i < KAT_05_ARRAY.length; ++i) {
            // TODO: FIX_THIS
            //actions.andExpect(jsonPath("$.value[" + i + "]").value(KAT_05_ARRAY[i]));
        }
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciArray method of class FibonacciController with a Negative arg and expecting XML.
     */
    @Test
    @Ignore
    public void testPutFibonacciArray_XML_Negative() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(-1);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacciArray")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_XML_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(APPLICATION_XML_VALUE, result.contentType().get());
        Document dom = getResultBodyAsDocument(result);
        assertEquals(ERR_MSG_NEG, XPath.selectText("/FibonacciResponse/value", dom));
        Logger.trace("Finished.");
    }

    /**
     * Test of putFibonacciArray method of class FibonacciController with a Negative arg and expecting JSON.
     */
    @Test
    @Ignore
    public void testPutFibonacciArray_JSON_Negative() throws Exception {
        Logger.trace("Starting...");
        final FibonacciRequest fibonacciRequest = new FibonacciRequest(-1);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/fibonacciArray")
                .bodyRaw(FibonacciUtil.toBytes(fibonacciRequest))
                .header(CONTENT_ENCODING, APPLICATION_OCTET_STREAM_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(APPLICATION_JSON_VALUE, result.contentType().get());
        assertEquals(ERR_MSG_NEG, getResultBodyAsString(result));
        Logger.trace("Finished.");
    }

    private static String getResultBodyAsString(Result result) {
        return ((play.http.HttpEntity.Strict) result.body()).data().decodeString(StandardCharsets.UTF_8);
    }

    private static Document getResultBodyAsDocument(Result result) {
        return XML.fromString(getResultBodyAsString(result));
    }

    private static JsonNode getResultBodyAsJsonNode(Result result) {
        return Json.parse(getResultBodyAsString(result));
    }
}
