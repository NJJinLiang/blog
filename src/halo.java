public class halo {

    private static byte sumCheck(byte[] b){
        byte sum = 0;
        for(int i = 0; i < b.length; i++){
            sum ^= b[i];
        }
        return sum;
    }

    public static void main(String[] strings){
        //读手环指令   AA 00 0A 20 01 01 01 ff ff ff ff ff ff 2b BB
        //读泳标指令 AA 00 04 11 02 00 02 15 BB
//        byte[] b = { (byte) 0x00, (byte) 0x0A, (byte) 0x20, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        byte[] b = { (byte) 0x00, 0x04, 0x11, 0x02, 0x00, 0x02};
//        byte result = sumCheck(b);
//        System.out.printf("%x", result);

        System.out.println((byte)100 == (byte)0x64);

    }

}
