package com.krld.foxypoxy.network;

import com.krld.foxypoxy.util.Action1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Action1<String> logger = System.out::println;
    private final Action1<String> loggerE = value -> System.out.println("ERROR: " + value);

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request request = chain.request();

        if (false) { //TODO setup
            return chain.proceed(request);
        }


        boolean logBody = true;
        boolean logHeaders = true;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage =
                "--> " + request.method() + ' ' + requestPath(request.url()) + ' ' + protocol(protocol);
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        logger.call(requestStartMessage);

        if (logHeaders) {
         /*   Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                //   logger.call(headers.name(i) + ": " + headers.value(i));
            }*/

            String endMessage = "--> END " + request.method();
            if (logBody && hasRequestBody) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    contentType.charset(UTF8);
                }
                logger.call(buffer.readString(charset));

                endMessage += " (" + requestBody.contentLength() + "-byte body)";
            }
            logger.call(endMessage);
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        Action1<String> errorLoggerIfNeed = response.code() != 200 ? loggerE : logger;
        errorLoggerIfNeed.call("<-- "

                + request.method() + ' ' + response.code() + ' '
                + response.request().url().toString() + ' '
                + response.message() + " (" + tookMs + "ms"
                + ')');

        if (logHeaders) {
       /*     Headers headers = response.headers();     //TODO setup
            for (int i = 0, count = headers.size(); i < count; i++) {
                //   logger.call(headers.name(i) + ": " + headers.value(i));
            }*/

            String endMessage = "<-- END HTTP";
            if (logBody) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (responseBody.contentLength() != 0) {
                    this.logger.call(buffer.clone().readString(charset));
                }

                endMessage += " (" + buffer.size() + "-byte body)";
            }
            this.logger.call(endMessage);
        }

        return response;
    }

    private static String protocol(Protocol protocol) {
        return protocol == Protocol.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1";
    }

    private static String requestPath(HttpUrl url) {
        String path = url.toString();
        return path;
    }
}