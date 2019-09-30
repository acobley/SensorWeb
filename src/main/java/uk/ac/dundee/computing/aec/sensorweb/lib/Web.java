/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.lib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import com.eclipsesource.json.JsonObject;

/**
 *
 * @author andy
 */
public final class Web {

    public void Web() {

    }

    public static void PostJson(String url, String Data) throws IOException {
        URL videos = null;
        try {
            videos = new URL(url);
        } catch (Exception et) {
            System.out.println("Videos URL is broken");
            return;
        }
        HttpURLConnection hc = null;
        try {
            hc = (HttpURLConnection) videos.openConnection();
            String login = "admin:admin";
            //final byte[] authBytes = login.getBytes(StandardCharsets.UTF_8);
            //final String encoded = Base64.getEncoder().encodeToString(authBytes);
            //hc.addRequestProperty("Authorization", "Basic " + encoded);
            //hc.setDoInput(true);
            //hc.setDoOutput(true);
            hc.setUseCaches(false);
            hc.setRequestMethod("POST");
            hc.setDoOutput(true);
            //hc.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //hc.setRequestProperty("Accept", "application/json");
            hc.setRequestProperty("Accept", "application/json,text/html,application/hal+json,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*");
            hc.setRequestProperty("charset", "utf-8");
            hc.setRequestProperty("Content-Length", Integer.toString(Data.length()));

        } catch (Exception et) {
            System.out.println("Post Json Can't prepare http URL con");
            return;
        }
        BufferedReader br = null;
        try {
            //OutputStreamWriter writer = new OutputStreamWriter(hc.getOutputStream());
            try (DataOutputStream wr = new DataOutputStream(hc.getOutputStream())) {
                wr.write(Data.getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception et) {
            System.out.println("Can't get reader to videos stream");
        }
        int rc = -1;
        try {
            rc = hc.getResponseCode();
        } catch (Exception et) {
            System.out.println("Can't get reponse code " + et);
        }
        if ((rc == HttpURLConnection.HTTP_OK) || (rc == HttpURLConnection.HTTP_CREATED)) {
            int Length = hc.getContentLength();
            String Content = hc.getContentType();
            String Encoding = hc.getContentEncoding();

            InputStreamReader in = new InputStreamReader((InputStream) hc.getInputStream());
            BufferedReader buff = new BufferedReader(in);

            StringBuffer response = new StringBuffer();
            String line = null;
            try {
                do {
                    line = buff.readLine();
                    if (line != null) {
                        response.append(line);
                    }
                } while (line != null);
            } catch (Exception et) {
                System.out.println("Can't read from input " + et);
            }
            //System.out.println(sBuff);

        }
        return;

    }

    public static String GetJson(String url, String Data) throws IOException {
        URL videos = null;
        JsonObject RespObj = new JsonObject();
        try {
            videos = new URL(url);
            //System.out.println("URL"+videos);
            //System.out.println(Data);
        } catch (Exception et) {
            System.out.println("Videos URL is broken");
            return null;
        }
        HttpURLConnection hc = null;
        try {
            hc = (HttpURLConnection) videos.openConnection();
            String login = "admin:admin";
            //final byte[] authBytes = login.getBytes(StandardCharsets.UTF_8);
            //final String encoded = Base64.getEncoder().encodeToString(authBytes);
            //hc.addRequestProperty("Authorization", "Basic " + encoded);
            //hc.setDoInput(true);
            //hc.setDoOutput(true);
            hc.setUseCaches(false);
            hc.setRequestMethod("POST");
            hc.setDoOutput(true);
            //hc.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            hc.setRequestProperty("Content-Type", "text/plain");
            //hc.setRequestProperty("Accept", "application/json");
            hc.setRequestProperty("Accept", "application/json,text/html,application/hal+json,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*");
            hc.setRequestProperty("charset", "utf-8");
            hc.setRequestProperty("Content-Length", Integer.toString(Data.length()));

        } catch (Exception et) {
            System.out.println("GetJSON Can't prepare http URL con"+et);
            return null;
        }
        BufferedReader br = null;
        try {
            //OutputStreamWriter writer = new OutputStreamWriter(hc.getOutputStream());
            try (DataOutputStream wr = new DataOutputStream(hc.getOutputStream())) {
                wr.write(Data.getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception et) {
            System.out.println("Can't get reader to videos stream");
        }
        int rc = -1;
        try {
            rc = hc.getResponseCode();
        } catch (Exception et) {
            System.out.println("Can't get reponse code " + et);
        }
        StringBuffer response = new StringBuffer();
        if ((rc == HttpURLConnection.HTTP_OK) || (rc == HttpURLConnection.HTTP_CREATED)) {
            int Length = hc.getContentLength();
            String Content = hc.getContentType();
            String Encoding = hc.getContentEncoding();

            InputStreamReader in = new InputStreamReader((InputStream) hc.getInputStream());
            BufferedReader buff = new BufferedReader(in);

            String line = null;
            try {
                do {
                    line = buff.readLine();
                    if (line != null) {
                        response.append(line+"\r\n");
                    }
                } while (line != null);
            } catch (Exception et) {
                System.out.println("Can't read from input " + et);
            }
            //System.out.println("Response"+response);
           
            
        }
return response.toString();
    }
}
