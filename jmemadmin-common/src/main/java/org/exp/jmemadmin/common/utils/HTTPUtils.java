package org.exp.jmemadmin.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.exp.jmemadmin.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ZhangQingliang
 *
 */
public class HTTPUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HTTPUtils.class);

    private static HttpClient client = HttpClientBuilder.create().build();

    private HTTPUtils() {
        // Do nothing
    }

    /**
     * Build HTTP request path string.
     *
     * @param pathStrs
     * @return
     */
    public static String buildRequetPath(String... pathStrs) {
        StringBuffer buffer = new StringBuffer();
        for (String pathStr : pathStrs) {
            buffer.append(Constants.SLASH_DELIMITER).append(pathStr);
        }
        return buffer.toString();
    }

    /**
     * Build a HTTP URI with parameters.
     *
     * @param host
     * @param port
     * @param path
     * @param params
     * @return
     * @throws URISyntaxException
     */
    public static URI buildURI(String host, int port, String path, NameValuePair... params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setCharset(Constants.DEFAULT_ENCODING_CHARSET);
        builder.setScheme(Constants.HTTP_SHCEME);
        builder.setHost(host);
        builder.setPort(port);
        if (null != path) {
            builder.setPath(path);
        }
        return (null == params || 0 >= params.length ? builder.build() : builder.setParameters(params).build());
    }

    /**
     * Build a HTTP URI without parameter.
     *
     * @param host
     * @param port
     * @param path
     * @return
     * @throws URISyntaxException
     */
    public static URI buildURI(String host, int port, String path) throws URISyntaxException {
        return buildURI(host, port, path, new NameValuePair[] {});
    }

    /**
     * Build a HTTP URI just with host address and port.
     *
     * @param host
     * @param port
     * @return
     * @throws URISyntaxException
     */
    public static URI buildURI(String host, int port) throws URISyntaxException {
        return buildURI(host, port, null, new NameValuePair[] {});
    }

    /**
     * Send a HTTP GET request and get response body string.
     *
     * @param uri
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendGETRequest(URI uri, Header... headers) throws IOException {
        LOG.info("Send HTTP GET request, URI is [" + uri.toString() + "].");
        HttpGet get = new HttpGet();
        get.setURI(uri);
        for (Header header : headers) {
            get.setHeader(header);
        }
        HttpResponse response = client.execute(get);
        String result = null;
        try (InputStream stream = response.getEntity().getContent()) {
            result = IOUtils.toString(stream, Constants.DEFAULT_ENCODING);
        }
        return result;
    }

    /**
     * Send a HTTP GET request and get status code.
     *
     * @param uri
     * @return
     * @throws IOException
     */
    public static int sendGETRequest(URI uri) throws IOException {
        LOG.info("Send HTTP GET request, URI is [" + uri.toString() + "].");
        HttpGet get = new HttpGet();
        get.setURI(uri);
        return client.execute(get).getStatusLine().getStatusCode();
    }

    /**
     * Send a HTTP POST request and get response body string.
     *
     * @param uri
     * @param params
     * @param headers
     * @return
     */
    public static String sendPOSTRequest(URI uri, Map<String, Object> params, Header... headers) {
        LOG.info("Send HTTP POST request, URI is [" + uri.toString() + "].");
        String response = null;
        HttpPost post = new HttpPost();
        post.setURI(uri);
        for (Header header : headers) {
            post.setHeader(header);
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            if (pairs != null && pairs.size() > 0) {
                post.setEntity(new UrlEncodedFormEntity(pairs, Constants.DEFAULT_ENCODING));
            }
            response = EntityUtils.toString(client.execute(post).getEntity(), Constants.DEFAULT_ENCODING);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return response;
    }

}
