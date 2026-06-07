package io.appwrite.core.result

import kotlinx.serialization.Serializable

/**
 * Sealed result type — errors are values, not exceptions.
 *
 * Usage:
 * ```
 * when (val result = auth.signIn(email, password)) {
 *     is AppwriteResult.Success -> navigateHome(result.data)
 *     is AppwriteResult.Failure -> showError(result.error)
 * }
 * ```
 */
sealed interface AppwriteResult<out T> {

    data class Success<T>(val data: T) : AppwriteResult<T>

    data class Failure(val error: AppwriteError) : AppwriteResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw AppwriteException(error)
    }

    fun errorOrNull(): AppwriteError? = when (this) {
        is Success -> null
        is Failure -> error
    }

    companion object {
        fun <T> success(data: T): AppwriteResult<T> = Success(data)
        fun failure(error: AppwriteError): AppwriteResult<Nothing> = Failure(error)

        inline fun <T> catching(block: () -> T): AppwriteResult<T> = try {
            Success(block())
        } catch (e: AppwriteException) {
            Failure(e.error)
        } catch (e: Exception) {
            Failure(
                AppwriteError(
                    message = e.message ?: "Unknown error",
                    code = 0,
                    type = "unknown",
                )
            )
        }
    }
}

/**
 * Transform success value while preserving failure.
 */
inline fun <T, R> AppwriteResult<T>.map(transform: (T) -> R): AppwriteResult<R> = when (this) {
    is AppwriteResult.Success -> AppwriteResult.Success(transform(data))
    is AppwriteResult.Failure -> this
}

/**
 * Chain dependent operations.
 */
inline fun <T, R> AppwriteResult<T>.flatMap(
    transform: (T) -> AppwriteResult<R>,
): AppwriteResult<R> = when (this) {
    is AppwriteResult.Success -> transform(data)
    is AppwriteResult.Failure -> this
}

/**
 * Execute side effect on success.
 */
inline fun <T> AppwriteResult<T>.onSuccess(action: (T) -> Unit): AppwriteResult<T> {
    if (this is AppwriteResult.Success) action(data)
    return this
}

/**
 * Execute side effect on failure.
 */
inline fun <T> AppwriteResult<T>.onFailure(action: (AppwriteError) -> Unit): AppwriteResult<T> {
    if (this is AppwriteResult.Failure) action(error)
    return this
}

@Serializable
data class AppwriteError(
    val message: String,
    val code: Int,
    val type: String,
)

class AppwriteException(
    val error: AppwriteError,
) : Exception(error.message)
