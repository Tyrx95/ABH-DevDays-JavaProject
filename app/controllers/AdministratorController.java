package controllers;

import models.tables.RestaurantPhoto;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import services.AdministratorService;

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
		String restaurantId = request().body().asMultipartFormData().asFormUrlEncoded().get("restaurantId")[0];
		String imageFor = request().body().asMultipartFormData().asFormUrlEncoded().get("imageType")[0];
		String[] timestamp =request().body().asMultipartFormData().asFormUrlEncoded().get("timestamp");
		final String basePath = "public/assets/images/";
		String pathString;
		if(imageFor.equals("gallery") && timestamp!=null){
		    pathString = basePath + restaurantId+"-"+timestamp[0]+".";
			System.out.println("pathString : "+pathString);
        }
        else{
			pathString=basePath+restaurantId+"-"
					+imageFor+".";
		}
		if (picture != null) {
			String[] fileNameSplit = picture.getFilename().split("\\.");
			String fileExt = fileNameSplit[fileNameSplit.length-1];
			pathString+=fileExt;
			File file = picture.getFile();
			try {
				Files.move(Paths.get(file.getAbsolutePath()), Paths.get(pathString), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
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


}
