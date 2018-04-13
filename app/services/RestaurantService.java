package services;

import models.helpers.PaginationAdapter;
import models.helpers.PopularLocation;
import models.helpers.PopularRestaurantsBean;
import models.helpers.RestaurantFilter;
import models.helpers.forms.ImageUploadForm;
import models.helpers.forms.ReviewForm;
import models.tables.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Restaurant service.
 */
@Singleton
public class RestaurantService extends BaseService {

	private static final String BASE_PATH = "http://localhost:9000/assets/images/";

	@Inject
	private RestaurantService() { }

	/**
	 * Create restaurant boolean.
	 *
	 * @param restaurant the restaurant
	 * @throws Exception the exception
	 */
	public Boolean createRestaurant(final Restaurant restaurant) throws Exception {
		getSession().save(restaurant);
		log("The restaurant by name:" + restaurant.getName() + " has been created by admin." );
		return true;
	}

	/**
	 * Edit restaurant boolean.
	 *
	 * @param restaurant the restaurant
	 * @throws Exception the exception
	 */
	public Boolean editRestaurant(final Restaurant restaurant) throws Exception {
		getSession().merge(restaurant);
		log("The restaurant by name:" + restaurant.getName() + " has been edited by admin." );
		return true;
	}

	/**
	 * Delete restaurant boolean.
	 *
	 * @param id the id
	 * @throws Exception the exception
	 */
	public Boolean deleteRestaurant(final UUID id) throws Exception {
		Restaurant restaurant = (Restaurant) getSession().createCriteria(Restaurant.class)
				.add(Restrictions.eq("id", id))
				.uniqueResult();

		getSession().delete(restaurant);
		log("The restaurant by name:" + restaurant.getName() + " has been deleted by admin." );
		return true;
	}

	/**
	 * Find restaurants with filter pagination adapter.
	 *
	 * @param restaurantFilter the restaurant filter
	 * @return the pagination adapter
	 */
	@SuppressWarnings("unchecked")
	public PaginationAdapter<Restaurant> findRestaurantsWithFilter(final RestaurantFilter restaurantFilter) {
		Criteria criteria = getSession().createCriteria(Restaurant.class);

		if (restaurantFilter.name != null) {
			criteria.add(Restrictions.ilike("name", restaurantFilter.name, MatchMode.ANYWHERE));
		}

		if (restaurantFilter.cuisine != null && !restaurantFilter.cuisine.isEmpty() ) {
			Criteria cuisineCriteria = criteria.createCriteria("cuisines");
			Disjunction disjunction = Restrictions.disjunction();
			for(String singleCuisine : restaurantFilter.cuisine.split(",")){
                disjunction.add(Restrictions.eq("name", singleCuisine));
            }
			cuisineCriteria.add(disjunction);

		}

		if (restaurantFilter.cityId != null) {
			criteria.add(Restrictions.eq("city.id", restaurantFilter.cityId));
		}

		if (restaurantFilter.price != null && restaurantFilter.price != 0) {
			criteria.add(Restrictions.eq("priceRange", restaurantFilter.price));
		}

		if (restaurantFilter.rating != null && restaurantFilter.rating != 0){
			criteria.add(Restrictions.eq("starRating", restaurantFilter.rating));
		}

		Long numberOfPages = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()) / restaurantFilter.pageSize;

		criteria.setProjection(null)
				.setFirstResult((restaurantFilter.pageNumber - 1) * restaurantFilter.pageSize)
				.setMaxResults(restaurantFilter.pageSize);


		if (restaurantFilter.sortBy.equals("price")) {
			criteria.addOrder(Order.desc("priceRange"));
		}

		criteria.addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Restaurant> restaurants = criteria.list();

		switch (restaurantFilter.sortBy) {
			case "rating":
				restaurants.sort((o1, o2) -> o2.getAverageRating().compareTo(o1.getAverageRating()));
				break;
		}

		return PaginationAdapter.createOutput()
				.setPageNumber(restaurantFilter.pageNumber)
				.setPageSize(restaurantFilter.pageSize)
				.setModel(restaurants)
				.setNumberOfPages(numberOfPages);
	}

	/**
	 * Gets restaurant with id.
	 *
	 * @param id the id
	 * @return the restaurant with id
	 */
	public Restaurant getRestaurantWithId(final UUID id) {
		return (Restaurant) getSession().createCriteria(Restaurant.class)
				.add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	/**
	 * Gets nearby restaurants.
	 *
	 * @param latitude  the latitude
	 * @param longitude the longitude
	 * @return the nearby restaurants
	 */
	@SuppressWarnings("unchecked")
	public List<Restaurant> getNearbyRestaurants(final Float latitude, final Float longitude) {
		return getSession()
				.createSQLQuery("SELECT * FROM restaurant WHERE restaurant.longitude <> 0 AND restaurant.latitude <> 0 ORDER BY ST_Distance(ST_GeomFromText('POINT(' || restaurant.longitude || ' ' || restaurant.latitude || ')' ,4326), ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')',4326)) ASC LIMIT 3")
				.addEntity(Restaurant.class)
				.setParameter("longitude", longitude)
				.setParameter("latitude", latitude)
				.list();
	}

	/**
	 * Gets popular restaurants.
	 *
	 * @return the popular restaurants
	 */
	@SuppressWarnings("unchecked")
	public List<Restaurant> getPopularRestaurants() {

		List<PopularRestaurantsBean> popularRestaurantsBeans = getSession().createCriteria(Reservation.class, "reservation")
				.createAlias("reservation.table", "table")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("table.restaurantId").as("restaurantId"))
						.add(Projections.count("table").as("tableCount")))
				.addOrder(Order.asc("tableCount"))
				.setResultTransformer(Transformers.aliasToBean(PopularRestaurantsBean.class))
				.setMaxResults(6)
				.list();

		if (popularRestaurantsBeans.size() > 0) {
			List<UUID> popularRestaurantsIds = popularRestaurantsBeans.stream().map(PopularRestaurantsBean::getRestaurantId).collect(Collectors.toList());

			return (List<Restaurant>) getSession().createCriteria(Restaurant.class)
					.add(Restrictions.in("id", popularRestaurantsIds))
					.addOrder(Order.asc("name"))
					.list();
		}

		return new ArrayList<>();
	}

	/**
	 * Gets popular locations.
	 *
	 * @return the popular locations
	 */
	@SuppressWarnings("unchecked")
	public List<PopularLocation> getPopularLocations() {
		List<Object[]> popularLocations = getSession().createCriteria(Restaurant.class)
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("city"))
						.add(Projections.count("id").as("numberOfRestaurants")))
				.addOrder(Order.desc("numberOfRestaurants"))
				.list();

		return popularLocations.stream().map(PopularLocation::new).collect(Collectors.toList());
	}

	/**
	 * Post review boolean.
	 *
	 * @param reviewForm the review form
	 * @param user       the user
	 */
	public Boolean postReview(final ReviewForm reviewForm, final User user) {
		RestaurantReview restaurantReview = (RestaurantReview) getSession().createCriteria(RestaurantReview.class)
				.add(Restrictions.eq("restaurantId", reviewForm.getRestaurantId()))
				.add(Restrictions.eq("userId", user.getId()))
				.uniqueResult();
		if (restaurantReview == null) {
			restaurantReview = new RestaurantReview(
					reviewForm.getRestaurantId(),
					user.getId(),
					reviewForm.getReviewScore(),
					reviewForm.getReviewText()
			);
		} else {
			restaurantReview.setReview(reviewForm.getReviewText());
			restaurantReview.setRating(reviewForm.getReviewScore());
		}

		getSession().save(restaurantReview);
		updateStarRating(reviewForm.getRestaurantId());
		String userName = user.getName();
		String restaurantName = getRestaurantWithId(reviewForm.getRestaurantId()).getName();
		Integer reviewScore = reviewForm.getReviewScore();
		String reviewText = reviewForm.getReviewText();
		log("The user: " + userName + " has posted a review for a restaurant:" +
				restaurantName + " rating it with: " +
				reviewScore + " and the following text: \n" + reviewText);
		return true;
	}

	private void updateStarRating(final UUID restaurantId) {
		Restaurant restaurant = getRestaurantWithId(restaurantId);
		int starRating;
		if(restaurant.getAverageRating() >= 4.75){
			starRating = 5;
		}
		else if(restaurant.getAverageRating() >= 4){
			starRating = 4;
		}
		else if(restaurant.getAverageRating() >= 3){
			starRating = 3;
		}
		else if(restaurant.getAverageRating() >= 2){
			starRating = 2;
		}
		else if(restaurant.getAverageRating() >= 0.25){
			starRating = 1;
		}
		else{
			starRating = 0;
		}
		restaurant.setStarRating(starRating);
		getSession().save(restaurant);
	}


	/**
	 * Gets number of restaurants.
	 *
	 * @return the number of restaurants
	 */
	public Long getNumberOfRestaurants() {
		return Long.valueOf(getSession().createCriteria(Restaurant.class)
				.setProjection(Projections.rowCount())
				.uniqueResult().toString());
	}

	/**
	 * Update picture string.
	 *
	 * @param imageUploadForm the image upload form
	 * @return the string
	 * @throws Exception the exception
	 */
	public String updatePicture(final ImageUploadForm imageUploadForm) throws Exception {
		Restaurant restaurant = (Restaurant) getSession().createCriteria(Restaurant.class)
				.add(Restrictions.eq("id", imageUploadForm.getRestaurantId()))
				.uniqueResult();

		String newImagePath = BASE_PATH + imageUploadForm.getRestaurantId() + "-";
		if (imageUploadForm.getImageType().equals("profile")) {
			restaurant.setProfileImagePath(newImagePath);
			newImagePath+=imageUploadForm.getImageType() + "." + imageUploadForm.getExtension();
		} else if (imageUploadForm.getImageType().equals("cover")){
			restaurant.setCoverImagePath(newImagePath);
			newImagePath+=imageUploadForm.getImageType() + "." + imageUploadForm.getExtension();
		}
		else {
			newImagePath+=imageUploadForm.getTimestamp()+ "." + imageUploadForm.getExtension();;
			RestaurantPhoto newPhoto = new RestaurantPhoto();
			newPhoto.setRestaurantId(imageUploadForm.getRestaurantId());
			newPhoto.setPath(newImagePath);
			getSession().persist(newPhoto);

			return "{ \"id\": \"" + newPhoto.getId()
					+ "\", \"restaurantId\": \"" + newPhoto.getRestaurantId() +"\", \"path\": \"" + newPhoto.getPath() + "\"}";
		}

		getSession().update(restaurant);
		return "{ \"imageFor\": \"" + imageUploadForm.getImageType() + "\", \"url\": \"" + newImagePath + "\"}";
	}
}
