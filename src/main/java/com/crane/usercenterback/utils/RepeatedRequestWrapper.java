package com.crane.usercenterback.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * 可重复读取requestbody
 *
 * @Author Crane Resigned
 * @Date 2024/6/23 23:04:33
 */
@Slf4j
public class RepeatedRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    public RepeatedRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        BufferedReader reader = request.getReader();
        body = reader.lines().collect(Collectors.joining());
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayIns = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletIns = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayIns.read();
            }
        };
        return servletIns;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
