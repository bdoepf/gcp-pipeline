package de.bdoepf;

import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;

@DefaultSchema(JavaBeanSchema.class)
public class IncomingPubSubMsg {
    private int a, e;
    private String b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
