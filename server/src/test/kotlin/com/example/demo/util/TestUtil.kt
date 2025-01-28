package com.example.demo.util

import org.mockito.Mockito

object TestUtil {
    fun <T> any(clazz: Class<T>): T {
        return Mockito.any(clazz)
    }
}