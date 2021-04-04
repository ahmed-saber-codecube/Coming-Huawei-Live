package com.coming.customer.di.module

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.coming.customer.BuildConfig
import com.coming.customer.core.AESCryptoInterceptor
import com.coming.customer.core.AppSession
import com.coming.customer.core.Session
import com.coming.customer.data.URLFactory
import com.coming.customer.data.datasource.UserLiveDataSource
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.data.service.AuthenticationService
import com.google.gson.Gson
import com.throdle.exception.AuthenticationException
import com.throdle.exception.ServerError
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by hlink21 on 9/5/16.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Named("cache")
    internal fun provideCacheDir(application: Application): File {
        return application.cacheDir
    }

    @Provides
    @Singleton
    internal fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Provides
    @Singleton
    internal fun provideCurrentLocale(resources: Resources): Locale {

        val locale: Locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = resources.configuration.locales.get(0)
        } else {
            locale = resources.configuration.locale
        }

        return locale
    }

    @Provides
    @Singleton
    internal fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun getSession(session: AppSession): Session = session

    @Provides
    @Singleton
    @Named("api-key")
    internal fun getApiKey(): String = Session.API_KEY

    @Provides
    @Singleton
    internal fun gson() = Gson()

    @Provides
    @Singleton
    @Named("aes-key")
    internal fun provideAESKey(): String {
        return "sDiKCxnSRBACfb0f864hYf75tG0pn7tU"
    }

    @Singleton
    @Provides
    internal fun provideClient(
        @Named("header") headerInterceptor: Interceptor,
        @Named("pre_validation") networkInterceptor: Interceptor
        // @Named("aes") aesInterceptor: Interceptor
    ): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor).apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(httpLoggingInterceptor)
                }
            }//            .addInterceptor(aesInterceptor)
//            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .connectTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(URLFactory.provideHttpUrl())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    @Named("header")
    internal fun provideHeaderInterceptor(session: Session): Interceptor {
        return Interceptor { chain ->
            val build = if (session.userSession.isNullOrEmpty()) {
                chain.request().newBuilder()
                    .addHeader(Session.API_KEY, Session.API_KEY_VALUE)
                    .addHeader(Session.ACCEPT_LANGUAGE, session.language)
                    .addHeader(Session.APP, Session.APP_VALUE)
                    .build()

            } else {
                chain.request().newBuilder()
                    .addHeader(Session.API_KEY, Session.API_KEY_VALUE)
                    .addHeader(Session.AUTHORIZATION, "Bearer ".plus(session.userSession))
                    //  .addHeader(Session.AUTHORIZATION, "".plus(session.userSession))
                    .addHeader(Session.ACCEPT_LANGUAGE, session.language)
                    .addHeader(Session.APP, Session.APP_VALUE)
                    .build()

            }
            chain.proceed(build)
        }
    }

    @Provides
    @Singleton
    @Named("pre_validation")
    internal fun provideNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val code = response.code
            if (code >= 500)
                throw ServerError("Unknown server error", response.body!!.string())
            else if (code == 401 || code == 403)
                throw AuthenticationException("Authentication Exception")
            response
        }
    }

    @Provides
    @Singleton
    @Named("aes")
    internal fun provideAesCryptoInterceptor(aesCryptoInterceptor: AESCryptoInterceptor): Interceptor {
        return aesCryptoInterceptor
    }

    @Provides
    @Singleton
    fun provideCustomerRepository(customerDataSource: UserLiveDataSource): UserRepository {
        return customerDataSource
    }

    @Provides
    @Singleton
    fun provideAuthenticationService(retrofit: Retrofit): AuthenticationService {
        return retrofit.create(AuthenticationService::class.java)
    }
}
