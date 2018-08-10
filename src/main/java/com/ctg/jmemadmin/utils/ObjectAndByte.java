package com.ctg.jmemadmin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectAndByte {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectAndByte.class);
	
	/**
	 * 对象转byte数组
	 * @param obj
	 * @return
	 */
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			bytes = byteArrayOutputStream.toByteArray();
			objectOutputStream.close();
			byteArrayOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static Object toObject(byte[] bytes) {
		Object obj = null;      
        try {        
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream (bytes);        
            ObjectInputStream objectInputStream = new ObjectInputStream (byteArrayInputStream);        
            obj = objectInputStream.readObject();      
            objectInputStream.close();   
            byteArrayInputStream.close();   
        } catch (IOException e) {        
            e.printStackTrace();   
        } catch (ClassNotFoundException e) {        
            e.printStackTrace();   
        }      
        return obj;    
	}
	
	public static void main(String[] args) {   
        Object tb = new Object();   
        tb = 124;
        byte[] b = ObjectAndByte.toByteArray(tb);   
        System.out.println(new String(b));
        System.out.println("=======================================");   
        Object teb = (Object) ObjectAndByte.toObject(b);
        System.out.println(teb.toString());
    }   
}
