package org.retriever.dailypet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.retriever.dailypet.data.network.diary.DiaryApiInterface
import org.retriever.dailypet.data.network.home.HomeApiService
import org.retriever.dailypet.data.network.login.LoginApiService
import org.retriever.dailypet.data.network.mypage.MyPageApiService
import org.retriever.dailypet.data.network.image.ImageApiService
import org.retriever.dailypet.data.network.signup.GroupApiInterface
import org.retriever.dailypet.data.network.signup.FindGroupApiInterface
import org.retriever.dailypet.data.network.signup.PetApiService
import org.retriever.dailypet.data.network.signup.ProfileApiService
import org.retriever.dailypet.data.repository.diary.DiaryRepository
import org.retriever.dailypet.data.repository.home.HomeRepository
import org.retriever.dailypet.data.repository.login.LoginRepository
import org.retriever.dailypet.data.repository.mypage.MyPageRepository
import org.retriever.dailypet.data.repository.image.ImageRepository
import org.retriever.dailypet.data.repository.signup.GroupRepository
import org.retriever.dailypet.data.repository.signup.FindGroupRepository
import org.retriever.dailypet.data.repository.signup.PetRepository
import org.retriever.dailypet.data.repository.signup.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository(loginApiService: LoginApiService): LoginRepository = LoginRepository(loginApiService)

    @Provides
    @Singleton
    fun provideProfileRepository(profileApiService: ProfileApiService): ProfileRepository = ProfileRepository(profileApiService)

    @Provides
    @Singleton
    fun provideFamilyRepository(groupApiInterface: GroupApiInterface): GroupRepository = GroupRepository(groupApiInterface)

    @Provides
    @Singleton
    fun providePetRepository(petApiService: PetApiService): PetRepository = PetRepository(petApiService)

    @Provides
    @Singleton
    fun provideMyPageRepository(myPageApiService: MyPageApiService): MyPageRepository = MyPageRepository(myPageApiService)

    @Provides
    @Singleton
    fun provideFindGroupRepository(findGroupApiInterface: FindGroupApiInterface): FindGroupRepository = FindGroupRepository(findGroupApiInterface)

    @Provides
    @Singleton
    fun provideDiaryRepository(diaryApiInterface: DiaryApiInterface): DiaryRepository = DiaryRepository(diaryApiInterface)

    @Provides
    @Singleton
    fun provideHomeRepository(homeApiService: HomeApiService): HomeRepository = HomeRepository(homeApiService)

    @Provides
    @Singleton
    fun provideImageRepository(imageApiService: ImageApiService): ImageRepository = ImageRepository(imageApiService)

}