/**
 * This class file was automatically generated by jASN1 v1.8.2 (http://www.openmuc.org)
 */

package org.onosproject.xran.asn1lib.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.onosproject.xran.asn1lib.ber.BerByteArrayOutputStream;
import org.onosproject.xran.asn1lib.ber.BerLength;
import org.onosproject.xran.asn1lib.ber.BerTag;
import org.onosproject.xran.asn1lib.ber.types.BerInteger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SchedMeasRepPerServCell implements Serializable {

    public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    public byte[] code = null;
    private ECGI ecgi = null;
    private QciVals qciVals = null;
    private PRBUsage prbUsage = null;
    private McsDl mcsDl = null;
    private NumSchedTtisDl numSchedTtisDl = null;
    private McsUl mcsUl = null;
    private NumSchedTtisUl numSchedTtisUl = null;
    private RankDl1 rankDl1 = null;
    private RankDl2 rankDl2 = null;
    public SchedMeasRepPerServCell() {
    }
    public SchedMeasRepPerServCell(byte[] code) {
        this.code = code;
    }

    public ECGI getEcgi() {
        return ecgi;
    }

    public void setEcgi(ECGI ecgi) {
        this.ecgi = ecgi;
    }

    public QciVals getQciVals() {
        return qciVals;
    }

    public void setQciVals(QciVals qciVals) {
        this.qciVals = qciVals;
    }

    public PRBUsage getPrbUsage() {
        return prbUsage;
    }

    public void setPrbUsage(PRBUsage prbUsage) {
        this.prbUsage = prbUsage;
    }

    public McsDl getMcsDl() {
        return mcsDl;
    }

    public void setMcsDl(McsDl mcsDl) {
        this.mcsDl = mcsDl;
    }

    public NumSchedTtisDl getNumSchedTtisDl() {
        return numSchedTtisDl;
    }

    public void setNumSchedTtisDl(NumSchedTtisDl numSchedTtisDl) {
        this.numSchedTtisDl = numSchedTtisDl;
    }

    public McsUl getMcsUl() {
        return mcsUl;
    }

    public void setMcsUl(McsUl mcsUl) {
        this.mcsUl = mcsUl;
    }

    public NumSchedTtisUl getNumSchedTtisUl() {
        return numSchedTtisUl;
    }

    public void setNumSchedTtisUl(NumSchedTtisUl numSchedTtisUl) {
        this.numSchedTtisUl = numSchedTtisUl;
    }

    public RankDl1 getRankDl1() {
        return rankDl1;
    }

    public void setRankDl1(RankDl1 rankDl1) {
        this.rankDl1 = rankDl1;
    }

    public RankDl2 getRankDl2() {
        return rankDl2;
    }

    public void setRankDl2(RankDl2 rankDl2) {
        this.rankDl2 = rankDl2;
    }

    public int encode(BerByteArrayOutputStream os) throws IOException {
        return encode(os, true);
    }

    public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
            if (withTag) {
                return tag.encode(os) + code.length;
            }
            return code.length;
        }

        int codeLength = 0;
        codeLength += rankDl2.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 8
        os.write(0xA8);
        codeLength += 1;

        codeLength += rankDl1.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 7
        os.write(0xA7);
        codeLength += 1;

        codeLength += numSchedTtisUl.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 6
        os.write(0xA6);
        codeLength += 1;

        codeLength += mcsUl.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 5
        os.write(0xA5);
        codeLength += 1;

        codeLength += numSchedTtisDl.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 4
        os.write(0xA4);
        codeLength += 1;

        codeLength += mcsDl.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 3
        os.write(0xA3);
        codeLength += 1;

        codeLength += prbUsage.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 2
        os.write(0xA2);
        codeLength += 1;

        codeLength += qciVals.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 1
        os.write(0xA1);
        codeLength += 1;

        codeLength += ecgi.encode(os, false);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 0
        os.write(0xA0);
        codeLength += 1;

        codeLength += BerLength.encodeLength(os, codeLength);

        if (withTag) {
            codeLength += tag.encode(os);
        }

        return codeLength;

    }

    public int decode(InputStream is) throws IOException {
        return decode(is, true);
    }

    public int decode(InputStream is, boolean withTag) throws IOException {
        int codeLength = 0;
        int subCodeLength = 0;
        BerTag berTag = new BerTag();

        if (withTag) {
            codeLength += tag.decodeAndCheck(is);
        }

        BerLength length = new BerLength();
        codeLength += length.decode(is);

        int totalLength = length.val;
        codeLength += totalLength;

        subCodeLength += berTag.decode(is);
        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 0)) {
            ecgi = new ECGI();
            subCodeLength += ecgi.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 1)) {
            qciVals = new QciVals();
            subCodeLength += qciVals.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 2)) {
            prbUsage = new PRBUsage();
            subCodeLength += prbUsage.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 3)) {
            mcsDl = new McsDl();
            subCodeLength += mcsDl.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 4)) {
            numSchedTtisDl = new NumSchedTtisDl();
            subCodeLength += numSchedTtisDl.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 5)) {
            mcsUl = new McsUl();
            subCodeLength += mcsUl.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 6)) {
            numSchedTtisUl = new NumSchedTtisUl();
            subCodeLength += numSchedTtisUl.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 7)) {
            rankDl1 = new RankDl1();
            subCodeLength += rankDl1.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 8)) {
            rankDl2 = new RankDl2();
            subCodeLength += rankDl2.decode(is, false);
            if (subCodeLength == totalLength) {
                return codeLength;
            }
        }
        throw new IOException("Unexpected end of sequence, length tag: " + totalLength + ", actual sequence length: " + subCodeLength);


    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
        encode(os, false);
        code = os.getArray();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendAsString(sb, 0);
        return sb.toString();
    }

    public void appendAsString(StringBuilder sb, int indentLevel) {

        sb.append("{");
        sb.append("\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (ecgi != null) {
            sb.append("ecgi: ");
            ecgi.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("ecgi: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (qciVals != null) {
            sb.append("qciVals: ");
            qciVals.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("qciVals: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (prbUsage != null) {
            sb.append("prbUsage: ");
            prbUsage.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("prbUsage: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (mcsDl != null) {
            sb.append("mcsDl: ");
            mcsDl.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("mcsDl: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (numSchedTtisDl != null) {
            sb.append("numSchedTtisDl: ");
            numSchedTtisDl.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("numSchedTtisDl: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (mcsUl != null) {
            sb.append("mcsUl: ");
            mcsUl.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("mcsUl: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (numSchedTtisUl != null) {
            sb.append("numSchedTtisUl: ");
            numSchedTtisUl.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("numSchedTtisUl: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (rankDl1 != null) {
            sb.append("rankDl1: ");
            rankDl1.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("rankDl1: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (rankDl2 != null) {
            sb.append("rankDl2: ");
            rankDl2.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("rankDl2: <empty-required-field>");
        }

        sb.append("\n");
        for (int i = 0; i < indentLevel; i++) {
            sb.append("\t");
        }
        sb.append("}");
    }

    public static class QciVals implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<QCI> seqOf = null;

        public QciVals() {
            seqOf = new ArrayList<QCI>();
        }

        public QciVals(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<QCI> getQCI() {
            if (seqOf == null) {
                seqOf = new ArrayList<QCI>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                QCI element = new QCI();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<QCI> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class McsDl implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public McsDl() {
            seqOf = new ArrayList<BerInteger>();
        }

        public McsDl(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class NumSchedTtisDl implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public NumSchedTtisDl() {
            seqOf = new ArrayList<BerInteger>();
        }

        public NumSchedTtisDl(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class McsUl implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public McsUl() {
            seqOf = new ArrayList<BerInteger>();
        }

        public McsUl(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class NumSchedTtisUl implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public NumSchedTtisUl() {
            seqOf = new ArrayList<BerInteger>();
        }

        public NumSchedTtisUl(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class RankDl1 implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public RankDl1() {
            seqOf = new ArrayList<BerInteger>();
        }

        public RankDl1(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

    public static class RankDl2 implements Serializable {

        public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 16);
        private static final long serialVersionUID = 1L;
        @JsonIgnore
        public byte[] code = null;
        private List<BerInteger> seqOf = null;

        public RankDl2() {
            seqOf = new ArrayList<BerInteger>();
        }

        public RankDl2(byte[] code) {
            this.code = code;
        }

        @JsonValue
        public List<BerInteger> getBerInteger() {
            if (seqOf == null) {
                seqOf = new ArrayList<BerInteger>();
            }
            return seqOf;
        }

        public int encode(BerByteArrayOutputStream os) throws IOException {
            return encode(os, true);
        }

        public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

            if (code != null) {
                for (int i = code.length - 1; i >= 0; i--) {
                    os.write(code[i]);
                }
                if (withTag) {
                    return tag.encode(os) + code.length;
                }
                return code.length;
            }

            int codeLength = 0;
            for (int i = (seqOf.size() - 1); i >= 0; i--) {
                codeLength += seqOf.get(i).encode(os, true);
            }

            codeLength += BerLength.encodeLength(os, codeLength);

            if (withTag) {
                codeLength += tag.encode(os);
            }

            return codeLength;
        }

        public int decode(InputStream is) throws IOException {
            return decode(is, true);
        }

        public int decode(InputStream is, boolean withTag) throws IOException {
            int codeLength = 0;
            int subCodeLength = 0;
            if (withTag) {
                codeLength += tag.decodeAndCheck(is);
            }

            BerLength length = new BerLength();
            codeLength += length.decode(is);
            int totalLength = length.val;

            while (subCodeLength < totalLength) {
                BerInteger element = new BerInteger();
                subCodeLength += element.decode(is, true);
                seqOf.add(element);
            }
            if (subCodeLength != totalLength) {
                throw new IOException("Decoded SequenceOf or SetOf has wrong length. Expected " + totalLength + " but has " + subCodeLength);

            }
            codeLength += subCodeLength;

            return codeLength;
        }

        public void encodeAndSave(int encodingSizeGuess) throws IOException {
            BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
            encode(os, false);
            code = os.getArray();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAsString(sb, 0);
            return sb.toString();
        }

        public void appendAsString(StringBuilder sb, int indentLevel) {

            sb.append("{\n");
            for (int i = 0; i < indentLevel + 1; i++) {
                sb.append("\t");
            }
            if (seqOf == null) {
                sb.append("null");
            } else {
                Iterator<BerInteger> it = seqOf.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) {
                        sb.append(",\n");
                        for (int i = 0; i < indentLevel + 1; i++) {
                            sb.append("\t");
                        }
                        sb.append(it.next());
                    }
                }
            }

            sb.append("\n");
            for (int i = 0; i < indentLevel; i++) {
                sb.append("\t");
            }
            sb.append("}");
        }

    }

}

