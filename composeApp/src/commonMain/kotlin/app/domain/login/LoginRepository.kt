package app.domain.login

import app.data.login.LoginApi
import app.data.user.GetUserApi

class LoginRepository(
    val loginApi: LoginApi,
    val getUserApi: GetUserApi
) {

}