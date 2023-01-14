/*
 * Copyright (c) 1-1/14/23, 7:48 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.exception

import org.springframework.http.HttpStatusCode
import org.springframework.web.server.ResponseStatusException

class FileException(status: HttpStatusCode, reason: String?) : ResponseStatusException(status, reason)