package fr.isen.java2.db.daos;

import java.util.LinkedList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	
	private String sqliteURL = "jdbc:sqlite:sqlite.db";

	public List<Genre> listGenres() {
		List<Genre> allGenres = new LinkedList<>();
		
		String query = "SELECT * FROM genre";
		try(Connection connection = DriverManager.getConnection(sqliteURL);
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery()
		) {
			while (resultSet.next()) {
				allGenres.add(new Genre(resultSet.getInt("idgenre"), resultSet.getString("name")));
			}
			return allGenres;
			
		} catch(SQLException e) {
			throw new RuntimeException("From listGenre", e);
		}
	}

	public Genre getGenre(String name) {
		String query = "SELECT * FROM genre WHERE name=?";
		
		try (Connection connection = DriverManager.getConnection(sqliteURL);
			 PreparedStatement statement = connection.prepareStatement(query);
		) {
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				return new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
			}
		} catch (SQLException e) {
			return null;
		}
		
		
	}

	public void addGenre(String name) {
		String query = "INSERT INTO genre(name) VALUES(?)";
		try(Connection connection = DriverManager.getConnection(sqliteURL);
			PreparedStatement statement = connection.prepareStatement(query);
		) {
			statement.setString(1, name);
			statement.executeUpdate();
			
			statement.close();
		} catch(SQLException e) {
			throw new RuntimeException("From addGenre", e);
		}
	}
	
	public Genre getGenreByID(int id) {
		String query = "SELECT * FROM genre WHERE idgenre=?";
		
		try (Connection connection = DriverManager.getConnection(sqliteURL);
			 PreparedStatement statement = connection.prepareStatement(query);
		) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			
			Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name")); 
			
			resultSet.close();
			statement.close();
			
			return genre;
		} catch (SQLException e) {
			return null;
		}

	}
}
