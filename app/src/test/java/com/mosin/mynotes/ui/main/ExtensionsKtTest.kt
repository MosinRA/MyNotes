package com.mosin.mynotes.ui.main

import org.junit.Test

import org.junit.Assert.*

class ExtensionsKtTest {

    @Test
    fun `should return empty list for null`() {
        val result = emptyList<Int>()
        val testData = null

        assertEquals(result, sortDescAndDistinctAndRemoteNulls(testData))
    }

    @Test
    fun `should return duplicate elements`() {
        val result = listOf<Int>(55)
        val testData = listOf<Int>(55, 55, 55, 55, 55, 55, 55, 55)

        assertEquals(result, sortDescAndDistinctAndRemoteNulls(testData))
    }

    @Test
    fun `should return some list for single not nulls element in list`() {
        val result = listOf(55)
        val testData = listOf(55)

        assertEquals(result, sortDescAndDistinctAndRemoteNulls(testData))
    }

    @Test
    fun `should return sort elements`() {
        val result = listOf(5, 4, 3, 2, 1)
        val testData = listOf(1, 2, 3, 4, 5)

        assertEquals(result, sortDescAndDistinctAndRemoteNulls(testData))
    }

    @Test
    fun `should remove null elements`() {
        val result = listOf(5, 4, 3, 2, 1)
        val testData = listOf(1, 2, 3, 4, 5, null)

        assertEquals(result, sortDescAndDistinctAndRemoteNulls(testData))
    }

}