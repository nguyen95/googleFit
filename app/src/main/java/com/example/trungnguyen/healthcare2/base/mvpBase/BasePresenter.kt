package com.example.trungnguyen.healthcare2.base.mvpBase

/**
 * Created by Trung Nguyen on 22-Aug-18.
 */
open class BasePresenter<T : MvpView> : Presenter<T> {

    private var mvpView: T? = null

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
    }

    fun getMvpView() : T? {
        return mvpView
    }
}