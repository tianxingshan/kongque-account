/**
 * 
 */
package com.kongque.component.RequestCustom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * @author yuehui
 *
 * @2018年1月17日
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] bytes;
    private WrappedServletInputStream wrappedServletInputStream;

    public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // 读取输入流里的请求参数，并保存到bytes里
        bytes = IOUtils.toByteArray(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        this.wrappedServletInputStream = new WrappedServletInputStream(byteArrayInputStream);
        // 很重要，把post参数重新写入请求流
        reWriteInputStream();
    }

    /**
     * 把参数重新写进请求里
     */
    public void reWriteInputStream() {
        wrappedServletInputStream.setStream(new ByteArrayInputStream(bytes != null ? bytes : new byte[0]));
    }
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return wrappedServletInputStream;
    }

   @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(wrappedServletInputStream));
    }

    /**
     * 获取post参数，可以自己再转为相应格式
     */
    public String getRequestParams() throws IOException {
        return new String(bytes, this.getCharacterEncoding());
    }

    private class WrappedServletInputStream extends ServletInputStream {

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        private InputStream stream;

        public WrappedServletInputStream(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

		/* (non-Javadoc)
		 * @see javax.servlet.ServletInputStream#isFinished()
		 */
		@Override
		public boolean isFinished() {
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletInputStream#isReady()
		 */
		@Override
		public boolean isReady() {
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
		 */
		@Override
		public void setReadListener(ReadListener readListener) {
			
		}
    }
}  
