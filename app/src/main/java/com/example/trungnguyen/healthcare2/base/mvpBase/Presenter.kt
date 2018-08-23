package com.example.trungnguyen.healthcare2.base.mvpBase

/**
 * Created by Trung Nguyen on 22-Aug-18.
 */
interface Presenter<T : MvpView> {
    fun attachView(mvpView: T)

    fun detachView()
}