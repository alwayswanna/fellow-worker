/*
 * Copyright (c) 1-1/9/23, 11:01 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class MessageService(private val messageSource: MessageSource) {

    fun getMessage(key: String, vararg params: Any?): String {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale())
    }

}