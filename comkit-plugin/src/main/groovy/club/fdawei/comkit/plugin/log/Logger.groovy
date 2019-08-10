package club.fdawei.comkit.plugin.log

/**
 * Created by david on 2019/07/15.
 */
class Logger {
    static void i(String msg) {
        System.out.println("$LOG_TAG $msg")
    }

    static void e(String msg) {
        System.err.println("$LOG_TAG $msg")
    }

    static final String LOG_TAG = "ComKit"
}
