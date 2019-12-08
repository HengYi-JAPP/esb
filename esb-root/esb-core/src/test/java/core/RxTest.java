package core;

import java.util.Base64;

/**
 * 描述：
 *
 * @author jzb 2018-05-01
 */
public class RxTest {
    public static void main(String[] args) {
        final byte[] bytes = Base64.getDecoder().decode("cm9vdDEyMzQ1");
        System.out.println(new String(bytes));
    }
}
