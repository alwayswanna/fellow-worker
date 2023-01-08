/*
 * Copyright (c) 1-1/9/23, 12:45 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageService(private val messageSource: MessageSource) {

    fun getMessage(key: String, vararg params: Any?): String {
        return messageSource.getMessage(key, params, Locale("ru"))
    }

}