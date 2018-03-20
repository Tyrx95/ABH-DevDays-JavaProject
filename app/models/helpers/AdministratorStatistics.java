package models.helpers;

/**
 * The type Administrator statistics.
 */
public class AdministratorStatistics {


	private Long numberOfRestaurants;
	private Long numberOfUsers;
	private Long numberOfLocations;
	private Long numberOfCuisines;

	private AdministratorStatistics() {}

	public static AdministratorStatistics createAdminStatistics()  { return  new AdministratorStatistics(); }

	/**
	 *
	 * @return the number of restaurants
	 */
	public Long getNumberOfRestaurants() {
		return numberOfRestaurants;
	}

	/**
	 *
	 * @param numberOfRestaurants the number of Restaurants
	 * @return The number of restaurants
	 */
	public AdministratorStatistics setNumberOfRestaurants(Long numberOfRestaurants) {
		this.numberOfRestaurants = numberOfRestaurants;
		return this;
	}

	/**
	 *
	 * @return the number of Users
	 */
	public Long getNumberOfUsers() {
		return numberOfUsers;
	}

	/**
	 *
	 * @param numberOfUsers the number of Users
	 * @return the number of users
	 */
	public AdministratorStatistics setNumberOfUsers(Long numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
		return this;
	}

	/**
	 *
	 * @return the number of locations
	 */
	public Long getNumberOfLocations() {
		return numberOfLocations;
	}

	/**
	 *
	 * @param numberOfLocations
	 * @return the number of locations
	 */
	public AdministratorStatistics setNumberOfLocations(Long numberOfLocations) {
		this.numberOfLocations = numberOfLocations;
		return this;
	}

	/**
	 *
	 * @return the number of Cuisines
	 */
	public Long getNumberOfCuisines() {
		return numberOfCuisines;
	}

	/**
	 * @param numberOfCuisines
	 * @return the numberOfCuisines
	 */
	public AdministratorStatistics setNumberOfCuisines(Long numberOfCuisines) {
		this.numberOfCuisines = numberOfCuisines;
		return this;
	}
}
