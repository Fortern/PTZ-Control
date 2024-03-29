package xyz.fortern.controller

import org.apache.commons.io.IOUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.fortern.api.CameraFeign
import xyz.fortern.pojo.SpringResponse
import xyz.fortern.service.OnvifControlService
import xyz.fortern.util.presetToXyz
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@RestController
class CameraOperateController(
	private val onvifControlService: OnvifControlService,
	private val cameraFeign: CameraFeign,
) {
	@PostMapping("/getInfo/{id}")
	fun cameraInfo(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getOnvifInfo(camera)
	}
	
	@PostMapping("/profiles/{id}")
	fun cameraProfiles(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getOnvifMediaProfiles(camera)
	}
	
	@PostMapping("/configurations/{id}")
	@ResponseBody
	fun cameraConfigurations(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getConfigurationList(camera)
	}
	
	@PostMapping("/services/{id}")
	@ResponseBody
	fun onvifServices(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getService(camera)
	}
	
	@PostMapping("/ptz-info/{id}")
	@ResponseBody
	fun onvifStatus(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getPtzInfo(camera)
	}
	
	@ResponseBody
	@PostMapping("/ptz/{id}")
	fun cameraPtzControl(@PathVariable id: Int, p: Double, t: Double, z: Double): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		val presetToXyz = presetToXyz(p, t, z)
		onvifControlService.ptzAbsoluteMove(camera, presetToXyz[0], presetToXyz[1], presetToXyz[2])
		return null
	}
	
	@ResponseBody
	@PostMapping("/presets/{id}")
	fun onvifPresetList(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getPresetList(camera)
	}
	
	@ResponseBody
	@PostMapping("/toPreset/{id}")
	fun onvifToPreset(@PathVariable id: Int, token: String): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.gotoPreset(camera, token)
	}
	
	@ResponseBody
	@PostMapping("/preset-add/{id}")
	fun onvifAddPreset(@PathVariable id: Int): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getPresetList(camera)
	}
	
	@ResponseBody
	@PostMapping("/capture/{id}/{preset}")
	@Throws(IOException::class)
	fun capture(@PathVariable id: Int, @PathVariable preset: Int, response: HttpServletResponse): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		IOUtils.copy(onvifControlService.capture(camera), response.outputStream)
		return null
	}
	
	@ResponseBody
	@PostMapping("/live/{id}")
	fun liveUri(@PathVariable id: Int, response: HttpServletResponse): Any? {
		val camera = cameraFeign.getById(id) ?: return SpringResponse(HttpStatus.NOT_FOUND.value())
		return onvifControlService.getVideoUri(camera)
	}
	
}
