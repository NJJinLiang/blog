package serialPort;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kimi
 * 2019/1/13
 */
public class EncodingFormatUtils {
    //日期格式化
    public static String dateFormateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    public static String dateFormateToString(Date date,String formatStr) {
        return new SimpleDateFormat(formatStr).format(date);
    }

    //byte[] 转 16进制
    public static String toHexString1(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp ="";
        for (int i = 0; i< bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    //byte[] 转 16进制 加空格
    public static String toHexStringAddSpace(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp ="";
        for (int i = 0; i< bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }
    //byte[] 转 16进制 加空格
    public static String toHexStringAddSpace(List<Byte> bArray) {
        StringBuffer sb = new StringBuffer(bArray.size());
        String sTemp ="";
        for (int i = 0; i< bArray.size(); i++) {
            sTemp = Integer.toHexString(0xFF & bArray.get(i));
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }
    //byte[] 转 16进制
    public static String toHexString1(List<Byte> bArray) {
        StringBuffer sb = new StringBuffer(bArray.size());
        String sTemp ="";
        for (int i = 0; i< bArray.size(); i++) {
            sTemp = Integer.toHexString(0xFF & bArray.get(i));
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 把byte转化成2进制字符串
     * @param b
     * @return
     */
    public static String getBinaryStrFromByte(byte b){
        String result ="";
        for (int i = 0; i < 8; i++){
            int x = (((int) b ) & (1<<(7-i))) == 0 ? 0:1;
            result += ("" + x);
        }
        return result;
    }
    //byte[] 转 16进制
    public static String toHexString1(byte bArray) {
        StringBuffer sb = new StringBuffer(1);
        String sTemp = Integer.toHexString(0xFF & bArray);
        if (sTemp.length() < 2)
            sb.append(0);
        sb.append(sTemp.toUpperCase());
        return sb.toString();
    }

    //转 16进制 再转 byte
    public static byte[] toHexToBytes(int i) {
        return hexTobytes(decimalToHex(i));
    }

    //转 16进制 再转 byte
    public static byte[] toHexToBytes(String str) {
        return hexTobytes(strTo16(str));
    }

    /**
     * 十六进制转byte[]
     * @param hexString
     * @return
     */
    public static byte[] hexTobytes(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0"+ hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //10进制转16进制
    public static String decimalToHex(int decimal) {
        return Integer.toHexString(decimal);
    }

    /**
     * 字符串转化成为16进制字符串
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0 ; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s.length() == 0) {
            return "";
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i< baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            s = new String(baKeyword);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return s;
    }

    /**
     * u32byte
     * @param hex
     * @return
     */
    public static byte[] u32HexToByteOrder(String hex , int lenght){
        byte[] id = EncodingFormatUtils.hexTobytes(hex);
        byte[] idByte = new byte[lenght];
        for (int i = id.length-1; i >= 0; i--) {
            idByte[id.length-1-i] = id[i];
        }
        return idByte;
    }
    public static byte[] u32DecimaltoByteOrder(int i){
        return u32HexToByteOrder(decimalToHex(i) , 4);
    }

    public static byte[] byteListToArray(List<Byte> list){
        byte[] bytes = new byte[list.size()];
        for(int i = 0 ; i< list.size(); i++){
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static List<Byte> byteArrayToList(byte[] bytes){
        List<Byte> list = new ArrayList<>();
        for(int i = 0 ; i< bytes.length; i++){
            list.add(bytes[i]);
        }
        return list;
    }
    public static byte[] subBytes(byte[] srcBytes , int off, int len){
        byte[] newBytes = new byte[len];
        System.arraycopy(srcBytes, off, newBytes, 0, len);
        return newBytes;
    }

    public static byte[] getBytesOfLong(long x , int len)
    {
        byte[ ] rt = new byte[len];
        for(int i = 0 ; i< len; i++){
            rt[i] = (byte)((x>> i*8 ) & 0xff);
        }

        return rt ;

    }

}
