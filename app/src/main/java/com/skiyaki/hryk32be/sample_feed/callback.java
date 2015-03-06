package com.skiyaki.hryk32be.sample_feed;

import java.util.Map;

/**
 * Created by hryk32be on 2015/03/06.
 */
interface Callback {

    /** 成功時のレスポンスコード */
    public static final int SUCCESS = 0;

    /** 失敗時のレスポンスコード */
    public static final int ERROR = -1;

    /**
     * コールバックメソッド
     */
    public void callback(final String token);

}
