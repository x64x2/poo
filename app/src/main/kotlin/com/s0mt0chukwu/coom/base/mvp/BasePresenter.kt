package com.s0mt0chukwu.coom.base.mvp

interface BasePresenter {
    fun attachView(view: BaseView<*>)
}