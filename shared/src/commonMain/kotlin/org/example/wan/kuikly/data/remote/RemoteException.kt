package org.example.wan.kuikly.data.remote

class RemoteException(val statusCode: Int, cause: Throwable?) : Exception(cause) {
    override fun toString(): String {
        return "RemoteException(statusCode=$statusCode, message=${message})"
    }
}
