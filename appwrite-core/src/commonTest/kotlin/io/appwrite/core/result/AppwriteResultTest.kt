package io.appwrite.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class AppwriteResultTest {

    private val sampleError = AppwriteError(
        message = "Not found",
        code = 404,
        type = "not_found",
    )

    // --- Construction ---

    @Test
    fun test_success_returnsData() {
        val result = AppwriteResult.success("hello")

        assertIs<AppwriteResult.Success<String>>(result)
        assertEquals("hello", result.data)
    }

    @Test
    fun test_failure_returnsError() {
        val result = AppwriteResult.failure(sampleError)

        assertIs<AppwriteResult.Failure>(result)
        assertEquals(sampleError, result.error)
    }

    // --- getOrNull ---

    @Test
    fun test_getOrNull_returnsNullOnFailure() {
        val result: AppwriteResult<String> = AppwriteResult.failure(sampleError)

        val value = result.getOrNull()

        assertNull(value)
    }

    @Test
    fun test_getOrNull_returnsDataOnSuccess() {
        val result = AppwriteResult.success(42)

        val value = result.getOrNull()

        assertNotNull(value)
        assertEquals(42, value)
    }

    // --- getOrThrow ---

    @Test
    fun test_getOrThrow_throwsOnFailure() {
        val result: AppwriteResult<String> = AppwriteResult.failure(sampleError)

        val exception = assertFailsWith<AppwriteException> {
            result.getOrThrow()
        }

        assertEquals(sampleError, exception.error)
    }

    @Test
    fun test_getOrThrow_returnsDataOnSuccess() {
        val result = AppwriteResult.success("data")

        val value = result.getOrThrow()

        assertEquals("data", value)
    }

    // --- map ---

    @Test
    fun test_map_transformsSuccessValue() {
        val result = AppwriteResult.success(10)

        val mapped = result.map { it * 2 }

        assertIs<AppwriteResult.Success<Int>>(mapped)
        assertEquals(20, mapped.data)
    }

    @Test
    fun test_map_preservesFailure() {
        val result: AppwriteResult<Int> = AppwriteResult.failure(sampleError)

        val mapped = result.map { it * 2 }

        assertIs<AppwriteResult.Failure>(mapped)
        assertEquals(sampleError, mapped.error)
    }

    // --- flatMap ---

    @Test
    fun test_flatMap_chainsSuccessOperations() {
        val result = AppwriteResult.success(5)

        val chained = result.flatMap { AppwriteResult.success(it.toString()) }

        assertIs<AppwriteResult.Success<String>>(chained)
        assertEquals("5", chained.data)
    }

    @Test
    fun test_flatMap_shortCircuitsOnFailure() {
        val result: AppwriteResult<Int> = AppwriteResult.failure(sampleError)
        var called = false

        val chained = result.flatMap {
            called = true
            AppwriteResult.success(it.toString())
        }

        assertFalse(called)
        assertIs<AppwriteResult.Failure>(chained)
    }

    // --- catching ---

    @Test
    fun test_catching_wrapsExceptions() {
        val result = AppwriteResult.catching<String> {
            throw RuntimeException("boom")
        }

        assertIs<AppwriteResult.Failure>(result)
        assertEquals("boom", result.error.message)
        assertEquals(0, result.error.code)
        assertEquals("unknown", result.error.type)
    }

    @Test
    fun test_catching_returnsSuccessOnNoException() {
        val result = AppwriteResult.catching { "ok" }

        assertIs<AppwriteResult.Success<String>>(result)
        assertEquals("ok", result.data)
    }

    // --- onSuccess / onFailure ---

    @Test
    fun test_onSuccess_executesOnlyForSuccess() {
        var captured: String? = null
        val result = AppwriteResult.success("yes")

        result.onSuccess { captured = it }

        assertEquals("yes", captured)
    }

    @Test
    fun test_onSuccess_doesNotExecuteForFailure() {
        var called = false
        val result: AppwriteResult<String> = AppwriteResult.failure(sampleError)

        result.onSuccess { called = true }

        assertFalse(called)
    }

    @Test
    fun test_onFailure_executesOnlyForFailure() {
        var captured: AppwriteError? = null
        val result: AppwriteResult<String> = AppwriteResult.failure(sampleError)

        result.onFailure { captured = it }

        assertEquals(sampleError, captured)
    }

    @Test
    fun test_onFailure_doesNotExecuteForSuccess() {
        var called = false
        val result = AppwriteResult.success("yes")

        result.onFailure { called = true }

        assertFalse(called)
    }

    // --- flags ---

    @Test
    fun test_isSuccess_isFailure_flags() {
        val success = AppwriteResult.success("x")
        val failure = AppwriteResult.failure(sampleError)

        assertTrue(success.isSuccess)
        assertFalse(success.isFailure)
        assertTrue(failure.isFailure)
        assertFalse(failure.isSuccess)
    }
}
