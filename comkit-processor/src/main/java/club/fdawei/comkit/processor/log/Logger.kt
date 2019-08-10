package club.fdawei.comkit.processor.log

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Create by david on 2019/07/20.
 */
const val LOG_TAG = "ComKitProcessor"

class Logger(
    private val messager: Messager
) {

    fun i(msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, "$LOG_TAG $msg")
    }

    fun w(msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.WARNING, "$LOG_TAG $msg")
    }

    fun e(msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.ERROR, "$LOG_TAG $msg")
    }
}