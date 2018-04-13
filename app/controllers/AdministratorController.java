package controllers;

import models.tables.RestaurantPhoto;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import services.AdministratorService;
import utils.FileNameUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * The type Administrator controller.
 */
public class AdministratorController extends BaseController {

	private AdministratorService service;
	private static final String IMAGE_ASSETS_DIRECTORY = "public/assets/images/";

	/**
	 * Sets service.
	 *
	 * @param service the service
	 */
	@Inject
	public void setService(final AdministratorService service) {
		this.service = service;
	}


	@Transactional
	public Result fileUpload() {
		Http.MultipartFormData<File> body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart<File> picture = body.getFile("file");
		Map<String , String[]> formData = request().body().asMultipartFormData().asFormUrlEncoded();
		String restaurantId = formData.get("restaurantId")[0];
		String imageFor = formData.get("imageType")[0];
		String[] timestamp = formData.get("timestamp");
		String pathString;
		if(imageFor.equals("gallery") && timestamp != null){
			pathString = IMAGE_ASSETS_DIRECTORY + restaurantId + "-" + timestamp[0] + ".";
		}
		else{
			pathString = IMAGE_ASSETS_DIRECTORY + restaurantId + "-" + imageFor + ".";
		}
		if (picture != null) {
			pathString+= FileNameUtils.getExtension(picture.getFilename());
			File file = picture.getFile();
			try {
				Files.move(Paths.get(file.getAbsolutePath()), Paths.get(pathString), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				flash("error", "File not saved successfully");
			}
			return ok("File uploaded");
		} else {
			flash("error", "Missing file");
			return badRequest();
		}
	}

	@Transactional
	public Result deletePicture(String id) {
		return wrapForAdmin(() -> this.service.deletePicture(UUID.fromString(id)));
	}

	@Transactional
	public Result getAdministratorStatistics() {
		return wrapForAdmin(() -> this.service.getAdministratorStatistics());
	}

	@Transactional
	public Result getAllActivityLogs() {
        return wrapForAdmin(() -> this.service.getAllActivityLogs());
    }


}
