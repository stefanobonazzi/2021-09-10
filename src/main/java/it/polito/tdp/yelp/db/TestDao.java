package it.polito.tdp.yelp.db;

public class TestDao {

	public static void main(String[] args) {
		YelpDao dao = new YelpDao();
		System.out.println(String.format("Users: %d\nBusiness: %d\nReviews: %d\n", 
				dao.getAllUsers().size(), dao.getAllBusiness().size(), dao.getAllReviews().size()));
		
	}

}
