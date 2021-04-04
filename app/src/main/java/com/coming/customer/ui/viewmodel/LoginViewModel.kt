package com.coming.customer.ui.viewmodel

import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.User
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.ui.base.APILiveData
import com.coming.customer.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {

    val loginLiveData = APILiveData<User>()

    fun login(phoneNumber: ApiRequestParams) {
        userRepository.login(phoneNumber).subscribe(withLiveData(loginLiveData))
    }
}