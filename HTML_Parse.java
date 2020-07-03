package com.zoho.ifsc;

import javax.net.ssl.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class HTML_Parse {
    public static void main(String args[]) throws Exception {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        // Now you can access URL(https) without having the certificate in the truststore
        try {

            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                            + session.getPeerHost());
                    return true;
                }
            };

            String datam = "param=myparam";
            URL url = new URL("https://www.npci.org.in/national-automated-clearing-live-members-1");
            URLConnection conn = url.openConnection();
            HttpsURLConnection urlConn = (HttpsURLConnection) conn;
            urlConn.setHostnameVerifier(hv);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(datam);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            String res = sb.toString();
            FileWriter fw=new FileWriter("NACH_valid.xls");
    		FileWriter fw1=new FileWriter("NACH_invalid.xls");
    		fw.write("Bank Name\tIFSC Code\n");
    		fw1.write("Bank Name\tIFSC Code\n");
            Document doc = Jsoup.parse(res);
            Element table = doc.select("table").get(0);
            Iterator<Element> ite = table.select("td").iterator();
            int i=0;
            while(ite.hasNext())
            {
            	ArrayList<String> temp = new ArrayList<>();
            	ite.next();
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            temp.add(ite.next().text());
	            ite.next();
	            if(temp.get(3).length()==11)
	            {
	            	fw.write(temp.get(1)+"\t"+temp.get(3)+"\n");
	            }
	            else
	            {
	            	fw1.write(temp.get(1)+"\t"+temp.get(3)+"\n");
	            }
	            System.out.println(++i);
            }
            fw.close();
            fw1.close();
        } catch (MalformedURLException e) {
            System.out.println("Error in SLL Connetion" + e);
        }
    }
}