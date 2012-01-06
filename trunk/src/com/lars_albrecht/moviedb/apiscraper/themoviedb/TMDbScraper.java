/**
 * 
 */
package com.lars_albrecht.moviedb.apiscraper.themoviedb;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin;
import com.lars_albrecht.moviedb.apiscraper.themoviedb.model.TheMovieDBMovieModel;
import com.moviejukebox.themoviedb.TheMovieDb;
import com.moviejukebox.themoviedb.model.Country;
import com.moviejukebox.themoviedb.model.MovieDB;
import com.moviejukebox.themoviedb.model.Person;

/**
 * @author lalbrecht
 * 
 */
public class TMDbScraper implements IApiScraperPlugin {

	private final String apiKey = "d2bfb8abb70809759df091b8d23876af";
	private final String langKey = "de";

	private TheMovieDb tmdb = null;

	public TMDbScraper() {
		this.tmdb = new TheMovieDb(this.apiKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin#
	 * getMovieFromKey(java.lang.String)
	 */
	@Override
	public TheMovieDBMovieModel getMovieFromKey(final String key) {
		final MovieDB m = this.tmdb.moviedbGetInfo(key, this.langKey);
		if (m != null) {
			return this.returnInfosFromMovie(m);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin#
	 * getMovieFromString(java.lang.String, java.lang.Integer)
	 */
	@Override
	public TheMovieDBMovieModel getMovieFromStringYear(final String s, final Integer year) {
		final List<MovieDB> searchResults = this.tmdb.moviedbSearch(s, this.langKey);
		MovieDB m = null;
		if (searchResults.size() > 1) {
			m = this.findMovie(searchResults, s, (year != null ? Integer.toString(year) : null));
		} else if (searchResults.size() == 1) {
			m = searchResults.get(0);
		}
		System.out.println(m);
		if (m != null) {
			return this.returnInfosFromMovie(m);
		}
		return null;
	}

	/**
	 * Search a list of movies and return the one that matches the title & year
	 * 
	 * @param movieList
	 *            The list of movies to search
	 * @param title
	 *            The title to search for
	 * @param year
	 *            The year of the title to search for
	 * @return The matching movie
	 */
	private MovieDB findMovie(final Collection<MovieDB> movieList, final String title, final String year) {
		if ((movieList == null) || movieList.isEmpty()) {
			return null;
		}

		for (final MovieDB moviedb : movieList) {
			if (this.compareMovies(moviedb, title, year)) {
				return moviedb;
			}
		}

		return null;
	}

	/**
	 * Compare the MovieDB object with a title & year
	 * 
	 * @param moviedb
	 *            The moviedb object to compare too
	 * @param title
	 *            The title of the movie to compare
	 * @param year
	 *            The year of the movie to compare
	 * @return True if there is a match, False otherwise.
	 */
	private boolean compareMovies(final MovieDB moviedb, final String title, final String year) {
		if ((moviedb == null) || (!Helper.isValidString(title))) {
			return false;
		}

		if (Helper.isValidString(year)) {
			if (Helper.isValidString(moviedb.getReleaseDate())) {
				// Compare with year
				final String movieYear = moviedb.getReleaseDate().substring(0, 4);
				if (movieYear.equals(year)
						&& (moviedb.getTitle().equalsIgnoreCase(title) || moviedb.getOriginalName().equalsIgnoreCase(title) || moviedb.getAlternativeName().equalsIgnoreCase(title) || (moviedb
								.getTitle().contains("-") && moviedb.getTitle().split("-")[0].trim().equalsIgnoreCase(title)))) {
					return true;
				}
			}
		} else {
			// Compare without year
			if (moviedb.getTitle().equalsIgnoreCase(title) || moviedb.getOriginalName().equalsIgnoreCase(title) || moviedb.getAlternativeName().equalsIgnoreCase(title)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin#
	 * getPluginName()
	 */
	@Override
	public String getPluginName() {
		return "TMDb";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin#getTabTitle
	 * ()
	 */
	@Override
	public String getTabTitle() {
		return "The Movie DB - TMDb";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.moviedb.apiscraper.interfaces.IApiScraperPlugin#getVersion
	 * ()
	 */
	@Override
	public String getVersion() {
		return "1.0.0.0";
	}

	private TheMovieDBMovieModel returnInfosFromMovie(final MovieDB m) {
		ArrayList<String> tempList = new ArrayList<String>();
		final TheMovieDBMovieModel movie = new TheMovieDBMovieModel();
		try {
			// Helper.getFieldsFromClass(MovieDB.class);
			// FieldList fl =
			// Helper.getDBFieldModelFromClass(TheMovieDBMovieModel.class);
			// for (FieldModel fieldModel : fl) {
			// System.out.println(fieldModel.getField().getName());
			// Method method = MovieDB.class.getMethod("get" +
			// Helper.ucfirst(fieldModel.getField().getName()));
			// movie.set(fieldModel.getField().getName(), method.invoke(m));
			// }

			movie.setAlternativeName(m.getAlternativeName());
			movie.setBudget(((m.getBudget() != null) && !m.getBudget().equals("") ? Integer.parseInt(m.getBudget()) : null));
			movie.setOriginalName(m.getOriginalName());
			movie.setRating(((m.getRating() != null) && !m.getRating().equals("") ? new Double(m.getRating()).intValue() : null));
			movie.setRuntime(((m.getRuntime() != null) && !m.getRuntime().equals("") ? Integer.parseInt(m.getRuntime()) : null));
			movie.setTmdbId(Integer.parseInt(m.getId()));
			movie.set("descriptionShort", m.getOverview());
			tempList.clear();
			for (Country c : m.getCountries()) {
				tempList.add(c.getName());
			}
			movie.setCountries(tempList);
			tempList.clear();
			for (Person p : m.getPeople()) {
				tempList.add(p.getName());
			}
			movie.setPeople(tempList);

			if (m.getArtwork().size() > 0) {
				movie.set("cover", Toolkit.getDefaultToolkit().getImage(m.getArtwork().get(0).getUrl()));
			}

			// movie.set("maintitle", m.getTitle());
			//
			// movie.set("tmdbId", m.getId());
			// // movie.setId(Integer.parseInt(m.getId()));
			// movie.set("descriptionShort", m.getOverview());
			// for (final Category category : m.getCategories()) {
			// movie.getGenreList().add(category.getName());
			// }

			// if (m.getReleaseDate() != null) {
			// movie.set("year",
			// Integer.parseInt(m.getReleaseDate().substring(0, 4)));
			// }

		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated catch block
		// resultMap = new HashMap<String, Object>();
		// resultMap.put("adult", m.getAdult());
		// resultMap.put("alternativeName", m.getAlternativeName());
		// resultMap.put("budget", m.getBudget());
		// resultMap.put("categories", m.getCategories());
		// resultMap.put("certification", m.getCertification());
		// resultMap.put("countries", m.getCountries());
		// resultMap.put("homepage", m.getHomepage());
		// resultMap.put("id", m.getId());
		// resultMap.put("imdb", m.getImdb());
		// resultMap.put("language", m.getLanguage());
		// resultMap.put("originalName", m.getOriginalName());
		// resultMap.put("overview", m.getOverview());
		// resultMap.put("people", m.getPeople());
		// resultMap.put("popularity", m.getPopularity());
		// resultMap.put("productionCountries", m.getProductionCountries());
		// resultMap.put("rating", m.getRating());
		// resultMap.put("releaseDate", m.getReleaseDate());
		// resultMap.put("revenue", m.getRevenue());
		// resultMap.put("runtime", m.getRuntime());
		// resultMap.put("studios", m.getStudios());
		// resultMap.put("tagline", m.getTagline());
		// resultMap.put("title", m.getTitle());
		// resultMap.put("trailer", m.getTrailer());
		// resultMap.put("translated", m.getTranslated());
		// resultMap.put("type", m.getType());
		// resultMap.put("url", m.getUrl());
		// resultMap.put("version", m.getVersion());
		catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}

		return movie;
	}

	@Override
	public ConcurrentHashMap<String, Object> getAdditionalInformationFromKey(final String key) {
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Object> getAdditionalInformationFromStringYear(final String s, final Integer year) {
		return null;
	}

	@Override
	public Class<? extends TheMovieDBMovieModel> getMovieModelInstance() {
		return new TheMovieDBMovieModel().getClass();
	}
}
