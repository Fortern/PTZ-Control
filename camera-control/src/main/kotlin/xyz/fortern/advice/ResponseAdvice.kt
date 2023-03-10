package xyz.fortern.advice

import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import xyz.fortern.pojo.SpringResponse
import java.util.*

@Component
@ControllerAdvice("xyz.fortern.controller")
class ResponseAdvice : ResponseBodyAdvice<Any> {
	override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
		return true
	}
	
	override fun beforeBodyWrite(
		body: Any?,
		returnType: MethodParameter,
		selectedContentType: MediaType,
		selectedConverterType: Class<out HttpMessageConverter<*>>,
		request: ServerHttpRequest,
		response: ServerHttpResponse,
	): Any? {
		body ?: return null
		if (body !is SpringResponse) return body
		
		body.timestamp = Date()
		if (body.error == null) {
			val httpStatus = HttpStatus.resolve(body.status)
			if (httpStatus == null) {
				body.error = "Other error."
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
			} else {
				body.error = httpStatus.reasonPhrase
				response.setStatusCode(httpStatus)
			}
		}
		
		return body
	}
}
