package com.jeankarax.codewars.model.response

data class ViewResponse <out T>(
    val status: Status,
    val data: T? = null,
    val message: String?= null,
    val throwable: Throwable? = null
){

    companion object {

        fun <T> success(data: T?): ViewResponse<T> {
            return ViewResponse(
                status = Status.SUCCESS,
                data = data
            )
        }

        fun <T> error(msg: String, data: T?, throwable: Throwable?): ViewResponse<T> {
            return ViewResponse(
                Status.ERROR,
                data,
                msg,
                throwable
            )
        }

        fun <T> loading(data: T?): ViewResponse<T> {
            return ViewResponse(
                Status.LOADING,
                data
            )
        }

    }

}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}