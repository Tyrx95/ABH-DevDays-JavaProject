package services;

import models.tables.RestaurantPhoto;
import org.hibernate.criterion.Restrictions;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.UUID;


/**
 * The type Administrator service.
 */
@Singleton
public class AdministratorService extends BaseService {

	@Inject
	private AdministratorService() { }

	/**
	 * Delete picture
	 *
	 * @param id the id
	 *
	 */
	public boolean deletePicture(final UUID id) throws Exception {
		RestaurantPhoto restaurantPhoto = (RestaurantPhoto) getSession().createCriteria(RestaurantPhoto.class)
				.add(Restrictions.eq("id", id))
				.uniqueResult();
		getSession().delete(restaurantPhoto);
		String path = restaurantPhoto.getPath().replace("http://localhost:9000","");
		path = new StringBuilder(path).insert(0,"public").toString();
		Files.delete(Paths.get(path));
		return true;
	}



}
