package org.networkedassets.kdc

import io.kotlintest.matchers.Matcher
import io.kotlintest.specs.StringSpec

object not

infix fun <T> T.should(x: not): NotWrapper<T> = NotWrapper(this)
class NotWrapper<T>(val value: T)
infix fun <T> NotWrapper<T>.equal(any: Any?) {
    when (any) {
        is Matcher<*> -> (any as Matcher<T>).test(value)
        else -> {
            if (this == any)
                throw AssertionError(value.toString() + " did equal $any")
        }
    }
}

abstract class StringSpecEx(val init: StringSpecEx.() -> Unit): StringSpec() {
    init { init() }
}