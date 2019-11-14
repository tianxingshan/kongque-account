package com.kongque.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.codingapi.tx.aop.bean.TxTransactionLocal;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
	
	private static Logger logger=LoggerFactory.getLogger(HttpClientUtils.class);
	
	public static String doGet(String url, Map<String, String> param) {
		return doGet(SysUtil.getToken(),url,param);
	}

	public static String doGet(String token,String url, Map<String, String> param) {
		
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);
			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpGet.addHeader("tx-group",groupId);
			//
			if(token != null) {
				httpGet.addHeader("token", token);
			}
			// 执行请求
			response = httpclient.execute(httpGet);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}

		}
		logger.info("系统调用GET："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"param:"+param+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, String> param) {
		return doPost(SysUtil.getToken(),url,param);
	}

	public static String doPost(String token,String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);

			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}

			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpPost.addHeader("tx-group",groupId);
			//
			if(token != null) {
				httpPost.addHeader("token", token);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用POST："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"param:"+param+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		return resultString;
	}
	/**
	 * 向订单中心发送post请求
	 * @param sysCode
	 * @param url
	 * @param json
	 * @return
	 */
	public static String doPostForDataCenter(String token,String sysCode,String url, String json) {

		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpPost.addHeader("tx-group",groupId);
			if(sysCode!=null){
				httpPost.addHeader("sysCode", sysCode);
			}
			if(token!=null){
				httpPost.addHeader("token", token);
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用POST："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"数据:"+json+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		
		return resultString;
	
	}
	/**
	 * 向订单中心发送post请求
	 * @param sysCode
	 * @param url
	 * @param json
	 * @return
	 */
	public static String doPutForDataCenter(String token,String sysCode,String url, String json) {

		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Put请求
			HttpPut httpPut = new HttpPut(url);
			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpPut.addHeader("tx-group",groupId);
			if(sysCode!=null){
				httpPut.addHeader("sysCode", sysCode);
			}
			if(token!=null){
				httpPut.addHeader("token", token);
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPut.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPut);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用PUT："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"数据:"+json+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		
		return resultString;
	
	}
	public static String doPost(String url) {
		return doPost(url, null);
	}

	public static String doPostJson(String url, String json) {
		return doPostJson(SysUtil.getToken(),url,json);
	}

	public static String doPostJson(String token,String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);

			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpPost.addHeader("tx-group",groupId);
			//
			if(token != null) {
				httpPost.addHeader("token", token);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用POST："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"数据:"+json+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		
		return resultString;
	}
	public static String doPutJson(String url, String json) {
		return doPutJson(SysUtil.getToken(),url,json);
	}
	public static String doPutJson(String token,String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Put请求
			HttpPut httpPut = new HttpPut(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPut.setEntity(entity);

			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpPut.addHeader("tx-group",groupId);
			if(token!=null){
				httpPut.addHeader("token", token);
			}
			// 执行http请求
			response = httpClient.execute(httpPut);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用PUT："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"数据:"+json+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		
		return resultString;
	
	}
	public static String doDelete(String url) {
		return doDelete(SysUtil.getToken(),url);
	}
	public static String doDelete(String token,String url) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Delete请求
			HttpDelete httpDelete = new HttpDelete(url);

			//分布式事务head
			TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
			String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
			httpDelete.addHeader("tx-group",groupId);
			if(token!=null){
				httpDelete.addHeader("token", token);
			}
			// 执行http请求
			response = httpClient.execute(httpDelete);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			logger.error("http错误",e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("http错误",e);
			}
		}
		logger.info("系统调用DEL："+System.lineSeparator()+"token:"+token+System.lineSeparator()+"url:"+url+System.lineSeparator()+"result:"+resultString);
		
		return resultString;
	
	}
}
