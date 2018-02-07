package com.yourcompany;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class CustomSort {

	static class SortByScoreAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if((o1.getScore() != null || !o1.getScore().equals("None")) && (o2.getScore() != null || !o2.getScore().equals("None"))) {
				return Double.compare(Double.parseDouble(o1.getScore()), Double.parseDouble(o2.getScore()));
			}else {
				return 0;
			}
		}

		
	}
	
	static class SortByScoreDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if((o1.getScore() != null || !o1.getScore().equals("None")) && (o2.getScore() != null || !o2.getScore().equals("None"))) {
				return Double.compare(Double.parseDouble(o2.getScore()), Double.parseDouble(o1.getScore()));
			}else {
				return 0;
			}
		}

		
	}
	
	
	static class SortByReviewsAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getReviews() != null && o2.getReviews() != null) {
				if(!o1.getReviews().equals("Unknown") && !o2.getReviews().equals("Unknown")) {
					return Integer.parseInt(o1.getReviews().replaceAll(",", "").replaceAll(" ", "")) - Integer.parseInt(o2.getReviews().replaceAll(",", "").replaceAll(" ", ""));
				}else {
					if(o1.getReviews().equals("Unknown") && o2.getReviews().equals("Unknown")) {
						return 0;
					}else if(o1.getReviews().equals("Unknown")) {
						return -1;
					}else {
						return 1;
					}
				}
			}
			return 0;
		}

		
	}
	
	static class SortByReviewsDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			if(o1.getReviews() != null && o2.getReviews() != null) {
				if(!o1.getReviews().equals("Unknown") && !o2.getReviews().equals("Unknown")) {
					return Integer.parseInt(o2.getReviews().replaceAll(",", "").replaceAll(" ", "")) - Integer.parseInt(o1.getReviews().replaceAll(",", "").replaceAll(" ", ""));
				}else {
					if(o1.getReviews().equals("Unknown") && o2.getReviews().equals("Unknown")) {
						return 0;
					}else if(o1.getReviews().equals("Unknown")) {
						return 1;
					}else {
						return -1;
					}
				}
			}
			return 0;
		}

		
	}
	
	
	static class SortByPriceAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getPrice() != null && o2.getPrice() != null) {
				if(!o1.getPrice().equals("FREE") && !o2.getPrice().equals("FREE")) {
					return Double.compare(Double.parseDouble(o1.getPrice().replaceAll("£", "")), Double.parseDouble(o2.getPrice().replaceAll("£", "")));
				}else {
					return 0;
				}
			}else {
				return 0;
			}
		}

		
	}
	
	static class SortByPriceDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getPrice() != null && o2.getPrice() != null) {
				if(!o1.getPrice().equals("FREE") && !o2.getPrice().equals("FREE")) {
					return Double.compare(Double.parseDouble(o2.getPrice().replaceAll("£", "")), Double.parseDouble(o1.getPrice().replaceAll("£", "")));
				}else {
					return 0;
				}	
			}else {
				return 0;
			}
		}

		
	}
	
	
	static class SortByLowerDownloadsAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getInstalls() != null && o2.getInstalls() != null) {
				return Integer.parseInt(o1.getInstalls().split("-")[0].replaceAll(",", "").trim()) - Integer.parseInt(o2.getInstalls().split("-")[0].replaceAll(",", "").trim());
		
			}
			return 0;
		}
		
		
	}
	
	
	static class SortByLowerDownloadsDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getInstalls() != null && o2.getInstalls() != null) {
				return Integer.parseInt(o2.getInstalls().split("-")[0].replaceAll(",", "").trim()) - Integer.parseInt(o1.getInstalls().split("-")[0].replaceAll(",", "").trim());
		
			}
			return 0;
		}
		
		
	}
	
	
	static class SortByUpperDownloadsAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getInstalls() != null && o2.getInstalls() != null) {
				return Integer.parseInt(o1.getInstalls().split("-")[1].replaceAll(",", "").trim()) - Integer.parseInt(o2.getInstalls().split("-")[1].replaceAll(",", "").trim());
		
			}
			return 0;
		}
		
		
	}
	
	static class SortByUpperDownloadsDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getInstalls() != null && o2.getInstalls() != null) {
				return Integer.parseInt(o2.getInstalls().split("-")[1].replaceAll(",", "").trim()) - Integer.parseInt(o1.getInstalls().split("-")[1].replaceAll(",", "").trim());
		
			}
			return 0;
		}
		
		
	}
	
	
	static class SortByLastUpdatedAsc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getLastUpdate() != null && o2.getLastUpdate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
				Date date1 = null, date2 = null;
				try {
					date1 = sdf.parse(o1.getLastUpdate());
					date2 = sdf.parse(o2.getLastUpdate());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(date1 !=null && date2 !=null ) {
					return date1.compareTo(date2);
				}
				
			}
			return 0;
		}
		
		
	}
	
	
	static class SortByLastUpdatedDesc implements Comparator<App>{

		@Override
		public int compare(App o1, App o2) {
			// TODO Auto-generated method stub
			if(o1.getLastUpdate() != null && o2.getLastUpdate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
				Date date1 = null, date2 = null;
				try {
					date1 = sdf.parse(o1.getLastUpdate());
					date2 = sdf.parse(o2.getLastUpdate());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(date1 !=null && date2 !=null ) {
					return date2.compareTo(date1);
				}
				
			}
			return 0;
		}
		
		
	}
	
}
