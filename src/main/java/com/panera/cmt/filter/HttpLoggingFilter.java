package com.panera.cmt.filter;

import com.panera.cmt.mongo.entity.AuthenticatedUser;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);
    private static final String REQUEST_ID = "requestId";
    private static final String USERNAME = "username";
    private static final String REQUEST_DURATION = "requestDuration";

    public static void doLogAndReturn(HttpServletRequest request, HttpServletResponse response, String logMessage, int statusCode, String responseMessage, Date requestStartTime) {
        try {
            MDC.put(REQUEST_ID, UUID.randomUUID().toString());
            MDC.put(USERNAME, getUsername());

            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(request);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(response);

            logRequest(bufferedRequest);

            log.info(logMessage);
            response.sendError(statusCode, responseMessage);

            logResponse(bufferedRequest, bufferedResponse, requestStartTime);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(USERNAME);
        }
    }

    public static String getUsername() {
        String currentUsername = null;
        AuthenticatedUser authenticatedUser = AuthenticatedUserManager.getAuthenticatedUser();
        if (authenticatedUser != null) {
            currentUsername = authenticatedUser.getUsername();
        }
        return (currentUsername != null) ? currentUsername : "unknown";
    }

    private static void logRequest(BufferedRequestWrapper bufferedRequest) {
        try {
            Map<String, String> requestMap = getTypeSafeRequestMap(bufferedRequest);
            log.info(buildRequestLog(requestMap, bufferedRequest));
        } catch (Throwable a) {
            log.error("Failed to log request info. This will not affect the functionality of the request. Reason: {}", a.getMessage());
        }
    }

    private static void logResponse(BufferedRequestWrapper bufferedRequest, BufferedResponseWrapper bufferedResponse, Date requestStartTime) {
        try {
            long requestDuration = System.currentTimeMillis() - requestStartTime.getTime();

            log.info(buildResponseLog(bufferedResponse, requestDuration, bufferedRequest));

        } catch (Throwable a) {
            log.error("Failed to log response info. This will not affect the functionality of the request. Reason: {}", a.getMessage());
        }
    }

    private static String buildResponseLog(BufferedResponseWrapper bufferedResponse, long requestDuration, BufferedRequestWrapper bufferedRequest) {
        return "Response - " +
                "requestDuration=" + new DecimalFormat("#,###").format(requestDuration) + ", " +
                "statusCode=" + bufferedResponse.getStatus() + ", " +
                "body=" + bufferedResponse.getContent() + ", " +
                "headers=" + getResponseHeaders(bufferedResponse) + ", " +
                "requestMethod=" + bufferedRequest.getMethod() + ", " +
                "requestPath=" + bufferedRequest.getServletPath();
    }

    private static String buildRequestLog(Map<String, String> requestMap, BufferedRequestWrapper bufferedRequest) throws IOException {
        return "Request - " +
                "method=" + bufferedRequest.getMethod() + ", " +
                "path=" + bufferedRequest.getServletPath() + ", " +
                "user=" + getUsername() + ", " +
                "parameters=" + requestMap + ", " +
                "body=" + sanitizedRequestBody(bufferedRequest.getRequestBody()) + ", " +
                "remoteAddress=" + bufferedRequest.getRemoteAddr() + ", " +
                "headers=" + getRequestHeaders(bufferedRequest);
    }

    private static String sanitizedRequestBody(String requestBody) {
        requestBody = sanitizePasswords(requestBody);
        return requestBody;
    }
    private static String sanitizePasswords(String requestBody) {
        Pattern pattern = Pattern.compile("([\"']*)(password|newPassword|oldPassword|currentPassword|encodedPassword)([\"']*)([:=])([\\s]*)([\"'])(.*?)([\"'])");
        Matcher matcher = pattern.matcher(requestBody);
        if (matcher.find()) {
            requestBody = matcher.replaceAll("$1$2$3$4$5$6******$8");
        }
        return requestBody;
    }

    private static Map<String, String> getTypeSafeRequestMap(HttpServletRequest request) {
        Map<String, String> typeSafeRequestMap = new HashMap<>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        if (requestParamNames != null) {
            while (requestParamNames.hasMoreElements()) {
                String requestParamName = (String) requestParamNames.nextElement();
                String requestParamValue;
                if (requestParamName.equalsIgnoreCase("password")) { // You better not have a password in here in the first place, but just in case.
                    requestParamValue = "********";
                } else {
                    requestParamValue = request.getParameter(requestParamName);
                }
                typeSafeRequestMap.put(requestParamName, requestParamValue);
            }
        }
        return typeSafeRequestMap;
    }

    private static Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if ("authorization".equalsIgnoreCase(name)) { // do not log basic auth header
                    headers.put(name, "******");
                } else {
                    List<String> values = Collections.list(request.getHeaders(name));
                    Object value = values;
                    if (values.size() == 1) {
                        value = values.get(0);
                    } else if (values.isEmpty()) {
                        value = "";
                    }
                    headers.put(name, value);
                }
            }
        }
        return headers;
    }

    private static Map<String, Object> getResponseHeaders(HttpServletResponse response) {
        Map<String, Object> headers = new LinkedHashMap<>();
        Collection<String> names = response.getHeaderNames();
        if (names != null) {
            for (String name : names) {
                if ("authorization".equalsIgnoreCase(name)) { // do not log basic auth header
                    headers.put(name, "******");
                } else {
                    List<String> values = new ArrayList<>(response.getHeaders(name));
                    Object value = values;
                    if (values.size() == 1) {
                        value = values.get(0);
                    } else if (values.isEmpty()) {
                        value = "";
                    }
                    headers.put(name, value);
                }
            }
        }
        return headers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(USERNAME, getUsername());
        try {
            boolean shouldLogRequest = httpServletRequest.getServletPath() != null && httpServletRequest.getServletPath().startsWith("/api");
            if (shouldLogRequest) {
                BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
                BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);

                logRequest(bufferedRequest);

                Date requestStartTime = new Date();

                filterChain.doFilter(bufferedRequest, bufferedResponse);

                logResponse(bufferedRequest, bufferedResponse, requestStartTime);
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(USERNAME);
        }
    }

    @Override
    public void destroy() {
    }

    public static final class BufferedRequestWrapper extends HttpServletRequestWrapper {

        private ByteArrayInputStream bais = null;
        private ByteArrayOutputStream baos = null;
        private BufferedServletInputStream bsis = null;
        private byte[] buffer = null;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            // Read InputStream and store its content in a buffer.
            InputStream is = req.getInputStream();
            this.baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int read;
            while ((read = is.read(buf)) > 0) {
                this.baos.write(buf, 0, read);
            }
            this.buffer = this.baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            this.bais = new ByteArrayInputStream(this.buffer);
            this.bsis = new BufferedServletInputStream(this.bais);
            return this.bsis;
        }

        String getRequestBody() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getInputStream()));
            String line;
            StringBuilder inputBuffer = new StringBuilder();
            do {
                line = reader.readLine();
                if (null != line) {
                    inputBuffer.append(line.trim());
                }
            } while (line != null);
            reader.close();
            return inputBuffer.toString().trim();
        }

    }

    public static class BufferedResponseWrapper implements HttpServletResponse {
        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        public BufferedResponseWrapper(HttpServletResponse response) {
            original = response;
        }

        private String getContent() {
            return bos != null ? bos.toString() : null;
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(), bos);
            }
            return tee;

        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long l) {
            original.setContentLengthLong(l);
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            if (tee != null) {
                tee.flush();
            }
        }

        @Override
        public void resetBuffer() {
            if (original != null){
                original.resetBuffer();
            }
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public void setLocale(Locale loc) {
            original.setLocale(loc);
        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        @Override
        public void addCookie(Cookie cookie) {
            original.addCookie(cookie);
        }

        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeUrl(String url) {
            return original.encodeUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeRedirectUrl(String url) {
            return original.encodeRedirectUrl(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            original.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setStatus(int sc, String sm) {
            original.setStatus(sc, sm);
        }

        @Override
        public String getHeader(String arg0) {
            return original.getHeader(arg0);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return original.getHeaderNames();
        }

        @Override
        public Collection<String> getHeaders(String arg0) {
            return original.getHeaders(arg0);
        }

        @Override
        public int getStatus() {
            return original.getStatus();
        }

    }

    private static class TeeServletOutputStream extends ServletOutputStream {
        private final TeeOutputStream targetStream;

        TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

    private static final class BufferedServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bais;

        private BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int available() {
            return this.bais.available();
        }

        @Override
        public int read() {
            return this.bais.read();
        }

        @Override
        public int read(byte[] buf, int off, int len) {
            return this.bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
