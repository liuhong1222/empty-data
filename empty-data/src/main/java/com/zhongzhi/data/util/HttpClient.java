package com.zhongzhi.data.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 
 * @author zhanggt
 * 
 */
public class HttpClient {

	private static final String CHARSET = "UTF-8";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "application/x-www-form-urlencoded;charset=UTF-8";

	public static Map<String, Object> jsonPost(String url, Map<String, Object> params) {
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject(params);
		String reqStr = json.toJSONString();
		String respStr = post(url, reqStr);
		com.alibaba.fastjson.JSONObject resp = com.alibaba.fastjson.JSON.parseObject(respStr);
		return resp;
	}

	/**
	 * http post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return
	 */
	public static String post(String url, Map<String, Object> params) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				if(params.get(key)!=null){
					parameters.add(new BasicNameValuePair(key, params.get(key).toString()));
				}else{
					parameters.add(new BasicNameValuePair(key, null));
				}
			}
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, CHARSET);
			uefEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
			httpPost.setEntity(uefEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, CHARSET);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public static String uploadFile(String httpUrl, byte[] b, int length, String fileName) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Upload-File-Name", fileName);
			connection.setRequestProperty("Content-Length", "" + length);
			connection.setDoOutput(true);
			connection.getOutputStream().write(b, 0, length);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static String postJSON(String URL, String JSONBody) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(URL);
			httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			StringEntity sEntity = new StringEntity(JSONBody, CHARSET);
			httpPost.setEntity(sEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, CHARSET);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public static String post(String httpUrl, byte[] b, int length) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "multipart/form-data");
			connection.setDoOutput(true);
			connection.getOutputStream().write(b, 0, length);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * http post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return
	 */
	public static String post(String url, String params) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			StringEntity sEntity = new StringEntity(params, CHARSET);
			sEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
			sEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
			httpPost.setEntity(sEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, CHARSET);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	
	
	public static String get(String fullUrl) {
		return httpGet(fullUrl,CHARSET);
	}

	private static String httpGet(String fullUrl,String reCharSet) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(fullUrl);
			httpGet.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, reCharSet);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	
	public static String get(String fullUrl,String reCharSet){
		return httpGet(fullUrl,reCharSet);
	}
	

	public static String getUrl(String url, String uri) {
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		if (url.endsWith("/") && uri.startsWith("/")) {
			uri = uri.substring(1);
		} else if (!url.endsWith("/") && !uri.startsWith("/")) {
			sb.append("/");
		}
		sb.append(uri);
		return sb.toString();
	}
	public static String sendHttpsPost(String url, Map<String, Object> params){
		CloseableHttpClient sslClient = null;
		try {
			sslClient = getSSLClient();

			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				if(params.get(key)!=null){
					parameters.add(new BasicNameValuePair(key, params.get(key).toString()));
				}else{
					parameters.add(new BasicNameValuePair(key, null));
				}
			}
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, CHARSET);
			uefEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
			httpPost.setEntity(uefEntity);
			CloseableHttpResponse response = sslClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, CHARSET);
				}
			} finally {
				response.close();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}


		return "";
	}

	/**
	 * 创建Https client
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	private static CloseableHttpClient getSSLClient() throws KeyManagementException, NoSuchAlgorithmException {
		CloseableHttpClient client;
		SSLContext sslcontext = SSLContexts.custom().useSSL().build();
		sslcontext.init(null, new X509TrustManager[]{new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		}}, new SecureRandom());
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		client = HttpClients.custom().setSSLSocketFactory(factory).build();

		return client;
	}

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
		Map<String,Object> param = new HashMap<>();
		param.put("AppSecretKey","0YRupPaTuBf144iPzdTqPCw**");
		param.put("appId","Oouej0jL");
		param.put("appKey","QJpXTaEa");
		param.put("CaptchaAppId","2048455487");
		param.put("RendStr","@zLo");
		param.put("Ticket","t020S0CF-dbM7JkWQTYk4VrM4QPpmweejW9BjW0o6bhURKO2VHuK19dVzQVGPIn-fUuJM5uqEaivxAGAEENxr7q8pfD-2vkegfzalUouijNaZ6-4vzUZCpH9g**");
		param.put("IP","127.0.0.1");
		String s = HttpClient.sendHttpsPost("https://api.253.com/open/wool/yzm", param);
		System.out.println(s);
	}
}
