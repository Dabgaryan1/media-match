package com.danielabgaryan.mediamatch.service;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.danielabgaryan.mediamatch.dto.MediaSearchResponse;
import com.danielabgaryan.mediamatch.exception.ExternalServiceException;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;
import com.danielabgaryan.mediamatch.model.MediaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class TmdbService {
    private final RestClient client;
    private final String token;

    public TmdbService(@Qualifier("tmdbRestClient") RestClient client,
            @Value("${tmdb.read-access-token:}") String token) {
        this.client = client;
        this.token = token.trim();
    }

    public MediaSearchResponse searchMovies(String query, int page) {
        if (query == null || query.isBlank() || query.trim().length() > 200) {
            throw new InvalidRequestException("Search query must contain between 1 and 200 characters");
        }
        if (page < 1 || page > 500) {
            throw new InvalidRequestException("Page must be between 1 and 500");
        }
        if (token.isBlank()) {
            throw new ExternalServiceException("Media search is not configured");
        }

        TmdbSearchResponse response;
        try {
            response = client.get()
                .uri("/search/movie?query={query}&page={page}&include_adult=false", query.trim(), page)
                .headers(headers -> headers.setBearerAuth(token))
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TmdbSearchResponse.class);
        } catch (RestClientException exception) {
            // Provider errors must not expose credentials or look like a MediaMatch login failure.
            throw new ExternalServiceException("Media search is temporarily unavailable. Please try again later.");
        }
        if (response == null || response.results() == null) {
            throw new ExternalServiceException("Media search returned an invalid response");
        }

        List<MediaSearchResponse.Result> results = response.results().stream()
            .map(movie -> new MediaSearchResponse.Result("TMDB", movie.id(), movie.title(),
                movie.overview(), movie.posterPath(), movie.releaseDate(), MediaType.MOVIE))
            .toList();
        return new MediaSearchResponse(response.page(), response.totalPages(), response.totalResults(), results);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbSearchResponse(int page, @JsonProperty("total_pages") int totalPages,
            @JsonProperty("total_results") long totalResults, List<TmdbMovie> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbMovie(long id, String title, String overview,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("release_date") String releaseDate) {}
}
