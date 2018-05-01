package core;

import io.reactivex.Maybe;

/**
 * 描述：
 *
 * @author jzb 2018-05-01
 */
public class RxTest {
    public static void main(String[] args) {
        Maybe.empty()
                .toSingle("")
                .doOnSuccess(it -> System.out.print("value=" + it))
                .subscribe();
    }
}
