package com.postmarkapp.postmark.client;

import com.postmarkapp.postmark.client.data.parser.DataHandler;
import com.postmarkapp.postmark.client.exception.*;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * Client class acts as handler between HTTP requests handler class (HttpClient) and class which provides access to all endpoints to call.
 * This class provides correct data for both sides and acts as controller. Also it verifies whether data sent and received is correct.
 */
public class HttpClientHandler {

    private final HttpClient httpClient;
    protected final DataHandler dataHandler;
    private final HttpClientErrorHandler httpClientErrorHandler;

    protected HttpClientHandler(MultivaluedMap<String,Object> headers) {
        this.dataHandler = new DataHandler(false);
        this.httpClientErrorHandler = new HttpClientErrorHandler(this.dataHandler);
        httpClient = new HttpClient(headers);
    }

    protected HttpClientHandler(MultivaluedMap<String,Object> headers, boolean secureConnection) {
        this(headers);
        this.getHttpClient().setSecureConnection(secureConnection);
    }
    
    /**
     *
     * Execute HTTP requests with no data sending required.
     *
     * @param request_type HTTP request type
     * @param url HTTP request URL
     * @throws PostmarkException Errors thrown by invalid or unhandled requests made to Postmark
     * @throws IOException Errors thrown by Data Handler
     *
     * @see #execute(HttpClient.REQUEST_TYPES, String, Object) for details
     *
     * @return request response
     */
    protected String execute(HttpClient.REQUEST_TYPES request_type, String url) throws PostmarkException, IOException {
        return execute(request_type, url, null);
    }

    /**
     *
     * Execute HTTP requests
     *
     * @param request_type HTTP request type
     * @param url HTTP request URL
     * @param data request data to send
     * @throws PostmarkException Errors thrown by invalid or unhandled requests made to Postmark
     * @throws IOException Errors thrown by Data Handler
     *
     * @see HttpClient for details about HTTP request execution.
     * @return HTTP response message
     */
    protected String execute(HttpClient.REQUEST_TYPES request_type, String url, Object data) throws PostmarkException, IOException {
        HttpClient.ClientResponse response = httpClient.execute(request_type, url, dataHandler.toJson(data));

        if (response.getCode() == 200) {
            return response.getMessage();
        }
        else {
            throw httpClientErrorHandler.throwErrorBasedOnStatusCode(response.getCode(), response.getMessage());
        }
    }

    /**
     * Main source of issues can be the Object serialization process. To debug it, this method will set strict mapping, which
     * will fail on any issues.
     */
    public void setDebugMode() {
        this.dataHandler.setStrictMapper();
    }

    /**
     * @return HTTP client which processes HTTP requests
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Delegation method for HTTP client connection settings
     *
     * @param connectTimeoutSeconds HTTP client connection timeout
     */
    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        getHttpClient().setConnectTimeoutSeconds(connectTimeoutSeconds);
    }

    /**
     * Delegation method for HTTP client connection settings
     *
     * @param readTimeoutSeconds HTTP client read timeout
     */
    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        getHttpClient().setReadTimeoutSeconds(readTimeoutSeconds);
    }

    /**
     * Delegation method for HTTP client connection settings
     *
     * @param secureConnection - choose http or https
     */
    public void setSecureConnection(boolean secureConnection) {
        getHttpClient().setSecureConnection(secureConnection);
    }

    /**
     * @return DataHandler which processes HTTP requests sent, and HTTP request responses
     */
    public DataHandler getDataHandler() { return dataHandler; }
}
