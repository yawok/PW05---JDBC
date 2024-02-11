package fr.isen.java2.db.daos;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.DriverManager;

import fr.isen.java2.db.entities.Movie;

public class MovieDao { 
	
	private String sqliteURL = "jdbc:sqlite:sqlite.db";
	
	public List<Movie> listMovies() {
		List<Movie> movies = new LinkedList<>();
		String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
		
		try(Connection connection = DriverManager.getConnection(sqliteURL);
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
		) {
			while( resultSet.next()) {
				Movie movie = new Movie();
				movie.setId(resultSet.getInt("idmovie"));
				movie.setTitle(resultSet.getString("title"));
				movie.setDirector(resultSet.getString("director"));
				movie.setDuration(resultSet.getInt("duration"));
				movie.setSummary(resultSet.getString("summary"));
				
				Date legacyDate = resultSet.getDate("release_date");
				LocalDate date = legacyDate.toLocalDate();
				movie.setReleaseDate(date);
				
				Integer genreId = resultSet.getInt("genre_id");
				movie.setGenre(new GenreDao().getGenreByID(genreId));
				movies.add(movie);
			}
			
			resultSet.close();
			return movies;
		} catch (SQLException e) {
			throw new RuntimeException("From listMovies", e);
		}
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new LinkedList<>();
		String query = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?";
		
		try (Connection connection = DriverManager.getConnection(sqliteURL);
			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		) {
			statement.setString(1, genreName);
			ResultSet resultSet = statement.executeQuery();
			while( resultSet.next()) {
				Movie movie = new Movie();
				movie.setId(resultSet.getInt("idmovie"));
				movie.setTitle(resultSet.getString("title"));
				movie.setDirector(resultSet.getString("director"));
				movie.setDuration(resultSet.getInt("duration"));
				
				Date legacyDate = resultSet.getDate("release_date");
				LocalDate date = legacyDate.toLocalDate();
				movie.setReleaseDate(date);
				
				Integer genreId = resultSet.getInt("genre_id");
				movie.setGenre(new GenreDao().getGenreByID(genreId));
				movies.add(movie);
				
				resultSet.close();
				statement.close();
				connection.close();
			}
			return movies;
			
		} catch (SQLException e) {
			throw new RuntimeException("from listMoviesByGenre", e);
		}
		
		
	}

	public Movie addMovie(Movie movie) {
		String query = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) "
				+ "VALUES(?,?,?,?,?,?)";
		try (Connection connection = DriverManager.getConnection(sqliteURL);
			PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		) {
			statement.setString(1, movie.getTitle());
			statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6,  movie.getSummary());
			
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			Integer returnedId = 0;
			
			if (resultSet.next()) { returnedId = resultSet.getInt(1);}
				
			
			Movie returnedMovie = movie;
			returnedMovie.setId(returnedId);
			
			resultSet.close();
			statement.close();
			connection.close();
			return returnedMovie;
			
		} catch (SQLException e) {
			throw new RuntimeException("from addMovie", e);
		}
	}
}
