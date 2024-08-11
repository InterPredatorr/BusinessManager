@file:JvmName("AppModuleKtJvm")

package app.di

import app.data.login.LoginApi
import app.data.login.LoginApiFirebaseImpl
import app.data.rooms.RoomProviderC1Api
import app.data.rooms.RoomsProviderApi
import app.data.user.GetUserApi
import app.data.user.GetUserFirebaseApi
import app.data.user.UserHistoryApi
import app.data.user.UserHistoryFirebaseApi
import app.data.work.CurrentWorkFirebaseApi
import app.data.work.CurrentWorksApi
import app.data.work.WorkAssignApi
import app.data.work.WorkAssignFirebaseApi
import app.domain.login.LoginUseCase
import app.domain.login.LoginUseCaseImpl
import app.domain.rooms.RoomsProviderCombineUseCase
import app.domain.rooms.RoomsProviderUseCase
import app.domain.user.UserMapper
import app.domain.user.UserProviderUseCase
import app.domain.user.UserProviderUseCaseImpl
import app.domain.user.WorkerHistoryUseCase
import app.domain.user.WorkerHistoryUseCaseImpl
import app.domain.user.WorksUseCase
import app.presentation.addworker.AddWorkerViewModel
import app.presentation.login.LoginViewModel
import app.presentation.rooms.RoomViewModel
import app.presentation.splash.SplashViewModel
import app.presentation.usersList.UsersListViewModel
import app.presentation.usersList.WorkerHistoryViewModel
import app.presentation.worker.WorkerScreenViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.jvm.JvmName

private val commonAppModule = module {
    factory<GetUserApi>(named(firebaseApiKey)) {
        GetUserFirebaseApi()
    }

    factory<UserProviderUseCase> {
        UserProviderUseCaseImpl(
            cacheService = get(named(userCacheServiceKey), parameters = { parametersOf("users") }),
            getUserApi = get(named(firebaseApiKey)))
    }

    single {
        UsersListViewModel(get())
    }

    single {
        LoginViewModel(loginUseCase = get(), userProviderUseCase = get())
    }

    single<WorkerScreenViewModel> { WorkerScreenViewModel(worksUseCase = get()) }

    factory { WorksUseCase(userProviderUseCase = get(), workAssignApi = get()) }

    factory<WorkAssignApi> { WorkAssignFirebaseApi() }

    factory<LoginApi> { LoginApiFirebaseImpl() }

    factory<LoginUseCase> { LoginUseCaseImpl(loginApi = get(), getUserApi = get(named(firebaseApiKey))) }

    single { SplashViewModel(userProviderUseCase = get()) }

    single { AddWorkerViewModel(loginUseCase = get()) }

    single { RoomViewModel(roomsProviderUseCase = get(), workAssignApi = get(), userProviderUseCase = get(), worksUseCase = get()) }

    factory<RoomsProviderUseCase> { RoomsProviderCombineUseCase(roomsProviderApi = get(), currentWorksApi = get(), userProviderUseCase = get()) }

    factory<RoomsProviderApi> { RoomProviderC1Api() }

    factory<CurrentWorksApi> { CurrentWorkFirebaseApi() }

    single {
        WorkerHistoryViewModel(historyUseCase = get())
    }

    factory<WorkerHistoryUseCase> {
        WorkerHistoryUseCaseImpl(userHistoryApi = get(), userMapper = get())
    }

    factory<UserHistoryApi> {
        UserHistoryFirebaseApi()
    }

    single<UserMapper> {
        UserMapper(userProviderUseCase = get())
    }
}

expect val platformAppModule: Module

val appModule: Module
    get() = module {
        this.includes(commonAppModule + platformAppModule)
    }


const val firebaseApiKey = "firebaseApi"
const val userCacheServiceKey = "userCacheService"
