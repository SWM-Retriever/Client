package org.retriever.dailypet.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.retriever.dailypet.BuildConfig.BASE_URL
import org.retriever.dailypet.data.network.diary.DiaryApiInterface
import org.retriever.dailypet.data.network.login.LoginApiService
import org.retriever.dailypet.data.network.home.HomeApiService
import org.retriever.dailypet.data.network.home.StatisticsApiService
import org.retriever.dailypet.data.network.mypage.MyPageApiService
import org.retriever.dailypet.data.network.image.ImageApiService
import org.retriever.dailypet.data.network.signup.GroupApiInterface
import org.retriever.dailypet.data.network.signup.FindGroupApiInterface
import org.retriever.dailypet.data.network.signup.PetApiService
import org.retriever.dailypet.data.network.signup.ProfileApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFamilyApiService(retrofit: Retrofit): GroupApiInterface {
        return retrofit.create(GroupApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun providePetApiService(retrofit: Retrofit): PetApiService {
        return retrofit.create(PetApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMyPageApiService(retrofit: Retrofit): MyPageApiService {
        return retrofit.create(MyPageApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFindGroupApiService(retrofit: Retrofit): FindGroupApiInterface {
        return retrofit.create(FindGroupApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideDiaryApiService(retrofit: Retrofit): DiaryApiInterface {
        return retrofit.create(DiaryApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStatisticsApiService(retrofit: Retrofit): StatisticsApiService {
        return retrofit.create(StatisticsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageApiService(retrofit: Retrofit): ImageApiService {
        return retrofit.create(ImageApiService::class.java)
    }

}