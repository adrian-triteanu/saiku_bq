
package com.projecta.monsai.connection;

import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.saiku.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.stereotype.Component;

/**
 * Retrieves versions of database schemas
 */
@Component
public class SchemaVersioning {

    @Autowired
    private Config config;

    private static final int REQUEST_TIMEOUT_MILLIS = 30000;

    public String getSchemaVersionList() {

        String zedVersionUrl = StringUtils.trimToNull(config.getProperty("zedVersionUrl"));
        String headerUser = StringUtils.trimToNull(config.getProperty("zedLoginHeaderUser"));
        String headerPassword = StringUtils.trimToNull(config.getProperty("zedLoginHeaderPassword"));

        String responseString = "{}";

        if (zedVersionUrl == null) {
            return responseString;
        }

        try {
            String headerLogin = "PA" + DatatypeConverter.printBase64Binary((headerUser + ":" + headerPassword).getBytes("UTF-8")) + "z";
            // call the configured url
            Request request = Request.Post(zedVersionUrl)
                    .addHeader("Zed-Authorization", headerLogin)
                    .connectTimeout(REQUEST_TIMEOUT_MILLIS).socketTimeout(REQUEST_TIMEOUT_MILLIS);
            responseString = request.execute().returnContent().asString();

            // return repsponse
            return responseString;

        } catch (Throwable e) {
            // any error means that the authorization failed
            System.out.print("Error calling " + zedVersionUrl + ". Response: " + responseString + e.getMessage());
            return null;
        }
    }
}