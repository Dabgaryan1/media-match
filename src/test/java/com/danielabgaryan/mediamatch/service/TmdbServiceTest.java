package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import com.danielabgaryan.mediamatch.exception.ExternalServiceException;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;

class TmdbServiceTest {
    private MockRestServiceServer server;
    private TmdbService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.themoviedb.org/3");
        server = MockRestServiceServer.bindTo(builder).build();
        service = new TmdbService(builder.build(), "test-token");
    }

    @Test
    void search_encodesQueryAndMapsPaginatedResultsWithoutRequiringOptionalFields() {
        server.expect(requestTo("https://api.themoviedb.org/3/search/movie?query=A%20%26%20B%2B&page=2&include_adult=false"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer test-token"))
            .andExpect(content().string(""))
            .andRespond(withSuccess("""
                {"page":2,"total_pages":3,"total_results":41,"results":[
                  {"id":27205,"title":"Inception","overview":"A dream.",
                   "poster_path":null,"release_date":"","popularity":99}
                ]}
                """, MediaType.APPLICATION_JSON));

        var response = service.searchMovies("  A & B+  ", 2);
        assertEquals(2, response.page());
        assertEquals(3, response.totalPages());
        assertEquals(41, response.totalResults());
        var movie = response.results().getFirst();
        assertEquals(27205, movie.externalId());
        assertEquals("TMDB", movie.externalSource());
        assertEquals("Inception", movie.title());
        assertEquals("A dream.", movie.description());
        assertEquals("MOVIE", movie.mediaType().name());
        assertNull(movie.posterPath());
        assertEquals("", movie.releaseDate());
        server.verify();
    }

    @Test
    void noMatches_returnsEmptyResults() {
        server.expect(anything()).andRespond(withSuccess(
            "{\"page\":1,\"total_pages\":0,\"total_results\":0,\"results\":[]}", MediaType.APPLICATION_JSON));
        assertTrue(service.searchMovies("unknown", 1).results().isEmpty());
        server.verify();
    }

    @Test
    void invalidInputs_doNotContactProvider() {
        assertThrows(InvalidRequestException.class, () -> service.searchMovies(" ", 1));
        assertThrows(InvalidRequestException.class, () -> service.searchMovies("x".repeat(201), 1));
        assertThrows(InvalidRequestException.class, () -> service.searchMovies("movie", 0));
        assertThrows(InvalidRequestException.class, () -> service.searchMovies("movie", 501));
        server.verify();
    }

    @Test
    void missingToken_reportsConfigurationProblemWithoutRequest() {
        var unconfigured = new TmdbService(RestClient.create(), " ");
        assertThrows(ExternalServiceException.class, () -> unconfigured.searchMovies("movie", 1));
    }

    @Test
    void providerFailure_doesNotExposeProviderBody() {
        server.expect(anything()).andRespond(withStatus(HttpStatus.UNAUTHORIZED)
            .body("sensitive provider details"));
        var error = assertThrows(ExternalServiceException.class, () -> service.searchMovies("movie", 1));
        assertEquals("Media search is temporarily unavailable. Please try again later.", error.getMessage());
        server.verify();
    }

    @Test
    void networkFailure_returnsSafeError() {
        server.expect(anything()).andRespond(withException(new IOException("connection timed out")));
        assertThrows(ExternalServiceException.class, () -> service.searchMovies("movie", 1));
        server.verify();
    }

    @Test
    void malformedResponse_returnsSafeError() {
        server.expect(anything()).andRespond(withSuccess("not json", MediaType.APPLICATION_JSON));
        assertThrows(ExternalServiceException.class, () -> service.searchMovies("movie", 1));
        server.verify();
    }
}
