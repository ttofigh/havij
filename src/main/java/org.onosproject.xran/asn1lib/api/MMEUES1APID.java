/**
 * This class file was automatically generated by jASN1 v1.8.2 (http://www.openmuc.org)
 */

package org.onosproject.xran.asn1lib.api;

import org.onosproject.xran.asn1lib.ber.types.BerInteger;

import java.math.BigInteger;
import java.util.Objects;


public class MMEUES1APID extends BerInteger {

    private static final long serialVersionUID = 1L;

    public MMEUES1APID() {
    }

    public MMEUES1APID(byte[] code) {
        super(code);
    }

    public MMEUES1APID(BigInteger value) {
        super(value);
    }

    public MMEUES1APID(long value) {
        super(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MMEUES1APID) {
            return Objects.equals(value, ((MMEUES1APID) obj).value);
        }
        return super.equals(obj);
    }
}
