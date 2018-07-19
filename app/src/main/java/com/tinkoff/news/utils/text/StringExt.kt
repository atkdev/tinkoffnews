package com.tinkoff.news.utils.text

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.fromHtml(): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(this)
        }