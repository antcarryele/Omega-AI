package com.omega.engine.nn.network.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.omega.common.data.Tensor;

import jcuda.Sizeof;

public class ModelUtils {
	
	public static void saveParams(RandomAccessFile outputStream,Tensor data) throws IOException {
		writeFloat(outputStream, data);
	}
	
	public static void loadParams(RandomAccessFile inputStream,Tensor data) throws IOException {
		readFloat(inputStream, data);
	}
	
	public static void readFloat(RandomAccessFile inputStream,Tensor data) throws IOException {
		for(int i = 0;i<data.data.length;i++) {
			float v = readFloat(inputStream);
			data.data[i] = v;
			if(v == Float.NaN) {
				System.err.println(v);
			}
			
		}
		if(data.isHasGPU()) {
			data.hostToDevice();
		}
	}
	
	public static void writeFloat(RandomAccessFile outputStream,Tensor data) throws IOException {
		if(data.isHasGPU()) {
			data.syncHost();
		}
		for(int i = 0;i<data.data.length;i++) {
			writeFloat(outputStream, data.data[i]);
		}
	}
	
	public static void writeFloat(RandomAccessFile outputStream,float val) throws IOException {
	    byte[] buffer = float2byte(val);
	    outputStream.write(buffer);
	}
	
	public static float readFloat(RandomAccessFile inputStream) throws IOException {
		float retVal;
	    byte[] buffer = new byte[Sizeof.FLOAT];
	    inputStream.readFully(buffer);
	    ByteBuffer wrapped = ByteBuffer.wrap(buffer);
	    wrapped.order(ByteOrder.LITTLE_ENDIAN);
	    retVal = wrapped.getFloat();
	    return retVal;
	}
	
	public static byte[] float2byte(float f) {
        // 把float转换为byte[]
	    int fbit = Float.floatToIntBits(f);  
	      
	    byte[] b = new byte[4];    
	    for (int i = 0; i < 4; i++) {    
	        b[i] = (byte) (fbit >> (24 - i * 8));    
	    }   
	      
	    // 翻转数组  
	    int len = b.length;  
	    // 建立一个与源数组元素类型相同的数组  
	    byte[] dest = new byte[len];  
	    // 为了防止修改源数组，将源数组拷贝一份副本  
	    System.arraycopy(b, 0, dest, 0, len);  
	    byte temp;  
	    // 将顺位第i个与倒数第i个交换  
	    for (int i = 0; i < len / 2; ++i) {  
	        temp = dest[i];  
	        dest[i] = dest[len - i - 1];  
	        dest[len - i - 1] = temp;  
	    }  
	      
	    return dest;
	}
	
	public static float byte2float(byte[] b, int index) {    
	    int l;                                             
	    l = b[index + 0];                                  
	    l &= 0xff;                                         
	    l |= ((long) b[index + 1] << 8);                   
	    l &= 0xffff;                                       
	    l |= ((long) b[index + 2] << 16);                  
	    l &= 0xffffff;                                     
	    l |= ((long) b[index + 3] << 24);                  
	    return Float.intBitsToFloat(l);                    
	}
	
}
