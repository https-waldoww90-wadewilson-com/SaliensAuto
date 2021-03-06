package com.gmail.vkhanh234.SaliensAuto.utils;

import com.gmail.vkhanh234.SaliensAuto.Main;
import com.gmail.vkhanh234.SaliensAuto.data.RequestResult;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;


public class RequestUtils {
    public static RequestResult post(String type, String dat) {
        return sendRequest(type,dat,true);
    }

    public static RequestResult sendRequest(String type, String dat, boolean post){
        if(post && Main.token==null){
            Main.debug("&cError:&r Token hasn't been set yet");
            return null;
        }
        RequestResult result = new RequestResult();
        HttpsURLConnection conn=null;
        try {
            trustAllHosts();
            URL url;
            if(post) url = new URL("https://community.steam-api.com/"+type+"/v0001/");
            else url = new URL("https://community.steam-api.com/ITerritoryControlMinigameService/"+type+"/v0001/?"+dat);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(post?"POST":"GET");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("Origin", "https://steamcommunity.com");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Referer", "https://steamcommunity.com/saliengame/play");
//            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
            conn.setRequestProperty("User-Agent","SaliensAuto (https://github.com/KickVN/SaliensAuto)");
            conn.setUseCaches(false);
            if(post) {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                if (dat.length() > 0) dat += "&access_token=" + Main.token;
                else dat = "access_token=" + Main.token;
                byte[] postData = dat.getBytes(StandardCharsets.UTF_8);
                wr.write(postData);
            }

            result.parseData(conn);
            return result;
        } catch (IOException e) {
            Main.debug("&cError: &rCouldn't connect to Steam Server.");
            if(result!=null && result.responseCode>=0) Main.debug("\tResponse code: &e"+result.responseCode+" - &e"+getResponseMessage(result.responseCode,e));
        }
        finally {
            if(conn!=null) conn.disconnect();
            if(result!=null && result.eResult>=0){
                if(result.eResult!=1){
                    Main.debug("\tEResult: &c"+result.eResult+"&r"+(result.errorMessage!=null?" - Error message: &c"+result.errorMessage:""));
                }
            }
        }
        return null;
    }

    private static String getResponseMessage(int responseCode, Exception e) {
        switch (responseCode){
            case 401: return "Unauthorized. It could mean your token is incorrect.";
            case 403: return "Forbidden";
            case 500: return "Internal Server Error";
            case 503: return "Service Unavailable. Most likely this means server goes down.  Let's wait a little while.";
        }
        return e.getLocalizedMessage();
    }

    public static RequestResult get(String type, String dat) {
        return sendRequest(type,dat,false);
    }

    public static String githubApi(String text) {
        try {
            trustAllHosts();
            URL url = new URL("https://api.github.com/repos/KickVN/SaliensAuto/"+text);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent","SaliensAuto (https://github.com/KickVN/SaliensAuto)");
//            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
            conn.setUseCaches(false);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
            if (conn.getResponseCode() != 200) return null;
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            conn.disconnect();
            in.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Copied from stackoverflow lol
    public static void trustAllHosts()
    {
        try
        {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509ExtendedTrustManager()
                    {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException
                        {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException
                        {
                        }
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException
                        {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException
                        {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new  HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }
        catch (Exception e)
        {
        }
    }
}
