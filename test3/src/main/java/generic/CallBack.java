package generic;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public interface CallBack<K, V> {

    void call(K k, V v);

    void call2(K k, V v);
}
