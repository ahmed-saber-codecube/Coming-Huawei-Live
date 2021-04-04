package com.coming.customer.data.datasource

import com.coming.customer.data.pojo.DataWrapper
import com.coming.customer.data.pojo.ResponseBody
import com.coming.customer.exception.ServerException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

open class BaseDataSource {

    fun <T> execute(observable: Single<ResponseBody<T>>): Single<DataWrapper<T>> {
        return observable
            /*.subscribeOn(Schedulers.from(appExecutors.networkIO()))
            .observeOn(Schedulers.from(appExecutors.mainThread()))*/
            .subscribeOn(Schedulers.io())
            .map { t -> DataWrapper(t, null) }
            .onErrorReturn { t -> DataWrapper(null, t) }
            .map {
                if (it.responseBody != null) {
                    when (it.responseBody.responseCode) {
                        0, 2, 3, 11 -> return@map DataWrapper(it.responseBody, ServerException(it.responseBody.message, it.responseBody.responseCode))
                    }
                }
                return@map it
            }
    }

}