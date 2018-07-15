/**
 * This class file was automatically generated by jASN1 v1.8.2 (http://www.openmuc.org)
 */

package org.onosproject.xran.asn1lib.api;

import org.onosproject.xran.asn1lib.ber.types.BerInteger;

import java.math.BigInteger;


public class ERABID extends BerInteger {

    private static final long serialVersionUID = 1L;

    public ERABID() {
    }

    public ERABID(byte[] code) {
        super(code);
    }

    public ERABID(BigInteger value) {
        super(value);
    }

    public ERABID(long value) {
        super(value);
    }

    @Override
    public int hashCode() {
        return value.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ERABID) {
            return value.intValue() == ((ERABID) obj).intValue();
        }
        return super.equals(obj);
    }
}