package com.kominfotabalong.simasganteng.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.data.repository.UserDataStoreRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltWorker
class LogoutWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val repository: ApiRepository,
    private val userRepo: UserDataStoreRepository,
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var gson: Gson

    companion object {
        const val FCM_TOKEN = "FCM_TOKEN"
    }

    override suspend fun doWork(): Result {
        var userToken = ""
        val fcmToken = inputData.getString(FCM_TOKEN) ?: ""
        userRepo.getLoggedUser().first {
            println("workerData = $it")
            val userStatePrefs = gson.fromJson(it, LoginResponse::class.java)
            userStatePrefs?.let {
                userToken = it.token
            }
            if (fcmToken == "") userRepo.saveUserData(null)
            true
        }
        try {
            repository.postFCMToken(userToken, fcmToken).catch {
                Throwable("Flow Error")
            }.collect { data ->
                when (data) {
                    is NetworkResponse.Success -> {
                        Timber.tag("workerSuccess").d(data.body.body.message)
                    }

                    is NetworkResponse.ServerError -> {
                        Throwable(
                            data.body?.message
                                ?: "Terjadi kesalahan saat memproses data"
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        Throwable("Terjadi kesalahan network")
                    }

                    is NetworkResponse.UnknownError -> {
                        Throwable(data.error.localizedMessage ?: "Unknown Error")
                    }
                }
            }
            return Result.success()
        } catch (throwable: Throwable) {
            Timber.tag("WorkerFailure").e(throwable)
            return Result.failure()
        }
    }

}