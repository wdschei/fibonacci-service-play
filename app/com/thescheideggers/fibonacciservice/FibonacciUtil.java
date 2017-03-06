package com.thescheideggers.fibonacciservice;

import java.io.*;

public class FibonacciUtil {

    /**
     * Don't let anyone instantiate this class.
     */
    private FibonacciUtil() {
        // Don't let anyone instantiate this class.
    }

    public static byte[] toBytes(Serializable serializable) {
        byte[] rtn = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(serializable);
            out.flush();
            rtn = bos.toByteArray();
        } catch (IOException e) {
            // ignore close exception
        }
        return rtn;
    }

    public static Object fromBytes(byte[] bytes) {
        Object rtn = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            rtn = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // ignore close exception
        }
        return rtn;
    }
}
