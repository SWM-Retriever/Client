package org.retriever.dailypet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.retriever.dailypet.data.network.login.LoginApiService
import org.retriever.dailypet.data.network.mypage.MyPageApiService
import org.retriever.dailypet.data.repository.login.LoginRepository
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository(loginApiService: LoginApiService) : LoginRepository
        = LoginRepository(loginApiService)

    @Provides
    @Singleton
    fun provideMyPageRepository(myPageApiService: MyPageApiService) : MyPageRepository
        = MyPageRepository(myPageApiService)

}